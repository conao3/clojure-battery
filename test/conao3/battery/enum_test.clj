;; Original: Lib/test/test_enum.py
;; All tests excluded: Python-specific Enum type system (no direct Clojure equivalent)

(ns conao3.battery.enum-test
  (:require
   [clojure.test :as t]
   [conao3.battery.enum :as enum])
  (:import
   [clojure.lang ExceptionInfo]))

(enum/defenum Color
  RED   1
  GREEN 2
  BLUE  3)

(enum/defenum Direction
  NORTH "north"
  SOUTH "south"
  EAST  "east"
  WEST  "west")

(t/deftest test-defenum-creation
  (t/is (map? Color))
  (t/is (contains? Color :RED))
  (t/is (contains? Color :GREEN))
  (t/is (contains? Color :BLUE)))

(t/deftest test-enum-value
  (t/is (= 1 (enum/value (:RED Color))))
  (t/is (= 2 (enum/value (:GREEN Color))))
  (t/is (= 3 (enum/value (:BLUE Color)))))

(t/deftest test-enum-name
  (t/is (= "RED" (enum/enum-name (:RED Color))))
  (t/is (= "GREEN" (enum/enum-name (:GREEN Color)))))

(t/deftest test-string-enum
  (t/is (= "north" (enum/value (:NORTH Direction))))
  (t/is (= "NORTH" (enum/enum-name (:NORTH Direction)))))

(t/deftest test-from-value
  (t/is (= (:RED Color) (enum/from-value Color 1)))
  (t/is (= (:BLUE Color) (enum/from-value Color 3)))
  (t/is (nil? (enum/from-value Color 99))))

(t/deftest test-members
  (t/is (= 3 (count (enum/members Color))))
  (t/is (= 4 (count (enum/members Direction)))))

(t/deftest test-unique-valid
  (t/is (map? (enum/unique Color))))

(t/deftest test-unique-duplicate
  (let [dup-enum {:A (enum/->EnumMember "Dup" "A" 1)
                  :B (enum/->EnumMember "Dup" "B" 1)}]
    (t/is (thrown? ExceptionInfo (enum/unique dup-enum)))))

(t/deftest test-enum-member-fields
  (let [m (:RED Color)]
    (t/is (= "Color" (:enum-name m)))
    (t/is (= "RED" (:member-name m)))
    (t/is (= 1 (:value m)))))

(t/deftest test-is-enum-false-for-member
  (t/is (false? (enum/is-enum? (:RED Color)))))

(t/deftest test-from-value-string-enum
  (t/is (= (:SOUTH Direction) (enum/from-value Direction "south")))
  (t/is (nil? (enum/from-value Direction "invalid"))))

(t/deftest test-members-are-enum-members
  (let [ms (enum/members Color)]
    (t/is (= 3 (count ms)))
    (t/is (every? #(instance? conao3.battery.enum.EnumMember %) ms))))

(t/deftest test-defenum-creates-map
  (t/is (map? Color))
  (t/is (map? Direction)))

(t/deftest test-auto
  (let [v1 (enum/auto)
        v2 (enum/auto)]
    (t/is (integer? v1))
    (t/is (integer? v2))
    (t/is (= 1 (- v2 v1)))))

(t/deftest test-register-enum
  (let [my-enum {:A (enum/->EnumMember "MyEnum" "A" 1)}]
    (t/is (set? (enum/register-enum! my-enum)))))

(t/deftest test-is-enum-true-for-enum
  (t/is (true? (enum/is-enum? Color)))
  (t/is (true? (enum/is-enum? Direction))))

(t/deftest test-enum-member-print
  ;; print-method produces readable output
  (let [m (:RED Color)
        s (pr-str m)]
    (t/is (string? s))
    (t/is (clojure.string/includes? s "RED"))))

(t/deftest test-from-value-missing
  ;; from-value returns nil for missing values
  (t/is (nil? (enum/from-value Color 0)))
  (t/is (nil? (enum/from-value Color -1)))
  (t/is (nil? (enum/from-value Direction ""))))

(t/deftest test-members-order-independent
  ;; members returns all members as a set
  (let [ms (set (map enum/enum-name (enum/members Color)))]
    (t/is (= #{"RED" "GREEN" "BLUE"} ms))))

(t/deftest test-defenum-all-values-unique
  ;; all values in a freshly defined enum are unique
  (t/is (= 3 (count (set (map enum/value (enum/members Color))))))
  (t/is (= 4 (count (set (map enum/value (enum/members Direction)))))))

(t/deftest test-members-as-seq
  ;; members returns a sequential collection that can be iterated
  (let [ms (enum/members Color)]
    (t/is (sequential? ms))
    (t/is (= 3 (count ms)))))

(t/deftest test-enum-member-equality
  ;; the same key in the same enum map gives equal members
  (t/is (= (:RED Color) (:RED Color)))
  (t/is (not= (:RED Color) (:GREEN Color))))

(t/deftest test-from-value-returns-enum-member
  ;; from-value returns an EnumMember instance, not just a value
  (let [m (enum/from-value Color 2)]
    (t/is (instance? conao3.battery.enum.EnumMember m))
    (t/is (= "GREEN" (enum/enum-name m)))))

(t/deftest test-enum-member-as-map-key
  ;; EnumMember instances are value objects and can serve as map keys
  (let [m (:RED Color)
        h {m :found}]
    (t/is (= :found (get h (:RED Color))))))

(t/deftest test-unique-returns-same-map
  ;; unique returns a map identical in structure to the original
  (let [result (enum/unique Color)]
    (t/is (map? result))
    (t/is (= (count Color) (count result)))
    (t/is (contains? result :RED))))
