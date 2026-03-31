(ns conao3.battery.uuid
  (:require [clojure.string :as str])
  (:import [java.security MessageDigest]
           [java.util UUID Arrays]))

(defn- unsigned-int [x]
  (bit-and x 0xFFFFFFFF))

(defn- uuid-from-longs [most least]
  {:most most :least least})

(defn uuid-int [u]
  (let [m (BigInteger/valueOf (:most u))
        l (BigInteger/valueOf (:least u))
        m-u (if (.testBit m 63)
              (.add m (.shiftLeft BigInteger/ONE 64))
              m)
        l-u (if (.testBit l 63)
              (.add l (.shiftLeft BigInteger/ONE 64))
              l)]
    (.add (.shiftLeft m-u 64) l-u)))

(defn uuid-bytes [u]
  (let [m (:most u)
        l (:least u)
        result (byte-array 16)]
    (dotimes [i 8]
      (aset result i (unchecked-byte (bit-shift-right m (* 8 (- 7 i))))))
    (dotimes [i 8]
      (aset result (+ i 8) (unchecked-byte (bit-shift-right l (* 8 (- 7 i))))))
    result))

(defn uuid-hex [u]
  (->> (uuid-bytes u)
       (map #(format "%02x" (bit-and % 0xff)))
       (apply str)))

(defn uuid-str [u]
  (let [h (uuid-hex u)]
    (str (subs h 0 8) "-" (subs h 8 12) "-" (subs h 12 16) "-"
         (subs h 16 20) "-" (subs h 20 32))))

(defn uuid-version [u]
  (int (bit-and (bit-shift-right (:most u) 12) 0xf)))

(defn uuid-variant [u]
  (let [bits (bit-and (bit-shift-right (:least u) 62) 0x3)]
    (cond
      (zero? (bit-and bits 2)) :reserved-ncs
      (zero? (bit-and bits 1)) :rfc-4122
      :else                    :reserved-microsoft)))

(defn uuid-fields [u]
  (let [m (:most u)
        l (:least u)
        time-low           (unsigned-int (bit-shift-right m 32))
        time-mid           (bit-and (bit-shift-right m 16) 0xFFFF)
        time-hi-version    (bit-and m 0xFFFF)
        clock-seq-hi-var   (bit-and (bit-shift-right l 56) 0xFF)
        clock-seq-low      (bit-and (bit-shift-right l 48) 0xFF)
        node               (bit-and l 0xFFFFFFFFFFFF)]
    [time-low time-mid time-hi-version clock-seq-hi-var clock-seq-low node]))

(defn uuid-time-low [u]
  (nth (uuid-fields u) 0))

(defn uuid-time-mid [u]
  (nth (uuid-fields u) 1))

(defn uuid-time-hi-version [u]
  (nth (uuid-fields u) 2))

(defn uuid-clock-seq-hi-variant [u]
  (nth (uuid-fields u) 3))

(defn uuid-clock-seq-low [u]
  (nth (uuid-fields u) 4))

(defn uuid-node [u]
  (nth (uuid-fields u) 5))

(defn- from-str [s]
  (let [clean (str/replace s "-" "")
        _ (when (not= (count clean) 32)
            (throw (ex-info (str "badly formed hexadecimal UUID string: " s) {:str s})))
        big (BigInteger. clean 16)
        mask64 (.subtract (.shiftLeft BigInteger/ONE 64) BigInteger/ONE)
        m   (.longValue (.shiftRight big 64))
        l   (.longValue (.and big mask64))]
    (uuid-from-longs m l)))

(defn- from-bytes [bs]
  (when (not= (alength ^bytes bs) 16)
    (throw (ex-info "bytes is not a 16-char string" {})))
  (let [m (loop [i 0 acc (long 0)]
             (if (= i 8) acc
               (recur (inc i)
                      (bit-or (bit-shift-left acc 8)
                              (bit-and (aget ^bytes bs i) 0xFF)))))
        l (loop [i 8 acc (long 0)]
             (if (= i 16) acc
               (recur (inc i)
                      (bit-or (bit-shift-left acc 8)
                              (bit-and (aget ^bytes bs i) 0xFF)))))]
    (uuid-from-longs m l)))

(defn- from-int [n]
  (let [big (if (instance? BigInteger n) n (BigInteger/valueOf (long n)))
        mask64 (.subtract (.shiftLeft BigInteger/ONE 64) BigInteger/ONE)
        m   (.longValue (.shiftRight big 64))
        l   (.longValue (.and big mask64))]
    (uuid-from-longs m l)))

(defn- from-fields [[time-low time-mid time-hi-version clock-seq-hi-var clock-seq-low node]]
  (let [m (bit-or (bit-shift-left (long time-low) 32)
                  (bit-shift-left (long time-mid) 16)
                  (long time-hi-version))
        l (bit-or (bit-shift-left (long clock-seq-hi-var) 56)
                  (bit-shift-left (long clock-seq-low) 48)
                  (long node))]
    (uuid-from-longs m l)))

(defn make-uuid
  [& {:keys [str hex bytes int fields]}]
  (cond
    str    (from-str str)
    hex    (from-str hex)
    bytes  (from-bytes bytes)
    int    (from-int int)
    fields (from-fields fields)
    :else  (throw (ex-info "one of str, hex, bytes, int, or fields must be given" {}))))

(defn- set-version [u version]
  (let [m (bit-or (bit-and (:most u) (bit-not 0xF000))
                  (bit-shift-left (long version) 12))]
    (uuid-from-longs m (:least u))))

(defn- set-variant [u]
  (let [l (bit-or (bit-and (:least u) (bit-not (bit-shift-left 0x3 62)))
                  (bit-shift-left (long 2) 62))]
    (uuid-from-longs (:most u) l)))

(defn- name-uuid [namespace-uuid name-str version hash-algo]
  (let [ns-bytes   (uuid-bytes namespace-uuid)
        name-bytes (.getBytes ^String name-str "UTF-8")
        combined   (byte-array (+ (alength ns-bytes) (alength name-bytes)))]
    (System/arraycopy ns-bytes 0 combined 0 (alength ns-bytes))
    (System/arraycopy name-bytes 0 combined (alength ns-bytes) (alength name-bytes))
    (let [md     (MessageDigest/getInstance hash-algo)
          digest (.digest md combined)]
      (-> (from-bytes (Arrays/copyOf digest 16))
          (set-version version)
          (set-variant)))))

(def NAMESPACE_DNS  (from-str "6ba7b810-9dad-11d1-80b4-00c04fd430c8"))
(def NAMESPACE_URL  (from-str "6ba7b811-9dad-11d1-80b4-00c04fd430c8"))
(def NAMESPACE_OID  (from-str "6ba7b812-9dad-11d1-80b4-00c04fd430c8"))
(def NAMESPACE_X500 (from-str "6ba7b814-9dad-11d1-80b4-00c04fd430c8"))

(defn uuid1 []
  (let [ju (UUID/randomUUID)
        m  (.getMostSignificantBits ju)
        m' (bit-or (bit-and m (bit-not (long 0xF000)))
                   (long 0x1000))]
    (set-variant (uuid-from-longs m' (.getLeastSignificantBits ju)))))

(defn uuid3 [namespace name-str]
  (name-uuid namespace name-str 3 "MD5"))

(defn uuid4 []
  (let [ju (UUID/randomUUID)]
    (uuid-from-longs (.getMostSignificantBits ju)
                     (.getLeastSignificantBits ju))))

(defn uuid5 [namespace name-str]
  (name-uuid namespace name-str 5 "SHA-1"))
