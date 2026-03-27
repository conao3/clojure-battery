;; Original: Lib/test/test_argparse.py

(ns conao3.battery.argparse-test
  (:require
   [clojure.test :as t]
   [conao3.battery.argparse :as argparse])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest ^:kaocha/skip test-skip-invalid-stderr
  (t/is (= :argparse/test-skip-invalid-stderr (argparse/test-dispatch :argparse/test-skip-invalid-stderr))))

(t/deftest ^:kaocha/skip test-skip-invalid-stdout
  (t/is (= :argparse/test-skip-invalid-stdout (argparse/test-dispatch :argparse/test-skip-invalid-stdout))))

(t/deftest ^:kaocha/skip test-pickle-roundtrip
  (t/is (= :argparse/test-pickle-roundtrip (argparse/test-dispatch :argparse/test-pickle-roundtrip))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-combined-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-combined-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-combined-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-combined-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-combined-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-combined-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-combined-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-combined-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-combined-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-combined-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-combined-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-combined-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-combined-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-combined-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-long-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-long-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-long-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-long-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-long-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-long-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-long-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-long-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-long-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-long-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-long-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-long-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-long-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-long-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-subset-ambiguous-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-subset-ambiguous-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-subset-ambiguous-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-dash-ambiguous-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-single-dash-ambiguous-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-dash-ambiguous-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-numeric-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-numeric-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-numeric-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-numeric-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-numeric-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-numeric-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-numeric-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-numeric-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-numeric-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-numeric-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-numeric-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-numeric-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-numeric-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-numeric-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-numeric-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-numeric-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-double-dash-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-double-dash-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-double-dash-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-double-dash-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-double-dash-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-double-dash-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-double-dash-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-double-dash-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-double-dash-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-double-dash-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-partial-match-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-partial-match-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-partial-match-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-double-dash-prefix-match-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-double-dash-prefix-match-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-double-dash-prefix-match-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-single-double-dash-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-single-double-dash-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-single-double-dash-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-single-double-dash-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-single-double-dash-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-single-double-dash-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-single-double-dash-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-single-double-dash-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-single-double-dash-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-single-double-dash-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-single-double-dash-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-single-double-dash-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-single-double-dash-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-single-double-dash-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-added-help-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-added-help-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-added-help-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-short-long-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-short-long-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-short-long-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-short-long-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-short-long-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-short-long-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-short-long-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-short-long-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-short-long-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-short-long-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-short-long-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-short-long-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-short-long-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-short-long-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-short-long-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-short-long-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-short-long-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-short-long-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-short-long-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-short-long-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-short-long-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-short-long-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-short-long-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-short-long-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-short-long-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-dest-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-dest-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-dest-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-dest-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-dest-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-dest-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-dest-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-dest-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-dest-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-dest-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-dest-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-dest-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-dest-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-dest-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-dest-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-dest-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-dest-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-dest-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-dest-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-dest-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-dest-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-dest-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-dest-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-dest-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-dest-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-default-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-default-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-default-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-default-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-default-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-default-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-default-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-default-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-default-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-default-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-default-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-default-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-default-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-default-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-default-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-default-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-default-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-default-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-default-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-default-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-default-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-default-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-default-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-default-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-default-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-default-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-default-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-default-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-default-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-default-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-default-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-default-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-default-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-default-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-default-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-default-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-default-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-default-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-default-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-default-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-nargs-default-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-nargs-default-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-default-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-default-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-default-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-default-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-default-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-default-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-default-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-default-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-nargs1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-nargs1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-nargs1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-nargs1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-nargs1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-nargs1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-nargs1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-nargs1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs1-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-nargs3-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs3-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-nargs3-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs3-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs3-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs3-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs3-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs3-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-nargs3-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs3-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-nargs3-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs3-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-nargs3-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs3-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-nargs3-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs3-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-nargs3-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs3-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-nargs3-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs3-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs3-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs3-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs3-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs3-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs3-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-optional-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-optional-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-optional-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-optional-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-nargs-optional-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-nargs-optional-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-optional-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-optional-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-optional-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-optional-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-optional-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-optional-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-optional-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-optional-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-zero-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-zero-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-zero-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-nargs-one-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-nargs-one-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-nargs-one-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-choices-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-choices-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-choices-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-choices-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-choices-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-choices-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-choices-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-choices-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-choices-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-choices-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-choices-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-choices-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-choices-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-choices-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-choices-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-choices-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-choices-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-choices-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-choices-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-choices-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-choices-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-choices-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-choices-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-choices-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-choices-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-required-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-required-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-required-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-required-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-required-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-required-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-required-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-required-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-required-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-required-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-required-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-required-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-required-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-required-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-required-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-required-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-required-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-required-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-required-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-required-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-required-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-required-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-required-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-required-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-required-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-required-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-required-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-required-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-required-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-required-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-required-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-required-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-required-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-required-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-required-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-required-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-store-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-store-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-const-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-const-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-const-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-const-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-store-const-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-store-const-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-const-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-const-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-const-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-const-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-const-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-const-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-const-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-const-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-false-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-false-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-false-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-false-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-store-false-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-store-false-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-false-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-false-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-false-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-false-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-false-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-false-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-false-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-false-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-true-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-store-true-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-true-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-store-true-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-store-true-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-store-true-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-true-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-store-true-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-true-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-store-true-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-true-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-store-true-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-store-true-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-store-true-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip test-const
  (t/is (= :argparse/test-const (argparse/test-dispatch :argparse/test-const))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-invalid-name
  (t/is (= :argparse/boolean-optional-action-test-invalid-name (argparse/test-dispatch :argparse/boolean-optional-action-test-invalid-name))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-failures-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-successes-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-failures-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-successes-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-failures-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-test-failures-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-successes-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-test-successes-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-failures-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-successes-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-failures-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-successes-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-failures-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-test-successes-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-invalid-name
  (t/is (= :argparse/boolean-optional-action-single-dash-test-invalid-name (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-invalid-name))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-failures-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-successes-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-failures-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-successes-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-failures-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-failures-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-successes-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-successes-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-failures-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-successes-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-failures-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-successes-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-failures-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-dash-test-successes-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-dash-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-dash-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-invalid-name
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-invalid-name (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-invalid-name))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-failures-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-successes-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-failures-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-successes-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-invalid-name
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-invalid-name (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-invalid-name))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-failures-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-required-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-successes-no-groups-listargs
  (t/is (= :argparse/boolean-optional-action-required-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-failures-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-required-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-successes-no-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-required-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-failures-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-required-test-failures-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-successes-one-group-listargs
  (t/is (= :argparse/boolean-optional-action-required-test-successes-one-group-listargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-failures-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-required-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-successes-one-group-sysargs
  (t/is (= :argparse/boolean-optional-action-required-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-failures-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-required-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-successes-many-groups-listargs
  (t/is (= :argparse/boolean-optional-action-required-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-failures-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-required-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip boolean-optional-action-required-test-successes-many-groups-sysargs
  (t/is (= :argparse/boolean-optional-action-required-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/boolean-optional-action-required-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-append-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-append-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-with-default-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-with-default-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-with-default-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-with-default-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-append-with-default-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-append-with-default-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-with-default-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-with-default-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-with-default-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-with-default-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-with-default-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-with-default-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-with-default-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-with-default-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-failures-no-groups-listargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-successes-no-groups-listargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-failures-no-groups-sysargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-successes-no-groups-sysargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-failures-one-group-listargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-failures-one-group-listargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-successes-one-group-listargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-successes-one-group-listargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-failures-one-group-sysargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-successes-one-group-sysargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-failures-many-groups-listargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-successes-many-groups-listargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-failures-many-groups-sysargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip const-actions-missing-const-kwarg-test-successes-many-groups-sysargs
  (t/is (= :argparse/const-actions-missing-const-kwarg-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/const-actions-missing-const-kwarg-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-append-const-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-append-const-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-const-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-const-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-append-const-with-default-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-append-const-with-default-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-append-const-with-default-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-action-count-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-count-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-action-count-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-action-count-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-action-count-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-count-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-action-count-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-count-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-action-count-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-count-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-action-count-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-action-count-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-action-count-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-count-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-action-count-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-action-count-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-action-count-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-count-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-action-count-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-action-count-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-action-count-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-count-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-action-count-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-action-count-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-action-count-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-allow-long-abbreviation-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-allow-long-abbreviation-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-allow-long-abbreviation-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-listargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-sysargs
  (t/is (= :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip test-parse-enum-value
  (t/is (= :argparse/test-parse-enum-value (argparse/test-dispatch :argparse/test-parse-enum-value))))

(t/deftest ^:kaocha/skip test-help-message-contains-enum-choices
  (t/is (= :argparse/test-help-message-contains-enum-choices (argparse/test-dispatch :argparse/test-help-message-contains-enum-choices))))

(t/deftest ^:kaocha/skip test-invalid-enum-value-raises-error
  (t/is (= :argparse/test-invalid-enum-value-raises-error (argparse/test-dispatch :argparse/test-invalid-enum-value-raises-error))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-default-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-default-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-default-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-default-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-default-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-default-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-converted-default-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-converted-default-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-converted-default-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-none-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-none-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-none-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-none-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-none-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-none-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-none-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-none-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-none-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-none-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-none-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-none-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-none-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-none-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-none-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-none-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-none-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-none-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-none-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-none-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-none-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-none-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-none-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-none-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-none-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-none-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-none-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-none-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more-none-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more-none-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more-none-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more-none-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more-none-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more-none-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-none-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-none-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-none-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-zero-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-zero-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-zero-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-one-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-one-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-one-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-optional-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs2-optional-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-optional-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-optional-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-optional-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs2-optional-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-optional-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs2-optional-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-optional-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs2-optional-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-optional-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs2-optional-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs2-optional-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs2-optional-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-zero-or-more1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-zero-or-more1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-zero-or-more1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-one-or-more1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-one-or-more1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-one-or-more1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-zero-or-more1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-zero-or-more1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-zero-or-more1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-one-or-more1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-one-or-more1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-one-or-more1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-none-optional1-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-none-optional1-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-none-optional1-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-optional-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-optional-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-optional-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-zero-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-zero-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-zero-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-nargs-optional-one-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-nargs-optional-one-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-nargs-optional-one-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-choices-string-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-string-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-choices-string-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-string-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-choices-string-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-string-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-choices-string-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-string-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-choices-string-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-choices-string-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-choices-string-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-choices-string-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-choices-string-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-choices-string-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-choices-string-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-choices-string-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-choices-string-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-string-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-choices-string-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-string-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-choices-string-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-string-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-string-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-choices-string-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-string-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-choices-int-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-int-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-choices-int-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-int-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-choices-int-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-int-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-choices-int-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-int-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-choices-int-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-choices-int-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-choices-int-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-choices-int-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-choices-int-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-choices-int-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-choices-int-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-choices-int-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-choices-int-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-int-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-choices-int-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-choices-int-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-choices-int-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-int-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-choices-int-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-choices-int-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-choices-int-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-action-append-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-action-append-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-action-append-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-action-append-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-action-append-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-append-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-action-append-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-append-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-action-append-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-action-append-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-action-append-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-action-append-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-action-append-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-action-append-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-action-append-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-action-append-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-action-append-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-action-append-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-action-append-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-action-append-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-action-append-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-append-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-append-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-action-append-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-append-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-failures-no-groups-listargs
  (t/is (= :argparse/positionals-action-extend-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/positionals-action-extend-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-successes-no-groups-listargs
  (t/is (= :argparse/positionals-action-extend-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/positionals-action-extend-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-failures-no-groups-sysargs
  (t/is (= :argparse/positionals-action-extend-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-extend-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-successes-no-groups-sysargs
  (t/is (= :argparse/positionals-action-extend-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-extend-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-failures-one-group-listargs
  (t/is (= :argparse/positionals-action-extend-test-failures-one-group-listargs (argparse/test-dispatch :argparse/positionals-action-extend-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-successes-one-group-listargs
  (t/is (= :argparse/positionals-action-extend-test-successes-one-group-listargs (argparse/test-dispatch :argparse/positionals-action-extend-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-failures-one-group-sysargs
  (t/is (= :argparse/positionals-action-extend-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/positionals-action-extend-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-successes-one-group-sysargs
  (t/is (= :argparse/positionals-action-extend-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/positionals-action-extend-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-failures-many-groups-listargs
  (t/is (= :argparse/positionals-action-extend-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/positionals-action-extend-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-successes-many-groups-listargs
  (t/is (= :argparse/positionals-action-extend-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/positionals-action-extend-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-failures-many-groups-sysargs
  (t/is (= :argparse/positionals-action-extend-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-extend-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip positionals-action-extend-test-successes-many-groups-sysargs
  (t/is (= :argparse/positionals-action-extend-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/positionals-action-extend-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-numeric-and-positionals-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-numeric-and-positionals-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-numeric-and-positionals-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-almost-numeric-and-positionals-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-almost-numeric-and-positionals-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-almost-numeric-and-positionals-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-append-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-append-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-append-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-append-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-and-positionals-append-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-and-positionals-append-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-and-positionals-append-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-and-positionals-append-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-append-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-append-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-append-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-append-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-append-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-append-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-failures-no-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-successes-no-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-failures-no-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-successes-no-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-failures-one-group-listargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-failures-one-group-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-successes-one-group-listargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-successes-one-group-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-failures-one-group-sysargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-successes-one-group-sysargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-failures-many-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-successes-many-groups-listargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-failures-many-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip optionals-and-positionals-extend-test-successes-many-groups-sysargs
  (t/is (= :argparse/optionals-and-positionals-extend-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/optionals-and-positionals-extend-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-failures-no-groups-listargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-successes-no-groups-listargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-failures-no-groups-sysargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-successes-no-groups-sysargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-failures-one-group-listargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-failures-one-group-listargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-successes-one-group-listargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-successes-one-group-listargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-failures-one-group-sysargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-successes-one-group-sysargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-failures-many-groups-listargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-successes-many-groups-listargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-failures-many-groups-sysargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip empty-and-space-containing-arguments-test-successes-many-groups-sysargs
  (t/is (= :argparse/empty-and-space-containing-arguments-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/empty-and-space-containing-arguments-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-failures-no-groups-listargs
  (t/is (= :argparse/prefix-character-only-arguments-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-successes-no-groups-listargs
  (t/is (= :argparse/prefix-character-only-arguments-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-failures-no-groups-sysargs
  (t/is (= :argparse/prefix-character-only-arguments-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-successes-no-groups-sysargs
  (t/is (= :argparse/prefix-character-only-arguments-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-failures-one-group-listargs
  (t/is (= :argparse/prefix-character-only-arguments-test-failures-one-group-listargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-successes-one-group-listargs
  (t/is (= :argparse/prefix-character-only-arguments-test-successes-one-group-listargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-failures-one-group-sysargs
  (t/is (= :argparse/prefix-character-only-arguments-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-successes-one-group-sysargs
  (t/is (= :argparse/prefix-character-only-arguments-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-failures-many-groups-listargs
  (t/is (= :argparse/prefix-character-only-arguments-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-successes-many-groups-listargs
  (t/is (= :argparse/prefix-character-only-arguments-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-failures-many-groups-sysargs
  (t/is (= :argparse/prefix-character-only-arguments-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip prefix-character-only-arguments-test-successes-many-groups-sysargs
  (t/is (= :argparse/prefix-character-only-arguments-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/prefix-character-only-arguments-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-failures-no-groups-listargs
  (t/is (= :argparse/nargs-zero-or-more-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-successes-no-groups-listargs
  (t/is (= :argparse/nargs-zero-or-more-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-failures-no-groups-sysargs
  (t/is (= :argparse/nargs-zero-or-more-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-successes-no-groups-sysargs
  (t/is (= :argparse/nargs-zero-or-more-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-failures-one-group-listargs
  (t/is (= :argparse/nargs-zero-or-more-test-failures-one-group-listargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-successes-one-group-listargs
  (t/is (= :argparse/nargs-zero-or-more-test-successes-one-group-listargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-failures-one-group-sysargs
  (t/is (= :argparse/nargs-zero-or-more-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-successes-one-group-sysargs
  (t/is (= :argparse/nargs-zero-or-more-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-failures-many-groups-listargs
  (t/is (= :argparse/nargs-zero-or-more-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-successes-many-groups-listargs
  (t/is (= :argparse/nargs-zero-or-more-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-failures-many-groups-sysargs
  (t/is (= :argparse/nargs-zero-or-more-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-zero-or-more-test-successes-many-groups-sysargs
  (t/is (= :argparse/nargs-zero-or-more-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/nargs-zero-or-more-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-failures-no-groups-listargs
  (t/is (= :argparse/nargs-remainder-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/nargs-remainder-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-successes-no-groups-listargs
  (t/is (= :argparse/nargs-remainder-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/nargs-remainder-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-failures-no-groups-sysargs
  (t/is (= :argparse/nargs-remainder-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/nargs-remainder-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-successes-no-groups-sysargs
  (t/is (= :argparse/nargs-remainder-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/nargs-remainder-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-failures-one-group-listargs
  (t/is (= :argparse/nargs-remainder-test-failures-one-group-listargs (argparse/test-dispatch :argparse/nargs-remainder-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-successes-one-group-listargs
  (t/is (= :argparse/nargs-remainder-test-successes-one-group-listargs (argparse/test-dispatch :argparse/nargs-remainder-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-failures-one-group-sysargs
  (t/is (= :argparse/nargs-remainder-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/nargs-remainder-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-successes-one-group-sysargs
  (t/is (= :argparse/nargs-remainder-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/nargs-remainder-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-failures-many-groups-listargs
  (t/is (= :argparse/nargs-remainder-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/nargs-remainder-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-successes-many-groups-listargs
  (t/is (= :argparse/nargs-remainder-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/nargs-remainder-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-failures-many-groups-sysargs
  (t/is (= :argparse/nargs-remainder-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/nargs-remainder-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip nargs-remainder-test-successes-many-groups-sysargs
  (t/is (= :argparse/nargs-remainder-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/nargs-remainder-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip option-like-test-failures-no-groups-listargs
  (t/is (= :argparse/option-like-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/option-like-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip option-like-test-successes-no-groups-listargs
  (t/is (= :argparse/option-like-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/option-like-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip option-like-test-failures-no-groups-sysargs
  (t/is (= :argparse/option-like-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/option-like-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip option-like-test-successes-no-groups-sysargs
  (t/is (= :argparse/option-like-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/option-like-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip option-like-test-failures-one-group-listargs
  (t/is (= :argparse/option-like-test-failures-one-group-listargs (argparse/test-dispatch :argparse/option-like-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip option-like-test-successes-one-group-listargs
  (t/is (= :argparse/option-like-test-successes-one-group-listargs (argparse/test-dispatch :argparse/option-like-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip option-like-test-failures-one-group-sysargs
  (t/is (= :argparse/option-like-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/option-like-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip option-like-test-successes-one-group-sysargs
  (t/is (= :argparse/option-like-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/option-like-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip option-like-test-failures-many-groups-listargs
  (t/is (= :argparse/option-like-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/option-like-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip option-like-test-successes-many-groups-listargs
  (t/is (= :argparse/option-like-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/option-like-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip option-like-test-failures-many-groups-sysargs
  (t/is (= :argparse/option-like-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/option-like-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip option-like-test-successes-many-groups-sysargs
  (t/is (= :argparse/option-like-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/option-like-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip default-suppress-test-failures-no-groups-listargs
  (t/is (= :argparse/default-suppress-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/default-suppress-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip default-suppress-test-successes-no-groups-listargs
  (t/is (= :argparse/default-suppress-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/default-suppress-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip default-suppress-test-failures-no-groups-sysargs
  (t/is (= :argparse/default-suppress-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/default-suppress-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip default-suppress-test-successes-no-groups-sysargs
  (t/is (= :argparse/default-suppress-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/default-suppress-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip default-suppress-test-failures-one-group-listargs
  (t/is (= :argparse/default-suppress-test-failures-one-group-listargs (argparse/test-dispatch :argparse/default-suppress-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip default-suppress-test-successes-one-group-listargs
  (t/is (= :argparse/default-suppress-test-successes-one-group-listargs (argparse/test-dispatch :argparse/default-suppress-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip default-suppress-test-failures-one-group-sysargs
  (t/is (= :argparse/default-suppress-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/default-suppress-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip default-suppress-test-successes-one-group-sysargs
  (t/is (= :argparse/default-suppress-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/default-suppress-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip default-suppress-test-failures-many-groups-listargs
  (t/is (= :argparse/default-suppress-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/default-suppress-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip default-suppress-test-successes-many-groups-listargs
  (t/is (= :argparse/default-suppress-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/default-suppress-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip default-suppress-test-failures-many-groups-sysargs
  (t/is (= :argparse/default-suppress-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/default-suppress-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip default-suppress-test-successes-many-groups-sysargs
  (t/is (= :argparse/default-suppress-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/default-suppress-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-failures-no-groups-listargs
  (t/is (= :argparse/parser-default-suppress-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/parser-default-suppress-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-successes-no-groups-listargs
  (t/is (= :argparse/parser-default-suppress-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/parser-default-suppress-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-failures-no-groups-sysargs
  (t/is (= :argparse/parser-default-suppress-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/parser-default-suppress-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-successes-no-groups-sysargs
  (t/is (= :argparse/parser-default-suppress-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/parser-default-suppress-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-failures-one-group-listargs
  (t/is (= :argparse/parser-default-suppress-test-failures-one-group-listargs (argparse/test-dispatch :argparse/parser-default-suppress-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-successes-one-group-listargs
  (t/is (= :argparse/parser-default-suppress-test-successes-one-group-listargs (argparse/test-dispatch :argparse/parser-default-suppress-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-failures-one-group-sysargs
  (t/is (= :argparse/parser-default-suppress-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/parser-default-suppress-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-successes-one-group-sysargs
  (t/is (= :argparse/parser-default-suppress-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/parser-default-suppress-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-failures-many-groups-listargs
  (t/is (= :argparse/parser-default-suppress-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/parser-default-suppress-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-successes-many-groups-listargs
  (t/is (= :argparse/parser-default-suppress-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/parser-default-suppress-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-failures-many-groups-sysargs
  (t/is (= :argparse/parser-default-suppress-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/parser-default-suppress-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default-suppress-test-successes-many-groups-sysargs
  (t/is (= :argparse/parser-default-suppress-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/parser-default-suppress-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default42-test-failures-no-groups-listargs
  (t/is (= :argparse/parser-default42-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/parser-default42-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default42-test-successes-no-groups-listargs
  (t/is (= :argparse/parser-default42-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/parser-default42-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default42-test-failures-no-groups-sysargs
  (t/is (= :argparse/parser-default42-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/parser-default42-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default42-test-successes-no-groups-sysargs
  (t/is (= :argparse/parser-default42-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/parser-default42-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default42-test-failures-one-group-listargs
  (t/is (= :argparse/parser-default42-test-failures-one-group-listargs (argparse/test-dispatch :argparse/parser-default42-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip parser-default42-test-successes-one-group-listargs
  (t/is (= :argparse/parser-default42-test-successes-one-group-listargs (argparse/test-dispatch :argparse/parser-default42-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip parser-default42-test-failures-one-group-sysargs
  (t/is (= :argparse/parser-default42-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/parser-default42-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip parser-default42-test-successes-one-group-sysargs
  (t/is (= :argparse/parser-default42-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/parser-default42-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip parser-default42-test-failures-many-groups-listargs
  (t/is (= :argparse/parser-default42-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/parser-default42-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default42-test-successes-many-groups-listargs
  (t/is (= :argparse/parser-default42-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/parser-default42-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip parser-default42-test-failures-many-groups-sysargs
  (t/is (= :argparse/parser-default42-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/parser-default42-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip parser-default42-test-successes-many-groups-sysargs
  (t/is (= :argparse/parser-default42-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/parser-default42-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip test-r
  (t/is (= :argparse/test-r (argparse/test-dispatch :argparse/test-r))))

(t/deftest ^:kaocha/skip test-wb-1
  (t/is (= :argparse/test-wb-1 (argparse/test-dispatch :argparse/test-wb-1))))

(t/deftest ^:kaocha/skip test-r-latin
  (t/is (= :argparse/test-r-latin (argparse/test-dispatch :argparse/test-r-latin))))

(t/deftest ^:kaocha/skip test-w-big5-ignore
  (t/is (= :argparse/test-w-big5-ignore (argparse/test-dispatch :argparse/test-w-big5-ignore))))

(t/deftest ^:kaocha/skip test-r-1-replace
  (t/is (= :argparse/test-r-1-replace (argparse/test-dispatch :argparse/test-r-1-replace))))

(t/deftest ^:kaocha/skip test-open-args
  (t/is (= :argparse/test-open-args (argparse/test-dispatch :argparse/test-open-args))))

(t/deftest ^:kaocha/skip test-invalid-file-type
  (t/is (= :argparse/test-invalid-file-type (argparse/test-dispatch :argparse/test-invalid-file-type))))

(t/deftest ^:kaocha/skip type-callable-test-failures-no-groups-listargs
  (t/is (= :argparse/type-callable-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/type-callable-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip type-callable-test-successes-no-groups-listargs
  (t/is (= :argparse/type-callable-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/type-callable-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip type-callable-test-failures-no-groups-sysargs
  (t/is (= :argparse/type-callable-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/type-callable-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip type-callable-test-successes-no-groups-sysargs
  (t/is (= :argparse/type-callable-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/type-callable-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip type-callable-test-failures-one-group-listargs
  (t/is (= :argparse/type-callable-test-failures-one-group-listargs (argparse/test-dispatch :argparse/type-callable-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip type-callable-test-successes-one-group-listargs
  (t/is (= :argparse/type-callable-test-successes-one-group-listargs (argparse/test-dispatch :argparse/type-callable-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip type-callable-test-failures-one-group-sysargs
  (t/is (= :argparse/type-callable-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/type-callable-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip type-callable-test-successes-one-group-sysargs
  (t/is (= :argparse/type-callable-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/type-callable-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip type-callable-test-failures-many-groups-listargs
  (t/is (= :argparse/type-callable-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/type-callable-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip type-callable-test-successes-many-groups-listargs
  (t/is (= :argparse/type-callable-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/type-callable-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip type-callable-test-failures-many-groups-sysargs
  (t/is (= :argparse/type-callable-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/type-callable-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip type-callable-test-successes-many-groups-sysargs
  (t/is (= :argparse/type-callable-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/type-callable-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-failures-no-groups-listargs
  (t/is (= :argparse/type-user-defined-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/type-user-defined-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-successes-no-groups-listargs
  (t/is (= :argparse/type-user-defined-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/type-user-defined-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-failures-no-groups-sysargs
  (t/is (= :argparse/type-user-defined-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/type-user-defined-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-successes-no-groups-sysargs
  (t/is (= :argparse/type-user-defined-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/type-user-defined-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-failures-one-group-listargs
  (t/is (= :argparse/type-user-defined-test-failures-one-group-listargs (argparse/test-dispatch :argparse/type-user-defined-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-successes-one-group-listargs
  (t/is (= :argparse/type-user-defined-test-successes-one-group-listargs (argparse/test-dispatch :argparse/type-user-defined-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-failures-one-group-sysargs
  (t/is (= :argparse/type-user-defined-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/type-user-defined-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-successes-one-group-sysargs
  (t/is (= :argparse/type-user-defined-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/type-user-defined-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-failures-many-groups-listargs
  (t/is (= :argparse/type-user-defined-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/type-user-defined-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-successes-many-groups-listargs
  (t/is (= :argparse/type-user-defined-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/type-user-defined-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-failures-many-groups-sysargs
  (t/is (= :argparse/type-user-defined-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/type-user-defined-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip type-user-defined-test-successes-many-groups-sysargs
  (t/is (= :argparse/type-user-defined-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/type-user-defined-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-failures-no-groups-listargs
  (t/is (= :argparse/type-classic-class-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/type-classic-class-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-successes-no-groups-listargs
  (t/is (= :argparse/type-classic-class-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/type-classic-class-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-failures-no-groups-sysargs
  (t/is (= :argparse/type-classic-class-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/type-classic-class-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-successes-no-groups-sysargs
  (t/is (= :argparse/type-classic-class-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/type-classic-class-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-failures-one-group-listargs
  (t/is (= :argparse/type-classic-class-test-failures-one-group-listargs (argparse/test-dispatch :argparse/type-classic-class-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-successes-one-group-listargs
  (t/is (= :argparse/type-classic-class-test-successes-one-group-listargs (argparse/test-dispatch :argparse/type-classic-class-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-failures-one-group-sysargs
  (t/is (= :argparse/type-classic-class-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/type-classic-class-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-successes-one-group-sysargs
  (t/is (= :argparse/type-classic-class-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/type-classic-class-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-failures-many-groups-listargs
  (t/is (= :argparse/type-classic-class-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/type-classic-class-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-successes-many-groups-listargs
  (t/is (= :argparse/type-classic-class-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/type-classic-class-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-failures-many-groups-sysargs
  (t/is (= :argparse/type-classic-class-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/type-classic-class-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip type-classic-class-test-successes-many-groups-sysargs
  (t/is (= :argparse/type-classic-class-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/type-classic-class-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-failures-no-groups-listargs
  (t/is (= :argparse/action-user-defined-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/action-user-defined-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-successes-no-groups-listargs
  (t/is (= :argparse/action-user-defined-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/action-user-defined-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-failures-no-groups-sysargs
  (t/is (= :argparse/action-user-defined-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/action-user-defined-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-successes-no-groups-sysargs
  (t/is (= :argparse/action-user-defined-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/action-user-defined-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-failures-one-group-listargs
  (t/is (= :argparse/action-user-defined-test-failures-one-group-listargs (argparse/test-dispatch :argparse/action-user-defined-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-successes-one-group-listargs
  (t/is (= :argparse/action-user-defined-test-successes-one-group-listargs (argparse/test-dispatch :argparse/action-user-defined-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-failures-one-group-sysargs
  (t/is (= :argparse/action-user-defined-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/action-user-defined-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-successes-one-group-sysargs
  (t/is (= :argparse/action-user-defined-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/action-user-defined-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-failures-many-groups-listargs
  (t/is (= :argparse/action-user-defined-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/action-user-defined-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-successes-many-groups-listargs
  (t/is (= :argparse/action-user-defined-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/action-user-defined-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-failures-many-groups-sysargs
  (t/is (= :argparse/action-user-defined-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/action-user-defined-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip action-user-defined-test-successes-many-groups-sysargs
  (t/is (= :argparse/action-user-defined-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/action-user-defined-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip action-extend-test-failures-no-groups-listargs
  (t/is (= :argparse/action-extend-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/action-extend-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip action-extend-test-successes-no-groups-listargs
  (t/is (= :argparse/action-extend-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/action-extend-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip action-extend-test-failures-no-groups-sysargs
  (t/is (= :argparse/action-extend-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/action-extend-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip action-extend-test-successes-no-groups-sysargs
  (t/is (= :argparse/action-extend-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/action-extend-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip action-extend-test-failures-one-group-listargs
  (t/is (= :argparse/action-extend-test-failures-one-group-listargs (argparse/test-dispatch :argparse/action-extend-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip action-extend-test-successes-one-group-listargs
  (t/is (= :argparse/action-extend-test-successes-one-group-listargs (argparse/test-dispatch :argparse/action-extend-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip action-extend-test-failures-one-group-sysargs
  (t/is (= :argparse/action-extend-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/action-extend-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip action-extend-test-successes-one-group-sysargs
  (t/is (= :argparse/action-extend-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/action-extend-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip action-extend-test-failures-many-groups-listargs
  (t/is (= :argparse/action-extend-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/action-extend-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip action-extend-test-successes-many-groups-listargs
  (t/is (= :argparse/action-extend-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/action-extend-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip action-extend-test-failures-many-groups-sysargs
  (t/is (= :argparse/action-extend-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/action-extend-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip action-extend-test-successes-many-groups-sysargs
  (t/is (= :argparse/action-extend-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/action-extend-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip negative-number-test-failures-no-groups-listargs
  (t/is (= :argparse/negative-number-test-failures-no-groups-listargs (argparse/test-dispatch :argparse/negative-number-test-failures-no-groups-listargs))))

(t/deftest ^:kaocha/skip negative-number-test-successes-no-groups-listargs
  (t/is (= :argparse/negative-number-test-successes-no-groups-listargs (argparse/test-dispatch :argparse/negative-number-test-successes-no-groups-listargs))))

(t/deftest ^:kaocha/skip negative-number-test-failures-no-groups-sysargs
  (t/is (= :argparse/negative-number-test-failures-no-groups-sysargs (argparse/test-dispatch :argparse/negative-number-test-failures-no-groups-sysargs))))

(t/deftest ^:kaocha/skip negative-number-test-successes-no-groups-sysargs
  (t/is (= :argparse/negative-number-test-successes-no-groups-sysargs (argparse/test-dispatch :argparse/negative-number-test-successes-no-groups-sysargs))))

(t/deftest ^:kaocha/skip negative-number-test-failures-one-group-listargs
  (t/is (= :argparse/negative-number-test-failures-one-group-listargs (argparse/test-dispatch :argparse/negative-number-test-failures-one-group-listargs))))

(t/deftest ^:kaocha/skip negative-number-test-successes-one-group-listargs
  (t/is (= :argparse/negative-number-test-successes-one-group-listargs (argparse/test-dispatch :argparse/negative-number-test-successes-one-group-listargs))))

(t/deftest ^:kaocha/skip negative-number-test-failures-one-group-sysargs
  (t/is (= :argparse/negative-number-test-failures-one-group-sysargs (argparse/test-dispatch :argparse/negative-number-test-failures-one-group-sysargs))))

(t/deftest ^:kaocha/skip negative-number-test-successes-one-group-sysargs
  (t/is (= :argparse/negative-number-test-successes-one-group-sysargs (argparse/test-dispatch :argparse/negative-number-test-successes-one-group-sysargs))))

(t/deftest ^:kaocha/skip negative-number-test-failures-many-groups-listargs
  (t/is (= :argparse/negative-number-test-failures-many-groups-listargs (argparse/test-dispatch :argparse/negative-number-test-failures-many-groups-listargs))))

(t/deftest ^:kaocha/skip negative-number-test-successes-many-groups-listargs
  (t/is (= :argparse/negative-number-test-successes-many-groups-listargs (argparse/test-dispatch :argparse/negative-number-test-successes-many-groups-listargs))))

(t/deftest ^:kaocha/skip negative-number-test-failures-many-groups-sysargs
  (t/is (= :argparse/negative-number-test-failures-many-groups-sysargs (argparse/test-dispatch :argparse/negative-number-test-failures-many-groups-sysargs))))

(t/deftest ^:kaocha/skip negative-number-test-successes-many-groups-sysargs
  (t/is (= :argparse/negative-number-test-successes-many-groups-sysargs (argparse/test-dispatch :argparse/negative-number-test-successes-many-groups-sysargs))))

(t/deftest ^:kaocha/skip test-wrong-argument-error-with-suggestions
  (t/is (= :argparse/test-wrong-argument-error-with-suggestions (argparse/test-dispatch :argparse/test-wrong-argument-error-with-suggestions))))

(t/deftest ^:kaocha/skip test-wrong-argument-error-no-suggestions
  (t/is (= :argparse/test-wrong-argument-error-no-suggestions (argparse/test-dispatch :argparse/test-wrong-argument-error-no-suggestions))))

(t/deftest ^:kaocha/skip test-wrong-argument-subparsers-with-suggestions
  (t/is (= :argparse/test-wrong-argument-subparsers-with-suggestions (argparse/test-dispatch :argparse/test-wrong-argument-subparsers-with-suggestions))))

(t/deftest ^:kaocha/skip test-wrong-argument-subparsers-no-suggestions
  (t/is (= :argparse/test-wrong-argument-subparsers-no-suggestions (argparse/test-dispatch :argparse/test-wrong-argument-subparsers-no-suggestions))))

(t/deftest ^:kaocha/skip test-wrong-argument-with-suggestion-explicit
  (t/is (= :argparse/test-wrong-argument-with-suggestion-explicit (argparse/test-dispatch :argparse/test-wrong-argument-with-suggestion-explicit))))

(t/deftest ^:kaocha/skip test-suggestions-choices-empty
  (t/is (= :argparse/test-suggestions-choices-empty (argparse/test-dispatch :argparse/test-suggestions-choices-empty))))

(t/deftest ^:kaocha/skip test-suggestions-choices-int
  (t/is (= :argparse/test-suggestions-choices-int (argparse/test-dispatch :argparse/test-suggestions-choices-int))))

(t/deftest ^:kaocha/skip test-suggestions-choices-mixed-types
  (t/is (= :argparse/test-suggestions-choices-mixed-types (argparse/test-dispatch :argparse/test-suggestions-choices-mixed-types))))

(t/deftest ^:kaocha/skip invalid-action-test-invalid-type
  (t/is (= :argparse/invalid-action-test-invalid-type (argparse/test-dispatch :argparse/invalid-action-test-invalid-type))))

(t/deftest ^:kaocha/skip test-modified-invalid-action
  (t/is (= :argparse/test-modified-invalid-action (argparse/test-dispatch :argparse/test-modified-invalid-action))))

(t/deftest ^:kaocha/skip test-parse-args-failures
  (t/is (= :argparse/test-parse-args-failures (argparse/test-dispatch :argparse/test-parse-args-failures))))

(t/deftest ^:kaocha/skip test-parse-args-failures-details
  (t/is (= :argparse/test-parse-args-failures-details (argparse/test-dispatch :argparse/test-parse-args-failures-details))))

(t/deftest ^:kaocha/skip test-parse-args-failures-details-custom-usage
  (t/is (= :argparse/test-parse-args-failures-details-custom-usage (argparse/test-dispatch :argparse/test-parse-args-failures-details-custom-usage))))

(t/deftest ^:kaocha/skip test-parse-args
  (t/is (= :argparse/test-parse-args (argparse/test-dispatch :argparse/test-parse-args))))

(t/deftest ^:kaocha/skip test-parse-known-args
  (t/is (= :argparse/test-parse-known-args (argparse/test-dispatch :argparse/test-parse-known-args))))

(t/deftest ^:kaocha/skip test-parse-known-args-to-class-namespace
  (t/is (= :argparse/test-parse-known-args-to-class-namespace (argparse/test-dispatch :argparse/test-parse-known-args-to-class-namespace))))

(t/deftest ^:kaocha/skip test-abbreviation
  (t/is (= :argparse/test-abbreviation (argparse/test-dispatch :argparse/test-abbreviation))))

(t/deftest ^:kaocha/skip test-parse-known-args-with-single-dash-option
  (t/is (= :argparse/test-parse-known-args-with-single-dash-option (argparse/test-dispatch :argparse/test-parse-known-args-with-single-dash-option))))

(t/deftest ^:kaocha/skip add-subparsers-test-dest
  (t/is (= :argparse/add-subparsers-test-dest (argparse/test-dispatch :argparse/add-subparsers-test-dest))))

(t/deftest ^:kaocha/skip test-required-subparsers-via-attribute
  (t/is (= :argparse/test-required-subparsers-via-attribute (argparse/test-dispatch :argparse/test-required-subparsers-via-attribute))))

(t/deftest ^:kaocha/skip test-required-subparsers-via-kwarg
  (t/is (= :argparse/test-required-subparsers-via-kwarg (argparse/test-dispatch :argparse/test-required-subparsers-via-kwarg))))

(t/deftest ^:kaocha/skip test-required-subparsers-default
  (t/is (= :argparse/test-required-subparsers-default (argparse/test-dispatch :argparse/test-required-subparsers-default))))

(t/deftest ^:kaocha/skip test-required-subparsers-no-destination-error
  (t/is (= :argparse/test-required-subparsers-no-destination-error (argparse/test-dispatch :argparse/test-required-subparsers-no-destination-error))))

(t/deftest ^:kaocha/skip test-optional-subparsers
  (t/is (= :argparse/test-optional-subparsers (argparse/test-dispatch :argparse/test-optional-subparsers))))

(t/deftest ^:kaocha/skip test-subparser-help-with-parent-required-optional
  (t/is (= :argparse/test-subparser-help-with-parent-required-optional (argparse/test-dispatch :argparse/test-subparser-help-with-parent-required-optional))))

(t/deftest ^:kaocha/skip add-subparsers-test-help
  (t/is (= :argparse/add-subparsers-test-help (argparse/test-dispatch :argparse/add-subparsers-test-help))))

(t/deftest ^:kaocha/skip test-help-extra-prefix-chars
  (t/is (= :argparse/test-help-extra-prefix-chars (argparse/test-dispatch :argparse/test-help-extra-prefix-chars))))

(t/deftest ^:kaocha/skip test-help-non-breaking-spaces
  (t/is (= :argparse/test-help-non-breaking-spaces (argparse/test-dispatch :argparse/test-help-non-breaking-spaces))))

(t/deftest ^:kaocha/skip test-help-blank
  (t/is (= :argparse/test-help-blank (argparse/test-dispatch :argparse/test-help-blank))))

(t/deftest ^:kaocha/skip test-help-alternate-prefix-chars
  (t/is (= :argparse/test-help-alternate-prefix-chars (argparse/test-dispatch :argparse/test-help-alternate-prefix-chars))))

(t/deftest ^:kaocha/skip test-parser-command-help
  (t/is (= :argparse/test-parser-command-help (argparse/test-dispatch :argparse/test-parser-command-help))))

(t/deftest ^:kaocha/skip test-invalid-subparsers-help
  (t/is (= :argparse/test-invalid-subparsers-help (argparse/test-dispatch :argparse/test-invalid-subparsers-help))))

(t/deftest ^:kaocha/skip test-invalid-subparser-help
  (t/is (= :argparse/test-invalid-subparser-help (argparse/test-dispatch :argparse/test-invalid-subparser-help))))

(t/deftest ^:kaocha/skip test-subparser-title-help
  (t/is (= :argparse/test-subparser-title-help (argparse/test-dispatch :argparse/test-subparser-title-help))))

(t/deftest ^:kaocha/skip test-subparser1-help
  (t/is (= :argparse/test-subparser1-help (argparse/test-dispatch :argparse/test-subparser1-help))))

(t/deftest ^:kaocha/skip test-subparser2-help
  (t/is (= :argparse/test-subparser2-help (argparse/test-dispatch :argparse/test-subparser2-help))))

(t/deftest ^:kaocha/skip test-alias-invocation
  (t/is (= :argparse/test-alias-invocation (argparse/test-dispatch :argparse/test-alias-invocation))))

(t/deftest ^:kaocha/skip test-error-alias-invocation
  (t/is (= :argparse/test-error-alias-invocation (argparse/test-dispatch :argparse/test-error-alias-invocation))))

(t/deftest ^:kaocha/skip test-alias-help
  (t/is (= :argparse/test-alias-help (argparse/test-dispatch :argparse/test-alias-help))))

(t/deftest ^:kaocha/skip test-nongroup-first
  (t/is (= :argparse/test-nongroup-first (argparse/test-dispatch :argparse/test-nongroup-first))))

(t/deftest ^:kaocha/skip test-group-first
  (t/is (= :argparse/test-group-first (argparse/test-dispatch :argparse/test-group-first))))

(t/deftest ^:kaocha/skip test-interleaved-groups
  (t/is (= :argparse/test-interleaved-groups (argparse/test-dispatch :argparse/test-interleaved-groups))))

(t/deftest ^:kaocha/skip test-group-prefix-chars
  (t/is (= :argparse/test-group-prefix-chars (argparse/test-dispatch :argparse/test-group-prefix-chars))))

(t/deftest ^:kaocha/skip test-group-prefix-chars-default
  (t/is (= :argparse/test-group-prefix-chars-default (argparse/test-dispatch :argparse/test-group-prefix-chars-default))))

(t/deftest ^:kaocha/skip test-nested-argument-group
  (t/is (= :argparse/test-nested-argument-group (argparse/test-dispatch :argparse/test-nested-argument-group))))

(t/deftest ^:kaocha/skip test-single-parent
  (t/is (= :argparse/test-single-parent (argparse/test-dispatch :argparse/test-single-parent))))

(t/deftest ^:kaocha/skip test-single-parent-mutex
  (t/is (= :argparse/test-single-parent-mutex (argparse/test-dispatch :argparse/test-single-parent-mutex))))

(t/deftest ^:kaocha/skip test-single-grandparent-mutex
  (t/is (= :argparse/test-single-grandparent-mutex (argparse/test-dispatch :argparse/test-single-grandparent-mutex))))

(t/deftest ^:kaocha/skip test-multiple-parents
  (t/is (= :argparse/test-multiple-parents (argparse/test-dispatch :argparse/test-multiple-parents))))

(t/deftest ^:kaocha/skip test-multiple-parents-mutex
  (t/is (= :argparse/test-multiple-parents-mutex (argparse/test-dispatch :argparse/test-multiple-parents-mutex))))

(t/deftest ^:kaocha/skip test-conflicting-parents
  (t/is (= :argparse/test-conflicting-parents (argparse/test-dispatch :argparse/test-conflicting-parents))))

(t/deftest ^:kaocha/skip test-conflicting-parents-mutex
  (t/is (= :argparse/test-conflicting-parents-mutex (argparse/test-dispatch :argparse/test-conflicting-parents-mutex))))

(t/deftest ^:kaocha/skip test-same-argument-name-parents
  (t/is (= :argparse/test-same-argument-name-parents (argparse/test-dispatch :argparse/test-same-argument-name-parents))))

(t/deftest ^:kaocha/skip test-subparser-parents
  (t/is (= :argparse/test-subparser-parents (argparse/test-dispatch :argparse/test-subparser-parents))))

(t/deftest ^:kaocha/skip test-subparser-parents-mutex
  (t/is (= :argparse/test-subparser-parents-mutex (argparse/test-dispatch :argparse/test-subparser-parents-mutex))))

(t/deftest ^:kaocha/skip test-parent-help
  (t/is (= :argparse/test-parent-help (argparse/test-dispatch :argparse/test-parent-help))))

(t/deftest ^:kaocha/skip test-groups-parents
  (t/is (= :argparse/test-groups-parents (argparse/test-dispatch :argparse/test-groups-parents))))

(t/deftest ^:kaocha/skip test-wrong-type-parents
  (t/is (= :argparse/test-wrong-type-parents (argparse/test-dispatch :argparse/test-wrong-type-parents))))

(t/deftest ^:kaocha/skip test-mutex-groups-parents
  (t/is (= :argparse/test-mutex-groups-parents (argparse/test-dispatch :argparse/test-mutex-groups-parents))))

(t/deftest ^:kaocha/skip test-invalid-add-argument-group
  (t/is (= :argparse/test-invalid-add-argument-group (argparse/test-dispatch :argparse/test-invalid-add-argument-group))))

(t/deftest ^:kaocha/skip test-invalid-add-argument
  (t/is (= :argparse/test-invalid-add-argument (argparse/test-dispatch :argparse/test-invalid-add-argument))))

(t/deftest ^:kaocha/skip mutually-exclusive-group-errors-test-help
  (t/is (= :argparse/mutually-exclusive-group-errors-test-help (argparse/test-dispatch :argparse/mutually-exclusive-group-errors-test-help))))

(t/deftest ^:kaocha/skip test-optional-order
  (t/is (= :argparse/test-optional-order (argparse/test-dispatch :argparse/test-optional-order))))

(t/deftest ^:kaocha/skip test-help-subparser-all-mutually-exclusive-group-members-suppressed
  (t/is (= :argparse/test-help-subparser-all-mutually-exclusive-group-members-suppressed (argparse/test-dispatch :argparse/test-help-subparser-all-mutually-exclusive-group-members-suppressed))))

(t/deftest ^:kaocha/skip test-usage-empty-group
  (t/is (= :argparse/test-usage-empty-group (argparse/test-dispatch :argparse/test-usage-empty-group))))

(t/deftest ^:kaocha/skip test-nested-mutex-groups
  (t/is (= :argparse/test-nested-mutex-groups (argparse/test-dispatch :argparse/test-nested-mutex-groups))))

(t/deftest ^:kaocha/skip test-failures-when-not-required
  (t/is (= :argparse/test-failures-when-not-required (argparse/test-dispatch :argparse/test-failures-when-not-required))))

(t/deftest ^:kaocha/skip test-failures-when-required
  (t/is (= :argparse/test-failures-when-required (argparse/test-dispatch :argparse/test-failures-when-required))))

(t/deftest ^:kaocha/skip test-successes-when-not-required
  (t/is (= :argparse/test-successes-when-not-required (argparse/test-dispatch :argparse/test-successes-when-not-required))))

(t/deftest ^:kaocha/skip test-successes-when-required
  (t/is (= :argparse/test-successes-when-required (argparse/test-dispatch :argparse/test-successes-when-required))))

(t/deftest ^:kaocha/skip test-usage-when-not-required
  (t/is (= :argparse/test-usage-when-not-required (argparse/test-dispatch :argparse/test-usage-when-not-required))))

(t/deftest ^:kaocha/skip test-usage-when-required
  (t/is (= :argparse/test-usage-when-required (argparse/test-dispatch :argparse/test-usage-when-required))))

(t/deftest ^:kaocha/skip test-help-when-not-required
  (t/is (= :argparse/test-help-when-not-required (argparse/test-dispatch :argparse/test-help-when-not-required))))

(t/deftest ^:kaocha/skip test-help-when-required
  (t/is (= :argparse/test-help-when-required (argparse/test-dispatch :argparse/test-help-when-required))))

(t/deftest ^:kaocha/skip test-set-defaults-no-args
  (t/is (= :argparse/test-set-defaults-no-args (argparse/test-dispatch :argparse/test-set-defaults-no-args))))

(t/deftest ^:kaocha/skip test-set-defaults-with-args
  (t/is (= :argparse/test-set-defaults-with-args (argparse/test-dispatch :argparse/test-set-defaults-with-args))))

(t/deftest ^:kaocha/skip test-set-defaults-subparsers
  (t/is (= :argparse/test-set-defaults-subparsers (argparse/test-dispatch :argparse/test-set-defaults-subparsers))))

(t/deftest ^:kaocha/skip test-set-defaults-parents
  (t/is (= :argparse/test-set-defaults-parents (argparse/test-dispatch :argparse/test-set-defaults-parents))))

(t/deftest ^:kaocha/skip test-set-defaults-on-parent-and-subparser
  (t/is (= :argparse/test-set-defaults-on-parent-and-subparser (argparse/test-dispatch :argparse/test-set-defaults-on-parent-and-subparser))))

(t/deftest ^:kaocha/skip test-set-defaults-same-as-add-argument
  (t/is (= :argparse/test-set-defaults-same-as-add-argument (argparse/test-dispatch :argparse/test-set-defaults-same-as-add-argument))))

(t/deftest ^:kaocha/skip test-set-defaults-same-as-add-argument-group
  (t/is (= :argparse/test-set-defaults-same-as-add-argument-group (argparse/test-dispatch :argparse/test-set-defaults-same-as-add-argument-group))))

(t/deftest ^:kaocha/skip test-get-default
  (t/is (= :argparse/test-get-default (argparse/test-dispatch :argparse/test-get-default))))

(t/deftest ^:kaocha/skip test-empty
  (t/is (= :argparse/test-empty (argparse/test-dispatch :argparse/test-empty))))

(t/deftest ^:kaocha/skip test-non-empty
  (t/is (= :argparse/test-non-empty (argparse/test-dispatch :argparse/test-non-empty))))

(t/deftest ^:kaocha/skip test-all-suppressed-mutex-followed-by-long-arg
  (t/is (= :argparse/test-all-suppressed-mutex-followed-by-long-arg (argparse/test-dispatch :argparse/test-all-suppressed-mutex-followed-by-long-arg))))

(t/deftest ^:kaocha/skip test-newline-in-metavar
  (t/is (= :argparse/test-newline-in-metavar (argparse/test-dispatch :argparse/test-newline-in-metavar))))

(t/deftest ^:kaocha/skip test-empty-metavar-required-arg
  (t/is (= :argparse/test-empty-metavar-required-arg (argparse/test-dispatch :argparse/test-empty-metavar-required-arg))))

(t/deftest ^:kaocha/skip test-all-suppressed-mutex-with-optional-nargs
  (t/is (= :argparse/test-all-suppressed-mutex-with-optional-nargs (argparse/test-dispatch :argparse/test-all-suppressed-mutex-with-optional-nargs))))

(t/deftest ^:kaocha/skip test-long-mutex-groups-wrap
  (t/is (= :argparse/test-long-mutex-groups-wrap (argparse/test-dispatch :argparse/test-long-mutex-groups-wrap))))

(t/deftest ^:kaocha/skip test-mutex-groups-with-mixed-optionals-positionals-wrap
  (t/is (= :argparse/test-mutex-groups-with-mixed-optionals-positionals-wrap (argparse/test-dispatch :argparse/test-mutex-groups-with-mixed-optionals-positionals-wrap))))

(t/deftest ^:kaocha/skip help-custom-help-formatter-test-custom-formatter-function
  (t/is (= :argparse/help-custom-help-formatter-test-custom-formatter-function (argparse/test-dispatch :argparse/help-custom-help-formatter-test-custom-formatter-function))))

(t/deftest ^:kaocha/skip help-custom-help-formatter-test-custom-formatter-class
  (t/is (= :argparse/help-custom-help-formatter-test-custom-formatter-class (argparse/test-dispatch :argparse/help-custom-help-formatter-test-custom-formatter-class))))

(t/deftest ^:kaocha/skip test-usage-long-subparser-command
  (t/is (= :argparse/test-usage-long-subparser-command (argparse/test-dispatch :argparse/test-usage-long-subparser-command))))

(t/deftest ^:kaocha/skip test-direct-formatter-instantiation
  (t/is (= :argparse/test-direct-formatter-instantiation (argparse/test-dispatch :argparse/test-direct-formatter-instantiation))))

(t/deftest ^:kaocha/skip test-invalid-keyword-arguments
  (t/is (= :argparse/test-invalid-keyword-arguments (argparse/test-dispatch :argparse/test-invalid-keyword-arguments))))

(t/deftest ^:kaocha/skip test-missing-destination
  (t/is (= :argparse/test-missing-destination (argparse/test-dispatch :argparse/test-missing-destination))))

(t/deftest ^:kaocha/skip test-invalid-option-strings
  (t/is (= :argparse/test-invalid-option-strings (argparse/test-dispatch :argparse/test-invalid-option-strings))))

(t/deftest ^:kaocha/skip test-invalid-prefix
  (t/is (= :argparse/test-invalid-prefix (argparse/test-dispatch :argparse/test-invalid-prefix))))

(t/deftest ^:kaocha/skip invalid-argument-constructors-test-invalid-type
  (t/is (= :argparse/invalid-argument-constructors-test-invalid-type (argparse/test-dispatch :argparse/invalid-argument-constructors-test-invalid-type))))

(t/deftest ^:kaocha/skip test-invalid-action
  (t/is (= :argparse/test-invalid-action (argparse/test-dispatch :argparse/test-invalid-action))))

(t/deftest ^:kaocha/skip test-invalid-help
  (t/is (= :argparse/test-invalid-help (argparse/test-dispatch :argparse/test-invalid-help))))

(t/deftest ^:kaocha/skip test-multiple-dest
  (t/is (= :argparse/test-multiple-dest (argparse/test-dispatch :argparse/test-multiple-dest))))

(t/deftest ^:kaocha/skip test-no-argument-actions
  (t/is (= :argparse/test-no-argument-actions (argparse/test-dispatch :argparse/test-no-argument-actions))))

(t/deftest ^:kaocha/skip test-no-argument-no-const-actions
  (t/is (= :argparse/test-no-argument-no-const-actions (argparse/test-dispatch :argparse/test-no-argument-no-const-actions))))

(t/deftest ^:kaocha/skip test-more-than-one-argument-actions
  (t/is (= :argparse/test-more-than-one-argument-actions (argparse/test-dispatch :argparse/test-more-than-one-argument-actions))))

(t/deftest ^:kaocha/skip test-required-const-actions
  (t/is (= :argparse/test-required-const-actions (argparse/test-dispatch :argparse/test-required-const-actions))))

(t/deftest ^:kaocha/skip test-parsers-action-missing-params
  (t/is (= :argparse/test-parsers-action-missing-params (argparse/test-dispatch :argparse/test-parsers-action-missing-params))))

(t/deftest ^:kaocha/skip test-version-missing-params
  (t/is (= :argparse/test-version-missing-params (argparse/test-dispatch :argparse/test-version-missing-params))))

(t/deftest ^:kaocha/skip test-required-positional
  (t/is (= :argparse/test-required-positional (argparse/test-dispatch :argparse/test-required-positional))))

(t/deftest ^:kaocha/skip test-user-defined-action
  (t/is (= :argparse/test-user-defined-action (argparse/test-dispatch :argparse/test-user-defined-action))))

(t/deftest ^:kaocha/skip actions-returned-test-dest
  (t/is (= :argparse/actions-returned-test-dest (argparse/test-dispatch :argparse/actions-returned-test-dest))))

(t/deftest ^:kaocha/skip test-misc
  (t/is (= :argparse/test-misc (argparse/test-dispatch :argparse/test-misc))))

(t/deftest ^:kaocha/skip test-bad-type
  (t/is (= :argparse/test-bad-type (argparse/test-dispatch :argparse/test-bad-type))))

(t/deftest ^:kaocha/skip test-conflict-error
  (t/is (= :argparse/test-conflict-error (argparse/test-dispatch :argparse/test-conflict-error))))

(t/deftest ^:kaocha/skip test-resolve-error
  (t/is (= :argparse/test-resolve-error (argparse/test-dispatch :argparse/test-resolve-error))))

(t/deftest ^:kaocha/skip test-subparser-conflict
  (t/is (= :argparse/test-subparser-conflict (argparse/test-dispatch :argparse/test-subparser-conflict))))

(t/deftest ^:kaocha/skip test-version
  (t/is (= :argparse/test-version (argparse/test-dispatch :argparse/test-version))))

(t/deftest ^:kaocha/skip test-version-format
  (t/is (= :argparse/test-version-format (argparse/test-dispatch :argparse/test-version-format))))

(t/deftest ^:kaocha/skip test-version-no-help
  (t/is (= :argparse/test-version-no-help (argparse/test-dispatch :argparse/test-version-no-help))))

(t/deftest ^:kaocha/skip test-version-action
  (t/is (= :argparse/test-version-action (argparse/test-dispatch :argparse/test-version-action))))

(t/deftest ^:kaocha/skip test-no-help
  (t/is (= :argparse/test-no-help (argparse/test-dispatch :argparse/test-no-help))))

(t/deftest ^:kaocha/skip test-alternate-help-version
  (t/is (= :argparse/test-alternate-help-version (argparse/test-dispatch :argparse/test-alternate-help-version))))

(t/deftest ^:kaocha/skip test-help-version-extra-arguments
  (t/is (= :argparse/test-help-version-extra-arguments (argparse/test-dispatch :argparse/test-help-version-extra-arguments))))

(t/deftest ^:kaocha/skip test-optional
  (t/is (= :argparse/test-optional (argparse/test-dispatch :argparse/test-optional))))

(t/deftest ^:kaocha/skip test-argument
  (t/is (= :argparse/test-argument (argparse/test-dispatch :argparse/test-argument))))

(t/deftest ^:kaocha/skip test-namespace
  (t/is (= :argparse/test-namespace (argparse/test-dispatch :argparse/test-namespace))))

(t/deftest ^:kaocha/skip test-namespace-starkwargs-notidentifier
  (t/is (= :argparse/test-namespace-starkwargs-notidentifier (argparse/test-dispatch :argparse/test-namespace-starkwargs-notidentifier))))

(t/deftest ^:kaocha/skip test-namespace-kwargs-and-starkwargs-notidentifier
  (t/is (= :argparse/test-namespace-kwargs-and-starkwargs-notidentifier (argparse/test-dispatch :argparse/test-namespace-kwargs-and-starkwargs-notidentifier))))

(t/deftest ^:kaocha/skip test-namespace-starkwargs-identifier
  (t/is (= :argparse/test-namespace-starkwargs-identifier (argparse/test-dispatch :argparse/test-namespace-starkwargs-identifier))))

(t/deftest ^:kaocha/skip test-parser
  (t/is (= :argparse/test-parser (argparse/test-dispatch :argparse/test-parser))))

(t/deftest ^:kaocha/skip test-constructor
  (t/is (= :argparse/test-constructor (argparse/test-dispatch :argparse/test-constructor))))

(t/deftest ^:kaocha/skip test-equality
  (t/is (= :argparse/test-equality (argparse/test-dispatch :argparse/test-equality))))

(t/deftest ^:kaocha/skip test-equality-returns-notimplemented
  (t/is (= :argparse/test-equality-returns-notimplemented (argparse/test-dispatch :argparse/test-equality-returns-notimplemented))))

(t/deftest ^:kaocha/skip test-argparse-module-encoding
  (t/is (= :argparse/test-argparse-module-encoding (argparse/test-dispatch :argparse/test-argparse-module-encoding))))

(t/deftest ^:kaocha/skip test-test-argparse-module-encoding
  (t/is (= :argparse/test-test-argparse-module-encoding (argparse/test-dispatch :argparse/test-test-argparse-module-encoding))))

(t/deftest ^:kaocha/skip test-argument-error
  (t/is (= :argparse/test-argument-error (argparse/test-dispatch :argparse/test-argument-error))))

(t/deftest ^:kaocha/skip test-argument-type-error
  (t/is (= :argparse/test-argument-type-error (argparse/test-dispatch :argparse/test-argument-type-error))))

(t/deftest ^:kaocha/skip message-content-error-test-missing-argument-name-in-message
  (t/is (= :argparse/message-content-error-test-missing-argument-name-in-message (argparse/test-dispatch :argparse/message-content-error-test-missing-argument-name-in-message))))

(t/deftest ^:kaocha/skip test-optional-optional-not-in-message
  (t/is (= :argparse/test-optional-optional-not-in-message (argparse/test-dispatch :argparse/test-optional-optional-not-in-message))))

(t/deftest ^:kaocha/skip test-optional-positional-not-in-message
  (t/is (= :argparse/test-optional-positional-not-in-message (argparse/test-dispatch :argparse/test-optional-positional-not-in-message))))

(t/deftest ^:kaocha/skip test-type-function-call-only-once
  (t/is (= :argparse/test-type-function-call-only-once (argparse/test-dispatch :argparse/test-type-function-call-only-once))))

(t/deftest ^:kaocha/skip test-deprecated-option
  (t/is (= :argparse/test-deprecated-option (argparse/test-dispatch :argparse/test-deprecated-option))))

(t/deftest ^:kaocha/skip test-deprecated-boolean-option
  (t/is (= :argparse/test-deprecated-boolean-option (argparse/test-dispatch :argparse/test-deprecated-boolean-option))))

(t/deftest ^:kaocha/skip test-deprecated-arguments
  (t/is (= :argparse/test-deprecated-arguments (argparse/test-dispatch :argparse/test-deprecated-arguments))))

(t/deftest ^:kaocha/skip test-deprecated-varargument
  (t/is (= :argparse/test-deprecated-varargument (argparse/test-dispatch :argparse/test-deprecated-varargument))))

(t/deftest ^:kaocha/skip test-deprecated-subparser
  (t/is (= :argparse/test-deprecated-subparser (argparse/test-dispatch :argparse/test-deprecated-subparser))))

(t/deftest ^:kaocha/skip test-type-function-call-with-non-string-default
  (t/is (= :argparse/test-type-function-call-with-non-string-default (argparse/test-dispatch :argparse/test-type-function-call-with-non-string-default))))

(t/deftest ^:kaocha/skip test-type-function-call-with-string-default
  (t/is (= :argparse/test-type-function-call-with-string-default (argparse/test-dispatch :argparse/test-type-function-call-with-string-default))))

(t/deftest ^:kaocha/skip test-no-double-type-conversion-of-default
  (t/is (= :argparse/test-no-double-type-conversion-of-default (argparse/test-dispatch :argparse/test-no-double-type-conversion-of-default))))

(t/deftest ^:kaocha/skip test-issue-15906
  (t/is (= :argparse/test-issue-15906 (argparse/test-dispatch :argparse/test-issue-15906))))

(t/deftest ^:kaocha/skip test-arguments-tuple
  (t/is (= :argparse/test-arguments-tuple (argparse/test-dispatch :argparse/test-arguments-tuple))))

(t/deftest ^:kaocha/skip test-arguments-list
  (t/is (= :argparse/test-arguments-list (argparse/test-dispatch :argparse/test-arguments-list))))

(t/deftest ^:kaocha/skip test-arguments-tuple-positional
  (t/is (= :argparse/test-arguments-tuple-positional (argparse/test-dispatch :argparse/test-arguments-tuple-positional))))

(t/deftest ^:kaocha/skip test-arguments-list-positional
  (t/is (= :argparse/test-arguments-list-positional (argparse/test-dispatch :argparse/test-arguments-list-positional))))

(t/deftest ^:kaocha/skip test-optionals
  (t/is (= :argparse/test-optionals (argparse/test-dispatch :argparse/test-optionals))))

(t/deftest ^:kaocha/skip test-mixed
  (t/is (= :argparse/test-mixed (argparse/test-dispatch :argparse/test-mixed))))

(t/deftest ^:kaocha/skip test-zero-or-more-optional
  (t/is (= :argparse/test-zero-or-more-optional (argparse/test-dispatch :argparse/test-zero-or-more-optional))))

(t/deftest ^:kaocha/skip test-single-argument-option
  (t/is (= :argparse/test-single-argument-option (argparse/test-dispatch :argparse/test-single-argument-option))))

(t/deftest ^:kaocha/skip test-multiple-argument-option
  (t/is (= :argparse/test-multiple-argument-option (argparse/test-dispatch :argparse/test-multiple-argument-option))))

(t/deftest ^:kaocha/skip test-multiple-double-dashes
  (t/is (= :argparse/test-multiple-double-dashes (argparse/test-dispatch :argparse/test-multiple-double-dashes))))

(t/deftest ^:kaocha/skip double-dash-test-remainder
  (t/is (= :argparse/double-dash-test-remainder (argparse/test-dispatch :argparse/double-dash-test-remainder))))

(t/deftest ^:kaocha/skip test-optional-remainder
  (t/is (= :argparse/test-optional-remainder (argparse/test-dispatch :argparse/test-optional-remainder))))

(t/deftest ^:kaocha/skip test-subparser
  (t/is (= :argparse/test-subparser (argparse/test-dispatch :argparse/test-subparser))))

(t/deftest ^:kaocha/skip test-subparser-after-multiple-argument-option
  (t/is (= :argparse/test-subparser-after-multiple-argument-option (argparse/test-dispatch :argparse/test-subparser-after-multiple-argument-option))))

(t/deftest ^:kaocha/skip test-basic
  (t/is (= :argparse/test-basic (argparse/test-dispatch :argparse/test-basic))))

(t/deftest ^:kaocha/skip intermixed-args-test-remainder
  (t/is (= :argparse/intermixed-args-test-remainder (argparse/test-dispatch :argparse/intermixed-args-test-remainder))))

(t/deftest ^:kaocha/skip test-required-exclusive
  (t/is (= :argparse/test-required-exclusive (argparse/test-dispatch :argparse/test-required-exclusive))))

(t/deftest ^:kaocha/skip test-required-exclusive-with-positional
  (t/is (= :argparse/test-required-exclusive-with-positional (argparse/test-dispatch :argparse/test-required-exclusive-with-positional))))

(t/deftest ^:kaocha/skip test-invalid-args
  (t/is (= :argparse/test-invalid-args (argparse/test-dispatch :argparse/test-invalid-args))))

(t/deftest ^:kaocha/skip intermixed-message-content-error-test-missing-argument-name-in-message
  (t/is (= :argparse/intermixed-message-content-error-test-missing-argument-name-in-message (argparse/test-dispatch :argparse/intermixed-message-content-error-test-missing-argument-name-in-message))))

(t/deftest ^:kaocha/skip test-nargs-None-metavar-string
  (t/is (= :argparse/test-nargs-None-metavar-string (argparse/test-dispatch :argparse/test-nargs-None-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-None-metavar-length0
  (t/is (= :argparse/test-nargs-None-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-None-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-None-metavar-length1
  (t/is (= :argparse/test-nargs-None-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-None-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-None-metavar-length2
  (t/is (= :argparse/test-nargs-None-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-None-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-None-metavar-length3
  (t/is (= :argparse/test-nargs-None-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-None-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-optional-metavar-string
  (t/is (= :argparse/test-nargs-optional-metavar-string (argparse/test-dispatch :argparse/test-nargs-optional-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-optional-metavar-length0
  (t/is (= :argparse/test-nargs-optional-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-optional-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-optional-metavar-length1
  (t/is (= :argparse/test-nargs-optional-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-optional-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-optional-metavar-length2
  (t/is (= :argparse/test-nargs-optional-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-optional-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-optional-metavar-length3
  (t/is (= :argparse/test-nargs-optional-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-optional-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-zeroormore-metavar-string
  (t/is (= :argparse/test-nargs-zeroormore-metavar-string (argparse/test-dispatch :argparse/test-nargs-zeroormore-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-zeroormore-metavar-length0
  (t/is (= :argparse/test-nargs-zeroormore-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-zeroormore-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-zeroormore-metavar-length1
  (t/is (= :argparse/test-nargs-zeroormore-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-zeroormore-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-zeroormore-metavar-length2
  (t/is (= :argparse/test-nargs-zeroormore-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-zeroormore-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-zeroormore-metavar-length3
  (t/is (= :argparse/test-nargs-zeroormore-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-zeroormore-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-oneormore-metavar-string
  (t/is (= :argparse/test-nargs-oneormore-metavar-string (argparse/test-dispatch :argparse/test-nargs-oneormore-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-oneormore-metavar-length0
  (t/is (= :argparse/test-nargs-oneormore-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-oneormore-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-oneormore-metavar-length1
  (t/is (= :argparse/test-nargs-oneormore-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-oneormore-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-oneormore-metavar-length2
  (t/is (= :argparse/test-nargs-oneormore-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-oneormore-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-oneormore-metavar-length3
  (t/is (= :argparse/test-nargs-oneormore-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-oneormore-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-remainder-metavar-string
  (t/is (= :argparse/test-nargs-remainder-metavar-string (argparse/test-dispatch :argparse/test-nargs-remainder-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-remainder-metavar-length0
  (t/is (= :argparse/test-nargs-remainder-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-remainder-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-remainder-metavar-length1
  (t/is (= :argparse/test-nargs-remainder-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-remainder-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-remainder-metavar-length2
  (t/is (= :argparse/test-nargs-remainder-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-remainder-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-remainder-metavar-length3
  (t/is (= :argparse/test-nargs-remainder-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-remainder-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-parser-metavar-string
  (t/is (= :argparse/test-nargs-parser-metavar-string (argparse/test-dispatch :argparse/test-nargs-parser-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-parser-metavar-length0
  (t/is (= :argparse/test-nargs-parser-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-parser-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-parser-metavar-length1
  (t/is (= :argparse/test-nargs-parser-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-parser-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-parser-metavar-length2
  (t/is (= :argparse/test-nargs-parser-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-parser-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-parser-metavar-length3
  (t/is (= :argparse/test-nargs-parser-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-parser-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-1-metavar-string
  (t/is (= :argparse/test-nargs-1-metavar-string (argparse/test-dispatch :argparse/test-nargs-1-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-1-metavar-length0
  (t/is (= :argparse/test-nargs-1-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-1-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-1-metavar-length1
  (t/is (= :argparse/test-nargs-1-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-1-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-1-metavar-length2
  (t/is (= :argparse/test-nargs-1-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-1-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-1-metavar-length3
  (t/is (= :argparse/test-nargs-1-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-1-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-2-metavar-string
  (t/is (= :argparse/test-nargs-2-metavar-string (argparse/test-dispatch :argparse/test-nargs-2-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-2-metavar-length0
  (t/is (= :argparse/test-nargs-2-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-2-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-2-metavar-length1
  (t/is (= :argparse/test-nargs-2-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-2-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-2-metavar-length2
  (t/is (= :argparse/test-nargs-2-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-2-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-2-metavar-length3
  (t/is (= :argparse/test-nargs-2-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-2-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-3-metavar-string
  (t/is (= :argparse/test-nargs-3-metavar-string (argparse/test-dispatch :argparse/test-nargs-3-metavar-string))))

(t/deftest ^:kaocha/skip test-nargs-3-metavar-length0
  (t/is (= :argparse/test-nargs-3-metavar-length0 (argparse/test-dispatch :argparse/test-nargs-3-metavar-length0))))

(t/deftest ^:kaocha/skip test-nargs-3-metavar-length1
  (t/is (= :argparse/test-nargs-3-metavar-length1 (argparse/test-dispatch :argparse/test-nargs-3-metavar-length1))))

(t/deftest ^:kaocha/skip test-nargs-3-metavar-length2
  (t/is (= :argparse/test-nargs-3-metavar-length2 (argparse/test-dispatch :argparse/test-nargs-3-metavar-length2))))

(t/deftest ^:kaocha/skip test-nargs-3-metavar-length3
  (t/is (= :argparse/test-nargs-3-metavar-length3 (argparse/test-dispatch :argparse/test-nargs-3-metavar-length3))))

(t/deftest ^:kaocha/skip test-nargs-alphabetic
  (t/is (= :argparse/test-nargs-alphabetic (argparse/test-dispatch :argparse/test-nargs-alphabetic))))

(t/deftest ^:kaocha/skip test-nargs-zero
  (t/is (= :argparse/test-nargs-zero (argparse/test-dispatch :argparse/test-nargs-zero))))

(t/deftest ^:kaocha/skip test-all-exports-everything-but-modules
  (t/is (= :argparse/test-all-exports-everything-but-modules (argparse/test-dispatch :argparse/test-all-exports-everything-but-modules))))

(t/deftest ^:kaocha/skip test-help-with-metavar
  (t/is (= :argparse/test-help-with-metavar (argparse/test-dispatch :argparse/test-help-with-metavar))))

(t/deftest ^:kaocha/skip test-exit-on-error-with-good-args
  (t/is (= :argparse/test-exit-on-error-with-good-args (argparse/test-dispatch :argparse/test-exit-on-error-with-good-args))))

(t/deftest ^:kaocha/skip test-exit-on-error-with-bad-args
  (t/is (= :argparse/test-exit-on-error-with-bad-args (argparse/test-dispatch :argparse/test-exit-on-error-with-bad-args))))

(t/deftest ^:kaocha/skip test-unrecognized-args
  (t/is (= :argparse/test-unrecognized-args (argparse/test-dispatch :argparse/test-unrecognized-args))))

(t/deftest ^:kaocha/skip test-unrecognized-intermixed-args
  (t/is (= :argparse/test-unrecognized-intermixed-args (argparse/test-dispatch :argparse/test-unrecognized-intermixed-args))))

(t/deftest ^:kaocha/skip test-required-args
  (t/is (= :argparse/test-required-args (argparse/test-dispatch :argparse/test-required-args))))

(t/deftest ^:kaocha/skip test-required-args-with-metavar
  (t/is (= :argparse/test-required-args-with-metavar (argparse/test-dispatch :argparse/test-required-args-with-metavar))))

(t/deftest ^:kaocha/skip test-required-args-n
  (t/is (= :argparse/test-required-args-n (argparse/test-dispatch :argparse/test-required-args-n))))

(t/deftest ^:kaocha/skip test-required-args-n-with-metavar
  (t/is (= :argparse/test-required-args-n-with-metavar (argparse/test-dispatch :argparse/test-required-args-n-with-metavar))))

(t/deftest ^:kaocha/skip test-required-args-optional
  (t/is (= :argparse/test-required-args-optional (argparse/test-dispatch :argparse/test-required-args-optional))))

(t/deftest ^:kaocha/skip test-required-args-zero-or-more
  (t/is (= :argparse/test-required-args-zero-or-more (argparse/test-dispatch :argparse/test-required-args-zero-or-more))))

(t/deftest ^:kaocha/skip test-required-args-one-or-more
  (t/is (= :argparse/test-required-args-one-or-more (argparse/test-dispatch :argparse/test-required-args-one-or-more))))

(t/deftest ^:kaocha/skip test-required-args-one-or-more-with-metavar
  (t/is (= :argparse/test-required-args-one-or-more-with-metavar (argparse/test-dispatch :argparse/test-required-args-one-or-more-with-metavar))))

(t/deftest ^:kaocha/skip test-required-args-remainder
  (t/is (= :argparse/test-required-args-remainder (argparse/test-dispatch :argparse/test-required-args-remainder))))

(t/deftest ^:kaocha/skip test-required-mutually-exclusive-args
  (t/is (= :argparse/test-required-mutually-exclusive-args (argparse/test-dispatch :argparse/test-required-mutually-exclusive-args))))

(t/deftest ^:kaocha/skip test-conflicting-mutually-exclusive-args-optional-with-metavar
  (t/is (= :argparse/test-conflicting-mutually-exclusive-args-optional-with-metavar (argparse/test-dispatch :argparse/test-conflicting-mutually-exclusive-args-optional-with-metavar))))

(t/deftest ^:kaocha/skip test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar1
  (t/is (= :argparse/test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar1 (argparse/test-dispatch :argparse/test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar1))))

(t/deftest ^:kaocha/skip test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar2
  (t/is (= :argparse/test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar2 (argparse/test-dispatch :argparse/test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar2))))

(t/deftest ^:kaocha/skip test-ambiguous-option
  (t/is (= :argparse/test-ambiguous-option (argparse/test-dispatch :argparse/test-ambiguous-option))))

(t/deftest ^:kaocha/skip test-os-error
  (t/is (= :argparse/test-os-error (argparse/test-dispatch :argparse/test-os-error))))

(t/deftest ^:kaocha/skip test-script
  (t/is (= :argparse/test-script (argparse/test-dispatch :argparse/test-script))))

(t/deftest ^:kaocha/skip test-script-compiled
  (t/is (= :argparse/test-script-compiled (argparse/test-dispatch :argparse/test-script-compiled))))

(t/deftest ^:kaocha/skip test-directory
  (t/is (= :argparse/test-directory (argparse/test-dispatch :argparse/test-directory))))

(t/deftest ^:kaocha/skip test-directory-compiled
  (t/is (= :argparse/test-directory-compiled (argparse/test-dispatch :argparse/test-directory-compiled))))

(t/deftest ^:kaocha/skip test-module
  (t/is (= :argparse/test-module (argparse/test-dispatch :argparse/test-module))))

(t/deftest ^:kaocha/skip test-module-compiled
  (t/is (= :argparse/test-module-compiled (argparse/test-dispatch :argparse/test-module-compiled))))

(t/deftest ^:kaocha/skip test-package
  (t/is (= :argparse/test-package (argparse/test-dispatch :argparse/test-package))))

(t/deftest ^:kaocha/skip test-package-compiled
  (t/is (= :argparse/test-package-compiled (argparse/test-dispatch :argparse/test-package-compiled))))

(t/deftest ^:kaocha/skip test-zipfile
  (t/is (= :argparse/test-zipfile (argparse/test-dispatch :argparse/test-zipfile))))

(t/deftest ^:kaocha/skip test-zipfile-compiled
  (t/is (= :argparse/test-zipfile-compiled (argparse/test-dispatch :argparse/test-zipfile-compiled))))

(t/deftest ^:kaocha/skip test-directory-in-zipfile
  (t/is (= :argparse/test-directory-in-zipfile (argparse/test-dispatch :argparse/test-directory-in-zipfile))))

(t/deftest ^:kaocha/skip test-directory-in-zipfile-compiled
  (t/is (= :argparse/test-directory-in-zipfile-compiled (argparse/test-dispatch :argparse/test-directory-in-zipfile-compiled))))

(t/deftest ^:kaocha/skip test-translations
  (t/is (= :argparse/test-translations (argparse/test-dispatch :argparse/test-translations))))

(t/deftest ^:kaocha/skip test-argparse-color
  (t/is (= :argparse/test-argparse-color (argparse/test-dispatch :argparse/test-argparse-color))))

(t/deftest ^:kaocha/skip test-argparse-color-mutually-exclusive-group-usage
  (t/is (= :argparse/test-argparse-color-mutually-exclusive-group-usage (argparse/test-dispatch :argparse/test-argparse-color-mutually-exclusive-group-usage))))

(t/deftest ^:kaocha/skip test-argparse-color-custom-usage
  (t/is (= :argparse/test-argparse-color-custom-usage (argparse/test-dispatch :argparse/test-argparse-color-custom-usage))))

(t/deftest ^:kaocha/skip colorized-test-custom-formatter-function
  (t/is (= :argparse/colorized-test-custom-formatter-function (argparse/test-dispatch :argparse/colorized-test-custom-formatter-function))))

(t/deftest ^:kaocha/skip colorized-test-custom-formatter-class
  (t/is (= :argparse/colorized-test-custom-formatter-class (argparse/test-dispatch :argparse/colorized-test-custom-formatter-class))))

(t/deftest ^:kaocha/skip test-subparser-prog-is-stored-without-color
  (t/is (= :argparse/test-subparser-prog-is-stored-without-color (argparse/test-dispatch :argparse/test-subparser-prog-is-stored-without-color))))

(t/deftest ^:kaocha/skip test-error-and-warning-keywords-colorized
  (t/is (= :argparse/test-error-and-warning-keywords-colorized (argparse/test-dispatch :argparse/test-error-and-warning-keywords-colorized))))

(t/deftest ^:kaocha/skip test-error-and-warning-not-colorized-when-disabled
  (t/is (= :argparse/test-error-and-warning-not-colorized-when-disabled (argparse/test-dispatch :argparse/test-error-and-warning-not-colorized-when-disabled))))

(t/deftest ^:kaocha/skip test-backtick-markup-in-epilog
  (t/is (= :argparse/test-backtick-markup-in-epilog (argparse/test-dispatch :argparse/test-backtick-markup-in-epilog))))

(t/deftest ^:kaocha/skip test-backtick-markup-in-description
  (t/is (= :argparse/test-backtick-markup-in-description (argparse/test-dispatch :argparse/test-backtick-markup-in-description))))

(t/deftest ^:kaocha/skip test-backtick-markup-multiple
  (t/is (= :argparse/test-backtick-markup-multiple (argparse/test-dispatch :argparse/test-backtick-markup-multiple))))

(t/deftest ^:kaocha/skip test-backtick-markup-not-applied-when-color-disabled
  (t/is (= :argparse/test-backtick-markup-not-applied-when-color-disabled (argparse/test-dispatch :argparse/test-backtick-markup-not-applied-when-color-disabled))))

(t/deftest ^:kaocha/skip test-backtick-markup-with-format-string
  (t/is (= :argparse/test-backtick-markup-with-format-string (argparse/test-dispatch :argparse/test-backtick-markup-with-format-string))))

(t/deftest ^:kaocha/skip test-backtick-markup-in-subparser
  (t/is (= :argparse/test-backtick-markup-in-subparser (argparse/test-dispatch :argparse/test-backtick-markup-in-subparser))))

(t/deftest ^:kaocha/skip test-backtick-markup-special-regex-chars
  (t/is (= :argparse/test-backtick-markup-special-regex-chars (argparse/test-dispatch :argparse/test-backtick-markup-special-regex-chars))))

(t/deftest ^:kaocha/skip test-help-with-format-specifiers
  (t/is (= :argparse/test-help-with-format-specifiers (argparse/test-dispatch :argparse/test-help-with-format-specifiers))))

(t/deftest ^:kaocha/skip test-print-help-uses-target-file-for-color-decision
  (t/is (= :argparse/test-print-help-uses-target-file-for-color-decision (argparse/test-dispatch :argparse/test-print-help-uses-target-file-for-color-decision))))

(t/deftest ^:kaocha/skip test-print-usage-uses-target-file-for-color-decision
  (t/is (= :argparse/test-print-usage-uses-target-file-for-color-decision (argparse/test-dispatch :argparse/test-print-usage-uses-target-file-for-color-decision))))

(t/deftest ^:kaocha/skip test-deprecated--version--
  (t/is (= :argparse/test-deprecated--version-- (argparse/test-dispatch :argparse/test-deprecated--version--))))

