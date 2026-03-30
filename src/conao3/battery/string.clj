(ns conao3.battery.string
  (:require [clojure.string :as str]))

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
              (.append sb (str v))))
          (some? braced)
          (let [v (get mapping braced ::missing)]
            (if (= v ::missing)
              (if raise-on-missing?
                (throw (ex-info (str "Missing key: " braced) {:key braced}))
                (.append sb (str "${" braced "}")))
              (.append sb (str v))))
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

(defn formatter-format [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-format-keyword [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-get-value [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-format-field [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-convert-field [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-parse [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-check-unused-args [& _]
  (throw (ex-info "Not implemented" {})))

(defn formatter-vformat-recursion-limit [& _]
  (throw (ex-info "Not implemented" {})))

(defn template-is-valid [& _]
  (throw (ex-info "Not implemented" {})))

(defn template-get-identifiers [& _]
  (throw (ex-info "Not implemented" {})))
