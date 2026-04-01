;; Original: Lib/test/test_base64.py

(ns conao3.battery.base64-test
  (:require
   [clojure.test :as t]
   [conao3.battery.base64 :as base64])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- b [s]
  (.getBytes s "ISO-8859-1"))

(defn- bytes= [a b]
  (java.util.Arrays/equals a b))

(t/deftest test-encodebytes
  (t/is (bytes= (base64/encodebytes (b "www.python.org")) (b "d3d3LnB5dGhvbi5vcmc=\n")))
  (t/is (bytes= (base64/encodebytes (b "a")) (b "YQ==\n")))
  (t/is (bytes= (base64/encodebytes (b "ab")) (b "YWI=\n")))
  (t/is (bytes= (base64/encodebytes (b "abc")) (b "YWJj\n")))
  (t/is (bytes= (base64/encodebytes (b "")) (b "")))
  (t/is (bytes= (base64/encodebytes (b "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#0^&*();:<>,. []{}"))
                (b "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0\nNTY3ODkhQCMwXiYqKCk7Ojw+LC4gW117fQ==\n")))
  (t/is (bytes= (base64/encodebytes (b "Aladdin:open sesame")) (b "QWxhZGRpbjpvcGVuIHNlc2FtZQ==\n"))))

(t/deftest test-decodebytes
  (t/is (bytes= (base64/decodebytes (b "d3d3LnB5dGhvbi5vcmc=\n")) (b "www.python.org")))
  (t/is (bytes= (base64/decodebytes (b "YQ==\n")) (b "a")))
  (t/is (bytes= (base64/decodebytes (b "YWI=\n")) (b "ab")))
  (t/is (bytes= (base64/decodebytes (b "YWJj\n")) (b "abc")))
  (t/is (bytes= (base64/decodebytes (b "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0\nNTY3ODkhQCMwXiYqKCk7Ojw+LC4gW117fQ==\n"))
                (b "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#0^&*();:<>,. []{}")))
  (t/is (bytes= (base64/decodebytes (b "")) (b "")))
  (t/is (bytes= (base64/decodebytes (b "QWxhZGRpbjpvcGVuIHNlc2FtZQ==\n")) (b "Aladdin:open sesame"))))

(t/deftest test-b64encode
  (t/is (bytes= (base64/b64encode (b "www.python.org")) (b "d3d3LnB5dGhvbi5vcmc=")))
  (t/is (bytes= (base64/b64encode (byte-array [0])) (b "AA==")))
  (t/is (bytes= (base64/b64encode (b "a")) (b "YQ==")))
  (t/is (bytes= (base64/b64encode (b "ab")) (b "YWI=")))
  (t/is (bytes= (base64/b64encode (b "abc")) (b "YWJj")))
  (t/is (bytes= (base64/b64encode (b "")) (b "")))
  (t/is (bytes= (base64/b64encode (b "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#0^&*();:<>,. []{}"))
                (b "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0NTY3ODkhQCMwXiYqKCk7Ojw+LC4gW117fQ==")))
  (t/is (bytes= (base64/b64encode (byte-array (map unchecked-byte [0xd3 0x56 0xbe 0x6f 0xf7 0x1d])) :altchars (b "*$"))
                (b "01a*b$cd")))
  (t/is (bytes= (base64/standard-b64encode (b "www.python.org")) (b "d3d3LnB5dGhvbi5vcmc=")))
  (t/is (bytes= (base64/standard-b64encode (b "a")) (b "YQ==")))
  (t/is (bytes= (base64/standard-b64encode (b "ab")) (b "YWI=")))
  (t/is (bytes= (base64/standard-b64encode (b "abc")) (b "YWJj")))
  (t/is (bytes= (base64/standard-b64encode (b "")) (b "")))
  (t/is (bytes= (base64/urlsafe-b64encode (byte-array (map unchecked-byte [0xd3 0x56 0xbe 0x6f 0xf7 0x1d])))
                (b "01a-b_cd"))))

