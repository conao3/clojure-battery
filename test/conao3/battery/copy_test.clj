;; Original: Lib/test/test_copy.py

(ns conao3.battery.copy-test
  (:require
   [clojure.test :as t]
   [conao3.battery.copy :as copy-m])
  (:import
   [java.util Arrays ArrayList HashMap]))

(t/deftest test-copy-bytes
  (let [original (byte-array [1 2 3 4 5])
        copied   (copy-m/copy original)]
    (t/is (Arrays/equals ^bytes original ^bytes copied))
    (t/is (not (identical? original copied)))))

(t/deftest test-copy-map-shallow
  (let [original {:a [1 2 3] :b 4}
        copied   (copy-m/copy original)]
    (t/is (= original copied))
    (t/is (identical? (:a original) (:a copied)))))

(t/deftest test-copy-primitives
  (t/is (= 42 (copy-m/copy 42)))
  (t/is (= "hello" (copy-m/copy "hello")))
  (t/is (= true (copy-m/copy true)))
  (t/is (nil? (copy-m/copy nil))))

(t/deftest test-deepcopy-nested-map
  (let [original {:a [1 2 3] :b {:c 4}}
        copied   (copy-m/deepcopy original)]
    (t/is (= original copied))
    (t/is (not (identical? (:a original) (:a copied))))
    (t/is (not (identical? (:b original) (:b copied))))))

(t/deftest test-deepcopy-vector
  (let [original [[1 2] [3 4]]
        copied   (copy-m/deepcopy original)]
    (t/is (= original copied))
    (t/is (not (identical? (first original) (first copied))))))

(t/deftest test-deepcopy-bytes
  (let [original (byte-array [1 2 3])
        copied   (copy-m/deepcopy original)]
    (t/is (Arrays/equals ^bytes original ^bytes copied))
    (t/is (not (identical? original copied)))))

(t/deftest test-deepcopy-primitives
  (t/is (= 42 (copy-m/deepcopy 42)))
  (t/is (= "hello" (copy-m/deepcopy "hello")))
  (t/is (nil? (copy-m/deepcopy nil))))

(t/deftest test-deepcopy-set
  (let [original #{1 2 3}
        copied   (copy-m/deepcopy original)]
    (t/is (= original copied))))

(t/deftest test-deepcopy-list
  (let [original '(1 2 3)
        copied   (copy-m/deepcopy original)]
    (t/is (= original copied))))

(t/deftest test-deepcopy-java-list
  (let [original (doto (ArrayList.) (.add 1) (.add 2) (.add (ArrayList. [3 4])))
        copied   (copy-m/deepcopy original)]
    (t/is (= (.get original 0) (.get copied 0)))
    (t/is (= (.get original 1) (.get copied 1)))
    (t/is (not (identical? (.get original 2) (.get copied 2))))))

(t/deftest test-copy-java-list
  (let [original (doto (ArrayList.) (.add 1) (.add 2) (.add 3))
        copied   (copy-m/copy original)]
    (t/is (= original copied))
    (t/is (not (identical? original copied)))))

(t/deftest test-deepcopy-keyword
  (let [k :foo]
    (t/is (= k (copy-m/deepcopy k)))
    (t/is (identical? k (copy-m/deepcopy k)))))

(t/deftest test-deepcopy-symbol
  (let [s 'my-sym]
    (t/is (= s (copy-m/deepcopy s)))))

(t/deftest test-deepcopy-nested-vector
  (let [original [1 [2 [3 [4]]]]
        copied   (copy-m/deepcopy original)]
    (t/is (= original copied))
    (t/is (not (identical? (second original) (second copied))))
    (t/is (not (identical? (second (second original)) (second (second copied)))))))

(t/deftest test-copy-set
  (let [original #{1 2 3}
        copied   (copy-m/copy original)]
    (t/is (= original copied))))

(t/deftest test-deepcopy-mixed
  (let [original {:a [1 2 #{3 4}] :b '(5 6)}
        copied   (copy-m/deepcopy original)]
    (t/is (= original copied))
    (t/is (not (identical? (:a original) (:a copied))))))
