;; Original: Lib/test/test_filecmp.py

(ns conao3.battery.filecmp-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.filecmp :as filecmp])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- create-file-shallow-equal [_template-path _new-path]
  (throw (ex-info "Not implemented" {})))

(defn- file-compare-setup []
  {:name "filecmp-test"
   :name-same "filecmp-test-same"
   :name-diff "filecmp-test-diff"
   :name-same-shallow "filecmp-test-same-shallow"
   :dir "/tmp"})

(defn- dir-compare-setup []
  {:dir "/tmp/dir"
   :dir-same "/tmp/dir-same"
   :dir-diff "/tmp/dir-diff"
   :dir-diff-file "/tmp/dir-diff-file"
   :dir-same-shallow "/tmp/dir-same-shallow"
   :case-insensitive false})

(defn- assert-lists [actual expected]
  (t/is (= (vec (sort actual))
           (vec (sort expected)))))

(defn- assert-dircmp-identical-directories
  [state & [options]]
  (let [left-dir (:dir state)
        right-dir (:dir-same state)
        d (apply filecmp/dircmp (concat [left-dir right-dir] options))]
    (t/is (= left-dir (:left d)))
    (t/is (= right-dir (:right d)))
    (if (:case-insensitive state)
      (assert-lists (:left-list d) ["file" "subdir"])
      (assert-lists (:left-list d) ["file" "subdir"]))
    (if (:case-insensitive state)
      (assert-lists (:right-list d) ["FiLe" "subdir"])
      (assert-lists (:right-list d) ["file" "subdir"]))
    (assert-lists (:common d) ["file" "subdir"])
    (assert-lists (:common-dirs d) ["subdir"])
    (t/is (= [] (:left-only d)))
    (t/is (= [] (:right-only d)))
    (t/is (= ["file"] (:same-files d)))
    (t/is (= [] (:diff-files d)))))

