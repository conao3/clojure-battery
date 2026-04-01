(ns conao3.battery.base64
  (:import [java.util Arrays Base64]
           [java.io ByteArrayOutputStream]))

(def ^:private b32-alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567")

(def ^:private b32-decode-table
  (let [t (int-array 256 -1)]
    (doseq [[i c] (map-indexed vector b32-alphabet)]
      (aset t (int c) i))
    t))

(defn- to-bytes [s]
  (if (string? s)
    (.getBytes s "ISO-8859-1")
    s))

(defn- byte-array? [x]
  (instance? (Class/forName "[B") x))

(defn- strip-whitespace [data]
  (->> (seq data)
       (remove #(let [b (bit-and % 0xff)]
                  (or (= b 9) (= b 10) (= b 11) (= b 12) (= b 13) (= b 32))))
       byte-array))

(def ^:private b64-valid-set
  (set (map byte "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/")))

(def ^:private b64-set-with-pad
  (conj b64-valid-set (byte \=)))

(defn- normalize-b64 [data]
  "Normalize base64 data: discard non-base64 chars, keep = only at valid padding positions.
   Returns normalized byte-array or throws on invalid length."
  (let [result (java.util.ArrayList.)]
    (doseq [b (seq data)]
      (cond
        (contains? b64-valid-set b)
        (.add result b)

        (= b (byte \=))
        ;; = is valid only at positions 2 or 3 within a 4-char group
        (let [pos (mod (.size result) 4)]
          (when (>= pos 2)
            (.add result b)))

        ;; else: discard non-base64 character
        ))
    (let [n (.size result)
          rem (mod n 4)]
      (cond
        (= rem 1)
        (throw (ex-info (str "Invalid base64-encoded string: number of data characters ("
                             n ") cannot be 1 more than a multiple of 4") {}))
        (> rem 1)
        (throw (ex-info "Incorrect padding" {}))
        :else
        (byte-array (map identity result))))))

(defn- apply-altchars-decode [data alt]
  (let [r (aclone data)]
    (dotimes [i (alength r)]
      (let [b (aget r i)]
        (cond
          (= b (aget alt 0)) (aset r i (byte \+))
          (= b (aget alt 1)) (aset r i (byte \/)))))
    r))

(defn b64encode
  [s & {:keys [altchars]}]
  (when (string? s)
    (throw (ex-info "expected bytes-like object, not str" {})))
  (let [encoded (-> (Base64/getEncoder) (.encode s))]
    (if altchars
      (let [alt (to-bytes altchars)
            result (aclone encoded)]
        (dotimes [i (alength result)]
          (let [b (aget result i)]
            (cond
              (= b (byte \+)) (aset result i (aget alt 0))
              (= b (byte \/)) (aset result i (aget alt 1)))))
        result)
      encoded)))

