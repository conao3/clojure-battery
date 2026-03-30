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
