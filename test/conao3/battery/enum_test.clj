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
