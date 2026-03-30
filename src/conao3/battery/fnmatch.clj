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
              (let [stuff (subs pat @i @j)
                    stuff (str/replace stuff "\\" "\\\\")
                    stuff (cond
                            (= (first stuff) \!) (str "^" (subs stuff 1))
                            (contains? #{\^ \[} (first stuff)) (str "\\" stuff)
                            :else stuff)]
                (swap! result conj (str "[" stuff "]"))
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
