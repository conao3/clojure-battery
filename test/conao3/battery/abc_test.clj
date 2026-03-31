;; Original: Lib/test/test_abc.py

(ns conao3.battery.abc-test
  (:require
   [clojure.test :as t]
   [conao3.battery.abc :as abc-m])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-abstractmethod
  (let [f (abc-m/abstractmethod (fn [x] x))]
    (t/is (fn? f))
    (t/is (abc-m/abstract? f))))

(t/deftest test-not-abstract
  (let [f (fn [x] x)]
    (t/is (not (abc-m/abstract? f)))))

(t/deftest test-defabstract
  (abc-m/defabstract my-abstract-fn [x]
    "abstract method")
  (t/is (abc-m/abstract? my-abstract-fn))
  (t/is (thrown? ExceptionInfo (my-abstract-fn 42))))

(t/deftest test-get-abstractmethods
  (let [cls {:foo (abc-m/abstractmethod (fn [x] x))
             :bar (fn [x] x)
             :baz (abc-m/abstractmethod (fn [x] x))}]
    (t/is (= #{:foo :baz} (abc-m/get-abstractmethods cls)))))

(t/deftest test-isinstance
  (t/is (true? (abc-m/isinstance? "hello" String)))
  (t/is (false? (abc-m/isinstance? 42 String)))
  (t/is (true? (abc-m/isinstance? 42 Long))))

(t/deftest test-issubclass
  (t/is (true? (abc-m/issubclass? Object String)))
  (t/is (false? (abc-m/issubclass? String Object))))

(t/deftest test-abstractmethod-preserves-fn
  (let [f (fn [x y] (+ x y))
        af (abc-m/abstractmethod f)]
    (t/is (= (f 1 2) (af 1 2)))
    (t/is (abc-m/abstract? af))
    (t/is (not (abc-m/abstract? f)))))

(t/deftest test-get-abstractmethods-empty
  (let [cls {:concrete (fn [x] x)}]
    (t/is (empty? (abc-m/get-abstractmethods cls)))))

(t/deftest test-isinstance-inheritance
  (t/is (true? (abc-m/isinstance? "hello" CharSequence)))
  (t/is (true? (abc-m/isinstance? "hello" Comparable)))
  (t/is (false? (abc-m/isinstance? 42 String))))

(t/deftest test-issubclass-hierarchy
  (t/is (true? (abc-m/issubclass? Number Long)))
  (t/is (true? (abc-m/issubclass? Object Long)))
  (t/is (false? (abc-m/issubclass? Long Object))))
