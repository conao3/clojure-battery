;; Original: Lib/operator.py

(ns conao3.battery.operator)

(defn add [a b] (+ a b))
(defn sub [a b] (- a b))
(defn mul [a b] (* a b))
(defn truediv [a b] (/ (double a) (double b)))
(defn floordiv [a b] (Math/floorDiv (long a) (long b)))
(defn mod [a b] (clojure.core/mod a b))
(defn pow [a b] (Math/pow (double a) (double b)))
(defn neg [a] (- a))
(defn pos [a] (+ a))
(defn abs [a] (Math/abs (double a)))
(defn inv [a] (bit-not (long a)))
(defn invert [a] (bit-not (long a)))

(defn lshift [a b] (bit-shift-left (long a) (long b)))
(defn rshift [a b] (bit-shift-right (long a) (long b)))
(defn and_ [a b] (bit-and (long a) (long b)))
(defn or_ [a b] (bit-or (long a) (long b)))
(defn xor [a b] (bit-xor (long a) (long b)))

(defn lt [a b] (< a b))
(defn le [a b] (<= a b))
(defn eq [a b] (= a b))
(defn ne [a b] (not= a b))
(defn ge [a b] (>= a b))
(defn gt [a b] (> a b))

(defn not_ [a] (not a))
(defn truth [a] (boolean a))
(defn is_ [a b] (identical? a b))
(defn is-not [a b] (not (identical? a b)))

(defn contains [a b]
  (cond
    (string? a) (.contains ^String a (str b))
    (map? a) (contains? a b)
    (set? a) (contains? a b)
    (sequential? a) (boolean (some #(= % b) a))
    :else (throw (ex-info (str "cannot check containment for " (type a)) {}))))

(defn getitem [a b]
  (cond
    (string? a) (.charAt ^String a (int b))
    (map? a) (let [v (get a b ::missing)]
               (if (= v ::missing)
                 (throw (ex-info (str "Key not found: " b) {:key b}))
                 v))
    (sequential? a) (nth a b)
    (instance? java.util.List a) (.get ^java.util.List a (int b))
    (instance? java.util.Map a) (let [v (.get ^java.util.Map a b)]
                                   (when (and (nil? v) (not (.containsKey ^java.util.Map a b)))
                                     (throw (ex-info (str "Key not found: " b) {:key b})))
                                   v)
    :else (throw (ex-info (str "cannot index " (type a)) {}))))

(defn setitem [a b c]
  (cond
    (instance? java.util.List a) (.set ^java.util.List a (int b) c)
    (instance? java.util.Map a) (.put ^java.util.Map a b c)
    :else (throw (ex-info (str "cannot set item on " (type a)) {}))))

(defn delitem [a b]
  (cond
    (instance? java.util.List a) (.remove ^java.util.List a (int b))
    (instance? java.util.Map a) (.remove ^java.util.Map a b)
    :else (throw (ex-info (str "cannot delete item from " (type a)) {}))))

(defn length-hint [a]
  (cond
    (nil? a) 0
    (string? a) (.length ^String a)
    (counted? a) (count a)
    (instance? java.util.Collection a) (.size ^java.util.Collection a)
    :else 0))

(defn concat [a b]
  (cond
    (string? a) (str a b)
    (vector? a) (into a b)
    (list? a) (apply list (clojure.core/concat a b))
    (sequential? a) (clojure.core/concat a b)
    :else (throw (ex-info (str "cannot concat " (type a)) {}))))

(defn itemgetter
  [& items]
  (if (= 1 (count items))
    (fn [obj] (getitem obj (first items)))
    (fn [obj] (mapv #(getitem obj %) items))))

(defn attrgetter
  [& attrs]
  (letfn [(get-attr [obj attr]
            (let [parts (clojure.string/split attr #"\.")
                  k (first parts)]
              (let [v (cond
                        (map? obj) (or (get obj k)
                                       (get obj (keyword k))
                                       (throw (ex-info (str "Attribute not found: " k) {:attr k})))
                        :else (throw (ex-info (str "Cannot get attribute on " (type obj)) {})))]
                (if (seq (rest parts))
                  (get-attr v (clojure.string/join "." (rest parts)))
                  v))))]
    (if (= 1 (count attrs))
      (fn [obj] (get-attr obj (first attrs)))
      (fn [obj] (mapv #(get-attr obj %) attrs)))))

(defn methodcaller
  [name & args]
  (fn [obj]
    (let [m (.. obj getClass (getMethod name (into-array Class (map type args))))]
      (.invoke m obj (into-array Object args)))))

(defn index [a]
  (if (integer? a) (long a)
      (throw (ex-info (str "'" (type a) "' object cannot be interpreted as an integer") {}))))

(defn indexOf [a b]
  (let [idx (first (keep-indexed #(when (= %2 b) %1) a))]
    (if (nil? idx)
      (throw (ex-info (str (pr-str b) " is not in sequence") {}))
      idx)))

(defn countOf [a b]
  (count (filter #(= % b) a)))

(defn iadd [a b] (add a b))
(defn isub [a b] (sub a b))
(defn imul [a b] (mul a b))
(defn itruediv [a b] (truediv a b))
(defn ifloordiv [a b] (floordiv a b))
(defn imod [a b] (mod a b))
(defn ipow [a b] (pow a b))
(defn ilshift [a b] (lshift a b))
(defn irshift [a b] (rshift a b))
(defn iand [a b] (and_ a b))
(defn ior [a b] (or_ a b))
(defn ixor [a b] (xor a b))
(defn iconcat [a b] (concat a b))
