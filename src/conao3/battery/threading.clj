(ns conao3.battery.threading
  (:import
   [java.util.concurrent
    CountDownLatch Semaphore TimeUnit
    locks.ReentrantLock locks.ReentrantReadWriteLock]))

(defrecord Lock [^java.util.concurrent.Semaphore _sem])
(defrecord RLock [^ReentrantLock _lock])
(defrecord Event [_set])
(defrecord Semaphore_ [^java.util.concurrent.Semaphore _sem])
(defrecord BoundedSemaphore [^java.util.concurrent.Semaphore _sem ^long _max _count])
(defrecord Condition [^Object _lock _waiters])
(defrecord Barrier [^CountDownLatch _latch _n _count _phase])

(defn lock []
  (->Lock (java.util.concurrent.Semaphore. 1)))

(defn lock-acquire
  ([^Lock l] (lock-acquire l true nil))
  ([^Lock l blocking] (lock-acquire l blocking nil))
  ([^Lock l blocking timeout]
   (if (not blocking)
     (.tryAcquire ^java.util.concurrent.Semaphore (:_sem l))
     (if timeout
       (.tryAcquire ^java.util.concurrent.Semaphore (:_sem l) (long (* timeout 1000)) TimeUnit/MILLISECONDS)
       (do (.acquire ^java.util.concurrent.Semaphore (:_sem l)) true)))))

(defn lock-release [^Lock l]
  (.release ^java.util.concurrent.Semaphore (:_sem l)))

(defn lock-locked? [^Lock l]
  (zero? (.availablePermits ^java.util.concurrent.Semaphore (:_sem l))))

(defn rlock []
  (->RLock (ReentrantLock. true)))

(defn rlock-acquire
  ([^RLock l] (rlock-acquire l true nil))
  ([^RLock l blocking] (rlock-acquire l blocking nil))
  ([^RLock l blocking timeout]
   (if (not blocking)
     (.tryLock ^ReentrantLock (:_lock l))
     (if timeout
       (.tryLock ^ReentrantLock (:_lock l) (long (* timeout 1000)) TimeUnit/MILLISECONDS)
       (do (.lock ^ReentrantLock (:_lock l)) true)))))

(defn rlock-release [^RLock l]
  (.unlock ^ReentrantLock (:_lock l)))

(defn event []
  (->Event (atom false)))

(defn event-is-set? [^Event e]
  @(:_set e))

(defn event-set! [^Event e]
  (reset! (:_set e) true))

(defn event-clear! [^Event e]
  (reset! (:_set e) false))

(defn event-wait
  ([^Event e] (loop [] (when (not @(:_set e)) (Thread/sleep 1) (recur))))
  ([^Event e timeout]
   (let [deadline (+ (System/currentTimeMillis) (long (* timeout 1000)))]
     (loop []
       (cond
         @(:_set e) true
         (> (System/currentTimeMillis) deadline) false
         :else (do (Thread/sleep 1) (recur)))))))

(defn semaphore
  ([] (semaphore 1))
  ([value] (->Semaphore_ (java.util.concurrent.Semaphore. (int value)))))

(defn semaphore-acquire
  ([^Semaphore_ s] (semaphore-acquire s true nil))
  ([^Semaphore_ s blocking] (semaphore-acquire s blocking nil))
  ([^Semaphore_ s blocking timeout]
   (if (not blocking)
     (.tryAcquire ^java.util.concurrent.Semaphore (:_sem s))
     (if timeout
       (.tryAcquire ^java.util.concurrent.Semaphore (:_sem s) (long (* timeout 1000)) TimeUnit/MILLISECONDS)
       (do (.acquire ^java.util.concurrent.Semaphore (:_sem s)) true)))))

(defn semaphore-release [^Semaphore_ s]
  (.release ^java.util.concurrent.Semaphore (:_sem s)))

(defrecord Thread_ [^Thread _thread _name _daemon _target _args])

(defn make-thread
  ([] (make-thread nil nil nil nil))
  ([target] (make-thread target nil nil nil))
  ([target args] (make-thread target args nil nil))
  ([target args name daemon]
   (let [t-name  (or name (str "Thread-" (System/nanoTime)))
         t-daemon (boolean (or daemon false))
         run-fn  (fn []
                   (when target
                     (apply target (or args []))))
         t       (Thread. ^Runnable run-fn ^String t-name)]
     (.setDaemon t t-daemon)
     (->Thread_ t t-name t-daemon target (or args [])))))

(defn thread-start! [^Thread_ t]
  (.start ^Thread (:_thread t)))

(defn thread-join!
  ([^Thread_ t] (.join ^Thread (:_thread t)))
  ([^Thread_ t timeout] (.join ^Thread (:_thread t) (long (* timeout 1000)))))

(defn thread-is-alive? [^Thread_ t]
  (.isAlive ^Thread (:_thread t)))

(defn thread-name [^Thread_ t] (:_name t))

(defn thread-daemon? [^Thread_ t] (:_daemon t))

(defn current-thread []
  (let [t (Thread/currentThread)]
    (->Thread_ t (.getName t) (.isDaemon t) nil [])))

(defn active-count []
  (Thread/activeCount))

(defn enumerate []
  (let [group (.getThreadGroup (Thread/currentThread))
        arr   (make-array Thread (.activeCount group))]
    (.enumerate group arr)
    (mapv #(->Thread_ % (.getName %) (.isDaemon %) nil []) (remove nil? arr))))

(defn main-thread []
  (first (filter #(= "main" (thread-name %)) (enumerate))))
