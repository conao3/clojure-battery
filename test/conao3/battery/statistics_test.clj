;; Original: Lib/test/test_statistics.py

(ns conao3.battery.statistics-test
  (:require
   [clojure.test :as t]
   [conao3.battery.statistics :as statistics])
  (:import
   [clojure.lang ExceptionInfo]))

;; ---- mean ----

(t/deftest test-mean-ints
  (t/is (= 3 (statistics/mean [1 2 3 4 5])))
  (t/is (= 2 (statistics/mean [1 2 3]))))

(t/deftest test-mean-floats
  (t/is (< (Math/abs (- 2.8 (statistics/mean [1.0 2.0 3.0 4.0 4.0]))) 1e-10)))

(t/deftest test-mean-fractions
  (t/is (= 2/5 (statistics/mean [1/5 3/5]))))

(t/deftest test-mean-inf
  (t/is (= Double/POSITIVE_INFINITY (statistics/mean [1.0 Double/POSITIVE_INFINITY]))))

(t/deftest test-mismatched-infs
  (t/is (Double/isNaN (statistics/mean [Double/POSITIVE_INFINITY Double/NEGATIVE_INFINITY]))))

(t/deftest test-mean-nan
  (t/is (Double/isNaN (statistics/mean [1.0 Double/NaN]))))

(t/deftest test-mean-empty
  (t/is (= 3 (statistics/mean [1 2 3 4 5])))
  (t/is (thrown? ExceptionInfo (statistics/mean []))))

;; ---- fmean ----

(t/deftest test-fmean-basics
  (t/is (= 2.5 (statistics/fmean [1 2 3 4])))
  (t/is (= 1.0 (statistics/fmean [1]))))

(t/deftest test-fmean-error-cases
  (t/is (= 1.5 (statistics/fmean [1 2] [1 1])))
  (t/is (thrown? ExceptionInfo (statistics/fmean []))))

(t/deftest test-fmean-special-values
  (t/is (Double/isNaN (statistics/fmean [Double/NaN 1.0])))
  (t/is (= Double/POSITIVE_INFINITY (statistics/fmean [Double/POSITIVE_INFINITY 1.0]))))

(t/deftest test-weights
  (t/is (= 3.0 (statistics/fmean [1 2 3 4 5] [1 1 1 1 1]))
        "equal weights should give same result as unweighted")
  (t/is (thrown? ExceptionInfo (statistics/fmean [1 2] [1]))
        "weights must be same length as data")
  (t/is (= 6.0 (statistics/fmean [4 8] [1 1]))))

;; ---- geometric-mean ----

(t/deftest test-geometricmean-basics
  (t/is (< (Math/abs (- 36.0 (statistics/geometric-mean [54 24 36]))) 1e-10))
  (t/is (< (Math/abs (- 4.0 (statistics/geometric-mean [2 8]))) 1e-10)))

(t/deftest test-geometricmean-error-cases
  (t/is (= 2.0 (statistics/geometric-mean [1 4])))
  (t/is (thrown? ExceptionInfo (statistics/geometric-mean [])))
  (t/is (thrown? ExceptionInfo (statistics/geometric-mean [-1 2 3]))))

(t/deftest test-geometricmean-special-values
  (t/is (= Double/POSITIVE_INFINITY (statistics/geometric-mean [Double/POSITIVE_INFINITY 1.0])))
  (t/is (Double/isNaN (statistics/geometric-mean [Double/NaN 1.0]))))

;; ---- harmonic-mean ----

(t/deftest test-harmonicmean-ints
  (t/is (= 24/5 (statistics/harmonic-mean [2 4 4 8 16 16]))))

(t/deftest test-floats-exact
  (t/is (< (Math/abs (- 0.4 (statistics/harmonic-mean [0.25 1.0]))) 1e-10)))

(t/deftest test-singleton-lists
  (t/is (= 5 (statistics/harmonic-mean [5]))))

(t/deftest test-harmonicmean-fractions
  (t/is (= 1/2 (statistics/harmonic-mean [1/3 1]))))

(t/deftest test-harmonicmean-inf
  (t/is (= 2.0 (statistics/harmonic-mean [Double/POSITIVE_INFINITY 1.0]))))

(t/deftest test-harmonicmean-nan
  (t/is (Double/isNaN (statistics/harmonic-mean [Double/NaN 1.0]))))

(t/deftest test-zero
  (t/is (= 12/7 (statistics/harmonic-mean [1 2 4])))
  (t/is (thrown? ExceptionInfo (statistics/harmonic-mean []))))

(t/deftest test-negative-error
  (t/is (= 18/11 (statistics/harmonic-mean [1 2 3])))
  (t/is (thrown? ExceptionInfo (statistics/harmonic-mean [-1 2 3]))))

(t/deftest test-with-weights
  (t/is (= 24/17 (statistics/harmonic-mean [1 2 4] [3 2 1]))))

;; ---- median ----

(t/deftest test-odd-ints
  (t/is (= 3 (statistics/median [1 3 5])))
  (t/is (= 3 (statistics/median [5 3 1]))))

(t/deftest test-median-even-ints
  (t/is (= 4 (statistics/median [1 3 5 7])))
  (t/is (= 7/2 (statistics/median [1 2 5 7]))))

(t/deftest test-median-odd-fractions
  (t/is (= 1/2 (statistics/median [3/10 9/20 11/20 4/5])))
  (t/is (= 1/2 (statistics/median [1/4 1/2 3/4]))))

(t/deftest test-median-even-fractions
  (t/is (= 1/2 (statistics/median [1/4 1/3 2/3 3/4]))))

;; ---- median-low ----

(t/deftest test-medianlow-even-ints
  (t/is (= 3 (statistics/median-low [1 3 5 7])))
  (t/is (= 1 (statistics/median-low [1 3]))))

