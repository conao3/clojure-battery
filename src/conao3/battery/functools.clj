(ns conao3.battery.functools)

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

(defn cmp-to-key [f]
  (throw (ex-info "Not implemented" {})))

(defn lru-cache
  ([f] (throw (ex-info "Not implemented" {})))
  ([maxsize f] (throw (ex-info "Not implemented" {}))))

(defn cached-property [f]
  (throw (ex-info "Not implemented" {})))

(defn singledispatch [f]
  (throw (ex-info "Not implemented" {})))

(defn total-ordering [cls]
  (throw (ex-info "Not implemented" {})))
