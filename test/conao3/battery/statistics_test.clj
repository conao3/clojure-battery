;; Original: Lib/test/test_statistics.py

(ns conao3.battery.statistics-test
  (:require
   [clojure.test :as t]
   [conao3.battery.statistics :as statistics])
  (:import
   [clojure.lang ExceptionInfo]))

;; ---- mean ----

(t/deftest ^:kaocha/skip test-mean-ints
  (t/is (= 3 (statistics/mean [1 2 3 4 5])))
  (t/is (= 2 (statistics/mean [1 2 3]))))

(t/deftest ^:kaocha/skip test-mean-floats
  (t/is (< (Math/abs (- 2.8 (statistics/mean [1.0 2.0 3.0 4.0 4.0]))) 1e-10)))

(t/deftest ^:kaocha/skip test-mean-fractions
  (t/is (= 2/5 (statistics/mean [1/5 3/5]))))

(t/deftest ^:kaocha/skip test-mean-inf
  (t/is (= Double/POSITIVE_INFINITY (statistics/mean [1.0 Double/POSITIVE_INFINITY]))))

(t/deftest ^:kaocha/skip test-mismatched-infs
  (t/is (Double/isNaN (statistics/mean [Double/POSITIVE_INFINITY Double/NEGATIVE_INFINITY]))))

(t/deftest ^:kaocha/skip test-mean-nan
  (t/is (Double/isNaN (statistics/mean [1.0 Double/NaN]))))

(t/deftest ^:kaocha/skip test-mean-empty
  (t/is (= 3 (statistics/mean [1 2 3 4 5])))
  (t/is (thrown? ExceptionInfo (statistics/mean []))))

;; ---- fmean ----

(t/deftest ^:kaocha/skip test-fmean-basics
  (t/is (= 2.5 (statistics/fmean [1 2 3 4])))
  (t/is (= 1.0 (statistics/fmean [1]))))

(t/deftest ^:kaocha/skip test-fmean-error-cases
  (t/is (= 1.5 (statistics/fmean [1 2] [1 1])))
  (t/is (thrown? ExceptionInfo (statistics/fmean []))))

(t/deftest ^:kaocha/skip test-fmean-special-values
  (t/is (Double/isNaN (statistics/fmean [Double/NaN 1.0])))
  (t/is (= Double/POSITIVE_INFINITY (statistics/fmean [Double/POSITIVE_INFINITY 1.0]))))

(t/deftest ^:kaocha/skip test-weights
  (t/is (= 3.0 (statistics/fmean [1 2 3 4 5] [1 1 1 1 1]))
        "equal weights should give same result as unweighted")
  (t/is (thrown? ExceptionInfo (statistics/fmean [1 2] [1]))
        "weights must be same length as data")
  (t/is (= 4.0 (statistics/fmean [4 8] [1 1]))))

;; ---- geometric-mean ----

(t/deftest ^:kaocha/skip test-geometricmean-basics
  (t/is (< (Math/abs (- 36.0 (statistics/geometric-mean [54 24 36]))) 1e-10))
  (t/is (< (Math/abs (- 4.0 (statistics/geometric-mean [2 8]))) 1e-10)))

(t/deftest ^:kaocha/skip test-geometricmean-error-cases
  (t/is (= 2.0 (statistics/geometric-mean [1 4])))
  (t/is (thrown? ExceptionInfo (statistics/geometric-mean [])))
  (t/is (thrown? ExceptionInfo (statistics/geometric-mean [-1 2 3]))))

(t/deftest ^:kaocha/skip test-geometricmean-special-values
  (t/is (= Double/POSITIVE_INFINITY (statistics/geometric-mean [Double/POSITIVE_INFINITY 1.0])))
  (t/is (Double/isNaN (statistics/geometric-mean [Double/NaN 1.0]))))

;; ---- harmonic-mean ----

(t/deftest ^:kaocha/skip test-harmonicmean-ints
  (t/is (= 24/5 (statistics/harmonic-mean [2 4 4 8 16 16]))))

(t/deftest ^:kaocha/skip test-floats-exact
  (t/is (< (Math/abs (- 0.5 (statistics/harmonic-mean [0.25 1.0]))) 1e-10)))

