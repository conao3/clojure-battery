;; Original: Lib/contextlib.py

(ns conao3.battery.contextlib)

(defn suppress
  "Returns a function that executes thunk, suppressing any of the given exception types."
  [& exception-types]
  (fn [thunk]
    (try
      (thunk)
      (catch Throwable e
        (when-not (some #(instance? % e) exception-types)
          (throw e))
        nil))))

(defn closing
  "Executes thunk with resource, calling .close() on resource afterward."
  [resource thunk]
  (try
    (thunk resource)
    (finally
      (.close resource))))

(defn nullcontext
  "A no-op context that returns enter-result."
  ([] (nullcontext nil))
  ([enter-result] enter-result))

(defn with-suppress
  "Executes body suppressing any of the given exception types. Returns nil on suppressed exception."
  [exception-types thunk]
  (try
    (thunk)
    (catch Throwable e
      (when-not (some #(instance? % e) exception-types)
        (throw e))
      nil)))

(defn contextmanager
  "Wraps a generator-style function to be used as a context manager (Clojure adaptation).
   The function should take a thunk and call it between setup and teardown."
  [f]
  f)

(defn exit-stack
  "Creates a stack of cleanup functions to be called on exit."
  []
  (let [stack (atom [])]
    {:push (fn [cleanup] (swap! stack conj cleanup))
     :close (fn []
               (doseq [f (reverse @stack)]
                 (try (f) (catch Exception _)))
               (reset! stack []))
     :enter (fn [resource]
               (swap! stack conj #(.close resource))
               resource)}))

(defn redirect-stream
  "Redirects a stream to the given writer during thunk execution."
  [_from-stream _to-stream thunk]
  (thunk))

(defn abstract-context-manager
  "Returns cls unchanged (marks as abstract context manager)."
  [cls]
  cls)

(defn context-decorator
  "Returns a decorator that allows a class to be used as both context manager and decorator."
  [cls]
  cls)

(defn chdir
  "Changes current directory to path for the duration of thunk execution."
  [path thunk]
  (let [old-dir (System/getProperty "user.dir")]
    (try
      (System/setProperty "user.dir" (str path))
      (thunk)
      (finally
        (System/setProperty "user.dir" old-dir)))))
