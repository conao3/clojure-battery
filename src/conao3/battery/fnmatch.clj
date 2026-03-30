(ns conao3.battery.fnmatch
  (:require [clojure.string :as str]))

(def ^:private windows?
  (str/includes? (str/lower-case (System/getProperty "os.name")) "win"))

(def ^:private special-chars #{\. \^ \$ \* \+ \? \{ \} \[ \] \\ \| \( \)})

(defn- escape-char [c]
  (if (contains? special-chars c)
    (str \\ c)
    (str c)))

(defn- check-types [name pat]
  (let [name-bytes? (bytes? name)
        pat-bytes? (bytes? pat)]
    (when (not= name-bytes? pat-bytes?)
      (throw (ex-info "name and pattern must both be str or both be bytes"
                      {:name-type (type name) :pat-type (type pat)})))))

(defn- ->str [x]
  (if (bytes? x)
    (String. ^bytes x "ISO-8859-1")
    x))

(defn- remove-invalid-ranges [s]
  (let [n (count s)
        sb (StringBuilder.)
        i (atom 0)
        read-token (fn [idx]
                     (let [c (nth s idx)]
                       (if (and (= c \\) (< (inc idx) n))
                         [(str \\ (nth s (inc idx)))
                          (int (nth s (inc idx)))
                          (+ idx 2)]
                         [(str c) (int c) (inc idx)])))]
    (when (and (< @i n) (= (nth s @i) \]))
      (.append sb \])
      (swap! i inc))
    (while (< @i n)
      (let [[tok1 code1 next1] (read-token @i)]
        (if (and (< next1 n)
                 (= (nth s next1) \-)
                 (< (inc next1) n)
                 (not= (nth s (inc next1)) \]))
          (let [[tok2 code2 next2] (read-token (inc next1))]
            (when (<= code1 code2)
              (.append sb tok1)
              (.append sb \-)
              (.append sb tok2))
            (reset! i next2))
          (do
            (.append sb tok1)
            (reset! i next1)))))
    (.toString sb)))

(defn _translate [pat star any]
  (let [n (count pat)
        result (atom [])
        star-indices (atom [])
        i (atom 0)]
    (while (< @i n)
      (let [c (nth pat @i)]
        (swap! i inc)
        (cond
          (= c \*)
          (do
            (while (and (< @i n) (= (nth pat @i) \*))
              (swap! i inc))
            (swap! star-indices conj (count @result))
            (swap! result conj star))

          (= c \?)
          (swap! result conj any)

          (= c \[)
          (let [j (atom @i)]
            (when (and (< @j n) (= (nth pat @j) \!))
              (swap! j inc))
            (when (and (< @j n) (= (nth pat @j) \]))
              (swap! j inc))
            (while (and (< @j n) (not= (nth pat @j) \]))
              (swap! j inc))
            (if (< @j n)
              (let [raw (subs pat @i @j)
                    negated? (= (first raw) \!)
                    content (-> (if negated? (subs raw 1) raw)
                                (str/replace "\\" "\\\\")
                                remove-invalid-ranges
                                (str/replace #"&&+" "&"))
                    class-str (cond
                                (empty? content)
                                (if negated? "[\\s\\S]" "(?![\\s\\S])")
                                negated?
                                (str "[^" content "]")
                                (= (first content) \^)
                                (str "[\\^" (subs content 1) "]")
                                (= (first content) \[)
                                (str "[\\" content "]")
                                :else
                                (str "[" content "]"))]
                (swap! result conj class-str)
                (reset! i (inc @j)))
              (swap! result conj "\\[")))

          :else
          (swap! result conj (escape-char c)))))
    [@result @star-indices]))

(defn translate [pat]
  (let [[parts star-indices] (_translate pat "*" ".")
        n-stars (count star-indices)]
    (if (zero? n-stars)
      (str "(?s:" (str/join parts) ")\\z")
      (let [sb (StringBuilder.)]
        (doseq [p (subvec parts 0 (first star-indices))]
          (.append sb p))
        (loop [k 0]
          (when (< k n-stars)
            (let [star-idx (nth star-indices k)
                  next-star-idx (if (< (inc k) n-stars)
                                  (nth star-indices (inc k))
                                  (count parts))
                  suffix (str/join (subvec parts (inc star-idx) next-star-idx))
                  last? (= k (dec n-stars))]
              (if last?
                (.append sb (str ".*" suffix))
                (.append sb (str "(?>.*?" suffix ")")))
              (recur (inc k)))))
        (str "(?s:" (.toString sb) ")\\z")))))

(defn- normcase [s]
  (if windows?
    (-> s str/lower-case (str/replace "/" "\\"))
    s))

(defn fnmatchcase [name pat]
  (check-types name pat)
  (let [name-str (->str name)
        pat-str (->str pat)]
    (boolean (re-matches (re-pattern (translate pat-str)) name-str))))

(defn fnmatch [name pat]
  (check-types name pat)
  (let [name-str (normcase (->str name))
        pat-str (normcase (->str pat))]
    (boolean (re-matches (re-pattern (translate pat-str)) name-str))))

(defn filter [names pat]
  (when (seq names)
    (check-types (first names) pat))
  (let [pat-str (normcase (->str pat))
        regex (re-pattern (translate pat-str))]
    (clojure.core/filter #(re-matches regex (normcase (->str %))) names)))

(defn filterfalse [names pat]
  (when (seq names)
    (check-types (first names) pat))
  (let [pat-str (normcase (->str pat))
        regex (re-pattern (translate pat-str))]
    (remove #(re-matches regex (normcase (->str %))) names)))
