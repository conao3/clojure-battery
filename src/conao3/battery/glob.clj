(ns conao3.battery.glob
  (:require [clojure.string :as str])
  (:import
   [java.io File]
   [java.nio.file Files Path FileSystems]
   [java.nio.file.attribute BasicFileAttributes]))

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

(defn- pattern-matches? [^java.util.regex.Pattern regex ^String path]
  (boolean (re-matches regex path)))

(defn iglob
  ([pattern] (iglob pattern {}))
  ([pattern {:keys [recursive include-hidden root-dir]}]
   (let [base-dir (or root-dir (System/getProperty "user.dir"))
         regex (re-pattern (translate pattern
                                      :recursive recursive
                                      :include-hidden include-hidden
                                      :seps [File/separator]))]
     (->> (file-seq (File. ^String base-dir))
          (map #(.getAbsolutePath ^File %))
          (map (fn [^String abs]
                 (if (.startsWith abs (str base-dir File/separator))
                   (subs abs (inc (count base-dir)))
                   abs)))
          (filter #(pattern-matches? regex %))
          (map #(if (.isAbsolute (File. ^String pattern))
                  (str base-dir File/separator %)
                  %))))))

(defn glob
  ([pattern] (vec (iglob pattern)))
  ([pattern opts] (vec (iglob pattern opts))))
