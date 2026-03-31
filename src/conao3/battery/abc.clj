;; Original: Lib/abc.py

(ns conao3.battery.abc)

(defn abstractmethod
  "Marks a function var as abstract. Returns the function unchanged."
  [f]
  (with-meta f (assoc (meta f) :abstract true)))

(defn abstract?
  "Returns true if the var is marked as abstract."
  [f]
  (boolean (:abstract (meta f))))

(defn get-abstractmethods
  "Returns the set of abstract method names in the given protocol/map."
  [cls]
  (->> cls
       (filter (fn [[_ v]] (and (fn? v) (abstract? v))))
       (map first)
       set))

(defmacro defabstract
  "Defines an abstract function that throws NotImplementedError when called."
  [name & args]
  `(def ~name
     (abstractmethod
      (fn ~@args
        (throw (ex-info (str "abstract method " ~(str name) " not implemented")
                        {:method '~name}))))))

(defprotocol ABCMeta
  (register [cls subclass]
    "Register subclass as a virtual subclass of cls.")
  (subclasshook [cls subclass]
    "Override for issubclass() without inheriting from ABC.")
  (isabstractclass? [cls]
    "Returns true if cls has unimplemented abstract methods."))

(defn issubclass? [cls subclass]
  (isa? subclass cls))

(defn isinstance? [obj cls]
  (instance? cls obj))
