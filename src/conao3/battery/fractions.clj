(ns conao3.battery.fractions
  (:require [clojure.string :as str]))

(defn- double->exact-ratio
  [^double f]
  (when (Double/isInfinite f)
    (throw (ex-info "cannot convert Infinity to integer ratio" {:type :overflow-error})))
  (when (Double/isNaN f)
    (throw (ex-info "cannot convert NaN to integer ratio" {:type :value-error})))
  (let [bits (Double/doubleToRawLongBits f)
        negative? (neg? bits)
        mantissa-stored (bit-and bits 0x000fffffffffffff)
        exp-biased (int (bit-and (unsigned-bit-shift-right bits 52) 0x7FF))
        [mantissa exp] (if (zero? exp-biased)
                         [(bigint mantissa-stored) -1022]
                         [(bigint (bit-or mantissa-stored (bit-shift-left 1 52)))
                          (- exp-biased 1023)])
        mantissa (if negative? (- mantissa) mantissa)
        exp-shift (- exp 52)]
    (if (neg? exp-shift)
      (/ mantissa (.shiftLeft BigInteger/ONE (- exp-shift)))
      (* mantissa (.shiftLeft BigInteger/ONE exp-shift)))))

(defn fraction
  "Create a fraction. Uses Clojure's native ratio type."
  ([] 0)
  ([n]
   (cond
     (ratio? n) n
     (float? n) (double->exact-ratio n)
     (instance? BigDecimal n) (rationalize n)
     (vector? n) (/ (first n) (second n))
     :else (long n)))
  ([n d]
   (/ n d)))

(defn- frac-num
  [f]
  (if (ratio? f) (numerator f) (long f)))

(defn- frac-den
  [f]
  (if (ratio? f) (denominator f) 1))

(defn components
  "Return [numerator denominator] of a fraction."
  [f]
  [(frac-num f) (frac-den f)])

(defn from-float
  "Create exact fraction from float using IEEE754 bit representation."
  [f]
  (cond
    (integer? f) (long f)
    :else (double->exact-ratio (double f))))

(defn from-decimal
  "Create fraction from BigDecimal."
  [d]
  (rationalize d))

(defn from-string
  "Create fraction from string representation."
  [s]
  (let [s (str/trim s)]
    (cond
      (re-matches #"-?\d+" s)
      (Long/parseLong s)

      (re-matches #"-?\d+/\d+" s)
      (let [[_ n d] (re-matches #"(-?\d+)/(\d+)" s)
            nd (Long/parseLong n)
            dd (Long/parseLong d)]
        (when (zero? dd)
          (throw (ex-info (str "Fraction('" s "') has zero denominator") {})))
        (/ nd dd))

      (re-matches #"-?(?:\d+\.?\d*|\.\d+)(?:[eE][+-]?\d+)?" s)
      (rationalize (bigdec s))

      :else
      (throw (ex-info (str "Invalid literal for Fraction: '" s "'") {})))))

(defn from-number
  "Create fraction from number (ratio, float, or integer)."
  [n]
  (cond
    (ratio? n) n
    (float? n) (rationalize n)
    :else (long n)))

(defn is-integer?
  "Return true if fraction has denominator 1."
  [f]
  (not (ratio? f)))

(defn as-integer-ratio
  "Return [numerator denominator] pair."
  [f]
  [(frac-num f) (frac-den f)])

(defn limit-denominator
  "Return nearest fraction with denominator at most max-denominator."
  ([f] (limit-denominator f 1000000))
  ([f max-denominator]
   (when (< max-denominator 1)
     (throw (ex-info "max-denominator should be at least 1" {:type :value-error})))
   (let [d (frac-den f)]
     (if (<= d max-denominator)
       f
       (loop [p0 0N q0 1N p1 1N q1 0N
              n (frac-num f) d d]
         (let [a (quot n d)
               q2 (+ q0 (* a q1))]
           (if (> q2 max-denominator)
             (let [k (quot (- max-denominator q0) q1)
                   bound1 (/ (+ p0 (* k p1)) (+ q0 (* k q1)))
                   bound2 (/ p1 q1)]
               (if (<= (abs (- bound2 f)) (abs (- bound1 f)))
                 bound2
                 bound1))
             (recur p1 q1 (+ p0 (* a p1)) q2
                    d (- n (* a d))))))))))
