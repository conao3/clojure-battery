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

(t/deftest test-multiline-values
  ;; Indented continuation lines are part of the value
  (let [cfg (configparser/read-string "[s]\nkey=first line\n\tsecond line\n\tthird line\n")]
    (t/is (= "first line\nsecond line\nthird line" (configparser/get cfg "s" "key")))))

(t/deftest test-duplicate-section-merges
  ;; Duplicate sections should merge (not overwrite)
  (let [cfg (configparser/read-string "[section]\nkey1=value1\n[section]\nkey2=value2\n")]
    (t/is (= "value1" (configparser/get cfg "section" "key1")))
    (t/is (= "value2" (configparser/get cfg "section" "key2")))
    (t/is (= 1 (count (configparser/sections cfg))))))

(t/deftest test-inline-comment-stripping
  (let [cfg (configparser/read-string "[s]\nkey1=value ; comment\nkey2=value # comment\n")]
    (t/is (= "value" (configparser/get cfg "s" "key1")))
    (t/is (= "value" (configparser/get cfg "s" "key2")))))

(t/deftest test-key-case-insensitive
  ;; Keys should be normalized to lowercase (Python behavior)
  (let [cfg (configparser/read-string "[s]\nFOO=bar\nMixedCase=val\n")]
    (t/is (= "bar" (configparser/get cfg "s" "foo")))
    (t/is (= "val" (configparser/get cfg "s" "mixedcase")))))

(t/deftest test-get-boolean-values
  (let [cfg (configparser/read-string "[s]\nt1=true\nt2=yes\nt3=on\nt4=1\nf1=false\nf2=no\nf3=off\nf4=0\n")]
    (t/is (true? (configparser/get-boolean cfg "s" "t1")))
    (t/is (true? (configparser/get-boolean cfg "s" "t2")))
    (t/is (true? (configparser/get-boolean cfg "s" "t3")))
    (t/is (true? (configparser/get-boolean cfg "s" "t4")))
    (t/is (false? (configparser/get-boolean cfg "s" "f1")))
    (t/is (false? (configparser/get-boolean cfg "s" "f2")))))

(t/deftest test-get-with-fallback
  (let [cfg (configparser/read-string "[s]\nkey=value\n")]
    (t/is (= "value" (configparser/get cfg "s" "key")))
    (t/is (= "default" (configparser/get cfg "s" "missing" "default")))))

(t/deftest test-get-int-and-float
  (let [cfg (configparser/read-string "[s]\ni=42\nf=3.14\n")]
    (t/is (= 42 (configparser/get-int cfg "s" "i")))
    (t/is (< (Math/abs (- 3.14 (configparser/get-float cfg "s" "f"))) 0.0001))))

(t/deftest test-has-option
  (let [cfg (configparser/read-string "[s]\nkey=val\n")]
    (t/is (true? (configparser/has-option cfg "s" "key")))
    (t/is (false? (configparser/has-option cfg "s" "missing")))
    (t/is (false? (configparser/has-option cfg "nosection" "key")))))
