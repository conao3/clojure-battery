;; Original: Lib/test/test_csv.py

(ns conao3.battery.csv-test
  (:require
   [clojure.test :as t]
   [conao3.battery.csv :as csv-m])
  (:import
   [java.io StringWriter]))

(t/deftest test-reader-basic
  (let [rows (csv-m/reader ["a,b,c" "1,2,3"])]
    (t/is (= [["a" "b" "c"] ["1" "2" "3"]] (vec rows)))))

(t/deftest test-reader-quoted
  (let [rows (csv-m/reader ["\"hello world\",foo,bar"])]
    (t/is (= [["hello world" "foo" "bar"]] (vec rows)))))

(t/deftest test-reader-quoted-with-delimiter
  (let [rows (csv-m/reader ["\"a,b\",c"])]
    (t/is (= [["a,b" "c"]] (vec rows)))))

(t/deftest test-reader-doubled-quote
  (let [rows (csv-m/reader ["\"he said \"\"hello\"\"\""])]
    (t/is (= [["he said \"hello\""]] (vec rows)))))

(t/deftest test-reader-custom-delimiter
  (let [rows (csv-m/reader ["a;b;c" "1;2;3"] {:delimiter \;})]
    (t/is (= [["a" "b" "c"] ["1" "2" "3"]] (vec rows)))))

(t/deftest test-reader-empty-fields
  (let [rows (csv-m/reader ["a,,c" ",b,"])]
    (t/is (= [["a" "" "c"] ["" "b" ""]] (vec rows)))))

(t/deftest test-reader-single-field
  (let [rows (csv-m/reader ["hello"])]
    (t/is (= [["hello"]] (vec rows)))))

(t/deftest test-writer-basic
  (let [sw  (StringWriter.)
        w   (csv-m/writer sw)]
    (csv-m/writerow w ["a" "b" "c"])
    (csv-m/writerow w [1 2 3])
    (t/is (= "a,b,c\r\n1,2,3\r\n" (.toString sw)))))

(t/deftest test-writer-quoting-special
  (let [sw (StringWriter.)
        w  (csv-m/writer sw)]
    (csv-m/writerow w ["hello,world" "foo" "bar"])
    (t/is (= "\"hello,world\",foo,bar\r\n" (.toString sw)))))

(t/deftest test-writer-quote-all
  (let [sw (StringWriter.)
        w  (csv-m/writer sw {:quoting csv-m/QUOTE_ALL})]
    (csv-m/writerow w ["a" "b" "c"])
    (t/is (= "\"a\",\"b\",\"c\"\r\n" (.toString sw)))))

(t/deftest test-writer-custom-delimiter
  (let [sw (StringWriter.)
        w  (csv-m/writer sw {:delimiter \;})]
    (csv-m/writerow w ["a" "b" "c"])
    (t/is (= "a;b;c\r\n" (.toString sw)))))

(t/deftest test-writer-writerows
  (let [sw (StringWriter.)
        w  (csv-m/writer sw)]
    (csv-m/writerows w [["a" "b"] [1 2] ["x" "y"]])
    (t/is (= "a,b\r\n1,2\r\nx,y\r\n" (.toString sw)))))

(t/deftest test-dict-reader-basic
  (let [rows (csv-m/dict-reader ["name,age,city" "Alice,30,NYC" "Bob,25,LA"])]
    (let [rows-v (vec rows)]
      (t/is (= 2 (count rows-v)))
      (t/is (= "Alice" (get (first rows-v) "name")))
      (t/is (= "30" (get (first rows-v) "age")))
      (t/is (= "NYC" (get (first rows-v) "city"))))))

(t/deftest test-dict-writer-basic
  (let [sw (StringWriter.)
        dw (csv-m/dict-writer sw ["name" "age"])]
    (csv-m/writeheader dw)
    (csv-m/writerow-dict dw {"name" "Alice" "age" 30})
    (t/is (= "name,age\r\nAlice,30\r\n" (.toString sw)))))

(t/deftest test-roundtrip
  (let [data [["name" "age" "city"]
              ["Alice" "30" "NYC"]
              ["Bob" "25" "LA"]]
        sw (StringWriter.)
        w  (csv-m/writer sw)]
    (csv-m/writerows w data)
    (let [csv-str (.toString sw)
          lines   (clojure.string/split-lines (clojure.string/replace csv-str "\r\n" "\n"))
          rows    (vec (csv-m/reader lines))]
      (t/is (= data rows)))))

(t/deftest test-reader-single-quote
  (let [rows (csv-m/reader ["'a','b','c'"] {:quotechar \'})]
    (t/is (= [["a" "b" "c"]] (vec rows)))))

(t/deftest test-writer-escape-quote-in-field
  (let [sw (StringWriter.)
        w  (csv-m/writer sw)]
    (csv-m/writerow w ["say \"hello\""])
    (t/is (= "\"say \"\"hello\"\"\"\r\n" (.toString sw)))))

(t/deftest test-quoting-constants
  (t/is (= 0 csv-m/QUOTE_MINIMAL))
  (t/is (= 1 csv-m/QUOTE_ALL))
  (t/is (= 2 csv-m/QUOTE_NONNUMERIC))
  (t/is (= 3 csv-m/QUOTE_NONE)))

(t/deftest test-writer-writerows-dict
  (let [sw (StringWriter.)
        dw (csv-m/dict-writer sw ["x" "y"])]
    (csv-m/writerows-dict dw [{"x" 1 "y" 2} {"x" 3 "y" 4}])
    (t/is (= "1,2\r\n3,4\r\n" (.toString sw)))))

(t/deftest test-dict-reader-with-fieldnames
  (let [rows (csv-m/dict-reader-with-fieldnames ["1,2,3"] ["a" "b" "c"])]
    (t/is (= {"a" "1" "b" "2" "c" "3"} (first (vec rows))))))

(t/deftest test-reader-newline-in-quoted
  (let [rows (csv-m/reader ["\"line1\nline2\",foo"])]
    (t/is (= [["line1\nline2" "foo"]] (vec rows)))))
