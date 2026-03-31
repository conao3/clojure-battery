;; Original: Lib/test/test_configparser.py

(ns conao3.battery.configparser-test
  (:require
   [clojure.test :as t]
   [conao3.battery.configparser :as configparser])
  (:import
   [clojure.lang ExceptionInfo]))

;; BasicTestCase / RawConfigParserTestCase

(t/deftest test-basic
  (let [cfg (configparser/read-string "[Foo Bar]\nfoo=bar1\n[Spacey Bar]\nfoo = bar2\n")]
    (t/is (= "bar1" (configparser/get cfg "Foo Bar" "foo")))
    (t/is (= "bar2" (configparser/get cfg "Spacey Bar" "foo")))))

(t/deftest test-sections
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\n[section2]\nbaz=qux\n")]
    (t/is (= (sort ["section1" "section2"]) (sort (configparser/sections cfg))))))

(t/deftest test-items
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\nbaz=qux\n")]
    (t/is (= (sort [["foo" "bar"] ["baz" "qux"]]) (sort (configparser/items cfg "section1"))))))

(t/deftest test-types
  (let [cfg (configparser/read-string "[Types]\nint=42\nfloat=0.44\nboolean=off\n")]
    (t/is (= 42 (configparser/get-int cfg "Types" "int")))
    (t/is (< (Math/abs (- 0.44 (configparser/get-float cfg "Types" "float"))) 1e-10))
    (t/is (false? (configparser/get-boolean cfg "Types" "boolean")))))

(t/deftest test-boolean-values
  (let [cfg (configparser/read-string "[b]\nt1=1\nt2=yes\nt3=true\nt4=on\nf1=0\nf2=no\nf3=false\nf4=off\n")]
    (t/is (true? (configparser/get-boolean cfg "b" "t1")))
    (t/is (true? (configparser/get-boolean cfg "b" "t2")))
    (t/is (true? (configparser/get-boolean cfg "b" "t3")))
    (t/is (true? (configparser/get-boolean cfg "b" "t4")))
    (t/is (false? (configparser/get-boolean cfg "b" "f1")))
    (t/is (false? (configparser/get-boolean cfg "b" "f2")))
    (t/is (false? (configparser/get-boolean cfg "b" "f3")))
    (t/is (false? (configparser/get-boolean cfg "b" "f4")))))

(t/deftest test-case-sensitivity
  (let [cfg (configparser/read-string "[section1]\nFoo=bar\n")]
    (t/is (= "bar" (configparser/get cfg "section1" "foo")))))

(t/deftest test-has-section
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\n")]
    (t/is (true? (configparser/has-section cfg "section1")))
    (t/is (false? (configparser/has-section cfg "section2")))))

(t/deftest test-has-option
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\n")]
    (t/is (true? (configparser/has-option cfg "section1" "foo")))
    (t/is (false? (configparser/has-option cfg "section1" "baz")))))

(t/deftest test-no-section-error
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\n")]
    (t/is (thrown? ExceptionInfo (configparser/get cfg "no-such-section" "foo")))))

(t/deftest test-no-option-error
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\n")]
    (t/is (thrown? ExceptionInfo (configparser/get cfg "section1" "no-such-option")))))

(t/deftest test-fallback
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\n")]
    (t/is (= "bar" (configparser/get cfg "section1" "foo" "fallback")))
    (t/is (= "fallback" (configparser/get cfg "section1" "no-such" "fallback")))
    (t/is (= "fallback" (configparser/get cfg "no-such-section" "foo" "fallback")))))

(t/deftest test-multiline-values
  (let [cfg (configparser/read-string "[section1]\nfoo=this line is much, much longer\n    than my editor\n    likes it.\n")]
    (t/is (= "this line is much, much longer\nthan my editor\nlikes it." (configparser/get cfg "section1" "foo")))))

(t/deftest test-comment-prefixes
  (let [cfg (configparser/read-string "[section1]\nfoo=bar  ; inline comment\nbaz=qux  # another comment\n")]
    (t/is (= "bar" (configparser/get cfg "section1" "foo")))
    (t/is (= "qux" (configparser/get cfg "section1" "baz")))))

(t/deftest test-remove-section
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\n[section2]\nbaz=qux\n")]
    (t/is (true? (configparser/remove-section cfg "section1")))
    (t/is (false? (configparser/has-section cfg "section1")))
    (t/is (false? (configparser/remove-section cfg "section1")))))

(t/deftest test-remove-option
  (let [cfg (configparser/read-string "[section1]\nfoo=bar\nbaz=qux\n")]
    (t/is (true? (configparser/remove-option cfg "section1" "foo")))
    (t/is (false? (configparser/has-option cfg "section1" "foo")))
    (t/is (false? (configparser/remove-option cfg "section1" "foo")))))

(t/deftest test-colon-delimiter
  (let [cfg (configparser/read-string "[section]\nkey: value\n")]
    (t/is (= "value" (configparser/get cfg "section" "key")))))

(t/deftest test-empty-value
  (let [cfg (configparser/read-string "[section]\nkey=\n")]
    (t/is (= "" (configparser/get cfg "section" "key")))))

(t/deftest test-multiple-sections
  (let [cfg (configparser/read-string "[s1]\na=1\n[s2]\nb=2\n[s3]\nc=3\n")]
    (t/is (= 3 (count (configparser/sections cfg))))))

(t/deftest test-items-multiple-keys
  (let [cfg (configparser/read-string "[s]\na=1\nb=2\nc=3\n")
        kv (into {} (configparser/items cfg "s"))]
    (t/is (= "1" (get kv "a")))
    (t/is (= "2" (get kv "b")))
    (t/is (= "3" (get kv "c")))
    (t/is (= 3 (count kv)))))