(t/deftest ^:kaocha/skip test-singleton-lists
  (t/is (= 5 (statistics/harmonic-mean [5]))))

(t/deftest ^:kaocha/skip test-harmonicmean-fractions
  (t/is (= 1/2 (statistics/harmonic-mean [1/3 1]))))

(t/deftest ^:kaocha/skip test-harmonicmean-inf
  (t/is (= 0.0 (statistics/harmonic-mean [Double/POSITIVE_INFINITY 1.0]))))

(t/deftest ^:kaocha/skip test-harmonicmean-nan
  (t/is (Double/isNaN (statistics/harmonic-mean [Double/NaN 1.0]))))

(t/deftest ^:kaocha/skip test-zero
  (t/is (= 2.0 (statistics/harmonic-mean [1 2 4]))
        "harmonic mean of [1 2 4] is 2")
  (t/is (thrown? ExceptionInfo (statistics/harmonic-mean []))))

(t/deftest ^:kaocha/skip test-negative-error
  (t/is (= 4/3 (statistics/harmonic-mean [1 2 3])))
  (t/is (thrown? ExceptionInfo (statistics/harmonic-mean [-1 2 3]))))

(t/deftest ^:kaocha/skip test-with-weights
  (t/is (= 2 (statistics/harmonic-mean [1 2 4] [3 2 1]))))

;; ---- median ----

(t/deftest ^:kaocha/skip test-odd-ints
  (t/is (= 3 (statistics/median [1 3 5])))
  (t/is (= 3 (statistics/median [5 3 1]))))

(t/deftest ^:kaocha/skip test-median-even-ints
  (t/is (= 4.0 (statistics/median [1 3 5 7])))
  (t/is (= 3.5 (statistics/median [1 2 5 7]))))

(t/deftest ^:kaocha/skip test-median-odd-fractions
  (t/is (= 11/20 (statistics/median [3/10 9/20 11/20 4/5])))
  (t/is (= 1/2 (statistics/median [1/4 1/2 3/4]))))

(t/deftest ^:kaocha/skip test-median-even-fractions
  (t/is (= 1/2 (statistics/median [1/4 1/3 2/3 3/4]))))

;; ---- median-low ----

(t/deftest ^:kaocha/skip test-medianlow-even-ints
  (t/is (= 3 (statistics/median-low [1 3 5 7])))
  (t/is (= 1 (statistics/median-low [1 3]))))

(t/deftest ^:kaocha/skip test-medianlow-even-fractions
  (t/is (= 1/3 (statistics/median-low [1/4 1/3 2/3 3/4]))))

;; ---- median-high ----

(t/deftest ^:kaocha/skip test-medianhigh-even-ints
  (t/is (= 5 (statistics/median-high [1 3 5 7])))
  (t/is (= 3 (statistics/median-high [1 3]))))

(t/deftest ^:kaocha/skip test-medianhigh-even-fractions
  (t/is (= 2/3 (statistics/median-high [1/4 1/3 2/3 3/4]))))

;; ---- median-grouped ----

(t/deftest ^:kaocha/skip test-odd-number-repeated
  (t/is (= 2.5 (statistics/median-grouped [1 2 2 3 4]))))

(t/deftest ^:kaocha/skip test-even-number-repeated
  (t/is (= 3.5 (statistics/median-grouped [1 2 3 3 4 5]))))

(t/deftest ^:kaocha/skip test-mediangrouped-single-value
  (t/is (= 1.5 (statistics/median-grouped [1])))
  (t/is (= 2.0 (statistics/median-grouped [2]))))

(t/deftest ^:kaocha/skip test-interval
  (t/is (= 3.25 (statistics/median-grouped [1 2 3 3 4 5] 2))))

;; ---- mode ----

(t/deftest ^:kaocha/skip test-mode-range-data
  (t/is (= 5 (statistics/mode [1 1 2 5 5 5 7]))))

(t/deftest ^:kaocha/skip test-nominal-data
  (t/is (= "red" (statistics/mode ["red" "blue" "red" "green"]))))

