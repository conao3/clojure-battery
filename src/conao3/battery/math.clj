(ns conao3.battery.math)

(def pi Math/PI)
(def e Math/E)
(def tau (* 2.0 Math/PI))
(def inf Double/POSITIVE_INFINITY)
(def nan Double/NaN)

(defn sqrt [x] (Math/sqrt (double x)))
(defn floor [x] (long (Math/floor (double x))))
(defn ceil [x] (long (Math/ceil (double x))))
(defn trunc [x] (long x))
(defn fabs [x] (Math/abs (double x)))
(defn pow [x y] (Math/pow (double x) (double y)))
(defn exp [x] (Math/exp (double x)))

(defn log
  ([x] (Math/log (double x)))
  ([x base] (/ (Math/log (double x)) (Math/log (double base)))))

(defn log2 [x] (/ (Math/log (double x)) (Math/log 2.0)))
(defn log10 [x] (Math/log10 (double x)))
(defn log1p [x] (Math/log1p (double x)))
(defn expm1 [x] (Math/expm1 (double x)))

(defn sin [x]  (Math/sin (double x)))
(defn cos [x]  (Math/cos (double x)))
(defn tan [x]  (Math/tan (double x)))
(defn asin [x] (Math/asin (double x)))
(defn acos [x] (Math/acos (double x)))
(defn atan [x] (Math/atan (double x)))
(defn atan2 [y x] (Math/atan2 (double y) (double x)))

(defn sinh [x]  (Math/sinh (double x)))
(defn cosh [x]  (Math/cosh (double x)))
(defn tanh [x]  (Math/tanh (double x)))
(defn asinh [x] (Math/log (+ (double x) (Math/sqrt (+ (* (double x) (double x)) 1.0)))))
(defn acosh [x] (Math/log (+ (double x) (Math/sqrt (- (* (double x) (double x)) 1.0)))))
(defn atanh [x] (* 0.5 (Math/log (/ (+ 1.0 (double x)) (- 1.0 (double x))))))

(defn degrees [x] (* (double x) (/ 180.0 Math/PI)))
(defn radians [x] (* (double x) (/ Math/PI 180.0)))

(defn hypot
  ([x y] (Math/hypot (double x) (double y)))
  ([x y & more] (Math/sqrt (reduce + (map #(* (double %) (double %)) (list* x y more))))))

(defn isnan [x] (Double/isNaN (double x)))
(defn isinf [x] (Double/isInfinite (double x)))
(defn isfinite [x] (and (not (Double/isNaN (double x))) (not (Double/isInfinite (double x)))))
(defn isclose
  ([a b] (isclose a b 1e-9 0.0))
  ([a b rel-tol] (isclose a b rel-tol 0.0))
  ([a b rel-tol abs-tol]
   (<= (Math/abs (- (double a) (double b)))
       (max (* rel-tol (max (Math/abs (double a)) (Math/abs (double b)))) abs-tol))))

(defn factorial [n]
  (if (<= n 1) 1 (* (long n) (factorial (dec n)))))

(defn gcd
  ([a b]
   (if (zero? b)
     (Math/abs (long a))
     (recur b (mod (long a) (long b)))))
  ([a b & more]
   (reduce gcd (gcd a b) more)))

(defn lcm
  ([a b]
   (let [g (gcd a b)]
     (if (zero? g) 0 (/ (* (Math/abs (long a)) (Math/abs (long b))) g))))
  ([a b & more]
   (reduce lcm (lcm a b) more)))

(defn comb [n k]
  (if (or (< k 0) (> k n))
    0
    (let [k (min k (- n k))]
      (loop [i 0 result 1]
        (if (= i k)
          result
          (recur (inc i) (/ (* result (- n i)) (inc i))))))))

(defn perm
  ([n] (factorial n))
  ([n k]
   (if (< k 0)
     (throw (ex-info "k must be non-negative" {}))
     (reduce * (range (- n k -1) (inc n))))))

(defn prod [iterable]
  (reduce * 1 iterable))

(defn fsum [iterable]
  (double (reduce + iterable)))

(defn copysign [x y]
  (Math/copySign (double x) (double y)))

(defn frexp [x]
  (if (zero? x)
    [0.0 0]
    (let [bits (Double/doubleToLongBits (double x))
          exp  (bit-and (unsigned-bit-shift-right bits 52) 0x7FF)
          mantissa (bit-or (bit-and bits (Long/parseUnsignedLong "000FFFFFFFFFFFFF" 16)) (Long/parseUnsignedLong "0010000000000000" 16))
          m    (* (if (neg? x) -1.0 1.0) (/ mantissa (Math/pow 2.0 52)))
          e    (- exp 1022)]
      [(/ m 2.0) e])))

(defn ldexp [x i]
  (* (double x) (Math/pow 2.0 (double i))))

(defn modf [x]
  (let [d (double x)
        i (Math/rint d)
        f (- d i)]
    [f (long i)]))

(defn remainder [x y]
  (let [d (double x)
        v (double y)]
    (- d (* (Math/rint (/ d v)) v))))

(defn floor-div [x y]
  (Math/floorDiv (long x) (long y)))

(defn nextafter [x y]
  (Math/nextAfter (double x) (double y)))

(defn ulp [x]
  (Math/ulp (double x)))

(defn dist
  ([p q]
   (Math/sqrt (double (reduce + (map (fn [pi qi] (let [d (- (double pi) (double qi))] (* d d))) p q))))))
