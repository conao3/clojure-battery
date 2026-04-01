;; Original: Lib/test/test_math.py

(ns conao3.battery.math-test
  (:require
   [clojure.test :as t]
   [conao3.battery.math :as math-m]))

(defn- approx= [a b]
  (< (Math/abs (- (double a) (double b))) 1e-10))

(t/deftest test-constants
  (t/is (approx= 3.141592653589793 math-m/pi))
  (t/is (approx= 2.718281828459045 math-m/e))
  (t/is (approx= 6.283185307179586 math-m/tau))
  (t/is (Double/isInfinite math-m/inf))
  (t/is (Double/isNaN math-m/nan)))

(t/deftest test-sqrt
  (t/is (approx= 1.4142135623730951 (math-m/sqrt 2)))
  (t/is (approx= 2.0 (math-m/sqrt 4)))
  (t/is (approx= 3.0 (math-m/sqrt 9))))

(t/deftest test-floor-ceil-trunc
  (t/is (= 3 (math-m/floor 3.7)))
  (t/is (= -4 (math-m/floor -3.2)))
  (t/is (= 4 (math-m/ceil 3.2)))
  (t/is (= -3 (math-m/ceil -3.7)))
  (t/is (= 3 (math-m/trunc 3.7)))
  (t/is (= -3 (math-m/trunc -3.7))))

(t/deftest test-fabs
  (t/is (= 3.7 (math-m/fabs -3.7)))
  (t/is (= 3.7 (math-m/fabs 3.7))))

(t/deftest test-pow-exp-log
  (t/is (approx= 1024.0 (math-m/pow 2 10)))
  (t/is (approx= math-m/e (math-m/exp 1)))
  (t/is (approx= 1.0 (math-m/log math-m/e)))
  (t/is (approx= 3.0 (math-m/log2 8)))
  (t/is (approx= 2.0 (math-m/log10 100))))

(t/deftest test-trig
  (t/is (approx= 1.0 (math-m/sin (/ math-m/pi 2))))
  (t/is (approx= 1.0 (math-m/cos 0)))
  (t/is (approx= 1.0 (math-m/tan (/ math-m/pi 4))))
  (t/is (approx= (/ math-m/pi 2) (math-m/asin 1)))
  (t/is (approx= 0.0 (math-m/acos 1)))
  (t/is (approx= (/ math-m/pi 4) (math-m/atan 1))))

(t/deftest test-atan2
  (t/is (approx= (/ math-m/pi 2) (math-m/atan2 1 0)))
  (t/is (approx= math-m/pi (math-m/atan2 0 -1))))

(t/deftest test-hypot
  (t/is (approx= 5.0 (math-m/hypot 3 4)))
  (t/is (approx= 5.0 (math-m/hypot -3 -4))))

(t/deftest test-degrees-radians
  (t/is (approx= 180.0 (math-m/degrees math-m/pi)))
  (t/is (approx= math-m/pi (math-m/radians 180))))

(t/deftest test-isnan-isinf-isfinite
  (t/is (true? (math-m/isnan Double/NaN)))
  (t/is (false? (math-m/isnan 1.0)))
  (t/is (true? (math-m/isinf Double/POSITIVE_INFINITY)))
  (t/is (false? (math-m/isinf 1.0)))
  (t/is (true? (math-m/isfinite 1.0)))
  (t/is (false? (math-m/isfinite Double/NaN))))

(t/deftest test-factorial
  (t/is (= 1 (math-m/factorial 0)))
  (t/is (= 1 (math-m/factorial 1)))
  (t/is (= 120 (math-m/factorial 5)))
  (t/is (= 3628800 (math-m/factorial 10))))

(t/deftest test-gcd
  (t/is (= 4 (math-m/gcd 12 8)))
  (t/is (= 1 (math-m/gcd 7 5)))
  (t/is (= 6 (math-m/gcd 12 18))))

(t/deftest test-lcm
  (t/is (= 24 (math-m/lcm 8 12)))
  (t/is (= 35 (math-m/lcm 5 7))))

(t/deftest test-comb
  (t/is (= 10 (math-m/comb 5 2)))
  (t/is (= 1 (math-m/comb 5 0)))
  (t/is (= 1 (math-m/comb 5 5)))
  (t/is (= 0 (math-m/comb 5 6))))

(t/deftest test-perm
  (t/is (= 20 (math-m/perm 5 2)))
  (t/is (= 120 (math-m/perm 5)))
  (t/is (= 1 (math-m/perm 5 0))))

(t/deftest test-isclose
  (t/is (true? (math-m/isclose 1.0 1.0)))
  (t/is (true? (math-m/isclose 1.0 1.0000000001)))
  (t/is (false? (math-m/isclose 1.0 2.0))))

