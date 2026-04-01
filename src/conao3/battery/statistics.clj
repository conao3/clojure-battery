(ns conao3.battery.statistics)

(defn- check-nonempty [data fname]
  (when (empty? data)
    (throw (ex-info (str fname " requires at least one data point") {}))))

(defn mean [data]
  (check-nonempty data "mean")
  (/ (reduce + data) (count data)))

(defn fmean
  ([data]
   (check-nonempty data "fmean")
   (/ (double (reduce + data)) (count data)))
  ([data weights]
   (check-nonempty data "fmean")
   (when (not= (count data) (count weights))
     (throw (ex-info "weights must be same length as data" {})))
   (/ (double (reduce + (map * data weights)))
      (double (reduce + weights)))))

(defn geometric-mean [data]
  (check-nonempty data "geometric-mean")
  (when (some neg? data)
    (throw (ex-info "geometric-mean requires non-negative data" {})))
  (Math/exp (/ (double (reduce + (map #(Math/log (double %)) data)))
               (count data))))

(defn harmonic-mean
  ([data]
   (check-nonempty data "harmonic-mean")
   (when (some neg? data)
     (throw (ex-info "harmonic-mean requires non-negative data" {})))
   ;; If any value is 0, harmonic mean is 0 (Python behavior)
   (if (some zero? data)
     0
     (/ (count data) (reduce + (map #(/ 1 %) data)))))
  ([data weights]
   (check-nonempty data "harmonic-mean")
   (when (some neg? data)
     (throw (ex-info "harmonic-mean requires non-negative data" {})))
   (if (some zero? data)
     0
     (/ (reduce + weights)
        (reduce + (map (fn [w x] (/ w x)) weights data))))))

(defn median [data]
  (check-nonempty data "median")
  (let [sorted (sort data)
        n (count sorted)
        mid (quot n 2)]
    (if (odd? n)
      (nth sorted mid)
      (/ (+ (nth sorted (dec mid)) (nth sorted mid)) 2))))

(defn median-low [data]
  (check-nonempty data "median-low")
  (let [sorted (sort data)
        n (count sorted)
        mid (quot n 2)]
    (if (odd? n)
      (nth sorted mid)
      (nth sorted (dec mid)))))

(defn median-high [data]
  (check-nonempty data "median-high")
  (let [sorted (sort data)
        n (count sorted)
        mid (quot n 2)]
    (nth sorted mid)))

(defn median-grouped
  ([data] (median-grouped data 1))
  ([data interval]
   (check-nonempty data "median-grouped")
   (let [sorted (vec (sort data))
         n (count sorted)]
     (if (= n 1)
       (double (first sorted))
       (let [x (nth sorted (quot n 2))
             l (- (double x) (/ interval 2.0))
             cf (count (filter #(< % x) sorted))
             f (count (filter #(= % x) sorted))]
         (+ l (* interval (/ (- (/ n 2.0) cf) f))))))))

(defn mode [data]
  (check-nonempty data "mode")
  (let [freqs (reduce (fn [acc x] (update acc x (fnil inc 0))) {} data)
        max-count (apply max (vals freqs))]
    (->> data
         (distinct)
         (filter #(= (freqs %) max-count))
         first)))

(defn multimode [data]
  (if (empty? data)
    []
    (let [freqs (reduce (fn [acc x] (update acc x (fnil inc 0))) {} data)
          max-count (apply max (vals freqs))]
      (->> data
           (distinct)
           (filter #(= (freqs %) max-count))
           vec))))

(defn pvariance
  ([data] (pvariance data (mean data)))
  ([data mu]
   (check-nonempty data "pvariance")
   (/ (reduce + (map #(let [d (- % mu)] (* d d)) data))
      (count data))))

(defn variance
  ([data] (variance data (mean data)))
  ([data xbar]
   (check-nonempty data "variance")
   (let [n (count data)]
     (when (< n 2)
       (throw (ex-info "variance requires at least two data points" {})))
     (/ (reduce + (map #(let [d (- % xbar)] (* d d)) data))
        (dec n)))))

(defn pstdev
  ([data] (Math/sqrt (double (pvariance data))))
  ([data mu] (Math/sqrt (double (pvariance data mu)))))

(defn stdev
  ([data] (Math/sqrt (double (variance data))))
  ([data xbar] (Math/sqrt (double (variance data xbar)))))

(defn quantiles
  ([data] (quantiles data {}))
  ([data opts]
   (let [n (get opts :n 4)
         method (get opts :method :exclusive)
         sorted (vec (sort data))
         ld (count sorted)]
     (when (< n 1)
       (throw (ex-info "n must be at least 1" {})))
     (when (< ld 2)
       (throw (ex-info "must have at least two data points" {})))
     (if (= method :inclusive)
       (let [m (dec ld)]
         (mapv (fn [i]
                 (let [q (quot (* i m) n)
                       r (rem (* i m) n)]
                   (/ (double (+ (* (nth sorted q) (- n r))
                                 (* (nth sorted (min (inc q) (dec ld))) r)))
                      n)))
               (range 1 n)))
       (let [m (inc ld)]
         (mapv (fn [i]
                 (let [j (max 1 (min (quot (* i m) n) (dec ld)))
                       delta (- (* i m) (* j n))]
                   (/ (double (+ (* (nth sorted (dec j)) (- n delta))
                                 (* (nth sorted j) delta)))
                      n)))
               (range 1 n)))))))

(defn covariance [x y]
  (let [n (count x)]
    (when (< n 2)
      (throw (ex-info "covariance requires at least two data points" {})))
    (let [mx (mean x)
          my (mean y)]
      (/ (double (reduce + (map (fn [xi yi] (* (- xi mx) (- yi my))) x y)))
         (dec n)))))

(defn correlation
  ([x y]
   (let [sx (stdev x)
         sy (stdev y)]
     (when (or (zero? sx) (zero? sy))
       (throw (ex-info "correlation requires non-constant data" {})))
     (/ (covariance x y) (* sx sy))))
  ([x y _method]
   (correlation x y)))

(defn linear-regression
  ([x y]
   (let [slope (/ (covariance x y) (variance x))
         intercept (- (mean y) (* slope (mean x)))]
     {:slope (double slope) :intercept (double intercept)}))
  ([x y _proportional]
   (linear-regression x y)))
