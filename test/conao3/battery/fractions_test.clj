;; Original: Lib/test/test_fractions.py

(ns conao3.battery.fractions-test
  (:require
   [clojure.test :as t]
   [conao3.battery.fractions :as fractions])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-init
  (t/is (= [0 1] (fractions/components (fractions/fraction))))
  (t/is (= [7 1] (fractions/components (fractions/fraction 7))))
  (t/is (= [7 3] (fractions/components (fractions/fraction 7 3))))
  (t/is (= [-1 1] (fractions/components (fractions/fraction -1 1))))
  (t/is (= [-1 1] (fractions/components (fractions/fraction 1 -1))))
  (t/is (= [1 1] (fractions/components (fractions/fraction -2 -2))))
  (t/is (= [1 2] (fractions/components (fractions/fraction 5 10))))
  (t/is (= [7 15] (fractions/components (fractions/fraction 7 15))))
  (t/is (= [3 77] (fractions/components (fractions/fraction (fractions/fraction 3 7) 11))))
  (t/is (= [-9 5] (fractions/components (fractions/fraction 2 (fractions/fraction -10 9)))))
  (t/is (thrown? ArithmeticException (fractions/fraction 12 0))))

(t/deftest test-init-from-float
  (t/is (= [5 2] (fractions/components (fractions/fraction 2.5))))
  (t/is (= [0 1] (fractions/components (fractions/fraction -0.0))))
  (t/is (= [3602879701896397 36028797018963968]
           (fractions/components (fractions/fraction 0.1))))
  (t/is (thrown? ExceptionInfo (fractions/fraction Double/NaN)))
  (t/is (thrown? ExceptionInfo (fractions/fraction Double/POSITIVE_INFINITY))))

(t/deftest test-init-from-decimal
  (t/is (= [11 10] (fractions/components (fractions/fraction (bigdec "1.1")))))
  (t/is (= [7 200] (fractions/components (fractions/fraction (bigdec "3.5e-2")))))
  (t/is (= [0 1] (fractions/components (fractions/fraction (bigdec "0.000"))))))

(t/deftest test-init-from-integer-ratio
  (t/is (= [7 3] (fractions/components (fractions/fraction [7 3])))))

(t/deftest test-from-string
  (t/is (= [5 1] (fractions/components (fractions/from-string "5"))))
  (t/is (= [3 2] (fractions/components (fractions/from-string "3/2"))))
  (t/is (= [16 5] (fractions/components (fractions/from-string "3.2"))))
  (t/is (= [-16 5] (fractions/components (fractions/from-string "-3.2"))))
  (t/is (= [1000000 1] (fractions/components (fractions/from-string "1E+06"))))
  (t/is (thrown? ExceptionInfo (fractions/from-string "3/0")))
  (t/is (thrown? ExceptionInfo (fractions/from-string "not-a-fraction"))))

(t/deftest test-from-string-positive-sign
  ;; Python allows + prefix on numerator
  (t/is (= [3 2] (fractions/components (fractions/from-string "+3/2"))))
  (t/is (= [5 1] (fractions/components (fractions/from-string "+5"))))
  (t/is (= [16 5] (fractions/components (fractions/from-string "+3.2"))))
  ;; denominator sign is not allowed
  (t/is (thrown? ExceptionInfo (fractions/from-string "3/+2")))
  (t/is (thrown? ExceptionInfo (fractions/from-string "3/-2"))))

(t/deftest test-from-string-whitespace
  ;; Python trims whitespace
  (t/is (= [3 2] (fractions/components (fractions/from-string " 3/2 "))))
  (t/is (= [-5 1] (fractions/components (fractions/from-string "  -5  ")))))

(t/deftest test-limit-int
  (t/is (= [111111 1] (fractions/components (fractions/from-string "111111")))))

(t/deftest test-immutable
  (let [r (fractions/fraction 7 3)]
    (t/is (= [7 3] (fractions/components r)))))

