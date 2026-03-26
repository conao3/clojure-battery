;; Original: Lib/test/test_statistics.py

(ns conao3.battery.statistics-test
  (:require
   [clojure.test :as t]
   [conao3.battery.statistics :as statistics]))

(t/deftest ^:kaocha/skip test-py-functions
  (t/is true))

(t/deftest ^:kaocha/skip test-c-functions
  (t/is true))

(t/deftest ^:kaocha/skip test-relative-symmetry
  (t/is true))

(t/deftest ^:kaocha/skip test-symmetry
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-floats
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-absolute
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-absolute-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-relative
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-equal-both
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-unequal-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-unequal-floats
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-unequal-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-exactly-unequal-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-absolute-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-absolute-floats
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-absolute-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-absolute-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-cross-zero
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-relative-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-relative-floats
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-relative-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-relative-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-both1
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-both2
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-both3
  (t/is true))

(t/deftest ^:kaocha/skip test-approx-equal-both4
  (t/is true))

(t/deftest ^:kaocha/skip test-approxequalspecialstest-inf
  (t/is true))

(t/deftest ^:kaocha/skip test-approxequalspecialstest-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-float-zeroes
  (t/is true))

(t/deftest ^:kaocha/skip test-decimal-zeroes
  (t/is true))

(t/deftest ^:kaocha/skip test-bad-tol
  (t/is true))

(t/deftest ^:kaocha/skip test-bad-rel
  (t/is true))

(t/deftest ^:kaocha/skip test-numerictestcase-is-testcase
  (t/is true))

(t/deftest ^:kaocha/skip test-error-msg-numeric
  (t/is true))

(t/deftest ^:kaocha/skip test-error-msg-sequence
  (t/is true))

(t/deftest ^:kaocha/skip test-meta
  (t/is true))

(t/deftest ^:kaocha/skip test-check-all
  (t/is true))

(t/deftest ^:kaocha/skip test-has-exception
  (t/is true))

(t/deftest ^:kaocha/skip test-exactratiotest-int
  (t/is true))

(t/deftest ^:kaocha/skip test-exactratiotest-fraction
  (t/is true))

(t/deftest ^:kaocha/skip test-exactratiotest-float
  (t/is true))

(t/deftest ^:kaocha/skip test-exactratiotest-decimal
  (t/is true))

(t/deftest ^:kaocha/skip test-exactratiotest-inf
  (t/is true))

(t/deftest ^:kaocha/skip test-float-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-decimal-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-decimaltoratiotest-infinity
  (t/is true))

(t/deftest ^:kaocha/skip test-decimaltoratiotest-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-sign
  (t/is true))

(t/deftest ^:kaocha/skip test-negative-exponent
  (t/is true))

(t/deftest ^:kaocha/skip test-positive-exponent
  (t/is true))

(t/deftest ^:kaocha/skip test-regression-20536
  (t/is true))

(t/deftest ^:kaocha/skip test-finite
  (t/is true))

(t/deftest ^:kaocha/skip test-isfinitetest-infinity
  (t/is true))

(t/deftest ^:kaocha/skip test-isfinitetest-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-bool
  (t/is true))

(t/deftest ^:kaocha/skip test-w-int
  (t/is true))

(t/deftest ^:kaocha/skip test-w-fraction
  (t/is true))

(t/deftest ^:kaocha/skip test-w-decimal
  (t/is true))

(t/deftest ^:kaocha/skip test-w-float
  (t/is true))

(t/deftest ^:kaocha/skip test-non-numeric-types
  (t/is true))

(t/deftest ^:kaocha/skip test-incompatible-types
  (t/is true))

(t/deftest ^:kaocha/skip test-converttest-int
  (t/is true))

(t/deftest ^:kaocha/skip test-myint-fraction
  (t/is true))

(t/deftest ^:kaocha/skip test-myfraction-float
  (t/is true))

(t/deftest ^:kaocha/skip test-myfloat-decimal
  (t/is true))

(t/deftest ^:kaocha/skip test-mydecimal-inf
  (t/is true))

(t/deftest ^:kaocha/skip test-mydecimal-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-invalid-input-type
  (t/is true))

(t/deftest ^:kaocha/skip test-pass-through
  (t/is true))

(t/deftest ^:kaocha/skip test-negatives-raise
  (t/is true))

(t/deftest ^:kaocha/skip test-error-msg
  (t/is true))

(t/deftest ^:kaocha/skip test-no-args
  (t/is true))

(t/deftest ^:kaocha/skip test-univariatecommon-empty-data
  (t/is true))

(t/deftest ^:kaocha/skip test-no-inplace-modifications
  (t/is true))

(t/deftest ^:kaocha/skip test-order-doesnt-matter
  (t/is true))

(t/deftest ^:kaocha/skip test-type-of-data-collection
  (t/is true))

(t/deftest ^:kaocha/skip test-mytuple-range-data
  (t/is true))

(t/deftest ^:kaocha/skip test-bad-arg-types
  (t/is true))

(t/deftest ^:kaocha/skip test-type-of-data-element
  (t/is true))

