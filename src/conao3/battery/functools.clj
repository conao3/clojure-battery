(ns conao3.battery.functools)

(deftype CmpKey [val cmp-fn]
  Comparable
  (compareTo [_ other]
    (cmp-fn val (.val other))))

(defn partial [f & args]
  (apply clojure.core/partial f args))

(defn reduce
  ([f coll]
   (let [s (seq coll)]
     (when (nil? s)
       (throw (ex-info "reduce() of empty sequence with no initial value" {})))
     (clojure.core/reduce f s)))
  ([f coll init]
   (clojure.core/reduce f init coll)))

(defn cmp-to-key
  [cmp-fn]
  (fn [x]
    (->CmpKey x cmp-fn)))

(defn lru-cache
  ([f] (lru-cache nil f))
  ([maxsize f]
   (let [cache  (atom {})
         order  (atom [])
         hits   (atom 0)
         misses (atom 0)]
     (fn lru-fn [& args]
       (condp = args
         [:cache-info] {:hits @hits :misses @misses :maxsize maxsize :currsize (count @cache)}
         [:cache-clear] (do (reset! cache {}) (reset! order []) (reset! hits 0) (reset! misses 0) nil)
         [:wrapped]     f
         (let [k (vec args)]
           (if-let [[_ v] (find @cache k)]
             (do (swap! hits inc) v)
             (let [result (apply f args)]
               (swap! misses inc)
               (swap! cache assoc k result)
               (swap! order conj k)
               (when (and maxsize (> (count @cache) maxsize))
                 (let [evict (first @order)]
                   (swap! order (comp vec rest))
                   (swap! cache dissoc evict)))
               result))))))))

(defn cached-property [f]
  (let [cache (atom {})]
    (fn [obj]
      (let [id (System/identityHashCode obj)]
        (if-let [[_ v] (find @cache id)]
          v
          (let [result (f obj)]
            (swap! cache assoc id result)
            result))))))

(defn singledispatch [f]
  (let [registry (atom {Object f})]
    (fn dispatch [x & args]
      (let [cls (class x)
            impl (or (get @registry cls)
                     (some (fn [[k v]] (when (and (class? k) (instance? k x)) v)) @registry)
                     f)]
        (apply impl x args)))))

(defn total-ordering [cls]
  cls)
