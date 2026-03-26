;; Original: Lib/test/test_fnmatch.py

(ns conao3.battery.fnmatch-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.fnmatch :as fnmatch])
  (:import
   [clojure.lang ExceptionInfo]))

(def windows?
  (str/includes? (str/lower-case (System/getProperty "os.name")) "win"))

(defn- normalize-case [value]
  (if windows?
    (str/lower-case value)
    value))

(defn- normalize-path [value]
  (if windows?
    (str/replace value "/" "\\")
    value))

(def ignorecase (= (normalize-case "P") (normalize-case "p")))
(def normsep (= (normalize-path "\\") (normalize-path "/")))

(def test-chars (str "abcdefghijklmnopqrstuvwxyz" "0123456789" "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"))

(defn- str->bytes [s]
  (.getBytes ^String s "UTF-8"))

(defn- byte-vector [value]
  (vec value))

(defn- check-match
  ([filename pattern]
   (check-match filename pattern true fnmatch/fnmatch))
  ([filename pattern should-match]
   (check-match filename pattern should-match fnmatch/fnmatch))
  ([filename pattern should-match matcher]
   (if should-match
     (t/is (matcher filename pattern))
     (t/is (not (matcher filename pattern))))))

(t/deftest ^:kaocha/skip test-fnmatch
  (check-match "abc" "abc")
  (check-match "abc" "?*?")
  (check-match "abc" "???*")
  (check-match "abc" "*???")
  (check-match "abc" "???")
  (check-match "abc" "*")
  (check-match "abc" "ab[cd]")
  (check-match "abc" "ab[!de]")
  (check-match "abc" "ab[de]" false)
  (check-match "a" "??" false)
  (check-match "a" "b" false)
  (check-match "\\" "[\\]")
  (check-match "a" "[!\\]" )
  (check-match "\\" "[!\\]" false)
  (check-match "foo\nbar" "foo*")
  (check-match "foo\nbar\n" "foo*")
  (check-match "\nfoo" "foo*" false)
  (check-match "\n" "*"))

(t/deftest ^:kaocha/skip test-slow-fnmatch
  (check-match (apply str (repeat 50 "a")) "*a*a*a*a*a*a*a*a*a*a")
  (check-match (str (apply str (repeat 50 "a")) "b")
               "*a*a*a*a*a*a*a*a*a*a" false))

(t/deftest ^:kaocha/skip test-mix-bytes-str
  (t/is (thrown? ExceptionInfo (fnmatch/fnmatch "test" (str->bytes "*"))))
  (t/is (thrown? ExceptionInfo (fnmatch/fnmatch (str->bytes "test") "*")))
  (t/is (thrown? ExceptionInfo (fnmatch/fnmatchcase "test" (str->bytes "*"))))
  (t/is (thrown? ExceptionInfo (fnmatch/fnmatchcase (str->bytes "test") "*"))))

(t/deftest ^:kaocha/skip test-fnmatchcase
  (check-match "abc" "abc" true fnmatch/fnmatchcase)
  (check-match "AbC" "abc" false fnmatch/fnmatchcase)
  (check-match "abc" "AbC" false fnmatch/fnmatchcase)
  (check-match "AbC" "AbC" true fnmatch/fnmatchcase)
  (check-match "usr/bin" "usr/bin" true fnmatch/fnmatchcase)
  (check-match "usr\\bin" "usr/bin" false fnmatch/fnmatchcase)
  (check-match "usr/bin" "usr\\bin" false fnmatch/fnmatchcase)
  (check-match "usr\\bin" "usr\\bin" true fnmatch/fnmatchcase))

(t/deftest ^:kaocha/skip test-bytes
  (check-match (str->bytes "test") (str->bytes "te*"))
  (check-match (.getBytes (str "test" (char 0xff)) "ISO-8859-1")
               (.getBytes (str "te*" (char 0xff)) "ISO-8859-1"))
  (check-match (str->bytes "foo\nbar") (str->bytes "foo*")))