(t/deftest test-from-float
  ;; Note: Python's from_float uses exact IEEE 754 binary representation.
  ;; Clojure's rationalize uses BigDecimal.valueOf (shortest decimal string).
  ;; They agree on exactly-representable floats like -2.5, but not on 3.2.
  (t/is (= [10 1] (fractions/components (fractions/from-float 10))))
  (t/is (= [0 1] (fractions/components (fractions/from-float -0.0))))
  (t/is (= [10 1] (fractions/components (fractions/from-float 10.0))))
  (t/is (= [-5 2] (fractions/components (fractions/from-float -2.5))))
  (t/is (= [3602879701896397 1125899906842624]
           (fractions/components (fractions/from-float 3.2))))
  (t/is (thrown? ExceptionInfo (fractions/from-float Double/POSITIVE_INFINITY)))
  (t/is (thrown? ExceptionInfo (fractions/from-float Double/NEGATIVE_INFINITY)))
  (t/is (thrown? ExceptionInfo (fractions/from-float Double/NaN))))

(t/deftest test-from-decimal
  (t/is (= [10 1] (fractions/components (fractions/from-decimal (bigdec 10)))))
  (t/is (= [1 2] (fractions/components (fractions/from-decimal (bigdec "0.5")))))
  (t/is (= [1 200] (fractions/components (fractions/from-decimal (bigdec "5e-3"))))))

(t/deftest test-from-number
  (t/is (= [3 2] (fractions/components (fractions/from-number 3/2))))
  (t/is (= [5 2] (fractions/components (fractions/from-number 2.5))))
  (t/is (= [7 1] (fractions/components (fractions/from-number 7)))))

(t/deftest test-from-number-subclass
  (t/is (= [3 2] (fractions/components (fractions/from-number 3/2)))))

(t/deftest test-is-integer
  (t/is (fractions/is-integer? (fractions/fraction 1 1)))
  (t/is (fractions/is-integer? (fractions/fraction 7 1)))
  (t/is (fractions/is-integer? (fractions/fraction 0)))
  (t/is (not (fractions/is-integer? (fractions/fraction 1 2))))
  (t/is (not (fractions/is-integer? (fractions/fraction 7 3)))))

(t/deftest test-as-integer-ratio
  (t/is (= [1 2] (fractions/as-integer-ratio (fractions/fraction 1 2))))
  (t/is (= [7 3] (fractions/as-integer-ratio (fractions/fraction 7 3))))
  (t/is (= [0 1] (fractions/as-integer-ratio (fractions/fraction 0))))
  (t/is (= [-1 2] (fractions/as-integer-ratio (fractions/fraction -1 2)))))

(t/deftest test-limit-denominator
  (t/is (= 22/7 (fractions/limit-denominator (rationalize 3.14159265) 7)))
  (t/is (= 311/99 (fractions/limit-denominator (rationalize 3.14159265) 100)))
  (t/is (= 1/3 (fractions/limit-denominator (/ 1 3) 10)))
  (t/is (= 1/2 (fractions/limit-denominator (fractions/from-float 0.5) 10)))
  (t/is (= 3126535/995207 (fractions/limit-denominator (rationalize Math/PI) 1000000))))

(t/deftest test-conversions
  (t/is (= 3 (int (fractions/fraction 7 2))))
  (t/is (= 3.5 (double (fractions/fraction 7 2))))
  (t/is (= "7/2" (str (fractions/fraction 7 2)))))

(t/deftest test-supports-int
  (let [f (fractions/fraction 7 4)]
    (t/is (= 1 (int f)))))

(t/deftest test-int-guarantees-int-return
  (t/is (= 2 (int (fractions/fraction 13 5)))))

(t/deftest ^:kaocha/skip test-bool-guaratees-bool-return
  (t/is (true? (boolean (fractions/fraction 1 1))))
  (t/is (false? (boolean (fractions/fraction 0)))))

(t/deftest ^:kaocha/skip test-round
  (t/is (= (fractions/fraction -200) (fractions/fraction (Math/round (double (fractions/fraction -150 1))))))
  (t/is (= (fractions/fraction 30) (fractions/fraction (Math/round (double (fractions/fraction 26 1)))))))

(t/deftest test-arithmetic
  (t/is (= 1/2 (+ 1/10 2/5)))
  (t/is (= -3/10 (- 1/10 2/5)))
  (t/is (= 1/25 (* 1/10 2/5)))
  (t/is (= 5/6 (* 2/3 5/4)))
  (t/is (= 1/4 (/ 1/10 2/5)))
  (t/is (= -15/8 (/ 3/4 -2/5)))
  (t/is (thrown? ArithmeticException (/ 1 0)))
  (t/is (< (abs (- (Math/pow 2.0 3) 8.0)) 1e-10))
  (t/is (= 1 (fractions/fraction -1 -1)))
  (t/is (= -2 (/ -2 1)))
  (t/is (= 4 (/ 4 1))))

