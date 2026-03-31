(ns conao3.battery.enum)

(def enum-registry (atom #{}))

(defrecord EnumMember [enum-name member-name value])

(defmethod print-method EnumMember [em w]
  (.write w (str "<" (:enum-name em) "." (:member-name em) ": " (:value em) ">")))

(defn register-enum! [cls]
  (swap! enum-registry conj cls))

(def ^:private auto-counter (atom 0))

(defn auto []
  (swap! auto-counter inc)
  @auto-counter)

(defmacro defenum
  [enum-name & pairs]
  (let [entries (partition 2 pairs)
        members (mapv (fn [[k v]]
                        [(keyword k)
                         `(->EnumMember ~(str enum-name) ~(str k) ~v)])
                      entries)]
    `(do
       (def ~enum-name
         ~(into {} members))
       (conao3.battery.enum/register-enum! ~enum-name)
       '~enum-name)))

(defn is-enum?
  [obj]
  (boolean (contains? @enum-registry obj)))

(defn enum-name
  [member]
  (:member-name member))

(defn value
  [member]
  (:value member))

(defn from-value
  [enum val]
  (some (fn [[_ m]] (when (= (:value m) val) m)) enum))

(defn members
  [enum]
  (vec (vals enum)))

(defn unique
  [enum]
  (let [vals (mapv :value (vals enum))]
    (when (not= (count vals) (count (set vals)))
      (throw (ex-info "Duplicate values found in enum" {:enum enum})))
    enum))
