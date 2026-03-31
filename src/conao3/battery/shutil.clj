(ns conao3.battery.shutil
  (:import
   [java.io File]
   [java.nio.file Files CopyOption StandardCopyOption]))

(defn rmtree
  ([path] (rmtree path false))
  ([path _ignore-errors]
   (let [f (File. ^String path)]
     (when (.exists f)
       (doseq [child (reverse (file-seq f))]
         (.delete ^File child))))))

(defn copytree
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn copy
  [src dst]
  (let [src-f (File. ^String src)
        dst-f (File. ^String dst)
        dst-path (if (.isDirectory dst-f)
                   (File. dst-f (.getName src-f))
                   dst-f)]
    (Files/copy (.toPath src-f) (.toPath dst-path) (into-array CopyOption [StandardCopyOption/REPLACE_EXISTING]))
    (.getAbsolutePath dst-path)))

(defn copy2
  [src dst]
  (copy src dst))

(defn copyfile
  [src dst]
  (copy src dst))

(defn make-tarfile
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn make-archive
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn register-archive-format
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn unpack-archive
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn which
  ([name] (which name nil nil))
  ([name mode path]
   (let [path-dirs (if path
                     (clojure.string/split path (re-pattern File/pathSeparator))
                     (clojure.string/split (or (System/getenv "PATH") "") (re-pattern File/pathSeparator)))]
     (first
      (for [dir path-dirs
            :let [f (File. ^String dir ^String name)]
            :when (and (.isFile f) (.canExecute f))]
        (.getAbsolutePath f))))))

(defn move
  [src dst]
  (let [src-f (File. ^String src)
        dst-f (File. ^String dst)
        dst-path (if (.isDirectory dst-f)
                   (File. dst-f (.getName src-f))
                   dst-f)]
    (Files/move (.toPath src-f) (.toPath dst-path) (into-array CopyOption [StandardCopyOption/REPLACE_EXISTING]))
    (.getAbsolutePath dst-path)))

(defn copyfileobj
  [& _]
  (throw (ex-info "Not implemented" {})))

(defn get-terminal-size
  ([] (get-terminal-size [80 24]))
  ([[fallback-cols fallback-lines]]
   (let [cols (try
                (Long/parseLong (or (System/getenv "COLUMNS") "0"))
                (catch Exception _ 0))
         lines (try
                 (Long/parseLong (or (System/getenv "LINES") "0"))
                 (catch Exception _ 0))]
     {:columns (if (pos? cols) cols fallback-cols)
      :lines (if (pos? lines) lines fallback-lines)})))

(defn disk-usage
  [path]
  (let [f (File. ^String path)
        total (.getTotalSpace f)
        free (.getFreeSpace f)
        used (- total free)]
    {:total total :used used :free free}))
