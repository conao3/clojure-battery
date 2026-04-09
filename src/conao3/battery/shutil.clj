(ns conao3.battery.shutil
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import
   [java.io File]
   [java.nio.file Files CopyOption StandardCopyOption]
   [java.util.zip ZipEntry ZipInputStream ZipOutputStream]))

(defn rmtree
  ([path] (rmtree path false))
  ([path _ignore-errors]
   (let [f (File. ^String path)]
     (when (.exists f)
       (doseq [child (reverse (file-seq f))]
         (.delete ^File child))))))

(defn- copy-tree-recursive [^File src ^File dst]
  (when-not (.exists dst)
    (.mkdirs dst))
  (doseq [^File child (.listFiles src)]
    (let [dst-child (File. dst (.getName child))]
      (if (.isDirectory child)
        (copy-tree-recursive child dst-child)
        (Files/copy (.toPath child) (.toPath dst-child)
                    (into-array CopyOption [StandardCopyOption/REPLACE_EXISTING]))))))

(defn copytree
  ([src dst] (copytree src dst {}))
  ([src dst _opts]
   (let [src-f (File. ^String src)
         dst-f (File. ^String dst)]
     (copy-tree-recursive src-f dst-f)
     dst)))

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

(declare make-archive)

(def ^:private archive-formats (atom {}))

(defn- relativize-path [^File root ^File file]
  (-> (.toPath root)
      (.relativize (.toPath file))
      (.toString)
      (str/replace File/separator "/")))

(defn- zip-directory [archive-path ^File source-root]
  (with-open [os (io/output-stream archive-path)
              zos (ZipOutputStream. os)]
    (doseq [^File file (file-seq source-root)
            :when (.isFile file)]
      (let [entry-name (relativize-path source-root file)]
        (.putNextEntry zos (ZipEntry. entry-name))
        (with-open [is (io/input-stream file)]
          (io/copy is zos))
        (.closeEntry zos))))
  archive-path)

(defn- run-tar-command [root-dir args]
  (let [pb (ProcessBuilder. ^java.util.List args)]
    (.directory pb (File. ^String root-dir))
    (.redirectErrorStream pb true)
    (let [proc (.start pb)
          out (slurp (.getInputStream proc))
          status (.waitFor proc)]
      (when-not (zero? status)
        (throw (ex-info (str "tar command failed: " out) {:status status :args args}))))))

(defn make-tarfile
  ([base-name base-dir] (make-archive base-name "tar" nil base-dir))
  ([base-name base-dir compress]
   (make-archive base-name (if compress "gztar" "tar") nil base-dir)))

(defn make-archive
  ([base-name format] (make-archive base-name format nil nil))
  ([base-name format root-dir] (make-archive base-name format root-dir nil))
  ([base-name format root-dir base-dir]
   (if-let [custom (get @archive-formats format)]
     (custom base-name root-dir base-dir)
     (let [root (or root-dir ".")
           base (or base-dir ".")
           source (File. ^String root ^String base)]
       (case format
         "zip"
         (zip-directory (str base-name ".zip") source)

         "tar"
         (let [archive (str base-name ".tar")]
           (run-tar-command root ["tar" "-cf" archive base])
           archive)

         "gztar"
         (let [archive (str base-name ".tar.gz")]
           (run-tar-command root ["tar" "-czf" archive base])
           archive)

         (throw (ex-info (str "unknown archive format: " format) {:format format})))))))

(defn register-archive-format
  ([name function] (register-archive-format name function nil))
  ([name function _extra]
   (swap! archive-formats assoc name function)
   name))

(defn unpack-archive
  ([filename] (unpack-archive filename nil nil))
  ([filename extract-dir] (unpack-archive filename extract-dir nil))
  ([filename extract-dir format]
   (let [target (or extract-dir ".")
         archive (str filename)
         fmt (or format
                 (cond
                   (str/ends-with? archive ".zip") "zip"
                   (or (str/ends-with? archive ".tar.gz") (str/ends-with? archive ".tgz")) "gztar"
                   (str/ends-with? archive ".tar") "tar"
                   :else nil))]
     (case fmt
       "zip"
       (do
         (with-open [is (io/input-stream archive)
                     zis (ZipInputStream. is)]
           (loop []
             (when-let [entry (.getNextEntry zis)]
               (let [out-file (File. ^String target (.getName entry))]
                 (if (.isDirectory entry)
                   (.mkdirs out-file)
                   (do
                     (.mkdirs (.getParentFile out-file))
                     (with-open [os (io/output-stream out-file)]
                       (io/copy zis os))))
                 (.closeEntry zis)
                 (recur)))))
         target)

       "tar"
       (do (run-tar-command target ["tar" "-xf" archive]) target)

       "gztar"
       (do (run-tar-command target ["tar" "-xzf" archive]) target)

       (throw (ex-info (str "unknown archive format: " fmt) {:format fmt :filename filename}))))))

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
  ([src dst]
   (copyfileobj src dst nil))
  ([src dst _length]
   (io/copy src dst)
   nil))

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
