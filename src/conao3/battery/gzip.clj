(ns conao3.battery.gzip
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]
           [java.util.zip CRC32 Deflater GZIPInputStream GZIPOutputStream]))

(defn- gzip-compress-with-level [^bytes data level]
  (let [d   (Deflater. (int level) true)
        out (ByteArrayOutputStream.)
        crc (CRC32.)]
    (.update crc data)
    (let [crc-val (.getValue crc)
          size    (alength data)
          header  (byte-array [0x1f -0x75 0x08 0x00 0x00 0x00 0x00 0x00 0x00 -0x01])]
      (aset header 1 (unchecked-byte 0x8b))
      (aset header 9 (unchecked-byte 0xff))
      (.write out header)
      (.setInput d data)
      (.finish d)
      (let [buf (byte-array 8192)]
        (loop []
          (when (not (.finished d))
            (let [n (.deflate d buf)]
              (.write out buf 0 n)
              (recur)))))
      (.end d)
      (let [trailer (byte-array 8)]
        (aset trailer 0 (unchecked-byte (bit-and crc-val 0xff)))
        (aset trailer 1 (unchecked-byte (bit-and (bit-shift-right crc-val 8) 0xff)))
        (aset trailer 2 (unchecked-byte (bit-and (bit-shift-right crc-val 16) 0xff)))
        (aset trailer 3 (unchecked-byte (bit-and (bit-shift-right crc-val 24) 0xff)))
        (aset trailer 4 (unchecked-byte (bit-and size 0xff)))
        (aset trailer 5 (unchecked-byte (bit-and (bit-shift-right size 8) 0xff)))
        (aset trailer 6 (unchecked-byte (bit-and (bit-shift-right size 16) 0xff)))
        (aset trailer 7 (unchecked-byte (bit-and (bit-shift-right size 24) 0xff)))
        (.write out trailer))
      (.toByteArray out))))

(defn compress
  ([data] (compress data 9))
  ([data compresslevel]
   (gzip-compress-with-level data compresslevel)))

(defn decompress [data]
  (let [in  (ByteArrayInputStream. ^bytes data)
        out (ByteArrayOutputStream.)]
    (with-open [gz (GZIPInputStream. in)]
      (let [buf (byte-array 8192)]
        (loop []
          (let [n (.read gz buf)]
            (when (pos? n)
              (.write out buf 0 n)
              (recur))))))
    (.toByteArray out)))
