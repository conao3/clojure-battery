(ns conao3.battery.codecs
  (:import
   [java.nio.charset Charset StandardCharsets CodingErrorAction]
   [java.nio ByteBuffer CharBuffer]
   [java.util Base64]
   [java.util HexFormat]))

(def BOM_UTF8     (byte-array [0xef 0xbb 0xbf]))
(def BOM_UTF16_LE (byte-array [0xff 0xfe]))
(def BOM_UTF16_BE (byte-array [0xfe 0xff]))
(def BOM_UTF32_LE (byte-array [0xff 0xfe 0x00 0x00]))
(def BOM_UTF32_BE (byte-array [0x00 0x00 0xfe 0xff]))
(def BOM_UTF16    BOM_UTF16_LE)
(def BOM_UTF32    BOM_UTF32_LE)
(def BOM          BOM_UTF16)

(defn- normalize-encoding [name]
  (-> name
      (clojure.string/lower-case)
      (clojure.string/replace #"[-_\s]+" "-")
      (clojure.string/replace "utf-8" "UTF-8")
      (clojure.string/replace "utf8" "UTF-8")
      (clojure.string/replace "utf-16" "UTF-16")
      (clojure.string/replace "utf-32" "UTF-32")
      (clojure.string/replace "latin-1" "ISO-8859-1")
      (clojure.string/replace "iso-8859-1" "ISO-8859-1")
      (clojure.string/replace "ascii" "US-ASCII")
      (clojure.string/replace "us-ascii" "US-ASCII")))

(defn- charset-for [encoding]
  (try
    (Charset/forName ^String (normalize-encoding encoding))
    (catch Exception _
      (throw (ex-info (str "unknown encoding: " encoding) {})))))

(defn- java-error-action [errors]
  (case errors
    "strict"  CodingErrorAction/REPORT
    "ignore"  CodingErrorAction/IGNORE
    "replace" CodingErrorAction/REPLACE
    CodingErrorAction/REPORT))

(defrecord CodecInfo [name])

(defn lookup [encoding]
  (let [cs (charset-for encoding)]
    (->CodecInfo (.name cs))))

(defn- rot13-char [c]
  (cond
    (and (>= (int c) (int \a)) (<= (int c) (int \z)))
    (char (+ (int \a) (mod (+ (- (int c) (int \a)) 13) 26)))
    (and (>= (int c) (int \A)) (<= (int c) (int \Z)))
    (char (+ (int \A) (mod (+ (- (int c) (int \A)) 13) 26)))
    :else c))

(defn- rot13 [s]
  (apply str (map rot13-char s)))

(defn- normalize-codec [encoding]
  (-> (clojure.string/lower-case (str encoding))
      (clojure.string/replace #"[-_\s]+" "_")
      (clojure.string/replace #"^hex$" "hex_codec")
      (clojure.string/replace #"^base64$" "base64_codec")
      (clojure.string/replace #"^rot.?13$" "rot_13")))

(defn encode
  ([obj encoding] (encode obj encoding "strict"))
  ([obj encoding errors]
   (let [enc (normalize-codec encoding)]
     (cond
       (= enc "hex_codec") (-> (if (string? obj) (.getBytes ^String obj "ISO-8859-1") obj)
                               ((fn [^bytes bs] (.formatHex (HexFormat/of) bs)))
                               (.getBytes "ASCII"))
       (= enc "base64_codec") (let [bs (if (string? obj) (.getBytes ^String obj "ISO-8859-1") obj)
                                    enc64 (Base64/getEncoder)
                                    b64   (.encode enc64 ^bytes bs)
                                    result (byte-array (inc (alength b64)))]
                                (System/arraycopy b64 0 result 0 (alength b64))
                                (aset result (alength b64) (byte \newline))
                                result)
       (= enc "rot_13") (if (string? obj) (rot13 obj) (rot13 (String. ^bytes obj "ASCII")))
       (string? obj)
       (let [cs    (charset-for encoding)
             act   (java-error-action errors)
             encdr (.newEncoder cs)]
         (.onMalformedInput encdr act)
         (.onUnmappableCharacter encdr act)
         (try
           (let [bb  (.encode encdr (java.nio.CharBuffer/wrap ^String obj))
                 arr (byte-array (.remaining bb))]
             (.get bb arr)
             arr)
           (catch java.nio.charset.CharacterCodingException e
             (throw (ex-info (.getMessage e) {:encoding encoding :errors errors})))))
       (bytes? obj)
       (throw (ex-info "encode requires string input for text encodings" {}))))))

(defn decode
  ([obj encoding] (decode obj encoding "strict"))
  ([obj encoding errors]
   (let [enc (normalize-codec encoding)]
     (cond
       (= enc "hex_codec") (let [s (if (bytes? obj) (String. ^bytes obj "ASCII") (str obj))]
                             (.parseHex (HexFormat/of) ^String s))
       (= enc "base64_codec") (let [s   (if (bytes? obj) (String. ^bytes obj "ASCII") (str obj))
                                    dec (Base64/getMimeDecoder)]
                                (.decode dec ^String s))
       (= enc "rot_13") (if (bytes? obj) (rot13 (String. ^bytes obj "ASCII")) (rot13 (str obj)))
       (bytes? obj)
       (let [cs  (charset-for encoding)
             err (java-error-action errors)
             dec (.newDecoder cs)]
         (.onMalformedInput dec err)
         (.onUnmappableCharacter dec err)
         (try
           (let [bb (ByteBuffer/wrap ^bytes obj)
                 cb (.decode dec bb)]
             (.toString cb))
           (catch java.nio.charset.CharacterCodingException e
             (throw (ex-info (.getMessage e) {:encoding encoding :errors errors})))))
       (string? obj)
       (throw (ex-info "decode requires bytes input for text encodings" {}))))))

(defn getincrementaldecoder [encoding]
  (charset-for encoding))

(defn getincrementalencoder [encoding]
  (charset-for encoding))

(defn- strict-errors [e] (throw e))
(defn- ignore-errors [_] nil)
(defn- replace-errors [_] "?")

(def ^:private error-handlers
  {"strict"  strict-errors
   "ignore"  ignore-errors
   "replace" replace-errors})

(defn lookup-error [name]
  (or (get error-handlers name)
      (throw (ex-info (str "unknown error handler name: " name) {}))))

(defn register-error [name fn]
  (alter-var-root #'error-handlers assoc name fn))
