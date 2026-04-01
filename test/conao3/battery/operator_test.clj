;; Original: Lib/test/test_operator.py

(ns conao3.battery.operator-test
  (:require
   [clojure.test :as t]
   [conao3.battery.operator :as op])
  (:import
   [clojure.lang ExceptionInfo]
   [java.util ArrayList HashMap]))

(t/deftest test-arithmetic
  (t/is (= 5 (op/add 2 3)))
  (t/is (= -1 (op/sub 2 3)))
  (t/is (= 6 (op/mul 2 3)))
  (t/is (= 2.0 (op/truediv 4 2)))
  (t/is (= 1 (op/floordiv 7 4)))
  (t/is (= 1 (op/mod 7 3)))
  (t/is (= 8.0 (op/pow 2 3)))
  (t/is (= -5 (op/neg 5)))
  (t/is (= 5 (op/pos 5)))
  (t/is (= 5.0 (op/abs -5)))
  (t/is (= 5.0 (op/abs 5))))

(t/deftest test-bitwise
  (t/is (= 4 (op/lshift 1 2)))
  (t/is (= 1 (op/rshift 4 2)))
  (t/is (= 0 (op/and_ 3 4)))
  (t/is (= 7 (op/or_ 3 4)))
  (t/is (= 7 (op/xor 3 4)))
  (t/is (= -4 (op/inv 3)))
  (t/is (= -4 (op/invert 3))))

(t/deftest test-comparisons
  (t/is (true? (op/lt 1 2)))
  (t/is (false? (op/lt 2 1)))
  (t/is (true? (op/le 1 1)))
  (t/is (true? (op/eq 1 1)))
  (t/is (true? (op/ne 1 2)))
  (t/is (true? (op/ge 2 1)))
  (t/is (true? (op/gt 2 1))))

(t/deftest test-logical
  (t/is (false? (op/not_ true)))
  (t/is (true? (op/not_ false)))
  (t/is (true? (op/truth 1)))
  (t/is (false? (op/truth nil)))
  (t/is (true? (let [x :a] (op/is_ x x))))
  (t/is (false? (op/is_ :a :b)))
  (t/is (false? (let [x :a] (op/is-not x x))))
  (t/is (true? (op/is-not :a :b))))

(t/deftest test-contains
  (t/is (true? (op/contains [1 2 3] 2)))
  (t/is (false? (op/contains [1 2 3] 5)))
  (t/is (true? (op/contains #{:a :b} :a)))
  (t/is (true? (op/contains {:a 1} :a)))
  (t/is (true? (op/contains "hello" "ell")))
  (t/is (false? (op/contains "hello" "xyz"))))

(t/deftest test-getitem
  (t/is (= 2 (op/getitem [1 2 3] 1)))
  (t/is (= 1 (op/getitem {:a 1} :a)))
  (t/is (= \e (op/getitem "hello" 1)))
  (t/is (thrown? ExceptionInfo (op/getitem {:a 1} :b)))
  (t/is (thrown? ExceptionInfo (op/getitem 42 0))))

(t/deftest test-setitem-delitem
  (let [lst (ArrayList. [1 2 3])]
    (op/setitem lst 1 99)
    (t/is (= [1 99 3] (vec lst)))
    (op/delitem lst 0)
    (t/is (= [99 3] (vec lst))))
  (let [m (HashMap. {"a" 1})]
    (op/setitem m "b" 2)
    (t/is (= {"a" 1 "b" 2} (into {} m)))
    (op/delitem m "a")
    (t/is (= {"b" 2} (into {} m)))))

(t/deftest test-length-hint
  (t/is (= 3 (op/length-hint [1 2 3])))
  (t/is (= 5 (op/length-hint "hello")))
  (t/is (= 2 (op/length-hint {:a 1 :b 2})))
  (t/is (= 0 (op/length-hint nil))))

(t/deftest test-concat
  (t/is (= "hello world" (op/concat "hello " "world")))
  (t/is (= [1 2 3 4] (op/concat [1 2] [3 4])))
  (t/is (= '(1 2 3 4) (op/concat '(1 2) '(3 4)))))

(t/deftest test-itemgetter
  (let [g (op/itemgetter 1)]
    (t/is (= 2 (g [1 2 3]))))
  (let [g (op/itemgetter 1 2)]
    (t/is (= [2 3] (g [1 2 3])))))

(t/deftest test-attrgetter
  (let [g (op/attrgetter "name")]
    (t/is (= "alice" (g {"name" "alice"}))))
  (let [g (op/attrgetter "a" "b")]
    (t/is (= ["x" "y"] (g {"a" "x" "b" "y"})))))

(t/deftest test-in-place
  (t/is (= 5 (op/iadd 2 3)))
  (t/is (= -1 (op/isub 2 3)))
  (t/is (= 6 (op/imul 2 3)))
  (t/is (= "hello world" (op/iconcat "hello " "world"))))

(t/deftest test-in-place-more
  (t/is (= 2.0 (op/itruediv 4 2)))
  (t/is (= 1 (op/ifloordiv 7 4)))
  (t/is (= 1 (op/imod 7 3)))
  (t/is (= 8.0 (op/ipow 2 3)))
  (t/is (= 4 (op/ilshift 1 2)))
  (t/is (= 1 (op/irshift 4 2)))
  (t/is (= 0 (op/iand 3 4)))
  (t/is (= 7 (op/ior 3 4)))
  (t/is (= 7 (op/ixor 3 4))))

(t/deftest test-methodcaller
  (let [upper-fn (op/methodcaller "toUpperCase")]
    (t/is (= "HELLO" (upper-fn "hello")))))

(t/deftest test-attrgetter-nested
  (let [g (op/attrgetter "user.name")]
    (t/is (= "alice" (g {"user" {:name "alice"}})))))

(t/deftest test-length-hint-java-collection
  (let [lst (doto (ArrayList.) (.add 1) (.add 2) (.add 3))]
    (t/is (= 3 (op/length-hint lst)))))

(t/deftest test-concat-seq
  (t/is (= [1 2 3 4] (vec (op/concat '(1 2) [3 4])))))

(t/deftest test-index-of
  (t/is (= 1 (op/indexOf [10 20 30] 20)))
  (t/is (= 0 (op/indexOf [1 2 3] 1)))
  (t/is (thrown? clojure.lang.ExceptionInfo (op/indexOf [1 2 3] 99))))

(t/deftest test-count-of
  (t/is (= 2 (op/countOf [1 2 2 3] 2)))
  (t/is (= 0 (op/countOf [1 2 3] 99)))
  (t/is (= 3 (op/countOf [5 5 5] 5))))

(t/deftest test-index-fn
  (t/is (= 5 (op/index 5)))
  (t/is (= 0 (op/index 0)))
  (t/is (thrown? clojure.lang.ExceptionInfo (op/index "not-an-int"))))

(t/deftest test-is-none
  (t/is (true? (op/is-none nil)))
  (t/is (false? (op/is-none "xyzpdq")))
  (t/is (false? (op/is-none "")))
  (t/is (false? (op/is-none 0))))

(t/deftest test-is-not-none
  (t/is (false? (op/is-not-none nil)))
  (t/is (true? (op/is-not-none "xyzpdq")))
  (t/is (true? (op/is-not-none "")))
  (t/is (true? (op/is-not-none 0))))

(t/deftest test-call
  (t/is (= 5 (op/call + 2 3)))
  (t/is (= "hello" (op/call str "hel" "lo")))
  (t/is (= [1 2 3] (op/call vector 1 2 3))))
