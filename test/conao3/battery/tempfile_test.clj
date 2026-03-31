;; Original: Lib/test/test_tempfile.py

(ns conao3.battery.tempfile-test
  (:require
   [clojure.test :as t]
   [conao3.battery.tempfile :as tempfile])
  (:import
   [java.io File]))

;; Excluded:
;; - _xxx private API accessors
;; - filesystem-dependent tests

(t/deftest  test-exports
  (let [expected #{'NamedTemporaryFile 'TemporaryFile 'mkstemp 'mkdtemp 'mktemp
                  'TMP_MAX 'gettempprefix 'gettempprefixb 'gettempdir 'gettempdirb
                  'tempdir 'template 'SpooledTemporaryFile 'TemporaryDirectory}]
    (t/is (= expected (set (keys (ns-publics 'conao3.battery.tempfile)))))))

(t/deftest  test-sane-template
  (let [prefix (tempfile/gettempprefix)
        prefix-bytes (tempfile/gettempprefixb)]
    (t/is (string? prefix))
    (t/is (> (count prefix) 0))
    (t/is (= (type prefix) java.lang.String))
    (t/is (= (type prefix-bytes) (type (byte-array 0))))
    (t/is (> (alength prefix-bytes) 0))))

(t/deftest  test-same-thing
  (let [a (tempfile/gettempdir)
        b (tempfile/gettempdir)
        c (tempfile/gettempdirb)]
    (t/is (= a b))
    (t/is (not= (type a) (type c)))
    (t/is (= a (String. c "UTF-8")))))

(t/deftest test-mkstemp-basic
  (let [[fd path] (tempfile/mkstemp)]
    (t/is (= 0 fd))
    (t/is (string? path))
    (t/is (.exists (File. ^String path)))
    (.delete (File. ^String path))))

(t/deftest test-mkstemp-prefix-suffix
  (let [[_ path] (tempfile/mkstemp "txt" "myprefix" nil false)]
    (t/is (.startsWith (.getName (File. ^String path)) "myprefix"))
    (t/is (.endsWith path "txt"))
    (.delete (File. ^String path))))

(t/deftest test-mkdtemp-basic
  (let [path (tempfile/mkdtemp)]
    (t/is (string? path))
    (let [f (File. ^String path)]
      (t/is (.exists f))
      (t/is (.isDirectory f))
      (.delete f))))

(t/deftest test-mkdtemp-prefix
  (let [path (tempfile/mkdtemp nil "testprefix" nil)]
    (t/is (.startsWith (.getName (File. ^String path)) "testprefix"))
    (.delete (File. ^String path))))

(t/deftest test-mktemp-basic
  (let [path (tempfile/mktemp)]
    (t/is (string? path))
    (t/is (not (.exists (File. ^String path))))))

(t/deftest test-mktemp-in-dir
  (let [tmpdir (tempfile/gettempdir)
        path (tempfile/mktemp nil "prefix" tmpdir)]
    (t/is (.startsWith path tmpdir))
    (t/is (not (.exists (File. ^String path))))))

(t/deftest test-temporary-directory
  (let [td (tempfile/TemporaryDirectory)
        dir-path (:name td)]
    (t/is (string? dir-path))
    (t/is (.isDirectory (File. ^String dir-path)))
    ((:cleanup td))
    (t/is (not (.exists (File. ^String dir-path))))))
