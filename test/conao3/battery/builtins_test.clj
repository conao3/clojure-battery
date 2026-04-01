;; Original: Lib/test/test_builtin.py

(ns conao3.battery.builtins-test
  (:require
   [clojure.test :as t]
   [conao3.battery.builtins :as b])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-abs
  (t/is (= 5.0 (b/abs 5)))
  (t/is (= 5.0 (b/abs -5)))
  (t/is (= 0.0 (b/abs 0))))

(t/deftest test-all
  (t/is (true? (b/all [true true true])))
  (t/is (false? (b/all [true false true])))
  (t/is (true? (b/all []))))

(t/deftest test-any
  (t/is (true? (b/any [false true false])))
  (t/is (false? (b/any [false false false])))
  (t/is (false? (b/any []))))

(t/deftest test-bin
  (t/is (= "0b1010" (b/bin 10)))
  (t/is (= "0b0" (b/bin 0))))

(t/deftest test-bool
  (t/is (true? (b/bool 1)))
  (t/is (false? (b/bool nil)))
  (t/is (false? (b/bool false))))

(t/deftest test-chr-ord
  (t/is (= \A (b/chr 65)))
  (t/is (= 65 (b/ord \A)))
  (t/is (= \newline (b/chr 10))))

(t/deftest test-divmod
  (t/is (= [3 1] (b/divmod 10 3)))
  (t/is (= [0 5] (b/divmod 5 7))))

(t/deftest test-enumerate
  (t/is (= [[0 "a"] [1 "b"] [2 "c"]] (vec (b/enumerate ["a" "b" "c"]))))
  (t/is (= [[5 "a"] [6 "b"]] (vec (b/enumerate ["a" "b"] 5)))))

(t/deftest test-filter
  (t/is (= [2 4] (vec (b/filter even? [1 2 3 4]))))
  (t/is (= [1 2 3] (vec (b/filter identity [1 nil 2 false 3])))))

(t/deftest test-hex-oct
  (t/is (= "0xff" (b/hex 255)))
  (t/is (= "0o377" (b/oct 255))))

(t/deftest test-len
  (t/is (= 5 (b/len "hello")))
  (t/is (= 3 (b/len [1 2 3])))
  (t/is (= 2 (b/len {:a 1 :b 2}))))

(t/deftest test-max-min
  (t/is (= 5 (b/max [1 3 5 2 4])))
  (t/is (= 1 (b/min [1 3 5 2 4])))
  (t/is (= 5 (b/max 1 3 5 2 4)))
  (t/is (= 1 (b/min 1 3 5 2 4))))

(t/deftest test-pow
  (t/is (= 8.0 (b/pow 2 3)))
  (t/is (= 2.0 (b/pow 8 1/3)))
  (t/is (= 2 (b/pow 2 10 1022))))

(t/deftest test-repr
  (t/is (= "'hello'" (b/repr "hello")))
  (t/is (= "None" (b/repr nil)))
  (t/is (= "True" (b/repr true)))
  (t/is (= "False" (b/repr false))))

(t/deftest test-reversed
  (t/is (= [3 2 1] (vec (b/reversed [1 2 3])))))

(t/deftest test-round
  (t/is (= 3 (b/round 3.4)))
  (t/is (= 4 (b/round 3.6)))
  (t/is (= 3.14 (b/round 3.14159 2))))

(t/deftest test-sorted
  (t/is (= [1 1 3 4 5] (b/sorted [3 1 4 1 5] {})))
  (t/is (= [5 4 3 1 1] (b/sorted [3 1 4 1 5] {:reverse true}))))

(t/deftest test-sum
  (t/is (= 15 (b/sum [1 2 3 4 5])))
  (t/is (= 25 (b/sum [1 2 3 4 5] 10))))

(t/deftest test-zip
  (t/is (= [[1 4] [2 5] [3 6]] (vec (b/zip [1 2 3] [4 5 6]))))
  (t/is (= [[1 4 7] [2 5 8]] (vec (b/zip [1 2 3] [4 5 6] [7 8])))))

(t/deftest test-iter
  (t/is (= [1 2 3] (vec (b/iter [1 2 3])))))

(t/deftest test-iter-callable-sentinel
  (let [counter (atom 0)
        f (fn [] (swap! counter inc))
        result (vec (b/iter f 4))]
    (t/is (= [1 2 3] result))))

(t/deftest test-format-basic
  (t/is (= "hello" (b/format "hello" "")))
  (t/is (= "42" (b/format 42 ""))))

(t/deftest test-getattr-default
  (t/is (= "default" (b/getattr {:a 1} :b "default")))
  (t/is (= 1 (b/getattr {:a 1} :a "default"))))

(t/deftest test-hasattr
  (t/is (true? (b/hasattr {:a 1 :b 2} :a)))
  (t/is (false? (b/hasattr {:a 1} :z)))
  (t/is (false? (b/hasattr "string" :anything))))

(t/deftest test-setattr
  (t/is (= {:a 1 :b 2} (b/setattr {:a 1} :b 2)))
  (t/is (= {:a 99} (b/setattr {:a 1} :a 99))))

