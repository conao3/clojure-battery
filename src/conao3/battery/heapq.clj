(ns conao3.battery.heapq)

(defn- validate-heap [heap-atom]
  (when-not (instance? clojure.lang.IAtom heap-atom)
    (throw (ex-info "heap must be an atom" {})))
  (when-not (vector? @heap-atom)
    (throw (ex-info "heap must contain a vector" {}))))

(defn- safe-compare [a b]
  (try
    (compare a b)
    (catch Throwable e
      (throw (ex-info "comparison failed" {} e)))))

(defn- swap-at [v i j]
  (assoc v i (nth v j) j (nth v i)))

(defn- sift-up [v i]
  (loop [v v i i]
    (let [parent (quot (dec i) 2)]
      (if (and (pos? i) (neg? (safe-compare (nth v i) (nth v parent))))
        (recur (swap-at v i parent) parent)
        v))))

(defn- sift-down [v i n]
  (loop [v v i i]
    (let [left  (inc (* 2 i))
          right (+ 2 (* 2 i))
          smallest (cond
                     (and (< left n) (neg? (safe-compare (nth v left) (nth v i)))) left
                     :else i)
          smallest (cond
                     (and (< right n) (neg? (safe-compare (nth v right) (nth v smallest)))) right
                     :else smallest)]
      (if (= smallest i)
        v
        (recur (swap-at v i smallest) smallest)))))

(defn- sift-up-max [v i]
  (loop [v v i i]
    (let [parent (quot (dec i) 2)]
      (if (and (pos? i) (pos? (safe-compare (nth v i) (nth v parent))))
        (recur (swap-at v i parent) parent)
        v))))

(defn- sift-down-max [v i n]
  (loop [v v i i]
    (let [left    (inc (* 2 i))
          right   (+ 2 (* 2 i))
          largest (cond
                    (and (< left n) (pos? (safe-compare (nth v left) (nth v i)))) left
                    :else i)
          largest (cond
                    (and (< right n) (pos? (safe-compare (nth v right) (nth v largest)))) right
                    :else largest)]
      (if (= largest i)
        v
        (recur (swap-at v i largest) largest)))))

(defn heappush! [heap-atom item]
  (validate-heap heap-atom)
  (swap! heap-atom (fn [v]
    (let [v (conj v item)]
      (sift-up v (dec (count v)))))))

(defn heappop! [heap-atom]
  (validate-heap heap-atom)
  (let [v @heap-atom
        n (count v)]
    (when (zero? n)
      (throw (ex-info "heap is empty" {})))
    (let [top (nth v 0)]
      (if (= n 1)
        (reset! heap-atom [])
        (let [last-item (nth v (dec n))
              v (subvec v 0 (dec n))]
          (reset! heap-atom (sift-down (assoc v 0 last-item) 0 (dec n)))))
      top)))

(defn heapify! [heap-atom]
  (validate-heap heap-atom)
  (let [v @heap-atom
        n (count v)]
    (reset! heap-atom
            (reduce (fn [v i] (sift-down v i n))
                    v
                    (range (dec (quot n 2)) -1 -1)))))

(defn heapreplace! [heap-atom item]
  (validate-heap heap-atom)
  (let [v @heap-atom]
    (when (empty? v)
      (throw (ex-info "heap is empty" {})))
    (let [top (nth v 0)
          v (assoc v 0 item)]
      (reset! heap-atom (sift-down v 0 (count v)))
      top)))

(defn heappushpop! [heap-atom item]
  (validate-heap heap-atom)
  (let [v @heap-atom]
    (if (or (empty? v) (not (pos? (safe-compare item (nth v 0)))))
      item
      (let [top (nth v 0)
            v (assoc v 0 item)]
        (reset! heap-atom (sift-down v 0 (count v)))
        top))))

(defn heappush-max! [heap-atom item]
  (validate-heap heap-atom)
  (swap! heap-atom (fn [v]
    (let [v (conj v item)]
      (sift-up-max v (dec (count v)))))))

(defn heappop-max! [heap-atom]
  (validate-heap heap-atom)
  (let [v @heap-atom
        n (count v)]
    (when (zero? n)
      (throw (ex-info "heap is empty" {})))
    (let [top (nth v 0)]
      (if (= n 1)
        (reset! heap-atom [])
        (let [last-item (nth v (dec n))
              v (subvec v 0 (dec n))]
          (reset! heap-atom (sift-down-max (assoc v 0 last-item) 0 (dec n)))))
      top)))

(defn heapify-max! [heap-atom]
  (validate-heap heap-atom)
  (let [v @heap-atom
        n (count v)]
    (reset! heap-atom
            (reduce (fn [v i] (sift-down-max v i n))
                    v
                    (range (dec (quot n 2)) -1 -1)))))

(defn heapreplace-max! [heap-atom item]
  (validate-heap heap-atom)
  (let [v @heap-atom]
    (when (empty? v)
      (throw (ex-info "heap is empty" {})))
    (let [top (nth v 0)
          v (assoc v 0 item)]
      (reset! heap-atom (sift-down-max v 0 (count v)))
      top)))

(defn heappushpop-max! [heap-atom item]
  (validate-heap heap-atom)
  (let [v @heap-atom]
    (if (or (empty? v) (not (neg? (safe-compare item (nth v 0)))))
      item
      (let [top (nth v 0)
            v (assoc v 0 item)]
        (reset! heap-atom (sift-down-max v 0 (count v)))
        top))))

(defn nsmallest [n data]
  (when-not (sequential? data)
    (throw (ex-info "data must be iterable" {})))
  (vec (take n (sort data))))

(defn nlargest [n data]
  (when-not (sequential? data)
    (throw (ex-info "data must be iterable" {})))
  (vec (take n (sort > data))))

(defn heap-merge [& args]
  (let [n (count args)
        has-key? (and (>= n 2) (= :key (nth args (- n 2))))
        [seqs key-fn] (if has-key?
                        [(take (- n 2) args) (last args)]
                        [args identity])]
    (sort-by key-fn (apply concat seqs))))
