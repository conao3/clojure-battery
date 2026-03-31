(ns conao3.battery.hmac
  (:require [clojure.string :as str])
  (:import [javax.crypto Mac]
           [javax.crypto.spec SecretKeySpec]))

(def ^:private digestmod-to-java
  {"md5"    "HmacMD5"
   "sha1"   "HmacSHA1"
   "sha224" "HmacSHA224"
   "sha256" "HmacSHA256"
   "sha384" "HmacSHA384"
   "sha512" "HmacSHA512"})

(defn- resolve-digestmod [digestmod]
  (cond
    (string? digestmod)
    (or (get digestmod-to-java (str/lower-case digestmod))
        (throw (ex-info (str "unsupported digestmod: " digestmod) {:digestmod digestmod})))

    (keyword? digestmod)
    (or (get digestmod-to-java (str/lower-case (name digestmod)))
        (throw (ex-info (str "unsupported digestmod: " digestmod) {:digestmod digestmod})))

    (map? digestmod)
    (or (get digestmod-to-java (str/lower-case (:name digestmod "")))
        (throw (ex-info (str "unsupported digestmod: " (:name digestmod)) {:digestmod digestmod})))

    :else
    (throw (ex-info "digestmod must be a string, keyword, or hash object" {:digestmod digestmod}))))

(defn- make-mac [key digestmod]
  (when (string? key)
    (throw (ex-info "key must be bytes, not str" {:key key})))
  (let [algo      (resolve-digestmod digestmod)
        mac       (Mac/getInstance algo)
        safe-key  (if (zero? (alength ^bytes key)) (byte-array [0]) key)
        key-obj   (SecretKeySpec. ^bytes safe-key ^String (subs algo 4))]
    (.init mac key-obj)
    mac))

(defn- build-hmac [key digestmod mac]
  {:key       key
   :digestmod digestmod
   :_mac      (atom mac)
   :digest-size (.getMacLength ^Mac mac)})

(defn- create-hmac
  ([key digestmod] (create-hmac key digestmod nil))
  ([key digestmod msg]
   (let [mac (make-mac key digestmod)
         h   (build-hmac key digestmod mac)]
     (when msg
       (when (string? msg)
         (throw (ex-info "msg must be bytes, not str" {:msg msg})))
       (.update ^Mac @(:_mac h) ^bytes msg))
     h)))

(def new create-hmac)

(defn update!
  [h msg]
  (when (string? msg)
    (throw (ex-info "msg must be bytes, not str" {:msg msg})))
  (.update ^Mac @(:_mac h) ^bytes msg)
  h)

(defn digest [h]
  (let [cloned (.clone ^Mac @(:_mac h))]
    (.doFinal ^Mac cloned)))

(defn hexdigest [h]
  (->> (digest h)
       (map #(format "%02x" (bit-and % 0xff)))
       (apply str)))

(defn copy [h]
  (let [cloned (.clone ^Mac @(:_mac h))]
    (assoc h :_mac (atom cloned))))

(defn compare-digest [a b]
  (let [ba (if (string? a) (.getBytes ^String a "ISO-8859-1") a)
        bb (if (string? b) (.getBytes ^String b "ISO-8859-1") b)]
    (if (not= (alength ^bytes ba) (alength ^bytes bb))
      false
      (let [result (atom 0)]
        (dotimes [i (alength ^bytes ba)]
          (swap! result bit-or (bit-xor (aget ^bytes ba i) (aget ^bytes bb i))))
        (zero? @result)))))
