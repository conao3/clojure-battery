(ns conao3.battery.binascii
  (:import [java.util HexFormat Base64]
           [java.util.zip CRC32]))

(defonce ^:private crc32-table
  (let [table (int-array 256)]
    (dotimes [i 256]
      (loop [c i j 8]
        (if (zero? j)
          (aset table i (unchecked-int c))
          (recur (if (odd? c)
                   (bit-xor (unsigned-bit-shift-right c 1) 0xEDB88320)
                   (unsigned-bit-shift-right c 1))
                 (dec j)))))
    table))

(defn- to-bytes [data]
  (if (bytes? data) data (.getBytes ^String data "ISO-8859-1")))

(defn hexlify
  ([data] (hexlify data nil 1))
  ([data sep] (hexlify data sep 1))
  ([data sep bytes-per-sep]
   (let [bs   (to-bytes data)
         n    (alength ^bytes bs)
         fmt  (HexFormat/of)
         hex  (.formatHex fmt ^bytes bs)]
     (if (nil? sep)
       (.getBytes ^String hex "ASCII")
       (let [sep-str (if (bytes? sep) (String. ^bytes sep "ASCII") (str sep))
             pairs   (mapv #(subs hex (* % 2) (+ (* % 2) 2)) (range n))
             abs-n   (Math/abs (int bytes-per-sep))
             groups  (if (neg? bytes-per-sep)
                       (partition abs-n abs-n [] pairs)
                       (let [rem (mod n abs-n)
                             front (if (zero? rem) '() (list (take rem pairs)))
                             rest-pairs (drop rem pairs)]
                         (concat front (partition abs-n rest-pairs))))
             joined  (map #(apply str %) groups)]
         (.getBytes ^String (clojure.string/join sep-str joined) "ASCII"))))))

(def b2a-hex hexlify)

(defn unhexlify [hexstr]
  (let [s  (if (bytes? hexstr) (String. ^bytes hexstr "ASCII") (str hexstr))
        fmt (HexFormat/of)]
    (try
      (.parseHex fmt ^String s)
      (catch IllegalArgumentException e
        (throw (ex-info (.getMessage e) {:type :binascii-error}))))))

(def a2b-hex unhexlify)

(defn b2a-base64
  ([data] (b2a-base64 data true))
  ([data newline]
   (let [bs  (to-bytes data)
         enc (Base64/getEncoder)
         b64 (.encode enc ^bytes bs)]
     (if newline
       (let [result (byte-array (inc (alength b64)))]
         (System/arraycopy b64 0 result 0 (alength b64))
         (aset result (alength b64) (byte \newline))
         result)
       b64))))

(defn a2b-base64 [data]
  (let [s  (if (bytes? data) (String. ^bytes data "ASCII") (str data))
        dec (Base64/getMimeDecoder)]
    (try
      (.decode dec ^String s)
      (catch IllegalArgumentException e
        (throw (ex-info (.getMessage e) {:type :binascii-error}))))))

(defn crc32
  ([data] (crc32 data 0))
  ([data value]
   (let [bs   (to-bytes data)
         init (bit-xor (bit-and (long value) (long 0xFFFFFFFF)) (long 0xFFFFFFFF))]
     (loop [i 0 crc init]
       (if (>= i (alength ^bytes bs))
         (bit-and (bit-xor crc (long 0xFFFFFFFF)) (long 0xFFFFFFFF))
         (let [b    (bit-and (long (aget ^bytes bs i)) 0xFF)
               idx  (int (bit-and (bit-xor crc b) 0xFF))
               tval (bit-and (long (aget ^ints crc32-table idx)) (long 0xFFFFFFFF))
               new-crc (bit-xor (unsigned-bit-shift-right crc 8) tval)]
           (recur (inc i) new-crc)))))))

(defn crc-hqx [data value]
  (let [bs (to-bytes data)]
    (loop [i 0 crc (bit-and (int value) 0xFFFF)]
      (if (>= i (alength ^bytes bs))
        crc
        (let [b    (bit-and (aget ^bytes bs i) 0xFF)
              new-crc (loop [j 0 c (bit-and (bit-xor crc (bit-shift-left b 8)) 0xFFFF)]
                        (if (= j 8)
                          c
                          (recur (inc j)
                                 (if (not (zero? (bit-and c 0x8000)))
                                   (bit-and (bit-xor (bit-shift-left c 1) 0x1021) 0xFFFF)
                                   (bit-and (bit-shift-left c 1) 0xFFFF)))))]
          (recur (inc i) new-crc))))))
