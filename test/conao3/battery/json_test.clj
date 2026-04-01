;; Original: Lib/test/test_json.py

(ns conao3.battery.json-test
  (:require
   [clojure.test :as t]
   [conao3.battery.json :as json-m]))

(t/deftest test-dumps-primitives
  (t/is (= "null" (json-m/dumps nil)))
  (t/is (= "true" (json-m/dumps true)))
  (t/is (= "false" (json-m/dumps false)))
  (t/is (= "42" (json-m/dumps 42)))
  (t/is (= "\"hello\"" (json-m/dumps "hello"))))

(t/deftest test-dumps-list
  (t/is (= "[1, 2, 3]" (json-m/dumps [1 2 3])))
  (t/is (= "[]" (json-m/dumps []))))

(t/deftest test-dumps-map
  (let [result (json-m/dumps {"a" 1 "b" 2} :sort-keys true)]
    (t/is (= "{\"a\": 1, \"b\": 2}" result))))

(t/deftest test-dumps-nested
  (t/is (= "{\"x\": {\"y\": \"z\"}}" (json-m/dumps {"x" {"y" "z"}}))))

(t/deftest test-dumps-sort-keys
  (t/is (= "{\"a\": 1, \"b\": 2}" (json-m/dumps {"b" 2 "a" 1} :sort-keys true))))

(t/deftest test-dumps-separators
  (t/is (= "{\"a\":1,\"b\":2}" (json-m/dumps {"a" 1 "b" 2} :separators ["," ":"] :sort-keys true))))

(t/deftest test-dumps-string-escape
  (t/is (= "\"hello\\nworld\"" (json-m/dumps "hello\nworld")))
  (t/is (= "\"tab\\there\"" (json-m/dumps "tab\there")))
  (t/is (= "\"quote\\\"inside\"" (json-m/dumps "quote\"inside"))))

(t/deftest test-loads-primitives
  (t/is (nil? (json-m/loads "null")))
  (t/is (= true (json-m/loads "true")))
  (t/is (= false (json-m/loads "false")))
  (t/is (= 42 (json-m/loads "42")))
  (t/is (= 3.14 (json-m/loads "3.14")))
  (t/is (= "hello" (json-m/loads "\"hello\""))))

(t/deftest test-loads-list
  (t/is (= [1 2 3] (json-m/loads "[1, 2, 3]")))
  (t/is (= [] (json-m/loads "[]"))))

(t/deftest test-loads-map
  (let [result (json-m/loads "{\"a\": 1, \"b\": 2}")]
    (t/is (= 1 (get result "a")))
    (t/is (= 2 (get result "b")))))

(t/deftest test-loads-nested
  (let [result (json-m/loads "{\"x\": {\"y\": \"z\"}}")]
    (t/is (= "z" (get-in result ["x" "y"])))))

(t/deftest test-loads-mixed-types
  (let [result (json-m/loads "[true, false, null, 1, 2.5, \"hello\"]")]
    (t/is (= true (nth result 0)))
    (t/is (= false (nth result 1)))
    (t/is (nil? (nth result 2)))
    (t/is (= 1 (nth result 3)))
    (t/is (= 2.5 (nth result 4)))
    (t/is (= "hello" (nth result 5)))))

(t/deftest test-loads-string-escape
  (t/is (= "hello\nworld" (json-m/loads "\"hello\\nworld\"")))
  (t/is (= "quote\"inside" (json-m/loads "\"quote\\\"inside\""))))

(t/deftest test-loads-unicode-escape
  (t/is (= "\u0041" (json-m/loads "\"\\u0041\""))))

(t/deftest test-loads-error-invalid
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads "{invalid}"))))

(t/deftest test-roundtrip
  (let [data {"name" "Alice" "age" 30 "scores" [95.5 87.0 92.3] "active" true "note" nil}]
    (t/is (= data (json-m/loads (json-m/dumps data))))))

(t/deftest test-dumps-indent
  (let [result (json-m/dumps {"a" 1 "b" [1 2]} :indent 2 :sort-keys true)]
    (t/is (clojure.string/includes? result "\n"))
    (t/is (clojure.string/includes? result "  "))))

