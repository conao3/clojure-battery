;; Original: Lib/test/test_tempfile.py

(ns conao3.battery.tempfile-test
  (:require
   [clojure.test :as t]
   [conao3.battery.tempfile :as tempfile]))

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
