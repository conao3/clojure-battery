;; Original: Lib/test/test_functools.py

(ns conao3.battery.functools-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.functools :as functools])
  (:import
   [clojure.lang ExceptionInfo]))

;; TestPartialPy

(t/deftest test-basic-examples
  (let [p (functools/partial + 1 2)]
    (t/is (= 6 (p 3)))
    (t/is (= 13 (p 10))))
  (let [p (functools/partial map (fn [x] (* x 10)))]
    (t/is (= [10 20 30 40] (vec (p [1 2 3 4]))))))

(t/deftest test-arg-combinations
  (let [p (functools/partial +)]
    (t/is (= 0 (p)))
    (t/is (= 3 (p 1 2))))
  (let [p (functools/partial + 1 2)]
    (t/is (= 3 (p)))
    (t/is (= 10 (p 3 4)))))

(t/deftest test-no-side-effects
  (let [p (functools/partial + 0 1)]
    (t/is (= 3 (p 2)))
    (t/is (= 3 (p 2)))))

(t/deftest test-positional
  (doseq [args [[] [0] [0 1] [0 1 2]]]
    (let [p (apply functools/partial vector args)
          expected (conj (vec args) :x)]
      (t/is (= expected (p :x))))))

(t/deftest test-error-propagation
  (let [f (fn [x y] (/ x y))]
    (t/is (thrown? ArithmeticException ((functools/partial f 1 0))))
    (t/is (thrown? ArithmeticException ((functools/partial f 1) 0)))
    (t/is (thrown? ArithmeticException ((functools/partial f) 1 0)))))

(t/deftest test-with-bound-and-unbound-methods
  (let [data ["0" "1" "2" "3" "4" "5" "6" "7" "8" "9"]
        join (functools/partial str/join "")]
    (t/is (= "0123456789" (join data)))))

;; TestReducePy

(t/deftest test-reduce
  (t/is (= "abc" (functools/reduce str ["a" "b" "c"] "")))
  (t/is (= ["a" "c" "d" "w"] (functools/reduce into [["a" "c"] [] ["d" "w"]] [])))
  (t/is (= 5040 (functools/reduce * (range 2 8) 1)))
  (t/is (= 2432902008176640000 (functools/reduce * (range 2 21) 1)))
  (t/is (= 285 (functools/reduce + (map (fn [n] (* n n)) (range 10)))))
  (t/is (= 285 (functools/reduce + (map (fn [n] (* n n)) (range 10)) 0)))
  (t/is (= 0 (functools/reduce + [] 0)))
  (t/is (= 42 (functools/reduce + [] 42)))
  (t/is (thrown? ExceptionInfo (functools/reduce + []))))

(t/deftest test-iterator-usage
  (t/is (= 10 (functools/reduce + (range 5))))
  (t/is (= 52 (functools/reduce + (range 5) 42)))
  (t/is (thrown? ExceptionInfo (functools/reduce + (range 0))))
  (t/is (= 42 (functools/reduce + (range 0) 42)))
  (t/is (= 0 (functools/reduce + (range 1))))
  (t/is (= 42 (functools/reduce + (range 1) 42))))

;; TestCmpToKey

(t/deftest test-cmp-to-key-basic
  (let [cmp (fn [a b] (compare a b))
        key (functools/cmp-to-key cmp)]
    (t/is (= [1 1 3 4 5] (sort-by key [3 1 4 1 5])))))

(t/deftest test-cmp-to-key-reverse
  (let [cmp (fn [a b] (compare b a))
        key (functools/cmp-to-key cmp)]
    (t/is (= [5 4 3 1 1] (sort-by key [3 1 4 1 5])))))

(t/deftest test-cmp-to-key-strings
  (let [cmp (fn [a b] (compare a b))
        key (functools/cmp-to-key cmp)]
    (t/is (= ["apple" "banana" "cherry"] (sort-by key ["banana" "apple" "cherry"])))))

;; TestLRUCache

(t/deftest test-lru-cache-basic
  (let [call-count (atom 0)
        f (functools/lru-cache (fn [x]
                                 (swap! call-count inc)
                                 (* x x)))]
    (t/is (= 4 (f 2)))
    (t/is (= 4 (f 2)))
    (t/is (= 1 @call-count))
    (t/is (= 9 (f 3)))
    (t/is (= 2 @call-count))))

