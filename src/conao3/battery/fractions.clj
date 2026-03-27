(ns conao3.battery.fractions)

(defn fraction
  "Create a fraction. Uses Clojure's native ratio type."
  ([] 0)
  ([n]
   (if (ratio? n)
     n
     (/ (if (ratio? n) (numerator n) (long n))
        (if (ratio? n) (denominator n) 1))))
  ([n d]
   (/ n d)))

(defn- frac-num
  "Numerator of a fraction (integer or ratio)."
  [f]
  (if (ratio? f) (numerator f) (long f)))

(defn- frac-den
  "Denominator of a fraction (integer or ratio)."
  [f]
  (if (ratio? f) (denominator f) 1))

(defn components
  "Return [numerator denominator] of a fraction."
  [f]
  [(frac-num f) (frac-den f)])

(defn from-float
  "Create exact fraction from float."
  [f]
  (when (Double/isInfinite f)
    (throw (ex-info "cannot convert Infinity to integer ratio" {:type :overflow-error})))
  (when (Double/isNaN f)
    (throw (ex-info "cannot convert NaN to integer ratio" {:type :value-error})))
  (rationalize f))

(defn from-decimal
  "Create fraction from BigDecimal."
  [d]
  (throw (ex-info "Not implemented" {})))

(defn from-string
  "Create fraction from string representation."
  [s]
  (throw (ex-info "Not implemented" {})))

(defn from-number
  "Create fraction from number."
  [n]
  (throw (ex-info "Not implemented" {})))

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
