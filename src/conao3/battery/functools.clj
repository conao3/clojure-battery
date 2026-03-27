(ns conao3.battery.functools)

(defn partial
  [f & args]
  (throw (ex-info "Not implemented" {})))

(defn reduce
  ([f coll] (throw (ex-info "Not implemented" {})))
  ([f coll init] (throw (ex-info "Not implemented" {}))))

(defn cmp-to-key
  ([f] (throw (ex-info "Not implemented" {}))))

(defn lru-cache
  ([f] (throw (ex-info "Not implemented" {})))
  ([maxsize f] (throw (ex-info "Not implemented" {}))))

(defn cached-property
  ([f] (throw (ex-info "Not implemented" {}))))

(defn singledispatch
  ([f] (throw (ex-info "Not implemented" {}))))

(defn total-ordering
  ([cls] (throw (ex-info "Not implemented" {}))))
