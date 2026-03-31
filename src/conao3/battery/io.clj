(ns conao3.battery.io)

(defn- bytes-io-state
  ([] (bytes-io-state (byte-array 0)))
  ([initial-bytes]
   (let [n   (alength ^bytes initial-bytes)
         buf (byte-array n)]
     (System/arraycopy initial-bytes 0 buf 0 n)
     (atom {:buf    buf
            :len    n
            :pos    0
            :closed false}))))

(defn- ensure-open [state]
  (when (:closed @state)
    (throw (ex-info "I/O operation on closed file" {}))))

(defn- ensure-capacity [state needed]
  (let [{:keys [^bytes buf len]} @state
        current-cap (alength buf)]
    (when (> needed current-cap)
      (let [new-cap (max needed (* 2 current-cap) 16)
            new-buf (byte-array new-cap)]
        (System/arraycopy buf 0 new-buf 0 len)
        (swap! state assoc :buf new-buf)))))

(defprotocol IBuffer
  (buf-read   [this n])
  (buf-write  [this data])
  (buf-seek   [this pos whence])
  (buf-tell   [this])
  (buf-close  [this])
  (buf-truncate [this size])
  (buf-getvalue [this]))

(deftype BytesIO [state]
  IBuffer
  (buf-read [_ n]
    (ensure-open state)
    (let [{:keys [^bytes buf pos len]} @state
          available (- len pos)
          to-read   (if (nil? n) available (min n available))
          result    (byte-array to-read)]
      (when (pos? to-read)
        (System/arraycopy buf pos result 0 to-read))
      (swap! state update :pos + to-read)
      result))

  (buf-write [_ data]
    (ensure-open state)
    (let [bs  (if (bytes? data) data (.getBytes ^String data "ISO-8859-1"))
          n   (alength ^bytes bs)
          pos (:pos @state)]
      (ensure-capacity state (+ pos n))
      (let [^bytes buf (:buf @state)]
        (System/arraycopy bs 0 buf pos n)
        (swap! state (fn [{:keys [len] :as s}]
                       (-> s
                           (assoc :pos (+ pos n))
                           (assoc :len (max len (+ pos n)))))))
      n))

  (buf-seek [_ offset whence]
    (ensure-open state)
    (let [len (:len @state)
          new-pos (case (int whence)
                    0 offset
                    1 (+ (:pos @state) offset)
                    2 (+ len offset)
                    (throw (ex-info "invalid whence" {:whence whence})))]
      (when (neg? new-pos)
        (throw (ex-info "negative seek position" {:pos new-pos})))
      (swap! state assoc :pos new-pos)
      new-pos))

  (buf-tell [_]
    (ensure-open state)
    (:pos @state))

  (buf-close [_]
    (swap! state assoc :closed true))

  (buf-truncate [_ size]
    (ensure-open state)
    (let [new-size (if (nil? size) (:pos @state) size)]
      (swap! state (fn [{:keys [^bytes buf len pos] :as s}]
                     (let [new-len (min new-size len)
                           new-buf (byte-array new-size)]
                       (System/arraycopy buf 0 new-buf 0 new-len)
                       (-> s
                           (assoc :buf new-buf)
                           (assoc :len new-len)
                           (assoc :pos (min pos new-len))))))
      (:pos @state)))

  (buf-getvalue [_]
    (let [{:keys [^bytes buf len]} @state
          result (byte-array len)]
      (System/arraycopy buf 0 result 0 len)
      result))

  java.io.Closeable
  (close [this] (buf-close this)))

(deftype StringIO [state]
  IBuffer
  (buf-read [_ n]
    (ensure-open state)
    (let [{:keys [^StringBuilder buf pos]} @state
          len       (.length buf)
          available (- len pos)
          to-read   (if (nil? n) available (min n available))
          result    (.substring buf pos (+ pos to-read))]
      (swap! state update :pos + to-read)
      result))

  (buf-write [_ data]
    (ensure-open state)
    (let [s   (str data)
          pos (:pos @state)
          ^StringBuilder buf (:buf @state)]
      (when (< pos (.length buf))
        (.replace buf pos (min (+ pos (count s)) (.length buf)) s))
      (when (>= pos (.length buf))
        (.append buf s))
      (swap! state update :pos + (count s))
      (count s)))

  (buf-seek [_ offset whence]
    (ensure-open state)
    (let [len (.length ^StringBuilder (:buf @state))
          new-pos (case (int whence)
                    0 offset
                    1 (+ (:pos @state) offset)
                    2 (+ len offset)
                    (throw (ex-info "invalid whence" {:whence whence})))]
      (when (neg? new-pos)
        (throw (ex-info "negative seek position" {:pos new-pos})))
      (swap! state assoc :pos (min new-pos len))
      new-pos))

  (buf-tell [_]
    (ensure-open state)
    (:pos @state))

  (buf-close [_]
    (swap! state assoc :closed true))

  (buf-truncate [_ size]
    (ensure-open state)
    (let [^StringBuilder buf (:buf @state)
          new-size (if (nil? size) (:pos @state) size)]
      (.setLength buf new-size)
      (swap! state update :pos min new-size)
      (:pos @state)))

  (buf-getvalue [_]
    (.toString ^StringBuilder (:buf @state)))

  java.io.Closeable
  (close [this] (buf-close this)))

(defn bytes-io
  ([] (->BytesIO (bytes-io-state)))
  ([initial] (->BytesIO (bytes-io-state initial))))

(defn string-io
  ([] (->StringIO (atom {:buf (StringBuilder.) :pos 0 :closed false})))
  ([initial]
   (->StringIO (atom {:buf (StringBuilder. ^String initial)
                      :pos 0
                      :closed false}))))

(defn read
  ([buf] (buf-read buf nil))
  ([buf n] (buf-read buf n)))

(defn write [buf data]
  (buf-write buf data))

(defn seek
  ([buf offset] (buf-seek buf offset 0))
  ([buf offset whence] (buf-seek buf offset whence)))

(defn tell [buf]
  (buf-tell buf))

(defn close [buf]
  (buf-close buf))

(defn truncate
  ([buf] (buf-truncate buf nil))
  ([buf size] (buf-truncate buf size)))

(defn getvalue [buf]
  (buf-getvalue buf))

(defn readline [io-buf]
  (ensure-open (.-state io-buf))
  (if (instance? StringIO io-buf)
    (let [{sb :buf pos :pos} @(.-state ^StringIO io-buf)
          ^StringBuilder sb sb
          len (.length sb)
          newline-pos (loop [i pos]
                        (cond
                          (>= i len) len
                          (= (.charAt sb i) \newline) (inc i)
                          :else (recur (inc i))))]
      (let [result (.substring sb pos newline-pos)]
        (swap! (.-state ^StringIO io-buf) assoc :pos newline-pos)
        result))
    (let [{raw-buf :buf pos :pos dlen :len} @(.-state ^BytesIO io-buf)
          ^bytes raw-buf raw-buf
          newline-pos (loop [i pos]
                        (cond
                          (>= i dlen) dlen
                          (= (aget raw-buf i) (byte \newline)) (inc i)
                          :else (recur (inc i))))
          result (byte-array (- newline-pos pos))]
      (System/arraycopy raw-buf pos result 0 (alength result))
      (swap! (.-state ^BytesIO io-buf) assoc :pos newline-pos)
      result)))
