;; Original: Lib/test/test_sched.py

(ns conao3.battery.sched-test
  (:require
   [clojure.test :as t]
   [conao3.battery.sched :as sched]))

(t/deftest test-enter
  (let [results (atom [])
        s (sched/scheduler)]
    (doseq [x [0.5 0.4 0.3 0.2 0.1]]
      (sched/enter! s x 1 #(swap! results conj x)))
    (sched/run! s)
    (t/is (= @results [0.1 0.2 0.3 0.4 0.5]))))

(t/deftest test-enterabs
  (let [results (atom [])
        s (sched/scheduler)]
    (doseq [x [0.05 0.04 0.03 0.02 0.01]]
      (sched/enterabs! s x 1 #(swap! results conj x)))
    (sched/run! s)
    (t/is (= @results [0.01 0.02 0.03 0.04 0.05]))))

(t/deftest test-priority
  (let [results (atom [])
        s (sched/scheduler)
        cases [[[1 2 3 4 5] [1 2 3 4 5]]
               [[5 4 3 2 1] [1 2 3 4 5]]
               [[2 5 3 1 4] [1 2 3 4 5]]
               [[1 2 3 2 1] [1 1 2 2 3]]]]
    (t/are [priorities expected]
           (do
             (reset! results [])
             (doseq [p priorities]
               (sched/enterabs! s 0.01 p #(swap! results conj p)))
             (sched/run! s)
             (= @results expected))
      [1 2 3 4 5] [1 2 3 4 5]
      [5 4 3 2 1] [1 2 3 4 5]
      [2 5 3 1 4] [1 2 3 4 5]
      [1 2 3 2 1] [1 1 2 2 3])))

(t/deftest test-cancel
  (let [results (atom [])
        s (sched/scheduler)
        now (/ (System/currentTimeMillis) 1000.0)
        ev1 (sched/enterabs! s (+ now 0.01) 1 #(swap! results conj 0.01))
        ev2 (sched/enterabs! s (+ now 0.02) 1 #(swap! results conj 0.02))
        ev3 (sched/enterabs! s (+ now 0.03) 1 #(swap! results conj 0.03))
        ev4 (sched/enterabs! s (+ now 0.04) 1 #(swap! results conj 0.04))
        ev5 (sched/enterabs! s (+ now 0.05) 1 #(swap! results conj 0.05))]
    (sched/cancel! s ev1)
    (sched/cancel! s ev5)
    (sched/run! s)
    (t/is (= @results [0.02 0.03 0.04]))))

(t/deftest test-cancel-correct-event
  (let [events (atom [])
        s (sched/scheduler)
        _ (sched/enterabs! s 1 1 #(swap! events conj "a"))
        b (sched/enterabs! s 1 1 #(swap! events conj "b"))
        _ (sched/enterabs! s 1 1 #(swap! events conj "c"))]
    (sched/cancel! s b)
    (sched/run! s)
    (t/is (= @events ["a" "c"]))))

(t/deftest test-empty
  (let [results (atom [])
        s (sched/scheduler)]
    (t/is (sched/sched-empty? s))
    (doseq [x [0.05 0.04 0.03 0.02 0.01]]
      (sched/enterabs! s x 1 #(swap! results conj x)))
    (t/is (not (sched/sched-empty? s)))
    (sched/run! s)
    (t/is (sched/sched-empty? s))))

(t/deftest test-queue
  (let [results (atom [])
        s (sched/scheduler)
        now (/ (System/currentTimeMillis) 1000.0)
        e5 (sched/enterabs! s (+ now 0.05) 1 #(swap! results conj 5))
        e1 (sched/enterabs! s (+ now 0.01) 1 #(swap! results conj 1))
        e2 (sched/enterabs! s (+ now 0.02) 1 #(swap! results conj 2))
        e4 (sched/enterabs! s (+ now 0.04) 1 #(swap! results conj 4))
        e3 (sched/enterabs! s (+ now 0.03) 1 #(swap! results conj 3))]
    (t/is (= (sched/sched-queue s) [e1 e2 e3 e4 e5]))))

(t/deftest test-args-kwargs
  (let [seq-results (atom [])
        s (sched/scheduler)
        now (/ (System/currentTimeMillis) 1000.0)]
    (sched/enterabs! s now 1 (fn [] (swap! seq-results conj [[] {}])))
    (sched/enterabs! s now 1 (fn [& args] (swap! seq-results conj [args {}])) [1 2])
    (sched/enterabs! s now 1 (fn [& args] (swap! seq-results conj [args {}])) [:a :b])
    (sched/enterabs! s now 1 (fn [& args] (swap! seq-results conj [args {:foo 3}])) [1 2])
    (sched/run! s)
    (t/is (= (set @seq-results)
             #{[[] {}] ['(1 2) {}] ['(:a :b) {}] ['(1 2) {:foo 3}]}))))

(t/deftest test-run-non-blocking
  (let [results (atom [])
        s (sched/scheduler)]
    (doseq [x [10 9 8 7 6]]
      (sched/enter! s x 1 #(swap! results conj x)))
    (sched/run! s false)
    (t/is (= @results []))))

(t/deftest test-scheduler-is-atom
  (let [s (sched/scheduler)]
    (t/is (instance? clojure.lang.Atom s))))

(t/deftest test-enter-returns-event
  (let [s (sched/scheduler)
        e (sched/enter! s 0.01 1 (fn []))]
    (t/is (map? e))
    (t/is (contains? e :time))
    (t/is (contains? e :priority))
    (t/is (contains? e :action))))

(t/deftest test-queue-after-cancel
  (let [s (sched/scheduler)
        now (/ (System/currentTimeMillis) 1000.0)
        e1 (sched/enterabs! s now 1 (fn []))
        e2 (sched/enterabs! s (+ now 0.01) 1 (fn []))]
    (t/is (= 2 (count (sched/sched-queue s))))
    (sched/cancel! s e1)
    (t/is (= 1 (count (sched/sched-queue s))))
    (t/is (= e2 (first (sched/sched-queue s))))
    (sched/cancel! s e2)))

(t/deftest test-enterabs-with-args
  (let [results (atom [])
        s (sched/scheduler)
        now (/ (System/currentTimeMillis) 1000.0)]
    (sched/enterabs! s now 1 (fn [a b] (swap! results conj [a b])) [10 20])
    (sched/run! s)
    (t/is (= [[10 20]] @results))))
