;; Original: Lib/platform.py

(ns conao3.battery.platform
  (:import
   [java.lang.management ManagementFactory]))

(defn system
  "Returns the OS name (e.g., 'Linux', 'Darwin', 'Windows')."
  []
  (let [os (System/getProperty "os.name")]
    (cond
      (.startsWith os "Windows") "Windows"
      (.startsWith os "Mac") "Darwin"
      (.startsWith os "Linux") "Linux"
      :else os)))

(defn release
  "Returns the OS release version."
  []
  (System/getProperty "os.version"))

(defn machine
  "Returns the machine hardware name."
  []
  (System/getProperty "os.arch"))

(defn processor
  "Returns the processor type."
  []
  (System/getProperty "os.arch"))

(defn node
  "Returns the hostname."
  []
  (try
    (.getHostName (java.net.InetAddress/getLocalHost))
    (catch Exception _ "")))

(defn python-version
  "Returns the JVM version (analogous to Python version)."
  []
  (System/getProperty "java.version"))

(defn python-version-tuple
  "Returns the JVM version as a tuple of strings."
  []
  (let [v (python-version)
        parts (clojure.string/split v #"\.")]
    (mapv str parts)))

(defn java-ver
  "Returns the Java version string."
  []
  (System/getProperty "java.version"))

(defn uname
  "Returns a map with system information."
  []
  {:system (system)
   :node (node)
   :release (release)
   :version (System/getProperty "os.version")
   :machine (machine)
   :processor (processor)})

(defn architecture
  "Returns the architecture bits and linkage."
  []
  (let [arch (System/getProperty "os.arch")]
    (cond
      (or (= arch "amd64") (= arch "x86_64")) ["64bit" ""]
      (= arch "x86") ["32bit" ""]
      :else ["64bit" ""])))

(defn platform
  "Returns a string identifying the platform."
  ([] (platform false))
  ([_aliased]
   (str (system) "-" (release) "-" (machine))))
