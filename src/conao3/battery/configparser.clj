(ns conao3.battery.configparser
  (:require [clojure.string :as str]))

(def ^:private boolean-states
  {"1" true "yes" true "true" true "on" true
   "0" false "no" false "false" false "off" false})

(defn- parse-ini [s]
  (loop [lines (str/split-lines s)
         current-section nil
         current-key nil
         sections {}
         order []]
    (if (empty? lines)
      {:sections sections :order order}
      (let [line (first lines)
            rest-lines (rest lines)
            trimmed (str/trim line)]
        (cond
          (or (str/blank? trimmed)
              (str/starts-with? trimmed "#")
              (str/starts-with? trimmed ";"))
          (recur rest-lines current-section nil sections order)

          (and (str/starts-with? trimmed "[") (str/ends-with? trimmed "]"))
          (let [section (subs trimmed 1 (dec (count trimmed)))]
            (recur rest-lines section nil
                   (if (contains? sections section) sections (assoc sections section {}))
                   (if (contains? sections section) order (conj order section))))

          (and current-section current-key (re-matches #"[ \t]+\S.*" line))
          (recur rest-lines current-section current-key
                 (update-in sections [current-section current-key] #(str % "\n" trimmed))
                 order)

          :else
          (let [eq-pos (str/index-of line "=")
                colon-pos (str/index-of line ":")
                delim-pos (cond
                            (and eq-pos colon-pos) (min eq-pos colon-pos)
                            eq-pos eq-pos
                            :else colon-pos)
                k (when delim-pos (str/lower-case (str/trim (subs line 0 delim-pos))))
                raw-val (when delim-pos (str/trim (subs line (inc delim-pos))))]
            (if (and k raw-val current-section)
              (let [val (str/replace raw-val #"\s+[;#].*$" "")]
                (recur rest-lines current-section k
                       (assoc-in sections [current-section k] val)
                       order))
              (recur rest-lines current-section nil sections order))))))))

(defn read-string [s]
  (atom (parse-ini s)))

(defn sections [cfg]
  (:order @cfg))

(defn has-section [cfg section]
  (contains? (:sections @cfg) section))

(defn has-option [cfg section k]
  (contains? (clojure.core/get (:sections @cfg) section {}) (str/lower-case k)))

(defn get
  ([cfg section k]
   (let [data @cfg
         s-data (clojure.core/get (:sections data) section ::no-section)]
     (if (= s-data ::no-section)
       (throw (ex-info (str "No section: " section) {:section section}))
       (let [v (clojure.core/get s-data (str/lower-case k) ::no-option)]
         (if (= v ::no-option)
           (throw (ex-info (str "No option: " k) {:section section :option k}))
           v)))))
  ([cfg section k fallback]
   (try
     (get cfg section k)
     (catch clojure.lang.ExceptionInfo _ fallback))))

(defn get-int
  ([cfg section k] (Long/parseLong (get cfg section k)))
  ([cfg section k fallback] (try (get-int cfg section k) (catch Exception _ fallback))))

(defn get-float
  ([cfg section k] (Double/parseDouble (get cfg section k)))
  ([cfg section k fallback] (try (get-float cfg section k) (catch Exception _ fallback))))

(defn get-boolean
  ([cfg section k]
   (let [v (str/lower-case (get cfg section k))]
     (if (contains? boolean-states v)
       (clojure.core/get boolean-states v)
       (throw (ex-info (str "Not a boolean: " v) {})))))
  ([cfg section k fallback]
   (try (get-boolean cfg section k) (catch Exception _ fallback))))

(defn items [cfg section]
  (let [s-data (clojure.core/get (:sections @cfg) section)]
    (when (nil? s-data)
      (throw (ex-info (str "No section: " section) {})))
    (vec s-data)))

(defn remove-section [cfg section]
  (if (has-section cfg section)
    (do (swap! cfg (fn [data]
                     (-> data
                         (update :sections dissoc section)
                         (update :order (fn [o] (vec (remove #{section} o)))))))
        true)
    false))

(defn remove-option [cfg section k]
  (let [lk (str/lower-case k)]
    (if (has-option cfg section lk)
      (do (swap! cfg update-in [:sections section] dissoc lk)
          true)
      false)))
