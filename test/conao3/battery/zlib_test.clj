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

(t/deftest test-compress-single-byte
  (let [data (b "A")
        compressed (zlib/compress data)
        decompressed (zlib/decompress compressed)]
    (t/is (bytes= data decompressed))))

(t/deftest test-compress-binary-data
  (let [data (byte-array (range 256))
        compressed (zlib/compress data)
        decompressed (zlib/decompress compressed)]
    (t/is (bytes= data decompressed))))

(t/deftest test-crc32-deterministic
  (let [data (b "hello world")]
    (t/is (= (zlib/crc32 data) (zlib/crc32 data)))))

(t/deftest test-adler32-deterministic
  (let [data (b "hello world")]
    (t/is (= (zlib/adler32 data) (zlib/adler32 data)))))

(t/deftest test-crc32-start
  ;; crc32(empty, 0) == crc32(empty)
  (t/is (= (zlib/crc32 (byte-array 0)) (zlib/crc32 (byte-array 0) 0))))

(t/deftest test-crc32-empty-with-value
  ;; crc32(b"", N) == N
  (t/is (= 0 (zlib/crc32 (byte-array 0) 0)))
  (t/is (= 1 (zlib/crc32 (byte-array 0) 1)))
  (t/is (= 432 (zlib/crc32 (byte-array 0) 432))))

(t/deftest test-adler32-empty-with-value
  ;; adler32(b"", N) == N
  (t/is (= 0 (zlib/adler32 (byte-array 0) 0)))
  (t/is (= 1 (zlib/adler32 (byte-array 0) 1)))
  (t/is (= 432 (zlib/adler32 (byte-array 0) 432))))

(t/deftest test-crc32-running-sum
  ;; crc32(a+b) == crc32(b, crc32(a))
  (let [a (b "hello ")
        b-bytes (b "world")
        ab (b "hello world")]
    (t/is (= (zlib/crc32 ab) (zlib/crc32 b-bytes (zlib/crc32 a))))))

(t/deftest test-adler32-running-sum
  ;; adler32(a+b) == adler32(b, adler32(a))
  (let [a (b "hello ")
        b-bytes (b "world")
        ab (b "hello world")]
    (t/is (= (zlib/adler32 ab) (zlib/adler32 b-bytes (zlib/adler32 a))))))

(t/deftest test-compress-level-constants
  (t/is (= 0 zlib/Z_NO_COMPRESSION))
  (t/is (= 1 zlib/Z_BEST_SPEED))
  (t/is (= 9 zlib/Z_BEST_COMPRESSION))
  (t/is (= -1 zlib/Z_DEFAULT_COMPRESSION)))

(t/deftest test-compress-no-compression-level
  ;; Z_NO_COMPRESSION still produces valid deflate stream
  (let [data (b "hello world")
        compressed (zlib/compress data zlib/Z_NO_COMPRESSION)
        decompressed (zlib/decompress compressed)]
    (t/is (bytes= data decompressed))))

(t/deftest test-compress-best-speed
  (let [data test-data
        compressed (zlib/compress data zlib/Z_BEST_SPEED)
        decompressed (zlib/decompress compressed)]
    (t/is (bytes= data decompressed))))

(t/deftest test-crc32-known-value
  ;; crc32 of "hello world" is a known constant
  (t/is (= 222957957 (zlib/crc32 (b "hello world")))))
