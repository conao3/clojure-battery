(ns conao3.battery.filecmp
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str])
  (:import [java.io File]
           [java.nio.file Files]
           [java.nio.file.attribute FileTime]))

(def default-ignores #{".hg"})

(def _cache (atom {}))

(defn- file? [^File f]
  (and f (.isFile f)))

(defn- same-stat? [^File f1 ^File f2]
  (and (= (.length f1) (.length f2))
       (= (.lastModified f1) (.lastModified f2))))

(defn- same-content? [^File f1 ^File f2]
  (let [b1 (Files/readAllBytes (.toPath f1))
        b2 (Files/readAllBytes (.toPath f2))]
    (java.util.Arrays/equals b1 b2)))

(defn cmp
  ([f1 f2] (cmp f1 f2 true))
  ([f1 f2 shallow]
   (when (nil? f2)
     (throw (ex-info "f2 must not be nil" {:f2 f2})))
   (let [^File file1 (io/file f1)
         ^File file2 (io/file f2)]
     (cond
       (not (file? file1)) false
       (= (.getCanonicalPath file1) (.getCanonicalPath file2)) true
       (not (file? file2)) false
       (and shallow (same-stat? file1 file2)) true
       :else (same-content? file1 file2)))))

(defn clear-cache
  []
  (reset! _cache {}))

(defn- valid-os-filename? [fname]
  (and (<= (count fname) 255)
       (not (str/includes? fname "\u0000"))
       (every? #(not (<= 0xD800 (int %) 0xDFFF)) fname)))

(defn cmpfiles
  ([a b common] (cmpfiles a b common true))
  ([a b common shallow]
   (reduce (fn [[match mismatch errors] fname]
             (if (not (valid-os-filename? fname))
               [match mismatch (conj errors fname)]
               (try
                 (if (cmp (str a File/separator fname) (str b File/separator fname) shallow)
                   [(conj match fname) mismatch errors]
                   [match (conj mismatch fname) errors])
                 (catch Exception _
                   [match mismatch (conj errors fname)]))))
           [[] [] []]
           common)))

(defn- list-dir [^File dir]
  (if (.isDirectory dir)
    (vec (.list dir))
    (throw (ex-info (str "not a directory: " (.getPath dir)) {:path (.getPath dir)}))))

(defn- py-repr-list [xs]
  (str "[" (str/join ", " (map #(str "'" % "'") xs)) "]"))

(defn- valid-os-path? [path]
  (let [s (str path)]
    (and (not (str/includes? s "\u0000"))
         (every? #(not (<= 0xD800 (int %) 0xDFFF)) s)
         (every? #(<= (count %) 255) (str/split s #"[/\\]")))))

(defn- make-error-dircmp [err-msg]
  (reify
    clojure.lang.IFn
    (invoke [_ _k] (throw (ex-info err-msg {})))
    clojure.lang.ILookup
    (valAt [_ _k] (throw (ex-info err-msg {})))
    (valAt [_ _k _default] (throw (ex-info err-msg {})))))

(defn dircmp
  [a b & options]
  (let [opts (if (and (= 1 (count options)) (map? (first options)))
               (first options)
               (apply hash-map options))]
    (if (or (not (valid-os-path? a)) (not (valid-os-path? b)))
      (make-error-dircmp (str "Invalid path: " (if (valid-os-path? a) b a)))
      (let [ignore (get opts :ignore default-ignores)
            hide (get opts :hide #{".git"})
            shallow (get opts :shallow true)
            dir-a (io/file a)
            dir-b (io/file b)
            excluded? #(or (contains? ignore %) (contains? hide %))
            list-a (->> (list-dir dir-a) (remove excluded?) sort vec)
            list-b (->> (list-dir dir-b) (remove excluded?) sort vec)
            set-a (set list-a)
            set-b (set list-b)
            common (vec (sort (set/intersection set-a set-b)))
            left-only (vec (sort (set/difference set-a set-b)))
            right-only (vec (sort (set/difference set-b set-a)))
            common-dirs (vec (filter #(.isDirectory (io/file a %)) common))
            common-files (vec (filter #(.isFile (io/file a %)) common))
            [same-files diff-files _] (if (seq common-files)
                                        (cmpfiles a b common-files shallow)
                                        [[] [] []])
            subdirs (into {} (map (fn [d] [d (dircmp (str a File/separator d)
                                                     (str b File/separator d)
                                                     opts)])
                                  common-dirs))
            report-fn (fn []
                        (println (str "diff " a " " b))
                        (when (seq same-files)
                          (println (str "Identical files : " (py-repr-list same-files))))
                        (when (seq diff-files)
                          (println (str "Differing files : " (py-repr-list diff-files))))
                        (when (seq left-only)
                          (println (str "Only in " a " : " (py-repr-list left-only))))
                        (when (seq right-only)
                          (println (str "Only in " b " : " (py-repr-list right-only))))
                        (when (seq common-dirs)
                          (println (str "Common subdirectories : " (py-repr-list common-dirs)))))
            report-partial-closure-fn (fn []
                                        (report-fn)
                                        (doseq [[_d sub] subdirs]
                                          (println "")
                                          ((:report sub))))]
        {:left a
         :right b
         :left-list list-a
         :right-list list-b
         :common common
         :left-only left-only
         :right-only right-only
         :common-dirs common-dirs
         :same-files same-files
         :diff-files diff-files
         :subdirs subdirs
         :report report-fn
         :report-partial-closure report-partial-closure-fn
         :report-full-closure (fn []
                                (report-fn)
                                (doseq [[_d sub] subdirs]
                                  (println "")
                                  ((:report-full-closure sub))))}))))
