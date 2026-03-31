;; Original: Lib/test/test_shlex.py

(ns conao3.battery.shlex-test
  (:require
   [clojure.test :as t]
   [conao3.battery.shlex :as shlex])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-split-none
  (t/is (thrown? ExceptionInfo (shlex/split nil))))

(t/deftest test-split-posix
  (t/are [s expected]
         (= (shlex/split s {:posix true :comments true}) expected)
    "x"                        ["x"]
    "foo bar"                  ["foo" "bar"]
    " foo bar"                 ["foo" "bar"]
    "foo   bar    bla     fasel" ["foo" "bar" "bla" "fasel"]
    "\"foo\" \"bar\" \"bla\"" ["foo" "bar" "bla"]
    "\"foo\" bar bla"          ["foo" "bar" "bla"]
    "foo 'bar' bla"            ["foo" "bar" "bla"]
    "\"\""                     [""]
    "''"                       [""]
    "foo \"\" bar"             ["foo" "" "bar"]
    "\\x bar"                  ["x" "bar"]
    "foo#bar\nbaz"             ["foo" "baz"]))

(t/deftest test-compat
  (t/are [s expected]
         (= (shlex/split s {:posix false}) expected)
    "x"                        ["x"]
    "foo bar"                  ["foo" "bar"]
    "\\x bar"                  ["\\" "x" "bar"]
    "foo \"bar\" bla"          ["foo" "\"bar\"" "bla"]
    "'foo' 'bar' 'bla'"        ["'foo'" "'bar'" "'bla'"]
    "\"\""                     ["\"\""]
    "''"                       ["''"]
    ":-) ;-)"                  [":" "-" ")" ";" "-" ")"]))

(t/deftest test-syntax-split-ampersand-and-pipe
  (t/are [ss expected]
         (= (shlex/tokenize (shlex/make-shlex ss {:punctuation-chars true})) expected)
    "echo hi && echo bye" ["echo" "hi" "&&" "echo" "bye"]
    "echo hi || echo bye" ["echo" "hi" "||" "echo" "bye"]
    "echo hi & echo bye"  ["echo" "hi" "&" "echo" "bye"]
    "echo hi | echo bye"  ["echo" "hi" "|" "echo" "bye"]))

(t/deftest test-syntax-split-semicolon
  (t/are [ss expected]
         (= (shlex/tokenize (shlex/make-shlex ss {:punctuation-chars true})) expected)
    "echo hi ; echo bye"  ["echo" "hi" ";" "echo" "bye"]
    "echo hi ;; echo bye" ["echo" "hi" ";;" "echo" "bye"]
    "echo hi ;& echo bye" ["echo" "hi" ";&" "echo" "bye"]))

(t/deftest test-syntax-split-redirect
  (t/are [ss expected]
         (= (shlex/tokenize (shlex/make-shlex ss {:punctuation-chars true})) expected)
    "echo hi < out"  ["echo" "hi" "<" "out"]
    "echo hi > out"  ["echo" "hi" ">" "out"]
    "echo hi | out"  ["echo" "hi" "|" "out"]))

(t/deftest test-syntax-split-paren
  (t/are [ss expected]
         (= (shlex/tokenize (shlex/make-shlex ss {:punctuation-chars true})) expected)
    "( echo hi )" ["(" "echo" "hi" ")"]
    "(echo hi)"   ["(" "echo" "hi" ")"]))

(t/deftest test-syntax-split-custom
  (let [ss "~/a&&b-c --color=auto||d *.py?"
        s1 (shlex/make-shlex ss {:punctuation-chars "|"})
        s2 (assoc s1 :whitespace-split true)]
    (t/is (= (shlex/tokenize s1)
             ["~/a" "&" "&" "b-c" "--color=auto" "||" "d" "*.py?"]))
    (t/is (= (shlex/tokenize s2)
             ["~/a&&b-c" "--color=auto" "||" "d" "*.py?"]))))

(t/deftest test-token-types
  (let [s (shlex/make-shlex "a && b || c" {:punctuation-chars true})
        tokens (shlex/tokenize s)
        punctuation-chars (get-in s [:opts :punctuation-chars])]
    (t/is (= tokens ["a" "&&" "b" "||" "c"]))))

