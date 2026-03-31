;; Original: Lib/test/test_hmac.py

(ns conao3.battery.hmac-test
  (:require
   [clojure.test :as t]
   [conao3.battery.hmac :as hmac]
   [conao3.battery.hashlib :as hashlib])
  (:import
   [clojure.lang ExceptionInfo]
   [java.util Arrays]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- bytes= [a b]
  (Arrays/equals ^bytes a ^bytes b))

(t/deftest test-hmac-md5
  (let [h (hmac/new (b "key")
                    "md5"
                    (b "The quick brown fox jumps over the lazy dog"))]
    (t/is (= "80070713463e7749b90c2dc24911e275" (hmac/hexdigest h)))))

(t/deftest test-hmac-sha1
  (let [h (hmac/new (b "key")
                    "sha1"
                    (b "The quick brown fox jumps over the lazy dog"))]
    (t/is (= "de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9" (hmac/hexdigest h)))))

(t/deftest test-hmac-sha256
  (let [h (hmac/new (b "key")
                    "sha256"
                    (b "The quick brown fox jumps over the lazy dog"))]
    (t/is (= "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
             (hmac/hexdigest h)))))

(t/deftest test-hmac-empty
  (let [h1 (hmac/new (b "") "md5" (b ""))]
    (t/is (= "74e6f7298a9c2d168935f58c001bad88" (hmac/hexdigest h1))))
  (let [h2 (hmac/new (b "") "sha256" (b ""))]
    (t/is (= "b613679a0814d9ec772f95d778c35fc5ff1697c493715653c6c712144292c5ad"
             (hmac/hexdigest h2)))))

(t/deftest test-hmac-with-hashlib-obj
  (let [h (hmac/new (b "key")
                    (hashlib/sha256)
                    (b "The quick brown fox jumps over the lazy dog"))]
    (t/is (= "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8"
             (hmac/hexdigest h)))))

(t/deftest test-update
  (let [h1 (hmac/new (b "key") "sha256" (b "The quick brown fox jumps over the lazy dog"))
        h2 (hmac/new (b "key") "sha256")]
    (hmac/update! h2 (b "The quick brown fox jumps over the lazy dog"))
    (t/is (= (hmac/hexdigest h1) (hmac/hexdigest h2)))))

(t/deftest test-copy
  (let [h1 (hmac/new (b "key") "sha256" (b "abc"))
        h2 (hmac/copy h1)]
    (t/is (= (hmac/hexdigest h1) (hmac/hexdigest h2)))
    (hmac/update! h1 (b "more"))
    (t/is (not= (hmac/hexdigest h1) (hmac/hexdigest h2)))))

(t/deftest test-digest
  (let [h (hmac/new (b "key") "sha256" (b "The quick brown fox jumps over the lazy dog"))
        d (hmac/digest h)]
    (t/is (= 32 (alength d)))
    (t/is (= (hmac/hexdigest h)
             (apply str (map #(format "%02x" (bit-and % 0xff)) d))))))

(t/deftest test-digest-idempotent
  (let [h (hmac/new (b "key") "sha256" (b "abc"))]
    (t/is (= (hmac/hexdigest h) (hmac/hexdigest h)))
    (t/is (bytes= (hmac/digest h) (hmac/digest h)))))

(t/deftest test-digest-size
  (t/is (= 16 (:digest-size (hmac/new (b "k") "md5"))))
  (t/is (= 20 (:digest-size (hmac/new (b "k") "sha1"))))
  (t/is (= 32 (:digest-size (hmac/new (b "k") "sha256"))))
  (t/is (= 64 (:digest-size (hmac/new (b "k") "sha512")))))

(t/deftest test-compare-digest-bytes
  (t/is (hmac/compare-digest (b "abc") (b "abc")))
  (t/is (not (hmac/compare-digest (b "abc") (b "abd"))))
  (t/is (not (hmac/compare-digest (b "abc") (b "ab"))))
  (t/is (hmac/compare-digest (b "") (b ""))))

(t/deftest test-compare-digest-strings
  (t/is (hmac/compare-digest "abc" "abc"))
  (t/is (not (hmac/compare-digest "abc" "abd")))
  (t/is (not (hmac/compare-digest "abc" "ab")))
  (t/is (hmac/compare-digest "" "")))

(t/deftest test-key-must-be-bytes
  (t/is (thrown? ExceptionInfo (hmac/new "key" "sha256"))))

(t/deftest test-msg-must-be-bytes
  (t/is (thrown? ExceptionInfo (hmac/new (b "key") "sha256" "msg")))
  (let [h (hmac/new (b "key") "sha256")]
    (t/is (thrown? ExceptionInfo (hmac/update! h "msg")))))

(t/deftest test-hmac-sha512
  (let [h (hmac/new (b "key") "sha512" (b "The quick brown fox jumps over the lazy dog"))]
    (t/is (= 128 (count (hmac/hexdigest h))))
    (t/is (= 64 (:digest-size h)))))

(t/deftest test-hmac-new-without-msg
  (let [h1 (hmac/new (b "key") "sha256")
        h2 (hmac/new (b "key") "sha256" (b "hello"))]
    (hmac/update! h1 (b "hello"))
    (t/is (= (hmac/hexdigest h1) (hmac/hexdigest h2)))))

(t/deftest test-compare-digest-timing-safe
  (t/is (hmac/compare-digest "secret" "secret"))
  (t/is (not (hmac/compare-digest "secret" "Secret")))
  (t/is (not (hmac/compare-digest "abc" "abcd"))))

(t/deftest test-digest-size-sha1
  (t/is (= 20 (:digest-size (hmac/new (b "k") "sha1"))))
  (t/is (= 20 (alength (hmac/digest (hmac/new (b "k") "sha1" (b "msg")))))))
