;; Original: Lib/test/test_dataclasses/__init__.py
;; All tests excluded: Python-specific dataclass metaclass / type annotation system

(ns conao3.battery.dataclasses-test
  (:require
   [clojure.test :as t]
   [conao3.battery.dataclasses :as dataclasses]))

(dataclasses/defdata Point [x y])
(dataclasses/defdata Person [name age])

(t/deftest test-defdata-creation
  (let [p (->Point 3 4)]
    (t/is (= 3 (:x p)))
    (t/is (= 4 (:y p)))))

(t/deftest test-defdata-equality
  (t/is (= (->Point 1 2) (->Point 1 2)))
  (t/is (not= (->Point 1 2) (->Point 1 3))))

(t/deftest test-is-dataclass
  (t/is (true? (dataclasses/is-dataclass? (->Point 1 2))))
  (t/is (true? (dataclasses/is-dataclass? (->Person "Alice" 30))))
  (t/is (false? (dataclasses/is-dataclass? {:x 1 :y 2})))
  (t/is (false? (dataclasses/is-dataclass? "string"))))

(t/deftest test-fields
  (t/is (= [:x :y] (dataclasses/fields (->Point 1 2))))
  (t/is (= [:name :age] (dataclasses/fields (->Person "Alice" 30)))))

(t/deftest test-asdict
  (t/is (= {:x 1 :y 2} (dataclasses/asdict (->Point 1 2))))
  (t/is (= {:name "Alice" :age 30} (dataclasses/asdict (->Person "Alice" 30)))))

(t/deftest test-astuple
  (t/is (= [1 2] (dataclasses/astuple (->Point 1 2))))
  (t/is (= ["Bob" 25] (dataclasses/astuple (->Person "Bob" 25)))))

(t/deftest test-replace
  (let [p (->Point 1 2)
        p2 (dataclasses/replace p {:x 10})]
    (t/is (= 10 (:x p2)))
    (t/is (= 2 (:y p2)))))

(t/deftest test-field-default
  (let [f (dataclasses/field :default 42)]
    (t/is (= 42 (:default f)))))
