(ns conao3.battery.zlib
  (:import [java.io ByteArrayOutputStream]
           [java.util.zip Adler32 CRC32 Deflater Inflater]))

(def Z_NO_COMPRESSION      0)
(def Z_BEST_SPEED          1)
(def Z_BEST_COMPRESSION    9)
(def Z_DEFAULT_COMPRESSION -1)

(defn adler32
  ([data] (.getValue (doto (Adler32.) (.update ^bytes data))))
  ([data value]
   (if (= value 1)
     (adler32 data)
     (let [s1 (long (bit-and value 0xFFFF))
           s2 (long (bit-and (bit-shift-right value 16) 0xFFFF))
           MOD_ADLER 65521]
       (loop [i 0 s1' s1 s2' s2]
         (if (= i (alength ^bytes data))
           (bit-or (bit-shift-left (mod s2' MOD_ADLER) 16)
                   (mod s1' MOD_ADLER))
           (let [b (bit-and (aget ^bytes data i) 0xFF)
                 s1'' (mod (+ s1' b) MOD_ADLER)
                 s2'' (mod (+ s2' s1'') MOD_ADLER)]
             (recur (inc i) s1'' s2''))))))))

(defn- crc32-table []
  (let [t    (int-array 256)
        poly (long 3988292384)    ;; 0xEDB88320 as positive long
        mask (long 4294967295)]   ;; 0xFFFFFFFF as long
    (dotimes [n 256]
      (aset t n
            (unchecked-int
             (loop [c (long n) k 0]
               (if (= k 8)
                 c
                 (recur (if (odd? c)
                          (bit-xor (unsigned-bit-shift-right c 1) poly)
                          (unsigned-bit-shift-right c 1))
                        (inc k)))))))
    t))

(def ^:private _crc32-table (crc32-table))

(defn crc32
  ([data] (crc32 data 0))
  ([data value]
   (let [mask     (long 4294967295)    ;; 0xFFFFFFFF
         init-crc (bit-xor (bit-and (long value) mask) mask)]
     (bit-and
      (bit-xor
       (loop [i 0 c init-crc]
         (if (= i (alength ^bytes data))
           c
           (let [b   (bit-and (long (aget ^bytes data i)) 0xFF)
                 idx (bit-and (bit-xor c b) 0xFF)
                 tbl (bit-and (long (aget ^ints _crc32-table (int idx))) mask)
                 nxt (bit-xor (unsigned-bit-shift-right c 8) tbl)]
             (recur (inc i) nxt))))
       mask)
      mask))))

(defn compress
  ([data] (compress data Z_DEFAULT_COMPRESSION))
  ([data level]
   (let [d   (if (= level Z_DEFAULT_COMPRESSION)
               (Deflater.)
               (Deflater. (int level)))
         out (ByteArrayOutputStream.)]
     (.setInput d ^bytes data)
     (.finish d)
     (let [buf (byte-array 8192)]
       (loop []
         (when (not (.finished d))
           (let [n (.deflate d buf)]
             (.write out buf 0 n)
             (recur)))))
     (.end d)
     (.toByteArray out))))

(defn decompress
  ([data] (decompress data 15))
  ([data _wbits]
   (let [inf (Inflater.)
         out (ByteArrayOutputStream.)]
     (.setInput inf ^bytes data)
     (let [buf (byte-array 8192)]
       (loop []
         (let [n (.inflate inf buf)]
           (when (pos? n) (.write out buf 0 n))
           (when (not (.finished inf))
             (recur)))))
     (.end inf)
     (.toByteArray out))))

(defn compressobj
  ([] (compressobj Z_DEFAULT_COMPRESSION))
  ([level]
   (let [d (if (= level Z_DEFAULT_COMPRESSION)
             (Deflater.)
             (Deflater. (int level)))]
     {:deflater d})))

(defn compress-chunk [obj data]
  (let [^Deflater d (:deflater obj)
        buf    (byte-array 8192)
        result (ByteArrayOutputStream.)]
    (.setInput d ^bytes data)
    (loop []
      (when (not (.needsInput d))
        (let [n (.deflate d buf (int 0) (int (alength buf)) (int Deflater/NO_FLUSH))]
          (.write result buf 0 n)
          (recur))))
    (.toByteArray result)))

(defn flush-compressor [obj]
  (let [^Deflater d (:deflater obj)
        buf    (byte-array 8192)
        result (ByteArrayOutputStream.)]
    (.finish d)
    (loop []
      (when (not (.finished d))
        (let [n (.deflate d buf)]
          (.write result buf 0 n)
          (recur))))
    (.end d)
    (.toByteArray result)))

(defn decompressobj []
  {:inflater (Inflater.)})

(defn decompress-chunk [obj data]
  (let [^Inflater inf (:inflater obj)
        buf    (byte-array 8192)
        result (ByteArrayOutputStream.)]
    (.setInput inf ^bytes data)
    (loop []
      (let [n (.inflate inf buf)]
        (when (pos? n) (.write result buf 0 n))
        (when (and (not (.finished inf)) (not (.needsInput inf)))
          (recur))))
    (.toByteArray result)))

(defn flush-decompressor [obj]
  (let [^Inflater inf (:inflater obj)
        buf    (byte-array 8192)
        result (ByteArrayOutputStream.)]
    (loop []
      (when (not (.finished inf))
        (let [n (.inflate inf buf)]
          (when (pos? n) (.write result buf 0 n))
          (recur))))
    (.end inf)
    (.toByteArray result)))
