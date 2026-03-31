;; Original: Lib/test/test_threading.py

(ns conao3.battery.threading-test
  (:require
   [clojure.test :as t]
   [conao3.battery.threading :as th-m]))

(t/deftest test-lock-basic
  (let [l (th-m/lock)]
    (t/is (true? (th-m/lock-acquire l)))
    (t/is (false? (th-m/lock-acquire l false)))
    (th-m/lock-release l)
    (t/is (true? (th-m/lock-acquire l)))
    (th-m/lock-release l)))

(t/deftest test-rlock-reentrant
  (let [l (th-m/rlock)]
    (th-m/rlock-acquire l)
    (th-m/rlock-acquire l)
    (th-m/rlock-release l)
    (th-m/rlock-release l)
    (t/is true)))

(t/deftest test-event-basic
  (let [e (th-m/event)]
    (t/is (false? (th-m/event-is-set? e)))
    (th-m/event-set! e)
    (t/is (true? (th-m/event-is-set? e)))
    (th-m/event-clear! e)
    (t/is (false? (th-m/event-is-set? e)))))

(t/deftest test-event-wait-already-set
  (let [e (th-m/event)]
    (th-m/event-set! e)
    (t/is (true? (th-m/event-wait e 0.1)))))

(t/deftest test-event-wait-timeout
  (let [e (th-m/event)]
    (t/is (false? (th-m/event-wait e 0.05)))))

(t/deftest test-semaphore-basic
  (let [s (th-m/semaphore 2)]
    (t/is (true? (th-m/semaphore-acquire s)))
    (t/is (true? (th-m/semaphore-acquire s)))
    (t/is (false? (th-m/semaphore-acquire s false)))
    (th-m/semaphore-release s)
    (t/is (true? (th-m/semaphore-acquire s false)))
    (th-m/semaphore-release s)
    (th-m/semaphore-release s)))

(t/deftest test-thread-basic
  (let [result (atom nil)
        t (th-m/make-thread #(reset! result 42))]
    (th-m/thread-start! t)
    (th-m/thread-join! t)
    (t/is (= 42 @result))))

(t/deftest test-thread-with-args
  (let [result (atom nil)
        t (th-m/make-thread #(reset! result (+ %1 %2)) [3 4])]
    (th-m/thread-start! t)
    (th-m/thread-join! t)
    (t/is (= 7 @result))))

(t/deftest test-thread-name
  (let [t (th-m/make-thread #(Thread/sleep 1) nil "mythread" false)]
    (t/is (= "mythread" (th-m/thread-name t)))))

(t/deftest test-thread-daemon
  (let [t1 (th-m/make-thread #(Thread/sleep 1) nil "d1" true)
        t2 (th-m/make-thread #(Thread/sleep 1) nil "d2" false)]
    (t/is (true? (th-m/thread-daemon? t1)))
    (t/is (false? (th-m/thread-daemon? t2)))))

(t/deftest test-thread-is-alive
  (let [started (th-m/event)
        done    (th-m/event)
        t (th-m/make-thread (fn []
                               (th-m/event-set! started)
                               (th-m/event-wait done)))]
    (th-m/thread-start! t)
    (th-m/event-wait started)
    (t/is (true? (th-m/thread-is-alive? t)))
    (th-m/event-set! done)
    (th-m/thread-join! t)
    (t/is (false? (th-m/thread-is-alive? t)))))

(t/deftest test-current-thread
  (let [t (th-m/current-thread)]
    (t/is (string? (th-m/thread-name t)))))

(t/deftest test-active-count
  (t/is (pos? (th-m/active-count))))

(t/deftest test-thread-concurrent
  (let [results (atom [])
        lock    (th-m/lock)
        threads (map #(th-m/make-thread
                        (fn []
                          (th-m/lock-acquire lock)
                          (swap! results conj %)
                          (th-m/lock-release lock)))
                     (range 5))]
    (doseq [t threads] (th-m/thread-start! t))
    (doseq [t threads] (th-m/thread-join! t))
    (t/is (= (sort @results) (range 5)))))
