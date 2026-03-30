(ns conao3.battery.glob
  (:require [clojure.string :as str]))

(defn escape [value]
  (if (bytes? value)
    (.getBytes ^String (escape (String. ^bytes value "UTF-8")) "UTF-8")
    (-> ^String value
        (str/replace "[" "[[]")
        (str/replace "?" "[?]")
        (str/replace "*" "[*]"))))

(defn- build-not-sep [sep-char-set]
  (str "[^" (str/join (map #(if (= % \\) "\\\\" (str %)) sep-char-set)) "]"))

(def ^:private regex-meta #{\. \^ \$ \| \( \) \+ \{ \} \\ \] \[})

(defn- translate-chars [chars recursive not-sep]
  (let [sb (StringBuilder.)
        n (count chars)
        i (volatile! 0)]
    (while (< @i n)
      (let [c (nth chars @i)]
        (vswap! i inc)
        (cond
          (and (= c \*) (< @i n) (= (nth chars @i) \*))
          (do (vswap! i inc)
              (if recursive (.append sb ".*") (.append sb (str not-sep "*"))))

          (= c \*)
          (.append sb (str not-sep "+"))

          (= c \?)
          (.append sb not-sep)

          (contains? regex-meta c)
          (.append sb (str \\ c))

          :else
          (.append sb c))))
    (.toString sb)))

(defn- component-has-wildcard? [chars]
  (boolean (some #{\* \?} chars)))

(defn translate [pattern & {:keys [recursive include-hidden seps]}]
  (let [seps-list (cond
                    (nil? seps) ["/"]
                    (string? seps) [seps]
                    :else (vec seps))
        sep-char-set (set (mapcat seq seps-list))
        not-sep (build-not-sep sep-char-set)
        parts (reduce (fn [acc c]
                        (if (contains? sep-char-set c)
                          (conj acc {:sep (str c)} {:chars []})
                          (update acc (dec (count acc)) update :chars (fnil conj []) c)))
                      [{:chars []}]
                      (seq pattern))
        sb (StringBuilder.)]
    (doseq [part parts]
      (if (:sep part)
        (.append sb (:sep part))
        (let [chars (:chars part)
              has-wc (component-has-wildcard? chars)
              prefix (if (and has-wc (not include-hidden)) "(?!\\.)" "")]
          (.append sb prefix)
          (.append sb (translate-chars chars recursive not-sep)))))
    (str "(?s:" (.toString sb) ")\\z")))

(defn glob
  ([pattern]
   (throw (ex-info "Not implemented" {})))
  ([pattern _opts]
   (throw (ex-info "Not implemented" {})))
  ([pattern _opts & _more]
   (throw (ex-info "Not implemented" {}))))

(defn iglob
  ([pattern]
   (throw (ex-info "Not implemented" {})))
  ([pattern _opts]
   (throw (ex-info "Not implemented" {})))
  ([pattern _opts & _more]
   (throw (ex-info "Not implemented" {}))))