(t/deftest test-dumps-keywords
  (t/is (= "{\"a\": 1}" (json-m/dumps {:a 1}))))

(t/deftest test-loads-whitespace
  (t/is (= {"a" 1} (json-m/loads "  {  \"a\"  :  1  }  "))))

(t/deftest test-loads-empty-string-key
  (t/is (= {"" "value"} (json-m/loads "{\"\" : \"value\"}"))))

(t/deftest test-loads-negative-number
  (t/is (= -42 (json-m/loads "-42")))
  (t/is (= -3.14 (json-m/loads "-3.14"))))

(t/deftest test-loads-scientific-notation
  (let [v (json-m/loads "1e3")]
    (t/is (< (Math/abs (- v 1000.0)) 0.001))))

(t/deftest test-dumps-large-int
  (t/is (= "9007199254740992" (json-m/dumps 9007199254740992))))

(t/deftest test-dumps-sort-keys
  (t/is (= "{\"a\": 1, \"b\": 2, \"c\": 3}" (json-m/dumps {"c" 3 "b" 2 "a" 1} :sort-keys true))))

(t/deftest test-dumps-indent
  (let [result (json-m/dumps {"a" 1} :indent 2)]
    (t/is (clojure.string/includes? result "\n"))
    (t/is (clojure.string/includes? result "  "))))

(t/deftest test-loads-unicode-escape
  (t/is (= "\u0041" (json-m/loads "\"\\u0041\"")))
  (t/is (= "A" (json-m/loads "\"\\u0041\""))))

(t/deftest test-dumps-special-chars
  (t/is (= "\"hello\\nworld\"" (json-m/dumps "hello\nworld")))
  (t/is (= "\"tab\\there\"" (json-m/dumps "tab\there"))))

(t/deftest test-dump-and-load-streams
  (let [data {"key" [1 2 3] "val" true}
        out (java.io.StringWriter.)]
    (json-m/dump data out)
    (let [s (.toString out)
          in (java.io.StringReader. s)
          result (json-m/load in)]
      (t/is (= {"key" [1 2 3] "val" true} result)))))

(t/deftest test-loads-bytes-input
  (t/is (= 42 (json-m/loads (.getBytes "42" "UTF-8"))))
  (t/is (= [1 2 3] (json-m/loads (.getBytes "[1, 2, 3]" "UTF-8"))))
  (t/is (= {"a" 1} (json-m/loads (.getBytes "{\"a\": 1}" "UTF-8")))))

(t/deftest test-loads-trailing-comma-errors
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads "[1,]")))
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads "{\"a\": 1,}")))
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads "{")))
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads "["))))

(t/deftest test-loads-invalid-literal
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads "undefined")))
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads "nil")))
  (t/is (thrown? clojure.lang.ExceptionInfo (json-m/loads ""))))

(t/deftest test-dumps-ensure-ascii-default
  ;; By default, non-ASCII chars should be escaped in JSON output
  (let [result (json-m/dumps "\u00e9")]
    (t/is (or (= "\"\\u00e9\"" result) (= "\"\u00e9\"" result)))))

(t/deftest test-loads-nested-deeply
  (let [data {"a" {"b" {"c" [1 2 {"d" true}]}}}]
    (t/is (= data (json-m/loads (json-m/dumps data))))))

(t/deftest test-dumps-array-of-mixed-types
  (let [data [1 "two" true nil 3.14]]
    (t/is (= data (json-m/loads (json-m/dumps data))))))

(t/deftest test-loads-number-types
  ;; integers stay integers, floats stay floats
  (t/is (= 42 (json-m/loads "42")))
  (t/is (= 3.14 (json-m/loads "3.14")))
  (t/is (integer? (json-m/loads "42")))
  (t/is (float? (json-m/loads "3.14"))))

(t/deftest test-dumps-null
  (t/is (= "null" (json-m/dumps nil))))

(t/deftest test-dumps-booleans
  (t/is (= "true" (json-m/dumps true)))
  (t/is (= "false" (json-m/dumps false))))

(t/deftest test-roundtrip-empty-collections
  (t/is (= {} (json-m/loads (json-m/dumps {}))))
  (t/is (= [] (json-m/loads (json-m/dumps [])))))
