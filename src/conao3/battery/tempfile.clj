(ns conao3.battery.tempfile
  (:require [clojure.java.io :as io])
  (:import
   [java.io ByteArrayOutputStream File]
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

(defn- make-temp-handle [path delete-on-close]
  (let [file (File. ^String path)]
    {:name (.getAbsolutePath file)
     :file file
     :write (fn [content]
              (spit file content :append true)
              nil)
     :read (fn []
             (slurp file))
     :close (fn []
              (when delete-on-close
                (.delete file))
              nil)}))

(defn NamedTemporaryFile
  ([] (NamedTemporaryFile nil nil nil true))
  ([{:keys [suffix prefix dir delete]
     :or {delete true}}]
   (NamedTemporaryFile suffix prefix dir delete))
  ([suffix prefix dir delete]
   (let [[_ path] (mkstemp suffix prefix dir false)]
     (make-temp-handle path delete))))

(defn TemporaryFile
  ([] (TemporaryFile nil nil nil))
  ([{:keys [suffix prefix dir]}]
   (TemporaryFile suffix prefix dir))
  ([suffix prefix dir]
   (NamedTemporaryFile suffix prefix dir true)))

(defn SpooledTemporaryFile
  ([] (SpooledTemporaryFile 0 nil nil nil))
  ([{:keys [max-size suffix prefix dir]
     :or {max-size 0}}]
   (SpooledTemporaryFile max-size suffix prefix dir))
  ([max-size suffix prefix dir]
   (let [buffer (atom (ByteArrayOutputStream.))
         handle (atom nil)
         ensure-handle! (fn []
                          (when-not @handle
                            (let [tmp (NamedTemporaryFile suffix prefix dir true)]
                              ((:write tmp) (.toString ^ByteArrayOutputStream @buffer "UTF-8"))
                              (reset! buffer (ByteArrayOutputStream.))
                              (reset! handle tmp)))
                          @handle)]
     {:rolled? (fn [] (boolean @handle))
      :write (fn [content]
               (let [s (str content)]
                 (if @handle
                   ((:write @handle) s)
                   (let [candidate-size (+ (.size ^ByteArrayOutputStream @buffer)
                                           (alength (.getBytes s "UTF-8")))]
                     (if (and (pos? max-size) (> candidate-size max-size))
                       (do
                         (ensure-handle!)
                         ((:write @handle) s))
                       (.write ^ByteArrayOutputStream @buffer (.getBytes s "UTF-8"))))))
               nil)
      :read (fn []
              (if @handle
                ((:read @handle))
                (.toString ^ByteArrayOutputStream @buffer "UTF-8")))
      :rollover (fn []
                  (ensure-handle!)
                  nil)
      :close (fn []
               (when @handle
                 ((:close @handle)))
               nil)
      :name (fn []
              (when @handle (:name @handle)))})))

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
