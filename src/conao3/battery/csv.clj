(ns conao3.battery.csv)

(def QUOTE_MINIMAL     0)
(def QUOTE_ALL         1)
(def QUOTE_NONNUMERIC  2)
(def QUOTE_NONE        3)

(defn- parse-row
  [^String line delimiter quotechar]
  (let [delim     (first (str delimiter))
        qchar     (first (str quotechar))
        n         (count line)]
    (loop [i 0 fields [] cur (StringBuilder.) in-quote false]
      (if (>= i n)
        (if in-quote
          (throw (ex-info "Unexpected end of quoted field" {}))
          (conj fields (.toString cur)))
        (let [c (.charAt line i)]
          (cond
            (and (not in-quote) (= c qchar))
            (recur (inc i) fields cur true)

            (and in-quote (= c qchar))
            (let [next-i (inc i)]
              (if (and (< next-i n) (= (.charAt line next-i) qchar))
                (do (.append cur qchar) (recur (+ i 2) fields cur true))
                (recur next-i fields cur false)))

            (and (not in-quote) (= c delim))
            (recur (inc i) (conj fields (.toString cur)) (StringBuilder.) false)

            :else
            (do (.append cur c) (recur (inc i) fields cur in-quote))))))))

(defn reader
  ([lines] (reader lines {}))
  ([lines {:keys [delimiter quotechar]
           :or   {delimiter \, quotechar \"}}]
   (map #(parse-row % delimiter quotechar) lines)))

(defn- needs-quoting? [^String s delim qchar]
  (some #(or (= % delim) (= % qchar) (= % \newline) (= % \return)) s))

(defn- write-field [^String s quoting delimiter quotechar]
  (let [delim (first (str delimiter))
        qchar (first (str quotechar))
        should-quote (case quoting
                       0 (or (needs-quoting? s delim qchar))
                       1 true
                       2 (not (try (Long/parseLong s) true (catch Exception _ false)))
                       3 false)]
    (if should-quote
      (str qchar (clojure.string/replace s (str qchar) (str qchar qchar)) qchar)
      s)))

(defn writer
  ([out] (writer out {}))
  ([out {:keys [delimiter quotechar quoting lineterminator]
         :or   {delimiter \, quotechar \" quoting QUOTE_MINIMAL lineterminator "\r\n"}}]
   {:out out :delimiter delimiter :quotechar quotechar :quoting quoting :lineterminator lineterminator}))

(defn writerow [w row]
  (let [{:keys [out delimiter quotechar quoting lineterminator]} w
        delim (str delimiter)
        line  (str (clojure.string/join delim
                                        (map #(write-field (str %) quoting delimiter quotechar) row))
                   lineterminator)]
    (.write ^java.io.Writer out ^String line)))

(defn writerows [w rows]
  (doseq [row rows] (writerow w row)))

(defn dict-reader
  ([lines] (dict-reader lines {}))
  ([lines opts]
   (let [all-rows (reader lines opts)
         headers  (first all-rows)
         data     (rest all-rows)]
     (map (fn [row] (zipmap headers row)) data))))

(defn dict-reader-with-fieldnames
  ([lines fieldnames] (dict-reader-with-fieldnames lines fieldnames {}))
  ([lines fieldnames opts]
   (let [rows (reader lines opts)]
     (map (fn [row] (zipmap fieldnames row)) rows))))

(defn dict-writer
  ([out fieldnames] (dict-writer out fieldnames {}))
  ([out fieldnames opts]
   (merge (writer out opts) {:fieldnames fieldnames})))

(defn writeheader [dw]
  (writerow dw (:fieldnames dw)))

(defn writerow-dict [dw row]
  (writerow dw (map #(get row % "") (:fieldnames dw))))

(defn writerows-dict [dw rows]
  (doseq [row rows] (writerow-dict dw row)))
