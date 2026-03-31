(ns conao3.battery.hashlib
  (:require [clojure.string :as str])
  (:import [java.security MessageDigest]))

(def algorithms-guaranteed
  #{"md5" "sha1" "sha224" "sha256" "sha384" "sha512"
    "sha3_224" "sha3_256" "sha3_384" "sha3_512"
    "blake2b" "blake2s" "shake_128" "shake_256"})

(def algorithms-available
  (-> (java.security.Security/getAlgorithms "MessageDigest")
      (->> (map str/lower-case))
      set))

(def ^:private py-to-java
  {"md5"      "MD5"
   "sha1"     "SHA-1"
   "sha224"   "SHA-224"
   "sha256"   "SHA-256"
   "sha384"   "SHA-384"
   "sha512"   "SHA-512"
   "sha3_224" "SHA3-224"
   "sha3_256" "SHA3-256"
   "sha3_384" "SHA3-384"
   "sha3_512" "SHA3-512"})

(def ^:private block-sizes
  {"md5"      64
   "sha1"     64
   "sha224"   64
   "sha256"   64
   "sha384"   128
   "sha512"   128
   "sha3_224" 144
   "sha3_256" 136
   "sha3_384" 104
   "sha3_512" 72})

(defn- to-java-name [algo]
  (or (get py-to-java (str/lower-case algo))
      (throw (ex-info (str "unsupported hash type " algo) {:name algo}))))

(defn- build-hash [py-name md]
  {:name        py-name
   :digest-size (.getDigestLength ^MessageDigest md)
   :block-size  (get block-sizes (str/lower-case py-name) 0)
   :_state      (atom md)})

(defn- create-hash
  ([algo] (create-hash algo nil))
  ([algo data]
   (let [h (build-hash (str/lower-case algo)
                       (MessageDigest/getInstance (to-java-name algo)))]
     (when data
       (when (string? data)
         (throw (ex-info "Unicode-objects must be encoded before hashing" {})))
       (.update ^MessageDigest @(:_state h) ^bytes data))
     h)))

(def new create-hash)

(defn update!
  [h data]
  (when (string? data)
    (throw (ex-info "Unicode-objects must be encoded before hashing" {})))
  (.update ^MessageDigest @(:_state h) ^bytes data)
  h)

(defn digest [h]
  (let [cloned (.clone ^MessageDigest @(:_state h))]
    (.digest ^MessageDigest cloned)))

(defn hexdigest [h]
  (->> (digest h)
       (map #(format "%02x" (bit-and % 0xff)))
       (apply str)))

(defn copy [h]
  (let [cloned (.clone ^MessageDigest @(:_state h))]
    (assoc h :_state (atom cloned))))

(defn md5
  ([] (create-hash "md5"))
  ([data] (create-hash "md5" data)))

(defn sha1
  ([] (create-hash "sha1"))
  ([data] (create-hash "sha1" data)))

(defn sha224
  ([] (create-hash "sha224"))
  ([data] (create-hash "sha224" data)))

(defn sha256
  ([] (create-hash "sha256"))
  ([data] (create-hash "sha256" data)))

(defn sha384
  ([] (create-hash "sha384"))
  ([data] (create-hash "sha384" data)))

(defn sha512
  ([] (create-hash "sha512"))
  ([data] (create-hash "sha512" data)))

(defn sha3-224
  ([] (create-hash "sha3_224"))
  ([data] (create-hash "sha3_224" data)))

(defn sha3-256
  ([] (create-hash "sha3_256"))
  ([data] (create-hash "sha3_256" data)))

(defn sha3-384
  ([] (create-hash "sha3_384"))
  ([data] (create-hash "sha3_384" data)))

(defn sha3-512
  ([] (create-hash "sha3_512"))
  ([data] (create-hash "sha3_512" data)))
