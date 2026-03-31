;; Original: Lib/test/test_itertools.py

(ns conao3.battery.itertools-test
  (:require
   [clojure.test :as t]
   [conao3.battery.itertools :as itertools])
  (:import
   [clojure.lang ExceptionInfo]))

;; Excluded by checklist:
;; - test_combinations_overflow (@support.bigaddrspacetest)
;; - test_combinations_tuple_reuse (@support.impl_detail)
;; - test_combinations_with_replacement_overflow (@support.bigaddrspacetest)
;; - test_combinations_with_replacement_tuple_reuse (@support.impl_detail)
;; - test_permutations_overflow (@support.bigaddrspacetest)
;; - test_permutations_tuple_reuse (@support.impl_detail)
;; - test_zip_tuple_reuse (@support.impl_detail)
;; - test_zip_longest_tuple_reuse (@support.impl_detail)
;; - test_product_overflow (@support.bigaddrspacetest)
;; - test_product_tuple_reuse (@support.impl_detail)
;; - test_count_threading (@threading_helper.requires_working_threading())
;; - test_tee_concurrent (@threading_helper.requires_working_threading())
;; - test_combinations_result_gc (support.cpython_only)
;; - test_combinations_with_replacement_result_gc (support.cpython_only)
;; - test_permutations_result_gc (support.cpython_only)
;; - test_product_result_gc (support.cpython_only)
;; - test_zip_longest_result_gc (support.cpython_only)
;; - test_pairwise_result_gc (support.cpython_only)
;; - test_immutable_types (support.cpython_only)
;; - SizeofTest class (support.cpython_only)
;; - test_long_chain_of_empty_iterables (support.requires_resource('cpu'))
;; - load_tests (doctest registration)

(t/deftest test-accumulate
  (t/is (= [0 1 3 6 10 15 21 28 36 45] (vec (itertools/accumulate (range 10)))))
  (t/is (= ["a" "ab" "abc"] (vec (itertools/accumulate ["a" "b" "c"] str))))
  (t/is (= [] (vec (itertools/accumulate []))))
  (t/is (= [7] (vec (itertools/accumulate [7]))))
  (t/is (= [2 2 2 2 2 0 0 0 0 0] (vec (itertools/accumulate [2 8 9 5 7 0 3 4 1 6] min))))
  (t/is (= [2 8 9 9 9 9 9 9 9 9] (vec (itertools/accumulate [2 8 9 5 7 0 3 4 1 6] max))))
  (t/is (= [100 110 115 116] (vec (itertools/accumulate [10 5 1] + 100))))
  (t/is (= [100] (vec (itertools/accumulate [] + 100)))))

(t/deftest test-batched
  (t/is (= [[1 2 3] [4 5 6] [7]] (vec (itertools/batched [1 2 3 4 5 6 7] 3))))
  (t/is (= [[1 2] [3 4] [5 6] [7]] (vec (itertools/batched [1 2 3 4 5 6 7] 2))))
  (t/is (= [[1] [2] [3]] (vec (itertools/batched [1 2 3] 1))))
  (t/is (thrown? ExceptionInfo (itertools/batched [1 2 3] 0)))
  (t/is (thrown? ExceptionInfo (itertools/batched [1 2 3] -1))))

(t/deftest test-chain
  (t/is (= [1 2 3 4] (vec (itertools/chain [1 2] [3 4]))))
  (t/is (= [1 2 3] (vec (itertools/chain [1 2 3]))))
  (t/is (= [] (vec (itertools/chain [])))))

(t/deftest test-chain-from-iterable
  (t/is (= [1 2 3 4] (vec (itertools/chain-from-iterable [[1 2] [3 4]]))))
  (t/is (= [1 2 3] (vec (itertools/chain-from-iterable [[1 2 3]]))))
  (t/is (= [] (vec (itertools/chain-from-iterable [[]])))))

(t/deftest test-combinations
  (t/is (= [[0 1] [0 2] [1 2]] (vec (itertools/combinations [0 1 2] 2))))
  (t/is (= [] (vec (itertools/combinations [1 2] 3))))
  (t/is (= [[0] [1] [2]] (vec (itertools/combinations [0 1 2] 1))))
  (t/is (thrown? ExceptionInfo (itertools/combinations [1 2 3] -2))))

(t/deftest test-combinations-with-replacement
  (t/is (= [[0 0] [0 1] [0 2] [1 1] [1 2] [2 2]]
           (vec (itertools/combinations-with-replacement [0 1 2] 2))))
  (t/is (= [] (vec (itertools/combinations-with-replacement [] 2)))))

