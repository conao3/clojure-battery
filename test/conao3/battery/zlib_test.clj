;; Original: Lib/test/test_zlib.py

(ns conao3.battery.zlib-test
  (:require
   [clojure.test :as t]
   [conao3.battery.zlib :as zlib])
  (:import
   [java.util Arrays]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- bytes= [a b]
  (Arrays/equals ^bytes a ^bytes b))

(def ^:private test-data
  (b "Hello, World! This is a test string for compression. AAAAAAAABBBBBBBB"))

(t/deftest test-compress-decompress-roundtrip
  (let [compressed   (zlib/compress test-data)
        decompressed (zlib/decompress compressed)]
    (t/is (bytes= test-data decompressed))))

(t/deftest test-compress-levels
  (doseq [level [0 1 6 9]]
    (let [c (zlib/compress test-data level)
          d (zlib/decompress c)]
      (t/is (bytes= test-data d)))))

(t/deftest test-compress-empty
  (let [compressed (zlib/compress (b ""))]
    (t/is (bytes= (b "") (zlib/decompress compressed)))))

(t/deftest test-decompress-known
  (let [known-compressed (byte-array [120 -100 3 0 0 0 0 1])
        result (zlib/decompress known-compressed)]
    (t/is (bytes= (b "") result))))

(t/deftest test-adler32-known
  (t/is (= 38600999 (zlib/adler32 (b "abc"))))
  (t/is (= 1 (zlib/adler32 (b "")))))

(t/deftest test-adler32-with-value
  (t/is (= (zlib/adler32 (b "abc")) (zlib/adler32 (b "abc") 1))))

(t/deftest test-crc32-known
  (t/is (= 891568578 (zlib/crc32 (b "abc"))))
  (t/is (= 0 (zlib/crc32 (b "")))))

(t/deftest test-crc32-with-value
  (t/is (= (zlib/crc32 (b "abc")) (zlib/crc32 (b "abc") 0))))

(t/deftest test-constants
  (t/is (= 0 zlib/Z_NO_COMPRESSION))
  (t/is (= 1 zlib/Z_BEST_SPEED))
  (t/is (= 9 zlib/Z_BEST_COMPRESSION))
  (t/is (= -1 zlib/Z_DEFAULT_COMPRESSION)))

(t/deftest test-compressobj-flush
  (let [obj        (zlib/compressobj 9)
        part1      (zlib/compress-chunk obj (b "Hello, "))
        part2      (zlib/compress-chunk obj (b "World!"))
        final      (zlib/flush-compressor obj)
        compressed (byte-array (+ (alength part1) (alength part2) (alength final)))]
    (System/arraycopy part1 0 compressed 0 (alength part1))
    (System/arraycopy part2 0 compressed (alength part1) (alength part2))
    (System/arraycopy final 0 compressed (+ (alength part1) (alength part2)) (alength final))
    (t/is (bytes= (b "Hello, World!") (zlib/decompress compressed)))))

(t/deftest test-decompressobj
  (let [compressed (zlib/compress test-data)
        obj        (zlib/decompressobj)
        result1    (zlib/decompress-chunk obj compressed)
        result2    (zlib/flush-decompressor obj)
        result     (byte-array (+ (alength result1) (alength result2)))]
    (System/arraycopy result1 0 result 0 (alength result1))
    (System/arraycopy result2 0 result (alength result1) (alength result2))
    (t/is (bytes= test-data result))))

(t/deftest test-compress-large
  (let [large-data (byte-array (repeat 10000 (byte 65)))
        compressed (zlib/compress large-data)
        decompressed (zlib/decompress compressed)]
    (t/is (bytes= large-data decompressed))
    (t/is (< (alength compressed) (alength large-data)))))

(t/deftest test-adler32-incremental
  (let [full-data  (b "Hello, World!")
        part1      (b "Hello, ")
        part2      (b "World!")
        full-hash  (zlib/adler32 full-data)
        part1-hash (zlib/adler32 part1)
        part2-hash (zlib/adler32 part2 part1-hash)]
    (t/is (= full-hash part2-hash))))
