(ns conao3.battery.sched)

(defn scheduler
  ([]
   (scheduler #(/ (System/currentTimeMillis) 1000.0)
              #(Thread/sleep (long (* % 1000)))))
  ([timefunc delayfunc]
   (atom {:timefunc timefunc
          :delayfunc delayfunc
          :seq-counter 0
          :queue (sorted-set-by
                   (fn [a b]
                     (let [c (compare (:time a) (:time b))]
                       (if (zero? c)
                         (let [c2 (compare (:priority a) (:priority b))]
                           (if (zero? c2)
                             (compare (:seq a) (:seq b))
                             c2))
                         c))))})))

(defn enterabs!
  ([s time priority action]
   (enterabs! s time priority action [] {}))
  ([s time priority action args]
   (enterabs! s time priority action args {}))
  ([s time priority action args kwargs]
   (let [result (volatile! nil)]
     (swap! s (fn [state]
                (let [seq-n (:seq-counter state)
                      event {:time time :priority priority :action action
                             :args args :kwargs kwargs :seq seq-n}]
                  (vreset! result event)
                  (-> state
                      (update :seq-counter inc)
                      (update :queue conj event)))))
     @result)))

(defn enter!
  ([s delay priority action]
   (enter! s delay priority action [] {}))
  ([s delay priority action args]
   (enter! s delay priority action args {}))
  ([s delay priority action args kwargs]
   (let [now ((:timefunc @s))]
     (enterabs! s (+ now delay) priority action args kwargs))))

(defn cancel!
  [s event]
  (swap! s update :queue disj event))

(defn sched-empty?
  [s]
  (empty? (:queue @s)))

(defn sched-queue
  [s]
  (vec (:queue @s)))

(defn run!
  ([s] (run! s true))
  ([s blocking]
   (let [{:keys [timefunc delayfunc]} @s]
     (if blocking
       (loop []
         (when-let [event (first (:queue @s))]
           (let [{:keys [time action args kwargs]} event
                 now (timefunc)
                 delay (- time now)]
             (when (pos? delay)
               (delayfunc delay))
             (swap! s update :queue disj event)
             (if (seq args)
               (apply action args)
               (action))
             (recur))))
       (let [now (timefunc)]
         (loop []
           (when-let [event (first (:queue @s))]
             (when (<= (:time event) now)
               (swap! s update :queue disj event)
               (let [{:keys [action args]} event]
                 (if (seq args)
                   (apply action args)
                   (action)))
               (recur)))))))))
