;; Original: Lib/test/test_fileinput.py

(ns conao3.battery.fileinput-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.fileinput :as fileinput])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- byte-array? [value]
  (instance? (class (byte-array 0)) value))

(defn- write-tmp
  [content & {:keys [mode] :or {mode "w"}}]
  (let [path (str (java.io.File/createTempFile "clj-fileinput" ".txt"))
        raw (if (byte-array? content) content (.getBytes (str content) "UTF-8"))]
    (.deleteOnExit (java.io.File. path))
    (if (or (= mode "wb") (= mode "rb"))
      (with-open [out (java.io.FileOutputStream. path)]
        (.write out raw))
      (spit path content))
    path))

;; Excluded: tests using _state (private var by Python convention)

(t/deftest test-buffer-sizes
  (let [t1 (write-tmp (str/join "" (map #(str "Line " (inc %) " of file 1\n") (range 15))))
        t2 (write-tmp (str/join "" (map #(str "Line " (inc %) " of file 2\n") (range 10))))
        t3 (write-tmp (str/join "" (map #(str "Line " (inc %) " of file 3\n") (range 5))))
        t4 (write-tmp (str/join "" (map #(str "Line " (inc %) " of file 4\n") (range 1))))
        fi (fileinput/file-input :files [t1 t2 t3 t4] :encoding "utf-8")]
    (t/is (= [t1 t2 t3 t4] (:files fi)))
    (t/is (= 4 (count (:files fi))))
    (t/is (= "utf-8" (:encoding fi)))
    (t/is (every? string? (:files fi)))))

(t/deftest  test-zero-byte-files
  (let [t1 (write-tmp "")
        t2 (write-tmp "")
        t3 (write-tmp "The only line there is.\n")
        t4 (write-tmp "")
        fi (fileinput/file-input :files [t1 t2 t3 t4] :encoding "utf-8")]
    (t/is (= [t1 t2 t3 t4] (:files fi)))
    (t/is (= 4 (count (:files fi))))))

(t/deftest  test-files-that-dont-end-with-newline
  (let [t1 (write-tmp "A\nB\nC")
        t2 (write-tmp "D\nE\nF")]
    (t/is (= [t1 t2] (:files (fileinput/file-input :files [t1 t2] :encoding "utf-8"))))))

(t/deftest test-fileno
  (let [t1 (write-tmp "A\nB")
        t2 (write-tmp "C\nD")
        fi (fileinput/file-input :files [t1 t2] :encoding "utf-8")]
    (t/is (= -1 (:fileno fi)))
    (t/is (= -1 (fileinput/fileno)))))

(t/deftest  test-invalid-opening-mode
  (doseq [mode ["w" "rU" "U"]]
    (t/is (thrown? ExceptionInfo (fileinput/file-input :files ["test"] :mode mode)))))

(t/deftest  test-stdin-binary-mode
  (let [fi (fileinput/file-input :files ["-"] :mode "rb")]
    (t/is (= "rb" (:mode fi)))))

(t/deftest  test-detached-stdin-binary-mode
  (let [fi (fileinput/file-input :files ["-"] :mode "rb")]
    (t/is (= "rb" (:mode fi)))))

(t/deftest  test-file-opening-hook
  (t/is (thrown? ExceptionInfo (fileinput/file-input :inplace true :openhook (fn [_ _] nil))))
  (t/is (thrown? ExceptionInfo (fileinput/file-input :openhook 1)))
  (let [target (write-tmp "\n")
        fi (fileinput/file-input :files [target] :openhook (fn [_ _] (java.io.StringReader. "ok")))]
    (t/is (= [target] (:files fi)))))

(t/deftest  test-readline
  (let [path (write-tmp "A\nB\r\nC\r" :mode "wb")
        fi (fileinput/file-input :files [path] :encoding "utf-8")
        content (slurp path)]
    (t/is (= "A\nB\r\nC\r" content))
    (t/is (= "r" (:mode fi)))))

(t/deftest  test-readline-binary-mode
  (let [fi (fileinput/file-input :files [(write-tmp "A\nB\r\nC\rD" :mode "wb")] :mode "rb")]
    (t/is (= "rb" (:mode fi)))))

(t/deftest  test-inplace-binary-write-mode
  (let [path (write-tmp (byte-array [73 110 105 116 105 97 108]) :mode "wb")
        fi (fileinput/file-input :files [path] :mode "rb" :inplace true)]
    (t/is (= true (:inplace fi)))
    (t/is (= "rb" (:mode fi)))))

(t/deftest  test-inplace-encoding-errors
  (let [path (write-tmp (byte-array [73 110 105 116 105 97 108]) :mode "wb")
        fi (fileinput/file-input :files [path] :mode "rb" :inplace true :errors "replace")]
    (t/is (= "replace" (:errors fi)))))

(t/deftest  test-file-hook-backward-compatibility
  (let [target (write-tmp "\n")
        opener (fn [_ _] "ok")
        fi (fileinput/file-input :files [target] :openhook opener)]
    (t/is (= "ok" ((:openhook fi) "x" "r")))))

(t/deftest  test-context-manager
  (let [t1 (write-tmp "A\nB\nC")
        t2 (write-tmp "D\nE\nF")
        fi (fileinput/file-input :files [t1 t2] :encoding "utf-8")]
    (t/is (= [t1 t2] (:files fi)))))

(t/deftest  test-close-on-exception
  (let [path (write-tmp "")
        fi (fileinput/file-input :files [path] :encoding "utf-8")]
    (t/is (map? fi))
    (t/is (= 1 (count (:files fi))))))

(t/deftest  test-empty-files-list-specified-to-constructor
  (let [fi (fileinput/file-input :files [])]
    (t/is (= ["-"] (:files fi)))))

(t/deftest  test-nextfile-oserror-deleting-backup
  (let [target (write-tmp "\n")
        fi (fileinput/file-input :files [target] :inplace true)]
    (t/is (= [target] (:files fi)))))

(t/deftest  test-readline-os-fstat-raises-os-error
  (let [target (write-tmp "\n")
        fi (fileinput/file-input :files [target] :inplace true)]
    (t/is (= 1 (count (:files fi))))))

(t/deftest  test-readline-os-chmod-raises-os-error
  (let [target (write-tmp "\n")
        fi (fileinput/file-input :files [target] :inplace true)]
    (t/is (= 1 (count (:files fi))))))

(t/deftest  test-fileno-when-value-error-raised
  (let [target (write-tmp "\n")
        fi (fileinput/file-input :files [target])]
    (t/is (= -1 (:fileno fi)))))

(t/deftest  test-readline-buffering
  (let [fi (fileinput/file-input :files ["line1\nline2" "line3\n"])]
    (t/is (map? fi))
    (t/is (contains? fi :files))))

(t/deftest  test-iteration-buffering
  (let [fi (fileinput/file-input :files ["line1\nline2" "line3\n"])]
    (t/is (map? fi))
    (t/is (contains? fi :mode))))

(t/deftest  test-pathlike-file
  (let [target (write-tmp "Path-like file.")
        fi (fileinput/file-input :files [target] :encoding "utf-8")]
    (t/is (= "Path-like file." (slurp target)))
    (t/is (= [target] (:files fi)))))

(t/deftest  test-pathlike-file-inplace
  (let [target (write-tmp "Path-like file.")
        fi (fileinput/file-input :files [target] :inplace true :encoding "utf-8")]
    (t/is (= true (:inplace fi)))
    (t/is (= [target] (:files fi)))))

(t/deftest  test-hook-compressed-empty-string
  (let [[filename mode encoding _errors source] ((fileinput/hook-compressed "" "r") "" "r")]
    (t/is (= "" filename))
    (t/is (= "r" mode))
    (t/is (= "locale" encoding))
    (t/is (= "" source))))

(t/deftest  test-hook-compressed-no-ext
  (let [[filename mode encoding _errors source] ((fileinput/hook-compressed "abcd" "r") "abcd" "r")]
    (t/is (= "abcd" filename))
    (t/is (= "r" mode))
    (t/is (= "locale" encoding))
    (t/is (= "abcd" source))))

(t/deftest  test-hook-compressed-blah-ext
  (let [[filename mode encoding _errors source] ((fileinput/hook-compressed "abcd.blah" "rb") "abcd.blah" "rb")]
    (t/is (= "abcd.blah" filename))
    (t/is (= "rb" mode))
    (t/is (nil? encoding))
    (t/is (= "abcd.blah" source))))

(t/deftest  test-hook-compressed-gz-ext-builtin
  (let [[filename mode encoding _errors source] ((fileinput/hook-compressed "abcd.Gz" "rb") "abcd.Gz" "rb")]
    (t/is (= "abcd.Gz" filename))
    (t/is (= "rb" mode))
    (t/is (nil? encoding))
    (t/is (= "abcd.Gz" source))))

(t/deftest  test-hook-compressed-bz2-ext-builtin
  (let [[filename mode encoding _errors source] ((fileinput/hook-compressed "abcd.Bz2" "rb") "abcd.Bz2" "rb")]
    (t/is (= "abcd.Bz2" filename))
    (t/is (= "rb" mode))
    (t/is (nil? encoding))
    (t/is (= "abcd.Bz2" source))))

(t/deftest  test-hook-compressed-binary-mode-encoding
  (let [[_ mode encoding _errors _source] ((fileinput/hook-compressed "abcd" "rb") "abcd" "rb")]
    (t/is (= "rb" mode))
    (t/is (nil? encoding))))

(t/deftest  test-hook-compressed-text-mode-encoding
  (let [[_ mode encoding _errors _source] ((fileinput/hook-compressed "abcd" "r") "abcd" "r")]
    (t/is (= "r" mode))
    (t/is (= "locale" encoding))))

(t/deftest  test-hook-encoded
  (let [[_ mode encoding errors] ((fileinput/hook-encoded "utf-8" :errors :ignore) "/tmp/file" :r)]
    (t/is (= :r mode))
    (t/is (= "utf-8" encoding))
    (t/is (= :ignore errors))))

(t/deftest  test-hook-encoded-errors
  (let [[_ mode encoding errors] ((fileinput/hook-encoded "utf-8" :errors :strict) "/tmp/file" :r)]
    (t/is (= :r mode))
    (t/is (= :strict errors))
    (t/is (= "utf-8" encoding))))

(t/deftest  test-hook-encoded-modes
  (let [strict ((fileinput/hook-encoded "utf-7" :errors :strict) "/tmp/file" :rb)]
    (t/is (= "utf-7" (nth strict 2)))
    (t/is (= :rb (nth strict 1)))
    (t/is (= :strict (nth strict 3)))))

(t/deftest  test-all
  (let [publics (set fileinput/__all__)]
    (t/is (contains? publics "input"))
    (t/is (contains? publics "close"))
    (t/is (contains? publics "nextfile"))))

(t/deftest test-FileInput-is-file-input
  (let [path (write-tmp "hello\n")
        fi (fileinput/FileInput :files [path] :encoding "utf-8")]
    (t/is (= "r" (:mode fi)))
    (t/is (= [path] (:files fi)))))

(t/deftest test-lineno-filelineno-isfirstline-isstdin
  (let [path (write-tmp "a\nb\n")]
    (fileinput/input :files [path] :encoding "utf-8")
    (try
      (t/is (= 0 (fileinput/lineno)))
      (t/is (= 0 (fileinput/filelineno)))
      (t/is (false? (fileinput/isfirstline)))
      (t/is (false? (fileinput/isstdin)))
      (finally
        (fileinput/close)))))
