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

(defrecord Decimal [^BigDecimal _bd])

(defn decimal
  ([x]
   (cond
     (instance? Decimal x) x
     (instance? BigDecimal x) (->Decimal x)
     (string? x) (->Decimal (BigDecimal. ^String x))
     (integer? x) (->Decimal (BigDecimal/valueOf (long x)))
     (float? x) (->Decimal (BigDecimal. (double x)))
     (ratio? x) (->Decimal (.divide (BigDecimal. (numerator x))
                                    (BigDecimal. (denominator x))
                                    (make-mc)))
     :else (->Decimal (BigDecimal. (str x))))))

(defn- bd [^Decimal d] ^BigDecimal (:_bd d))

(defn decimal-add [^Decimal a ^Decimal b]
  (->Decimal (.add (bd a) (bd b))))

(defn decimal-sub [^Decimal a ^Decimal b]
  (->Decimal (.subtract (bd a) (bd b))))

(defn decimal-mul [^Decimal a ^Decimal b]
  (->Decimal (.multiply (bd a) (bd b))))

(defn decimal-div
  ([^Decimal a ^Decimal b]
   (->Decimal (.divide (bd a) (bd b) (make-mc)))))

(defn decimal-rem [^Decimal a ^Decimal b]
  (->Decimal (.remainder (bd a) (bd b))))

(defn decimal-floordiv [^Decimal a ^Decimal b]
  (->Decimal (.setScale (.divideToIntegralValue (bd a) (bd b)) 0 ROUND_DOWN)))

(defn decimal-pow [^Decimal a ^Decimal b]
  (try
    (->Decimal (.pow (bd a) (int (.longValueExact (bd b)))))
    (catch Exception _
      (->Decimal (.pow (bd a) (int (.longValue (bd b))) (make-mc))))))

(defn decimal-neg [^Decimal a]
  (->Decimal (.negate (bd a))))

(defn decimal-abs [^Decimal a]
  (->Decimal (.abs (bd a))))

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
  (neg? (.signum (bd d))))

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
     (->Decimal (.setScale (bd d) (.scale exp-bd) ^RoundingMode rounding)))))

(defn to-integral-value
  ([^Decimal d]
   (to-integral-value d (current-rounding)))
  ([^Decimal d rounding]
   (->Decimal (.setScale (bd d) 0 ^RoundingMode rounding))))

(defn sqrt [^Decimal d]
  (->Decimal (.sqrt (bd d) (make-mc))))

(defn ln [^Decimal d]
  (let [v (Math/log (.doubleValue (bd d)))]
    (->Decimal (BigDecimal. v (make-mc)))))

(defn log10 [^Decimal d]
  (let [v (Math/log10 (.doubleValue (bd d)))]
    (->Decimal (BigDecimal. v (make-mc)))))

(defn decimal-int [^Decimal d]
  (.longValue (bd d)))

(defn decimal-float [^Decimal d]
  (.doubleValue (bd d)))

(defn as-tuple [^Decimal d]
  (let [bd-val  (bd d)
        sign    (if (neg? (.signum bd-val)) 1 0)
        scale   (.scale bd-val)
        unscaled (.unscaledValue bd-val)
        abs-u   (.abs unscaled)
        digits  (mapv #(- (int %) (int \0)) (str abs-u))
        exp     (- scale)]
    {:sign sign :digits digits :exponent exp}))
