(ns conao3.battery.re
  (:import
   [java.util.regex Pattern Matcher]))

(def IGNORECASE Pattern/CASE_INSENSITIVE)
(def MULTILINE Pattern/MULTILINE)
(def DOTALL Pattern/DOTALL)
(def VERBOSE Pattern/COMMENTS)
(def ASCII 0)
(def UNICODE 0)
(def I IGNORECASE)
(def M MULTILINE)
(def S DOTALL)
(def X VERBOSE)

(defn- py-to-java-regex [s]
  (-> s
      (clojure.string/replace #"\(\?P<(\w+)>" "(?<$1>")
      (clojure.string/replace #"\(\?P=(\w+)\)" "\\\\k<$1>")))

(defn- make-pattern [pattern flags]
  (if (instance? Pattern pattern)
    pattern
    (let [s (if (string? pattern) (py-to-java-regex pattern) pattern)]
      (if (zero? flags)
        (Pattern/compile ^String s)
        (Pattern/compile ^String s flags)))))

(defrecord Match [_whole _groups _named _start _end _s])

(defn- capture-match [^Matcher m s]
  (let [cnt   (.groupCount m)
        whole (.group m)
        grps  (mapv #(try (.group m (int %)) (catch Exception _ nil)) (range 1 (inc cnt)))
        pat   (.pattern (.pattern m))
        names (map second (re-seq #"(?<!\\)\(\?<(\w+)>" pat))
        named (into {} (map (fn [n]
                              [n (try (.group m ^String n) (catch Exception _ nil))])
                            names))]
    (->Match whole grps named (.start m) (.end m) s)))

(defn group
  ([^Match mo] (:_whole mo))
  ([^Match mo n]
   (if (integer? n)
     (if (zero? n) (:_whole mo) (get (:_groups mo) (dec n)))
     (get (:_named mo) (name n)))))

(defn groups [^Match mo]
  (:_groups mo))

(defn groupdict
  ([^Match mo] (groupdict mo nil))
  ([^Match mo default]
   (into {} (map (fn [[k v]] [k (or v default)]) (:_named mo)))))

(defn start
  ([^Match mo] (:_start mo))
  ([^Match mo n]
   (if (and (integer? n) (zero? n))
     (:_start mo)
     nil)))

(defn end
  ([^Match mo] (:_end mo))
  ([^Match mo n]
   (if (and (integer? n) (zero? n))
     (:_end mo)
     nil)))

(defn span
  ([^Match mo] [(:_start mo) (:_end mo)])
  ([^Match mo n] [(start mo n) (end mo n)]))

(defn string [^Match mo] (:_s mo))

(defn match
  ([pattern s] (match pattern s 0))
  ([pattern s flags]
   (let [pat (make-pattern pattern flags)
         m   (.matcher pat ^String s)]
     (when (.lookingAt m)
       (capture-match m s)))))

(defn fullmatch
  ([pattern s] (fullmatch pattern s 0))
  ([pattern s flags]
   (let [pat (make-pattern pattern flags)
         m   (.matcher pat ^String s)]
     (when (.matches m)
       (capture-match m s)))))

(defn search
  ([pattern s] (search pattern s 0))
  ([pattern s flags]
   (let [pat (make-pattern pattern flags)
         m   (.matcher pat ^String s)]
     (when (.find m)
       (capture-match m s)))))

(defn findall
  ([pattern s] (findall pattern s 0))
  ([pattern s flags]
   (let [pat (make-pattern pattern flags)
         m   (.matcher pat ^String s)
         cnt (.groupCount m)]
     (loop [results []]
       (if (.find m)
         (recur (conj results
                      (cond
                        (zero? cnt) (.group m)
                        (= 1 cnt)   (.group m 1)
                        :else       (mapv #(.group m (int %)) (range 1 (inc cnt))))))
         results)))))

(defn finditer
  ([pattern s] (finditer pattern s 0))
  ([pattern s flags]
   (let [pat (make-pattern pattern flags)
         m   (.matcher pat ^String s)]
     (loop [results []]
       (if (.find m)
         (recur (conj results (capture-match m s)))
         results)))))

(defn sub
  ([pattern repl s] (sub pattern repl s 0 0))
  ([pattern repl s max-count] (sub pattern repl s max-count 0))
  ([pattern repl s max-count flags]
   (let [pat (make-pattern pattern flags)
         m   (.matcher pat ^String s)
         sb  (StringBuilder.)]
     (loop [last-end 0 n 0]
       (if (and (.find m) (or (zero? max-count) (< n max-count)))
         (do (.append sb (.substring ^String s last-end (.start m)))
             (.append sb ^String (if (string? repl) repl (str (repl (capture-match m s)))))
             (recur (.end m) (inc n)))
         (.append sb (.substring ^String s last-end (clojure.core/count s)))))
     (.toString sb))))

(defn subn
  ([pattern repl s] (subn pattern repl s 0 0))
  ([pattern repl s max-count] (subn pattern repl s max-count 0))
  ([pattern repl s max-count flags]
   (let [pat (make-pattern pattern flags)
         m   (.matcher pat ^String s)
         sb  (StringBuilder.)
         n   (atom 0)]
     (loop [last-end 0]
       (if (and (.find m) (or (zero? max-count) (< @n max-count)))
         (do (.append sb (.substring ^String s last-end (.start m)))
             (.append sb ^String (if (string? repl) repl (str (repl (capture-match m s)))))
             (swap! n inc)
             (recur (.end m)))
         (.append sb (.substring ^String s last-end (clojure.core/count s)))))
     [(.toString sb) @n])))

(defn split
  ([pattern s] (split pattern s 0 0))
  ([pattern s maxsplit] (split pattern s maxsplit 0))
  ([pattern s maxsplit flags]
   (let [pat    (make-pattern pattern flags)
         m      (.matcher pat ^String s)
         cnt    (.groupCount m)
         result (atom [])
         last   (atom 0)
         splits (atom 0)]
     (while (and (.find m) (or (zero? maxsplit) (< @splits maxsplit)))
       (swap! result conj (.substring ^String s @last (.start m)))
       (when (pos? cnt)
         (doseq [i (range 1 (inc cnt))]
           (swap! result conj (.group m (int i)))))
       (reset! last (.end m))
       (swap! splits inc))
     (swap! result conj (.substring ^String s @last))
     @result)))

(defn escape [s]
  (Pattern/quote ^String s))

(defrecord CompiledPattern [^Pattern _pat])

(defn compile
  ([pattern] (compile pattern 0))
  ([pattern flags]
   (->CompiledPattern (make-pattern pattern flags))))

(defn compiled-match [^CompiledPattern cp s]
  (match (:_pat cp) s))

(defn compiled-fullmatch [^CompiledPattern cp s]
  (fullmatch (:_pat cp) s))

(defn compiled-search [^CompiledPattern cp s]
  (search (:_pat cp) s))

(defn compiled-findall [^CompiledPattern cp s]
  (findall (:_pat cp) s))

(defn compiled-finditer [^CompiledPattern cp s]
  (finditer (:_pat cp) s))

(defn compiled-sub
  ([^CompiledPattern cp repl s] (sub (:_pat cp) repl s))
  ([^CompiledPattern cp repl s max-count] (sub (:_pat cp) repl s max-count)))

(defn compiled-subn
  ([^CompiledPattern cp repl s] (subn (:_pat cp) repl s))
  ([^CompiledPattern cp repl s max-count] (subn (:_pat cp) repl s max-count)))

(defn compiled-split
  ([^CompiledPattern cp s] (split (:_pat cp) s))
  ([^CompiledPattern cp s maxsplit] (split (:_pat cp) s maxsplit)))

(defn pattern [^CompiledPattern cp]
  (.pattern ^Pattern (:_pat cp)))

(defn re-flags [^CompiledPattern cp]
  (.flags ^Pattern (:_pat cp)))