(t/deftest test-copysign
  (t/is (= -3.0 (math-m/copysign 3.0 -1.0)))
  (t/is (= 3.0 (math-m/copysign -3.0 1.0))))

(t/deftest test-log-with-base
  (t/is (approx= 3.0 (math-m/log 8 2)))
  (t/is (approx= 2.0 (math-m/log 100 10))))

(t/deftest test-hyperbolic
  (t/is (approx= 0.0 (math-m/sinh 0)))
  (t/is (approx= 1.0 (math-m/cosh 0)))
  (t/is (approx= 0.0 (math-m/tanh 0)))
  (t/is (approx= 0.0 (math-m/asinh 0)))
  (t/is (approx= 0.0 (math-m/atanh 0))))

(t/deftest test-log1p-expm1
  (t/is (approx= 0.0 (math-m/log1p 0)))
  (t/is (approx= 0.0 (math-m/expm1 0)))
  (t/is (approx= (Math/log 2.0) (math-m/log1p 1))))

(t/deftest test-prod-fsum
  (t/is (= 120 (math-m/prod [1 2 3 4 5])))
  (t/is (= 1 (math-m/prod [])))
  (t/is (approx= 6.0 (math-m/fsum [1.0 2.0 3.0]))))

(t/deftest test-modf-frexp
  (let [[_ int-part] (math-m/modf 3.7)]
    (t/is (integer? int-part)))
  (let [[m e] (math-m/frexp 8.0)]
    (t/is (approx= 0.5 m))
    (t/is (= 4 e))))

(t/deftest test-remainder-nextafter
  (t/is (approx= 1.0 (math-m/remainder 5.0 2.0)))
  (t/is (> (math-m/nextafter 1.0 2.0) 1.0))
  (t/is (< (math-m/nextafter 1.0 0.0) 1.0)))

(t/deftest test-acosh-ldexp-floordiv
  (t/is (approx= 0.0 (math-m/acosh 1.0)))
  (t/is (= 8.0 (math-m/ldexp 0.5 4)))
  (t/is (= 3 (math-m/floor-div 10 3)))
  (t/is (= -4 (math-m/floor-div -10 3))))

(t/deftest test-ulp
  ;; ulp(1.0) == machine epsilon
  (t/is (= (double (Math/ulp 1.0)) (math-m/ulp 1.0)))
  ;; ulp(inf) == inf
  (t/is (= Double/POSITIVE_INFINITY (math-m/ulp Double/POSITIVE_INFINITY)))
  ;; ulp(-x) == ulp(x)
  (t/is (= (math-m/ulp 1.0) (math-m/ulp -1.0)))
  ;; ulp(nan) is NaN
  (t/is (Double/isNaN (math-m/ulp Double/NaN))))

(t/deftest test-dist
  ;; 3-4-5 right triangle
  (t/is (approx= 5.0 (math-m/dist [0 0] [3 4])))
  ;; same point
  (t/is (approx= 0.0 (math-m/dist [1 2 3] [1 2 3])))
  ;; 3D distance
  (t/is (approx= (Math/sqrt 3.0) (math-m/dist [0 0 0] [1 1 1]))))

(t/deftest test-gcd-multiple
  (t/is (= 6 (math-m/gcd 12 18 24)))
  (t/is (= 1 (math-m/gcd 7 11 13)))
  (t/is (= 4 (math-m/gcd 4 4))))

(t/deftest test-lcm-multiple
  (t/is (= 12 (math-m/lcm 4 6)))
  (t/is (= 60 (math-m/lcm 4 6 10)))
  (t/is (= 5 (math-m/lcm 5 5))))

(t/deftest test-isclose-defaults
  (t/is (math-m/isclose 1.0 1.0))
  (t/is (math-m/isclose 1.0 1.0000000001))
  (t/is (not (math-m/isclose 1.0 2.0))))

(t/deftest test-isfinite
  (t/is (math-m/isfinite 1.0))
  (t/is (math-m/isfinite 0.0))
  (t/is (not (math-m/isfinite Double/POSITIVE_INFINITY)))
  (t/is (not (math-m/isfinite Double/NaN))))

(t/deftest test-comb-zero
  (t/is (= 1 (math-m/comb 5 0)))
  (t/is (= 1 (math-m/comb 5 5)))
  (t/is (= 5 (math-m/comb 5 1)))
  (t/is (= 10 (math-m/comb 5 2))))

(t/deftest test-perm
  (t/is (= 1 (math-m/perm 5 0)))
  (t/is (= 5 (math-m/perm 5 1)))
  (t/is (= 20 (math-m/perm 5 2)))
  (t/is (= 120 (math-m/perm 5 5))))