(t/deftest test-b64decode
  (t/is (bytes= (base64/b64decode (b "d3d3LnB5dGhvbi5vcmc=")) (b "www.python.org")))
  (t/is (bytes= (base64/b64decode (b "AA==")) (byte-array [0])))
  (t/is (bytes= (base64/b64decode (b "YQ==")) (b "a")))
  (t/is (bytes= (base64/b64decode (b "YWI=")) (b "ab")))
  (t/is (bytes= (base64/b64decode (b "YWJj")) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0\nNTY3ODkhQCMwXiYqKCk7Ojw+LC4gW117fQ=="))
                (b "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#0^&*();:<>,. []{}")))
  (t/is (bytes= (base64/b64decode (b "")) (b "")))
  (t/is (bytes= (base64/standard-b64decode (b "d3d3LnB5dGhvbi5vcmc=")) (b "www.python.org")))
  (t/is (bytes= (base64/standard-b64decode (b "YWJj")) (b "abc")))
  (t/is (bytes= (base64/urlsafe-b64decode (b "01a-b_cd"))
                (byte-array (map unchecked-byte [0xd3 0x56 0xbe 0x6f 0xf7 0x1d]))))
  (t/is (bytes= (base64/urlsafe-b64decode (b "")) (b ""))))

(t/deftest test-b64decode-altchars
  (let [res (byte-array (map unchecked-byte [0xd3 0x56 0xbe 0x6f 0xf7 0x1d]))]
    (doseq [altchars [(b "*$") (b "+/") (b "/+") (b "+_") (b "-+") (b "-/") (b "/_")]]
      (let [ac0 (bit-and (aget altchars 0) 0xff)
            ac1 (bit-and (aget altchars 1) 0xff)
            data (byte-array [0x30 0x31 0x61 ac0 0x62 ac1 0x63 0x64])]
        (t/is (bytes= (base64/b64decode data :altchars altchars) res))))))

(t/deftest test-b64decode-padding-error
  (t/is (thrown? ExceptionInfo (base64/b64decode (b "abc"))))
  (t/is (thrown? ExceptionInfo (base64/b64decode "abc"))))

(t/deftest test-b16encode
  (t/is (bytes= (base64/b16encode (byte-array (map unchecked-byte [0x01 0x02 0xab 0xcd 0xef])))
                (b "0102ABCDEF")))
  (t/is (bytes= (base64/b16encode (byte-array [0])) (b "00"))))

(t/deftest test-b16decode
  (t/is (bytes= (base64/b16decode (b "0102ABCDEF"))
                (byte-array (map unchecked-byte [0x01 0x02 0xab 0xcd 0xef]))))
  (t/is (bytes= (base64/b16decode "0102ABCDEF")
                (byte-array (map unchecked-byte [0x01 0x02 0xab 0xcd 0xef]))))
  (t/is (bytes= (base64/b16decode (b "00")) (byte-array [0])))
  (t/is (bytes= (base64/b16decode "00") (byte-array [0])))
  (t/is (thrown? ExceptionInfo (base64/b16decode (b "0102abcdef"))))
  (t/is (thrown? ExceptionInfo (base64/b16decode "0102abcdef")))
  (t/is (bytes= (base64/b16decode (b "0102abcdef") :casefold true)
                (byte-array (map unchecked-byte [0x01 0x02 0xab 0xcd 0xef]))))
  (t/is (bytes= (base64/b16decode "0102abcdef" :casefold true)
                (byte-array (map unchecked-byte [0x01 0x02 0xab 0xcd 0xef]))))
  (t/is (thrown? ExceptionInfo (base64/b16decode "0102AG")))
  (t/is (thrown? ExceptionInfo (base64/b16decode "010"))))

(t/deftest test-b32encode
  (t/is (bytes= (base64/b32encode (b "")) (b "")))
  (t/is (bytes= (base64/b32encode (byte-array [0])) (b "AA======")))
  (t/is (bytes= (base64/b32encode (b "a")) (b "ME======")))
  (t/is (bytes= (base64/b32encode (b "ab")) (b "MFRA====")))
  (t/is (bytes= (base64/b32encode (b "abc")) (b "MFRGG===")))
  (t/is (bytes= (base64/b32encode (b "abcd")) (b "MFRGGZA=")))
  (t/is (bytes= (base64/b32encode (b "abcde")) (b "MFRGGZDF"))))

(t/deftest test-b32decode
  (t/is (bytes= (base64/b32decode (b "")) (b "")))
  (t/is (bytes= (base64/b32decode (b "AA======")) (byte-array [0])))
  (t/is (bytes= (base64/b32decode (b "ME======")) (b "a")))
  (t/is (bytes= (base64/b32decode (b "MFRA====")) (b "ab")))
  (t/is (bytes= (base64/b32decode (b "MFRGG===")) (b "abc")))
  (t/is (bytes= (base64/b32decode (b "MFRGGZA=")) (b "abcd")))
  (t/is (bytes= (base64/b32decode (b "MFRGGZDF")) (b "abcde")))
  (t/is (bytes= (base64/b32decode "ME======") (b "a")))
  (t/is (bytes= (base64/b32decode "MFRGGZDF") (b "abcde"))))

