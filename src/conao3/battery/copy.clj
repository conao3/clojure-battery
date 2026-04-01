(ns conao3.battery.copy)

(declare deepcopy)

(defn copy [x]
  (cond
    (nil? x)     nil
    (bytes? x)   (let [result (byte-array (alength ^bytes x))]
                   (System/arraycopy x 0 result 0 (alength ^bytes x))
                   result)
    (instance? java.lang.Cloneable x)
    (let [m (.getDeclaredMethod (class x) "clone" (into-array Class []))]
      (.setAccessible m true)
      (.invoke m x (into-array Object [])))
    (map? x)     x
    (set? x)     x
    (vector? x)  x
    (list? x)    x
    (seq? x)     x
    (instance? java.util.List x)
    (java.util.ArrayList. ^java.util.Collection (vec x))
    (instance? java.util.Map x)
    (java.util.HashMap. ^java.util.Map x)
    (instance? java.util.Set x)
    (java.util.HashSet. ^java.util.Collection (vec x))
    :else x))

(defn deepcopy
  ([x] (deepcopy x {}))
  ([x _memo]
   (cond
     (nil? x)     nil
     (boolean? x) x
     (number? x)  x
     (string? x)  x
     (keyword? x) x
     (symbol? x)  x
     (bytes? x)   (let [result (byte-array (alength ^bytes x))]
                    (System/arraycopy x 0 result 0 (alength ^bytes x))
                    result)
     (map? x)     (into {} (map (fn [[k v]] [(deepcopy k) (deepcopy v)]) x))
     (set? x)     (into #{} (map deepcopy x))
     (vector? x)  (into [] (map deepcopy x))
     (list? x)    (apply list (map deepcopy x))
     (seq? x)     (map deepcopy x)
     (instance? java.util.List x)
     (java.util.ArrayList. ^java.util.Collection (mapv deepcopy x))
     (instance? java.util.Map x)
     (reduce (fn [^java.util.HashMap m [k v]]
               (doto m (.put (deepcopy k) (deepcopy v))))
             (java.util.HashMap.)
             x)
     (instance? java.util.Set x)
     (java.util.HashSet. ^java.util.Collection (mapv deepcopy x))
     :else x)))
