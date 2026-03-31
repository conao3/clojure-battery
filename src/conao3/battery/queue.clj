(ns conao3.battery.queue
  (:import
   [java.util.concurrent
    LinkedBlockingQueue LinkedBlockingDeque PriorityBlockingQueue
    ArrayBlockingQueue TimeUnit]))

(def Full  (class (ex-info "Full" {:type :queue-full})))
(def Empty (class (ex-info "Empty" {:type :queue-empty})))

(defn- throw-full []
  (throw (ex-info "Full" {:type :queue-full})))

(defn- throw-empty []
  (throw (ex-info "Empty" {:type :queue-empty})))

(defrecord Queue [^LinkedBlockingQueue _q ^long maxsize])
(defrecord LifoQueue [^LinkedBlockingDeque _q ^long maxsize])
(defrecord PriorityQueue_ [^PriorityBlockingQueue _q ^long maxsize])

(defn make-queue
  ([] (make-queue 0))
  ([maxsize]
   (if (pos? maxsize)
     (->Queue (LinkedBlockingQueue. (int maxsize)) maxsize)
     (->Queue (LinkedBlockingQueue.) 0))))

(defn queue-put
  ([^Queue q item] (queue-put q item true nil))
  ([^Queue q item block] (queue-put q item block nil))
  ([^Queue q item block timeout]
   (let [jq ^LinkedBlockingQueue (:_q q)]
     (cond
       (not block) (when-not (.offer jq item)
                     (throw-full))
       timeout     (when-not (.offer jq item (long (* timeout 1000)) TimeUnit/MILLISECONDS)
                     (throw-full))
       :else       (.put jq item)))))

(defn queue-get
  ([^Queue q] (queue-get q true nil))
  ([^Queue q block] (queue-get q block nil))
  ([^Queue q block timeout]
   (let [jq ^LinkedBlockingQueue (:_q q)]
     (cond
       (not block) (let [v (.poll jq)]
                     (if (nil? v) (throw-empty) v))
       timeout     (let [v (.poll jq (long (* timeout 1000)) TimeUnit/MILLISECONDS)]
                     (if (nil? v) (throw-empty) v))
       :else       (.take jq)))))

(defn queue-get-nowait [^Queue q]
  (queue-get q false nil))

(defn queue-put-nowait [^Queue q item]
  (queue-put q item false nil))

(defn queue-qsize [^Queue q]
  (.size ^LinkedBlockingQueue (:_q q)))

(defn queue-empty? [^Queue q]
  (.isEmpty ^LinkedBlockingQueue (:_q q)))

(defn queue-full? [^Queue q]
  (if (pos? (:maxsize q))
    (>= (.size ^LinkedBlockingQueue (:_q q)) (:maxsize q))
    false))

(defn queue-task-done [^Queue q])
(defn queue-join [^Queue q])

(defn make-lifo-queue
  ([] (make-lifo-queue 0))
  ([maxsize]
   (->LifoQueue (LinkedBlockingDeque.) maxsize)))

(defn lifo-put
  ([^LifoQueue q item] (lifo-put q item true nil))
  ([^LifoQueue q item block] (lifo-put q item block nil))
  ([^LifoQueue q item block timeout]
   (let [jq ^LinkedBlockingDeque (:_q q)]
     (if (not block)
       (when-not (.offerLast jq item)
         (throw-full))
       (.putLast jq item)))))

(defn lifo-get
  ([^LifoQueue q] (lifo-get q true nil))
  ([^LifoQueue q block] (lifo-get q block nil))
  ([^LifoQueue q block timeout]
   (let [jq ^LinkedBlockingDeque (:_q q)]
     (cond
       (not block) (let [v (.pollLast jq)]
                     (if (nil? v) (throw-empty) v))
       timeout     (let [v (.pollLast jq (long (* timeout 1000)) TimeUnit/MILLISECONDS)]
                     (if (nil? v) (throw-empty) v))
       :else       (.takeLast jq)))))

(defn lifo-qsize [^LifoQueue q]
  (.size ^LinkedBlockingDeque (:_q q)))

(defn lifo-empty? [^LifoQueue q]
  (.isEmpty ^LinkedBlockingDeque (:_q q)))

(defn make-priority-queue
  ([] (make-priority-queue 0))
  ([maxsize]
   (->PriorityQueue_ (PriorityBlockingQueue. 11 compare) maxsize)))

(defn priority-put
  ([^PriorityQueue_ q item] (.put ^PriorityBlockingQueue (:_q q) item))
  ([^PriorityQueue_ q item block] (priority-put q item))
  ([^PriorityQueue_ q item block timeout] (priority-put q item)))

(defn priority-get
  ([^PriorityQueue_ q] (.take ^PriorityBlockingQueue (:_q q)))
  ([^PriorityQueue_ q block]
   (if (not block)
     (let [v (.poll ^PriorityBlockingQueue (:_q q))]
       (if (nil? v) (throw-empty) v))
     (.take ^PriorityBlockingQueue (:_q q))))
  ([^PriorityQueue_ q block timeout]
   (if (not block)
     (let [v (.poll ^PriorityBlockingQueue (:_q q))]
       (if (nil? v) (throw-empty) v))
     (let [v (.poll ^PriorityBlockingQueue (:_q q) (long (* timeout 1000)) TimeUnit/MILLISECONDS)]
       (if (nil? v) (throw-empty) v)))))

(defn priority-qsize [^PriorityQueue_ q]
  (.size ^PriorityBlockingQueue (:_q q)))

(defn priority-empty? [^PriorityQueue_ q]
  (.isEmpty ^PriorityBlockingQueue (:_q q)))

(defn priority-full? [^PriorityQueue_ q]
  (if (pos? (:maxsize q))
    (>= (.size ^PriorityBlockingQueue (:_q q)) (:maxsize q))
    false))

(defn simple-queue
  "A simple unbounded FIFO queue with put/get"
  []
  (make-queue 0))
