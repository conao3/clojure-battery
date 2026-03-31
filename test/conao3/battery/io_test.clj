;; Original: Lib/test/test_io.py

(ns conao3.battery.io-test
  (:require
   [clojure.test :as t]
   [conao3.battery.io :as io-m])
  (:import
   [clojure.lang ExceptionInfo]
   [java.util Arrays]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- bytes= [a b]
  (Arrays/equals ^bytes a ^bytes b))

(t/deftest test-bytes-io-write-read
  (let [buf (io-m/bytes-io)]
    (io-m/write buf (b "hello"))
    (io-m/write buf (b " world"))
    (t/is (= 11 (io-m/tell buf)))
    (io-m/seek buf 0)
    (t/is (bytes= (b "hello world") (io-m/read buf)))))

(t/deftest test-bytes-io-read-n
  (let [buf (io-m/bytes-io (b "hello world"))]
    (t/is (bytes= (b "hel") (io-m/read buf 3)))
    (t/is (= 3 (io-m/tell buf)))
    (t/is (bytes= (b "lo w") (io-m/read buf 4)))))

(t/deftest test-bytes-io-seek-modes
  (let [buf (io-m/bytes-io (b "hello world"))]
    (io-m/seek buf 6)
    (t/is (bytes= (b "world") (io-m/read buf)))
    (io-m/seek buf -5 2)
    (t/is (bytes= (b "world") (io-m/read buf)))
    (io-m/seek buf -3 1)
    (t/is (bytes= (b "rld") (io-m/read buf)))))

(t/deftest test-bytes-io-tell
  (let [buf (io-m/bytes-io (b "hello"))]
    (t/is (= 0 (io-m/tell buf)))
    (io-m/read buf 3)
    (t/is (= 3 (io-m/tell buf)))
    (io-m/read buf)
    (t/is (= 5 (io-m/tell buf)))))

(t/deftest test-bytes-io-getvalue
  (let [buf (io-m/bytes-io)]
    (io-m/write buf (b "test"))
    (io-m/seek buf 0)
    (io-m/read buf 2)
    (t/is (bytes= (b "test") (io-m/getvalue buf)))))

(t/deftest test-bytes-io-truncate
  (let [buf (io-m/bytes-io (b "hello world"))]
    (io-m/seek buf 5)
    (io-m/truncate buf)
    (io-m/seek buf 0)
    (t/is (bytes= (b "hello") (io-m/read buf)))))

(t/deftest test-bytes-io-empty-read
  (let [buf (io-m/bytes-io)]
    (t/is (bytes= (b "") (io-m/read buf)))
    (t/is (bytes= (b "") (io-m/read buf 10)))))

(t/deftest test-bytes-io-from-bytes
  (let [buf (io-m/bytes-io (b "test data"))]
    (t/is (bytes= (b "test data") (io-m/read buf)))))

(t/deftest test-string-io-write-read
  (let [buf (io-m/string-io)]
    (io-m/write buf "hello")
    (io-m/write buf " world")
    (io-m/seek buf 0)
    (t/is (= "hello world" (io-m/read buf)))))

(t/deftest test-string-io-from-string
  (let [buf (io-m/string-io "hello world")]
    (t/is (= "hello world" (io-m/read buf)))))

(t/deftest test-string-io-tell-seek
  (let [buf (io-m/string-io "hello world")]
    (t/is (= 0 (io-m/tell buf)))
    (io-m/read buf 5)
    (t/is (= 5 (io-m/tell buf)))))

(t/deftest test-string-io-getvalue
  (let [buf (io-m/string-io)]
    (io-m/write buf "test")
    (t/is (= "test" (io-m/getvalue buf)))))

(t/deftest test-string-io-readline
  (let [buf (io-m/string-io "line1\nline2\nline3")]
    (t/is (= "line1\n" (io-m/readline buf)))
    (t/is (= "line2\n" (io-m/readline buf)))
    (t/is (= "line3" (io-m/readline buf)))))

(t/deftest test-bytes-io-write-returns-count
  (let [buf (io-m/bytes-io)]
    (t/is (= 5 (io-m/write buf (b "hello"))))
    (t/is (= 6 (io-m/write buf (b " world"))))))
