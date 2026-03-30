(ns conao3.battery.itertools)

(defn accumulate
  ([coll] (if-let [s (seq coll)] (reductions + s) []))
  ([coll func] (if-let [s (seq coll)] (reductions func s) []))
  ([coll func initial] (reductions func initial coll)))

(defn batched [coll n]
  (when (<= n 0)
    (throw (ex-info "n must be at least one" {})))
  (clojure.core/map vec (partition-all n coll)))

(defn chain [& colls]
  (apply concat colls))

(defn chain-from-iterable [colls]
  (apply concat colls))

(defn combinations [coll r]
  (when (neg? r)
    (throw (ex-info "r must be non-negative" {})))
  (let [pool (vec coll)
        n (clojure.core/count pool)]
    (cond
      (= r 0) [[]]
      (> r n) []
      :else
      (for [i (range n)
            rest-combo (combinations (subvec pool (inc i)) (dec r))]
        (vec (cons (nth pool i) rest-combo))))))

(defn combinations-with-replacement [coll r]
  (let [pool (vec coll)]
    (cond
      (= r 0) [[]]
      (empty? pool) []
      :else
      (for [i (range (clojure.core/count pool))
            rest-combo (combinations-with-replacement (subvec pool i) (dec r))]
        (vec (cons (nth pool i) rest-combo))))))

(defn permutations [coll r]
  (let [pool (vec coll)
        n (clojure.core/count pool)]
    (cond
      (> r n) []
      (= r 0) [[]]
      :else
      (for [i (range n)
            perm (permutations (vec (concat (subvec pool 0 i) (subvec pool (inc i)))) (dec r))]
        (vec (cons (nth pool i) perm))))))

(defn combinatorics [coll r]
  (combinations coll r))

(defn compress [data selectors]
  (->> (clojure.core/map vector data selectors)
       (clojure.core/filter second)
       (clojure.core/map first)))

(defn count
  ([] (iterate inc 0))
  ([start] (iterate inc start))
  ([start step]
   (if (zero? step)
     (clojure.core/repeat start)
     (iterate #(+ % step) start))))

(defn cycle [coll]
  (clojure.core/cycle coll))

(defn groupby
  ([coll] (groupby coll identity))
  ([coll keyfn]
   (->> coll
        (partition-by keyfn)
        (clojure.core/map (fn [group] [(keyfn (first group)) group])))))

(defn filter [pred coll]
  (clojure.core/filter pred coll))

(defn filterfalse [pred coll]
  (remove pred coll))

(defn zip [& colls]
  (if (empty? colls)
    []
    (apply clojure.core/map vector colls)))

(defn zip-longest [& args]
  (let [n (clojure.core/count args)
        has-fillvalue? (and (>= n 2) (= :fillvalue (nth args (- n 2))))
        [colls fillvalue] (if has-fillvalue?
                            [(take (- n 2) args) (last args)]
                            [args nil])]
    (doseq [c colls]
      (when-not (sequential? c)
        (throw (ex-info "zip-longest requires iterables" {}))))
    (if (empty? colls)
      []
      (letfn [(step [seqs]
                (when (some some? seqs)
                  (lazy-seq
                   (cons (mapv #(if (some? %) (first %) fillvalue) seqs)
                         (step (mapv #(when (some? %) (next %)) seqs))))))]
        (step (mapv seq colls))))))

(defn pairwise [coll]
  (clojure.core/map vector coll (rest coll)))

(defn product [& colls]
  (reduce (fn [acc coll]
            (for [a acc b coll]
              (conj a b)))
          [[]]
          colls))

(defn repeat
  ([elem] (clojure.core/repeat elem))
  ([elem n]
   (if (<= n 0)
     []
     (clojure.core/repeat n elem))))

(defn map [func & colls]
  (apply clojure.core/map func colls))

(defn starmap [func coll]
  (clojure.core/map #(apply func %) coll))

(defn islice
  ([coll stop]
   (if (nil? stop)
     coll
     (take stop coll)))
  ([coll start stop]
   (islice coll start stop 1))
  ([coll start stop step]
   (->> coll
        (drop start)
        (take (max 0 (- stop start)))
        (take-nth step))))

(defn takewhile [pred coll]
  (take-while pred coll))

(defn dropwhile [pred coll]
  (drop-while pred coll))

(defn tee
  ([coll] (tee coll 2))
  ([coll n]
   (let [s (seq coll)]
     (vec (clojure.core/repeat n s)))))