(defn b64decode
  [s & {:keys [altchars validate ignorechars]}]
  (when (and (not (byte-array? s)) (not (string? s)))
    (throw (ex-info "expected bytes-like object" {})))
  (when altchars
    (let [alt (to-bytes altchars)]
      (when (not= (alength alt) 2)
        (throw (ex-info "altchars must be exactly 2 bytes" {})))))
  (let [data (to-bytes s)
        alt (when altchars (to-bytes altchars))]
    (cond
      ;; ignorechars=b'' means strict mode (same as validate=true)
      (and ignorechars (zero? (alength (to-bytes ignorechars))))
      (let [normalized (if alt (apply-altchars-decode data alt) data)]
        (try
          (-> (Base64/getDecoder) (.decode normalized))
          (catch IllegalArgumentException e
            (throw (ex-info (.getMessage e) {})))))

      ignorechars
      (let [ignset (set (seq (to-bytes ignorechars)))
            after-ignore (->> (seq data)
                              (remove #(contains? ignset %))
                              byte-array)
            after-alt (if alt (apply-altchars-decode after-ignore alt) after-ignore)
            normalized (normalize-b64 after-alt)]
        (try
          (-> (Base64/getDecoder) (.decode normalized))
          (catch IllegalArgumentException e
            (throw (ex-info (.getMessage e) {})))))

      validate
      (let [normalized (if alt (apply-altchars-decode data alt) data)]
        (try
          (-> (Base64/getDecoder) (.decode normalized))
          (catch IllegalArgumentException e
            (throw (ex-info (.getMessage e) {})))))

      :else
      ;; Default: discard non-base64 chars, keep = only at valid padding positions
      (let [pre-alt (if alt (apply-altchars-decode data alt) data)
            normalized (normalize-b64 pre-alt)]
        (try
          (-> (Base64/getDecoder) (.decode normalized))
          (catch IllegalArgumentException e
            (throw (ex-info (.getMessage e) {}))))))))

(defn standard-b64encode [s]
  (b64encode s))

(defn standard-b64decode [s]
  (b64decode s))

(defn urlsafe-b64encode [s]
  (when (string? s)
    (throw (ex-info "expected bytes-like object, not str" {})))
  (-> (Base64/getUrlEncoder) (.encode s)))

(def ^:private b64url-valid-set
  (set (map byte "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_")))

(defn- normalize-b64url [data]
  "Normalize URL-safe base64: discard non-alphabet chars, keep = only at valid positions."
  (let [result (java.util.ArrayList.)]
    (doseq [b (seq data)]
      (cond
        (contains? b64url-valid-set b)
        (.add result b)

        (= b (byte \=))
        (let [pos (mod (.size result) 4)]
          (when (>= pos 2)
            (.add result b)))))
    (let [n (.size result)
          rem (mod n 4)]
      (cond
        (= rem 1)
        (throw (ex-info (str "Invalid base64-encoded string: number of data characters ("
                             n ") cannot be 1 more than a multiple of 4") {}))
        (> rem 1)
        (throw (ex-info "Incorrect padding" {}))
        :else
        (byte-array (map identity result))))))

(defn urlsafe-b64decode [s]
  (when (and (not (byte-array? s)) (not (string? s)))
    (throw (ex-info "expected bytes-like object" {})))
  (let [normalized (normalize-b64url (to-bytes s))]
    (try
      (-> (Base64/getUrlDecoder) (.decode normalized))
      (catch IllegalArgumentException e
        (throw (ex-info (.getMessage e) {}))))))

(defn b16encode [s]
  (when (string? s)
    (throw (ex-info "expected bytes-like object, not str" {})))
  (->> (seq s)
       (map #(format "%02X" (bit-and % 0xff)))
       (apply str)
       (#(.getBytes % "ISO-8859-1"))))

(defn b16decode
  [s & {:keys [casefold]}]
  (when (and (not (byte-array? s)) (not (string? s)))
    (throw (ex-info "expected bytes-like object" {})))
  (let [str-data (if (string? s) s (String. s "ISO-8859-1"))
        normalized (if casefold (.toUpperCase str-data) str-data)]
    (when (odd? (count normalized))
      (throw (ex-info "Odd-length string" {})))
    (when-not (re-matches #"[0-9A-F]*" normalized)
      (throw (ex-info "Non-hexadecimal digit found" {})))
    (->> (partition 2 normalized)
         (map #(unchecked-byte (Integer/parseInt (apply str %) 16)))
         byte-array)))

(defn- b32-encode-group [bits n-bytes]
  (let [pad-count (case n-bytes 1 6 2 4 3 3 4 1 5 0)]
    (str (->> (range (- 8 pad-count))
              (map (fn [i]
                     (let [shift (* (- 7 i) 5)
                           idx (bit-and (bit-shift-right bits shift) 0x1f)]
                       (nth b32-alphabet idx))))
              (apply str))
         (apply str (repeat pad-count \=)))))

(defn b32encode [s]
  (when (string? s)
    (throw (ex-info "expected bytes-like object, not str" {})))
  (if (zero? (alength s))
    (byte-array 0)
    (->> (partition-all 5 (seq s))
         (map (fn [group]
                (let [n (count group)
                      padded (concat group (repeat (- 5 n) 0))
                      [b0 b1 b2 b3 b4] (map #(bit-and % 0xff) padded)
                      bits (-> (long b0)
                               (bit-shift-left 32)
                               (bit-or (bit-shift-left (long b1) 24))
                               (bit-or (bit-shift-left (long b2) 16))
                               (bit-or (bit-shift-left (long b3) 8))
                               (bit-or (long b4)))]
                  (b32-encode-group bits n))))
         (apply str)
         (#(.getBytes % "ISO-8859-1")))))

(defn b32decode
  [s & {:keys [casefold map01]}]
  (when (and (not (byte-array? s)) (not (string? s)))
    (throw (ex-info "expected bytes-like object" {})))
  (let [str-data (if (string? s) s (String. s "ISO-8859-1"))
        map01-char (when map01
                     (if (string? map01)
                       (first map01)
                       (char (bit-and (aget map01 0) 0xff))))
        normalized (cond-> str-data
                     casefold (.toUpperCase)
                     map01 (-> (.replace \0 \O)
                               (.replace \1 map01-char)))]
    (when-not (zero? (mod (count normalized) 8))
      (throw (ex-info "Incorrect padding" {})))
    (if (zero? (count normalized))
      (byte-array 0)
      (do
        (doseq [c normalized]
          (when (and (not= c \=)
                     (neg? (aget b32-decode-table (int c))))
            (throw (ex-info (str "Non-base32 digit found: " c) {}))))
        (let [groups (partition 8 normalized)
              result (->> groups
                          (mapcat (fn [group]
                                    (let [padding (count (filter #(= % \=) group))
                                          n-bytes (case padding
                                                    0 5
                                                    1 4
                                                    3 3
                                                    4 2
                                                    6 1
                                                    (throw (ex-info "Incorrect padding" {})))
                                          valid-chars (take (- 8 padding) group)
                                          indices (concat (map #(aget b32-decode-table (int %)) valid-chars)
                                                          (repeat padding 0))
                                          [i0 i1 i2 i3 i4 i5 i6 i7] indices
                                          bits (-> (long i0)
                                                   (bit-shift-left 35)
                                                   (bit-or (bit-shift-left (long i1) 30))
                                                   (bit-or (bit-shift-left (long i2) 25))
                                                   (bit-or (bit-shift-left (long i3) 20))
                                                   (bit-or (bit-shift-left (long i4) 15))
                                                   (bit-or (bit-shift-left (long i5) 10))
                                                   (bit-or (bit-shift-left (long i6) 5))
                                                   (bit-or (long i7)))]
                                      (take n-bytes
                                            [(unchecked-byte (bit-shift-right bits 32))
                                             (unchecked-byte (bit-shift-right bits 24))
                                             (unchecked-byte (bit-shift-right bits 16))
                                             (unchecked-byte (bit-shift-right bits 8))
                                             (unchecked-byte bits)])))))]
          (byte-array result))))))

(defn encodebytes [s]
  (when (string? s)
    (throw (ex-info "expected bytes-like object, not str" {})))
  (if (zero? (alength s))
    (byte-array 0)
    (let [encoded (-> (Base64/getEncoder) (.encode s))
          line-len 76
          out (ByteArrayOutputStream.)]
      (loop [i 0]
        (when (< i (alength encoded))
          (let [end (min (+ i line-len) (alength encoded))]
            (.write out encoded i (- end i))
            (.write out (int \newline))
            (recur end))))
      (.toByteArray out))))

(defn decodebytes [s]
  (when (string? s)
    (throw (ex-info "expected bytes-like object, not str" {})))
  (b64decode s))

(defn encode [input output]
  (let [buf (byte-array 57)]
    (loop []
      (let [n (.read input buf)]
        (when (pos? n)
          (let [chunk (Arrays/copyOfRange buf 0 n)
                encoded (-> (Base64/getEncoder) (.encode chunk))
                line-len 76]
            (loop [i 0]
              (when (< i (alength encoded))
                (let [end (min (+ i line-len) (alength encoded))]
                  (.write output encoded i (- end i))
                  (.write output (int \newline))
                  (recur end)))))
          (recur))))))

(defn decode [input output]
  (let [baos (ByteArrayOutputStream.)]
    (let [buf (byte-array 4096)]
      (loop []
        (let [n (.read input buf)]
          (when (pos? n)
            (.write baos buf 0 n)
            (recur)))))
    (let [decoded (b64decode (.toByteArray baos))]
      (.write output decoded))))
