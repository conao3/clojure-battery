(ns conao3.battery.string
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(declare python-repr python-seq-str)

(def whitespace " \t\n\r\u000b\u000c")
(def ascii-lowercase "abcdefghijklmnopqrstuvwxyz")
(def ascii-uppercase "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
(def ascii-letters (str ascii-lowercase ascii-uppercase))
(def digits "0123456789")
(def hexdigits (str digits "abcdefABCDEF"))
(def octdigits "01234567")
(def punctuation "!\u0022#$%&'()*+,-./:;<=>?@[\\]^_`{|}~")
(def printable (str digits ascii-lowercase ascii-uppercase punctuation whitespace))

(defn capwords
  ([s] (capwords s nil))
  ([s sep]
   (if sep
     (->> (str/split s (re-pattern (java.util.regex.Pattern/quote sep)) -1)
          (map str/capitalize)
          (str/join sep))
     (->> (re-seq #"\S+" s)
          (map str/capitalize)
          (str/join " ")))))

(def ^:private tmpl-re
  (java.util.regex.Pattern/compile
   "\\$(?:(?<escaped>\\$)|(?<named>[_a-zA-Z][_a-zA-Z0-9]*)|\\{(?<braced>[_a-zA-Z][_a-zA-Z0-9]*)\\}|(?<invalid>))"
   java.util.regex.Pattern/CASE_INSENSITIVE))

(defn- tmpl-coerce-mapping [mapping kwargs]
  (letfn [(coerce [m]
            (into {} (map (fn [[k v]] [(if (keyword? k) (name k) (str k)) v]) m)))]
    (merge (coerce mapping) (coerce kwargs))))

(defn- tmpl-substitute* [tmpl mapping raise-on-missing?]
  (let [sb (StringBuilder.)
        m (.matcher tmpl-re tmpl)
        last-end (volatile! 0)]
    (while (.find m)
      (.append sb (.substring tmpl @last-end (.start m)))
      (vreset! last-end (.end m))
      (let [escaped (.group m "escaped")
            named (.group m "named")
            braced (.group m "braced")
            invalid (.group m "invalid")]
        (cond
          (some? escaped)
          (.append sb "$")
          (some? named)
          (let [v (get mapping named ::missing)]
            (if (= v ::missing)
              (if raise-on-missing?
                (throw (ex-info (str "Missing key: " named) {:key named}))
                (.append sb (str "$" named)))
              (.append sb (if (sequential? v) (python-seq-str v) (str v)))))
          (some? braced)
          (let [v (get mapping braced ::missing)]
            (if (= v ::missing)
              (if raise-on-missing?
                (throw (ex-info (str "Missing key: " braced) {:key braced}))
                (.append sb (str "${" braced "}")))
              (.append sb (if (sequential? v) (python-seq-str v) (str v)))))
          (some? invalid)
          (let [pos (.start m)
                prefix (.substring tmpl 0 pos)
                line (inc (count (re-seq #"\n" prefix)))
                col (- pos (or (clojure.string/last-index-of prefix "\n") -1))]
            (throw (ex-info (str "Invalid placeholder in string: line " line ", col " col) {}))))))
    (.append sb (.substring tmpl @last-end))
    (.toString sb)))

(defn template-substitute
  ([] (throw (ex-info "template-substitute requires at least 2 args" {})))
  ([_tmpl] (throw (ex-info "template-substitute requires at least 2 args" {})))
  ([tmpl mapping & kwargs]
   (when (odd? (count kwargs))
     (throw (ex-info "kwargs must be even keyword-value pairs" {})))
   (when (some #(not (keyword? %)) (take-nth 2 kwargs))
     (throw (ex-info "kwargs keys must be keywords" {})))
   (let [merged (tmpl-coerce-mapping mapping (apply hash-map kwargs))]
     (tmpl-substitute* tmpl merged true))))

(defn template-safe-substitute
  ([] (throw (ex-info "template-safe-substitute requires at least 2 args" {})))
  ([_tmpl] (throw (ex-info "template-safe-substitute requires at least 2 args" {})))
  ([tmpl mapping & kwargs]
   (when (odd? (count kwargs))
     (throw (ex-info "kwargs must be even keyword-value pairs" {})))
   (when (some #(not (keyword? %)) (take-nth 2 kwargs))
     (throw (ex-info "kwargs keys must be keywords" {})))
   (let [merged (tmpl-coerce-mapping mapping (apply hash-map kwargs))]
     (tmpl-substitute* tmpl merged false))))

(defn- string-like? [v]
  (or (string? v) (instance? Character v)))

(defn- python-repr [v]
  (if (string-like? v)
    (str "'" v "'")
    (str v)))

(defn- python-ascii [v]
  (if (string-like? v)
    (let [s (str v)
          sb (StringBuilder.)]
      (.append sb "'")
      (doseq [c s]
        (let [code (int c)]
          (cond
            (< code 0x80) (.append sb c)
            (< code 0x100) (.append sb (format "\\x%02x" code))
            (< code 0x10000) (.append sb (format "\\u%04x" code))
            :else (.append sb (format "\\U%08x" code)))))
      (.append sb "'")
      (.toString sb))
    (str v)))

(defn- apply-conversion [v conv]
  (cond
    (nil? conv) (str v)
    (= conv "s") (str v)
    (= conv "r") (python-repr v)
    (= conv "a") (python-ascii v)
    :else (throw (ex-info (str "Unknown conversion specifier: " conv) {:conv conv}))))

(def ^:private fmt-pattern
  (java.util.regex.Pattern/compile
   "\\{\\{|\\}\\}|\\{([_a-zA-Z][_a-zA-Z0-9]*|\\d+)(?:\\.([_a-zA-Z][_a-zA-Z0-9]*))?(?:\\[(\\d+)\\])?(?:!([a-zA-Z]))?\\}"))

(defn- check-mixed-format [fmt]
  (let [has-auto (boolean (re-find #"\{\}" fmt))
        has-explicit (boolean (re-find #"\{(?:\d+|[_a-zA-Z])" fmt))]
    (when (and has-auto has-explicit)
      (throw (ex-info "cannot switch from manual field specification to automatic field numbering" {})))))

(defn- python-seq-str [v]
  (str "(" (str/join ", " (map python-repr v)) ")"))

(defn- fmt-substitute
  [fmt positional named]
  (let [sb (StringBuilder.)
        m (.matcher fmt-pattern fmt)
        used-indices (volatile! #{})
        used-keys (volatile! #{})]
    (while (.find m)
      (let [full (.group m 0)]
        (.appendReplacement m sb "")
        (cond
          (= full "{{") (.append sb "{")
          (= full "}}") (.append sb "}")
          :else
          (let [key (.group m 1)
                field (.group m 2)
                idx-str (.group m 3)
                conv (.group m 4)
                base-val (if (re-matches #"\d+" key)
                           (let [i (Integer/parseInt key)]
                             (when (>= i (count positional))
                               (throw (ex-info (str "Positional index out of range: " i) {:index i})))
                             (vswap! used-indices conj i)
                             (nth positional i))
                           (do
                             (when-not (contains? named key)
                               (throw (ex-info (str "Missing key: " key) {:key key})))
                             (vswap! used-keys conj key)
                             (get named key)))
                accessed-val (cond
                               (some? field)
                               (if (map? base-val)
                                 (let [v (get base-val field ::missing)]
                                   (when (= v ::missing)
                                     (throw (ex-info (str "Field not found: " field) {:field field})))
                                   v)
                                 (throw (ex-info (str "Cannot access field on non-map: " (type base-val)) {})))
                               (some? idx-str)
                               (let [i (Integer/parseInt idx-str)]
                                 (if (sequential? base-val)
                                   (if (< i (count base-val))
                                     (nth base-val i)
                                     (throw (ex-info (str "Index out of range: " i) {:index i})))
                                   (throw (ex-info (str "Cannot index non-sequential: " (type base-val)) {}))))
                               :else base-val)]
            (.append sb (apply-conversion accessed-val conv))))))
    (.appendTail m sb)
    [(.toString sb) @used-indices @used-keys]))

(defn- split-positional-keyword [args]
  (let [[pos kw-args] (split-with (complement keyword?) args)]
    [(vec pos)
     (into {} (map (fn [[k v]] [(name k) (str v)]) (partition 2 kw-args)))]))

(defn formatter-format
  ([] (throw (ex-info "formatter-format requires at least 1 arg" {})))
  ([fmt & args]
   (check-mixed-format fmt)
   (let [[positional named] (split-positional-keyword args)
         [result _ _] (fmt-substitute fmt positional named)]
     result)))

(defn formatter-format-keyword
  ([] (throw (ex-info "formatter-format-keyword requires at least 1 arg" {})))
  ([fmt & kwargs]
   (when (odd? (count kwargs))
     (throw (ex-info "kwargs must be even keyword-value pairs" {})))
   (let [named (into {} (map (fn [[k v]] [(str/replace (name k) "-" "_") (str v)]) (partition 2 kwargs)))
         [result _ _] (fmt-substitute fmt [] named)]
     result)))

(defn formatter-get-value [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-format-field [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-convert-field [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-parse [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-check-unused-args
  ([] (throw (ex-info "formatter-check-unused-args requires at least 1 arg" {})))
  ([fmt & args]
   (let [[positional named] (split-positional-keyword args)
         [result used-indices used-keys] (fmt-substitute fmt positional named)
         unused-indices (set/difference (set (range (count positional))) used-indices)
         unused-keys (set/difference (set (keys named)) used-keys)]
     (when (or (seq unused-indices) (seq unused-keys))
       (throw (ex-info (str "Unused arguments") {:unused-indices unused-indices :unused-keys unused-keys})))
     result)))

(defn formatter-vformat-recursion-limit
  [fmt args kwargs recursion-limit]
  (when (neg? recursion-limit)
    (throw (ex-info "Max string formatting recursion exceeded" {:recursion-limit recursion-limit})))
  (let [positional (vec args)
        named (into {} (map (fn [[k v]] [(name k) (str v)]) kwargs))
        [result _ _] (fmt-substitute fmt positional named)]
    result))

(defn template-is-valid
  [tmpl]
  (let [m (.matcher tmpl-re tmpl)]
    (loop []
      (if (.find m)
        (if (some? (.group m "invalid"))
          false
          (recur))
        true))))

(defn template-get-identifiers
  [tmpl]
  (let [m (.matcher tmpl-re tmpl)
        seen (volatile! #{})
        result (volatile! [])]
    (while (.find m)
      (let [named (.group m "named")
            braced (.group m "braced")]
        (when-let [k (or named braced)]
          (when-not (contains? @seen k)
            (vswap! seen conj k)
            (vswap! result conj k)))))
    @result))