(t/deftest test-callable
  (t/is (true? (b/callable? (fn [x] x))))
  (t/is (false? (b/callable? 42)))
  (t/is (false? (b/callable? "string"))))

(t/deftest test-next
  (t/is (= 1 (b/next [1 2 3])))
  (t/is (= :default (b/next [] :default)))
  (t/is (= 1 (b/next (seq [1 2 3])))))

(t/deftest test-hash
  (t/is (integer? (b/hash 42)))
  (t/is (integer? (b/hash "hello")))
  (t/is (= (b/hash 42) (b/hash 42))))

(t/deftest test-id
  (let [x {:a 1}]
    (t/is (integer? (b/id x)))
    (t/is (= (b/id x) (b/id x)))))

(t/deftest test-range
  (t/is (= [0 1 2 3 4] (vec (b/range 5))))
  (t/is (= [2 3 4] (vec (b/range 2 5))))
  (t/is (= [0 2 4] (vec (b/range 0 5 2)))))

(t/deftest test-map-builtin
  (t/is (= [1 4 9] (vec (b/map #(* % %) [1 2 3]))))
  (t/is (= [5 7 9] (vec (b/map + [1 2 3] [4 5 6])))))

(t/deftest test-isinstance-issubclass
  (t/is (true? (b/isinstance 42 Long)))
  (t/is (true? (b/isinstance "s" String)))
  (t/is (false? (b/isinstance 42 String)))
  (t/is (true? (b/issubclass Long Number))))

(t/deftest test-vars-map
  (let [m {:a 1 :b 2}]
    (t/is (= m (b/vars m)))))

(t/deftest test-vars-namespace
  (let [result (b/vars (find-ns 'clojure.core))]
    (t/is (map? result))
    (t/is (pos? (count result)))))

;; Python test_builtin.py 互換テスト
(t/deftest test-divmod-negative
  ;; Python uses floor division: divmod(-12, 7) = (-2, 2)
  (t/is (= [-2 2] (b/divmod -12 7)))
  (t/is (= [-2 -2] (b/divmod 12 -7)))
  (t/is (= [1 -5] (b/divmod -12 -7)))
  ;; verify: q*b + r == a
  (doseq [[a b] [[-12 7] [12 -7] [-12 -7] [-5 3] [5 -3]]]
    (let [[q r] (b/divmod a b)]
      (t/is (= a (+ (* q b) r)) (str "divmod(" a "," b ")"))))
  ;; float divmod
  (t/is (= [3.0 0.25] (b/divmod 3.25 1.0)))
  (t/is (= [-4.0 0.75] (b/divmod -3.25 1.0))))

(t/deftest test-divmod-zero-remainder
  (t/is (= [2 0] (b/divmod 10 5)))
  (t/is (= [-2 0] (b/divmod -10 5)))
  (t/is (= [-2 0] (b/divmod 10 -5))))

(t/deftest test-pow-special
  ;; pow(0, 0) = 1.0 in Clojure (Math/pow)
  (t/is (= 1.0 (b/pow 0 0)))
  (t/is (= 1.0 (b/pow -1 0)))
  (t/is (= 0.0 (b/pow 0 1)))
  ;; pow with 3 args (modular exponentiation)
  (t/is (= 2 (b/pow 2 10 1022)))
  (t/is (= 0 (b/pow 2 3 8))))

(t/deftest test-max-min-empty
  ;; max/min with empty collection throws
  (t/is (thrown? Exception (b/max [])))
  (t/is (thrown? Exception (b/min []))))

(t/deftest test-max-min-key
  (t/is (= "hello" (b/max ["hi" "hello" "hey"] :key count)))
  (t/is (= "hi" (b/min ["hi" "hello" "hey"] :key count))))

(t/deftest test-sum-edge-cases
  (t/is (= 10 (b/sum [] 10)))
  (t/is (= 0 (b/sum [])))
  (t/is (= 0.0 (b/sum [0.0])))
  (t/is (Double/isInfinite (b/sum [(Double/POSITIVE_INFINITY) 1.0]))))

(t/deftest test-round-ndigits
  (t/is (= 3.14 (b/round 3.14159 2)))
  (t/is (= 3.142 (b/round 3.14159 3)))
  (t/is (= 3.0 (b/round 3.14159 0)))
  (t/is (= -3.14 (b/round -3.14159 2))))

(t/deftest test-len-string
  (t/is (= 0 (b/len "")))
  (t/is (= 5 (b/len "hello"))))

(t/deftest test-len-collections
  (t/is (= 0 (b/len [])))
  (t/is (= 3 (b/len [1 2 3])))
  (t/is (= 2 (b/len {:a 1 :b 2})))
  (t/is (= 3 (b/len #{1 2 3}))))

(t/deftest test-sorted-key
  (t/is (= ["hi" "hey" "hello"] (b/sorted ["hello" "hi" "hey"] {:key count})))
  (t/is (= ["hello" "hey" "hi"] (b/sorted ["hello" "hi" "hey"] {:key count :reverse true}))))
