(ns conao3.battery.os-path
  (:import
   [java.io File]
   [java.nio.file Path Paths Files LinkOption]))

(def sep File/separator)
(def pathsep File/pathSeparator)
(def curdir ".")
(def pardir "..")
(def extsep ".")
(def altsep nil)
(def defpath "/usr/bin:/bin")

(defn- ^Path to-path [s]
  (Paths/get ^String s (into-array String [])))

(defn join [first & more]
  (let [parts (cons first more)]
    (reduce (fn [acc part]
              (if (clojure.string/starts-with? part "/")
                part
                (let [acc-str (str acc)]
                  (if (or (empty? acc-str) (clojure.string/ends-with? acc-str "/"))
                    (str acc-str part)
                    (str acc-str "/" part)))))
            parts)))

(defn split [path]
  (let [p (str path)
        idx (.lastIndexOf p "/")]
    (if (= idx -1)
      ["" p]
      (let [head (subs p 0 (inc idx))
            tail (subs p (inc idx))]
        [(if (and (> (count head) 1) (clojure.string/ends-with? head "/"))
           (subs head 0 (dec (count head)))
           head)
         tail]))))

(defn splitext [path]
  (let [p (str path)
        base (last (clojure.string/split p #"/"))
        dot-idx (.lastIndexOf base ".")]
    (if (or (= dot-idx -1) (= dot-idx 0) (= dot-idx (dec (count base))))
      [p ""]
      (let [dir-part (subs p 0 (- (count p) (count base)))]
        [(str dir-part (subs base 0 dot-idx))
         (subs base dot-idx)]))))

(defn basename [path]
  (second (split (str path))))

(defn dirname [path]
  (first (split (str path))))

(defn normpath [path]
  (let [p (str path)]
    (-> (.normalize (to-path p))
        (.toString))))

(defn abspath [path]
  (.getAbsolutePath (File. ^String (str path))))

(defn expanduser [path]
  (let [p (str path)]
    (if (clojure.string/starts-with? p "~")
      (str (System/getProperty "user.home") (subs p 1))
      p)))

(defn expandvars [path]
  (let [p (str path)]
    (clojure.string/replace p #"\$\{?(\w+)\}?" #(or (System/getenv (second %)) (first %)))))

(defn exists [path]
  (.exists (File. ^String (str path))))

(defn lexists [path]
  (.exists (File. ^String (str path))))

(defn isfile [path]
  (.isFile (File. ^String (str path))))

(defn isdir [path]
  (.isDirectory (File. ^String (str path))))

(defn isabs [path]
  (.isAbsolute (File. ^String (str path))))

(defn islink [path]
  (Files/isSymbolicLink (to-path (str path))))

(defn getsize [path]
  (.length (File. ^String (str path))))

(defn getatime [path]
  (/ (.lastModified (File. ^String (str path))) 1000.0))

(defn getmtime [path]
  (/ (.lastModified (File. ^String (str path))) 1000.0))

(defn getctime [path]
  (/ (.lastModified (File. ^String (str path))) 1000.0))

(defn commonprefix [paths]
  (if (empty? paths)
    ""
    (let [strs (map str paths)]
      (loop [prefix (first strs) rest-paths (rest strs)]
        (if (empty? rest-paths)
          prefix
          (let [p (first rest-paths)
                len (min (count prefix) (count p))
                common (apply str (take-while identity (map #(when (= %1 %2) %1)
                                                             prefix p)))]
            (recur (subs prefix 0 (count common)) (rest rest-paths))))))))

(defn commonpath [paths]
  (if (empty? paths)
    (throw (ex-info "commonpath() arg is an empty sequence" {}))
    (let [parts (map #(clojure.string/split (normpath (str %)) #"/") paths)
          min-len (apply min (map count parts))
          common  (loop [i 0 result []]
                    (if (= i min-len)
                      result
                      (let [segment (first (first parts))]
                        (if (apply = (map first parts))
                          (recur (inc i)
                                 (conj result segment)
                                 #_(map rest parts))
                          result))))]
      common)))

(defn commonpath [paths]
  (if (empty? paths)
    (throw (ex-info "commonpath() arg is an empty sequence" {}))
    (let [norm-paths (map #(normpath (str %)) paths)
          split-paths (map #(vec (filter seq (clojure.string/split % #"/"))) norm-paths)
          all-abs (every? #(clojure.string/starts-with? % "/") norm-paths)
          min-len (apply min (map count split-paths))
          common  (loop [i 0]
                    (if (>= i min-len)
                      i
                      (let [segment (get (first split-paths) i)]
                        (if (every? #(= (get % i) segment) split-paths)
                          (recur (inc i))
                          i))))
          common-parts (take common (first split-paths))]
      (if (and all-abs (empty? common-parts))
        "/"
        (if all-abs
          (str "/" (clojure.string/join "/" common-parts))
          (clojure.string/join "/" common-parts))))))

(defn realpath [path]
  (.getCanonicalPath (File. ^String (str path))))

(defn relpath
  ([path] (relpath path (System/getProperty "user.dir")))
  ([path start]
   (let [p     (to-path (str path))
         s     (to-path (str start))
         rel   (.relativize s p)]
     (.toString rel))))