(defn- assert-dircmp-different-directories
  [state & [options]]
  (let [left-dir (:dir state)
        right-dir (:dir-diff state)
        d (apply filecmp/dircmp (concat [left-dir right-dir] options))]
    (t/is (= left-dir (:left d)))
    (t/is (= right-dir (:right d)))
    (assert-lists (:left-list d) ["file" "subdir"])
    (assert-lists (:right-list d) ["file" "file2" "subdir"])
    (assert-lists (:common d) ["file" "subdir"])
    (assert-lists (:common-dirs d) ["subdir"])
    (t/is (= [] (:left-only d)))
    (t/is (= ["file2"] (:right-only d)))
    (t/is (= ["file"] (:same-files d)))
    (t/is (= [] (:diff-files d)))
    (let [left-dir-right (:dir state)
          right-dir-left (:dir-diff state)
          d' (apply filecmp/dircmp (concat [left-dir-right right-dir-left] options))]
      (t/is (= left-dir-right (:left d')))
      (t/is (= right-dir-left (:right d')))
      (assert-lists (:left-list d') ["file" "file2" "subdir"])
      (assert-lists (:right-list d') ["file" "subdir"])
      (assert-lists (:common d') ["file" "subdir"])
      (assert-lists (:common-dirs d') ["subdir"])
      (t/is (= ["file2"] (:left-only d')))
      (t/is (= [] (:right-only d')))
      (t/is (= ["file"] (:same-files d')))
      (t/is (= [] (:diff-files d'))))))

(defn- assert-dircmp-different-file
  [state & [options]]
  (let [left-dir (:dir-diff state)
        right-dir (:dir-diff-file state)
        d (apply filecmp/dircmp (concat [left-dir right-dir] options))]
    (t/is (= ["file"] (:same-files d)))
    (t/is (= ["file2"] (:diff-files d)))))

(defn- assert-report [report expected-report-lines]
  (let [captured (->> (with-out-str (report))
                      str/split-lines
                      vec)]
    (t/is (= expected-report-lines captured))))

(defn- assert-lists-eq [actual expected]
  (assert-lists actual expected))

(t/deftest ^:kaocha/skip test-matching
  (let [{:keys [name name-same name-same-shallow]} (file-compare-setup)]
    (t/is (filecmp/cmp name name))
    (t/is (filecmp/cmp name name false))
    (t/is (filecmp/cmp name name-same))
    (t/is (filecmp/cmp name name-same false))
    (t/is (filecmp/cmp name name-same-shallow))
    (t/is (thrown? ExceptionInfo (filecmp/cmp name nil)))))

(t/deftest ^:kaocha/skip test-different
  (let [{:keys [name name-diff dir name-same-shallow]} (file-compare-setup)]
    (t/is (false? (filecmp/cmp name name-diff)))
    (t/is (false? (filecmp/cmp name dir)))
    (t/is (false? (filecmp/cmp name name-same-shallow false)))))

(t/deftest ^:kaocha/skip test-cache-clear
  (let [{:keys [name name-same name-diff]} (file-compare-setup)]
    (filecmp/cmp name name-same false)
    (filecmp/cmp name name-diff false)
    (filecmp/clear-cache)
    (t/is (= 0 (count @filecmp/_cache)))))

(t/deftest test-default-ignores
  (t/is (contains? filecmp/default-ignores ".hg")))

(t/deftest ^:kaocha/skip test-cmpfiles
  (let [state (dir-compare-setup)]
    (t/is (= ["file"] (first (filecmp/cmpfiles (:dir state) (:dir state) ["file"]))))
    (t/is (= [] (second (filecmp/cmpfiles (:dir state) (:dir state) ["file"]))))
    (t/is (= [] (nth (filecmp/cmpfiles (:dir state) (:dir state) ["file"]) 2)))
    (t/is (= ["file"] (first (filecmp/cmpfiles (:dir state) (:dir-same state) ["file"]))))
    (t/is (= [] (second (filecmp/cmpfiles (:dir state) (:dir-same state) ["file"]))))
    (t/is (= [] (nth (filecmp/cmpfiles (:dir state) (:dir-same state) ["file"]) 2)))
    (t/is (= ["file"] (first (filecmp/cmpfiles (:dir state) (:dir-state state) ["file"] false))))
    (t/is (= [] (second (filecmp/cmpfiles (:dir state) (:dir-state state) ["file"] false))))
    (t/is (= [] (nth (filecmp/cmpfiles (:dir state) (:dir-state state) ["file"] false) 2)))
    (t/is (= ["file"] (first (filecmp/cmpfiles (:dir state) (:dir-same state) ["file"] false))))
    (t/is (= [] (second (filecmp/cmpfiles (:dir state) (:dir-same state) ["file"] false))))
    (t/is (= [] (nth (filecmp/cmpfiles (:dir state) (:dir-same state) ["file"] false) 2)))
    (t/is (= ["file"] (first (filecmp/cmpfiles (:dir state) (:dir-diff-file state) ["file" "file2"] false))))
    (t/is (= ["file2"] (second (filecmp/cmpfiles (:dir state) (:dir-diff-file state) ["file" "file2"]))))
    (t/is (= [] (nth (filecmp/cmpfiles (:dir state) (:dir-diff-file state) ["file" "file2"]) 2)))))

(t/deftest ^:kaocha/skip test-cmpfiles-invalid-names
  (let [state (dir-compare-setup)
        cases [["\u0000" "NUL bytes filename"]
               [(str "__file__" "\u0000") "filename with embedded NUL bytes"]
               ["\uD834\uDD1E.py" "surrogate codes (MUSICAL SYMBOL G CLEF)"]
               [(str (apply str (repeat 1000000 \a)) "very long filename")]]]
    (doseq [[file _desc] cases]
      (doseq [other-dir [(:dir state) (:dir-same state) (:dir-diff state)]]
        (let [result (filecmp/cmpfiles (:dir state) other-dir [file])]
          (t/is (= [[] [] [file]] result)))))))

(t/deftest ^:kaocha/skip test-dircmp-invalid-names
  (let [state (dir-compare-setup)
        cases [["\u0000" "NUL bytes dirname"]
               [(str "Top" java.io.File/separator "Mid\u0000") "dirname with embedded NUL bytes"]
               ["\uD834\uDD1E" "surrogate codes (MUSICAL SYMBOL G CLEF)"]
               [(str (apply str (repeat 1000000 \a)) "very long dirname")]]]
    (doseq [[bad-dir _desc] cases]
      (let [d1 (filecmp/dircmp (:dir state) bad-dir)
            d2 (filecmp/dircmp bad-dir (:dir state))]
        (doseq [target [:left-list :right-list :left-only :right-only :common]]
          (t/is (thrown? ExceptionInfo (d1 target)))
          (t/is (thrown? ExceptionInfo (d2 target))))))))

(t/deftest ^:kaocha/skip test-dircmp-identical-directories
  (let [state (dir-compare-setup)]
    (assert-dircmp-identical-directories state)
    (assert-dircmp-identical-directories state {:shallow false})))

(t/deftest ^:kaocha/skip test-dircmp-different-file
  (let [state (dir-compare-setup)]
    (assert-dircmp-different-file state)
    (assert-dircmp-different-file state {:shallow false})))

(t/deftest ^:kaocha/skip test-dircmp-different-directories
  (let [state (dir-compare-setup)]
    (assert-dircmp-different-directories state)
    (assert-dircmp-different-directories state {:shallow false})))

(t/deftest ^:kaocha/skip test-dircmp-no-shallow-different-file
  (let [state (dir-compare-setup)
        d (filecmp/dircmp (:dir state) (:dir-same-shallow state) {:shallow false})]
    (t/is (= [] (:same-files d)))
    (t/is (= ["file"] (:diff-files d)))
    (t/is (= [] (:same-files d)))
    (t/is (= ["file"] (:diff-files d)))))

(t/deftest ^:kaocha/skip test-dircmp-shallow-same-file
  (let [state (dir-compare-setup)
        d (filecmp/dircmp (:dir state) (:dir-same-shallow state))]
    (t/is (= ["file"] (:same-files d)))
    (t/is (= [] (:diff-files d)))))

(t/deftest ^:kaocha/skip test-dircmp-shallow-is-keyword-only
  (let [state (dir-compare-setup)]
    (t/is (thrown-with-msg? ExceptionInfo #"dircmp.__init__() takes from 3 to 5 positional arguments but 6 were given"
                           (filecmp/dircmp (:dir state) (:dir-same state) nil nil true)))
    (t/is (instance? clojure.lang.IMapEntry (filecmp/dircmp (:dir state) (:dir-same state) nil nil :shallow true)))
    (t/is (false? (instance? clojure.lang.IMapEntry (filecmp/dircmp (:dir state) (:dir-same state) nil nil :shallow true))))))

(t/deftest ^:kaocha/skip test-dircmp-subdirs-type
  (let [state (dir-compare-setup)
        d (filecmp/dircmp (:dir state) (:dir-diff state))
        sub-dirs (or (:subdirs d) {})]
    (t/is (= ["subdir"] (keys sub-dirs)))
    (let [sub-dcmp (get sub-dirs "subdir")]
      (t/is (= true (map? sub-dcmp))))))

(t/deftest ^:kaocha/skip test-report-partial-closure
  (let [state (dir-compare-setup)
        d (filecmp/dircmp (:dir state) (:dir-same state))
        left-subdir (str (:dir state) "/subdir")
        right-subdir (str (:dir-same state) "/subdir")
        expected-report [(str "diff " (:dir state) " " (:dir-same state))
                         "Identical files : ['file']"
                         "Common subdirectories : ['subdir']"
                         ""
                         (str "diff " left-subdir " " right-subdir)]]
    (assert-report (:report-partial-closure d) expected-report)))

(t/deftest ^:kaocha/skip test-report-full-closure
  (let [state (dir-compare-setup)
        d (filecmp/dircmp (:dir state) (:dir-same state))
        left-subdir (str (:dir state) "/subdir")
        right-subdir (str (:dir-same state) "/subdir")
        expected-report [(str "diff " (:dir state) " " (:dir-same state))
                         "Identical files : ['file']"
                         "Common subdirectories : ['subdir']"
                         ""
                         (str "diff " left-subdir " " right-subdir)]]
    (assert-report (:report-full-closure d) expected-report)))