(t/deftest test-lru-cache-maxsize
  (let [call-count (atom 0)
        f (functools/lru-cache 2 (fn [x]
                                   (swap! call-count inc)
                                   (* x x)))]
    (f 1)
    (f 2)
    (f 1)
    (t/is (= 2 @call-count))
    (f 3)
    (f 1)
    (t/is (>= @call-count 3))))

(t/deftest test-lru-cache-multi-args
  (let [f (functools/lru-cache (fn [a b] (+ a b)))]
    (t/is (= 3 (f 1 2)))
    (t/is (= 5 (f 2 3)))
    (t/is (= 3 (f 1 2)))))

(t/deftest test-cached-property
  (let [call-count (atom 0)
        prop (functools/cached-property (fn [obj]
                                          (swap! call-count inc)
                                          (:x obj)))
        obj {:x 42}]
    (t/is (= 42 (prop obj)))
    (t/is (= 42 (prop obj)))
    (t/is (= 1 @call-count))))

(t/deftest test-singledispatch-default
  (let [f (functools/singledispatch (fn [x] (str "default:" x)))]
    (t/is (= "default:42" (f 42)))
    (t/is (= "default:hello" (f "hello")))))

(t/deftest test-cmp-to-key-ordering
  (let [cmp (fn [a b] (compare a b))
        key (functools/cmp-to-key cmp)]
    (t/is (neg? (compare (key 1) (key 2))))
    (t/is (pos? (compare (key 3) (key 2))))
    (t/is (zero? (compare (key 5) (key 5))))))

(t/deftest test-total-ordering-identity
  (let [cls {:lt (fn [a b] (< a b))}]
    (t/is (identical? cls (functools/total-ordering cls)))))

(t/deftest test-lru-cache-info
  (let [f (functools/lru-cache (fn [x] (* x x)))]
    (f 2)
    (f 2)
    (f 3)
    (let [info (f :cache-info)]
      (t/is (= 1 (:hits info)))
      (t/is (= 2 (:misses info)))
      (t/is (= 2 (:currsize info)))
      (t/is (nil? (:maxsize info))))))

(t/deftest test-lru-cache-clear
  (let [f (functools/lru-cache (fn [x] (* x x)))]
    (f 2)
    (f 3)
    (f :cache-clear)
    (let [info (f :cache-info)]
      (t/is (= 0 (:hits info)))
      (t/is (= 0 (:misses info)))
      (t/is (= 0 (:currsize info))))))

(t/deftest test-lru-cache-wrapped
  (let [orig (fn [x] (* x x))
        f (functools/lru-cache orig)]
    (t/is (identical? orig (f :wrapped)))))

(t/deftest test-partial-no-extra-args
  ;; partial with no extra args is still callable
  (let [f (functools/partial + 0)]
    (t/is (= 5 (f 5)))
    (t/is (= 10 (f 10)))))

(t/deftest test-partial-multiple-fixed
  (let [f (functools/partial * 2 3)]
    (t/is (= 24 (f 4)))
    (t/is (= 30 (f 5)))))

(t/deftest test-reduce-with-init-and-empty
  (t/is (= 0 (functools/reduce + [] 0)))
  (t/is (= 1 (functools/reduce * [] 1))))

(t/deftest test-reduce-single-element
  (t/is (= 5 (functools/reduce + [5])))
  (t/is (= 5 (functools/reduce + [5] 0))))

(t/deftest test-lru-cache-maxsize-evicts
  (let [calls (atom 0)
        f (functools/lru-cache 2 (fn [x] (swap! calls inc) (* x x)))]
    (f 1) (f 2)
    (t/is (= 2 @calls))
    (f 1) ;; cache hit
    (t/is (= 2 @calls))
    (f 3) ;; evicts oldest (1 or 2), 1 miss
    (t/is (= 3 @calls))))

(t/deftest test-singledispatch-dispatch
  (let [f (functools/singledispatch (fn [x] (str "default:" x)))]
    ;; default dispatch
    (t/is (clojure.string/starts-with? (f 42) "default:"))))
