(ns conao3.battery.tempfile)

(def TMP_MAX 10000)
(def template "tmp")
(def tempdir nil)

(defn- not-implemented
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn gettempdir
  []
  (or tempdir "/tmp"))

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
  [& _]
  (not-implemented))

(defn mkdtemp
  [& _]
  (not-implemented))

(defn mktemp
  [& _]
  (not-implemented))

(defn NamedTemporaryFile
  [& _]
  (not-implemented))

(defn TemporaryFile
  [& _]
  (not-implemented))

(defn SpooledTemporaryFile
  [& _]
  (not-implemented))

(defn TemporaryDirectory
  [& _]
  (not-implemented))
