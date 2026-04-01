;; Original: Lib/test/test_decimal.py

(ns conao3.battery.decimal-test
  (:require
   [clojure.test :as t]
   [conao3.battery.decimal :as dec-m]))

(t/deftest test-decimal-from-string
  (let [d (dec-m/decimal "1.1")]
    (t/is (= "1.1" (dec-m/decimal-str d)))))

(t/deftest test-decimal-from-int
  (let [d (dec-m/decimal 42)]
    (t/is (= "42" (dec-m/decimal-str d)))))

(t/deftest test-decimal-add
  (t/is (= "3.3" (dec-m/decimal-str (dec-m/decimal-add (dec-m/decimal "1.1") (dec-m/decimal "2.2")))))
  (t/is (= "10" (dec-m/decimal-str (dec-m/decimal-add (dec-m/decimal "3") (dec-m/decimal "7"))))))

(t/deftest test-decimal-sub
  (t/is (= "1.1" (dec-m/decimal-str (dec-m/decimal-sub (dec-m/decimal "3.3") (dec-m/decimal "2.2"))))))

(t/deftest test-decimal-mul
  (t/is (= "2.42" (dec-m/decimal-str (dec-m/decimal-mul (dec-m/decimal "1.1") (dec-m/decimal "2.2"))))))

(t/deftest test-decimal-div
  (let [result (dec-m/decimal-str (dec-m/decimal-div (dec-m/decimal "10") (dec-m/decimal "3")))]
    (t/is (clojure.string/starts-with? result "3.33333"))))

(t/deftest test-decimal-rem
  (t/is (= "1" (dec-m/decimal-str (dec-m/decimal-rem (dec-m/decimal "10") (dec-m/decimal "3"))))))

(t/deftest test-decimal-floordiv
  (t/is (= "3" (dec-m/decimal-str (dec-m/decimal-floordiv (dec-m/decimal "10") (dec-m/decimal "3"))))))

(t/deftest test-decimal-pow
  (t/is (= "1024" (dec-m/decimal-str (dec-m/decimal-pow (dec-m/decimal "2") (dec-m/decimal "10"))))))

(t/deftest test-decimal-neg
  (t/is (= "-1.5" (dec-m/decimal-str (dec-m/decimal-neg (dec-m/decimal "1.5")))))
  (t/is (= "1.5" (dec-m/decimal-str (dec-m/decimal-neg (dec-m/decimal "-1.5"))))))

(t/deftest test-decimal-abs
  (t/is (= "3.14" (dec-m/decimal-str (dec-m/decimal-abs (dec-m/decimal "-3.14")))))
  (t/is (= "3.14" (dec-m/decimal-str (dec-m/decimal-abs (dec-m/decimal "3.14"))))))

(t/deftest test-decimal-compare
  (t/is (= 0 (dec-m/decimal-compare (dec-m/decimal "1.0") (dec-m/decimal "1.00"))))
  (t/is (= -1 (dec-m/decimal-compare (dec-m/decimal "1.0") (dec-m/decimal "2.0"))))
  (t/is (= 1 (dec-m/decimal-compare (dec-m/decimal "2.0") (dec-m/decimal "1.0")))))

(t/deftest test-decimal-equality
  (t/is (dec-m/decimal-eq (dec-m/decimal "1.0") (dec-m/decimal "1.00")))
  (t/is (not (dec-m/decimal-eq (dec-m/decimal "1.0") (dec-m/decimal "2.0")))))

(t/deftest test-decimal-ordering
  (t/is (dec-m/decimal-lt (dec-m/decimal "1.0") (dec-m/decimal "2.0")))
  (t/is (dec-m/decimal-le (dec-m/decimal "1.0") (dec-m/decimal "1.0")))
  (t/is (dec-m/decimal-gt (dec-m/decimal "2.0") (dec-m/decimal "1.0")))
  (t/is (dec-m/decimal-ge (dec-m/decimal "1.0") (dec-m/decimal "1.0"))))

(t/deftest test-quantize-half-up
  (let [d (dec-m/decimal "2.675")]
    (t/is (= "2.68" (dec-m/decimal-str (dec-m/quantize d (dec-m/decimal "0.01") dec-m/ROUND_HALF_UP))))))

