;; Original: Lib/inspect.py

(ns conao3.battery.inspect
  (:require
   [clojure.string :as str]))

(defn isfunction?
  "Returns true if obj is a Clojure function (fn)."
  [obj]
  (fn? obj))

(defn isbuiltin?
  "Returns true if obj is a built-in function (Java method or Clojure fn)."
  [obj]
  (or (fn? obj)
      (instance? clojure.lang.MultiFn obj)))

(defn ismethod?
  "Returns true if obj is a method."
  [obj]
  (instance? java.lang.reflect.Method obj))

(defn isclass?
  "Returns true if obj is a class."
  [obj]
  (class? obj))

(defn ismodule?
  "Returns true if obj is a namespace."
  [obj]
  (instance? clojure.lang.Namespace obj))

(defn getmembers
  "Returns all members of obj as a map of name → value."
  ([obj] (getmembers obj nil))
  ([obj pred]
   (->> (ns-publics (if (instance? clojure.lang.Namespace obj)
                      obj
                      (find-ns (symbol (str obj)))))
        (map (fn [[k v]] [(str k) @v]))
        (filter (fn [[_ v]] (or (nil? pred) (pred v))))
        (sort-by first)
        vec)))

(defn getdoc
  "Returns the docstring of a var, fn, or namespace."
  [obj]
  (cond
    (var? obj) (:doc (meta obj))
    (fn? obj) (:doc (meta obj))
    (instance? clojure.lang.Namespace obj) (:doc (meta obj))
    :else nil))

(defn getsource
  "Returns the source code (not available at runtime in JVM)."
  [_obj]
  (throw (ex-info "Source not available at runtime" {})))

(defn getfile
  "Returns the file where a var was defined."
  [obj]
  (when (var? obj)
    (:file (meta obj))))

(defn getmodule
  "Returns the namespace where a var was defined."
  [obj]
  (when (var? obj)
    (.ns ^clojure.lang.Var obj)))

(defn signature
  "Returns the argument list of a function as a string."
  [f]
  (let [m (meta f)]
    (if-let [arglists (:arglists m)]
      (str/join " | " (map str arglists))
      (if (fn? f)
        (str (:name m))
        nil))))

(defn getfullargspec
  "Returns a map with argument spec of a function."
  [f]
  (let [m (meta f)
        arglists (:arglists m)]
    {:args (when arglists (first arglists))
     :varargs nil
     :varkw nil
     :defaults nil
     :kwonlyargs []
     :kwonlydefaults nil
     :annotations {}}))

(defn iscoroutinefunction?
  "Returns false (no async in JVM Clojure)."
  [_obj]
  false)

(defn isgeneratorfunction?
  "Returns false (no generators in JVM Clojure)."
  [_obj]
  false)

(defn stack
  "Returns the current call stack."
  []
  (->> (.getStackTrace (Thread/currentThread))
       (mapv (fn [^StackTraceElement ste]
               {:filename (.getFileName ste)
                :lineno (.getLineNumber ste)
                :function (.getMethodName ste)
                :classname (.getClassName ste)}))))

(defn currentframe
  "Returns info about the current frame."
  []
  (first (stack)))

(defn isframe?
  "Returns true if obj is a frame-like map."
  [obj]
  (and (map? obj)
       (contains? obj :filename)
       (contains? obj :lineno)))
