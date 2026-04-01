;; Original: Lib/test/test_re.py

(ns conao3.battery.re-test
  (:require
   [clojure.test :as t]
   [conao3.battery.re :as re-m]))

(t/deftest test-match-basic
  (let [m (re-m/match "hello" "hello world")]
    (t/is (some? m))
    (t/is (= "hello" (re-m/group m)))
    (t/is (= 0 (re-m/start m)))
    (t/is (= 5 (re-m/end m)))
    (t/is (= [0 5] (re-m/span m)))))

(t/deftest test-match-nil-when-no-match
  (t/is (nil? (re-m/match "world" "hello world"))))

(t/deftest test-match-groups
  (let [m (re-m/match "(\\d+)-(\\d+)" "2024-01")]
    (t/is (= "2024-01" (re-m/group m 0)))
    (t/is (= "2024" (re-m/group m 1)))
    (t/is (= "01" (re-m/group m 2)))
    (t/is (= ["2024" "01"] (re-m/groups m)))))

(t/deftest test-search-basic
  (let [m (re-m/search "\\d+" "abc123def")]
    (t/is (some? m))
    (t/is (= "123" (re-m/group m)))
    (t/is (= 3 (re-m/start m)))
    (t/is (= 6 (re-m/end m)))))

(t/deftest test-search-nil-when-no-match
  (t/is (nil? (re-m/search "\\d+" "abcdef"))))

(t/deftest test-fullmatch-basic
  (t/is (some? (re-m/fullmatch "\\d+" "12345")))
  (t/is (nil? (re-m/fullmatch "\\d+" "12345abc"))))

(t/deftest test-findall-basic
  (t/is (= ["123" "456"] (re-m/findall "\\d+" "abc123def456")))
  (t/is (= [] (re-m/findall "\\d+" "abcdef"))))

(t/deftest test-findall-with-groups
  (t/is (= [["2024" "01"] ["2023" "12"]] (re-m/findall "(\\d{4})-(\\d{2})" "2024-01 and 2023-12")))
  (t/is (= ["hello" "world"] (re-m/findall "(\\w+)" "hello world"))))

(t/deftest test-finditer-basic
  (let [results (re-m/finditer "\\d+" "abc123def456")]
    (t/is (= 2 (clojure.core/count results)))
    (t/is (= "123" (re-m/group (first results))))
    (t/is (= "456" (re-m/group (second results))))))

(t/deftest test-sub-basic
  (t/is (= "abcNUMdefNUM" (re-m/sub "\\d+" "NUM" "abc123def456")))
  (t/is (= "abcNUMdef456" (re-m/sub "\\d+" "NUM" "abc123def456" 1))))