(t/deftest test-b32decode-casefold
  (t/is (bytes= (base64/b32decode (b "") :casefold true) (b "")))
  (t/is (bytes= (base64/b32decode (b "ME======") :casefold true) (b "a")))
  (t/is (bytes= (base64/b32decode (b "MFRA====") :casefold true) (b "ab")))
  (t/is (bytes= (base64/b32decode (b "MFRGG===") :casefold true) (b "abc")))
  (t/is (bytes= (base64/b32decode (b "MFRGGZA=") :casefold true) (b "abcd")))
  (t/is (bytes= (base64/b32decode (b "MFRGGZDF") :casefold true) (b "abcde")))
  (t/is (bytes= (base64/b32decode (b "me======") :casefold true) (b "a")))
  (t/is (bytes= (base64/b32decode (b "mfra====") :casefold true) (b "ab")))
  (t/is (bytes= (base64/b32decode (b "mfrgg===") :casefold true) (b "abc")))
  (t/is (bytes= (base64/b32decode (b "mfrggza=") :casefold true) (b "abcd")))
  (t/is (bytes= (base64/b32decode (b "mfrggzdf") :casefold true) (b "abcde")))
  (t/is (thrown? ExceptionInfo (base64/b32decode (b "me======"))))
  (t/is (thrown? ExceptionInfo (base64/b32decode "me======"))))

(t/deftest test-b32decode-map01
  (let [res-L (byte-array (map unchecked-byte [0x62 0xdd 0xad 0xf3 0xbe]))
        res-I (byte-array (map unchecked-byte [0x62 0x1d 0xad 0xf3 0xbe]))]
    (t/is (bytes= (base64/b32decode (b "MLO23456")) res-L))
    (t/is (bytes= (base64/b32decode "MLO23456") res-L))
    (t/is (bytes= (base64/b32decode (b "MIO23456")) res-I))
    (t/is (bytes= (base64/b32decode "MIO23456") res-I))
    (t/is (thrown? ExceptionInfo (base64/b32decode (b "M1023456"))))
    (t/is (thrown? ExceptionInfo (base64/b32decode (b "M1O23456"))))
    (t/is (thrown? ExceptionInfo (base64/b32decode (b "ML023456"))))
    (t/is (thrown? ExceptionInfo (base64/b32decode (b "MI023456"))))
    (t/is (bytes= (base64/b32decode (b "M1023456") :map01 (b "L")) res-L))
    (t/is (bytes= (base64/b32decode "M1023456" :map01 (b "L")) res-L))
    (t/is (bytes= (base64/b32decode (b "M1023456") :map01 "L") res-L))
    (t/is (bytes= (base64/b32decode "M1023456" :map01 "L") res-L))
    (t/is (bytes= (base64/b32decode (b "M1023456") :map01 (b "I")) res-I))
    (t/is (bytes= (base64/b32decode "M1023456" :map01 "I") res-I))))

(t/deftest test-b32decode-error
  (let [base-tests [(b "abc") (b "ABCDEF==") (b "==ABCDEF")]
        prefixes [(b "M") (b "ME") (b "MFRA") (b "MFRGG") (b "MFRGGZA") (b "MFRGGZDF")]
        extra-tests (for [i (range 1 17)
                          :let [pad (byte-array (repeat i (byte \=)))]
                          prefix prefixes
                          :when (not= (+ (alength prefix) i) 8)]
                      (byte-array (concat (seq prefix) (seq pad))))
        all-tests (concat base-tests
                          (for [i (range 1 17)]
                            (byte-array (repeat i (byte \=))))
                          extra-tests)]
    (doseq [data all-tests]
      (t/is (thrown? ExceptionInfo (base64/b32decode data)))
      (t/is (thrown? ExceptionInfo (base64/b32decode (String. data "ISO-8859-1")))))))

(t/deftest test-urlsafe
  (let [data (byte-array (map unchecked-byte [0xd3 0x56 0xbe 0x6f 0xf7 0x1d]))]
    (t/is (bytes= (base64/urlsafe-b64encode data) (b "01a-b_cd")))
    (t/is (bytes= (base64/urlsafe-b64decode (b "01a-b_cd")) data))
    (t/is (bytes= (base64/urlsafe-b64decode (base64/urlsafe-b64encode data)) data))
    (t/is (bytes= (base64/urlsafe-b64encode (b "")) (b "")))
    (t/is (bytes= (base64/urlsafe-b64decode (b "")) (b "")))))

