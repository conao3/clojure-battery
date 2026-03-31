;; Original: Lib/test/test_binascii.py

(ns conao3.battery.binascii-test
  (:require
   [clojure.test :as t]
   [conao3.battery.binascii :as binascii-m])
  (:import
   [java.util Arrays]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- bytes= [a b]
  (Arrays/equals ^bytes a ^bytes b))

(t/deftest test-hexlify-basic
  (t/is (bytes= (b "68656c6c6f") (binascii-m/hexlify (b "hello"))))
  (t/is (bytes= (b "68656c6c6f20776f726c64") (binascii-m/hexlify (b "hello world"))))
  (t/is (bytes= (b "") (binascii-m/hexlify (b "")))))

(t/deftest test-hexlify-b2a-hex-alias
  (t/is (bytes= (binascii-m/hexlify (b "hello")) (binascii-m/b2a-hex (b "hello")))))

(t/deftest test-hexlify-with-sep
  (t/is (bytes= (b "68:65:6c:6c:6f") (binascii-m/hexlify (b "hello") (b ":"))))
  (t/is (bytes= (b "68:65:6c:6c:6f") (binascii-m/hexlify (b "hello") (b ":") 1)))
  (t/is (bytes= (b "68:656c:6c6f") (binascii-m/hexlify (b "hello") (b ":") 2)))
  (t/is (bytes= (b "6865:6c6c6f") (binascii-m/hexlify (b "hello") (b ":") 3)))
  (t/is (bytes= (b "6865:6c6c:6f") (binascii-m/hexlify (b "hello") (b ":") -2)))
  (t/is (bytes= (b "68656c:6c6f") (binascii-m/hexlify (b "hello") (b ":") -3))))

(t/deftest test-hexlify-sep-even
  (t/is (bytes= (b "666f:6f62:6172") (binascii-m/hexlify (b "foobar") (b ":") 2)))
  (t/is (bytes= (b "666f:6f62:6172") (binascii-m/hexlify (b "foobar") (b ":") -2))))

(t/deftest test-unhexlify-basic
  (t/is (bytes= (b "hello") (binascii-m/unhexlify "68656c6c6f")))
  (t/is (bytes= (b "hello world") (binascii-m/unhexlify "68656c6c6f20776f726c64")))
  (t/is (bytes= (b "") (binascii-m/unhexlify ""))))

(t/deftest test-unhexlify-a2b-hex-alias
  (t/is (bytes= (binascii-m/unhexlify "68656c6c6f") (binascii-m/a2b-hex "68656c6c6f"))))

(t/deftest test-unhexlify-from-bytes
  (t/is (bytes= (b "hello") (binascii-m/unhexlify (b "68656c6c6f")))))

(t/deftest test-unhexlify-error-odd-length
  (t/is (thrown? clojure.lang.ExceptionInfo (binascii-m/unhexlify "xyz"))))

(t/deftest test-unhexlify-error-invalid-char
  (t/is (thrown? clojure.lang.ExceptionInfo (binascii-m/unhexlify "zz"))))

(t/deftest test-b2a-base64-basic
  (t/is (bytes= (b "aGVsbG8gd29ybGQ=\n") (binascii-m/b2a-base64 (b "hello world"))))
  (t/is (bytes= (b "\n") (binascii-m/b2a-base64 (b "")))))

(t/deftest test-b2a-base64-no-newline
  (t/is (bytes= (b "aGVsbG8gd29ybGQ=") (binascii-m/b2a-base64 (b "hello world") false))))

(t/deftest test-a2b-base64-basic
  (t/is (bytes= (b "hello world") (binascii-m/a2b-base64 "aGVsbG8gd29ybGQ=")))
  (t/is (bytes= (b "") (binascii-m/a2b-base64 ""))))

(t/deftest test-a2b-base64-with-newline
  (t/is (bytes= (b "hello world") (binascii-m/a2b-base64 "aGVsbG8gd29ybGQ=\n"))))

(t/deftest test-a2b-base64-from-bytes
  (t/is (bytes= (b "hello world") (binascii-m/a2b-base64 (b "aGVsbG8gd29ybGQ=")))))

(t/deftest test-crc32-basic
  (t/is (= 0 (binascii-m/crc32 (b ""))))
  (t/is (= 907060870 (binascii-m/crc32 (b "hello"))))
  (t/is (= 222957957 (binascii-m/crc32 (b "hello world")))))

(t/deftest test-crc32-continuation
  (let [partial (binascii-m/crc32 (b "hello"))
        result  (binascii-m/crc32 (b " world") partial)]
    (t/is (= 222957957 result))))

(t/deftest test-crc32-max-value
  (t/is (= 4294967295 (binascii-m/crc32 (byte-array [0xff 0xff 0xff 0xff])))))

(t/deftest test-crc-hqx-basic
  (t/is (= 50018 (binascii-m/crc-hqx (b "hello") 0)))
  (t/is (= 15332 (binascii-m/crc-hqx (b "hello world") 0))))

(t/deftest test-crc-hqx-continuation
  (let [partial (binascii-m/crc-hqx (b "hello") 0)
        result  (binascii-m/crc-hqx (b " world") partial)]
    (t/is (= 15332 result))))

(t/deftest test-roundtrip-hex
  (let [data (byte-array (concat (map byte "binary ") [0] (map byte " data ") [-1] (map byte " test")))]
    (t/is (bytes= data (binascii-m/unhexlify (binascii-m/hexlify data))))))

(t/deftest test-roundtrip-base64
  (let [data (byte-array (concat (map byte "binary ") [0] (map byte " data ") [-1] (map byte " test")))]
    (t/is (bytes= data (binascii-m/a2b-base64 (binascii-m/b2a-base64 data false))))))