(t/deftest test-sub-with-function
  (t/is (= "abc246def912" (re-m/sub "\\d+" #(str (* 2 (Integer/parseInt (re-m/group %)))) "abc123def456"))))

(t/deftest test-subn-basic
  (let [[result n] (re-m/subn "\\d+" "NUM" "abc123def456")]
    (t/is (= "abcNUMdefNUM" result))
    (t/is (= 2 n))))

(t/deftest test-split-basic
  (t/is (= ["hello" "world" "foo"] (re-m/split "\\s+" "hello world  foo")))
  (t/is (= ["hello" "world  foo"] (re-m/split "\\s+" "hello world  foo" 1))))

(t/deftest test-split-with-groups
  (t/is (= ["one" "+" "two" "-" "three"] (re-m/split "([+-])" "one+two-three"))))

(t/deftest test-ignorecase
  (let [m (re-m/match "hello" "HELLO" re-m/IGNORECASE)]
    (t/is (some? m))
    (t/is (= "HELLO" (re-m/group m)))))

(t/deftest test-multiline
  (let [results (re-m/findall "^\\w+" "line1\nline2\nline3" re-m/MULTILINE)]
    (t/is (= ["line1" "line2" "line3"] results))))

(t/deftest test-named-groups
  (let [m (re-m/match "(?P<year>\\d{4})-(?P<month>\\d{2})" "2024-01")]
    (t/is (= "2024" (re-m/group m "year")))
    (t/is (= "01" (re-m/group m "month")))
    (t/is (= {"year" "2024" "month" "01"} (re-m/groupdict m)))))

(t/deftest test-compile-basic
  (let [cp (re-m/compile "\\d+")]
    (let [m (re-m/compiled-match cp "123abc")]
      (t/is (= "123" (re-m/group m))))
    (t/is (= ["123" "456"] (re-m/compiled-findall cp "abc123def456")))))

(t/deftest test-escape
  (let [escaped (re-m/escape "hello.world")]
    (t/is (some? (re-m/match escaped "hello.world")))
    (t/is (nil? (re-m/match escaped "helloxworld")))))

(t/deftest test-match-string
  (let [m (re-m/match "\\d+" "123abc")]
    (t/is (= "123abc" (re-m/string m)))))

(t/deftest test-compiled-search
  (let [cp (re-m/compile "\\d+")
        m  (re-m/compiled-search cp "abc123def")]
    (t/is (= "123" (re-m/group m)))))

(t/deftest test-compiled-sub
  (let [cp (re-m/compile "\\d+")]
    (t/is (= "abcNUMdefNUM" (re-m/compiled-sub cp "NUM" "abc123def456")))))

(t/deftest test-compiled-split
  (let [cp (re-m/compile "\\s+")]
    (t/is (= ["hello" "world" "foo"] (re-m/compiled-split cp "hello world  foo")))))

(t/deftest test-dotall
  (let [m (re-m/search "a.b" "a\nb" re-m/DOTALL)]
    (t/is (some? m))))

(t/deftest test-flag-aliases
  (t/is (= re-m/IGNORECASE re-m/I))
  (t/is (= re-m/MULTILINE re-m/M))
  (t/is (= re-m/DOTALL re-m/S))
  (t/is (= re-m/VERBOSE re-m/X)))

(t/deftest test-verbose-flag
  (let [m (re-m/match "\\d +" "123" re-m/VERBOSE)]
    (t/is (some? m))))

(t/deftest test-sub-count-limit
  (t/is (= "NUMbc123def456" (re-m/sub "\\d+" "NUM" "123bc123def456" 1)))
  (t/is (= "NUMbcNUMdefNUM" (re-m/sub "\\d+" "NUM" "123bc123def456"))))

(t/deftest test-finditer-positions
  (let [results (re-m/finditer "\\d+" "a1b22c333")]
    (t/is (= [1 2] (re-m/span (nth results 0))))
    (t/is (= [3 5] (re-m/span (nth results 1))))
    (t/is (= [6 9] (re-m/span (nth results 2))))))

(t/deftest test-compiled-fullmatch
  (let [cp (re-m/compile "\\d+")]
    (t/is (some? (re-m/compiled-fullmatch cp "123")))
    (t/is (nil? (re-m/compiled-fullmatch cp "123abc")))))

(t/deftest test-compiled-finditer
  (let [cp (re-m/compile "\\d+")
        results (re-m/compiled-finditer cp "a1b22c333")]
    (t/is (= 3 (count results)))
    (t/is (= "1" (re-m/group (nth results 0))))))

(t/deftest test-compiled-subn
  (let [cp (re-m/compile "\\d+")
        [result count] (re-m/compiled-subn cp "NUM" "abc123def456")]
    (t/is (= "abcNUMdefNUM" result))
    (t/is (= 2 count))))

(t/deftest test-pattern-and-flags
  (let [cp (re-m/compile "\\d+")]
    (t/is (= "\\d+" (re-m/pattern cp)))
    (t/is (integer? (re-m/re-flags cp)))))

(t/deftest test-groupdict
  (let [m (re-m/search "(?P<year>\\d{4})-(?P<month>\\d{2})" "2024-01")]
    (t/is (= {"year" "2024" "month" "01"} (re-m/groupdict m)))
    (t/is (= "2024" (get (re-m/groupdict m) "year")))
    (t/is (= "01" (get (re-m/groupdict m) "month")))))

(t/deftest test-span-and-start-end
  (let [m (re-m/search "\\d+" "abc123def")]
    (t/is (= 3 (re-m/start m)))
    (t/is (= 6 (re-m/end m)))
    (t/is (= [3 6] (re-m/span m)))))

(t/deftest test-lookahead
  ;; positive lookahead
  (t/is (= ["foo"] (re-m/findall "foo(?=bar)" "foobar foobaz")))
  ;; negative lookahead
  (t/is (= ["foo"] (re-m/findall "foo(?!bar)" "foobaz foobar"))))

(t/deftest test-lookbehind
  ;; positive lookbehind
  (t/is (= ["bar"] (re-m/findall "(?<=foo)bar" "foobar")))
  ;; negative lookbehind
  (t/is (= ["bar"] (re-m/findall "(?<!foo)bar" "bazbar"))))

(t/deftest test-re-split-limits
  (t/is (= ["a" "b" "c,d"] (re-m/split "," "a,b,c,d" 2)))
  (t/is (= ["a" "b" "c" "d"] (re-m/split "," "a,b,c,d"))))
