(ns conao3.battery.tempfile
  (:import
   [java.io File]
   [java.nio.file Files]
   [java.nio.file.attribute FileAttribute]))

(def TMP_MAX 10000)
(def template "tmp")
(def tempdir nil)

(defn gettempdir
  []
  (or tempdir (System/getProperty "java.io.tmpdir")))

(defn gettempdirb
  []
  (.getBytes (gettempdir) "UTF-8"))

(defn gettempprefix
  []
  "tmp")

(defn gettempprefixb
  []
  (.getBytes (gettempprefix) "UTF-8"))

(defn mkstemp
  ([] (mkstemp nil nil nil false))
  ([{:keys [suffix prefix dir text]}]
   (mkstemp suffix prefix dir text))
  ([suffix prefix dir _text]
   (let [p (or prefix "tmp")
         s (or suffix "")
         d (when dir (File. ^String dir))
         f (File/createTempFile ^String p ^String s ^File d)]
     [0 (.getAbsolutePath f)])))

(defn mkdtemp
  ([] (mkdtemp nil nil nil))
  ([{:keys [suffix prefix dir]}]
   (mkdtemp suffix prefix dir))
  ([suffix prefix dir]
   (let [p (or prefix "tmp")
         s (or suffix "")
         base (if dir (java.nio.file.Paths/get dir (into-array String [])) (java.nio.file.Paths/get (gettempdir) (into-array String [])))
         d (Files/createTempDirectory base (str p s) (into-array FileAttribute []))]
     (.toString d))))

(defn mktemp
  ([] (mktemp nil nil nil))
  ([{:keys [suffix prefix dir]}]
   (mktemp suffix prefix dir))
  ([suffix prefix dir]
   (let [p (or prefix "tmp")
         s (or suffix "")
         d (when dir (File. ^String dir))
         f (File/createTempFile ^String p ^String s ^File d)
         path (.getAbsolutePath f)]
     (.delete f)
     path)))

(defn NamedTemporaryFile
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn TemporaryFile
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn SpooledTemporaryFile
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn TemporaryDirectory
  ([] (TemporaryDirectory nil nil nil))
  ([{:keys [suffix prefix dir]}]
   (TemporaryDirectory suffix prefix dir))
  ([suffix prefix dir]
   (let [path (mkdtemp suffix prefix dir)]
     {:name path
      :cleanup (fn []
                 (let [f (File. ^String path)]
                   (doseq [child (.listFiles f)]
                     (.delete ^File child))
                   (.delete f)))})))