(t/deftest ^:kaocha/skip test-discrete-data
  (t/is (= 3/8 (statistics/mode [3/8 1/2 3/8 3/4]))))

(t/deftest ^:kaocha/skip test-unique-data
  (t/is (= 3 (statistics/mode [3 1 2 4]))))

;; ---- multimode ----

(t/deftest ^:kaocha/skip test-multimode-basics
  (t/is (= [1 3] (statistics/multimode [1 1 2 3 3])))
  (t/is (= [5] (statistics/multimode [1 1 2 5 5 5 7])))
  (t/is (= [] (statistics/multimode []))))

;; ---- pvariance ----

(t/deftest ^:kaocha/skip test-pvariance-ints
  (t/is (= 4 (statistics/pvariance [1 2 3 4 5 6 7]))))

(t/deftest ^:kaocha/skip test-pvariance-fractions
  (t/is (= 1/4 (statistics/pvariance [1/4 3/4]))))

(t/deftest ^:kaocha/skip test-variancestdev-single-value
  (t/is (= 0 (statistics/pvariance [17]))))

;; ---- variance ----

(t/deftest ^:kaocha/skip test-variance-single-value
  (t/is (= 35/4 (statistics/variance [1 2 3 4 5 6 7])))
  (t/is (thrown? ExceptionInfo (statistics/variance [17]))))

(t/deftest ^:kaocha/skip test-variance-ints
  (t/is (= 35/4 (statistics/variance [1 2 3 4 5 6 7]))))

(t/deftest ^:kaocha/skip test-variance-fractions
  (t/is (= 1/2 (statistics/variance [1/4 3/4]))))

;; ---- stdev ----

(t/deftest ^:kaocha/skip test-stdev-single-value
  (t/is (< (Math/abs (- (statistics/stdev [1 2 3])
                        (Math/sqrt (statistics/variance [1 2 3]))))
           1e-10))
  (t/is (thrown? ExceptionInfo (statistics/stdev [17]))))

(t/deftest ^:kaocha/skip test-stdev-compare-to-variance
  (let [data [1.0 2.0 3.0 4.0 5.0]]
    (t/is (< (Math/abs (- (statistics/stdev data)
                          (Math/sqrt (statistics/variance data))))
             1e-10))))

;; ---- pstdev ----

(t/deftest ^:kaocha/skip test-pstdev-compare-to-variance
  (let [data [1.0 2.0 3.0 4.0 5.0]]
    (t/is (< (Math/abs (- (statistics/pstdev data)
                          (Math/sqrt (statistics/pvariance data))))
             1e-10))))

;; ---- quantiles ----

(t/deftest ^:kaocha/skip test-specific-cases
  (t/is (= [1.5 3.0 4.5] (statistics/quantiles [1 2 3 4 5])))
  (t/is (= [1.0] (statistics/quantiles [1 2 3] {:n 2}))))

(t/deftest ^:kaocha/skip test-quantiles-error-cases
  (t/is (= [1.0] (statistics/quantiles [1 2 3] {:n 2})))
  (t/is (thrown? ExceptionInfo (statistics/quantiles [1 2] {:n 0})))
  (t/is (thrown? ExceptionInfo (statistics/quantiles [1]))))

;; ---- correlation and covariance ----

(t/deftest ^:kaocha/skip test-correlationandcovariance-results
  (let [x [1 2 3 4 5]
        y [1 2 3 4 5]]
    (t/is (< (Math/abs (- 1.0 (statistics/correlation x y))) 1e-10))
    (t/is (< (Math/abs (- 2.5 (statistics/covariance x y))) 1e-10))))

(t/deftest ^:kaocha/skip test-constant-input-error
  (t/is (= 1.0 (statistics/correlation [1 2 3] [1 2 3])))
  (t/is (thrown? ExceptionInfo (statistics/correlation [1 1 1] [1 2 3]))))

;; ---- linear-regression ----

(t/deftest ^:kaocha/skip test-linearregression-results
  (let [result (statistics/linear-regression [1 2 3] [2 4 6])]
    (t/is (< (Math/abs (- 2.0 (:slope result))) 1e-10))
    (t/is (< (Math/abs (- 0.0 (:intercept result))) 1e-10))))
