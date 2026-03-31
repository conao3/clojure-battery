;; Original: Lib/test/test_random.py

(ns conao3.battery.random-test
  (:require
   [clojure.test :as t]
   [conao3.battery.random :as random-m])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-random-in-range
  (let [rng (random-m/make-random 42)]
    (dotimes [_ 100]
      (let [v (random-m/random-with rng)]
        (t/is (and (>= v 0.0) (< v 1.0)))))))

(t/deftest test-randint-in-range
  (let [rng (random-m/make-random 42)]
    (dotimes [_ 100]
      (let [v (random-m/randint-with rng 1 10)]
        (t/is (and (>= v 1) (<= v 10)))))))

(t/deftest test-randint-endpoints
  (let [rng (random-m/make-random 42)
        results (set (repeatedly 1000 #(random-m/randint-with rng 1 2)))]
    (t/is (contains? results 1))
    (t/is (contains? results 2))
    (t/is (not (contains? results 0)))
    (t/is (not (contains? results 3)))))

(t/deftest test-choice-from-seq
  (let [rng  (random-m/make-random 42)
        coll [10 20 30 40 50]]
    (dotimes [_ 100]
      (let [v (random-m/choice-with rng coll)]
        (t/is (contains? (set coll) v))))))

(t/deftest test-choice-empty-throws
  (let [rng (random-m/make-random 42)]
    (t/is (thrown? ExceptionInfo (random-m/choice-with rng [])))))

(t/deftest test-shuffle-permutation
  (let [rng  (random-m/make-random 42)
        orig [1 2 3 4 5]
        shuffled (random-m/shuffle-with rng orig)]
    (t/is (= (sort orig) (sort shuffled)))
    (t/is (= (count orig) (count shuffled)))))

(t/deftest test-sample-size
  (let [rng  (random-m/make-random 42)
        pop  (range 100)]
    (doseq [k [0 1 5 10 50 100]]
      (let [s (random-m/sample-with rng pop k)]
        (t/is (= k (count s)))
        (t/is (= k (count (set s))))))))

(t/deftest test-sample-too-large-throws
  (let [rng (random-m/make-random 42)]
    (t/is (thrown? ExceptionInfo (random-m/sample-with rng [1 2 3] 5)))))

(t/deftest test-seed-reproducibility
  (let [rng1 (random-m/make-random 42)
        rng2 (random-m/make-random 42)
        vals1 (repeatedly 10 #(random-m/random-with rng1))
        vals2 (repeatedly 10 #(random-m/random-with rng2))]
    (t/is (= vals1 vals2))))

(t/deftest test-uniform-in-range
  (let [rng (random-m/make-random 42)]
    (dotimes [_ 100]
      (let [v (.nextDouble ^java.util.Random rng)
            u (+ 5.0 (* v 5.0))]
        (t/is (and (>= u 5.0) (<= u 10.0)))))))

(t/deftest test-module-level-seed
  (random-m/seed 42)
  (let [v1 (random-m/random)]
    (random-m/seed 42)
    (let [v2 (random-m/random)]
      (t/is (= v1 v2)))))

(t/deftest test-module-level-randint
  (random-m/seed 0)
  (dotimes [_ 100]
    (let [v (random-m/randint 1 6)]
      (t/is (and (>= v 1) (<= v 6))))))

(t/deftest test-module-level-choice
  (random-m/seed 0)
  (dotimes [_ 50]
    (let [v (random-m/choice [1 2 3 4 5])]
      (t/is (contains? #{1 2 3 4 5} v)))))

(t/deftest test-randrange-basic
  (random-m/seed 0)
  (dotimes [_ 100]
    (let [v (random-m/randrange 10)]
      (t/is (and (>= v 0) (< v 10)))))
  (dotimes [_ 100]
    (let [v (random-m/randrange 5 15)]
      (t/is (and (>= v 5) (< v 15)))))
  (dotimes [_ 100]
    (let [v (random-m/randrange 0 100 5)]
      (t/is (zero? (mod v 5))))))

(t/deftest test-uniform-range
  (random-m/seed 42)
  (dotimes [_ 100]
    (let [v (random-m/uniform 1.5 3.5)]
      (t/is (and (>= v 1.5) (<= v 3.5))))))

(t/deftest test-gauss-type
  (random-m/seed 42)
  (let [v (random-m/gauss 0.0 1.0)]
    (t/is (float? v))))

(t/deftest test-getrandbits
  (random-m/seed 42)
  (let [v (random-m/getrandbits 8)]
    (t/is (and (>= v 0) (< v 256)))))

(t/deftest test-sample-with-rng
  (let [rng (random-m/make-random 42)
        result (random-m/sample-with rng [1 2 3 4 5] 3)]
    (t/is (= 3 (count result)))
    (t/is (every? #(contains? #{1 2 3 4 5} %) result))))

(t/deftest test-shuffle-with-rng
  (let [rng (random-m/make-random 42)
        original [1 2 3 4 5]
        shuffled (random-m/shuffle-with rng original)]
    (t/is (= (sort shuffled) original))
    (t/is (= 5 (count shuffled)))))

(t/deftest test-triangular-in-range
  (random-m/seed 42)
  (dotimes [_ 100]
    (let [v (random-m/triangular 1.0 10.0)]
      (t/is (and (>= v 1.0) (<= v 10.0))))))

(t/deftest test-triangular-with-mode
  (random-m/seed 42)
  (dotimes [_ 100]
    (let [v (random-m/triangular 0.0 1.0 0.3)]
      (t/is (and (>= v 0.0) (<= v 1.0))))))

(t/deftest test-setstate-and-getstate
  (random-m/seed 42)
  (let [v1 (random-m/random)
        _ (random-m/seed 42)
        v2 (random-m/random)]
    (t/is (= v1 v2))))
