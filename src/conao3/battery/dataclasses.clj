(ns conao3.battery.dataclasses
  (:require [clojure.string :as str]))

(def dataclass-registry (atom #{}))

(defn register-dataclass! [cls]
  (swap! dataclass-registry conj cls))

(defmacro defdata
  [name field-syms]
  `(do
     (defrecord ~name ~field-syms)
     (conao3.battery.dataclasses/register-dataclass! ~name)
     '~name))

(defn is-dataclass?
  [obj]
  (boolean
   (when obj
     (contains? @dataclass-registry (if (class? obj) obj (class obj))))))

(defn fields
  [obj]
  (let [inst (if (class? obj) nil obj)]
    (when inst
      (vec (keys inst)))))

(defn asdict
  [obj]
  (into {} obj))

(defn astuple
  [obj]
  (vec (vals obj)))

(defn replace
  [obj changes]
  (merge obj changes))

(defn field
  [& {:keys [default default-factory]}]
  {:default default
   :default-factory default-factory})
