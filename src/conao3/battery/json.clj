(ns conao3.battery.json)

(defn- escape-string [s]
  (let [sb (StringBuilder.)]
    (.append sb \")
    (doseq [c s]
      (case c
        \" (.append sb "\\\"")
        \\ (.append sb "\\\\")
        \newline (.append sb "\\n")
        \return (.append sb "\\r")
        \tab (.append sb "\\t")
        (if (< (int c) 32)
          (.append sb (format "\\u%04x" (int c)))
          (.append sb c))))
    (.append sb \")
    (.toString sb)))

(defn- dumps-value [v indent sort-keys item-sep kv-sep depth]
  (cond
    (nil? v)      "null"
    (= true v)    "true"
    (= false v)   "false"
    (string? v)   (escape-string v)
    (integer? v)  (str v)
    (float? v)    (let [s (str v)]
                    (if (clojure.string/includes? s "E")
                      (clojure.string/replace s "E" "e+")
                      s))
    (ratio? v)    (str (double v))
    (map? v)      (let [entries (if sort-keys (sort-by key v) (seq v))
                        inner-indent (when indent (str indent (apply str (repeat depth "  "))))
                        sep (or item-sep (if indent ",\n" ", "))
                        kvs (clojure.string/join (str item-sep (if (and indent (not item-sep))
                                                                  (str "\n" inner-indent "  ")
                                                                  ""))
                                                 (map (fn [[k val]]
                                                        (str (escape-string (name k))
                                                             (or kv-sep (if indent ": " ": "))
                                                             (dumps-value val indent sort-keys item-sep kv-sep (inc depth))))
                                                      entries))]
                    (if (empty? entries)
                      "{}"
                      (if indent
                        (let [pad (apply str (repeat depth "  "))]
                          (str "{\n" indent "  " pad
                               (clojure.string/join (str ",\n" indent "  " pad)
                                                    (map (fn [[k val]]
                                                           (str (escape-string (name k))
                                                                ": "
                                                                (dumps-value val indent sort-keys item-sep kv-sep (inc depth))))
                                                         entries))
                               "\n" indent pad "}"))
                        (str "{" (clojure.string/join (str (or item-sep ", "))
                                                      (map (fn [[k val]]
                                                             (str (escape-string (name k))
                                                                  (or kv-sep ": ")
                                                                  (dumps-value val indent sort-keys item-sep kv-sep (inc depth))))
                                                           entries)) "}"))))
    (sequential? v) (if (empty? v)
                      "[]"
                      (if indent
                        (let [pad (apply str (repeat depth "  "))]
                          (str "[\n" indent "  " pad
                               (clojure.string/join (str ",\n" indent "  " pad)
                                                    (map #(dumps-value % indent sort-keys item-sep kv-sep (inc depth)) v))
                               "\n" indent pad "]"))
                        (str "[" (clojure.string/join (or item-sep ", ")
                                                      (map #(dumps-value % indent sort-keys item-sep kv-sep (inc depth)) v)) "]")))
    :else (str v)))

(defn dumps
  ([obj] (dumps obj nil nil nil nil))
  ([obj & {:keys [indent sort-keys separators]}]
   (let [[item-sep kv-sep] (or separators [nil nil])
         indent-str (when indent (apply str (repeat indent " ")))]
     (dumps-value obj indent-str sort-keys item-sep kv-sep 0))))

(defn dump [obj f & opts]
  (.write f ^String (apply dumps obj opts)))

(defn- skip-ws [^String s ^long i]
  (loop [j i]
    (if (and (< j (count s)) (Character/isWhitespace (.charAt s j)))
      (recur (inc j))
      j)))

(defn- parse-string [^String s ^long start]
  (let [sb (StringBuilder.)]
    (loop [i (inc start)]
      (if (>= i (count s))
        (throw (ex-info "Unterminated string" {}))
        (let [c (.charAt s i)]
          (cond
            (= c \")  [(.toString sb) (inc i)]
            (= c \\)  (let [nc (.charAt s (inc i))]
                        (.append sb (case nc
                                      \" \"
                                      \\ \\
                                      \/ \/
                                      \n \newline
                                      \r \return
                                      \t \tab
                                      \b \backspace
                                      \f \formfeed
                                      \u (let [hex (subs s (+ i 2) (+ i 6))
                                               code (Integer/parseInt hex 16)]
                                           (char code))
                                      nc))
                        (if (= nc \u)
                          (recur (+ i 6))
                          (recur (+ i 2))))
            :else      (do (.append sb c) (recur (inc i)))))))))

(defn- parse-number [^String s ^long i]
  (let [j (loop [j i]
             (if (and (< j (count s))
                      (let [c (.charAt s j)]
                        (or (Character/isDigit c) (= c \.) (= c \-) (= c \+)
                            (= c \e) (= c \E))))
               (recur (inc j))
               j))
        token (subs s i j)]
    (if (or (clojure.string/includes? token ".") (clojure.string/includes? token "e")
            (clojure.string/includes? token "E"))
      [(Double/parseDouble token) j]
      [(Long/parseLong token) j])))

(declare parse-value)

(defn- parse-array [^String s ^long i]
  (let [i (skip-ws s (inc i))]
    (if (= (.charAt s i) \])
      [[] (inc i)]
      (loop [j i result []]
        (let [[v j] (parse-value s j)
              j     (skip-ws s j)]
          (cond
            (= (.charAt s j) \,) (recur (skip-ws s (inc j)) (conj result v))
            (= (.charAt s j) \]) [(conj result v) (inc j)]
            :else (throw (ex-info "Expected , or ]" {:pos j}))))))))

(defn- parse-object [^String s ^long i]
  (let [i (skip-ws s (inc i))]
    (if (= (.charAt s i) \})
      [{} (inc i)]
      (loop [j i result {}]
        (let [j     (skip-ws s j)
              [k j] (parse-string s j)
              j     (skip-ws s j)
              _     (when (not= (.charAt s j) \:) (throw (ex-info "Expected :" {:pos j})))
              j     (skip-ws s (inc j))
              [v j] (parse-value s j)
              j     (skip-ws s j)
              result (assoc result k v)]
          (cond
            (= (.charAt s j) \,) (recur (skip-ws s (inc j)) result)
            (= (.charAt s j) \}) [result (inc j)]
            :else (throw (ex-info "Expected , or }" {:pos j}))))))))

(defn- parse-value [^String s ^long i]
  (let [i (skip-ws s i)]
    (when (>= i (count s))
      (throw (ex-info "Unexpected end of input" {})))
    (let [c (.charAt s i)]
      (cond
        (= c \") (parse-string s i)
        (= c \[) (parse-array s i)
        (= c \{) (parse-object s i)
        (= c \t) (if (= (subs s i (+ i 4)) "true")  [true  (+ i 4)] (throw (ex-info "Invalid token" {})))
        (= c \f) (if (= (subs s i (+ i 5)) "false") [false (+ i 5)] (throw (ex-info "Invalid token" {})))
        (= c \n) (if (= (subs s i (+ i 4)) "null")  [nil   (+ i 4)] (throw (ex-info "Invalid token" {})))
        (or (= c \-) (Character/isDigit c)) (parse-number s i)
        :else (throw (ex-info (str "Unexpected character: " c) {:pos i}))))))

(defn loads [s]
  (let [^String s (if (bytes? s) (String. ^bytes s "UTF-8") (str s))]
    (when (zero? (count s))
      (throw (ex-info "Expecting value: line 1 column 1 (char 0)" {})))
    (try
      (let [[v i] (parse-value s 0)
            i     (skip-ws s i)]
        (when (< i (count s))
          (throw (ex-info "Extra data after JSON" {:pos i})))
        v)
      (catch clojure.lang.ExceptionInfo e (throw e))
      (catch Exception e
        (throw (ex-info (str "JSONDecodeError: " (.getMessage e)) {} e))))))

(defn load [f]
  (loads (slurp f)))
