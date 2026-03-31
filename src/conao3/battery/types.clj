;; Original: Lib/types.py

(ns conao3.battery.types)

(def FunctionType clojure.lang.IFn)
(def LambdaType clojure.lang.IFn)
(def MethodType java.lang.reflect.Method)
(def BuiltinFunctionType clojure.lang.IFn)
(def BuiltinMethodType clojure.lang.IFn)
(def GeneratorType java.lang.Iterable)
(def CoroutineType java.util.concurrent.Future)
(def ModuleType clojure.lang.Namespace)
(def NoneType nil)
(def BooleanType java.lang.Boolean)
(def IntType java.lang.Long)
(def FloatType java.lang.Double)
(def StringType java.lang.String)
(def BytesType (Class/forName "[B"))
(def ListType clojure.lang.IPersistentVector)
(def TupleType clojure.lang.IPersistentVector)
(def DictType clojure.lang.IPersistentMap)
(def SetType clojure.lang.IPersistentSet)

(defn new-type
  "Creates a new simple record type with given field names."
  [type-name fields]
  (let [constructor (fn [& args]
                      (zipmap (map keyword fields) args))]
    (with-meta constructor
               {:type-name type-name
                :fields (mapv keyword fields)})))

(defn simple-namespace
  "Creates a simple namespace object (a mutable map)."
  [& kwargs]
  (atom (apply hash-map kwargs)))

(defn simple-namespace-get
  [ns-atom key]
  (get @ns-atom key))

(defn simple-namespace-set
  [ns-atom key val]
  (swap! ns-atom assoc key val)
  ns-atom)

(defn simple-namespace-del
  [ns-atom key]
  (swap! ns-atom dissoc key)
  ns-atom)

(defn dynamic-class-attribute
  "Descriptor that allows attribute to be set on class and instance."
  [getter]
  {:getter getter
   :type :dynamic-class-attribute})

(defn mapped-attribute?
  "Returns true if obj is a mapped attribute descriptor."
  [obj]
  (and (map? obj)
       (= :dynamic-class-attribute (:type obj))))

(defn union-type
  "Creates a union type accepting any of the given types."
  [& types]
  {:types (set types) :type :union})

(defn union-type?
  "Returns true if obj is a union type."
  [obj]
  (and (map? obj) (= :union (:type obj))))

(defn union-type-check
  "Returns true if val is an instance of any type in the union."
  [union val]
  (some #(instance? % val) (:types union)))
