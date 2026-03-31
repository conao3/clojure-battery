;; Original: Lib/test/test_queue.py

(ns conao3.battery.queue-test
  (:require
   [clojure.test :as t]
   [conao3.battery.queue :as q-m]))

(t/deftest test-queue-basic
  (let [q (q-m/make-queue)]
    (q-m/queue-put q 1)
    (q-m/queue-put q 2)
    (q-m/queue-put q 3)
    (t/is (= 3 (q-m/queue-qsize q)))
    (t/is (false? (q-m/queue-empty? q)))
    (t/is (= 1 (q-m/queue-get q)))
    (t/is (= 2 (q-m/queue-get q)))
    (t/is (= 1 (q-m/queue-qsize q)))))

(t/deftest test-queue-empty
  (let [q (q-m/make-queue)]
    (t/is (true? (q-m/queue-empty? q)))
    (t/is (= 0 (q-m/queue-qsize q)))))

(t/deftest test-queue-fifo-order
  (let [q (q-m/make-queue)]
    (doseq [i (range 5)] (q-m/queue-put q i))
    (t/is (= (range 5) (for [_ (range 5)] (q-m/queue-get q))))))

(t/deftest test-queue-maxsize
  (let [q (q-m/make-queue 2)]
    (q-m/queue-put q 1)
    (q-m/queue-put q 2)
    (t/is (true? (q-m/queue-full? q)))
    (t/is (thrown? clojure.lang.ExceptionInfo (q-m/queue-put q 3 false)))))

(t/deftest test-queue-get-empty-throws
  (let [q (q-m/make-queue)]
    (t/is (thrown? clojure.lang.ExceptionInfo (q-m/queue-get q false)))))

(t/deftest test-queue-get-timeout
  (let [q (q-m/make-queue)]
    (t/is (thrown? clojure.lang.ExceptionInfo (q-m/queue-get q true 0.01)))))

(t/deftest test-lifo-queue-basic
  (let [q (q-m/make-lifo-queue)]
    (q-m/lifo-put q "a")
    (q-m/lifo-put q "b")
    (q-m/lifo-put q "c")
    (t/is (= "c" (q-m/lifo-get q)))
    (t/is (= "b" (q-m/lifo-get q)))
    (t/is (= "a" (q-m/lifo-get q)))))

(t/deftest test-lifo-queue-empty
  (let [q (q-m/make-lifo-queue)]
    (t/is (true? (q-m/lifo-empty? q)))
    (t/is (thrown? clojure.lang.ExceptionInfo (q-m/lifo-get q false)))))

(t/deftest test-priority-queue-basic
  (let [q (q-m/make-priority-queue)]
    (q-m/priority-put q [3 "low"])
    (q-m/priority-put q [1 "high"])
    (q-m/priority-put q [2 "medium"])
    (t/is (= [1 "high"] (q-m/priority-get q)))
    (t/is (= [2 "medium"] (q-m/priority-get q)))
    (t/is (= [3 "low"] (q-m/priority-get q)))))

(t/deftest test-priority-queue-empty
  (let [q (q-m/make-priority-queue)]
    (t/is (true? (q-m/priority-empty? q)))
    (t/is (thrown? clojure.lang.ExceptionInfo (q-m/priority-get q false)))))

(t/deftest test-queue-thread-safe
  (let [q   (q-m/make-queue)
        n   50
        results (atom [])]
    (let [threads (map #(Thread. (fn [] (q-m/queue-put q %))) (range n))]
      (doseq [t threads] (.start t))
      (doseq [t threads] (.join t)))
    (t/is (= n (q-m/queue-qsize q)))))

(t/deftest test-queue-put-get-nowait
  (let [q (q-m/make-queue)]
    (q-m/queue-put-nowait q 42)
    (t/is (= 42 (q-m/queue-get-nowait q)))))

(t/deftest test-lifo-queue-size
  (let [q (q-m/make-lifo-queue)]
    (t/is (= 0 (q-m/lifo-qsize q)))
    (q-m/lifo-put q "a")
    (q-m/lifo-put q "b")
    (t/is (= 2 (q-m/lifo-qsize q)))))

(t/deftest test-priority-queue-multiple
  (let [q (q-m/make-priority-queue)]
    (doseq [n [5 1 3 2 4]]
      (q-m/priority-put q n))
    (t/is (= [1 2 3 4 5]
             (for [_ (range 5)] (q-m/priority-get q))))))

(t/deftest test-simple-queue
  (let [q (q-m/simple-queue)]
    (t/is (q-m/queue-empty? q))
    (q-m/queue-put q "item")
    (t/is (= "item" (q-m/queue-get q)))))

(t/deftest test-queue-task-done-no-op
  (let [q (q-m/make-queue)]
    (q-m/queue-put q 1)
    (q-m/queue-get q)
    (t/is (nil? (q-m/queue-task-done q)))))

(t/deftest test-queue-join-no-op
  (let [q (q-m/make-queue)]
    (t/is (nil? (q-m/queue-join q)))))

(t/deftest test-queue-full-no-maxsize
  (let [q (q-m/make-queue)]
    (doseq [i (range 100)] (q-m/queue-put q i))
    (t/is (false? (q-m/queue-full? q)))))

(t/deftest test-priority-full-no-maxsize
  (let [q (q-m/make-priority-queue)]
    (doseq [i (range 10)] (q-m/priority-put q i))
    (t/is (false? (q-m/priority-full? q)))))