(t/deftest test-medianlow-even-fractions
  (t/is (= 1/3 (statistics/median-low [1/4 1/3 2/3 3/4]))))

;; ---- median-high ----

(t/deftest test-medianhigh-even-ints
  (t/is (= 5 (statistics/median-high [1 3 5 7])))
  (t/is (= 3 (statistics/median-high [1 3]))))

(t/deftest test-medianhigh-even-fractions
  (t/is (= 2/3 (statistics/median-high [1/4 1/3 2/3 3/4]))))

;; ---- median-grouped ----

(t/deftest test-odd-number-repeated
  (t/is (= 2.25 (statistics/median-grouped [1 2 2 3 4]))))

(t/deftest test-even-number-repeated
  (t/is (= 3.0 (statistics/median-grouped [1 2 3 3 4 5]))))

(t/deftest test-mediangrouped-single-value
  (t/is (= 1.0 (statistics/median-grouped [1])))
  (t/is (= 2.0 (statistics/median-grouped [2]))))

(t/deftest test-interval
  (t/is (= 3.0 (statistics/median-grouped [1 2 3 3 4 5] 2))))

;; ---- mode ----

(t/deftest test-mode-range-data
  (t/is (= 5 (statistics/mode [1 1 2 5 5 5 7]))))

(t/deftest test-nominal-data
  (t/is (= "red" (statistics/mode ["red" "blue" "red" "green"]))))

(t/deftest test-discrete-data
  (t/is (= 3/8 (statistics/mode [3/8 1/2 3/8 3/4]))))

(t/deftest test-unique-data
  (t/is (= 3 (statistics/mode [3 1 2 4]))))

;; ---- multimode ----

(t/deftest test-multimode-basics
  (t/is (= [1 3] (statistics/multimode [1 1 2 3 3])))
  (t/is (= [5] (statistics/multimode [1 1 2 5 5 5 7])))
  (t/is (= [] (statistics/multimode []))))

;; ---- pvariance ----

(t/deftest test-pvariance-ints
  (t/is (= 4 (statistics/pvariance [1 2 3 4 5 6 7]))))

(t/deftest test-pvariance-fractions
  (t/is (= 1/16 (statistics/pvariance [1/4 3/4]))))

(t/deftest test-variancestdev-single-value
  (t/is (= 0 (statistics/pvariance [17]))))

;; ---- variance ----

(t/deftest test-variance-single-value
  (t/is (= 14/3 (statistics/variance [1 2 3 4 5 6 7])))
  (t/is (thrown? ExceptionInfo (statistics/variance [17]))))

(t/deftest test-variance-ints
  (t/is (= 14/3 (statistics/variance [1 2 3 4 5 6 7]))))

(t/deftest test-variance-fractions
  (t/is (= 1/8 (statistics/variance [1/4 3/4]))))

;; ---- stdev ----

(t/deftest test-stdev-single-value
  (t/is (< (Math/abs (- (statistics/stdev [1 2 3])
                        (Math/sqrt (statistics/variance [1 2 3]))))
           1e-10))
  (t/is (thrown? ExceptionInfo (statistics/stdev [17]))))

(t/deftest test-stdev-compare-to-variance
  (let [data [1.0 2.0 3.0 4.0 5.0]]
    (t/is (< (Math/abs (- (statistics/stdev data)
                          (Math/sqrt (statistics/variance data))))
             1e-10))))

;; ---- pstdev ----

(t/deftest test-pstdev-compare-to-variance
  (let [data [1.0 2.0 3.0 4.0 5.0]]
    (t/is (< (Math/abs (- (statistics/pstdev data)
                          (Math/sqrt (statistics/pvariance data))))
             1e-10))))

;; ---- quantiles ----

(t/deftest test-specific-cases
  (t/is (= [1.5 3.0 4.5] (statistics/quantiles [1 2 3 4 5])))
  (t/is (= [2.0] (statistics/quantiles [1 2 3] {:n 2}))))

(t/deftest test-quantiles-error-cases
  (t/is (= [2.0] (statistics/quantiles [1 2 3] {:n 2})))
  (t/is (thrown? ExceptionInfo (statistics/quantiles [1 2] {:n 0})))
  (t/is (thrown? ExceptionInfo (statistics/quantiles [1]))))

;; ---- correlation and covariance ----

(t/deftest test-correlationandcovariance-results
  (let [x [1 2 3 4 5]
        y [1 2 3 4 5]]
    (t/is (< (Math/abs (- 1.0 (statistics/correlation x y))) 1e-10))
    (t/is (< (Math/abs (- 2.5 (statistics/covariance x y))) 1e-10))))

(t/deftest test-constant-input-error
  (t/is (= 1.0 (statistics/correlation [1 2 3] [1 2 3])))
  (t/is (thrown? ExceptionInfo (statistics/correlation [1 1 1] [1 2 3]))))

;; ---- linear-regression ----

(t/deftest test-linearregression-results
  (let [result (statistics/linear-regression [1 2 3] [2 4 6])]
    (t/is (< (Math/abs (- 2.0 (:slope result))) 1e-10))
    (t/is (< (Math/abs (- 0.0 (:intercept result))) 1e-10))))

(t/deftest test-harmonicmean-with-zero
  ;; Python returns 0 when data contains 0
  (t/is (= 0 (statistics/harmonic-mean [1 0 2])))
  (t/is (= 0 (statistics/harmonic-mean [10 0 5]))))

(t/deftest test-statistics-no-modify-input
  ;; Statistics functions should not modify input data
  (let [data [3 1 4 1 5 9 2 6]
        original (vec data)]
    (statistics/mean data)
    (statistics/median data)
    (t/is (= original (vec data)))))