(t/deftest ^:kaocha/skip test-case
  (check-match "abc" "abc")
  (check-match "AbC" "abc" ignorecase)
  (check-match "abc" "AbC" ignorecase)
  (check-match "AbC" "AbC"))

(t/deftest ^:kaocha/skip test-sep
  (check-match "usr/bin" "usr/bin")
  (check-match "usr\\bin" "usr/bin" normsep)
  (check-match "usr/bin" "usr\\bin" normsep)
  (check-match "usr\\bin" "usr\\bin"))

(t/deftest ^:kaocha/skip test-char-set
  (let [upper-chars (apply str (filter #(Character/isUpperCase ^char %) test-chars))]
    (doseq [c (seq test-chars)]
      (check-match (str c) "[az]" (str/includes? "az" (str c)))
      (check-match (str c) "[!az]" (not (str/includes? "az" (str c)))))
    (doseq [c (seq test-chars)]
      (check-match (str c) "[AZ]" (and (str/includes? "az" (str c)) ignorecase))
      (check-match (str c) "[!AZ]" (or (not (str/includes? "az" (str c))) (not ignorecase))))
    (doseq [c (seq upper-chars)]
      (check-match (str c) "[az]" (and (str/includes? "AZ" (str c)) ignorecase))
      (check-match (str c) "[!az]" (or (not (str/includes? "AZ" (str c))) (not ignorecase))))
    (doseq [c (seq test-chars)]
      (check-match (str c) "[aa]" (= "a" (str c))))
    (doseq [c (seq test-chars)]
      (check-match (str c) "[^az]" (str/includes? "^az" (str c)))
      (check-match (str c) "[[az]" (str/includes? "[az" (str c)))
      (check-match (str c) "[!]]" (not= "]" (str c))))
    (check-match "[" "[")
    (check-match "[]" "[]")
    (check-match "[!" "[!")
    (check-match "[!]" "[!]")))

(t/deftest ^:kaocha/skip test-range
  (let [upper-chars (apply str (filter #(Character/isUpperCase ^char %) test-chars))]
    (doseq [c (seq test-chars)]
      (check-match (str c) "[b-d]" (str/includes? "bcd" (str c)))
      (check-match (str c) "[!b-d]" (not (str/includes? "bcd" (str c))))
      (check-match (str c) "[b-dx-z]" (str/includes? "bcdxyz" (str c)))
      (check-match (str c) "[!b-dx-z]" (not (str/includes? "bcdxyz" (str c)))))
    (doseq [c (seq test-chars)]
      (check-match (str c) "[B-D]" (and (str/includes? "bcd" (str c)) ignorecase))
      (check-match (str c) "[!B-D]" (or (not (str/includes? "bcd" (str c))) (not ignorecase))))
    (doseq [c (seq upper-chars)]
      (check-match (str c) "[b-d]" (and (str/includes? "BCD" (str c)) ignorecase))
      (check-match (str c) "[!b-d]" (or (not (str/includes? "BCD" (str c))) (not ignorecase))))
    (doseq [c (seq test-chars)]
      (check-match (str c) "[b-b]" (= "b" (str c)))
      (check-match (str c) "[!-#]" (not (str/includes? "-#" (str c))))
      (check-match (str c) "[!--.]" (not (str/includes? "-." (str c))))
      (check-match (str c) "[^-`]" (str/includes? "^_`" (str c)))
      (when-not (and normsep (= (str c) "/"))
        (check-match (str c) "[[-^]" (str/includes? "[\\]^" (str c)))
        (check-match (str c) "[\\-^]" (str/includes? "\\]^" (str c))))
      (check-match (str c) "[b-]" (str/includes? "-b" (str c)))
      (check-match (str c) "[!b-]" (not (str/includes? "-b" (str c))))
      (check-match (str c) "[-b]" (str/includes? "-b" (str c)))
      (check-match (str c) "[!-b]" (not (str/includes? "-b" (str c))))
      (check-match (str c) "[-]" (= "-" (str c)))
      (check-match (str c) "[!-]" (not= "-" (str c))))
    (doseq [c (seq test-chars)]
      (check-match (str c) "[d-b]" false)
      (check-match (str c) "[!d-b]" true)
      (check-match (str c) "[d-bx-z]" (str/includes? "xyz" (str c)))
      (check-match (str c) "[!d-bx-z]" (not (str/includes? "xyz" (str c))))
      (check-match (str c) "[d-b^-`]" (str/includes? "^_`" (str c)))
      (when-not (and normsep (= (str c) "/"))
        (check-match (str c) "[d-b[-^]" (str/includes? "[\\]^" (str c)))))))

(t/deftest ^:kaocha/skip test-sep-in-char-set
  (check-match "/" "[/]")
  (check-match "\\" "[\\]")
  (check-match "/" "[\\]" normsep)
  (check-match "\\" "[/]" normsep)
  (check-match "[/]" "[/]" false)
  (check-match "[\\\\]" "[/]" false)
  (check-match "\\" "[\\t]")
  (check-match "/" "[\\t]" normsep)
  (check-match "t" "[\\t]")
  (check-match "\t" "[\\t]" false))

(t/deftest ^:kaocha/skip test-sep-in-range
  (check-match "a/b" "a[.-0]b" (not normsep))
  (check-match "a\\b" "a[.-0]b" false)
  (check-match "a\\b" "a[Z-^]b" (not normsep))
  (check-match "a/b" "a[Z-^]b" false)
  (check-match "a/b" "a[/-0]b" (not normsep))
  (check-match "a\\b" "a[/-0]b" false)
  (check-match "a[/-0]b" "a[/-0]b" false)
  (check-match "a[\\-0]b" "a[/-0]b" false)
  (check-match "a/b" "a[.-/]b")
  (check-match "a\\b" "a[.-/]b" normsep)
  (check-match "a[.-/]b" "a[.-/]b" false)
  (check-match "a[.-\\]b" "a[.-/]b" false)
  (check-match "a\\b" "a[\\-^]b")
  (check-match "a/b" "a[\\-^]b" normsep)
  (check-match "a[\\-^]b" "a[\\-^]b" false)
  (check-match "a[/-^]b" "a[\\-^]b" false)
  (check-match "a\\b" "a[Z-\\]b" (not normsep))
  (check-match "a/b" "a[Z-\\]b" false)
  (check-match "a[Z-\\]b" "a[Z-\\]b" false)
  (check-match "a[Z-/]b" "a[Z-\\]b" false))

(t/deftest ^:kaocha/skip test-warnings
  (check-match "[" "[[]")
  (check-match "&" "[a&&b]")
  (check-match "|" "[a||b]")
  (check-match "~" "[a~~b]")
  (check-match "," "[a-z+--A-Z]")
  (check-match "." "[a-z--/A-Z]"))

(t/deftest ^:kaocha/skip test-translate
  (t/is (= "(?s:.*)\\z" (fnmatch/translate "*")))
  (t/is (= "(?s:.)\\z" (fnmatch/translate "?")))
  (t/is (= "(?s:a.b.*)\\z" (fnmatch/translate "a?b*")))
  (t/is (= "(?s:[abc])\\z" (fnmatch/translate "[abc]")))
  (t/is (= "(?s:[]])\\z" (fnmatch/translate "[]]")))
  (t/is (= "(?s:[^x])\\z" (fnmatch/translate "[!x]")))
  (t/is (= "(?s:[\\^x])\\z" (fnmatch/translate "[^x]")))
  (t/is (= "(?s:\\[x)\\z" (fnmatch/translate "[x")))
  (t/is (= "(?s:.*\\.txt)\\z" (fnmatch/translate "*.txt")))
  (t/is (= "(?s:.*)\\z" (fnmatch/translate "*********")))
  (t/is (= "(?s:A.*)\\z" (fnmatch/translate "A*********")))
  (t/is (= "(?s:.*A)\\z" (fnmatch/translate "*********A")))
  (t/is (= "(?s:A.*.[?].)\\z" (fnmatch/translate "A*********?[?]?")))
  (t/is (= "(?s:(?>.*?a)(?>.*?a).*a)\\z" (fnmatch/translate "**a*a****a")))
  (let [r1 (fnmatch/translate "**a**a**a*")
        r2 (fnmatch/translate "**b**b**b*")
        r3 (fnmatch/translate "*c*c*c*")
        fatre (str/join "|" [r1 r2 r3])]
    (t/is (re-matches (re-pattern fatre) "abaccad"))
    (t/is (re-matches (re-pattern fatre) "abxbcab"))
    (t/is (re-matches (re-pattern fatre) "cbabcaxc"))
    (t/is (not (re-matches (re-pattern fatre) "dabccbad")))))

(t/deftest ^:kaocha/skip test-translate-wildcards
  (t/are [pattern expect] (= expect (fnmatch/translate pattern))
    "ab*" "(?s:ab.*)\\z"
    "ab*cd" "(?s:ab.*cd)\\z"
    "ab*cd*" "(?s:ab(?>.*?cd).*)\\z"
    "ab*cd*12" "(?s:ab(?>.*?cd).*12)\\z"
    "ab*cd*12*" "(?s:ab(?>.*?cd)(?>.*?12).*)\\z"
    "ab*cd*12*34" "(?s:ab(?>.*?cd)(?>.*?12).*34)\\z"
    "ab*cd*12*34*" "(?s:ab(?>.*?cd)(?>.*?12)(?>.*?34).*)\\z"
    "*ab" "(?s:.*ab)\\z"
    "*ab*" "(?s:(?>.*?ab).*)\\z"
    "*ab*cd" "(?s:(?>.*?ab).*cd)\\z"
    "*ab*cd*" "(?s:(?>.*?ab)(?>.*?cd).*)\\z"
    "*ab*cd*12" "(?s:(?>.*?ab)(?>.*?cd).*12)\\z"
    "*ab*cd*12*" "(?s:(?>.*?ab)(?>.*?cd)(?>.*?12).*)\\z"
    "*ab*cd*12*34" "(?s:(?>.*?ab)(?>.*?cd)(?>.*?12).*34)\\z"
    "*ab*cd*12*34*" "(?s:(?>.*?ab)(?>.*?cd)(?>.*?12)(?>.*?34).*)\\z"))

(t/deftest ^:kaocha/skip test-translate-expressions
  (t/are [pattern expect] (= expect (fnmatch/translate pattern))
    "[" "(?s:\\[)\\z"
    "[!" "(?s:\\[!)\\z"
    "[]" "(?s:\\[\\])\\z"
    "[abc" "(?s:\\[abc)\\z"
    "[!abc" "(?s:\\[!abc)\\z"
    "[abc]" "(?s:[abc])\\z"
    "[!abc]" "(?s:[^abc])\\z"
    "[!abc][!def]" "(?s:[^abc][^def])\\z"
    "[[" "(?s:\\[\\[)\\z"
    "[[a" "(?s:\\[\\[a)\\z"
    "[[]" "(?s:[\\[])\\z"
    "[[]a" "(?s:[\\[]a)\\z"
    "[[]]" "(?s:[\\[]\\])\\z"
    "[[]a]" "(?s:[\\[]a\\])\\z"
    "[[a]" "(?s:[\\[a])\\z"
    "[[a]]" "(?s:[\\[a]\\])\\z"
    "[[a]b" "(?s:[\\[a]b)\\z"
    "[\\\\a" "(?s:\\[\\\\a)\\z"
    "[\\]" "(?s:[\\\\])\\z"
    "[\\\\]" "(?s:[\\\\\\\\])\\z"))

(t/deftest ^:kaocha/skip test-star-indices-locations
  (let [blocks ["a^b" "***" "?" "?" "[a-z]" "[1-9]" "*" "++" "[[a"]
        [parts star-indices] (fnmatch/_translate (str/join blocks) "*" ".")]
    (t/is (= ["a" "\\^" "b" "*"
            "." "."
            "[a-z]" "[1-9]" "*"
            "\\+" "\\+" "\\[" "\\[" "a"] parts))
    (t/is (= [3 8] star-indices))))

(t/deftest ^:kaocha/skip test-filter
  (t/is (= ["Python" "Perl"] (fnmatch/filter ["Python" "Ruby" "Perl" "Tcl"] "P*")))
  (t/is (= [(byte-vector (str->bytes "Python")) (byte-vector (str->bytes "Perl"))]
         (mapv byte-vector (fnmatch/filter [(str->bytes "Python") (str->bytes "Ruby") (str->bytes "Perl") (str->bytes "Tcl")] (str->bytes "P*"))))))

(t/deftest ^:kaocha/skip test-filter-mix-bytes-str
  (t/is (thrown? ExceptionInfo (fnmatch/filter ["test"] (str->bytes "*"))))
  (t/is (thrown? ExceptionInfo (fnmatch/filter [(str->bytes "test")] "*"))))

(t/deftest ^:kaocha/skip test-filter-case
  (t/is (= (if ignorecase ["Test.py" "Test.PL"] ["Test.py"])
         (fnmatch/filter ["Test.py" "Test.rb" "Test.PL"] "*.p*")))
  (t/is (= (if ignorecase ["Test.py" "Test.PL"] ["Test.PL"])
         (fnmatch/filter ["Test.py" "Test.rb" "Test.PL"] "*.P*"))))

(t/deftest ^:kaocha/skip test-filter-sep
  (t/is (= (if normsep ["usr/bin" "usr\\lib"] ["usr/bin"])
         (fnmatch/filter ["usr/bin" "usr" "usr\\lib"] "usr/*")))
  (t/is (= (if normsep ["usr/bin" "usr\\lib"] ["usr\\lib"])
         (fnmatch/filter ["usr/bin" "usr" "usr\\lib"] "usr\\*"))))

(t/deftest ^:kaocha/skip test-filterfalse
  (t/is (= ["Ruby" "Tcl"] (fnmatch/filterfalse ["Python" "Ruby" "Perl" "Tcl"] "P*")))
  (t/is (= [(byte-vector (str->bytes "Ruby")) (byte-vector (str->bytes "Tcl"))]
         (mapv byte-vector (fnmatch/filterfalse [(str->bytes "Python") (str->bytes "Ruby") (str->bytes "Perl") (str->bytes "Tcl")] (str->bytes "P*"))))))

(t/deftest ^:kaocha/skip test-filterfalse-mix-bytes-str
  (t/is (thrown? ExceptionInfo (fnmatch/filterfalse ["test"] (str->bytes "*"))))
  (t/is (thrown? ExceptionInfo (fnmatch/filterfalse [(str->bytes "test")] "*"))))

(t/deftest ^:kaocha/skip test-filterfalse-case
  (t/is (= (if ignorecase ["Test.rb"] ["Test.rb" "Test.PL"])
         (fnmatch/filterfalse ["Test.py" "Test.rb" "Test.PL"] "*.p*")))
  (t/is (= (if ignorecase ["Test.rb"] ["Test.py" "Test.rb"])
         (fnmatch/filterfalse ["Test.py" "Test.rb" "Test.PL"] "*.P*"))))

(t/deftest ^:kaocha/skip test-filterfalse-sep
  (t/is (= (if normsep ["usr"] ["usr" "usr\\lib"])
         (fnmatch/filterfalse ["usr/bin" "usr" "usr\\lib"] "usr/*")))
  (t/is (= (if normsep ["usr"] ["usr/bin" "usr"])
         (fnmatch/filterfalse ["usr/bin" "usr" "usr\\lib"] "usr\\*"))))