(t/deftest test-standard-b64encode
  (t/is (bytes= (base64/standard-b64encode (b "abc")) (b "YWJj")))
  (t/is (bytes= (base64/standard-b64encode (b "")) (b "")))
  (t/is (bytes= (base64/standard-b64encode (b "hello")) (b "aGVsbG8="))))

(t/deftest test-standard-b64decode
  (t/is (bytes= (base64/standard-b64decode (b "YWJj")) (b "abc")))
  (t/is (bytes= (base64/standard-b64decode (b "")) (b "")))
  (t/is (bytes= (base64/standard-b64decode (b "aGVsbG8=")) (b "hello"))))

(t/deftest test-encode-decode-streams
  (let [data (b "hello world")
        in   (java.io.ByteArrayInputStream. data)
        out  (java.io.ByteArrayOutputStream.)]
    (base64/encode in out)
    (let [encoded (.toByteArray out)
          in2  (java.io.ByteArrayInputStream. encoded)
          out2 (java.io.ByteArrayOutputStream.)]
      (base64/decode in2 out2)
      (t/is (bytes= data (.toByteArray out2))))))

;; test_b64decode_invalid_chars: Python's default behavior discards non-base64 chars
(t/deftest test-b64decode-invalid-chars-default
  ;; non-base64 chars like % and $ are discarded by default
  (t/is (bytes= (base64/b64decode (b "%3d==")) (byte-array [(unchecked-byte 0xdd)])))
  (t/is (bytes= (base64/b64decode (b "$3d==")) (byte-array [(unchecked-byte 0xdd)])))
  ;; = at wrong positions is discarded
  (t/is (bytes= (base64/b64decode (b "=YWJj")) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "Y=WJj")) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "Y==WJj")) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "Y===WJj")) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "YWJj=")) (b "abc")))
  ;; newline and inline whitespace is discarded
  (t/is (bytes= (base64/b64decode (b "YW\nJj")) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "YWJj\nYWI=")) (b "abcab")))
  ;; string input also works
  (t/is (bytes= (base64/b64decode "%3d==") (byte-array [(unchecked-byte 0xdd)])))
  (t/is (bytes= (base64/b64decode "=YWJj") (b "abc")))
  (t/is (bytes= (base64/b64decode "YW\nJj") (b "abc"))))

(t/deftest test-b64decode-validate-rejects-invalid
  ;; validate=true rejects any non-base64 chars
  (t/is (thrown? ExceptionInfo (base64/b64decode (b "%3d==") :validate true)))
  (t/is (thrown? ExceptionInfo (base64/b64decode (b "=YWJj") :validate true)))
  (t/is (thrown? ExceptionInfo (base64/b64decode (b "YW\nJj") :validate true)))
  (t/is (thrown? ExceptionInfo (base64/b64decode "%3d==" :validate true)))
  (t/is (thrown? ExceptionInfo (base64/b64decode "=YWJj" :validate true)))
  ;; validate=false (default) still works
  (t/is (bytes= (base64/b64decode (b "YWJj") :validate false) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "YWJj\nYWI=") :validate false) (b "abcab"))))

(t/deftest test-b64decode-ignorechars-empty-strict
  ;; ignorechars=b"" is strict mode - rejects non-base64 chars
  (t/is (thrown? ExceptionInfo (base64/b64decode (b "YW\nJj") :ignorechars (b ""))))
  (t/is (thrown? ExceptionInfo (base64/b64decode (b "%3d==") :ignorechars (b ""))))
  (t/is (thrown? ExceptionInfo (base64/b64decode (b "=YWJj") :ignorechars (b "")))))

(t/deftest test-b64decode-ignorechars
  ;; ignorechars specifies which chars to explicitly ignore
  (t/is (bytes= (base64/b64decode (b "YW\nJj") :ignorechars (b "\n")) (b "abc")))
  (t/is (bytes= (base64/b64decode (b "YWJj\nYWI=") :ignorechars (b "\n")) (b "abcab"))))

(t/deftest test-urlsafe-b64decode-invalid-chars
  ;; urlsafe also discards invalid chars by default
  (t/is (bytes= (base64/urlsafe-b64decode (b "=01a-b_cd"))
                (byte-array (map unchecked-byte [0xd3 0x56 0xbe 0x6f 0xf7 0x1d]))))
  (t/is (bytes= (base64/urlsafe-b64decode (b "01a\n-b_cd"))
                (byte-array (map unchecked-byte [0xd3 0x56 0xbe 0x6f 0xf7 0x1d])))))
