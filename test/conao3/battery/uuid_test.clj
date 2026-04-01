;; Original: Lib/test/test_uuid.py

(ns conao3.battery.uuid-test
  (:require
   [clojure.test :as t]
   [conao3.battery.uuid :as uuid-m])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-uuid3-known-values
  (let [u (uuid-m/uuid3 uuid-m/NAMESPACE_DNS "python.org")]
    (t/is (= "6fa459ea-ee8a-3ca4-894e-db77e160355e" (uuid-m/uuid-str u)))
    (t/is (= 3 (uuid-m/uuid-version u)))
    (t/is (= :rfc-4122 (uuid-m/uuid-variant u))))
  (let [u (uuid-m/uuid3 uuid-m/NAMESPACE_DNS "www.widgets.com")]
    (t/is (= "3d813cbb-47fb-32ba-91df-831e1593ac29" (uuid-m/uuid-str u)))))

(t/deftest test-uuid5-known-values
  (let [u (uuid-m/uuid5 uuid-m/NAMESPACE_DNS "python.org")]
    (t/is (= "886313e1-3b8a-5372-9b90-0c9aee199e5d" (uuid-m/uuid-str u)))
    (t/is (= 5 (uuid-m/uuid-version u)))
    (t/is (= :rfc-4122 (uuid-m/uuid-variant u))))
  (let [u (uuid-m/uuid5 uuid-m/NAMESPACE_DNS "www.example.com")]
    (t/is (= "2ed6657d-e927-568b-95e1-2665a8aea6a2" (uuid-m/uuid-str u)))))

(t/deftest test-uuid4-properties
  (let [u (uuid-m/uuid4)]
    (t/is (= 4 (uuid-m/uuid-version u)))
    (t/is (= :rfc-4122 (uuid-m/uuid-variant u)))))

(t/deftest test-uuid1-properties
  (let [u (uuid-m/uuid1)]
    (t/is (= 1 (uuid-m/uuid-version u)))
    (t/is (= :rfc-4122 (uuid-m/uuid-variant u)))))

(t/deftest test-uuid4-uniqueness
  (let [u1 (uuid-m/uuid4)
        u2 (uuid-m/uuid4)]
    (t/is (not= (uuid-m/uuid-str u1) (uuid-m/uuid-str u2)))))

(t/deftest test-make-uuid-from-str
  (let [s "12345678-1234-5678-1234-567812345678"
        u (uuid-m/make-uuid :str s)]
    (t/is (= s (uuid-m/uuid-str u)))))

(t/deftest test-make-uuid-from-bytes
  (let [bs (byte-array [0x12 0x34 0x56 0x78 0x12 0x34 0x56 0x78
                        0x12 0x34 0x56 0x78 0x12 0x34 0x56 0x78])
        u  (uuid-m/make-uuid :bytes bs)]
    (t/is (= "12345678-1234-5678-1234-567812345678" (uuid-m/uuid-str u)))))

(t/deftest test-make-uuid-from-int
  (let [u (uuid-m/make-uuid :int (BigInteger. "24197857161011715162171839636988778104"))]
    (t/is (= "12345678-1234-5678-1234-567812345678" (uuid-m/uuid-str u)))))

(t/deftest test-uuid-hex
  (let [u (uuid-m/make-uuid :str "12345678-1234-5678-1234-567812345678")]
    (t/is (= "12345678123456781234567812345678" (uuid-m/uuid-hex u)))))

(t/deftest test-uuid-int
  (let [u (uuid-m/make-uuid :str "12345678-1234-5678-1234-567812345678")]
    (t/is (= (BigInteger. "24197857161011715162171839636988778104")
             (uuid-m/uuid-int u)))))

(t/deftest test-uuid-bytes
  (let [expected (byte-array [0x12 0x34 0x56 0x78 0x12 0x34 0x56 0x78
                               0x12 0x34 0x56 0x78 0x12 0x34 0x56 0x78])
        u (uuid-m/make-uuid :str "12345678-1234-5678-1234-567812345678")]
    (t/is (java.util.Arrays/equals expected (uuid-m/uuid-bytes u)))))