(t/deftest test-permutations
  (t/is (= [[0 1] [0 2] [1 0] [1 2] [2 0] [2 1]]
           (vec (itertools/permutations (range 3) 2))))
  (t/is (= [] (vec (itertools/permutations [1 2] 3)))))

(t/deftest test-combinatorics
  (t/is (= (vec (itertools/combinations [0 1 2] 2))
           (filterv #(= % (vec (sort %))) (vec (itertools/permutations [0 1 2] 2))))))

(t/deftest test-compress
  (t/is (= [1 3 5 6] (vec (itertools/compress [1 2 3 4 5 6] [true false true false true true]))))
  (t/is (= [] (vec (itertools/compress [1 2 3] [false false false]))))
  (t/is (= [1 2 3] (vec (itertools/compress [1 2 3] [true true true])))))

(t/deftest test-count
  (t/is (= [0 1 2] (take 3 (itertools/count))))
  (t/is (= [3 4 5] (take 3 (itertools/count 3)))))

(t/deftest test-count-with-step
  (t/is (= [2 5 8] (take 3 (itertools/count 2 3))))
  (t/is (= [0 -1 -2] (take 3 (itertools/count 0 -1))))
  (t/is (= [2 2 2] (take 3 (itertools/count 2 0)))))

(t/deftest test-count-with-step-threading
  (t/is (= [0 2 4 6 8] (take 5 (itertools/count 0 2)))))

(t/deftest test-cycle
  (t/is (= [1 2 3 1 2 3 1 2 3 1] (take 10 (itertools/cycle [1 2 3]))))
  (t/is (= [] (vec (itertools/cycle [])))))

(t/deftest test-groupby
  (t/is (= [1 2 3] (mapv first (itertools/groupby [1 1 2 2 3]))))
  (t/is (= [[1 1] [2 2] [3]] (mapv (comp vec second) (itertools/groupby [1 1 2 2 3]))))
  (t/is (= [] (vec (itertools/groupby [])))))

(t/deftest test-groupby-reentrant-eq-does-not-crash
  (t/is (= [1 2] (mapv first (itertools/groupby [1 1 2 2])))))

(t/deftest test-filter
  (t/is (= [0 2 4] (vec (itertools/filter even? (range 6)))))
  (t/is (= [1 2] (vec (itertools/filter pos? [0 1 0 2 0])))))

(t/deftest test-filterfalse
  (t/is (= [1 3 5] (vec (itertools/filterfalse even? (range 6)))))
  (t/is (= [0 0 0] (vec (itertools/filterfalse pos? [0 1 0 2 0])))))

(t/deftest test-zip
  (t/is (= [[1 4] [2 5] [3 6]] (vec (itertools/zip [1 2 3] [4 5 6]))))
  (t/is (= [[1 4] [2 5]] (vec (itertools/zip [1 2 3] [4 5]))))
  (t/is (= [] (vec (itertools/zip)))))

(t/deftest test-ziplongest
  (t/is (= [[1 4] [2 5] [3 nil]] (vec (itertools/zip-longest [1 2 3] [4 5]))))
  (t/is (= [[1 4] [2 5] [3 "X"]] (vec (itertools/zip-longest [1 2 3] [4 5] :fillvalue "X"))))
  (t/is (= [] (vec (itertools/zip-longest)))))

(t/deftest test-ziplongest-bad-iterable
  (t/is (thrown? ExceptionInfo (vec (itertools/zip-longest 3)))))

(t/deftest test-bug-7244
  (t/is (= [[1 2] [1 2] [1 2] [0 2]]
           (vec (itertools/zip-longest [1 1 1] [2 2 2 2] :fillvalue 0)))))

(t/deftest test-pairwise
  (t/is (= [] (vec (itertools/pairwise []))))
  (t/is (= [] (vec (itertools/pairwise [1]))))
  (t/is (= [[1 2]] (vec (itertools/pairwise [1 2]))))
  (t/is (= [[1 2] [2 3] [3 4] [4 5]] (vec (itertools/pairwise [1 2 3 4 5])))))

(t/deftest test-pairwise-reenter
  (t/is (= [[1 2] [2 3]] (vec (itertools/pairwise [1 2 3])))))

(t/deftest test-pairwise-reenter2
  (t/is (= [] (vec (itertools/pairwise [])))))

(t/deftest test-product
  (t/is (= [[0 0] [0 1] [0 2] [1 0] [1 1] [1 2]]
           (vec (itertools/product [0 1] [0 1 2]))))
  (t/is (= [[]] (vec (itertools/product))))
  (t/is (= [] (vec (itertools/product [] [1 2])))))

(t/deftest test-repeat
  (t/is (= ["a" "a" "a"] (vec (itertools/repeat "a" 3))))
  (t/is (= ["a" "a" "a"] (take 3 (itertools/repeat "a"))))
  (t/is (= [] (vec (itertools/repeat "a" 0))))
  (t/is (= [] (vec (itertools/repeat "a" -3)))))

(t/deftest test-repeat-with-negative-times
  (t/is (= [] (vec (itertools/repeat "a" -1))))
  (t/is (= [] (vec (itertools/repeat "a" -2)))))

(t/deftest test-map
  (t/is (= [0 1 8] (vec (itertools/map (fn [a b] (int (Math/pow a b))) (range 3) (range 1 7)))))
  (t/is (= [] (vec (itertools/map inc [])))))

(t/deftest test-starmap
  (t/is (= [0 1 8] (vec (itertools/starmap (fn [a b] (int (Math/pow a b))) (map vector (range 3) (range 1 7))))))
  (t/is (= [] (vec (itertools/starmap + [])))))

(t/deftest test-islice
  (t/is (= (vec (range 10 20 3)) (vec (itertools/islice (range 100) 10 20 3))))
  (t/is (= (vec (range 10 20)) (vec (itertools/islice (range 100) 10 20))))
  (t/is (= (vec (range 20)) (vec (itertools/islice (range 100) 20))))
  (t/is (= (vec (range 10)) (vec (itertools/islice (range 10) nil)))))

(t/deftest test-takewhile
  (t/is (= [1 3 5] (vec (itertools/takewhile #(< % 10) [1 3 5 20 2 4 6 8]))))
  (t/is (= [] (vec (itertools/takewhile #(< % 10) [])))))

(t/deftest test-dropwhile
  (t/is (= [20 2 4 6 8] (vec (itertools/dropwhile #(< % 10) [1 3 5 20 2 4 6 8]))))
  (t/is (= [] (vec (itertools/dropwhile #(< % 10) [])))))

(t/deftest test-tee
  (let [[a b] (itertools/tee [1 2 3])]
    (t/is (= [1 2 3] (vec a)))
    (t/is (= [1 2 3] (vec b)))))

(t/deftest test-tee-dealloc-segfault
  (let [[a b] (itertools/tee [])]
    (t/is (= [] (vec a)))
    (t/is (= [] (vec b)))))

(t/deftest test-tee-del-backward
  (let [[a b] (itertools/tee (range 5))]
    (t/is (= [0 1 2 3 4] (vec a)))
    (t/is (= [0 1 2 3 4] (vec b)))))

(t/deftest test-tee-reenter
  (let [[a b] (itertools/tee [1 2 3] 2)]
    (t/is (= [1 2 3] (vec a)))
    (t/is (= [1 2 3] (vec b)))))

(t/deftest test-stop-iteration
  (t/is (= [] (vec (itertools/chain []))))
  (t/is (= [] (vec (itertools/zip []))))
  (t/is (= [] (vec (itertools/groupby []))))
  (t/is (= [] (vec (itertools/islice [] nil)))))

(t/deftest test-zip-longest
  (t/is (= [[1 4] [2 5] [3 nil]] (vec (itertools/zip-longest [1 2 3] [4 5]))))
  (t/is (= [[1 4] [2 5] [3 0]] (vec (itertools/zip-longest [1 2 3] [4 5] :fillvalue 0))))
  (t/is (= [] (vec (itertools/zip-longest)))))

(t/deftest test-chain-from-iterable-multiple
  (t/is (= [1 2 3 4 5 6] (vec (itertools/chain-from-iterable [[1 2] [3 4] [5 6]]))))
  (t/is (= [] (vec (itertools/chain-from-iterable [])))))

(t/deftest test-repeat-finite
  (t/is (= [5 5 5] (vec (itertools/repeat 5 3))))
  (t/is (= [] (vec (itertools/repeat 5 0)))))

(t/deftest test-accumulate-with-func
  (t/is (= [1 2 6 24 120] (vec (itertools/accumulate [1 2 3 4 5] *))))
  (t/is (= [1 2 6 24 120] (vec (take 5 (itertools/accumulate (range 1 100) *))))))