(t/deftest test-punctuation-in-word-chars
  (let [s (shlex/make-shlex "a_b__c" {:punctuation-chars "_"})]
    (t/is (= (shlex/tokenize s) ["a" "_" "b" "__" "c"]))))

(t/deftest test-punctuation-with-whitespace-split
  (let [s1 (shlex/make-shlex "a  && b  ||  c" {:punctuation-chars "&"})
        s2 (assoc s1 :whitespace-split true)]
    (t/is (= (shlex/tokenize s1) ["a" "&&" "b" "|" "|" "c"]))
    (t/is (= (shlex/tokenize s2) ["a" "&&" "b" "||" "c"]))))

(t/deftest test-punctuation-with-posix
  (let [s1 (shlex/make-shlex "f >\"abc\"" {:posix true :punctuation-chars true})
        s2 (shlex/make-shlex "f >\\\"abc\\\"" {:posix true :punctuation-chars true})]
    (t/is (= (shlex/tokenize s1) ["f" ">" "abc"]))
    (t/is (= (shlex/tokenize s2) ["f" ">" "\"abc\""]))))

(t/deftest test-empty-string-handling
  (let [s1 (shlex/make-shlex "'')abc" {:posix true :punctuation-chars false})
        s2 (shlex/make-shlex "'')abc" {:posix true :punctuation-chars true})
        s3 (shlex/make-shlex "'')abc" {:punctuation-chars true})]
    (t/is (= (shlex/tokenize s1) ["" ")" "abc"]))
    (t/is (= (shlex/tokenize s2) ["" ")" "abc"]))
    (t/is (= (shlex/tokenize s3) ["''" ")" "abc"]))))

(t/deftest test-unicode-handling
  (let [ss "\u2119\u01b4\u2602\u210c\u00f8\u1f24"
        s1 (assoc (shlex/make-shlex ss {:punctuation-chars true}) :whitespace-split true)
        s2 (shlex/make-shlex ss {:punctuation-chars true})]
    (t/is (= (shlex/tokenize s1) [ss]))
    (t/is (= (shlex/tokenize s2) ["\u2119" "\u01b4" "\u2602" "\u210c" "\u00f8" "\u1f24"]))))

(t/deftest test-quote
  (t/is (= (shlex/quote "") "''"))
  (t/is (= (shlex/quote "test file name") "'test file name'"))
  (t/is (= (shlex/quote "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@%_-+=:,./")
           "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@%_-+=:,./"))
  (t/is (thrown? ExceptionInfo (shlex/quote 42)))
  (t/is (thrown? ExceptionInfo (shlex/quote (.getBytes "abc" "UTF-8")))))

(t/deftest test-join
  (t/are [tokens expected]
         (= (shlex/join tokens) expected)
    ["a " "b"]    "'a ' b"
    ["a" " b"]    "a ' b'"
    ["a" " " "b"] "a ' ' b"
    ["\"a" "b\""] "'\"a' 'b\"'"))

(t/deftest test-join-roundtrip
  (t/are [tokens]
         (= tokens (shlex/split (shlex/join tokens) {:posix true}))
    ["foo" "bar"]
    ["foo bar" "baz"]
    ["" "nonempty"]
    ["with spaces" "and" "quotes"]))

(t/deftest test-punctuation-chars-read-only
  (let [s (shlex/make-shlex "" {:punctuation-chars "/|$%^"})]
    (t/is (= (get-in s [:opts :punctuation-chars]) "/|$%^"))))

(t/deftest test-split-empty
  (t/is (= [] (shlex/split "")))
  (t/is (= [] (shlex/split "   "))))

(t/deftest test-split-single-token
  (t/is (= ["hello"] (shlex/split "hello")))
  (t/is (= ["hello world"] (shlex/split "'hello world'"))))

(t/deftest test-split-escaped-space
  (t/is (= ["hello world"] (shlex/split "hello\\ world" {:posix true}))))

(t/deftest test-quote-special-chars
  (t/is (= "'foo bar'" (shlex/quote "foo bar")))
  (t/is (= "'it'\\''s'" (shlex/quote "it's"))))
