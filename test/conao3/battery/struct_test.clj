;; Original: Lib/test/test_struct.py

(ns conao3.battery.struct-test
  (:require
   [clojure.test :as t]
   [conao3.battery.struct :as struct-m])
  (:import
   [java.util Arrays]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- bytes= [a b]
  (Arrays/equals ^bytes a ^bytes b))

(t/deftest test-calcsize
  (t/is (= 2 (struct-m/calcsize ">H")))
  (t/is (= 4 (struct-m/calcsize ">HH")))
  (t/is (= 4 (struct-m/calcsize "<i")))
  (t/is (= 12 (struct-m/calcsize "<iif")))
  (t/is (= 8 (struct-m/calcsize "!q")))
  (t/is (= 3 (struct-m/calcsize "3B")))
  (t/is (= 4 (struct-m/calcsize "4s"))))

(t/deftest test-pack-unsigned-short-big-endian
  (let [packed (struct-m/pack ">H" 256)]
    (t/is (bytes= (byte-array [1 0]) packed))))

(t/deftest test-pack-signed-int-little-endian
  (let [packed (struct-m/pack "<i" -1)]
    (t/is (bytes= (byte-array [0xff 0xff 0xff 0xff]) packed))))

(t/deftest test-unpack-unsigned-short-big-endian
  (t/is (= [256] (struct-m/unpack ">H" (byte-array [1 0])))))

(t/deftest test-unpack-signed-int-little-endian
  (t/is (= [-1] (struct-m/unpack "<i" (byte-array [0xff 0xff 0xff 0xff])))))

(t/deftest test-pack-multiple
  (let [packed (struct-m/pack ">HH" 1 2)]
    (t/is (bytes= (byte-array [0x00 0x01 0x00 0x02]) packed))))

(t/deftest test-unpack-multiple
  (t/is (= [1 2] (struct-m/unpack ">HH" (byte-array [0x00 0x01 0x00 0x02])))))

(t/deftest test-pack-bytes
  (t/is (bytes= (byte-array [1 2 3]) (struct-m/pack "3B" 1 2 3))))

(t/deftest test-pack-float-big-endian
  (let [packed (struct-m/pack "!f" 1.5)
        unpacked (struct-m/unpack "!f" packed)]
    (t/is (< (Math/abs (- 1.5 (float (first unpacked)))) 1e-6))))

(t/deftest test-pack-string
  (let [packed (struct-m/pack "4s" (b "test"))]
    (t/is (bytes= (b "test") packed)))
  (let [[bs] (struct-m/unpack "4s" (b "test"))]
    (t/is (bytes= (b "test") bs))))

(t/deftest test-pack-long
  (let [packed (struct-m/pack ">q" 9223372036854775807)]
    (t/is (= [9223372036854775807] (struct-m/unpack ">q" packed)))))

(t/deftest test-pack-unsigned-byte
  (let [packed (struct-m/pack "B" 255)]
    (t/is (= [255] (struct-m/unpack "B" packed)))))

(t/deftest test-pack-network-order
  (t/is (bytes= (struct-m/pack "!H" 256) (struct-m/pack ">H" 256))))

(t/deftest test-pack-bool
  (let [packed-true  (struct-m/pack "?" true)
        packed-false (struct-m/pack "?" false)]
    (t/is (= [true] (struct-m/unpack "?" packed-true)))
    (t/is (= [false] (struct-m/unpack "?" packed-false)))))

(t/deftest test-pad-byte
  (let [packed (struct-m/pack "xH" 0x0102)]
    (t/is (= 3 (alength packed)))
    (t/is (= [0x0102] (struct-m/unpack "xH" packed)))))

(t/deftest test-roundtrip-double
  (let [v 3.14159265358979
        packed (struct-m/pack "<d" v)
        [v2] (struct-m/unpack "<d" packed)]
    (t/is (< (Math/abs (- v v2)) 1e-12))))

(t/deftest test-pack-into
  (let [buf (byte-array 6)]
    (struct-m/pack-into ">H" buf 0 0x0102)
    (struct-m/pack-into ">H" buf 2 0x0304)
    (struct-m/pack-into ">H" buf 4 0x0506)
    (t/is (= [1 2 3 4 5 6] (map #(bit-and % 0xff) buf)))))

(t/deftest test-pack-signed-short
  (let [packed (struct-m/pack ">h" -1)]
    (t/is (= [-1] (struct-m/unpack ">h" packed))))
  (let [packed (struct-m/pack "<h" 32767)]
    (t/is (= [32767] (struct-m/unpack "<h" packed)))))

(t/deftest test-pack-zero-values
  (let [packed (struct-m/pack ">HHH" 0 0 0)]
    (t/is (= [0 0 0] (struct-m/unpack ">HHH" packed)))))

(t/deftest test-unpack-from-offset
  (let [data (struct-m/pack ">HH" 0x0102 0x0304)]
    (t/is (= [0x0304] (struct-m/unpack ">H" (byte-array (drop 2 data)))))))

(t/deftest test-byteorder-equivalence
  ;; ! and > should produce same results for big-endian
  (t/is (Arrays/equals (struct-m/pack "!H" 0x1234) (struct-m/pack ">H" 0x1234)))
  (t/is (= [0x1234] (struct-m/unpack "!H" (byte-array [0x12 0x34])))))

(t/deftest test-signed-boundary-values
  ;; Signed short boundaries
  (t/is (= [32767]  (struct-m/unpack ">h" (struct-m/pack ">h" 32767))))
  (t/is (= [-32768] (struct-m/unpack ">h" (struct-m/pack ">h" -32768))))
  ;; Signed long boundaries
  (t/is (= [9223372036854775807]  (struct-m/unpack ">q" (struct-m/pack ">q" 9223372036854775807))))
  (t/is (= [-9223372036854775808] (struct-m/unpack ">q" (struct-m/pack ">q" -9223372036854775808)))))

(t/deftest test-unsigned-boundary-values
  ;; Unsigned short boundaries
  (t/is (= [0]     (struct-m/unpack ">H" (struct-m/pack ">H" 0))))
  (t/is (= [65535] (struct-m/unpack ">H" (struct-m/pack ">H" 65535))))
  ;; Unsigned int boundaries
  (t/is (= [0]          (struct-m/unpack ">I" (struct-m/pack ">I" 0))))
  (t/is (= [4294967295] (struct-m/unpack ">I" (struct-m/pack ">I" 4294967295)))))

(t/deftest test-unpack-insufficient-data
  ;; Try to unpack more bytes than provided
  (t/is (thrown? Exception (struct-m/unpack ">HH" (byte-array [0x01 0x02]))))
  (t/is (thrown? Exception (struct-m/unpack ">d" (byte-array [0x01 0x02 0x03])))))