(t/deftest test-quantize-half-even
  (let [d (dec-m/decimal "2.675")]
    (t/is (= "2.68" (dec-m/decimal-str (dec-m/quantize d (dec-m/decimal "0.01") dec-m/ROUND_HALF_EVEN))))))

(t/deftest test-quantize-down
  (let [d (dec-m/decimal "2.675")]
    (t/is (= "2.67" (dec-m/decimal-str (dec-m/quantize d (dec-m/decimal "0.01") dec-m/ROUND_DOWN))))))

(t/deftest test-to-integral-value
  (t/is (= "1" (dec-m/decimal-str (dec-m/to-integral-value (dec-m/decimal "1.7") dec-m/ROUND_DOWN))))
  (t/is (= "2" (dec-m/decimal-str (dec-m/to-integral-value (dec-m/decimal "1.7") dec-m/ROUND_UP))))
  (t/is (= "2" (dec-m/decimal-str (dec-m/to-integral-value (dec-m/decimal "1.5") dec-m/ROUND_HALF_EVEN)))))

(t/deftest test-sqrt
  (let [result (dec-m/decimal-float (dec-m/sqrt (dec-m/decimal "2")))]
    (t/is (< (Math/abs (- result 1.4142135623730951)) 1e-10))))

(t/deftest test-is-signed
  (t/is (dec-m/is-signed (dec-m/decimal "-1")))
  (t/is (not (dec-m/is-signed (dec-m/decimal "1")))))

(t/deftest test-sign
  (t/is (= -1 (dec-m/sign (dec-m/decimal "-5"))))
  (t/is (= 0 (dec-m/sign (dec-m/decimal "0"))))
  (t/is (= 1 (dec-m/sign (dec-m/decimal "5")))))

(t/deftest test-as-tuple
  (let [t (dec-m/as-tuple (dec-m/decimal "3.14"))]
    (t/is (= 0 (:sign t)))
    (t/is (= [3 1 4] (:digits t)))
    (t/is (= -2 (:exponent t)))))

(t/deftest test-getcontext
  (let [ctx (dec-m/getcontext)]
    (t/is (= 28 (:prec ctx)))))

(t/deftest test-decimal-int-float
  (t/is (= 3 (dec-m/decimal-int (dec-m/decimal "3.14"))))
  (t/is (< (Math/abs (- 3.14 (dec-m/decimal-float (dec-m/decimal "3.14")))) 1e-10)))

(t/deftest test-decimal-sqrt
  (let [result (dec-m/sqrt (dec-m/decimal "4"))]
    (t/is (< (Math/abs (- 2.0 (dec-m/decimal-float result))) 1e-10))))

(t/deftest test-decimal-ln-log10
  (let [e (dec-m/decimal "2.718281828459045")]
    (t/is (< (Math/abs (- 1.0 (dec-m/decimal-float (dec-m/ln e)))) 1e-6)))
  (let [hundred (dec-m/decimal "100")]
    (t/is (< (Math/abs (- 2.0 (dec-m/decimal-float (dec-m/log10 hundred)))) 1e-10))))

(t/deftest test-decimal-quantize
  (let [d (dec-m/decimal "3.14159")]
    (t/is (= "3.14" (dec-m/decimal-str (dec-m/quantize d (dec-m/decimal "0.01")))))))

(t/deftest test-decimal-to-integral
  (let [d (dec-m/decimal "3.7")]
    (t/is (= "4" (dec-m/decimal-str (dec-m/to-integral-value d)))))
  (let [d (dec-m/decimal "3.2")]
    (t/is (= "3" (dec-m/decimal-str (dec-m/to-integral-value d dec-m/ROUND_DOWN))))))

(t/deftest test-is-nan-is-infinite-is-finite
  (let [d (dec-m/decimal "3.14")]
    (t/is (false? (dec-m/is-nan d)))
    (t/is (false? (dec-m/is-infinite d)))
    (t/is (true? (dec-m/is-finite d)))))

(t/deftest test-decimal-repr
  (let [d (dec-m/decimal "3.14")]
    (t/is (string? (dec-m/decimal-repr d)))
    (t/is (clojure.string/includes? (dec-m/decimal-repr d) "3.14"))))

(t/deftest test-setcontext
  (let [original (dec-m/getcontext)]
    (dec-m/setcontext! (assoc original :prec 10))
    (t/is (= 10 (:prec (dec-m/getcontext))))
    (dec-m/setcontext! original)))
