;; Original: Lib/test/test_heapq.py

(ns conao3.battery.heapq-test
  (:require
   [clojure.test :as t]
   [conao3.battery.heapq :as heapq])
  (:import
   [clojure.lang ExceptionInfo]))

;; TestModules.test_py_functions / test_c_functions → excluded (import_helper CPython internals)
;; TestHeapC → excluded (@skipUnless c_heapq)
;; TestErrorHandlingC → excluded (@skipUnless c_heapq)
;; load_tests → excluded (load_tests doctest registration)

;; ---- TestHeapPython (= TestHeap) ----

(t/deftest ^:kaocha/skip test-push-pop
  (let [heap (atom [])]
    (heapq/heappush! heap 3)
    (heapq/heappush! heap 1)
    (heapq/heappush! heap 2)
    (t/is (= 1 (heapq/heappop! heap)))
    (t/is (= 2 (heapq/heappop! heap)))
    (t/is (= 3 (heapq/heappop! heap))))
  (t/is (thrown? ExceptionInfo (heapq/heappush! nil 1)))
  (t/is (thrown? ExceptionInfo (heapq/heappop! nil))))

(t/deftest ^:kaocha/skip test-max-push-pop
  (let [heap (atom [])]
    (heapq/heappush-max! heap 1)
    (heapq/heappush-max! heap 3)
    (heapq/heappush-max! heap 2)
    (t/is (= 3 (heapq/heappop-max! heap)))
    (t/is (= 2 (heapq/heappop-max! heap)))
    (t/is (= 1 (heapq/heappop-max! heap))))
  (t/is (thrown? ExceptionInfo (heapq/heappush-max! nil 1))))

(t/deftest ^:kaocha/skip test-heapify
  (let [heap (atom [3 1 4 1 5 9 2 6])]
    (heapq/heapify! heap)
    (let [results (loop [acc []]
                    (if (empty? @heap)
                      acc
                      (recur (conj acc (heapq/heappop! heap)))))]
      (t/is (= [1 1 2 3 4 5 6 9] results))))
  (t/is (thrown? ExceptionInfo (heapq/heapify! nil))))

(t/deftest ^:kaocha/skip test-heapify-max
  (let [heap (atom [3 1 4 1 5 9 2 6])]
    (heapq/heapify-max! heap)
    (let [results (loop [acc []]
                    (if (empty? @heap)
                      acc
                      (recur (conj acc (heapq/heappop-max! heap)))))]
      (t/is (= [9 6 5 4 3 2 1 1] results))))
  (t/is (thrown? ExceptionInfo (heapq/heapify-max! nil))))

(t/deftest ^:kaocha/skip test-naive-nbest
  (let [data [5 3 8 1 9 2 7 4 6 0 12 11 10]
        heap (atom [])]
    (doseq [item data]
      (heapq/heappush! heap item)
      (when (> (count @heap) 4)
        (heapq/heappop! heap)))
    (t/is (= (sort (take-last 4 (sort data))) (sort @heap)))))

(t/deftest ^:kaocha/skip test-nbest
  (let [data [5 3 8 1 9 2 7 4 6 0]
        heap (atom (vec (take 3 data)))]
    (heapq/heapify! heap)
    (doseq [item (drop 3 data)]
      (when (> item (first @heap))
        (heapq/heapreplace! heap item)))
    (t/is (= (sort (take-last 3 (sort data))) (sort @heap))))
  (t/is (thrown? ExceptionInfo (heapq/heapreplace! (atom nil) 1)))
  (t/is (thrown? ExceptionInfo (heapq/heapreplace! (atom []) 1))))

