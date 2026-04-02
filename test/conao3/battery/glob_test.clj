;; Original: Lib/test/test_glob.py

(ns conao3.battery.glob-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.glob :as glob])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- ->bytes [value]
  (.getBytes ^String value "UTF-8"))

;; Excluded: all filesystem-dependent tests (require real directory structure)

(t/deftest  test-escape
  (t/is (= "abc" (glob/escape "abc")))
  (t/is (= "[[]" (glob/escape "[")))
  (t/is (= "[?]" (glob/escape "?")))
  (t/is (= "[*]" (glob/escape "*")))
  (t/is (= "[[][[]_/[*][?][*]/_]]" (glob/escape "[[_/*?*/_]]")))
  (t/is (= "/[[][[]_/[*][?][*]/_]]/" (glob/escape "/[[_/*?*/_]]/")))
  (t/is (= (seq (->bytes "[[]")) (seq (glob/escape (->bytes "[")))))
  (t/is (= (seq (->bytes "[?]")) (seq (glob/escape (->bytes "?")))))
  (t/is (= (seq (->bytes "[*]")) (seq (glob/escape (->bytes "*"))))))

(t/deftest test-translate-matching
  (let [to-regex #(re-pattern (glob/translate %))]
    (t/is (not (nil? (re-matches (to-regex "*") "foo")))
          "glob translate should be compilable")
    (t/is (not (nil? (re-matches (to-regex "foo.bar") "foo.bar")))
          "dot should be matched by wildcard pattern")
    (t/is (not (nil? (re-matches (re-pattern (glob/translate "**" :recursive true)) "foo")))
          "recursive glob should match nested paths")
    (t/is (nil? (re-matches (re-pattern (glob/translate ".*" :recursive true)) ".foo")))
    (t/is (not (nil? (re-matches (re-pattern (glob/translate (str "foo" "/" "bar") :seps "/")) "foo/bar"))))))

(t/deftest  test-translate
  (t/is (= "(?s:foo)\\z" (glob/translate "foo" :seps "/")))
  (t/is (= "(?s:foo/bar)\\z" (glob/translate "foo/bar" :seps "/")))
  (t/is (= "(?s:(?!\\.)[^/]*)\\z" (glob/translate "**" :seps "/")))
  (t/is (str/includes? (glob/translate "a?" :seps "/") "a")))

(t/deftest  test-translate-include-hidden
  (t/is (= "(?s:[^/]+)\\z" (glob/translate "*" :include-hidden true :seps "/")))
  (t/is (= "(?s:[^/]*)\\z" (glob/translate "**" :include-hidden true :seps "/")))
  (t/is (not= (glob/translate "*" :include-hidden true :seps "/")
              (glob/translate "*" :seps "/"))))

(t/deftest  test-translate-recursive
  (t/is (= "(?s:[^/]+)\\z" (glob/translate "*" :recursive true :include-hidden true :seps "/")))
  (t/is (= "(?s:.*)\\z" (glob/translate "**" :recursive true :include-hidden true :seps "/"))))

(t/deftest  test-translate-seps
  (let [translated (glob/translate (str "foo" "/" "bar" "/" "baz") :recursive true :include-hidden true :seps ["/" "\\"])]
    (t/is (re-matches (re-pattern translated) "foo/bar/baz"))
    (t/is (str/includes? translated "/"))))

(t/deftest test-glob-returns-vector
  (let [tmpdir (java.io.File/createTempFile "glob-test-dir" "")
        _ (.delete tmpdir)
        _ (.mkdir tmpdir)
        f1 (java.io.File. tmpdir "foo.txt")
        f2 (java.io.File. tmpdir "bar.txt")
        f3 (java.io.File. tmpdir "baz.clj")]
    (.createNewFile f1)
    (.createNewFile f2)
    (.createNewFile f3)
    (try
      (let [result (glob/glob "*.txt" {:root-dir (.getAbsolutePath tmpdir)})]
        (t/is (vector? result))
        (t/is (= 2 (count result)))
        (t/is (every? #(str/ends-with? % ".txt") result)))
      (finally
        (.delete f1) (.delete f2) (.delete f3) (.delete tmpdir)))))

(t/deftest test-glob-no-match
  (let [tmpdir (java.io.File/createTempFile "glob-empty" "")
        _ (.delete tmpdir)
        _ (.mkdir tmpdir)]
    (try
      (let [result (glob/glob "*.xyz" {:root-dir (.getAbsolutePath tmpdir)})]
        (t/is (vector? result))
        (t/is (empty? result)))
      (finally (.delete tmpdir)))))

(t/deftest test-iglob-returns-lazy
  (let [tmpdir (java.io.File/createTempFile "iglob-test" "")
        _ (.delete tmpdir)
        _ (.mkdir tmpdir)
        f (java.io.File. tmpdir "test.txt")]
    (.createNewFile f)
    (try
      (let [result (glob/iglob "*.txt" {:root-dir (.getAbsolutePath tmpdir)})]
        (t/is (seq? (seq result)))
        (t/is (= 1 (count (vec result)))))
      (finally (.delete f) (.delete tmpdir)))))

(t/deftest test-glob-all-files
  (let [tmpdir (java.io.File/createTempFile "glob-all" "")
        _ (.delete tmpdir)
        _ (.mkdir tmpdir)
        f1 (java.io.File. tmpdir "a.txt")
        f2 (java.io.File. tmpdir "b.txt")]
    (.createNewFile f1)
    (.createNewFile f2)
    (try
      (let [result (glob/glob "*" {:root-dir (.getAbsolutePath tmpdir)})]
        (t/is (vector? result))
        (t/is (= 2 (count result))))
      (finally (.delete f1) (.delete f2) (.delete tmpdir)))))

(t/deftest test-escape-all-chars
  (t/is (= "[[]" (glob/escape "[")))
  (t/is (= "[?]" (glob/escape "?")))
  (t/is (= "[*]" (glob/escape "*")))
  (t/is (= "abc" (glob/escape "abc"))))

(t/deftest test-glob-specific-extension
  (let [tmpdir (java.io.File/createTempFile "glob-ext" "")
        _ (.delete tmpdir)
        _ (.mkdir tmpdir)
        f1 (java.io.File. tmpdir "foo.clj")
        f2 (java.io.File. tmpdir "bar.clj")
        f3 (java.io.File. tmpdir "baz.txt")]
    (.createNewFile f1)
    (.createNewFile f2)
    (.createNewFile f3)
    (try
      (let [result (glob/glob "*.clj" {:root-dir (.getAbsolutePath tmpdir)})]
        (t/is (= 2 (count result)))
        (t/is (every? #(str/ends-with? % ".clj") result)))
      (finally (.delete f1) (.delete f2) (.delete f3) (.delete tmpdir)))))

(t/deftest test-translate-basic
  (t/is (string? (glob/translate "*")))
  (t/is (string? (glob/translate "*.txt")))
  (t/is (string? (glob/translate "?")))
  (t/is (string? (glob/translate "**" :recursive true))))

(t/deftest test-escape-no-special
  ;; A plain string with no glob metacharacters is unchanged
  (t/is (= "hello" (glob/escape "hello")))
  (t/is (= "foo.bar" (glob/escape "foo.bar")))
  (t/is (= "" (glob/escape ""))))

(t/deftest test-translate-question-mark
  ;; ? matches exactly one non-separator character
  (let [re (re-pattern (glob/translate "a?c" :seps "/"))]
    (t/is (re-matches re "abc"))
    (t/is (re-matches re "axc"))
    (t/is (nil? (re-matches re "ac")))
    (t/is (nil? (re-matches re "abbc")))))

(t/deftest test-glob-single-file-match
  (let [tmpdir (java.io.File/createTempFile "glob-single" "")
        _ (.delete tmpdir)
        _ (.mkdir tmpdir)
        f (java.io.File. tmpdir "unique.txt")]
    (.createNewFile f)
    (try
      (let [result (glob/glob "unique.txt" {:root-dir (.getAbsolutePath tmpdir)})]
        (t/is (= 1 (count result)))
        (t/is (str/ends-with? (first result) "unique.txt")))
      (finally (.delete f) (.delete tmpdir)))))

(t/deftest test-translate-literal-string
  ;; A literal (no wildcards) pattern matches only the exact string
  (let [re (re-pattern (glob/translate "hello.txt" :seps "/"))]
    (t/is (re-matches re "hello.txt"))
    (t/is (nil? (re-matches re "hello_txt")))
    (t/is (nil? (re-matches re "hello.txt.bak")))))

(t/deftest test-escape-bytes-no-special
  ;; escape on bytes with no special chars returns bytes unchanged
  (let [plain "hello"]
    (t/is (= (seq (.getBytes plain "UTF-8"))
             (seq (glob/escape (.getBytes plain "UTF-8")))))))
