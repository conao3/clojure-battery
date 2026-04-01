;; Original: Lib/builtins.py (built-in module)

(ns conao3.battery.builtins)

(defn abs [x]
  (Math/abs (double x)))

(defn all [iterable]
  (every? identity iterable))

(defn any [iterable]
  (boolean (some identity iterable)))

(defn bin [x]
  (str "0b" (Long/toBinaryString (long x))))

(defn bool [x]
  (boolean x))

(defn callable? [obj]
  (fn? obj))

(defn chr [i]
  (char i))

(defn divmod [a b]
  (if (and (integer? a) (integer? b))
    [(Math/floorDiv (long a) (long b)) (Math/floorMod (long a) (long b))]
    (let [q (Math/floor (/ (double a) (double b)))]
      [q (- a (* q b))])))

(defn enumerate
  ([iterable] (enumerate iterable 0))
  ([iterable start]
   (map-indexed (fn [i x] [(+ i start) x]) iterable)))

(defn filter [pred iterable]
  (clojure.core/filter (if (nil? pred) identity pred) iterable))

(defn format [value fmt-spec]
  (if (empty? fmt-spec)
    (str value)
    (java.lang.String/format (str "%" fmt-spec) (into-array Object [value]))))

(defn getattr
  ([obj name] (getattr obj name ::missing))
  ([obj name default]
   (let [v (if (map? obj)
              (get obj name ::missing)
              ::missing)]
     (if (= v ::missing)
       (if (= default ::missing)
         (throw (ex-info (str "Attribute not found: " name) {:attr name}))
         default)
       v))))

(defn hasattr [obj name]
  (and (map? obj) (contains? obj name)))

(defn hash [x]
  (clojure.core/hash x))

(defn hex [x]
  (str "0x" (Long/toHexString (long x))))

(defn id [x]
  (System/identityHashCode x))

(defn input
  ([] (read-line))
  ([prompt]
   (print prompt)
   (flush)
   (read-line)))

(defn isinstance [obj cls]
  (instance? cls obj))

(defn issubclass [cls parent]
  (isa? cls parent))

(defn iter
  ([iterable] (clojure.core/seq iterable))
  ([callable sentinel]
   (take-while #(not= % sentinel) (repeatedly callable))))

(defn len [x]
  (cond
    (string? x) (.length ^String x)
    (counted? x) (count x)
    (instance? java.util.Collection x) (.size ^java.util.Collection x)
    :else (throw (ex-info (str "object has no len(): " (type x)) {}))))

(defn map [f & iterables]
  (apply clojure.core/map f iterables))

(defn max
  ([iterable] (clojure.core/reduce (fn [a b] (if (pos? (compare a b)) a b)) iterable))
  ([iterable & args]
   (if (keyword? (first args))
     ;; keyword opts: treat iterable as the collection
     (let [{:keys [key]} (apply hash-map args)]
       (if key
         (clojure.core/reduce (fn [a b] (if (pos? (compare (key a) (key b))) a b)) iterable)
         (clojure.core/reduce (fn [a b] (if (pos? (compare a b)) a b)) iterable)))
     ;; multiple positional args
     (let [all-args (cons iterable args)]
       (clojure.core/reduce (fn [a b] (if (pos? (compare a b)) a b)) all-args)))))

(defn min
  ([iterable] (clojure.core/reduce (fn [a b] (if (neg? (compare a b)) a b)) iterable))
  ([iterable & args]
   (if (keyword? (first args))
     (let [{:keys [key]} (apply hash-map args)]
       (if key
         (clojure.core/reduce (fn [a b] (if (neg? (compare (key a) (key b))) a b)) iterable)
         (clojure.core/reduce (fn [a b] (if (neg? (compare a b)) a b)) iterable)))
     (let [all-args (cons iterable args)]
       (clojure.core/reduce (fn [a b] (if (neg? (compare a b)) a b)) all-args)))))

(defn next
  ([it] (first it))
  ([it default]
   (if (seq it) (first it) default)))

(defn oct [x]
  (str "0o" (Long/toOctalString (long x))))

(defn ord [c]
  (int c))

(defn pow
  ([base exp] (Math/pow (double base) (double exp)))
  ([base exp mod] (clojure.core/mod (long (Math/pow (double base) (double exp))) (long mod))))

(defn range
  ([stop] (clojure.core/range stop))
  ([start stop] (clojure.core/range start stop))
  ([start stop step] (clojure.core/range start stop step)))

(defn repr [x]
  (cond
    (string? x) (str "'" x "'")
    (nil? x) "None"
    (true? x) "True"
    (false? x) "False"
    :else (str x)))

(defn reversed [iterable]
  (clojure.core/reverse iterable))

(defn round
  ([x] (Math/round (double x)))
  ([x ndigits]
   (let [factor (Math/pow 10.0 ndigits)]
     (/ (Math/round (* (double x) factor)) factor))))

(defn setattr [obj name value]
  (if (map? obj)
    (assoc obj name value)
    (throw (ex-info "setattr not supported on this type" {:type (type obj)}))))

(defn sorted
  ([iterable] (clojure.core/sort iterable))
  ([iterable {:keys [key reverse]}]
   (let [keyed (if key (clojure.core/sort-by key iterable) (clojure.core/sort iterable))]
     (if reverse (clojure.core/reverse keyed) keyed))))

(defn sum
  ([iterable] (clojure.core/reduce + 0 iterable))
  ([iterable start] (clojure.core/reduce + start iterable)))

(defn vars
  ([] (into {} (clojure.core/ns-publics *ns*)))
  ([obj] (if (map? obj) obj (into {} (clojure.core/ns-publics (find-ns (symbol (str obj))))))))

(defn zip [& iterables]
  (apply clojure.core/map vector iterables))
