;; Original: Lib/test/test_urlparse.py

(ns conao3.battery.urllib-parse-test
  (:require
   [clojure.test :as t]
   [conao3.battery.urllib-parse :as up-m]))

(t/deftest test-urlparse-basic
  (let [r (up-m/urlparse "https://example.com/path?a=1#section")]
    (t/is (= "https" (:scheme r)))
    (t/is (= "example.com" (:netloc r)))
    (t/is (= "/path" (:path r)))
    (t/is (= "a=1" (:query r)))
    (t/is (= "section" (:fragment r)))))

(t/deftest test-urlparse-with-port
  (let [r (up-m/urlparse "https://example.com:8080/path")]
    (t/is (= "example.com" (:hostname r)))
    (t/is (= 8080 (:port r)))))

(t/deftest test-urlparse-full
  (let [r (up-m/urlparse "https://example.com:8080/path/to/resource?a=1&b=2#section")]
    (t/is (= "https" (:scheme r)))
    (t/is (= "example.com:8080" (:netloc r)))
    (t/is (= "/path/to/resource" (:path r)))
    (t/is (= "a=1&b=2" (:query r)))
    (t/is (= "section" (:fragment r)))))

(t/deftest test-urlunparse
  (t/is (= "https://example.com/path?a=1#section"
           (up-m/urlunparse ["https" "example.com" "/path" "" "a=1" "section"]))))

(t/deftest test-quote
  (t/is (= "hello%20world" (up-m/quote "hello world")))
  (t/is (= "hello%20world/path" (up-m/quote "hello world/path")))
  (t/is (= "hello%20world%2Fpath" (up-m/quote "hello world/path" ""))))

(t/deftest test-unquote
  (t/is (= "hello world" (up-m/unquote "hello%20world")))
  (t/is (= "hello world/path" (up-m/unquote "hello%20world%2Fpath"))))

(t/deftest test-quote-plus
  (t/is (= "hello+world" (up-m/quote-plus "hello world")))
  (t/is (= "a%3D1" (up-m/quote-plus "a=1"))))

(t/deftest test-unquote-plus
  (t/is (= "hello world" (up-m/unquote-plus "hello+world")))
  (t/is (= "hello world" (up-m/unquote-plus "hello%20world"))))

(t/deftest test-urlencode-map
  (let [result (up-m/urlencode {"a" "1" "b" "hello world"})]
    (t/is (clojure.string/includes? result "b=hello+world"))))

(t/deftest test-urlencode-list
  (t/is (= "a=1&b=hello+world" (up-m/urlencode [["a" "1"] ["b" "hello world"]]))))

(t/deftest test-parse-qs-basic
  (let [result (up-m/parse-qs "a=1&b=2&a=3")]
    (t/is (= ["1" "3"] (sort (get result "a"))))
    (t/is (= ["2"] (get result "b")))))

(t/deftest test-parse-qsl-basic
  (t/is (= [["a" "1"] ["b" "2"] ["a" "3"]] (up-m/parse-qsl "a=1&b=2&a=3"))))

(t/deftest test-parse-qsl-encoded
  (t/is (= [["hello world" "foo bar"]] (up-m/parse-qsl "hello+world=foo+bar"))))

(t/deftest test-urljoin-relative
  (t/is (= "https://example.com/foo/bar" (up-m/urljoin "https://example.com/foo/" "bar"))))

(t/deftest test-urljoin-absolute
  (t/is (= "https://example.com/bar" (up-m/urljoin "https://example.com/foo/" "/bar"))))

(t/deftest test-urljoin-empty
  (t/is (= "https://example.com/foo/" (up-m/urljoin "https://example.com/foo/" ""))))

(t/deftest test-urldefrag
  (let [[url frag] (up-m/urldefrag "https://example.com/path#section")]
    (t/is (= "https://example.com/path" url))
    (t/is (= "section" frag))))

(t/deftest test-urldefrag-no-frag
  (let [[url frag] (up-m/urldefrag "https://example.com/path")]
    (t/is (= "https://example.com/path" url))
    (t/is (= "" frag))))

(t/deftest test-splittype
  (let [[scheme rest] (up-m/splittype "https://example.com")]
    (t/is (= "https" scheme))
    (t/is (= "//example.com" rest))))

(t/deftest test-splithost
  (let [[host rest] (up-m/splithost "//example.com/path")]
    (t/is (= "example.com" host))
    (t/is (= "/path" rest))))

(t/deftest test-parse-qs
  (let [result (up-m/parse-qs "a=1&b=2&a=3")]
    (t/is (= {"a" ["1" "3"] "b" ["2"]} result))))

(t/deftest test-quote-plus-roundtrip
  (let [s "hello world & more"]
    (t/is (= s (up-m/unquote-plus (up-m/quote-plus s))))))

(t/deftest test-urlsplit
  (let [r (up-m/urlsplit "https://user:pass@example.com/path?q=1#frag")]
    (t/is (= "https" (:scheme r)))
    (t/is (= "user:pass@example.com" (:netloc r)))
    (t/is (= "/path" (:path r)))
    (t/is (= "q=1" (:query r)))
    (t/is (= "frag" (:fragment r)))))

(t/deftest test-quote-safe-chars
  ;; safe chars not encoded
  (t/is (= "hello/world" (up-m/quote "hello/world" "/")))
  ;; spaces encoded as %20 by default
  (t/is (= "hello%20world" (up-m/quote "hello world"))))

(t/deftest test-parse-qsl
  (let [result (up-m/parse-qsl "a=1&b=2&a=3")]
    (t/is (= [["a" "1"] ["b" "2"] ["a" "3"]] result))))

(t/deftest test-urlencode-seq
  (let [encoded (up-m/urlencode [["a" "1"] ["b" "hello world"]])]
    (t/is (string? encoded))
    (t/is (clojure.string/includes? encoded "a=1"))))

(t/deftest test-urlparse-no-query-no-fragment
  (let [r (up-m/urlparse "https://example.com/path")]
    (t/is (= "https" (:scheme r)))
    (t/is (= "" (:query r)))
    (t/is (= "" (:fragment r)))))