(t/deftest ^:kaocha/skip test-types-conserved
  (t/is true))

(t/deftest ^:kaocha/skip test-sum-empty-data
  (t/is true))

(t/deftest ^:kaocha/skip test-sum-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-sum-floats
  (t/is true))

(t/deftest ^:kaocha/skip test-sum-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-sum-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-compare-with-math-fsum
  (t/is true))

(t/deftest ^:kaocha/skip test-strings-fail
  (t/is true))

(t/deftest ^:kaocha/skip test-bytes-fail
  (t/is true))

(t/deftest ^:kaocha/skip test-mixed-sum
  (t/is true))

(t/deftest ^:kaocha/skip test-torture
  (t/is true))

(t/deftest ^:kaocha/skip test-sumspecialvalues-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-float-inf
  (t/is true))

(t/deftest ^:kaocha/skip test-decimal-inf
  (t/is true))

(t/deftest ^:kaocha/skip test-float-mismatched-infs
  (t/is true))

(t/deftest ^:kaocha/skip test-decimal-extendedcontext-mismatched-infs-to-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-decimal-basiccontext-mismatched-infs-to-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-decimal-snan-raises
  (t/is true))

(t/deftest ^:kaocha/skip test-average-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-average-repeated-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-torture-pep
  (t/is true))

(t/deftest ^:kaocha/skip test-mean-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-mean-floats
  (t/is true))

(t/deftest ^:kaocha/skip test-mean-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-mean-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-mean-inf
  (t/is true))

(t/deftest ^:kaocha/skip test-mismatched-infs
  (t/is true))

(t/deftest ^:kaocha/skip test-mean-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-big-data
  (t/is true))

(t/deftest ^:kaocha/skip test-mean-doubled-data
  (t/is true))

(t/deftest ^:kaocha/skip test-regression-20561
  (t/is true))

(t/deftest ^:kaocha/skip test-regression-25177
  (t/is true))

(t/deftest ^:kaocha/skip test-zero
  (t/is true))

(t/deftest ^:kaocha/skip test-negative-error
  (t/is true))

(t/deftest ^:kaocha/skip test-invalid-type-error
  (t/is true))

(t/deftest ^:kaocha/skip test-harmonicmean-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-floats-exact
  (t/is true))

(t/deftest ^:kaocha/skip test-singleton-lists
  (t/is true))

(t/deftest ^:kaocha/skip test-decimals-exact
  (t/is true))

(t/deftest ^:kaocha/skip test-harmonicmean-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-harmonicmean-inf
  (t/is true))

(t/deftest ^:kaocha/skip test-harmonicmean-nan
  (t/is true))

(t/deftest ^:kaocha/skip test-multiply-data-points
  (t/is true))

(t/deftest ^:kaocha/skip test-harmonicmean-doubled-data
  (t/is true))

(t/deftest ^:kaocha/skip test-with-weights
  (t/is true))

(t/deftest ^:kaocha/skip test-median-even-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-odd-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-median-odd-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-median-even-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-median-odd-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-median-even-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-medianlow-even-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-medianlow-even-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-medianlow-even-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-medianhigh-even-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-medianhigh-even-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-medianhigh-even-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-odd-number-repeated
  (t/is true))

(t/deftest ^:kaocha/skip test-even-number-repeated
  (t/is true))

(t/deftest ^:kaocha/skip test-mediangrouped-repeated-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-mediangrouped-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-mediangrouped-odd-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-mediangrouped-even-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-mediangrouped-odd-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-mediangrouped-even-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-interval
  (t/is true))

(t/deftest ^:kaocha/skip test-data-type-error
  (t/is true))

(t/deftest ^:kaocha/skip test-mode-range-data
  (t/is true))

(t/deftest ^:kaocha/skip test-nominal-data
  (t/is true))

(t/deftest ^:kaocha/skip test-discrete-data
  (t/is true))

(t/deftest ^:kaocha/skip test-bimodal-data
  (t/is true))

(t/deftest ^:kaocha/skip test-unique-data
  (t/is true))

(t/deftest ^:kaocha/skip test-none-data
  (t/is true))

(t/deftest ^:kaocha/skip test-counter-data
  (t/is true))

(t/deftest ^:kaocha/skip test-multimode-basics
  (t/is true))

(t/deftest ^:kaocha/skip test-fmean-basics
  (t/is true))

(t/deftest ^:kaocha/skip test-fmean-error-cases
  (t/is true))

(t/deftest ^:kaocha/skip test-fmean-special-values
  (t/is true))

(t/deftest ^:kaocha/skip test-weights
  (t/is true))

(t/deftest ^:kaocha/skip test-variancestdev-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-variancestdev-repeated-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-domain-error-regression
  (t/is true))

(t/deftest ^:kaocha/skip test-shift-data
  (t/is true))

(t/deftest ^:kaocha/skip test-shift-data-exact
  (t/is true))

(t/deftest ^:kaocha/skip test-iter-list-same
  (t/is true))

(t/deftest ^:kaocha/skip test-exact-uniform
  (t/is true))

