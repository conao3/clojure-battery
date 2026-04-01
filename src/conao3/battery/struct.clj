(ns conao3.battery.struct
  (:import [java.nio ByteBuffer ByteOrder]))

(defn- parse-format [fmt]
  (let [s     (str fmt)
        [order rest-s] (if (#{"@" "=" "<" ">" "!"} (str (first s)))
                         [(first s) (subs s 1)]
                         [\@ s])
        byte-order (case order
                     \< ByteOrder/LITTLE_ENDIAN
                     \> ByteOrder/BIG_ENDIAN
                     \! ByteOrder/BIG_ENDIAN
                     (ByteOrder/nativeOrder))]
    (loop [i 0 items []]
      (if (>= i (count rest-s))
        [byte-order items]
        (let [c (nth rest-s i)]
          (if (Character/isDigit c)
            (let [j (loop [j (inc i)]
                      (if (or (>= j (count rest-s))
                              (not (Character/isDigit (nth rest-s j))))
                        j
                        (recur (inc j))))
                  n   (Integer/parseInt (subs rest-s i j))
                  ch  (nth rest-s j)]
              (recur (inc j) (conj items [n ch])))
            (recur (inc i) (conj items [1 c]))))))))

(defn- item-size [ch n]
  (case ch
    \x n
    \c n
    \b n
    \B n
    \? n
    \h (* n 2)
    \H (* n 2)
    \i (* n 4)
    \I (* n 4)
    \l (* n 4)
    \L (* n 4)
    \q (* n 8)
    \Q (* n 8)
    \f (* n 4)
    \d (* n 8)
    \s n
    \p n
    0))

(defn calcsize [fmt]
  (let [[_ items] (parse-format fmt)]
    (reduce (fn [acc [n ch]] (+ acc (item-size ch n))) 0 items)))

(defn- read-item [^ByteBuffer buf ch n]
  (case ch
    \x (do (dotimes [_ n] (.get buf)) nil)
    \c (vec (for [_ (range n)] (byte-array [(.get buf)])))
    \b (vec (for [_ (range n)] (int (.get buf))))
    \B (vec (for [_ (range n)] (bit-and (.get buf) 0xFF)))
    \? (vec (for [_ (range n)] (not (zero? (.get buf)))))
    \h (vec (for [_ (range n)] (int (.getShort buf))))
    \H (vec (for [_ (range n)] (bit-and (.getShort buf) 0xFFFF)))
    \i (vec (for [_ (range n)] (.getInt buf)))
    \I (vec (for [_ (range n)] (bit-and (long (.getInt buf)) (long 0xFFFFFFFF))))
    \l (vec (for [_ (range n)] (.getInt buf)))
    \L (vec (for [_ (range n)] (bit-and (long (.getInt buf)) (long 0xFFFFFFFF))))
    \q (vec (for [_ (range n)] (.getLong buf)))
    \Q (vec (for [_ (range n)] (.getLong buf)))
    \f (vec (for [_ (range n)] (.getFloat buf)))
    \d (vec (for [_ (range n)] (.getDouble buf)))
    \s (let [bs (byte-array n)]
         (.get buf bs)
         [bs])
    \p (let [len (bit-and (.get buf) 0xFF)
             bs  (byte-array (dec n))]
         (.get buf bs)
         [(java.util.Arrays/copyOf bs (min len (dec n)))])
    []))

(defn unpack [fmt data]
  (let [[byte-order items] (parse-format fmt)
        bs  (if (string? data) (.getBytes ^String data "ISO-8859-1") data)
        buf (doto (ByteBuffer/wrap ^bytes bs) (.order byte-order))]
    (->> items
         (mapcat (fn [[n ch]] (read-item buf ch n)))
         (remove nil?)
         vec)))

(defn- write-item [^ByteBuffer buf ch n values]
  (case ch
    \x (dotimes [_ n] (.put buf (byte 0)))
    \c (doseq [v (take n values)]
         (.put buf (aget ^bytes v 0)))
    \b (doseq [v (take n values)]
         (.put buf (unchecked-byte (int v))))
    \B (doseq [v (take n values)]
         (.put buf (unchecked-byte (int v))))
    \? (doseq [v (take n values)]
         (.put buf (byte (if v 1 0))))
    \h (doseq [v (take n values)]
         (.putShort buf (short (int v))))
    \H (doseq [v (take n values)]
         (.putShort buf (unchecked-short (int v))))
    \i (doseq [v (take n values)]
         (.putInt buf (int v)))
    \I (doseq [v (take n values)]
         (.putInt buf (unchecked-int (long v))))
    \l (doseq [v (take n values)]
         (.putInt buf (int v)))
    \L (doseq [v (take n values)]
         (.putInt buf (unchecked-int (long v))))
    \q (doseq [v (take n values)]
         (.putLong buf (long v)))
    \Q (doseq [v (take n values)]
         (.putLong buf (long v)))
    \f (doseq [v (take n values)]
         (.putFloat buf (float v)))
    \d (doseq [v (take n values)]
         (.putDouble buf (double v)))
    \s (let [bs (first values)
             actual (if (bytes? bs) bs (.getBytes ^String bs "ISO-8859-1"))]
         (dotimes [i n]
           (if (< i (alength ^bytes actual))
             (.put buf (aget ^bytes actual i))
             (.put buf (byte 0)))))
    \p (let [bs (first values)
             actual (if (bytes? bs) bs (.getBytes ^String bs "ISO-8859-1"))
             data-len (min (alength ^bytes actual) (dec n))]
         (.put buf (unchecked-byte data-len))
         (dotimes [i (dec n)]
           (if (< i data-len)
             (.put buf (aget ^bytes actual i))
             (.put buf (byte 0)))))))

(defn pack [fmt & values]
  (let [[byte-order items] (parse-format fmt)
        size (calcsize fmt)
        buf  (doto (ByteBuffer/allocate size) (.order byte-order))]
    (loop [items items vals values]
      (when (seq items)
        (let [[n ch] (first items)
              consumed (cond (= ch \x) 0
                        (contains? #{\s \p} ch) 1
                        :else n)]
          (write-item buf ch n vals)
          (recur (rest items) (drop consumed vals)))))
    (.array buf)))

(defn pack-into [fmt buffer offset & values]
  (let [result (apply pack fmt values)]
    (System/arraycopy result 0 buffer offset (alength result))
    nil))
