(ns conao3.battery.filecmp)

(def default-ignores #{".hg"})

(def _cache (atom {}))

(defn cmp
  ([x y]
   (cmp x y true))
  ([x y _shallow]
   (throw (ex-info "Not implemented" {}))))

(defn clear-cache
  []
  (reset! _cache {}))

(defn cmpfiles
  [x y names & _options]
  (throw (ex-info "Not implemented" {})))

(defn dircmp
  [x y & _options]
  (throw (ex-info "Not implemented" {})))