(t/deftest ^:kaocha/skip test-pvariance-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-pvariance-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-pvariance-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-pvariance-accuracy-bug-20499
  (t/is true))

(t/deftest ^:kaocha/skip test-variance-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-variance-ints
  (t/is true))

(t/deftest ^:kaocha/skip test-variance-fractions
  (t/is true))

(t/deftest ^:kaocha/skip test-variance-decimals
  (t/is true))

(t/deftest ^:kaocha/skip test-variance-center-not-at-mean
  (t/is true))

(t/deftest ^:kaocha/skip test-variance-accuracy-bug-20499
  (t/is true))

(t/deftest ^:kaocha/skip test-pstdev-compare-to-variance
  (t/is true))

(t/deftest ^:kaocha/skip test-pstdev-center-not-at-mean
  (t/is true))

(t/deftest ^:kaocha/skip test-gh-140938
  (t/is true))

(t/deftest ^:kaocha/skip test-integer-sqrt-of-frac-rto
  (t/is true))

(t/deftest ^:kaocha/skip test-float-sqrt-of-frac
  (t/is true))

(t/deftest ^:kaocha/skip test-decimal-sqrt-of-frac
  (t/is true))

(t/deftest ^:kaocha/skip test-stdev-single-value
  (t/is true))

(t/deftest ^:kaocha/skip test-stdev-compare-to-variance
  (t/is true))

(t/deftest ^:kaocha/skip test-stdev-center-not-at-mean
  (t/is true))

(t/deftest ^:kaocha/skip test-geometricmean-basics
  (t/is true))

(t/deftest ^:kaocha/skip test-various-input-types
  (t/is true))

(t/deftest ^:kaocha/skip test-big-and-small
  (t/is true))

(t/deftest ^:kaocha/skip test-geometricmean-error-cases
  (t/is true))

(t/deftest ^:kaocha/skip test-geometricmean-special-values
  (t/is true))

(t/deftest ^:kaocha/skip test-mixed-int-and-float
  (t/is true))

(t/deftest ^:kaocha/skip test-kde
  (t/is true))

(t/deftest ^:kaocha/skip test-kde-kernel-specs
  (t/is true))

(t/deftest ^:kaocha/skip test-kde-random
  (t/is true))

(t/deftest ^:kaocha/skip test-specific-cases
  (t/is true))

(t/deftest ^:kaocha/skip test-specific-cases-inclusive
  (t/is true))

(t/deftest ^:kaocha/skip test-equal-inputs
  (t/is true))

(t/deftest ^:kaocha/skip test-equal-sized-groups
  (t/is true))

(t/deftest ^:kaocha/skip test-quantiles-error-cases
  (t/is true))

(t/deftest ^:kaocha/skip test-unequal-size-error
  (t/is true))

(t/deftest ^:kaocha/skip test-small-sample-error
  (t/is true))

(t/deftest ^:kaocha/skip test-correlationandcovariance-results
  (t/is true))

(t/deftest ^:kaocha/skip test-different-scales
  (t/is true))

(t/deftest ^:kaocha/skip test-sqrtprod-helper-function-fundamentals
  (t/is true))

(t/deftest ^:kaocha/skip test-sqrtprod-helper-function-improved-accuracy
  (t/is true))

(t/deftest ^:kaocha/skip test-correlation-spearman
  (t/is true))

(t/deftest ^:kaocha/skip test-constant-input-error
  (t/is true))

(t/deftest ^:kaocha/skip test-linearregression-results
  (t/is true))

(t/deftest ^:kaocha/skip test-proportional
  (t/is true))

(t/deftest ^:kaocha/skip test-float-output
  (t/is true))

(t/deftest ^:kaocha/skip test-slots
  (t/is true))

(t/deftest ^:kaocha/skip test-instantiation-and-attributes
  (t/is true))

(t/deftest ^:kaocha/skip test-alternative-constructor
  (t/is true))

(t/deftest ^:kaocha/skip test-sample-generation
  (t/is true))

(t/deftest ^:kaocha/skip test-pdf
  (t/is true))

(t/deftest ^:kaocha/skip test-cdf
  (t/is true))

(t/deftest ^:kaocha/skip test-inv-cdf
  (t/is true))

(t/deftest ^:kaocha/skip test-quantiles
  (t/is true))

(t/deftest ^:kaocha/skip test-overlap
  (t/is true))

(t/deftest ^:kaocha/skip test-zscore
  (t/is true))

(t/deftest ^:kaocha/skip test-properties
  (t/is true))

(t/deftest ^:kaocha/skip test-same-type-addition-and-subtraction
  (t/is true))

(t/deftest ^:kaocha/skip test-translation-and-scaling
  (t/is true))

(t/deftest ^:kaocha/skip test-unary-operations
  (t/is true))

(t/deftest ^:kaocha/skip test-equality
  (t/is true))

(t/deftest ^:kaocha/skip test-copy
  (t/is true))

(t/deftest ^:kaocha/skip test-pickle
  (t/is true))

(t/deftest ^:kaocha/skip test-hashability
  (t/is true))

(t/deftest ^:kaocha/skip test-repr
  (t/is true))

