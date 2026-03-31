;; Original: Lib/weakref.py

(ns conao3.battery.weakref
  (:import
   [java.lang.ref WeakReference ReferenceQueue]
   [java.util WeakHashMap]))

(defn ref
  "Creates a weak reference to obj. Optionally accepts a callback function."
  ([obj] (WeakReference. obj))
  ([obj _callback] (WeakReference. obj)))

(defn deref-ref
  "Dereferences a weak reference, returning nil if the referent was collected."
  [^WeakReference r]
  (.get r))

(defn alive?
  "Returns true if the weak reference is still alive (referent not collected)."
  [^WeakReference r]
  (some? (.get r)))

(defn make-weak-dict
  "Creates a weak-keyed hash map (WeakHashMap)."
  []
  (WeakHashMap.))

(defrecord WeakValueDict [^java.util.Map _m])

(defn make-weak-value-dict
  "Creates a dict where values are weak references."
  []
  (->WeakValueDict (java.util.concurrent.ConcurrentHashMap.)))

(defn weak-value-dict-put
  [^WeakValueDict d key val]
  (.put ^java.util.Map (:_m d) key (WeakReference. val))
  d)

(defn weak-value-dict-get
  ([^WeakValueDict d key] (weak-value-dict-get d key nil))
  ([^WeakValueDict d key default]
   (if-let [^WeakReference r (.get ^java.util.Map (:_m d) key)]
     (or (.get r) default)
     default)))

(defn weak-value-dict-contains?
  [^WeakValueDict d key]
  (when-let [^WeakReference r (.get ^java.util.Map (:_m d) key)]
    (some? (.get r))))

(defn finalize
  "Attaches a finalizer function to obj. Returns a finalize object."
  [obj func]
  {:obj (WeakReference. obj)
   :func func
   :alive (atom true)})

(defn finalize-call
  "Calls the finalizer immediately if still alive."
  [fin]
  (when @(:alive fin)
    (reset! (:alive fin) false)
    ((:func fin))))

(defn finalize-alive?
  [fin]
  @(:alive fin))
