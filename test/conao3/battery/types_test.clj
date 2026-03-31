;; Original: Lib/test/test_types.py

(ns conao3.battery.types-test
  (:require
   [clojure.test :as t]
   [conao3.battery.types :as types-m]))

(t/deftest test-type-constants
  (t/is (= clojure.lang.IFn types-m/FunctionType))
  (t/is (= java.lang.String types-m/StringType))
  (t/is (= java.lang.Long types-m/IntType))
  (t/is (= java.lang.Double types-m/FloatType))
  (t/is (= clojure.lang.Namespace types-m/ModuleType)))

(t/deftest test-new-type
  (let [Point (types-m/new-type "Point" ["x" "y"])
        p (Point 3 4)]
    (t/is (= {:x 3 :y 4} p))
    (t/is (= "Point" (:type-name (meta Point))))
    (t/is (= [:x :y] (:fields (meta Point))))))

(t/deftest test-simple-namespace
  (let [ns-obj (types-m/simple-namespace :x 1 :y 2)]
    (t/is (= 1 (types-m/simple-namespace-get ns-obj :x)))
    (t/is (= 2 (types-m/simple-namespace-get ns-obj :y)))
    (types-m/simple-namespace-set ns-obj :z 3)
    (t/is (= 3 (types-m/simple-namespace-get ns-obj :z)))
    (types-m/simple-namespace-del ns-obj :x)
    (t/is (nil? (types-m/simple-namespace-get ns-obj :x)))))

(t/deftest test-union-type
  (let [u (types-m/union-type String Long)]
    (t/is (types-m/union-type? u))
    (t/is (types-m/union-type-check u "hello"))
    (t/is (types-m/union-type-check u 42))
    (t/is (not (types-m/union-type-check u 3.14)))))

(t/deftest test-dynamic-class-attribute
  (let [attr (types-m/dynamic-class-attribute (fn [x] x))]
    (t/is (types-m/mapped-attribute? attr))
    (t/is (fn? (:getter attr)))))

(t/deftest test-isfunction-check
  (t/is (instance? clojure.lang.IFn (fn [x] x)))
  (t/is (not (instance? clojure.lang.IFn 42))))

(t/deftest test-more-type-constants
  (t/is (= java.lang.Boolean types-m/BooleanType))
  (t/is (= clojure.lang.IPersistentVector types-m/ListType))
  (t/is (= clojure.lang.IPersistentMap types-m/DictType))
  (t/is (= clojure.lang.IPersistentSet types-m/SetType)))

(t/deftest test-new-type-multiple
  (let [Color (types-m/new-type "Color" ["r" "g" "b"])
        c (Color 255 128 0)]
    (t/is (= {:r 255 :g 128 :b 0} c))
    (t/is (= "Color" (:type-name (meta Color))))
    (t/is (= [:r :g :b] (:fields (meta Color))))))

(t/deftest test-simple-namespace-multiple-keys
  (let [ns-obj (types-m/simple-namespace :x 1 :y 2 :z 3)]
    (t/is (= 1 (types-m/simple-namespace-get ns-obj :x)))
    (t/is (= 2 (types-m/simple-namespace-get ns-obj :y)))
    (t/is (= 3 (types-m/simple-namespace-get ns-obj :z)))
    (t/is (nil? (types-m/simple-namespace-get ns-obj :w)))))

(t/deftest test-union-type-three
  (let [u (types-m/union-type String Long Double)]
    (t/is (true? (boolean (types-m/union-type-check u "hello"))))
    (t/is (true? (boolean (types-m/union-type-check u 42))))
    (t/is (true? (boolean (types-m/union-type-check u 3.14))))
    (t/is (nil? (types-m/union-type-check u true)))))

(t/deftest test-bytes-type
  (t/is (instance? types-m/BytesType (byte-array 0)))
  (t/is (not (instance? types-m/BytesType "string"))))

(t/deftest test-new-type-zero-fields
  (let [Unit (types-m/new-type "Unit" [])
        u (Unit)]
    (t/is (= {} u))
    (t/is (= "Unit" (:type-name (meta Unit))))))

(t/deftest test-simple-namespace-update
  (let [ns-obj (types-m/simple-namespace :x 1)]
    (types-m/simple-namespace-set ns-obj :x 99)
    (t/is (= 99 (types-m/simple-namespace-get ns-obj :x)))))

(t/deftest test-union-type-empty-no-match
  (let [u (types-m/union-type)]
    (t/is (nil? (types-m/union-type-check u "hello")))
    (t/is (nil? (types-m/union-type-check u 42)))))

(t/deftest test-lambda-type
  (t/is (= clojure.lang.IFn types-m/LambdaType))
  (t/is (instance? types-m/LambdaType (fn [x] x))))
