;; Original: Lib/test/test_codecs.py

(ns conao3.battery.codecs-test
  (:require
   [clojure.test :as t]
   [conao3.battery.codecs :as codecs-m])
  (:import
   [java.util Arrays]))

(defn- bytes= [a b]
  (Arrays/equals ^bytes a ^bytes b))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(t/deftest test-encode-utf8
  (let [result (codecs-m/encode "hello" "utf-8")]
    (t/is (bytes= (b "hello") result))))

(t/deftest test-decode-utf8
  (t/is (= "hello" (codecs-m/decode (b "hello") "utf-8"))))

(t/deftest test-encode-ascii
  (t/is (bytes= (b "hello") (codecs-m/encode "hello" "ascii"))))

(t/deftest test-decode-ascii
  (t/is (= "hello" (codecs-m/decode (b "hello") "ascii"))))

(t/deftest test-encode-latin1
  (let [s "caf\u00e9"
        result (codecs-m/encode s "latin-1")]
    (t/is (bytes= (b "caf\u00e9") result))))

(t/deftest test-decode-latin1
  (t/is (= "caf\u00e9" (codecs-m/decode (b "caf\u00e9") "latin-1"))))

(t/deftest test-encode-hex
  (t/is (bytes= (b "68656c6c6f") (codecs-m/encode (b "hello") "hex"))))

(t/deftest test-decode-hex
  (t/is (bytes= (b "hello") (codecs-m/decode (b "68656c6c6f") "hex"))))

(t/deftest test-encode-base64
  (let [result (codecs-m/encode (b "hello") "base64")]
    (t/is (bytes= (b "aGVsbG8=\n") result))))

(t/deftest test-decode-base64
  (t/is (bytes= (b "hello") (codecs-m/decode (b "aGVsbG8=") "base64"))))

(t/deftest test-encode-rot13
  (t/is (= "uryyb" (codecs-m/encode "hello" "rot_13"))))

(t/deftest test-decode-rot13
  (t/is (= "hello" (codecs-m/decode "uryyb" "rot_13"))))

(t/deftest test-encode-ascii-error
  (t/is (thrown? clojure.lang.ExceptionInfo (codecs-m/encode "caf\u00e9" "ascii" "strict"))))

(t/deftest test-encode-ascii-ignore
  (t/is (bytes= (b "caf") (codecs-m/encode "caf\u00e9" "ascii" "ignore"))))

(t/deftest test-encode-ascii-replace
  (t/is (bytes= (b "caf?") (codecs-m/encode "caf\u00e9" "ascii" "replace"))))

(t/deftest test-lookup-utf8
  (let [info (codecs-m/lookup "utf-8")]
    (t/is (= "UTF-8" (:name info)))))

(t/deftest test-lookup-normalization
  (t/is (= "UTF-8" (:name (codecs-m/lookup "utf8"))))
  (t/is (= "UTF-8" (:name (codecs-m/lookup "UTF-8")))))

(t/deftest test-bom-constants
  (t/is (bytes= (byte-array [0xef 0xbb 0xbf]) codecs-m/BOM_UTF8))
  (t/is (bytes= (byte-array [0xff 0xfe]) codecs-m/BOM_UTF16_LE))
  (t/is (bytes= (byte-array [0xfe 0xff]) codecs-m/BOM_UTF16_BE)))

(t/deftest test-lookup-error-strict
  (t/is (fn? (codecs-m/lookup-error "strict"))))

(t/deftest test-lookup-error-ignore
  (t/is (fn? (codecs-m/lookup-error "ignore"))))

(t/deftest test-lookup-error-unknown
  (t/is (thrown? clojure.lang.ExceptionInfo (codecs-m/lookup-error "unknown_handler"))))
