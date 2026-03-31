;; Original: Lib/test/test_hashlib.py

(ns conao3.battery.hashlib-test
  (:require
   [clojure.test :as t]
   [conao3.battery.hashlib :as hashlib])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- check [algo data expected-hex]
  (let [h (hashlib/new algo data)]
    (t/is (= expected-hex (hashlib/hexdigest h)))))

(t/deftest test-case-md5-0
  (check "md5" (b "") "d41d8cd98f00b204e9800998ecf8427e"))

(t/deftest test-case-md5-1
  (check "md5" (b "abc") "900150983cd24fb0d6963f7d28e17f72"))

(t/deftest test-case-md5-2
  (check "md5"
         (b "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")
         "d174ab98d277d9f5a5611c2c9f419d9f"))

(t/deftest test-case-sha1-0
  (check "sha1" (b "") "da39a3ee5e6b4b0d3255bfef95601890afd80709"))

(t/deftest test-case-sha1-1
  (check "sha1" (b "abc") "a9993e364706816aba3e25717850c26c9cd0d89d"))

(t/deftest test-case-sha1-2
  (check "sha1"
         (b "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq")
         "84983e441c3bd26ebaae4aa1f95129e5e54670f1"))

(t/deftest test-case-sha256-0
  (check "sha256" (b "")
         "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"))

(t/deftest test-case-sha256-1
  (check "sha256" (b "abc")
         "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"))

(t/deftest test-case-sha256-2
  (check "sha256"
         (b "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq")
         "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1"))

(t/deftest test-case-sha224-0
  (check "sha224" (b "") "d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f"))

(t/deftest test-case-sha224-1
  (check "sha224" (b "abc") "23097d223405d8228642a477bda255b32aadbce4bda0b3f7e36c9da7"))

(t/deftest test-case-sha384-0
  (check "sha384" (b "")
         "38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b"))

(t/deftest test-case-sha384-1
  (check "sha384" (b "abc")
         "cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7"))

(t/deftest test-case-sha512-0
  (check "sha512" (b "")
         "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"))

(t/deftest test-case-sha512-1
  (check "sha512" (b "abc")
         "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"))

(t/deftest test-case-sha3-256-0
  (check "sha3_256" (b "")
         "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a"))

(t/deftest test-case-sha3-256-1
  (check "sha3_256" (b "abc")
         "3a985da74fe225b2045c172d6bd390bd855f086e3e9d525b46bfe24511431532"))

(t/deftest test-update
  (let [h (hashlib/sha256)]
    (hashlib/update! h (b "abc"))
    (t/is (= "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
             (hashlib/hexdigest h))))
  (let [h (hashlib/sha256)]
    (hashlib/update! h (b "ab"))
    (hashlib/update! h (b "c"))
    (t/is (= "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
             (hashlib/hexdigest h)))))

(t/deftest test-copy
  (let [h1 (hashlib/sha256 (b "abc"))
        h2 (hashlib/copy h1)]
    (t/is (= (:name h1) (:name h2)))
    (t/is (java.util.Arrays/equals (hashlib/digest h1) (hashlib/digest h2)))
    (t/is (= (hashlib/hexdigest h1) (hashlib/hexdigest h2)))
    (hashlib/update! h1 (b "more"))
    (t/is (not= (hashlib/hexdigest h1) (hashlib/hexdigest h2)))))

(t/deftest test-digest-vs-hexdigest
  (let [h (hashlib/md5 (b "abc"))
        d (hashlib/digest h)
        hd (hashlib/hexdigest h)]
    (t/is (= (alength d) 16))
    (t/is (= (count hd) 32))
    (t/is (= hd (apply str (map #(format "%02x" (bit-and % 0xff)) d))))))

(t/deftest test-name-attribute
  (t/is (= "md5"      (:name (hashlib/md5))))
  (t/is (= "sha1"     (:name (hashlib/sha1))))
  (t/is (= "sha256"   (:name (hashlib/sha256))))
  (t/is (= "sha3_256" (:name (hashlib/sha3-256)))))

(t/deftest test-digest-size
  (t/is (= 16 (:digest-size (hashlib/md5))))
  (t/is (= 20 (:digest-size (hashlib/sha1))))
  (t/is (= 28 (:digest-size (hashlib/sha224))))
  (t/is (= 32 (:digest-size (hashlib/sha256))))
  (t/is (= 48 (:digest-size (hashlib/sha384))))
  (t/is (= 64 (:digest-size (hashlib/sha512)))))

(t/deftest test-block-size
  (t/is (= 64  (:block-size (hashlib/md5))))
  (t/is (= 64  (:block-size (hashlib/sha1))))
  (t/is (= 64  (:block-size (hashlib/sha256))))
  (t/is (= 128 (:block-size (hashlib/sha512))))
  (t/is (= 136 (:block-size (hashlib/sha3-256)))))

(t/deftest test-no-unicode
  (doseq [algo ["md5" "sha1" "sha224" "sha256" "sha384" "sha512"]]
    (t/is (thrown? ExceptionInfo (hashlib/new algo "abc")))
    (t/is (thrown? ExceptionInfo (hashlib/update! (hashlib/new algo) "abc")))))

(t/deftest test-unknown-hash
  (t/is (thrown? ExceptionInfo (hashlib/new "unknown_hash_algorithm"))))

(t/deftest test-algorithms-guaranteed
  (t/is (contains? hashlib/algorithms-guaranteed "md5"))
  (t/is (contains? hashlib/algorithms-guaranteed "sha1"))
  (t/is (contains? hashlib/algorithms-guaranteed "sha256"))
  (t/is (contains? hashlib/algorithms-guaranteed "sha512")))

(t/deftest test-hexdigest-idempotent
  (let [h (hashlib/sha256 (b "abc"))]
    (t/is (= (hashlib/hexdigest h) (hashlib/hexdigest h)))))
