(ns conao3.battery.random
  (:import [java.util Random]
           [java.security SecureRandom]))

(defn- make-rng
  ([] (Random.))
  ([seed] (Random. (long seed))))

(def ^:private _rng (atom (make-rng)))

(defn seed
  ([] (reset! _rng (make-rng)))
  ([s] (reset! _rng (make-rng s))))

(defn random []
  (.nextDouble ^Random @_rng))

(defn getrandbits [k]
  (let [^Random r @_rng]
    (loop [n k result 0]
      (if (<= n 0)
        result
        (let [bits (min n 32)
              chunk (if (< bits 32)
                      (bit-and (.nextInt r) (dec (bit-shift-left 1 bits)))
                      (bit-and (long (.nextInt r)) 0xFFFFFFFF))]
          (recur (- n bits)
                 (bit-or (bit-shift-left result bits) chunk)))))))

(defn randrange
  ([stop] (randrange 0 stop 1))
  ([start stop] (randrange start stop 1))
  ([start stop step]
   (let [width (- stop start)]
     (when (or (<= width 0) (= step 0))
       (throw (ex-info "empty range for randrange" {:start start :stop stop :step step})))
     (if (= step 1)
       (+ start (.nextInt ^Random @_rng width))
       (let [n (int (Math/ceil (/ width (double step))))]
         (+ start (* step (.nextInt ^Random @_rng n))))))))

(defn randint [a b]
  (+ a (.nextInt ^Random @_rng (inc (- b a)))))

(defn choice [seq]
  (let [v (vec seq)
        n (count v)]
    (when (zero? n)
      (throw (ex-info "Cannot choose from an empty sequence" {})))
    (nth v (.nextInt ^Random @_rng n))))

(defn shuffle [lst]
  (let [v (java.util.ArrayList. ^java.util.Collection lst)]
    (java.util.Collections/shuffle v ^Random @_rng)
    (vec v)))

(defn sample [population k]
  (let [v (vec population)
        n (count v)]
    (when (> k n)
      (throw (ex-info "Sample larger than population" {:k k :n n})))
    (let [indices (java.util.ArrayList. (range n))]
      (java.util.Collections/shuffle indices ^Random @_rng)
      (mapv #(nth v %) (take k indices)))))

(defn uniform [a b]
  (+ a (* (.nextDouble ^Random @_rng) (- b a))))

(defn gauss [mu sigma]
  (+ mu (* sigma (.nextGaussian ^Random @_rng))))

(defn triangular
  ([low high] (triangular low high (/ (+ low high) 2.0)))
  ([low high mode]
   (let [u (.nextDouble ^Random @_rng)
         c (/ (- mode low) (- high low))]
     (if (< u c)
       (+ low (* (Math/sqrt (* u (- high low) (- mode low)))))
       (- high (* (Math/sqrt (* (- 1 u) (- high low) (- high mode)))))))))

(defn getstate []
  (.getSeed ^Random (make-rng 0)))

(defn setstate [state]
  (reset! _rng (make-rng state)))

(defn make-random
  ([] (make-rng))
  ([seed] (make-rng seed)))

(defn random-with [rng]
  (.nextDouble ^java.util.Random rng))

(defn randint-with [rng a b]
  (+ a (.nextInt ^java.util.Random rng (inc (- b a)))))

(defn choice-with [rng coll]
  (let [v (vec coll)
        n (count v)]
    (when (zero? n)
      (throw (ex-info "Cannot choose from an empty sequence" {})))
    (nth v (.nextInt ^java.util.Random rng n))))

(defn shuffle-with [rng lst]
  (let [v (java.util.ArrayList. ^java.util.Collection (vec lst))]
    (java.util.Collections/shuffle v ^java.util.Random rng)
    (vec v)))

(defn sample-with [rng population k]
  (let [v (vec population)
        n (count v)]
    (when (> k n)
      (throw (ex-info "Sample larger than population" {:k k :n n})))
    (let [indices (java.util.ArrayList. (range n))]
      (java.util.Collections/shuffle indices ^java.util.Random rng)
      (mapv #(nth v %) (take k indices)))))
