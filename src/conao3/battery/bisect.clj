(ns conao3.battery.bisect)

(defn- parse-opts [args]
  (if (or (empty? args) (keyword? (first args)))
    (apply hash-map args)
    (let [[lo hi & rest] args]
      (cond-> (if (seq rest) (apply hash-map rest) {})
        (some? lo) (assoc :lo lo)
        (some? hi) (assoc :hi hi)))))

(defn- validate-key [opts]
  (when (and (contains? opts :key) (not (fn? (:key opts))))
    (throw (ex-info "key must be a callable function" {:key (:key opts)}))))

(defn- validate-sequence! [a]
  (when-not (sequential? a)
    (throw (ex-info (str "a must be a sequence, got " (type a)) {:a a}))))

(defn bisect-left [a x & args]
  (validate-sequence! a)
  (let [opts (parse-opts args)
        _ (validate-key opts)
        lo (get opts :lo 0)
        hi (get opts :hi (count a))
        key-fn (get opts :key identity)]
    (when (neg? lo)
      (throw (ex-info "lo must be non-negative" {:lo lo})))
    (loop [lo lo hi hi]
      (if (< lo hi)
        (let [mid (+ lo (quot (- hi lo) 2))]
          (if (neg? (compare (key-fn (nth a mid)) (key-fn x)))
            (recur (inc mid) hi)
            (recur lo mid)))
        lo))))

(defn bisect-right [a x & args]
  (validate-sequence! a)
  (let [opts (parse-opts args)
        _ (validate-key opts)
        lo (get opts :lo 0)
        hi (get opts :hi (count a))
        key-fn (get opts :key identity)]
    (when (neg? lo)
      (throw (ex-info "lo must be non-negative" {:lo lo})))
    (loop [lo lo hi hi]
      (if (< lo hi)
        (let [mid (+ lo (quot (- hi lo) 2))]
          (if (pos? (compare (key-fn (nth a mid)) (key-fn x)))
            (recur lo mid)
            (recur (inc mid) hi)))
        lo))))

(defn insort-left [a x & args]
  (let [opts (parse-opts args)
        _ (validate-key opts)
        vec-a (if (instance? clojure.lang.Atom a) @a a)
        _ (validate-sequence! vec-a)
        i (apply bisect-left vec-a x args)]
    (when (instance? clojure.lang.Atom a)
      (swap! a (fn [v] (into (conj (subvec v 0 i) x) (subvec v i)))))))

(defn insort-right [a x & args]
  (let [opts (parse-opts args)
        _ (validate-key opts)
        vec-a (if (instance? clojure.lang.Atom a) @a a)
        _ (validate-sequence! vec-a)
        i (apply bisect-right vec-a x args)]
    (when (instance? clojure.lang.Atom a)
      (swap! a (fn [v] (into (conj (subvec v 0 i) x) (subvec v i)))))))

(def insort insort-right)
(def bisect bisect-right)
