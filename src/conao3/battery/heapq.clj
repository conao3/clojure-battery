(ns conao3.battery.heapq)

(defn heappush!
  "Push item onto heap (atom of vector), maintaining heap invariant."
  [heap-atom item]
  (throw (ex-info "Not implemented" {})))

(defn heappop!
  "Pop and return smallest item from heap (atom of vector)."
  [heap-atom]
  (throw (ex-info "Not implemented" {})))

(defn heapify!
  "Transform vector in-place (atom of vector) into a heap."
  [heap-atom]
  (throw (ex-info "Not implemented" {})))

(defn heapreplace!
  "Pop and return smallest item, then push item. heap must not be empty."
  [heap-atom item]
  (throw (ex-info "Not implemented" {})))

(defn heappushpop!
  "Fast version of heappush followed by heappop."
  [heap-atom item]
  (throw (ex-info "Not implemented" {})))

(defn heappush-max!
  "Push item onto max-heap (atom of vector), maintaining max-heap invariant."
  [heap-atom item]
  (throw (ex-info "Not implemented" {})))

(defn heappop-max!
  "Pop and return largest item from max-heap (atom of vector)."
  [heap-atom]
  (throw (ex-info "Not implemented" {})))

(defn heapify-max!
  "Transform vector in-place (atom of vector) into a max-heap."
  [heap-atom]
  (throw (ex-info "Not implemented" {})))

(defn heapreplace-max!
  "Pop and return largest item from max-heap, then push item."
  [heap-atom item]
  (throw (ex-info "Not implemented" {})))

(defn heappushpop-max!
  "Fast version of heappush-max! followed by heappop-max!."
  [heap-atom item]
  (throw (ex-info "Not implemented" {})))

(defn nsmallest
  "Return a vector with the n smallest elements from data."
  [n data]
  (throw (ex-info "Not implemented" {})))

(defn nlargest
  "Return a vector with the n largest elements from data."
  [n data]
  (throw (ex-info "Not implemented" {})))

(defn heap-merge
  "Merge sorted inputs into a single sorted output (lazy seq).
  Options: :key fn, :reverse bool"
  [& args]
  (throw (ex-info "Not implemented" {})))
