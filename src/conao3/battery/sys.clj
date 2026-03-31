;; Original: Lib/sys.py (module)

(ns conao3.battery.sys
  (:import
   [java.lang.management ManagementFactory RuntimeMXBean]))

(def argv (vec *command-line-args*))

(def version (System/getProperty "java.version"))

(def version-info
  (let [parts (clojure.string/split (or version "0.0.0") #"\.")
        major (Integer/parseInt (first parts))
        minor (if (> (count parts) 1) (Integer/parseInt (second parts)) 0)
        micro (if (> (count parts) 2)
                (try (Integer/parseInt (nth parts 2)) (catch Exception _ 0))
                0)]
    {:major major :minor minor :micro micro :releaselevel "final" :serial 0}))

(def platform
  (let [os (System/getProperty "os.name")]
    (cond
      (.startsWith os "Windows") "win32"
      (.startsWith os "Mac") "darwin"
      (.startsWith os "Linux") "linux"
      :else (.toLowerCase os))))

(def maxsize Long/MAX_VALUE)
(def maxunicode 0x10FFFF)
(def byteorder (if (= java.nio.ByteOrder/BIG_ENDIAN (java.nio.ByteOrder/nativeOrder)) "big" "little"))

(defn exit
  "Exit the JVM with given status code."
  ([] (System/exit 0))
  ([n] (System/exit (int n))))

(defn getdefaultencoding
  "Returns the default string encoding."
  []
  (System/getProperty "file.encoding" "UTF-8"))

(defn getfilesystemencoding
  "Returns the filesystem encoding."
  []
  (System/getProperty "file.encoding" "UTF-8"))

(defn getrecursionlimit
  "Returns the recursion limit (not applicable in JVM, returns a large value)."
  []
  1000)

(defn setrecursionlimit
  "Sets the recursion limit (no-op in JVM)."
  [_n]
  nil)

(defn getsizeof
  "Returns approximate memory size of object."
  ([obj] (getsizeof obj 0))
  ([obj default]
   (try
     (let [baos (java.io.ByteArrayOutputStream.)
           oos (java.io.ObjectOutputStream. baos)]
       (.writeObject oos obj)
       (.close oos)
       (.size baos))
     (catch Exception _ default))))

(defn intern
  "Returns the canonical version of a string (Java string interning)."
  [s]
  (.intern ^String s))

(defn getrefcount
  "Returns reference count (not applicable in JVM, returns 1)."
  [_obj]
  1)

(def stdin System/in)
(def stdout System/out)
(def stderr System/err)

(defn exc-info
  "Returns current exception info (requires try/catch in Clojure)."
  []
  [nil nil nil])

(defn path
  "Returns the classpath as a vector of strings."
  []
  (->> (clojure.string/split
        (System/getProperty "java.class.path" "")
        (re-pattern (System/getProperty "path.separator")))
       vec))

(defn modules
  "Returns currently loaded namespaces."
  []
  (into {} (map (fn [ns] [(str (ns-name ns)) ns]) (all-ns))))
