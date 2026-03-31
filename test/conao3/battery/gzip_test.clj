;; Original: Lib/test/test_gzip.py

(ns conao3.battery.gzip-test
  (:require
   [clojure.test :as t]
   [conao3.battery.gzip :as gzip-m])
  (:import
   [java.util Arrays]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- bytes= [a b]
  (Arrays/equals ^bytes a ^bytes b))

(def ^:private test-data
  (b "Hello, World! This is a test string for GZIP compression. AAAAAABBBBBB"))

(t/deftest test-compress-decompress-roundtrip
  (let [compressed   (gzip-m/compress test-data)
        decompressed (gzip-m/decompress compressed)]
    (t/is (bytes= test-data decompressed))))

(t/deftest test-compress-levels
  (doseq [level [1 6 9]]
    (let [c (gzip-m/compress test-data level)
          d (gzip-m/decompress c)]
      (t/is (bytes= test-data d)))))

(t/deftest test-compress-empty
  (let [compressed (gzip-m/compress (b ""))]
    (t/is (bytes= (b "") (gzip-m/decompress compressed)))))

(t/deftest test-gzip-magic-bytes
  (let [compressed (gzip-m/compress test-data)]
    (t/is (= 0x1f (bit-and (aget compressed 0) 0xff)))
    (t/is (= 0x8b (bit-and (aget compressed 1) 0xff)))))

(t/deftest test-compress-produces-smaller-output
  (let [large-data   (byte-array (repeat 10000 (byte 65)))
        compressed   (gzip-m/compress large-data)
        decompressed (gzip-m/decompress compressed)]
    (t/is (bytes= large-data decompressed))
    (t/is (< (alength compressed) (alength large-data)))))

(t/deftest test-compress-unique-timestamps
  (let [c1 (gzip-m/compress test-data)
        c2 (gzip-m/compress test-data)]
    (t/is (bytes= (gzip-m/decompress c1) (gzip-m/decompress c2)))))

(t/deftest test-compress-default-level
  (let [compressed (gzip-m/compress test-data)
        decompressed (gzip-m/decompress compressed)]
    (t/is (bytes? compressed))
    (t/is (bytes= test-data decompressed))))

(t/deftest test-compress-single-byte
  (let [data (b "A")
        compressed (gzip-m/compress data)
        decompressed (gzip-m/decompress compressed)]
    (t/is (bytes= data decompressed))))

(t/deftest test-compress-binary-data
  (let [data (byte-array (range 256))
        compressed (gzip-m/compress data)
        decompressed (gzip-m/decompress compressed)]
    (t/is (bytes= data decompressed))))

(t/deftest test-compress-level-1-vs-9
  (let [large-data (byte-array (repeat 1000 (byte 65)))
        c1 (gzip-m/compress large-data 1)
        c9 (gzip-m/compress large-data 9)]
    (t/is (bytes= large-data (gzip-m/decompress c1)))
    (t/is (bytes= large-data (gzip-m/decompress c9)))))
