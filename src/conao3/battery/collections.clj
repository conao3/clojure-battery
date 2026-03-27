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
  (->> c
       (filter (fn [[_ v]] (pos? v)))
       (mapcat (fn [[k v]] (repeat v k)))))

(defn counter-most-common
  ([c] (sort-by (comp - second) c))
  ([c n] (take n (sort-by (comp - second) c))))

(defn counter-subtract
  [c1 c2]
  (->> (into #{} (concat (keys c1) (keys c2)))
       (map (fn [k] [k (- (get c1 k 0) (get c2 k 0))]))
       (into {})))

(defn counter-pos
  [c]
  (->> c (filter (fn [[_ v]] (pos? v))) (into {})))

(defn counter-neg
  [c]
  (->> c
       (filter (fn [[_ v]] (neg? v)))
       (map (fn [[k v]] [k (- v)]))
       (into {})))
