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
