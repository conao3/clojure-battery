(ns conao3.battery.collections)

(defn chain-map
  [& maps]
  (apply merge {} (reverse maps)))

(defn counter
  [coll]
  (frequencies coll))

(defn counter-total
  [c]
  (reduce + (vals c)))

(defn counter-elements
  [c]
  (mapcat (fn [[k v]] (when (pos? v) (repeat v k))) c))

(defn counter-most-common
  ([c] (sort-by (comp - second) c))
  ([c n] (take n (sort-by (comp - second) c))))

(defn counter-subtract
  [c1 c2]
  (reduce (fn [acc [k v]] (assoc acc k (- (get acc k 0) v)))
          (into {} c1)
          c2))

(defn counter-pos
  [c]
  (into {} (for [[k v] c :when (pos? v)] [k v])))

(defn counter-neg
  [c]
  (into {} (for [[k v] c :when (neg? v)] [k (- v)])))
