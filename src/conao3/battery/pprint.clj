(ns conao3.battery.pprint
  (:require [clojure.string :as str]))

(defn- readable? [obj]
  (cond
    (nil? obj) true
    (boolean? obj) true
    (number? obj) true
    (string? obj) true
    (keyword? obj) true
    (symbol? obj) true
    (vector? obj) (every? readable? obj)
    (list? obj) (every? readable? obj)
    (map? obj) (and (every? readable? (keys obj)) (every? readable? (vals obj)))
    (set? obj) (every? readable? obj)
    :else false))

(defn isrecursive [_obj]
  false)

(defn isreadable [obj]
  (readable? obj))

(defn pformat [obj]
  (cond
    (nil? obj) "nil"
    (boolean? obj) (str obj)
    (number? obj) (str obj)
    (string? obj) (str "\"" (str/replace obj "\"" "\\\"") "\"")
    (keyword? obj) (str obj)
    (vector? obj) (str "[" (str/join " " (map pformat obj)) "]")
    (list? obj) (str "(" (str/join " " (map pformat obj)) ")")
    (map? obj) (if (empty? obj) "{}"
                   (str "{" (str/join " " (map #(str (pformat (key %)) " " (pformat (val %))) obj)) "}"))
    (set? obj) (str "#{" (str/join " " (map pformat obj)) "}")
    :else (throw (ex-info (str "Cannot pformat: " (type obj)) {}))))

(defn saferepr [obj]
  (try (pformat obj)
       (catch Exception _ (str obj))))

(defn pprint
  ([obj] (pprint obj *out*))
  ([obj stream]
   (binding [*out* stream]
     (println (pformat obj)))
   nil))