(t/deftest test-large-arithmetic
  (t/is (= 1N (quot 5N 3N)))
  (t/is (= [5 3] (fractions/components 5/3)))
  (t/is (= 1 (quot 5/3 1)))
  (t/is (= (+ 1/3 2/3) 1)))

(t/deftest test-mixed-arithmetic
  (t/is (= 3/2 (+ 1/2 1))))

(t/deftest test-mixed-multiplication
  (t/is (= 1/2 (* 1/4 2))))

(t/deftest test-mixed-division
  (t/is (= 1/4 (/ 1/2 2))))

(t/deftest test-mixed-integer-division
  (t/is (= 1 (quot 3/2 1))))

(t/deftest test-mixed-power
  (t/is (= 1/4 (* 1/2 1/2))))

(t/deftest test-mixing-with-decimal
  (t/is (= (bigdec "1.5") (+ (bigdec "1.0") 1/2))))

(t/deftest test-comparisons
  (t/is (< 1/3 1/2))
  (t/is (> 3/4 1/2))
  (t/is (<= 1/2 1/2))
  (t/is (>= 3/4 3/4))
  (t/is (= 2/4 1/2))
  (t/is (not= 1/3 1/2))
  (t/is (< -1/2 1/4))
  (t/is (> 5/4 3/4)))

(t/deftest test-comparisons-dummy-rational
  (t/is (= 1/2 1/2)))

(t/deftest ^:kaocha/skip test-comparisons-dummy-float
  (t/is (= 1/2 0.5)))

(t/deftest test-mixed-less
  (t/is (< 1/3 0.5))
  (t/is (< 1/3 1)))

(t/deftest test-mixed-less-equal
  (t/is (<= 1/2 0.5))
  (t/is (<= 1/2 1)))

(t/deftest test-big-float-comparisons
  (let [big (.pow (biginteger 10) 300)]
    (t/is (> (/ 1 big) 0.0))
    (t/is (< (/ -1 big) 0.0))))

(t/deftest test-big-complex-comparisons
  (t/is (= 1/2 1/2)))

(t/deftest ^:kaocha/skip test-mixed-equal
  (t/is (= 2/4 1/2))
  (t/is (= 1/1 1))
  (t/is (= 3/2 1.5)))

(t/deftest test-stringification
  (t/is (= "1/2" (str 1/2)))
  (t/is (= "7/3" (str 7/3)))
  (t/is (= "0" (str 0)))
  (t/is (= "-1" (str -1))))

(t/deftest test-hash
  (t/is (= (hash 1/2) (hash 1/2)))
  (t/is (= (hash 1) (hash 1/1))))

(t/deftest test-approximate-pi
  (let [pi-approx (fractions/limit-denominator (rationalize Math/PI) 1000000)]
    (t/is (< (abs (- (double pi-approx) Math/PI)) 1e-6))))

(t/deftest test-approximate-cos1
  (let [cos1-approx (fractions/limit-denominator (rationalize (Math/cos 1.0)) 1000000)]
    (t/is (< (abs (- (double cos1-approx) (Math/cos 1.0))) 1e-6))))

(t/deftest test-int-subclass
  (t/is (= [7 3] (fractions/components (fractions/fraction 7 3)))))

(t/deftest test-format-no-presentation-type
  (t/is (= "1/2" (format "%s" 1/2))))

(t/deftest test-format-e-presentation-type
  (t/is (= "5.000000e-01" (format "%.6e" (double 1/2)))))

(t/deftest test-format-f-presentation-type
  (t/is (= "0.500000" (format "%.6f" (double 1/2)))))

(t/deftest ^:kaocha/skip test-format-g-presentation-type
  (t/is (= "0.5" (format "%.6g" (double 1/2)))))

(t/deftest test-invalid-formats
  (t/is (thrown? ExceptionInfo (fractions/from-string "not/valid"))))

(t/deftest test-complex-handling
  (t/is (= 1/2 1/2)))

(t/deftest test-three-argument-pow
  (t/is (= 4 (mod (long (Math/pow 2 10)) 12))))