(t/deftest ^:kaocha/skip test-nbest-maxheap
  (let [data [5 3 8 1 9 2 7 4 6 0]
        heap (atom (vec data))]
    (heapq/heapify-max! heap)
    (let [result (vec (repeatedly 3 #(heapq/heappop-max! heap)))]
      (t/is (= (reverse (take-last 3 (sort data))) result)))))

(t/deftest ^:kaocha/skip test-nbest-with-pushpop
  (let [data [5 3 8 1 9 2 7 4 6 0]
        heap (atom (vec (take 3 data)))]
    (heapq/heapify! heap)
    (doseq [item (drop 3 data)]
      (heapq/heappushpop! heap item))
    (t/is (= (sort (take-last 3 (sort data))) (sort @heap))))
  (t/is (= "x" (heapq/heappushpop! (atom []) "x"))))

(t/deftest ^:kaocha/skip test-naive-nworst
  (let [data [5 3 8 1 9 2 7 4 6 0]
        heap (atom [])]
    (doseq [item data]
      (heapq/heappush-max! heap item)
      (when (> (count @heap) 3)
        (heapq/heappop-max! heap)))
    (t/is (= (sort (take 3 (sort data))) (sort @heap)))))

(t/deftest ^:kaocha/skip test-nworst
  (let [data [5 3 8 1 9 2 7 4 6 0]
        heap (atom (vec (take 3 data)))]
    (heapq/heapify-max! heap)
    (doseq [item (drop 3 data)]
      (when (< item (first @heap))
        (heapq/heapreplace-max! heap item)))
    (t/is (= (sort (take 3 (sort data))) (sort @heap))))
  (t/is (thrown? ExceptionInfo (heapq/heapreplace-max! (atom nil) 1)))
  (t/is (thrown? ExceptionInfo (heapq/heapreplace-max! (atom []) 1))))

(t/deftest ^:kaocha/skip test-nworst-minheap
  (let [data [5 3 8 1 9 2 7 4 6 0]
        heap (atom (vec data))]
    (heapq/heapify! heap)
    (let [result (vec (repeatedly 3 #(heapq/heappop! heap)))]
      (t/is (= (take 3 (sort data)) (reverse result))))))

(t/deftest ^:kaocha/skip test-nworst-with-pushpop
  (let [data [5 3 8 1 9 2 7 4 6 0]
        heap (atom (vec (take 3 data)))]
    (heapq/heapify-max! heap)
    (doseq [item (drop 3 data)]
      (heapq/heappushpop-max! heap item))
    (t/is (= (take 3 (sort data)) (sort @heap))))
  (t/is (= "x" (heapq/heappushpop-max! (atom []) "x"))))

(t/deftest ^:kaocha/skip test-heappushpop
  (let [heap (atom [])]
    (t/is (= 10 (heapq/heappushpop! heap 10)))
    (t/is (empty? @heap)))
  (let [heap (atom [10])]
    (t/is (= 9 (heapq/heappushpop! heap 9)))
    (t/is (= [10] @heap)))
  (let [heap (atom [10])]
    (t/is (= 10 (heapq/heappushpop! heap 11)))
    (t/is (= [11] @heap))))

(t/deftest ^:kaocha/skip test-heappushpop-max
  (let [heap (atom [])]
    (t/is (= 10 (heapq/heappushpop-max! heap 10)))
    (t/is (empty? @heap)))
  (let [heap (atom [10])]
    (t/is (= 11 (heapq/heappushpop-max! heap 11)))
    (t/is (= [10] @heap)))
  (let [heap (atom [10])]
    (t/is (= 10 (heapq/heappushpop-max! heap 9)))
    (t/is (= [9] @heap))))

(t/deftest ^:kaocha/skip test-heappop-max
  (let [heap (atom [3 2])]
    (t/is (= 3 (heapq/heappop-max! heap)))
    (t/is (= 2 (heapq/heappop-max! heap)))))

(t/deftest ^:kaocha/skip test-heapsort
  (let [data [3 1 4 1 5 9 2 6]
        heap (atom (vec data))]
    (heapq/heapify! heap)
    (let [results (loop [acc []]
                    (if (empty? @heap)
                      acc
                      (recur (conj acc (heapq/heappop! heap)))))]
      (t/is (= (sort data) results)))))

(t/deftest ^:kaocha/skip test-heapsort-max
  (let [data [3 1 4 1 5 9 2 6]
        heap (atom (vec data))]
    (heapq/heapify-max! heap)
    (let [results (loop [acc []]
                    (if (empty? @heap)
                      acc
                      (recur (conj acc (heapq/heappop-max! heap)))))]
      (t/is (= (sort > data) results)))))

(t/deftest ^:kaocha/skip test-merge
  (t/is (= [1 2 3 4 5 6] (vec (heapq/heap-merge [1 3 5] [2 4 6]))))
  (t/is (= [1 2 3] (vec (heapq/heap-merge [1] [2] [3]))))
  (t/is (= [] (vec (heapq/heap-merge)))))

(t/deftest ^:kaocha/skip test-empty-merges
  (t/is (= [] (vec (heapq/heap-merge [] []))))
  (t/is (= [] (vec (heapq/heap-merge [] [] :key identity)))))

(t/deftest ^:kaocha/skip test-merge-does-not-suppress-index-error
  (t/is (thrown? IndexOutOfBoundsException
                 (let [bad-seq (concat [1 2] (lazy-seq (throw (IndexOutOfBoundsException. "boom"))))]
                   (vec (heapq/heap-merge bad-seq))))))

(t/deftest ^:kaocha/skip test-merge-stability
  (let [tagged (fn [src xs] (map #(hash-map :v % :src src) xs))
        s1 (tagged 0 [1 3 5])
        s2 (tagged 1 [1 3 5])
        result (vec (heapq/heap-merge s1 s2 :key :v))]
    (t/is (= (map :v result) (sort (map :v (concat s1 s2)))))
    (t/is (= 0 (:src (first result))))))

(t/deftest ^:kaocha/skip test-nsmallest
  (t/is (= [1 2 3] (heapq/nsmallest 3 [5 1 4 2 3])))
  (t/is (= [] (heapq/nsmallest 0 [5 1 4 2 3])))
  (t/is (= [1] (heapq/nsmallest 1 [3 1 2]))))

(t/deftest ^:kaocha/skip test-nlargest
  (t/is (= [5 4 3] (heapq/nlargest 3 [5 1 4 2 3])))
  (t/is (= [] (heapq/nlargest 0 [5 1 4 2 3])))
  (t/is (= [3] (heapq/nlargest 1 [3 1 2]))))

(t/deftest ^:kaocha/skip test-comparison-operator
  (let [data [3 1 4 1 5 9 2 6]
        heap (atom [])]
    (doseq [item data]
      (heapq/heappush! heap item))
    (let [results (loop [acc []]
                    (if (empty? @heap)
                      acc
                      (recur (conj acc (heapq/heappop! heap)))))]
      (t/is (= (sort data) results)))))

;; ---- TestErrorHandlingPython (= TestErrorHandling) ----

(t/deftest ^:kaocha/skip test-non-sequence
  (t/is (thrown? ExceptionInfo (heapq/heapify! (atom 10))))
  (t/is (thrown? ExceptionInfo (heapq/heappop! (atom 10))))
  (t/is (thrown? ExceptionInfo (heapq/heappush! (atom 10) 1)))
  (t/is (thrown? ExceptionInfo (heapq/heapreplace! (atom 10) 1)))
  (t/is (thrown? ExceptionInfo (heapq/nlargest 2 10)))
  (t/is (thrown? ExceptionInfo (heapq/nsmallest 2 10))))

(t/deftest ^:kaocha/skip test-len-only
  (t/is (thrown? ExceptionInfo (heapq/heapify! (atom {:len 10}))))
  (t/is (thrown? ExceptionInfo (heapq/heappop! (atom {:len 10}))))
  (t/is (thrown? ExceptionInfo (heapq/heappush! (atom {:len 10}) 1)))
  (t/is (thrown? ExceptionInfo (heapq/heapreplace! (atom {:len 10}) 1))))

(t/deftest ^:kaocha/skip test-cmp-err
  (t/is (thrown? ExceptionInfo
                 (let [heap (atom [1 2 3])]
                   (heapq/heappush! heap (reify Comparable
                                           (compareTo [_ _] (throw (ArithmeticException. "/")))))))))

(t/deftest ^:kaocha/skip test-arg-parsing
  (t/is (thrown? ExceptionInfo (heapq/heapify! 10)))
  (t/is (thrown? ExceptionInfo (heapq/heappop! 10)))
  (t/is (thrown? ExceptionInfo (heapq/heappush! 10 1)))
  (t/is (thrown? ExceptionInfo (heapq/heapreplace! 10 1)))
  (t/is (thrown? ExceptionInfo (heapq/nlargest 2 10)))
  (t/is (thrown? ExceptionInfo (heapq/nsmallest 2 10))))

(t/deftest ^:kaocha/skip test-iterable-args
  (t/is (= [1 2] (heapq/nsmallest 2 [3 1 2 4])))
  (t/is (= [4 3] (heapq/nlargest 2 [3 1 2 4])))
  (t/is (= [] (heapq/nsmallest 2 [])))
  (t/is (= [] (heapq/nlargest 2 []))))

(t/deftest ^:kaocha/skip test-heappush-mutating-heap
  (t/is (thrown? ExceptionInfo
                 (let [heap (atom (vec (range 10)))]
                   (heapq/heapify! heap)
                   (heapq/heappush! heap (reify Comparable
                                           (compareTo [_ _]
                                             (reset! heap [])
                                             (throw (RuntimeException. "heap modified")))))))))

(t/deftest ^:kaocha/skip test-heappop-mutating-heap
  (t/is (thrown? ExceptionInfo
                 (let [heap (atom (vec (range 10)))]
                   (heapq/heapify! heap)
                   (heapq/heappush! heap (reify Comparable
                                           (compareTo [_ _]
                                             (reset! heap [])
                                             (throw (RuntimeException. "heap modified")))))
                   (heapq/heappop! heap)))))

(t/deftest ^:kaocha/skip test-comparison-operator-modifying-heap
  (t/is (thrown? ExceptionInfo
                 (let [heap (atom [0])
                       evil (reify Comparable
                              (compareTo [_ _]
                                (reset! heap [])
                                (throw (RuntimeException. "heap modified"))))]
                   (heapq/heappushpop! heap evil)))))

(t/deftest ^:kaocha/skip test-comparison-operator-modifying-heap-two-heaps
  (t/is (thrown? ExceptionInfo
                 (let [heap1 (atom [0])
                       heap2 (atom [0])
                       evil1 (reify Comparable
                               (compareTo [_ _]
                                 (reset! heap2 [])
                                 (throw (RuntimeException. "heap modified"))))]
                   (heapq/heappush! heap1 evil1)))))