(t/deftest test-uuid-fields
  (let [u (uuid-m/make-uuid :str "12345678-1234-5678-1234-567812345678")
        f (uuid-m/uuid-fields u)]
    (t/is (= [0x12345678 0x1234 0x5678 0x12 0x34 0x567812345678] f))))

(t/deftest test-namespace-constants
  (t/is (= "6ba7b810-9dad-11d1-80b4-00c04fd430c8" (uuid-m/uuid-str uuid-m/NAMESPACE_DNS)))
  (t/is (= "6ba7b811-9dad-11d1-80b4-00c04fd430c8" (uuid-m/uuid-str uuid-m/NAMESPACE_URL)))
  (t/is (= "6ba7b812-9dad-11d1-80b4-00c04fd430c8" (uuid-m/uuid-str uuid-m/NAMESPACE_OID)))
  (t/is (= "6ba7b814-9dad-11d1-80b4-00c04fd430c8" (uuid-m/uuid-str uuid-m/NAMESPACE_X500))))

(t/deftest test-make-uuid-invalid-str
  (t/is (thrown? ExceptionInfo (uuid-m/make-uuid :str "not-a-uuid"))))

(t/deftest test-make-uuid-no-args
  (t/is (thrown? ExceptionInfo (uuid-m/make-uuid))))

(t/deftest test-uuid-roundtrip
  (let [u (uuid-m/uuid4)
        s (uuid-m/uuid-str u)
        u2 (uuid-m/make-uuid :str s)]
    (t/is (= s (uuid-m/uuid-str u2)))
    (t/is (= (:most u) (:most u2)))
    (t/is (= (:least u) (:least u2)))))

(t/deftest test-uuid3-vs-uuid5-differ
  (let [u3 (uuid-m/uuid3 uuid-m/NAMESPACE_DNS "python.org")
        u5 (uuid-m/uuid5 uuid-m/NAMESPACE_DNS "python.org")]
    (t/is (not= (uuid-m/uuid-str u3) (uuid-m/uuid-str u5)))))

(t/deftest test-uuid-time-fields
  (let [u (uuid-m/make-uuid :str "12345678-1234-5678-1234-567812345678")]
    (t/is (= 0x12345678 (uuid-m/uuid-time-low u)))
    (t/is (= 0x1234 (uuid-m/uuid-time-mid u)))
    (t/is (= 0x5678 (uuid-m/uuid-time-hi-version u)))))

(t/deftest test-uuid-clock-seq-fields
  (let [u (uuid-m/make-uuid :str "12345678-1234-5678-1234-567812345678")]
    (t/is (= 0x12 (uuid-m/uuid-clock-seq-hi-variant u)))
    (t/is (= 0x34 (uuid-m/uuid-clock-seq-low u)))
    (t/is (= 0x567812345678 (uuid-m/uuid-node u)))))

(t/deftest test-uuid4-str-format
  (let [s (uuid-m/uuid-str (uuid-m/uuid4))]
    (t/is (= 36 (count s)))
    (t/is (= \- (nth s 8)))
    (t/is (= \- (nth s 13)))
    (t/is (= \- (nth s 18)))
    (t/is (= \- (nth s 23)))))

(t/deftest test-uuid1-is-unique
  (let [u1 (uuid-m/uuid1)
        u2 (uuid-m/uuid1)]
    (t/is (not= (uuid-m/uuid-str u1) (uuid-m/uuid-str u2)))))

(t/deftest test-nil-uuid
  (let [s "00000000-0000-0000-0000-000000000000"]
    (t/is (= s (uuid-m/uuid-str uuid-m/NIL)))
    (t/is (= (BigInteger/ZERO) (uuid-m/uuid-int uuid-m/NIL)))))

(t/deftest test-max-uuid
  (let [s "ffffffff-ffff-ffff-ffff-ffffffffffff"
        max-int (.subtract (.shiftLeft BigInteger/ONE 128) BigInteger/ONE)]
    (t/is (= s (uuid-m/uuid-str uuid-m/MAX)))
    (t/is (= max-int (uuid-m/uuid-int uuid-m/MAX)))))
