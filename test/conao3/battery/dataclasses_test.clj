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

(t/deftest test-is-dataclass-class
  (t/is (true? (dataclasses/is-dataclass? Point)))
  (t/is (true? (dataclasses/is-dataclass? Person))))

(t/deftest test-replace-multiple
  (let [p (->Person "Alice" 30)
        p2 (dataclasses/replace p {:name "Bob" :age 25})]
    (t/is (= "Bob" (:name p2)))
    (t/is (= 25 (:age p2)))))

(t/deftest test-asdict-is-map
  (let [d (dataclasses/asdict (->Point 5 10))]
    (t/is (map? d))
    (t/is (= 5 (:x d)))
    (t/is (= 10 (:y d)))))

(t/deftest test-field-with-factory
  (let [f (dataclasses/field :default-factory list)]
    (t/is (= list (:default-factory f)))))

(t/deftest test-defdata-not-dataclass-plain-map
  (t/is (false? (dataclasses/is-dataclass? {:x 1 :y 2})))
  (t/is (false? (dataclasses/is-dataclass? nil))))

(t/deftest test-register-dataclass
  (let [my-class {:fields [:a :b]}]
    (t/is (set? (dataclasses/register-dataclass! my-class)))))

(t/deftest test-asdict-nested-stays-shallow
  ;; asdict returns a flat map (no deep copy of values)
  (let [inner [1 2 3]
        p (->Point inner 4)
        d (dataclasses/asdict p)]
    (t/is (identical? inner (:x d)))))

(t/deftest test-astuple-length
  (t/is (= 2 (count (dataclasses/astuple (->Point 1 2)))))
  (t/is (= 2 (count (dataclasses/astuple (->Person "Alice" 30))))))

(t/deftest test-replace-preserves-original
  (let [orig (->Point 1 2)
        new-p (dataclasses/replace orig {:x 99})]
    ;; original unchanged
    (t/is (= 1 (:x orig)))
    ;; new record has updated field
    (t/is (= 99 (:x new-p)))
    (t/is (= 2 (:y new-p)))))

(t/deftest test-fields-returns-keywords
  (let [fs (dataclasses/fields (->Point 3 4))]
    (t/is (every? keyword? fs))))

(t/deftest test-defdata-instances-not-equal-when-different-types
  ;; Point and Person with same-arity data are different types
  (t/is (not= (->Point 1 2) (->Person "a" 1))))
