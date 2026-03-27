(ns conao3.battery.string)

(def whitespace " \t\n\r\u000b\u000c")
(def ascii-lowercase "abcdefghijklmnopqrstuvwxyz")
(def ascii-uppercase "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
(def ascii-letters (str ascii-lowercase ascii-uppercase))
(def digits "0123456789")
(def hexdigits (str digits "abcdefABCDEF"))
(def octdigits "01234567")
(def punctuation "!\u0022#$%&'()*+,-./:;<=>?@[\\]^_`{|}~")
(def printable (str digits ascii-lowercase ascii-uppercase punctuation whitespace))

(defn- not-implemented []
  (throw (ex-info "Not implemented" {})))

(defn capwords [& _]
  (not-implemented))

(defn formatter-format [& _]
  (not-implemented))

(defn formatter-format-keyword [& _]
  (not-implemented))

(defn formatter-get-value [& _]
  (not-implemented))

(defn formatter-format-field [& _]
  (not-implemented))

(defn formatter-convert-field [& _]
  (not-implemented))

(defn formatter-parse [& _]
  (not-implemented))

(defn formatter-check-unused-args [& _]
  (not-implemented))

(defn formatter-vformat-recursion-limit [& _]
  (not-implemented))

(defn template-substitute [& _]
  (not-implemented))

(defn template-safe-substitute [& _]
  (not-implemented))

(defn template-is-valid [& _]
  (not-implemented))

(defn template-get-identifiers [& _]
  (not-implemented))
