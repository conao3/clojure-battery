(ns conao3.battery.decimal
  (:import
   [java.math BigDecimal MathContext RoundingMode]))

(def ROUND_UP       RoundingMode/UP)
(def ROUND_DOWN     RoundingMode/DOWN)
(def ROUND_CEILING  RoundingMode/CEILING)
(def ROUND_FLOOR    RoundingMode/FLOOR)
(def ROUND_HALF_UP  RoundingMode/HALF_UP)
(def ROUND_HALF_DOWN RoundingMode/HALF_DOWN)
(def ROUND_HALF_EVEN RoundingMode/HALF_EVEN)
(def ROUND_05UP     RoundingMode/UP)

(def ^:private default-context (atom {:prec 28 :rounding ROUND_HALF_EVEN}))

(defn getcontext [] @default-context)

(defn setcontext! [ctx]
  (reset! default-context ctx))

(defn- current-prec []     (:prec @default-context))
(defn- current-rounding [] (:rounding @default-context))

(defn- make-mc
  ([] (make-mc (current-prec) (current-rounding)))
  ([prec] (make-mc prec (current-rounding)))
  ([prec rounding] (MathContext. (int prec) ^RoundingMode rounding)))

;; _sign: nil means derive from _bd.signum(); true means explicitly negative (for -0)
(defrecord Decimal [^BigDecimal _bd _sign])

(defn- make-dec
  ([bd] (->Decimal bd nil))
  ([bd explicit-neg] (->Decimal bd explicit-neg)))

(defn- effective-sign
  "Returns Python-style sign: 0 for positive/zero, 1 for negative (including -0)."
  [^Decimal d]
  (if (true? (:_sign d))
    1
    (if (neg? (.signum ^BigDecimal (:_bd d))) 1 0)))

(defn decimal
  ([x]
   (cond
     (instance? Decimal x) x
     (instance? BigDecimal x) (make-dec x)
     (string? x)
     (let [trimmed (clojure.string/trim x)
           ;; detect negative zero: "-0", "-0.0", "-0.00", etc.
           neg-zero? (and (clojure.string/starts-with? trimmed "-")
                          (zero? (.compareTo (BigDecimal. ^String trimmed) BigDecimal/ZERO)))]
       (->Decimal (BigDecimal. ^String trimmed) neg-zero?))
     (integer? x) (make-dec (BigDecimal/valueOf (long x)))
     (float? x) (make-dec (BigDecimal. (double x)))
     (ratio? x) (make-dec (.divide (BigDecimal. (numerator x))
                                   (BigDecimal. (denominator x))
                                   (make-mc)))
     :else (make-dec (BigDecimal. (str x))))))

(defn- bd [^Decimal d] ^BigDecimal (:_bd d))

(defn decimal-add [^Decimal a ^Decimal b]
  (make-dec (.add (bd a) (bd b))))

(defn decimal-sub [^Decimal a ^Decimal b]
  (make-dec (.subtract (bd a) (bd b))))

(defn decimal-mul [^Decimal a ^Decimal b]
  (make-dec (.multiply (bd a) (bd b))))

(defn decimal-div
  ([^Decimal a ^Decimal b]
   (make-dec (.divide (bd a) (bd b) (make-mc)))))

(defn decimal-rem [^Decimal a ^Decimal b]
  (make-dec (.remainder (bd a) (bd b))))

(defn decimal-floordiv [^Decimal a ^Decimal b]
  (make-dec (.setScale (.divideToIntegralValue (bd a) (bd b)) 0 ROUND_DOWN)))

(defn decimal-pow [^Decimal a ^Decimal b]
  (try
    (make-dec (.pow (bd a) (int (.longValueExact (bd b)))))
    (catch Exception _
      (make-dec (.pow (bd a) (int (.longValue (bd b))) (make-mc))))))

(defn decimal-neg [^Decimal a]
  (let [neg-bd (.negate (bd a))
        ;; -0 negated => +0, +0 negated => -0, other values: follow BigDecimal
        explicit-neg? (if (zero? (.signum neg-bd))
                        (zero? (effective-sign a))
                        nil)]
    (->Decimal neg-bd explicit-neg?)))

(defn decimal-abs [^Decimal a]
  (make-dec (.abs (bd a))))

(defn decimal-compare [^Decimal a ^Decimal b]
  (.compareTo (bd a) (bd b)))

(defn decimal-eq [^Decimal a ^Decimal b]
  (= (.compareTo (bd a) (bd b)) 0))

(defn decimal-lt [^Decimal a ^Decimal b]
  (neg? (.compareTo (bd a) (bd b))))

(defn decimal-le [^Decimal a ^Decimal b]
  (<= (.compareTo (bd a) (bd b)) 0))

(defn decimal-gt [^Decimal a ^Decimal b]
  (pos? (.compareTo (bd a) (bd b))))

(defn decimal-ge [^Decimal a ^Decimal b]
  (>= (.compareTo (bd a) (bd b)) 0))

(defn decimal-str [^Decimal d]
  (.toPlainString (bd d)))

(defn decimal-repr [^Decimal d]
  (str "Decimal('" (.toPlainString (bd d)) "')"))

(defn is-signed [^Decimal d]
  (= 1 (effective-sign d)))

(defn is-nan [^Decimal d]
  (try (.intValue (.unscaledValue (bd d))) false
       (catch Exception _ false))
  false)

(defn is-infinite [^Decimal d]
  false)

(defn is-finite [^Decimal d]
  true)

(defn sign [^Decimal d]
  (.signum (bd d)))

(defn quantize
  ([^Decimal d exp]
   (quantize d exp (current-rounding)))
  ([^Decimal d exp rounding]
   (let [exp-bd (bd (decimal exp))
         scale  (- (.scale exp-bd))]
     (make-dec (.setScale (bd d) (.scale exp-bd) ^RoundingMode rounding)))))

(defn to-integral-value
  ([^Decimal d]
   (to-integral-value d (current-rounding)))
  ([^Decimal d rounding]
   (make-dec (.setScale (bd d) 0 ^RoundingMode rounding))))

(defn sqrt [^Decimal d]
  (make-dec (.sqrt (bd d) (make-mc))))

(defn ln [^Decimal d]
  (let [v (Math/log (.doubleValue (bd d)))]
    (make-dec (BigDecimal. v (make-mc)))))

(defn log10 [^Decimal d]
  (let [v (Math/log10 (.doubleValue (bd d)))]
    (make-dec (BigDecimal. v (make-mc)))))

(defn decimal-int [^Decimal d]
  (.longValue (bd d)))

(defn decimal-float [^Decimal d]
  (.doubleValue (bd d)))

(defn as-tuple [^Decimal d]
  (let [bd-val   (bd d)
        sign     (effective-sign d)
        unscaled (.unscaledValue bd-val)
        abs-u    (.abs unscaled)
        digits   (mapv #(- (int %) (int \0)) (str abs-u))
        exp      (- (.scale bd-val))]
    {:sign sign :digits digits :exponent exp}))
