;; Original: Lib/test/test_functools.py

(ns conao3.battery.functools-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.functools :as functools])
  (:import
   [clojure.lang ExceptionInfo]))

;; TestPartialPy

(t/deftest ^:kaocha/skip test-basic-examples
  (let [p (functools/partial + 1 2)]
    (t/is (= 6 (p 3)))
    (t/is (= 13 (p 10))))
  (let [p (functools/partial map (fn [x] (* x 10)))]
    (t/is (= [10 20 30 40] (vec (p [1 2 3 4]))))))

(t/deftest ^:kaocha/skip test-arg-combinations
  (let [p (functools/partial +)]
    (t/is (= 0 (p)))
    (t/is (= 3 (p 1 2))))
  (let [p (functools/partial + 1 2)]
    (t/is (= 3 (p)))
    (t/is (= 10 (p 3 4)))))

(t/deftest ^:kaocha/skip test-no-side-effects
  (let [p (functools/partial + 0 1)]
    (t/is (= 3 (p 2)))
    (t/is (= 3 (p 2)))))

(t/deftest ^:kaocha/skip test-positional
  (doseq [args [[] [0] [0 1] [0 1 2]]]
    (let [p (apply functools/partial vector args)
          expected (conj (vec args) :x)]
      (t/is (= expected (p :x))))))

(t/deftest ^:kaocha/skip test-error-propagation
  (let [f (fn [x y] (/ x y))]
    (t/is (thrown? ArithmeticException ((functools/partial f 1 0))))
    (t/is (thrown? ArithmeticException ((functools/partial f 1) 0)))
    (t/is (thrown? ArithmeticException ((functools/partial f) 1 0)))))

(t/deftest ^:kaocha/skip test-with-bound-and-unbound-methods
  (let [data ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9"]
        join (functools/partial str/join "")]
    (t/is (= "0123456789" (join data)))))

;; TestReducePy

(t/deftest ^:kaocha/skip test-reduce
  (t/is (= "abc" (functools/reduce str ["a" "b" "c"] "")))
  (t/is (= ["a" "c" "d" "w"] (functools/reduce into [["a" "c"] [] ["d" "w"]] [])))
  (t/is (= 5040 (functools/reduce * (range 2 8) 1)))
  (t/is (= 2432902008176640000 (functools/reduce * (range 2 21) 1)))
  (t/is (= 285 (functools/reduce + (map (fn [n] (* n n)) (range 10)))))
  (t/is (= 285 (functools/reduce + (map (fn [n] (* n n)) (range 10)) 0)))
  (t/is (= 0 (functools/reduce + [] 0)))
  (t/is (= 42 (functools/reduce + [] 42)))
  (t/is (thrown? ExceptionInfo (functools/reduce + []))))

(t/deftest ^:kaocha/skip test-iterator-usage
  (t/is (= 10 (functools/reduce + (range 5))))
  (t/is (= 52 (functools/reduce + (range 5) 42)))
  (t/is (thrown? ExceptionInfo (functools/reduce + (range 0))))
  (t/is (= 42 (functools/reduce + (range 0) 42)))
  (t/is (= 0 (functools/reduce + (range 1))))
  (t/is (= 42 (functools/reduce + (range 1) 42))))
