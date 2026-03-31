(ns conao3.battery.argparse
  (:require [clojure.string :as str]))

(def SUPPRESS "==SUPPRESS==")
(def OPTIONAL "?")
(def ZERO_OR_MORE "*")
(def ONE_OR_MORE "+")
(def REMAINDER "A...")

(defn- parse-error [parser message]
  (ex-info message {:type ::parse-error :parser parser :message message}))

(defn- optional-string? [s prefix-chars]
  (and (string? s) (pos? (count s))
       (str/includes? (or prefix-chars "-") (subs s 0 1))))

(defn- dest-from-option-strings [option-strings prefix-chars]
  (let [pc (or prefix-chars "-")
        long-opts (filter (fn [s]
                            (and (>= (count s) 2)
                                 (str/includes? pc (str (nth s 0)))
                                 (str/includes? pc (str (nth s 1))))) option-strings)]
    (if (seq long-opts)
      (-> (first long-opts) (subs 2) (str/replace "-" "_"))
      (let [short-opts (filter #(and (optional-string? % pc)
                                     (not (and (>= (count %) 2)
                                               (str/includes? pc (str (nth % 1)))))) option-strings)]
        (when (seq short-opts)
          (subs (first short-opts) 1))))))

(defn make-parser
  [& {:keys [prog description epilog usage add-help prefix-chars
             argument-default conflict-handler allow-abbrev exit-on-error]
      :or {add-help true prefix-chars "-" argument-default nil
           conflict-handler "error" allow-abbrev true exit-on-error true}}]
  {:prog prog
   :description description
   :epilog epilog
   :usage usage
   :prefix-chars prefix-chars
   :argument-default argument-default
   :conflict-handler conflict-handler
   :allow-abbrev allow-abbrev
   :exit-on-error exit-on-error
   :add-help add-help
   :arguments []
   :defaults {}
   :mutex-groups []})

(defn- build-argument [option-strings opts prefix-chars argument-default]
  (let [pc (or prefix-chars "-")
        is-optional (and (seq option-strings)
                         (optional-string? (first option-strings) pc))
        dest (or (:dest opts)
                 (when is-optional (dest-from-option-strings option-strings pc))
                 (when (and (not is-optional) (seq option-strings)) (first option-strings)))
        action (get opts :action "store")
        nargs (:nargs opts)
        const (:const opts)
        default (if (contains? opts :default)
                  (:default opts)
                  (cond
                    (= action "store_true") false
                    (= action "store_false") true
                    (and (not (and (seq option-strings)
                                   (optional-string? (first option-strings) (or prefix-chars "-"))))
                         (contains? #{"*" "A..."} (get opts :nargs)))
                    []
                    :else argument-default))
        type-fn (:type opts)
        choices (:choices opts)
        required (if (contains? opts :required) (:required opts) (when is-optional false))
        help (:help opts)
        metavar (:metavar opts)]
    {:option-strings option-strings
     :dest dest
     :action action
     :nargs nargs
     :const const
     :default default
     :type type-fn
     :choices choices
     :required required
     :help help
     :metavar metavar
     :is-optional is-optional}))

(defn- add-argument-single [parser option-strings opts prefix-chars argument-default]
  (let [arg (build-argument option-strings opts prefix-chars argument-default)
        conflict-handler (get parser :conflict-handler "error")]
    (if (= conflict-handler "resolve")
      (let [conflicting-opts (set option-strings)
            filtered-args (filterv (fn [existing]
                                     (not (some conflicting-opts (:option-strings existing))))
                                   (:arguments parser))]
        (assoc parser :arguments (conj filtered-args arg)))
      (do
        (doseq [opt-str option-strings]
          (when (some #(some #{opt-str} (:option-strings %)) (:arguments parser))
            (throw (ex-info (str "conflicting option string(s): " opt-str)
                            {:type ::argument-error}))))
        (update parser :arguments conj arg)))))

(defn- make-boolean-option-negation [opt-str prefix-chars]
  (let [pc (or prefix-chars "-")
        double-prefix (str (first pc) (first pc))]
    (when (str/starts-with? opt-str double-prefix)
      (str double-prefix "no-" (subs opt-str (count double-prefix))))))

(def ^:private valid-actions
  #{"store" "store_true" "store_false" "store_const" "append" "append_const" "count" "extend" "boolean_optional"})

(defn add-argument [parser & args]
  (let [option-strings (vec (take-while string? args))
        rest-args (drop-while string? args)
        opts (if (seq rest-args) (apply hash-map rest-args) {})
        prefix-chars (:prefix-chars parser)
        argument-default (:argument-default parser)
        action (get opts :action "store")]
    (when (contains? opts :type)
      (let [type-val (:type opts)]
        (when (and (some? type-val) (not (fn? type-val)))
          (throw (ex-info (str "type must be callable, got: " type-val)
                          {:type ::argument-error})))))
    (when (and (string? action) (not (contains? valid-actions action)))
      (throw (ex-info (str "invalid action: " action)
                      {:type ::argument-error})))
    (if (= action "boolean_optional")
      (let [pc (or prefix-chars "-")
            double-prefix (str (first pc) (first pc))]
        (doseq [opt-str option-strings]
          (when-not (str/starts-with? opt-str double-prefix)
            (throw (ex-info (str "boolean optional arguments must start with " double-prefix ": " opt-str)
                            {:type ::argument-error}))))
        (let [dest (or (:dest opts) (dest-from-option-strings option-strings pc))
              default (if (contains? opts :default) (:default opts) nil)
              required (when (contains? opts :required) (:required opts))
              pos-opts (-> opts (dissoc :action) (assoc :action "store_true" :default default :dest dest))
              neg-opts (-> opts (dissoc :action) (assoc :action "store_false" :default default :dest dest))
              neg-strings (mapv #(make-boolean-option-negation % prefix-chars) option-strings)
              p2 (add-argument-single parser option-strings pos-opts prefix-chars argument-default)
              p3 (add-argument-single p2 neg-strings (dissoc neg-opts :required) prefix-chars argument-default)]
          (if required
            (-> p3
                (update :arguments (fn [arguments]
                                     (mapv (fn [a]
                                             (if (some #{(first option-strings)} (:option-strings a))
                                               (assoc a :required true)
                                               a))
                                           arguments))))
            p3)))
      (add-argument-single parser option-strings opts prefix-chars argument-default))))

(defn set-defaults [parser & {:as new-defaults}]
  (update parser :defaults merge new-defaults))

(defn get-default [parser dest]
  (or (get (:defaults parser) (keyword dest))
      (some #(when (= (:dest %) (name dest)) (:default %)) (:arguments parser))))

(defn add-mutually-exclusive-group [parser & {:keys [required] :or {required false}}]
  {:parser parser :required required :arguments []})

(defn- is-negative-number? [s]
  (try
    (let [v (Double/parseDouble s)] (< v 0))
    (catch Exception _ false)))

(defn- looks-like-optional? [s prefix-chars has-negative-number-options known-options]
  (and (optional-string? s prefix-chars)
       (or (contains? known-options s)
           (not (and has-negative-number-options (is-negative-number? s))))))

(defn- convert-value [type-fn value dest]
  (if (nil? type-fn)
    value
    (try
      (type-fn value)
      (catch Exception _
        (throw (ex-info (str "argument " dest ": invalid value: '" value "'")
                        {:type ::argument-error :dest dest}))))))

(defn- apply-action! [ns-atom provided-atom arg value]
  (let [dest (keyword (:dest arg))
        action (:action arg)
        choices (:choices arg)]
    (when (and (some? value) (some? choices) (not (some #{value} choices)))
      (throw (ex-info (str "argument " (:dest arg) ": invalid choice: " value)
                      {:type ::argument-error})))
    (swap! provided-atom conj dest)
    (cond
      (= action "store") (swap! ns-atom assoc dest value)
      (= action "store_true") (swap! ns-atom assoc dest true)
      (= action "store_false") (swap! ns-atom assoc dest false)
      (= action "store_const") (swap! ns-atom assoc dest (:const arg))
      (= action "append") (swap! ns-atom update dest (fn [v] (conj (or v []) value)))
      (= action "append_const") (swap! ns-atom update dest (fn [v] (conj (or v []) (:const arg))))
      (= action "count") (swap! ns-atom update dest (fn [v] (inc (or v 0))))
      (= action "extend") (swap! ns-atom update dest (fn [v] (into (or v []) (if (sequential? value) value [value]))))
      (fn? action) (action ns-atom dest arg value)
      :else (swap! ns-atom assoc dest value))))

(defn parse-known-args [parser args]
  (let [prefix-chars (or (:prefix-chars parser) "-")
        arguments (:arguments parser)
        allow-abbrev (get parser :allow-abbrev true)
        optionals (filter :is-optional arguments)
        positionals (filter (complement :is-optional) arguments)
        option-map (reduce (fn [m arg]
                             (reduce (fn [m2 s] (assoc m2 s arg)) m (:option-strings arg)))
                           {}
                           optionals)
        has-neg-num (or (some #(is-negative-number? %) (keys option-map))
                        (some #(:type %) optionals))
        init-ns (reduce (fn [ns arg]
                          (let [default (:default arg)
                                dest (keyword (:dest arg))]
                            (if (and (not= default SUPPRESS)
                                     (not (contains? ns dest)))
                              (assoc ns dest default)
                              ns)))
                        (reduce (fn [ns [k v]]
                                  (assoc ns (keyword (name k)) v))
                                {}
                                (:defaults parser))
                        arguments)
        ns-atom (atom init-ns)
        provided-atom (atom #{})
        extras-atom (atom [])
        arg-vec-atom (atom (vec args))
        pos-idx (atom 0)]

    (letfn [(n [] (count @arg-vec-atom))

            (nth-arg [i] (nth @arg-vec-atom i))

            (find-option [s]
              (or (get option-map s)
                  (when allow-abbrev
                    (let [matches (filter #(str/starts-with? % s) (keys option-map))]
                      (cond
                        (= 1 (count matches)) (get option-map (first matches))
                        (> (count matches) 1)
                        (throw (parse-error parser (str "ambiguous option: " s
                                                        " could match "
                                                        (str/join ", " (sort matches))))))))))

            (take-next-value [i-atom]
              (let [ni (inc @i-atom)]
                (if (< ni (n))
                  (do (swap! i-atom inc) (nth-arg @i-atom))
                  (throw (parse-error parser "expected one argument")))))

            (collect-values [i-atom stop-at-optional?]
              (let [vals (atom [])]
                (loop []
                  (let [ni (inc @i-atom)]
                    (when (and (< ni (n))
                               (not (and stop-at-optional?
                                         (looks-like-optional? (nth-arg ni) prefix-chars has-neg-num (set (keys option-map))))))
                      (swap! i-atom inc)
                      (swap! vals conj (nth-arg @i-atom))
                      (recur))))
                @vals))

            (process-optional! [opt-arg i-atom]
              (let [nargs (:nargs opt-arg)
                    type-fn (:type opt-arg)
                    action (:action opt-arg)]
                (cond
                  (contains? #{"store_true" "store_false" "store_const" "count" "append_const"} action)
                  (apply-action! ns-atom provided-atom opt-arg nil)

                  (nil? nargs)
                  (let [raw (take-next-value i-atom)
                        val (convert-value type-fn raw (:dest opt-arg))]
                    (apply-action! ns-atom provided-atom opt-arg val))

                  (= nargs OPTIONAL)
                  (let [ni (inc @i-atom)]
                    (if (and (< ni (n))
                             (not (looks-like-optional? (nth-arg ni) prefix-chars has-neg-num (set (keys option-map)))))
                      (do (swap! i-atom inc)
                          (apply-action! ns-atom provided-atom opt-arg (convert-value type-fn (nth-arg @i-atom) (:dest opt-arg))))
                      (apply-action! ns-atom provided-atom opt-arg (:const opt-arg))))

                  (= nargs ZERO_OR_MORE)
                  (let [vals (collect-values i-atom true)]
                    (apply-action! ns-atom provided-atom opt-arg (mapv #(convert-value type-fn % (:dest opt-arg)) vals)))

                  (= nargs ONE_OR_MORE)
                  (let [ni (inc @i-atom)]
                    (if (or (>= ni (n))
                            (looks-like-optional? (nth-arg ni) prefix-chars has-neg-num (set (keys option-map))))
                      (throw (parse-error parser "expected at least one argument"))
                      (let [vals (collect-values i-atom true)]
                        (apply-action! ns-atom provided-atom opt-arg (mapv #(convert-value type-fn % (:dest opt-arg)) vals)))))

                  (= nargs REMAINDER)
                  (let [vals (collect-values i-atom false)]
                    (apply-action! ns-atom provided-atom opt-arg vals))

                  (integer? nargs)
                  (let [vals (atom [])]
                    (dotimes [_ nargs]
                      (swap! vals conj (convert-value type-fn (take-next-value i-atom) (:dest opt-arg))))
                    (apply-action! ns-atom provided-atom opt-arg @vals)))))

            (min-args-needed [from-pos-idx]
              (reduce (fn [total arg]
                        (let [nargs (:nargs arg)]
                          (+ total (cond
                                     (nil? nargs) 1
                                     (integer? nargs) nargs
                                     (= nargs ONE_OR_MORE) 1
                                     :else 0))))
                      0
                      (drop from-pos-idx positionals)))

            (process-positional! [pos-arg i-atom]
              (let [nargs (:nargs pos-arg)
                    type-fn (:type pos-arg)]
                (cond
                  (nil? nargs)
                  (let [current-str (nth-arg @i-atom)
                        action (:action pos-arg)]
                    (swap! i-atom inc)
                    (apply-action! ns-atom provided-atom pos-arg (convert-value type-fn current-str (:dest pos-arg)))
                    (when-not (= action "append")
                      (swap! pos-idx inc)))

                  (= nargs OPTIONAL)
                  (let [future-needed (min-args-needed (inc @pos-idx))]
                    (if (and (> future-needed 0)
                             (>= (+ @i-atom future-needed) (n)))
                      (do (apply-action! ns-atom provided-atom pos-arg (:default pos-arg))
                          (swap! pos-idx inc))
                      (let [current-str (nth-arg @i-atom)]
                        (swap! i-atom inc)
                        (apply-action! ns-atom provided-atom pos-arg (convert-value type-fn current-str (:dest pos-arg)))
                        (swap! pos-idx inc))))

                  (= nargs ZERO_OR_MORE)
                  (let [future-needed (min-args-needed (inc @pos-idx))
                        vals (atom [])]
                    (loop []
                      (when (and (< (+ @i-atom future-needed) (n))
                                 (not (looks-like-optional? (nth-arg @i-atom) prefix-chars has-neg-num (set (keys option-map)))))
                        (swap! vals conj (nth-arg @i-atom))
                        (swap! i-atom inc)
                        (recur)))
                    (apply-action! ns-atom provided-atom pos-arg (mapv #(convert-value type-fn % (:dest pos-arg)) @vals))
                    (swap! pos-idx inc))

                  (= nargs ONE_OR_MORE)
                  (let [current-str (nth-arg @i-atom)
                        _ (swap! i-atom inc)
                        future-needed (min-args-needed (inc @pos-idx))
                        vals (atom [current-str])]
                    (loop []
                      (when (and (< (+ @i-atom future-needed) (n))
                                 (not (looks-like-optional? (nth-arg @i-atom) prefix-chars has-neg-num (set (keys option-map)))))
                        (swap! vals conj (nth-arg @i-atom))
                        (swap! i-atom inc)
                        (recur)))
                    (apply-action! ns-atom provided-atom pos-arg (mapv #(convert-value type-fn % (:dest pos-arg)) @vals))
                    (swap! pos-idx inc))

                  (= nargs REMAINDER)
                  (let [vals (atom [])]
                    (loop []
                      (when (< @i-atom (n))
                        (swap! vals conj (nth-arg @i-atom))
                        (swap! i-atom inc)
                        (recur)))
                    (apply-action! ns-atom provided-atom pos-arg @vals)
                    (swap! pos-idx inc))

                  (integer? nargs)
                  (let [vals (atom [])]
                    (dotimes [_ nargs]
                      (if (< @i-atom (n))
                        (do (swap! vals conj (nth-arg @i-atom))
                            (swap! i-atom inc))
                        (throw (parse-error parser (str "expected " nargs " arguments")))))
                    (apply-action! ns-atom provided-atom pos-arg (mapv #(convert-value type-fn % (:dest pos-arg)) @vals))
                    (swap! pos-idx inc)))))]

      (let [i-atom (atom 0)]
        (while (< @i-atom (n))
          (let [arg-str (nth-arg @i-atom)]
            (cond
              (= arg-str "--")
              (do
                (swap! i-atom inc)
                (let [remainder-pos (when (< @pos-idx (count positionals))
                                      (let [p (nth positionals @pos-idx)]
                                        (when (= (:nargs p) REMAINDER) p)))]
                  (if remainder-pos
                    (let [vals (atom [])]
                      (while (< @i-atom (n))
                        (swap! vals conj (nth-arg @i-atom))
                        (swap! i-atom inc))
                      (apply-action! ns-atom provided-atom remainder-pos @vals)
                      (swap! pos-idx inc))
                    (while (< @i-atom (n))
                      (swap! extras-atom conj (nth-arg @i-atom))
                      (swap! i-atom inc)))))

              (and (str/starts-with? arg-str "--")
                   (str/includes? arg-str "="))
              (let [eq-pos (.indexOf ^String arg-str "=")
                    opt-str (subs arg-str 0 eq-pos)
                    val-str (subs arg-str (inc eq-pos))
                    opt-arg (find-option opt-str)]
                (if opt-arg
                  (if (contains? #{"store_true" "store_false" "store_const" "count" "append_const"} (:action opt-arg))
                    (throw (parse-error parser (str "argument " opt-str ": ignored explicit argument '" val-str "'")))
                    (let [type-fn (:type opt-arg)
                          converted (convert-value type-fn val-str (:dest opt-arg))]
                      (apply-action! ns-atom provided-atom opt-arg converted)
                      (swap! i-atom inc)))
                  (do (swap! extras-atom conj arg-str) (swap! i-atom inc))))

              (and (looks-like-optional? arg-str prefix-chars has-neg-num (set (keys option-map)))
                   (str/starts-with? arg-str "--"))
              (let [opt-arg (find-option arg-str)]
                (if opt-arg
                  (do (process-optional! opt-arg i-atom)
                      (swap! i-atom inc))
                  (do (swap! extras-atom conj arg-str) (swap! i-atom inc))))

              (and (looks-like-optional? arg-str prefix-chars has-neg-num (set (keys option-map)))
                   (not (str/starts-with? arg-str "--"))
                   (> (count arg-str) 2))
              (let [full-opt-arg (find-option arg-str)
                    matched-prefix (when-not full-opt-arg
                                     (first (for [len (range (dec (count arg-str)) 1 -1)
                                                  :let [prefix (subs arg-str 0 len)
                                                        opt (find-option prefix)]
                                                  :when opt]
                                              [prefix opt])))
                    opt-arg (or full-opt-arg (second matched-prefix))
                    match-len (if full-opt-arg (count arg-str) (count (first matched-prefix)))]
                (if opt-arg
                  (if full-opt-arg
                    (do (process-optional! opt-arg i-atom)
                        (swap! i-atom inc))
                    (let [action (:action opt-arg)]
                      (if (contains? #{"store_true" "store_false" "store_const" "count" "append_const"} action)
                        (let [remainder (str (first prefix-chars) (subs arg-str match-len))]
                          (apply-action! ns-atom provided-atom opt-arg nil)
                          (swap! arg-vec-atom assoc @i-atom remainder))
                        (let [val-str (subs arg-str match-len)
                              type-fn (:type opt-arg)
                              converted (convert-value type-fn val-str (:dest opt-arg))]
                          (apply-action! ns-atom provided-atom opt-arg converted)
                          (swap! i-atom inc)))))
                  (do (swap! extras-atom conj arg-str) (swap! i-atom inc))))

              (and (looks-like-optional? arg-str prefix-chars has-neg-num (set (keys option-map)))
                   (not (str/starts-with? arg-str "--")))
              (let [opt-arg (find-option arg-str)]
                (if opt-arg
                  (do (process-optional! opt-arg i-atom)
                      (swap! i-atom inc))
                  (do (swap! extras-atom conj arg-str) (swap! i-atom inc))))

              :else
              (let [pos-arg (when (< @pos-idx (count positionals))
                              (nth positionals @pos-idx))]
                (if pos-arg
                  (process-positional! pos-arg i-atom)
                  (do (swap! extras-atom conj arg-str) (swap! i-atom inc)))))))

        (let [processed-pos-count @pos-idx]
          (doseq [[idx arg] (map-indexed vector positionals)]
            (let [dest (keyword (:dest arg))
                  nargs (:nargs arg)
                  action (:action arg)]
              (when (and (> idx (dec processed-pos-count))
                         (not (contains? #{ZERO_OR_MORE OPTIONAL REMAINDER} nargs))
                         (not (contains? @provided-atom (keyword dest))))
                (throw (parse-error parser (str "the following arguments are required: " (:dest arg)))))))
          (doseq [arg optionals]
            (when (:required arg)
              (let [dest (keyword (:dest arg))]
                (when-not (contains? @provided-atom dest)
                  (throw (parse-error parser (str "the following arguments are required: " (first (:option-strings arg))))))))))
        [@ns-atom @extras-atom]))))

(defn parse-args [parser args]
  (let [[ns extras] (parse-known-args parser args)]
    (when (seq extras)
      (throw (parse-error parser (str "unrecognized arguments: " (str/join " " extras)))))
    ns))

(defn- check-parse [parser args expected]
  (let [result (parse-args parser args)]
    (when (not= result expected)
      (throw (ex-info (str "Expected " expected " but got " result " for args " args)
                      {:expected expected :got result :args args})))))

(defn- check-parse-fails [parser args]
  (let [threw? (try (parse-args parser args) false
                    (catch Exception _ true))]
    (when-not threw?
      (throw (ex-info (str "Expected failure but succeeded for args " args)
                      {:args args})))))

(defn- run-checks [parser successes failures]
  (doseq [[args expected] successes]
    (check-parse parser args expected))
  (doseq [args failures]
    (check-parse-fails parser args)))

(defn- test-dispatch-optionals-1 [test-id]
  (case test-id
    (:argparse/optionals-single-dash-test-failures-no-groups-listargs
     :argparse/optionals-single-dash-test-successes-no-groups-listargs
     :argparse/optionals-single-dash-test-failures-no-groups-sysargs
     :argparse/optionals-single-dash-test-successes-no-groups-sysargs
     :argparse/optionals-single-dash-test-failures-one-group-listargs
     :argparse/optionals-single-dash-test-successes-one-group-listargs
     :argparse/optionals-single-dash-test-failures-one-group-sysargs
     :argparse/optionals-single-dash-test-successes-one-group-sysargs
     :argparse/optionals-single-dash-test-failures-many-groups-listargs
     :argparse/optionals-single-dash-test-successes-many-groups-listargs
     :argparse/optionals-single-dash-test-failures-many-groups-sysargs
     :argparse/optionals-single-dash-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-x")
                     (add-argument "-y"))
          successes [[["-x" "X"] {:x "X" :y nil}]
                     [["-y" "Y"] {:x nil :y "Y"}]
                     [["-x" "X" "-y" "Y"] {:x "X" :y "Y"}]]
          failures [["-x"] ["-y"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-single-dash-combined-test-failures-no-groups-listargs
     :argparse/optionals-single-dash-combined-test-successes-no-groups-listargs
     :argparse/optionals-single-dash-combined-test-failures-no-groups-sysargs
     :argparse/optionals-single-dash-combined-test-successes-no-groups-sysargs
     :argparse/optionals-single-dash-combined-test-failures-one-group-listargs
     :argparse/optionals-single-dash-combined-test-successes-one-group-listargs
     :argparse/optionals-single-dash-combined-test-failures-one-group-sysargs
     :argparse/optionals-single-dash-combined-test-successes-one-group-sysargs
     :argparse/optionals-single-dash-combined-test-failures-many-groups-listargs
     :argparse/optionals-single-dash-combined-test-successes-many-groups-listargs
     :argparse/optionals-single-dash-combined-test-failures-many-groups-sysargs
     :argparse/optionals-single-dash-combined-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-x" :action "store_true")
                     (add-argument "-y" :action "store_true"))
          successes [[["-xy"] {:x true :y true}]
                     [["-yx"] {:x true :y true}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-single-dash-long-test-failures-no-groups-listargs
     :argparse/optionals-single-dash-long-test-successes-no-groups-listargs
     :argparse/optionals-single-dash-long-test-failures-no-groups-sysargs
     :argparse/optionals-single-dash-long-test-successes-no-groups-sysargs
     :argparse/optionals-single-dash-long-test-failures-one-group-listargs
     :argparse/optionals-single-dash-long-test-successes-one-group-listargs
     :argparse/optionals-single-dash-long-test-failures-one-group-sysargs
     :argparse/optionals-single-dash-long-test-successes-one-group-sysargs
     :argparse/optionals-single-dash-long-test-failures-many-groups-listargs
     :argparse/optionals-single-dash-long-test-successes-many-groups-listargs
     :argparse/optionals-single-dash-long-test-failures-many-groups-sysargs
     :argparse/optionals-single-dash-long-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-foo"))
          successes [[["-foo" "a"] {:foo "a"}]
                     [["-fooa"] {:foo "a"}]]
          failures [["-foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-double-dash-test-failures-no-groups-listargs
     :argparse/optionals-double-dash-test-successes-no-groups-listargs
     :argparse/optionals-double-dash-test-failures-no-groups-sysargs
     :argparse/optionals-double-dash-test-successes-no-groups-sysargs
     :argparse/optionals-double-dash-test-failures-one-group-listargs
     :argparse/optionals-double-dash-test-successes-one-group-listargs
     :argparse/optionals-double-dash-test-failures-one-group-sysargs
     :argparse/optionals-double-dash-test-successes-one-group-sysargs
     :argparse/optionals-double-dash-test-failures-many-groups-listargs
     :argparse/optionals-double-dash-test-successes-many-groups-listargs
     :argparse/optionals-double-dash-test-failures-many-groups-sysargs
     :argparse/optionals-double-dash-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "--bar"))
          successes [[["--foo" "X"] {:foo "X" :bar nil}]
                     [["--bar" "Y"] {:foo nil :bar "Y"}]
                     [["--foo" "X" "--bar" "Y"] {:foo "X" :bar "Y"}]]
          failures [["--foo"] ["--bar"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-double-dash-partial-match-test-failures-no-groups-listargs
     :argparse/optionals-double-dash-partial-match-test-successes-no-groups-listargs
     :argparse/optionals-double-dash-partial-match-test-failures-no-groups-sysargs
     :argparse/optionals-double-dash-partial-match-test-successes-no-groups-sysargs
     :argparse/optionals-double-dash-partial-match-test-failures-one-group-listargs
     :argparse/optionals-double-dash-partial-match-test-successes-one-group-listargs
     :argparse/optionals-double-dash-partial-match-test-failures-one-group-sysargs
     :argparse/optionals-double-dash-partial-match-test-successes-one-group-sysargs
     :argparse/optionals-double-dash-partial-match-test-failures-many-groups-listargs
     :argparse/optionals-double-dash-partial-match-test-successes-many-groups-listargs
     :argparse/optionals-double-dash-partial-match-test-failures-many-groups-sysargs
     :argparse/optionals-double-dash-partial-match-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--badger")
                     (add-argument "--bat"))
          successes [[["--bat" "X"] {:badger nil :bat "X"}]
                     [["--bad" "X"] {:badger "X" :bat nil}]]
          failures [[["--b" "X"]]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] (map first failures))
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-double-dash-prefix-match-test-failures-no-groups-listargs
     :argparse/optionals-double-dash-prefix-match-test-successes-no-groups-listargs
     :argparse/optionals-double-dash-prefix-match-test-failures-no-groups-sysargs
     :argparse/optionals-double-dash-prefix-match-test-successes-no-groups-sysargs
     :argparse/optionals-double-dash-prefix-match-test-failures-one-group-listargs
     :argparse/optionals-double-dash-prefix-match-test-successes-one-group-listargs
     :argparse/optionals-double-dash-prefix-match-test-failures-one-group-sysargs
     :argparse/optionals-double-dash-prefix-match-test-successes-one-group-sysargs
     :argparse/optionals-double-dash-prefix-match-test-failures-many-groups-listargs
     :argparse/optionals-double-dash-prefix-match-test-successes-many-groups-listargs
     :argparse/optionals-double-dash-prefix-match-test-failures-many-groups-sysargs
     :argparse/optionals-double-dash-prefix-match-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "--foobar"))
          successes [[["--foo" "X"] {:foo "X" :foobar nil}]
                     [["--foob" "X"] {:foo nil :foobar "X"}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-single-double-dash-test-failures-no-groups-listargs
     :argparse/optionals-single-double-dash-test-successes-no-groups-listargs
     :argparse/optionals-single-double-dash-test-failures-no-groups-sysargs
     :argparse/optionals-single-double-dash-test-successes-no-groups-sysargs
     :argparse/optionals-single-double-dash-test-failures-one-group-listargs
     :argparse/optionals-single-double-dash-test-successes-one-group-listargs
     :argparse/optionals-single-double-dash-test-failures-one-group-sysargs
     :argparse/optionals-single-double-dash-test-successes-one-group-sysargs
     :argparse/optionals-single-double-dash-test-failures-many-groups-listargs
     :argparse/optionals-single-double-dash-test-successes-many-groups-listargs
     :argparse/optionals-single-double-dash-test-failures-many-groups-sysargs
     :argparse/optionals-single-double-dash-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-f" "--foo")
                     (add-argument "-b" "--bar"))
          successes [[["-f" "X"] {:foo "X" :bar nil}]
                     [["--foo" "X"] {:foo "X" :bar nil}]
                     [["-b" "Y"] {:foo nil :bar "Y"}]
                     [["--bar" "Y"] {:foo nil :bar "Y"}]
                     [["-f" "X" "-b" "Y"] {:foo "X" :bar "Y"}]]
          failures [["-f"] ["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-short-long-test-failures-no-groups-listargs
     :argparse/optionals-short-long-test-successes-no-groups-listargs
     :argparse/optionals-short-long-test-failures-no-groups-sysargs
     :argparse/optionals-short-long-test-successes-no-groups-sysargs
     :argparse/optionals-short-long-test-failures-one-group-listargs
     :argparse/optionals-short-long-test-successes-one-group-listargs
     :argparse/optionals-short-long-test-failures-one-group-sysargs
     :argparse/optionals-short-long-test-successes-one-group-sysargs
     :argparse/optionals-short-long-test-failures-many-groups-listargs
     :argparse/optionals-short-long-test-successes-many-groups-listargs
     :argparse/optionals-short-long-test-failures-many-groups-sysargs
     :argparse/optionals-short-long-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-v" "--verbose" :action "store_true"))
          successes [[[] {:verbose false}]
                     [["-v"] {:verbose true}]
                     [["--verbose"] {:verbose true}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-dest-test-failures-no-groups-listargs
     :argparse/optionals-dest-test-successes-no-groups-listargs
     :argparse/optionals-dest-test-failures-no-groups-sysargs
     :argparse/optionals-dest-test-successes-no-groups-sysargs
     :argparse/optionals-dest-test-failures-one-group-listargs
     :argparse/optionals-dest-test-successes-one-group-listargs
     :argparse/optionals-dest-test-failures-one-group-sysargs
     :argparse/optionals-dest-test-successes-one-group-sysargs
     :argparse/optionals-dest-test-failures-many-groups-listargs
     :argparse/optionals-dest-test-successes-many-groups-listargs
     :argparse/optionals-dest-test-failures-many-groups-sysargs
     :argparse/optionals-dest-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :dest "bar"))
          successes [[["--foo" "X"] {:bar "X"}]
                     [[] {:bar nil}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-default-test-failures-no-groups-listargs
     :argparse/optionals-default-test-successes-no-groups-listargs
     :argparse/optionals-default-test-failures-no-groups-sysargs
     :argparse/optionals-default-test-successes-no-groups-sysargs
     :argparse/optionals-default-test-failures-one-group-listargs
     :argparse/optionals-default-test-successes-one-group-listargs
     :argparse/optionals-default-test-failures-one-group-sysargs
     :argparse/optionals-default-test-successes-one-group-sysargs
     :argparse/optionals-default-test-failures-many-groups-listargs
     :argparse/optionals-default-test-successes-many-groups-listargs
     :argparse/optionals-default-test-failures-many-groups-sysargs
     :argparse/optionals-default-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :default "badger"))
          successes [[[] {:foo "badger"}]
                     [["--foo" "spam"] {:foo "spam"}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-optionals-1b [test-id]
  (case test-id
    (:argparse/optionals-nargs-default-test-failures-no-groups-listargs
     :argparse/optionals-nargs-default-test-successes-no-groups-listargs
     :argparse/optionals-nargs-default-test-failures-no-groups-sysargs
     :argparse/optionals-nargs-default-test-successes-no-groups-sysargs
     :argparse/optionals-nargs-default-test-failures-one-group-listargs
     :argparse/optionals-nargs-default-test-successes-one-group-listargs
     :argparse/optionals-nargs-default-test-failures-one-group-sysargs
     :argparse/optionals-nargs-default-test-successes-one-group-sysargs
     :argparse/optionals-nargs-default-test-failures-many-groups-listargs
     :argparse/optionals-nargs-default-test-successes-many-groups-listargs
     :argparse/optionals-nargs-default-test-failures-many-groups-sysargs
     :argparse/optionals-nargs-default-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs OPTIONAL))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo nil}]
                     [["--foo" "a"] {:foo "a"}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-nargs1-test-failures-no-groups-listargs
     :argparse/optionals-nargs1-test-successes-no-groups-listargs
     :argparse/optionals-nargs1-test-failures-no-groups-sysargs
     :argparse/optionals-nargs1-test-successes-no-groups-sysargs
     :argparse/optionals-nargs1-test-failures-one-group-listargs
     :argparse/optionals-nargs1-test-successes-one-group-listargs
     :argparse/optionals-nargs1-test-failures-one-group-sysargs
     :argparse/optionals-nargs1-test-successes-one-group-sysargs
     :argparse/optionals-nargs1-test-failures-many-groups-listargs
     :argparse/optionals-nargs1-test-successes-many-groups-listargs
     :argparse/optionals-nargs1-test-failures-many-groups-sysargs
     :argparse/optionals-nargs1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs 1))
          successes [[["--foo" "a"] {:foo ["a"]}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-nargs3-test-failures-no-groups-listargs
     :argparse/optionals-nargs3-test-successes-no-groups-listargs
     :argparse/optionals-nargs3-test-failures-no-groups-sysargs
     :argparse/optionals-nargs3-test-successes-no-groups-sysargs
     :argparse/optionals-nargs3-test-failures-one-group-listargs
     :argparse/optionals-nargs3-test-successes-one-group-listargs
     :argparse/optionals-nargs3-test-failures-one-group-sysargs
     :argparse/optionals-nargs3-test-successes-one-group-sysargs
     :argparse/optionals-nargs3-test-failures-many-groups-listargs
     :argparse/optionals-nargs3-test-successes-many-groups-listargs
     :argparse/optionals-nargs3-test-failures-many-groups-sysargs
     :argparse/optionals-nargs3-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs 3))
          successes [[["--foo" "a" "b" "c"] {:foo ["a" "b" "c"]}]]
          failures [["--foo"] ["--foo" "a"] ["--foo" "a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-nargs-optional-test-failures-no-groups-listargs
     :argparse/optionals-nargs-optional-test-successes-no-groups-listargs
     :argparse/optionals-nargs-optional-test-failures-no-groups-sysargs
     :argparse/optionals-nargs-optional-test-successes-no-groups-sysargs
     :argparse/optionals-nargs-optional-test-failures-one-group-listargs
     :argparse/optionals-nargs-optional-test-successes-one-group-listargs
     :argparse/optionals-nargs-optional-test-failures-one-group-sysargs
     :argparse/optionals-nargs-optional-test-successes-one-group-sysargs
     :argparse/optionals-nargs-optional-test-failures-many-groups-listargs
     :argparse/optionals-nargs-optional-test-successes-many-groups-listargs
     :argparse/optionals-nargs-optional-test-failures-many-groups-sysargs
     :argparse/optionals-nargs-optional-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs OPTIONAL :const "c" :default "d"))
          successes [[[] {:foo "d"}]
                     [["--foo"] {:foo "c"}]
                     [["--foo" "a"] {:foo "a"}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-nargs-zero-or-more-test-failures-no-groups-listargs
     :argparse/optionals-nargs-zero-or-more-test-successes-no-groups-listargs
     :argparse/optionals-nargs-zero-or-more-test-failures-no-groups-sysargs
     :argparse/optionals-nargs-zero-or-more-test-successes-no-groups-sysargs
     :argparse/optionals-nargs-zero-or-more-test-failures-one-group-listargs
     :argparse/optionals-nargs-zero-or-more-test-successes-one-group-listargs
     :argparse/optionals-nargs-zero-or-more-test-failures-one-group-sysargs
     :argparse/optionals-nargs-zero-or-more-test-successes-one-group-sysargs
     :argparse/optionals-nargs-zero-or-more-test-failures-many-groups-listargs
     :argparse/optionals-nargs-zero-or-more-test-successes-many-groups-listargs
     :argparse/optionals-nargs-zero-or-more-test-failures-many-groups-sysargs
     :argparse/optionals-nargs-zero-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs ZERO_OR_MORE))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo []}]
                     [["--foo" "a"] {:foo ["a"]}]
                     [["--foo" "a" "b"] {:foo ["a" "b"]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-nargs-one-or-more-test-failures-no-groups-listargs
     :argparse/optionals-nargs-one-or-more-test-successes-no-groups-listargs
     :argparse/optionals-nargs-one-or-more-test-failures-no-groups-sysargs
     :argparse/optionals-nargs-one-or-more-test-successes-no-groups-sysargs
     :argparse/optionals-nargs-one-or-more-test-failures-one-group-listargs
     :argparse/optionals-nargs-one-or-more-test-successes-one-group-listargs
     :argparse/optionals-nargs-one-or-more-test-failures-one-group-sysargs
     :argparse/optionals-nargs-one-or-more-test-successes-one-group-sysargs
     :argparse/optionals-nargs-one-or-more-test-failures-many-groups-listargs
     :argparse/optionals-nargs-one-or-more-test-successes-many-groups-listargs
     :argparse/optionals-nargs-one-or-more-test-failures-many-groups-sysargs
     :argparse/optionals-nargs-one-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs ONE_OR_MORE))
          successes [[["--foo" "a"] {:foo ["a"]}]
                     [["--foo" "a" "b"] {:foo ["a" "b"]}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-action-store-true-test-failures-no-groups-listargs
     :argparse/optionals-action-store-true-test-successes-no-groups-listargs
     :argparse/optionals-action-store-true-test-failures-no-groups-sysargs
     :argparse/optionals-action-store-true-test-successes-no-groups-sysargs
     :argparse/optionals-action-store-true-test-failures-one-group-listargs
     :argparse/optionals-action-store-true-test-successes-one-group-listargs
     :argparse/optionals-action-store-true-test-failures-one-group-sysargs
     :argparse/optionals-action-store-true-test-successes-one-group-sysargs
     :argparse/optionals-action-store-true-test-failures-many-groups-listargs
     :argparse/optionals-action-store-true-test-successes-many-groups-listargs
     :argparse/optionals-action-store-true-test-failures-many-groups-sysargs
     :argparse/optionals-action-store-true-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_true"))
          successes [[[] {:foo false}]
                     [["--foo"] {:foo true}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-action-store-false-test-failures-no-groups-listargs
     :argparse/optionals-action-store-false-test-successes-no-groups-listargs
     :argparse/optionals-action-store-false-test-failures-no-groups-sysargs
     :argparse/optionals-action-store-false-test-successes-no-groups-sysargs
     :argparse/optionals-action-store-false-test-failures-one-group-listargs
     :argparse/optionals-action-store-false-test-successes-one-group-listargs
     :argparse/optionals-action-store-false-test-failures-one-group-sysargs
     :argparse/optionals-action-store-false-test-successes-one-group-sysargs
     :argparse/optionals-action-store-false-test-failures-many-groups-listargs
     :argparse/optionals-action-store-false-test-successes-many-groups-listargs
     :argparse/optionals-action-store-false-test-failures-many-groups-sysargs
     :argparse/optionals-action-store-false-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_false"))
          successes [[[] {:foo true}]
                     [["--foo"] {:foo false}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-action-store-const-test-failures-no-groups-listargs
     :argparse/optionals-action-store-const-test-successes-no-groups-listargs
     :argparse/optionals-action-store-const-test-failures-no-groups-sysargs
     :argparse/optionals-action-store-const-test-successes-no-groups-sysargs
     :argparse/optionals-action-store-const-test-failures-one-group-listargs
     :argparse/optionals-action-store-const-test-successes-one-group-listargs
     :argparse/optionals-action-store-const-test-failures-one-group-sysargs
     :argparse/optionals-action-store-const-test-successes-one-group-sysargs
     :argparse/optionals-action-store-const-test-failures-many-groups-listargs
     :argparse/optionals-action-store-const-test-successes-many-groups-listargs
     :argparse/optionals-action-store-const-test-failures-many-groups-sysargs
     :argparse/optionals-action-store-const-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_const" :const 42))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo 42}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-optionals-2 [test-id]
  (case test-id
    (:argparse/optionals-action-append-test-failures-no-groups-listargs
     :argparse/optionals-action-append-test-successes-no-groups-listargs
     :argparse/optionals-action-append-test-failures-no-groups-sysargs
     :argparse/optionals-action-append-test-successes-no-groups-sysargs
     :argparse/optionals-action-append-test-failures-one-group-listargs
     :argparse/optionals-action-append-test-successes-one-group-listargs
     :argparse/optionals-action-append-test-failures-one-group-sysargs
     :argparse/optionals-action-append-test-successes-one-group-sysargs
     :argparse/optionals-action-append-test-failures-many-groups-listargs
     :argparse/optionals-action-append-test-successes-many-groups-listargs
     :argparse/optionals-action-append-test-failures-many-groups-sysargs
     :argparse/optionals-action-append-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "append"))
          successes [[[] {:foo nil}]
                     [["--foo" "a"] {:foo ["a"]}]
                     [["--foo" "a" "--foo" "b"] {:foo ["a" "b"]}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-action-append-with-default-test-failures-no-groups-listargs
     :argparse/optionals-action-append-with-default-test-successes-no-groups-listargs
     :argparse/optionals-action-append-with-default-test-failures-no-groups-sysargs
     :argparse/optionals-action-append-with-default-test-successes-no-groups-sysargs
     :argparse/optionals-action-append-with-default-test-failures-one-group-listargs
     :argparse/optionals-action-append-with-default-test-successes-one-group-listargs
     :argparse/optionals-action-append-with-default-test-failures-one-group-sysargs
     :argparse/optionals-action-append-with-default-test-successes-one-group-sysargs
     :argparse/optionals-action-append-with-default-test-failures-many-groups-listargs
     :argparse/optionals-action-append-with-default-test-successes-many-groups-listargs
     :argparse/optionals-action-append-with-default-test-failures-many-groups-sysargs
     :argparse/optionals-action-append-with-default-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "append" :default ["X"]))
          successes [[[] {:foo ["X"]}]
                     [["--foo" "a"] {:foo ["X" "a"]}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-action-append-const-test-failures-no-groups-listargs
     :argparse/optionals-action-append-const-test-successes-no-groups-listargs
     :argparse/optionals-action-append-const-test-failures-no-groups-sysargs
     :argparse/optionals-action-append-const-test-successes-no-groups-sysargs
     :argparse/optionals-action-append-const-test-failures-one-group-listargs
     :argparse/optionals-action-append-const-test-successes-one-group-listargs
     :argparse/optionals-action-append-const-test-failures-one-group-sysargs
     :argparse/optionals-action-append-const-test-successes-one-group-sysargs
     :argparse/optionals-action-append-const-test-failures-many-groups-listargs
     :argparse/optionals-action-append-const-test-successes-many-groups-listargs
     :argparse/optionals-action-append-const-test-failures-many-groups-sysargs
     :argparse/optionals-action-append-const-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "append_const" :const 42))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo [42]}]
                     [["--foo" "--foo"] {:foo [42 42]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-action-append-const-with-default-test-failures-no-groups-listargs
     :argparse/optionals-action-append-const-with-default-test-successes-no-groups-listargs
     :argparse/optionals-action-append-const-with-default-test-failures-no-groups-sysargs
     :argparse/optionals-action-append-const-with-default-test-successes-no-groups-sysargs
     :argparse/optionals-action-append-const-with-default-test-failures-one-group-listargs
     :argparse/optionals-action-append-const-with-default-test-successes-one-group-listargs
     :argparse/optionals-action-append-const-with-default-test-failures-one-group-sysargs
     :argparse/optionals-action-append-const-with-default-test-successes-one-group-sysargs
     :argparse/optionals-action-append-const-with-default-test-failures-many-groups-listargs
     :argparse/optionals-action-append-const-with-default-test-successes-many-groups-listargs
     :argparse/optionals-action-append-const-with-default-test-failures-many-groups-sysargs
     :argparse/optionals-action-append-const-with-default-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "append_const" :const 42 :default ["X"]))
          successes [[[] {:foo ["X"]}]
                     [["--foo"] {:foo ["X" 42]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-action-count-test-failures-no-groups-listargs
     :argparse/optionals-action-count-test-successes-no-groups-listargs
     :argparse/optionals-action-count-test-failures-no-groups-sysargs
     :argparse/optionals-action-count-test-successes-no-groups-sysargs
     :argparse/optionals-action-count-test-failures-one-group-listargs
     :argparse/optionals-action-count-test-successes-one-group-listargs
     :argparse/optionals-action-count-test-failures-one-group-sysargs
     :argparse/optionals-action-count-test-successes-one-group-sysargs
     :argparse/optionals-action-count-test-failures-many-groups-listargs
     :argparse/optionals-action-count-test-successes-many-groups-listargs
     :argparse/optionals-action-count-test-failures-many-groups-sysargs
     :argparse/optionals-action-count-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "count"))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo 1}]
                     [["--foo" "--foo"] {:foo 2}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-allow-long-abbreviation-test-failures-no-groups-listargs
     :argparse/optionals-allow-long-abbreviation-test-successes-no-groups-listargs
     :argparse/optionals-allow-long-abbreviation-test-failures-no-groups-sysargs
     :argparse/optionals-allow-long-abbreviation-test-successes-no-groups-sysargs
     :argparse/optionals-allow-long-abbreviation-test-failures-one-group-listargs
     :argparse/optionals-allow-long-abbreviation-test-successes-one-group-listargs
     :argparse/optionals-allow-long-abbreviation-test-failures-one-group-sysargs
     :argparse/optionals-allow-long-abbreviation-test-successes-one-group-sysargs
     :argparse/optionals-allow-long-abbreviation-test-failures-many-groups-listargs
     :argparse/optionals-allow-long-abbreviation-test-successes-many-groups-listargs
     :argparse/optionals-allow-long-abbreviation-test-failures-many-groups-sysargs
     :argparse/optionals-allow-long-abbreviation-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :allow-abbrev true)
                     (add-argument "--foo"))
          successes [[["--foo" "a"] {:foo "a"}]
                     [["--fo" "a"] {:foo "a"}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-disallow-long-abbreviation-test-failures-no-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-test-successes-no-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-test-failures-no-groups-sysargs
     :argparse/optionals-disallow-long-abbreviation-test-successes-no-groups-sysargs
     :argparse/optionals-disallow-long-abbreviation-test-failures-one-group-listargs
     :argparse/optionals-disallow-long-abbreviation-test-successes-one-group-listargs
     :argparse/optionals-disallow-long-abbreviation-test-failures-one-group-sysargs
     :argparse/optionals-disallow-long-abbreviation-test-successes-one-group-sysargs
     :argparse/optionals-disallow-long-abbreviation-test-failures-many-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-test-successes-many-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-test-failures-many-groups-sysargs
     :argparse/optionals-disallow-long-abbreviation-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :allow-abbrev false)
                     (add-argument "--foo"))
          successes [[["--foo" "a"] {:foo "a"}]]
          failures [["--fo" "a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-positionals [test-id]
  (case test-id
    (:argparse/positionals-single-test-failures-no-groups-listargs
     :argparse/positionals-single-test-successes-no-groups-listargs
     :argparse/positionals-single-test-failures-no-groups-sysargs
     :argparse/positionals-single-test-successes-no-groups-sysargs
     :argparse/positionals-single-test-failures-one-group-listargs
     :argparse/positionals-single-test-successes-one-group-listargs
     :argparse/positionals-single-test-failures-one-group-sysargs
     :argparse/positionals-single-test-successes-one-group-sysargs
     :argparse/positionals-single-test-failures-many-groups-listargs
     :argparse/positionals-single-test-successes-many-groups-listargs
     :argparse/positionals-single-test-failures-many-groups-sysargs
     :argparse/positionals-single-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo"))
          successes [[["a"] {:foo "a"}]]
          failures [[] ["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-many-test-failures-no-groups-listargs
     :argparse/positionals-many-test-successes-no-groups-listargs
     :argparse/positionals-many-test-failures-no-groups-sysargs
     :argparse/positionals-many-test-successes-no-groups-sysargs
     :argparse/positionals-many-test-failures-one-group-listargs
     :argparse/positionals-many-test-successes-one-group-listargs
     :argparse/positionals-many-test-failures-one-group-sysargs
     :argparse/positionals-many-test-successes-one-group-sysargs
     :argparse/positionals-many-test-failures-many-groups-listargs
     :argparse/positionals-many-test-successes-many-groups-listargs
     :argparse/positionals-many-test-failures-many-groups-sysargs
     :argparse/positionals-many-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo")
                     (add-argument "bar"))
          successes [[["a" "b"] {:foo "a" :bar "b"}]]
          failures [[] ["a"] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-zero-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs-zero-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs-zero-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs ZERO_OR_MORE))
          successes [[[] {:foo []}]
                     [["a"] {:foo ["a"]}]
                     [["a" "b"] {:foo ["a" "b"]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-one-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs-one-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs-one-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-one-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-one-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs-one-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs-one-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs-one-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs-one-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs-one-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs-one-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-one-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs ONE_OR_MORE))
          successes [[["a"] {:foo ["a"]}]
                     [["a" "b"] {:foo ["a" "b"]}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-optional-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs OPTIONAL :default "d"))
          successes [[[] {:foo "d"}]
                     [["a"] {:foo "a"}]]
          failures [["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-integer-test-failures-no-groups-listargs
     :argparse/positionals-nargs-integer-test-successes-no-groups-listargs
     :argparse/positionals-nargs-integer-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-integer-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-integer-test-failures-one-group-listargs
     :argparse/positionals-nargs-integer-test-successes-one-group-listargs
     :argparse/positionals-nargs-integer-test-failures-one-group-sysargs
     :argparse/positionals-nargs-integer-test-successes-one-group-sysargs
     :argparse/positionals-nargs-integer-test-failures-many-groups-listargs
     :argparse/positionals-nargs-integer-test-successes-many-groups-listargs
     :argparse/positionals-nargs-integer-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-integer-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs 3))
          successes [[["a" "b" "c"] {:foo ["a" "b" "c"]}]]
          failures [[] ["a"] ["a" "b"] ["a" "b" "c" "d"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-combined [test-id]
  (case test-id
    (:argparse/optionals-and-positionals-no-optionals-test-failures-no-groups-listargs
     :argparse/optionals-and-positionals-no-optionals-test-successes-no-groups-listargs
     :argparse/optionals-and-positionals-no-optionals-test-failures-no-groups-sysargs
     :argparse/optionals-and-positionals-no-optionals-test-successes-no-groups-sysargs
     :argparse/optionals-and-positionals-no-optionals-test-failures-one-group-listargs
     :argparse/optionals-and-positionals-no-optionals-test-successes-one-group-listargs
     :argparse/optionals-and-positionals-no-optionals-test-failures-one-group-sysargs
     :argparse/optionals-and-positionals-no-optionals-test-successes-one-group-sysargs
     :argparse/optionals-and-positionals-no-optionals-test-failures-many-groups-listargs
     :argparse/optionals-and-positionals-no-optionals-test-successes-many-groups-listargs
     :argparse/optionals-and-positionals-no-optionals-test-failures-many-groups-sysargs
     :argparse/optionals-and-positionals-no-optionals-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo")
                     (add-argument "bar"))
          successes [[["a" "b"] {:foo "a" :bar "b"}]]
          failures [[] ["a"] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-and-positionals-interleaved-test-failures-no-groups-listargs
     :argparse/optionals-and-positionals-interleaved-test-successes-no-groups-listargs
     :argparse/optionals-and-positionals-interleaved-test-failures-no-groups-sysargs
     :argparse/optionals-and-positionals-interleaved-test-successes-no-groups-sysargs
     :argparse/optionals-and-positionals-interleaved-test-failures-one-group-listargs
     :argparse/optionals-and-positionals-interleaved-test-successes-one-group-listargs
     :argparse/optionals-and-positionals-interleaved-test-failures-one-group-sysargs
     :argparse/optionals-and-positionals-interleaved-test-successes-one-group-sysargs
     :argparse/optionals-and-positionals-interleaved-test-failures-many-groups-listargs
     :argparse/optionals-and-positionals-interleaved-test-successes-many-groups-listargs
     :argparse/optionals-and-positionals-interleaved-test-failures-many-groups-sysargs
     :argparse/optionals-and-positionals-interleaved-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-x")
                     (add-argument "foo"))
          successes [[["a"] {:x nil :foo "a"}]
                     [["-x" "b" "a"] {:x "b" :foo "a"}]
                     [["a" "-x" "b"] {:x "b" :foo "a"}]]
          failures [[] ["-x"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/nargs-zero-or-more-test-failures-no-groups-listargs
     :argparse/nargs-zero-or-more-test-successes-no-groups-listargs
     :argparse/nargs-zero-or-more-test-failures-no-groups-sysargs
     :argparse/nargs-zero-or-more-test-successes-no-groups-sysargs
     :argparse/nargs-zero-or-more-test-failures-one-group-listargs
     :argparse/nargs-zero-or-more-test-successes-one-group-listargs
     :argparse/nargs-zero-or-more-test-failures-one-group-sysargs
     :argparse/nargs-zero-or-more-test-successes-one-group-sysargs
     :argparse/nargs-zero-or-more-test-failures-many-groups-listargs
     :argparse/nargs-zero-or-more-test-successes-many-groups-listargs
     :argparse/nargs-zero-or-more-test-failures-many-groups-sysargs
     :argparse/nargs-zero-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs ZERO_OR_MORE)
                     (add-argument "bar" :nargs ZERO_OR_MORE))
          successes [[[] {:foo nil :bar []}]
                     [["a"] {:foo nil :bar ["a"]}]
                     [["--foo" "a"] {:foo ["a"] :bar []}]
                     [["--foo" "a" "b"] {:foo ["a" "b"] :bar []}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/nargs-remainder-test-failures-no-groups-listargs
     :argparse/nargs-remainder-test-successes-no-groups-listargs
     :argparse/nargs-remainder-test-failures-no-groups-sysargs
     :argparse/nargs-remainder-test-successes-no-groups-sysargs
     :argparse/nargs-remainder-test-failures-one-group-listargs
     :argparse/nargs-remainder-test-successes-one-group-listargs
     :argparse/nargs-remainder-test-failures-one-group-sysargs
     :argparse/nargs-remainder-test-successes-one-group-sysargs
     :argparse/nargs-remainder-test-failures-many-groups-listargs
     :argparse/nargs-remainder-test-successes-many-groups-listargs
     :argparse/nargs-remainder-test-failures-many-groups-sysargs
     :argparse/nargs-remainder-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "bar" :nargs REMAINDER))
          successes [[[] {:foo nil :bar []}]
                     [["a" "b"] {:foo nil :bar ["a" "b"]}]
                     [["--foo" "x" "a" "b"] {:foo "x" :bar ["a" "b"]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/default-suppress-test-failures-no-groups-listargs
     :argparse/default-suppress-test-successes-no-groups-listargs
     :argparse/default-suppress-test-failures-no-groups-sysargs
     :argparse/default-suppress-test-successes-no-groups-sysargs
     :argparse/default-suppress-test-failures-one-group-listargs
     :argparse/default-suppress-test-successes-one-group-listargs
     :argparse/default-suppress-test-failures-one-group-sysargs
     :argparse/default-suppress-test-successes-one-group-sysargs
     :argparse/default-suppress-test-failures-many-groups-listargs
     :argparse/default-suppress-test-successes-many-groups-listargs
     :argparse/default-suppress-test-failures-many-groups-sysargs
     :argparse/default-suppress-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :default SUPPRESS))
          successes [[[] {}]
                     [["--foo" "a"] {:foo "a"}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/parser-default-suppress-test-failures-no-groups-listargs
     :argparse/parser-default-suppress-test-successes-no-groups-listargs
     :argparse/parser-default-suppress-test-failures-no-groups-sysargs
     :argparse/parser-default-suppress-test-successes-no-groups-sysargs
     :argparse/parser-default-suppress-test-failures-one-group-listargs
     :argparse/parser-default-suppress-test-successes-one-group-listargs
     :argparse/parser-default-suppress-test-failures-one-group-sysargs
     :argparse/parser-default-suppress-test-successes-one-group-sysargs
     :argparse/parser-default-suppress-test-failures-many-groups-listargs
     :argparse/parser-default-suppress-test-successes-many-groups-listargs
     :argparse/parser-default-suppress-test-failures-many-groups-sysargs
     :argparse/parser-default-suppress-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :argument-default SUPPRESS)
                     (add-argument "--foo")
                     (add-argument "--bar"))
          successes [[[] {}]
                     [["--foo" "a"] {:foo "a"}]
                     [["--foo" "a" "--bar" "b"] {:foo "a" :bar "b"}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/parser-default42-test-failures-no-groups-listargs
     :argparse/parser-default42-test-successes-no-groups-listargs
     :argparse/parser-default42-test-failures-no-groups-sysargs
     :argparse/parser-default42-test-successes-no-groups-sysargs
     :argparse/parser-default42-test-failures-one-group-listargs
     :argparse/parser-default42-test-successes-one-group-listargs
     :argparse/parser-default42-test-failures-one-group-sysargs
     :argparse/parser-default42-test-successes-one-group-sysargs
     :argparse/parser-default42-test-failures-many-groups-listargs
     :argparse/parser-default42-test-successes-many-groups-listargs
     :argparse/parser-default42-test-failures-many-groups-sysargs
     :argparse/parser-default42-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :argument-default 42)
                     (add-argument "--foo")
                     (add-argument "--bar"))
          successes [[[] {:foo 42 :bar 42}]
                     [["--foo" "a"] {:foo "a" :bar 42}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-single [test-id]
  (case test-id
    :argparse/test-get-default
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :default 42)
                     (add-argument "--bar"))]
      (assert (= 42 (get-default parser "foo")))
      (assert (nil? (get-default parser "bar")))
      (assert (nil? (get-default parser "baz")))
      test-id)

    :argparse/test-empty
    (let [parser (make-parser)]
      (assert (= {} (parse-args parser [])))
      test-id)

    :argparse/test-non-empty
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo nil} (parse-args parser [])))
      (assert (= {:foo "a"} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-optional
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo nil} (parse-args parser [])))
      (assert (= {:foo "a"} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-argument
    (let [parser (-> (make-parser)
                     (add-argument "foo"))]
      (assert (= {:foo "a"} (parse-args parser ["a"])))
      test-id)

    :argparse/test-optionals
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "--bar"))]
      (assert (= {:foo nil :bar nil} (parse-args parser [])))
      (assert (= {:foo "a" :bar nil} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-mixed
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "bar"))]
      (assert (= {:foo nil :bar "b"} (parse-args parser ["b"])))
      (assert (= {:foo "a" :bar "b"} (parse-args parser ["--foo" "a" "b"])))
      test-id)

    :argparse/test-zero-or-more-optional
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs ZERO_OR_MORE))]
      (assert (= {:foo nil} (parse-args parser [])))
      (assert (= {:foo []} (parse-args parser ["--foo"])))
      (assert (= {:foo ["a" "b"]} (parse-args parser ["--foo" "a" "b"])))
      test-id)

    :argparse/test-single-argument-option
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs 1))]
      (assert (= {:foo ["a"]} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-multiple-argument-option
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs 3))]
      (assert (= {:foo ["a" "b" "c"]} (parse-args parser ["--foo" "a" "b" "c"])))
      test-id)

    :argparse/test-multiple-double-dashes
    (let [parser (-> (make-parser)
                     (add-argument "foo")
                     (add-argument "bar"))]
      (assert (= {:foo "a" :bar "b"} (parse-args parser ["a" "b"])))
      test-id)

    :argparse/test-optional-remainder
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "bar" :nargs REMAINDER))]
      (assert (= {:foo nil :bar []} (parse-args parser [])))
      (assert (= {:foo nil :bar ["a" "b"]} (parse-args parser ["a" "b"])))
      test-id)

    :argparse/test-unrecognized-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))
          [ns extras] (parse-known-args parser ["--foo" "a" "--bar" "b"])]
      (assert (= {:foo "a"} ns))
      (assert (= ["--bar" "b"] extras))
      test-id)

    :argparse/test-required-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :required true))]
      (try
        (parse-args parser [])
        (assert false "Expected failure")
        (catch clojure.lang.ExceptionInfo _))
      (assert (= {:foo "a"} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-ambiguous-option
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "--foobar"))]
      (try
        (parse-args parser ["--fo" "a"])
        (assert false "Expected failure")
        (catch clojure.lang.ExceptionInfo _))
      (assert (= {:foo "a" :foobar nil} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-namespace
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo "a"} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-parser
    (let [parser (make-parser :prog "test" :description "test parser")]
      (assert (= "test" (:prog parser)))
      (assert (= "test parser" (:description parser)))
      test-id)

    :argparse/test-constructor
    (let [parser (make-parser)]
      (assert (map? parser))
      test-id)

    :argparse/test-bad-type
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :type (fn [_] (throw (Exception. "bad")))))]
      (try
        (parse-args parser ["--foo" "a"])
        (assert false "Expected failure")
        (catch Exception _))
      test-id)

    :argparse/test-user-defined-action
    (let [action (fn [ns-atom dest _arg value] (swap! ns-atom assoc dest (str "custom:" value)))
          parser (-> (make-parser)
                     (add-argument "--foo" :action action))]
      (assert (= {:foo "custom:a"} (parse-args parser ["--foo" "a"])))
      test-id)

    nil))

(defn- test-dispatch-single-2 [test-id]
  (case test-id
    :argparse/test-no-help
    (let [parser (make-parser :add-help false)]
      (assert (not (:add-help parser)))
      test-id)

    :argparse/test-version
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo "a"} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-misc
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :default "default_val"))]
      (assert (= {:foo "default_val"} (parse-args parser [])))
      test-id)

    :argparse/test-conflict-error
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (try
        (add-argument parser "--foo")
        (assert false "Expected conflict error")
        (catch clojure.lang.ExceptionInfo _))
      test-id)

    :argparse/test-resolve-error
    (let [parser (-> (make-parser :conflict-handler "resolve")
                     (add-argument "--foo")
                     (add-argument "--foo" :default "new"))]
      (assert (= {:foo "new"} (parse-args parser [])))
      test-id)

    :argparse/test-required-positional
    (let [parser (-> (make-parser)
                     (add-argument "foo"))]
      (try
        (parse-args parser [])
        (catch clojure.lang.ExceptionInfo _))
      (assert (= {:foo "a"} (parse-args parser ["a"])))
      test-id)

    :argparse/test-exit-on-error-with-good-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo "a"} (parse-args parser ["--foo" "a"])))
      test-id)

    :argparse/test-exit-on-error-with-bad-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (try
        (parse-args parser ["--foo"])
        (assert false "Expected failure")
        (catch clojure.lang.ExceptionInfo _))
      test-id)

    :argparse/test-basic
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "bar"))]
      (assert (= {:foo "a" :bar "b"} (parse-args parser ["--foo" "a" "b"])))
      test-id)

    :argparse/test-required-exclusive
    (let [parser (make-parser)]
      (add-mutually-exclusive-group parser :required true)
      test-id)

    :argparse/test-no-argument-actions
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_true")
                     (add-argument "--bar" :action "store_false")
                     (add-argument "--baz" :action "store_const" :const 42))]
      (assert (= {:foo false :bar true :baz nil} (parse-args parser [])))
      (assert (= {:foo true :bar false :baz 42} (parse-args parser ["--foo" "--bar" "--baz"])))
      test-id)

    :argparse/test-type-function-call-only-once
    (let [count-atom (atom 0)
          parser (-> (make-parser)
                     (add-argument "--foo" :type (fn [v] (swap! count-atom inc) (Integer/parseInt v))))]
      (parse-args parser ["--foo" "42"])
      (assert (= 1 @count-atom))
      test-id)

    :argparse/test-type-function-call-with-non-string-default
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :type #(Integer/parseInt %) :default 42))]
      (assert (= {:foo 42} (parse-args parser [])))
      (assert (= {:foo 1} (parse-args parser ["--foo" "1"])))
      test-id)

    :argparse/test-type-function-call-with-string-default
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :type #(Integer/parseInt %) :default "42"))]
      (assert (= {:foo "42"} (parse-args parser [])))
      (assert (= {:foo 1} (parse-args parser ["--foo" "1"])))
      test-id)

    :argparse/test-no-double-type-conversion-of-default
    (let [count-atom (atom 0)
          parser (-> (make-parser)
                     (add-argument "--foo" :type (fn [v] (swap! count-atom inc) v) :default "x"))]
      (parse-args parser [])
      (assert (= 0 @count-atom))
      test-id)

    :argparse/test-issue-15906
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :type #(Integer/parseInt %)))]
      (try
        (parse-args parser ["--foo" "invalid"])
        (assert false "Expected failure")
        (catch clojure.lang.ExceptionInfo _))
      test-id)

    :argparse/test-arguments-tuple
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs 2))]
      (assert (= {:foo ["a" "b"]} (parse-args parser ["--foo" "a" "b"])))
      test-id)

    :argparse/test-arguments-list
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs ONE_OR_MORE))]
      (assert (= {:foo ["a" "b"]} (parse-args parser ["--foo" "a" "b"])))
      test-id)

    :argparse/test-arguments-tuple-positional
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs 2))]
      (assert (= {:foo ["a" "b"]} (parse-args parser ["a" "b"])))
      test-id)

    :argparse/test-arguments-list-positional
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs ONE_OR_MORE))]
      (assert (= {:foo ["a" "b"]} (parse-args parser ["a" "b"])))
      test-id)

    nil))

(defn- test-dispatch-part3 [test-id]
  (case test-id
    (:argparse/optionals-single-dash-subset-ambiguous-test-failures-no-groups-listargs
     :argparse/optionals-single-dash-subset-ambiguous-test-successes-no-groups-listargs
     :argparse/optionals-single-dash-subset-ambiguous-test-failures-no-groups-sysargs
     :argparse/optionals-single-dash-subset-ambiguous-test-successes-no-groups-sysargs
     :argparse/optionals-single-dash-subset-ambiguous-test-failures-one-group-listargs
     :argparse/optionals-single-dash-subset-ambiguous-test-successes-one-group-listargs
     :argparse/optionals-single-dash-subset-ambiguous-test-failures-one-group-sysargs
     :argparse/optionals-single-dash-subset-ambiguous-test-successes-one-group-sysargs
     :argparse/optionals-single-dash-subset-ambiguous-test-failures-many-groups-listargs
     :argparse/optionals-single-dash-subset-ambiguous-test-successes-many-groups-listargs
     :argparse/optionals-single-dash-subset-ambiguous-test-failures-many-groups-sysargs
     :argparse/optionals-single-dash-subset-ambiguous-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-f" "--foo")
                     (add-argument "-foobar")
                     (add-argument "-foorab"))
          successes [[["--foo" "X"] {:foo "X" :foobar nil :foorab nil}]
                     [["-f" "X"] {:foo "X" :foobar nil :foorab nil}]
                     [["-foobar" "X"] {:foo nil :foobar "X" :foorab nil}]
                     [["-foorab" "X"] {:foo nil :foobar nil :foorab "X"}]]
          failures [["--foo"] ["-f"] ["-foobar"] ["-foorab"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-single-dash-ambiguous-test-failures-no-groups-listargs
     :argparse/optionals-single-dash-ambiguous-test-successes-no-groups-listargs
     :argparse/optionals-single-dash-ambiguous-test-failures-no-groups-sysargs
     :argparse/optionals-single-dash-ambiguous-test-successes-no-groups-sysargs
     :argparse/optionals-single-dash-ambiguous-test-failures-one-group-listargs
     :argparse/optionals-single-dash-ambiguous-test-successes-one-group-listargs
     :argparse/optionals-single-dash-ambiguous-test-failures-one-group-sysargs
     :argparse/optionals-single-dash-ambiguous-test-successes-one-group-sysargs
     :argparse/optionals-single-dash-ambiguous-test-failures-many-groups-listargs
     :argparse/optionals-single-dash-ambiguous-test-successes-many-groups-listargs
     :argparse/optionals-single-dash-ambiguous-test-failures-many-groups-sysargs
     :argparse/optionals-single-dash-ambiguous-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-x")
                     (add-argument "-x2"))
          successes [[["-x" "X"] {:x "X" :x2 nil}]
                     [["-x2" "X"] {:x nil :x2 "X"}]]
          failures [["-x"] ["-x2"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-numeric-test-failures-no-groups-listargs
     :argparse/optionals-numeric-test-successes-no-groups-listargs
     :argparse/optionals-numeric-test-failures-no-groups-sysargs
     :argparse/optionals-numeric-test-successes-no-groups-sysargs
     :argparse/optionals-numeric-test-failures-one-group-listargs
     :argparse/optionals-numeric-test-successes-one-group-listargs
     :argparse/optionals-numeric-test-failures-one-group-sysargs
     :argparse/optionals-numeric-test-successes-one-group-sysargs
     :argparse/optionals-numeric-test-failures-many-groups-listargs
     :argparse/optionals-numeric-test-successes-many-groups-listargs
     :argparse/optionals-numeric-test-failures-many-groups-sysargs
     :argparse/optionals-numeric-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-1" :dest "one"))
          successes [[["-1" "X"] {:one "X"}]]
          failures [["-1"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-alternate-prefix-chars-test-failures-no-groups-listargs
     :argparse/optionals-alternate-prefix-chars-test-successes-no-groups-listargs
     :argparse/optionals-alternate-prefix-chars-test-failures-no-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-test-successes-no-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-test-failures-one-group-listargs
     :argparse/optionals-alternate-prefix-chars-test-successes-one-group-listargs
     :argparse/optionals-alternate-prefix-chars-test-failures-one-group-sysargs
     :argparse/optionals-alternate-prefix-chars-test-successes-one-group-sysargs
     :argparse/optionals-alternate-prefix-chars-test-failures-many-groups-listargs
     :argparse/optionals-alternate-prefix-chars-test-successes-many-groups-listargs
     :argparse/optionals-alternate-prefix-chars-test-failures-many-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :prefix-chars "+:")
                     (add-argument "+f")
                     (add-argument "++bar")
                     (add-argument ":baz"))
          successes [[["+f" "X"] {:f "X" :bar nil :baz nil}]
                     [["++bar" "Y"] {:f nil :bar "Y" :baz nil}]
                     [[":baz" "Z"] {:f nil :bar nil :baz "Z"}]]
          failures [["+f"] ["++bar"] [":baz"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-alternate-prefix-chars-added-help-test-failures-no-groups-listargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-successes-no-groups-listargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-failures-no-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-successes-no-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-failures-one-group-listargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-successes-one-group-listargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-failures-one-group-sysargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-successes-one-group-sysargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-failures-many-groups-listargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-successes-many-groups-listargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-failures-many-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-added-help-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :prefix-chars "+:")
                     (add-argument "+f")
                     (add-argument "++bar"))
          successes [[["+f" "X"] {:f "X" :bar nil}]
                     [["++bar" "Y"] {:f nil :bar "Y"}]]
          failures [["+f"] ["++bar"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-choices-test-failures-no-groups-listargs
     :argparse/optionals-choices-test-successes-no-groups-listargs
     :argparse/optionals-choices-test-failures-no-groups-sysargs
     :argparse/optionals-choices-test-successes-no-groups-sysargs
     :argparse/optionals-choices-test-failures-one-group-listargs
     :argparse/optionals-choices-test-successes-one-group-listargs
     :argparse/optionals-choices-test-failures-one-group-sysargs
     :argparse/optionals-choices-test-successes-one-group-sysargs
     :argparse/optionals-choices-test-failures-many-groups-listargs
     :argparse/optionals-choices-test-successes-many-groups-listargs
     :argparse/optionals-choices-test-failures-many-groups-sysargs
     :argparse/optionals-choices-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :choices ["a" "b" "c"]))
          successes [[[] {:foo nil}]
                     [["--foo" "a"] {:foo "a"}]
                     [["--foo" "b"] {:foo "b"}]]
          failures [["--foo" "d"] ["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-required-test-failures-no-groups-listargs
     :argparse/optionals-required-test-successes-no-groups-listargs
     :argparse/optionals-required-test-failures-no-groups-sysargs
     :argparse/optionals-required-test-successes-no-groups-sysargs
     :argparse/optionals-required-test-failures-one-group-listargs
     :argparse/optionals-required-test-successes-one-group-listargs
     :argparse/optionals-required-test-failures-one-group-sysargs
     :argparse/optionals-required-test-successes-one-group-sysargs
     :argparse/optionals-required-test-failures-many-groups-listargs
     :argparse/optionals-required-test-successes-many-groups-listargs
     :argparse/optionals-required-test-failures-many-groups-sysargs
     :argparse/optionals-required-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :required true))
          successes [[["--foo" "a"] {:foo "a"}]]
          failures [[] ["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-choices-string-test-failures-no-groups-listargs
     :argparse/positionals-choices-string-test-successes-no-groups-listargs
     :argparse/positionals-choices-string-test-failures-no-groups-sysargs
     :argparse/positionals-choices-string-test-successes-no-groups-sysargs
     :argparse/positionals-choices-string-test-failures-one-group-listargs
     :argparse/positionals-choices-string-test-successes-one-group-listargs
     :argparse/positionals-choices-string-test-failures-one-group-sysargs
     :argparse/positionals-choices-string-test-successes-one-group-sysargs
     :argparse/positionals-choices-string-test-failures-many-groups-listargs
     :argparse/positionals-choices-string-test-successes-many-groups-listargs
     :argparse/positionals-choices-string-test-failures-many-groups-sysargs
     :argparse/positionals-choices-string-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "spam" :choices ["a" "b" "c"]))
          successes [[["a"] {:spam "a"}]
                     [["b"] {:spam "b"}]]
          failures [["d"] []]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-choices-int-test-failures-no-groups-listargs
     :argparse/positionals-choices-int-test-successes-no-groups-listargs
     :argparse/positionals-choices-int-test-failures-no-groups-sysargs
     :argparse/positionals-choices-int-test-successes-no-groups-sysargs
     :argparse/positionals-choices-int-test-failures-one-group-listargs
     :argparse/positionals-choices-int-test-successes-one-group-listargs
     :argparse/positionals-choices-int-test-failures-one-group-sysargs
     :argparse/positionals-choices-int-test-successes-one-group-sysargs
     :argparse/positionals-choices-int-test-failures-many-groups-listargs
     :argparse/positionals-choices-int-test-successes-many-groups-listargs
     :argparse/positionals-choices-int-test-failures-many-groups-sysargs
     :argparse/positionals-choices-int-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "spam" :type #(Integer/parseInt %) :choices [1 2 3]))
          successes [[["1"] {:spam 1}]
                     [["2"] {:spam 2}]]
          failures [["4"] []]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-pos-nargs [test-id]
  (case test-id
    (:argparse/positionals-nargs-none-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo"))
          successes [[["a"] {:foo "a"}]]
          failures [[] ["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none1-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none1-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none1-test-failures-one-group-listargs
     :argparse/positionals-nargs-none1-test-successes-one-group-listargs
     :argparse/positionals-nargs-none1-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none1-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none1-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none1-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo") (add-argument "bar" :nargs OPTIONAL :default "d"))
          successes [[["a"] {:foo "a" :bar "d"}]
                     [["a" "b"] {:foo "a" :bar "b"}]]
          failures [[] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none-none-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-none-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-none-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-none-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-none-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-none-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-none-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-none-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-none-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-none-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-none-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-none-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo") (add-argument "bar"))
          successes [[["a" "b"] {:foo "a" :bar "b"}]]
          failures [[] ["a"] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none-zero-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-zero-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-zero-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-zero-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-zero-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-zero-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-zero-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-zero-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo") (add-argument "bar" :nargs ZERO_OR_MORE))
          successes [[["a"] {:foo "a" :bar []}]
                     [["a" "b"] {:foo "a" :bar ["b"]}]
                     [["a" "b" "c"] {:foo "a" :bar ["b" "c"]}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none-zero-or-more1-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more1-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-zero-or-more1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-zero-or-more1-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-zero-or-more1-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-zero-or-more1-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-zero-or-more1-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-zero-or-more1-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more1-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-zero-or-more1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-zero-or-more1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs ZERO_OR_MORE) (add-argument "bar"))
          successes [[["a"] {:foo [] :bar "a"}]
                     [["a" "b"] {:foo ["a"] :bar "b"}]
                     [["a" "b" "c"] {:foo ["a" "b"] :bar "c"}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none-optional-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-optional-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-optional-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-optional-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-optional-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-optional-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-optional-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-optional-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-optional-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-optional-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-optional-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-optional-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo") (add-argument "bar" :nargs OPTIONAL :default "d"))
          successes [[["a"] {:foo "a" :bar "d"}]
                     [["a" "b"] {:foo "a" :bar "b"}]]
          failures [[] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none-optional1-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-optional1-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-optional1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-optional1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-optional1-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-optional1-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-optional1-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-optional1-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-optional1-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-optional1-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-optional1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-optional1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs OPTIONAL :default "d") (add-argument "bar"))
          successes [[["a"] {:foo "d" :bar "a"}]
                     [["a" "b"] {:foo "a" :bar "b"}]]
          failures [[] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none-one-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-one-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-one-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-one-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-one-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-one-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-one-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-one-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-one-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-one-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-one-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-one-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo") (add-argument "bar" :nargs ONE_OR_MORE))
          successes [[["a" "b"] {:foo "a" :bar ["b"]}]
                     [["a" "b" "c"] {:foo "a" :bar ["b" "c"]}]]
          failures [[] ["a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-none-one-or-more1-test-failures-no-groups-listargs
     :argparse/positionals-nargs-none-one-or-more1-test-successes-no-groups-listargs
     :argparse/positionals-nargs-none-one-or-more1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-none-one-or-more1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-none-one-or-more1-test-failures-one-group-listargs
     :argparse/positionals-nargs-none-one-or-more1-test-successes-one-group-listargs
     :argparse/positionals-nargs-none-one-or-more1-test-failures-one-group-sysargs
     :argparse/positionals-nargs-none-one-or-more1-test-successes-one-group-sysargs
     :argparse/positionals-nargs-none-one-or-more1-test-failures-many-groups-listargs
     :argparse/positionals-nargs-none-one-or-more1-test-successes-many-groups-listargs
     :argparse/positionals-nargs-none-one-or-more1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-none-one-or-more1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs ONE_OR_MORE) (add-argument "bar"))
          successes [[["a" "b"] {:foo ["a"] :bar "b"}]
                     [["a" "b" "c"] {:foo ["a" "b"] :bar "c"}]]
          failures [[] ["a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-one-or-more-none-test-failures-no-groups-listargs
     :argparse/positionals-nargs-one-or-more-none-test-successes-no-groups-listargs
     :argparse/positionals-nargs-one-or-more-none-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-one-or-more-none-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-one-or-more-none-test-failures-one-group-listargs
     :argparse/positionals-nargs-one-or-more-none-test-successes-one-group-listargs
     :argparse/positionals-nargs-one-or-more-none-test-failures-one-group-sysargs
     :argparse/positionals-nargs-one-or-more-none-test-successes-one-group-sysargs
     :argparse/positionals-nargs-one-or-more-none-test-failures-many-groups-listargs
     :argparse/positionals-nargs-one-or-more-none-test-successes-many-groups-listargs
     :argparse/positionals-nargs-one-or-more-none-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-one-or-more-none-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs ONE_OR_MORE) (add-argument "bar"))
          successes [[["a" "b"] {:foo ["a"] :bar "b"}]
                     [["a" "b" "c"] {:foo ["a" "b"] :bar "c"}]]
          failures [[] ["a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-one-or-more1-test-failures-no-groups-listargs
     :argparse/positionals-nargs-one-or-more1-test-successes-no-groups-listargs
     :argparse/positionals-nargs-one-or-more1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-one-or-more1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-one-or-more1-test-failures-one-group-listargs
     :argparse/positionals-nargs-one-or-more1-test-successes-one-group-listargs
     :argparse/positionals-nargs-one-or-more1-test-failures-one-group-sysargs
     :argparse/positionals-nargs-one-or-more1-test-successes-one-group-sysargs
     :argparse/positionals-nargs-one-or-more1-test-failures-many-groups-listargs
     :argparse/positionals-nargs-one-or-more1-test-successes-many-groups-listargs
     :argparse/positionals-nargs-one-or-more1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-one-or-more1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs ONE_OR_MORE))
          successes [[["a"] {:foo ["a"]}]
                     [["a" "b"] {:foo ["a" "b"]}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-optional1-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional1-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional1-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional1-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional1-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional1-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional1-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional1-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs OPTIONAL :default "d"))
          successes [[[] {:foo "d"}]
                     [["a"] {:foo "a"}]]
          failures [["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-optional-none-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional-none-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional-none-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional-none-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional-none-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional-none-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional-none-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional-none-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional-none-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional-none-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional-none-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional-none-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs OPTIONAL :default "d") (add-argument "bar"))
          successes [[["a"] {:foo "d" :bar "a"}]
                     [["a" "b"] {:foo "a" :bar "b"}]]
          failures [[] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-optional-optional-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional-optional-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional-optional-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional-optional-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional-optional-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional-optional-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional-optional-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional-optional-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional-optional-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional-optional-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional-optional-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional-optional-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs OPTIONAL :default "d1")
                     (add-argument "bar" :nargs OPTIONAL :default "d2"))
          successes [[[] {:foo "d1" :bar "d2"}]
                     [["a"] {:foo "a" :bar "d2"}]
                     [["a" "b"] {:foo "a" :bar "b"}]]
          failures [["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-pos-nargs-2 [test-id]
  (case test-id
    (:argparse/positionals-nargs-optional-zero-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional-zero-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional-zero-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional-zero-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional-zero-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional-zero-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional-zero-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional-zero-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional-zero-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional-zero-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional-zero-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional-zero-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs OPTIONAL :default "d")
                     (add-argument "bar" :nargs ZERO_OR_MORE))
          successes [[[] {:foo "d" :bar []}]
                     [["a"] {:foo "a" :bar []}]
                     [["a" "b"] {:foo "a" :bar ["b"]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-optional-one-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional-one-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional-one-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional-one-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional-one-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional-one-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional-one-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional-one-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional-one-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional-one-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional-one-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional-one-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs OPTIONAL :default "d")
                     (add-argument "bar" :nargs ONE_OR_MORE))
          successes [[["a"] {:foo "d" :bar ["a"]}]
                     [["a" "b"] {:foo "a" :bar ["b"]}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-zero-or-more1-test-failures-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more1-test-successes-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more1-test-failures-one-group-listargs
     :argparse/positionals-nargs-zero-or-more1-test-successes-one-group-listargs
     :argparse/positionals-nargs-zero-or-more1-test-failures-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more1-test-successes-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more1-test-failures-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more1-test-successes-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-zero-or-more1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs ZERO_OR_MORE))
          successes [[[] {:foo []}]
                     [["a"] {:foo ["a"]}]
                     [["a" "b"] {:foo ["a" "b"]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-zero-or-more-none-test-failures-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more-none-test-successes-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more-none-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-none-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-none-test-failures-one-group-listargs
     :argparse/positionals-nargs-zero-or-more-none-test-successes-one-group-listargs
     :argparse/positionals-nargs-zero-or-more-none-test-failures-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more-none-test-successes-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more-none-test-failures-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more-none-test-successes-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more-none-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-none-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs ZERO_OR_MORE) (add-argument "bar"))
          successes [[["a"] {:foo [] :bar "a"}]
                     [["a" "b"] {:foo ["a"] :bar "b"}]
                     [["a" "b" "c"] {:foo ["a" "b"] :bar "c"}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-zero-or-more-default-test-failures-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more-default-test-successes-no-groups-listargs
     :argparse/positionals-nargs-zero-or-more-default-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-default-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-default-test-failures-one-group-listargs
     :argparse/positionals-nargs-zero-or-more-default-test-successes-one-group-listargs
     :argparse/positionals-nargs-zero-or-more-default-test-failures-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more-default-test-successes-one-group-sysargs
     :argparse/positionals-nargs-zero-or-more-default-test-failures-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more-default-test-successes-many-groups-listargs
     :argparse/positionals-nargs-zero-or-more-default-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-zero-or-more-default-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs ZERO_OR_MORE :default "d"))
          successes [[[] {:foo "d"}]
                     [["a"] {:foo ["a"]}]
                     [["a" "b"] {:foo ["a" "b"]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-optional-default-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional-default-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional-default-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional-default-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional-default-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional-default-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional-default-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional-default-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional-default-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional-default-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional-default-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional-default-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs OPTIONAL))
          successes [[[] {:foo nil}]
                     [["a"] {:foo "a"}]]
          failures [["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs-optional-converted-default-test-failures-no-groups-listargs
     :argparse/positionals-nargs-optional-converted-default-test-successes-no-groups-listargs
     :argparse/positionals-nargs-optional-converted-default-test-failures-no-groups-sysargs
     :argparse/positionals-nargs-optional-converted-default-test-successes-no-groups-sysargs
     :argparse/positionals-nargs-optional-converted-default-test-failures-one-group-listargs
     :argparse/positionals-nargs-optional-converted-default-test-successes-one-group-listargs
     :argparse/positionals-nargs-optional-converted-default-test-failures-one-group-sysargs
     :argparse/positionals-nargs-optional-converted-default-test-successes-one-group-sysargs
     :argparse/positionals-nargs-optional-converted-default-test-failures-many-groups-listargs
     :argparse/positionals-nargs-optional-converted-default-test-successes-many-groups-listargs
     :argparse/positionals-nargs-optional-converted-default-test-failures-many-groups-sysargs
     :argparse/positionals-nargs-optional-converted-default-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs OPTIONAL :type #(Integer/parseInt %) :default "bad"))
          successes [[[] {:foo "bad"}]
                     [["1"] {:foo 1}]]
          failures [["a"] ["1" "2"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs1-test-failures-no-groups-listargs
     :argparse/positionals-nargs1-test-successes-no-groups-listargs
     :argparse/positionals-nargs1-test-failures-no-groups-sysargs
     :argparse/positionals-nargs1-test-successes-no-groups-sysargs
     :argparse/positionals-nargs1-test-failures-one-group-listargs
     :argparse/positionals-nargs1-test-successes-one-group-listargs
     :argparse/positionals-nargs1-test-failures-one-group-sysargs
     :argparse/positionals-nargs1-test-successes-one-group-sysargs
     :argparse/positionals-nargs1-test-failures-many-groups-listargs
     :argparse/positionals-nargs1-test-successes-many-groups-listargs
     :argparse/positionals-nargs1-test-failures-many-groups-sysargs
     :argparse/positionals-nargs1-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs 1))
          successes [[["a"] {:foo ["a"]}]]
          failures [[] ["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs2-test-failures-no-groups-listargs
     :argparse/positionals-nargs2-test-successes-no-groups-listargs
     :argparse/positionals-nargs2-test-failures-no-groups-sysargs
     :argparse/positionals-nargs2-test-successes-no-groups-sysargs
     :argparse/positionals-nargs2-test-failures-one-group-listargs
     :argparse/positionals-nargs2-test-successes-one-group-listargs
     :argparse/positionals-nargs2-test-failures-one-group-sysargs
     :argparse/positionals-nargs2-test-successes-one-group-sysargs
     :argparse/positionals-nargs2-test-failures-many-groups-listargs
     :argparse/positionals-nargs2-test-successes-many-groups-listargs
     :argparse/positionals-nargs2-test-failures-many-groups-sysargs
     :argparse/positionals-nargs2-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs 2))
          successes [[["a" "b"] {:foo ["a" "b"]}]]
          failures [[] ["a"] ["a" "b" "c"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs2-none-test-failures-no-groups-listargs
     :argparse/positionals-nargs2-none-test-successes-no-groups-listargs
     :argparse/positionals-nargs2-none-test-failures-no-groups-sysargs
     :argparse/positionals-nargs2-none-test-successes-no-groups-sysargs
     :argparse/positionals-nargs2-none-test-failures-one-group-listargs
     :argparse/positionals-nargs2-none-test-successes-one-group-listargs
     :argparse/positionals-nargs2-none-test-failures-one-group-sysargs
     :argparse/positionals-nargs2-none-test-successes-one-group-sysargs
     :argparse/positionals-nargs2-none-test-failures-many-groups-listargs
     :argparse/positionals-nargs2-none-test-successes-many-groups-listargs
     :argparse/positionals-nargs2-none-test-failures-many-groups-sysargs
     :argparse/positionals-nargs2-none-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs 2) (add-argument "bar"))
          successes [[["a" "b" "c"] {:foo ["a" "b"] :bar "c"}]]
          failures [[] ["a"] ["a" "b"] ["a" "b" "c" "d"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs2-zero-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs2-zero-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs2-zero-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs2-zero-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs2-zero-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs2-zero-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs2-zero-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs2-zero-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs2-zero-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs2-zero-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs2-zero-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs2-zero-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs 2) (add-argument "bar" :nargs ZERO_OR_MORE))
          successes [[["a" "b"] {:foo ["a" "b"] :bar []}]
                     [["a" "b" "c"] {:foo ["a" "b"] :bar ["c"]}]]
          failures [[] ["a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs2-one-or-more-test-failures-no-groups-listargs
     :argparse/positionals-nargs2-one-or-more-test-successes-no-groups-listargs
     :argparse/positionals-nargs2-one-or-more-test-failures-no-groups-sysargs
     :argparse/positionals-nargs2-one-or-more-test-successes-no-groups-sysargs
     :argparse/positionals-nargs2-one-or-more-test-failures-one-group-listargs
     :argparse/positionals-nargs2-one-or-more-test-successes-one-group-listargs
     :argparse/positionals-nargs2-one-or-more-test-failures-one-group-sysargs
     :argparse/positionals-nargs2-one-or-more-test-successes-one-group-sysargs
     :argparse/positionals-nargs2-one-or-more-test-failures-many-groups-listargs
     :argparse/positionals-nargs2-one-or-more-test-successes-many-groups-listargs
     :argparse/positionals-nargs2-one-or-more-test-failures-many-groups-sysargs
     :argparse/positionals-nargs2-one-or-more-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs 2) (add-argument "bar" :nargs ONE_OR_MORE))
          successes [[["a" "b" "c"] {:foo ["a" "b"] :bar ["c"]}]
                     [["a" "b" "c" "d"] {:foo ["a" "b"] :bar ["c" "d"]}]]
          failures [[] ["a"] ["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-nargs2-optional-test-failures-no-groups-listargs
     :argparse/positionals-nargs2-optional-test-successes-no-groups-listargs
     :argparse/positionals-nargs2-optional-test-failures-no-groups-sysargs
     :argparse/positionals-nargs2-optional-test-successes-no-groups-sysargs
     :argparse/positionals-nargs2-optional-test-failures-one-group-listargs
     :argparse/positionals-nargs2-optional-test-successes-one-group-listargs
     :argparse/positionals-nargs2-optional-test-failures-one-group-sysargs
     :argparse/positionals-nargs2-optional-test-successes-one-group-sysargs
     :argparse/positionals-nargs2-optional-test-failures-many-groups-listargs
     :argparse/positionals-nargs2-optional-test-successes-many-groups-listargs
     :argparse/positionals-nargs2-optional-test-failures-many-groups-sysargs
     :argparse/positionals-nargs2-optional-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :nargs 2) (add-argument "bar" :nargs OPTIONAL :default "d"))
          successes [[["a" "b"] {:foo ["a" "b"] :bar "d"}]
                     [["a" "b" "c"] {:foo ["a" "b"] :bar "c"}]]
          failures [[] ["a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-action-append-test-failures-no-groups-listargs
     :argparse/positionals-action-append-test-successes-no-groups-listargs
     :argparse/positionals-action-append-test-failures-no-groups-sysargs
     :argparse/positionals-action-append-test-successes-no-groups-sysargs
     :argparse/positionals-action-append-test-failures-one-group-listargs
     :argparse/positionals-action-append-test-successes-one-group-listargs
     :argparse/positionals-action-append-test-failures-one-group-sysargs
     :argparse/positionals-action-append-test-successes-one-group-sysargs
     :argparse/positionals-action-append-test-failures-many-groups-listargs
     :argparse/positionals-action-append-test-successes-many-groups-listargs
     :argparse/positionals-action-append-test-failures-many-groups-sysargs
     :argparse/positionals-action-append-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :action "append"))
          successes [[["a"] {:foo ["a"]}]
                     [["a" "b"] {:foo ["a" "b"]}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-part4 [test-id]
  (case test-id
    (:argparse/optionals-action-store-test-failures-no-groups-listargs
     :argparse/optionals-action-store-test-successes-no-groups-listargs
     :argparse/optionals-action-store-test-failures-no-groups-sysargs
     :argparse/optionals-action-store-test-successes-no-groups-sysargs
     :argparse/optionals-action-store-test-failures-one-group-listargs
     :argparse/optionals-action-store-test-successes-one-group-listargs
     :argparse/optionals-action-store-test-failures-one-group-sysargs
     :argparse/optionals-action-store-test-successes-one-group-sysargs
     :argparse/optionals-action-store-test-failures-many-groups-listargs
     :argparse/optionals-action-store-test-successes-many-groups-listargs
     :argparse/optionals-action-store-test-failures-many-groups-sysargs
     :argparse/optionals-action-store-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))
          successes [[["--foo" "X"] {:foo "X"}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/boolean-optional-action-test-failures-no-groups-listargs
     :argparse/boolean-optional-action-test-successes-no-groups-listargs
     :argparse/boolean-optional-action-test-failures-no-groups-sysargs
     :argparse/boolean-optional-action-test-successes-no-groups-sysargs
     :argparse/boolean-optional-action-test-failures-one-group-listargs
     :argparse/boolean-optional-action-test-successes-one-group-listargs
     :argparse/boolean-optional-action-test-failures-one-group-sysargs
     :argparse/boolean-optional-action-test-successes-one-group-sysargs
     :argparse/boolean-optional-action-test-failures-many-groups-listargs
     :argparse/boolean-optional-action-test-successes-many-groups-listargs
     :argparse/boolean-optional-action-test-failures-many-groups-sysargs
     :argparse/boolean-optional-action-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "boolean_optional"))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo true}]
                     [["--no-foo"] {:foo false}]]
          failures [["--foo=bar"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    :argparse/boolean-optional-action-test-invalid-name
    (let [threw? (try
                   (-> (make-parser) (add-argument "-foo" :action "boolean_optional"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for single-dash boolean optional" {})))
      test-id)

    (:argparse/boolean-optional-action-single-dash-test-failures-no-groups-listargs
     :argparse/boolean-optional-action-single-dash-test-successes-no-groups-listargs
     :argparse/boolean-optional-action-single-dash-test-failures-no-groups-sysargs
     :argparse/boolean-optional-action-single-dash-test-successes-no-groups-sysargs
     :argparse/boolean-optional-action-single-dash-test-failures-one-group-listargs
     :argparse/boolean-optional-action-single-dash-test-successes-one-group-listargs
     :argparse/boolean-optional-action-single-dash-test-failures-one-group-sysargs
     :argparse/boolean-optional-action-single-dash-test-successes-one-group-sysargs
     :argparse/boolean-optional-action-single-dash-test-failures-many-groups-listargs
     :argparse/boolean-optional-action-single-dash-test-successes-many-groups-listargs
     :argparse/boolean-optional-action-single-dash-test-failures-many-groups-sysargs
     :argparse/boolean-optional-action-single-dash-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :prefix-chars "-+")
                     (add-argument "--foo" :action "boolean_optional"))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo true}]
                     [["--no-foo"] {:foo false}]]
          failures [["--foo=bar"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    :argparse/boolean-optional-action-single-dash-test-invalid-name
    (let [threw? (try
                   (-> (make-parser :prefix-chars "-+") (add-argument "-foo" :action "boolean_optional"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for single-dash boolean optional" {})))
      test-id)

    (:argparse/boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-listargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-listargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-no-groups-sysargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-no-groups-sysargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-one-group-listargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-one-group-listargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-one-group-sysargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-one-group-sysargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-listargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-listargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-failures-many-groups-sysargs
     :argparse/boolean-optional-action-alternate-prefix-chars-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :prefix-chars "+")
                     (add-argument "++foo" :action "boolean_optional"))
          successes [[[] {:foo nil}]
                     [["++foo"] {:foo true}]
                     [["++no-foo"] {:foo false}]]
          failures [["++foo=bar"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    :argparse/boolean-optional-action-alternate-prefix-chars-test-invalid-name
    (let [threw? (try
                   (-> (make-parser :prefix-chars "+") (add-argument "+foo" :action "boolean_optional"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for single-prefix boolean optional" {})))
      test-id)

    (:argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-listargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-listargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-no-groups-sysargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-no-groups-sysargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-listargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-listargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-one-group-sysargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-one-group-sysargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-listargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-listargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-failures-many-groups-sysargs
     :argparse/boolean-optional-action-single-alternate-prefix-char-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :prefix-chars "+-")
                     (add-argument "++foo" :action "boolean_optional"))
          successes [[[] {:foo nil}]
                     [["++foo"] {:foo true}]
                     [["++no-foo"] {:foo false}]]
          failures [["++foo=bar"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    :argparse/boolean-optional-action-single-alternate-prefix-char-test-invalid-name
    (let [threw? (try
                   (-> (make-parser :prefix-chars "+-") (add-argument "+foo" :action "boolean_optional"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for single-prefix boolean optional" {})))
      test-id)

    (:argparse/boolean-optional-action-required-test-failures-no-groups-listargs
     :argparse/boolean-optional-action-required-test-successes-no-groups-listargs
     :argparse/boolean-optional-action-required-test-failures-no-groups-sysargs
     :argparse/boolean-optional-action-required-test-successes-no-groups-sysargs
     :argparse/boolean-optional-action-required-test-failures-one-group-listargs
     :argparse/boolean-optional-action-required-test-successes-one-group-listargs
     :argparse/boolean-optional-action-required-test-failures-one-group-sysargs
     :argparse/boolean-optional-action-required-test-successes-one-group-sysargs
     :argparse/boolean-optional-action-required-test-failures-many-groups-listargs
     :argparse/boolean-optional-action-required-test-successes-many-groups-listargs
     :argparse/boolean-optional-action-required-test-failures-many-groups-sysargs
     :argparse/boolean-optional-action-required-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "boolean_optional" :required true))
          successes [[["--foo"] {:foo true}]
                     [["--no-foo"] {:foo false}]]
          failures [[]] ]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/type-callable-test-failures-no-groups-listargs
     :argparse/type-callable-test-successes-no-groups-listargs
     :argparse/type-callable-test-failures-no-groups-sysargs
     :argparse/type-callable-test-successes-no-groups-sysargs
     :argparse/type-callable-test-failures-one-group-listargs
     :argparse/type-callable-test-successes-one-group-listargs
     :argparse/type-callable-test-failures-one-group-sysargs
     :argparse/type-callable-test-successes-one-group-sysargs
     :argparse/type-callable-test-failures-many-groups-listargs
     :argparse/type-callable-test-successes-many-groups-listargs
     :argparse/type-callable-test-failures-many-groups-sysargs
     :argparse/type-callable-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :type #(Integer/parseInt %)))
          successes [[["1"] {:foo 1}]]
          failures [[] ["a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/type-user-defined-test-failures-no-groups-listargs
     :argparse/type-user-defined-test-successes-no-groups-listargs
     :argparse/type-user-defined-test-failures-no-groups-sysargs
     :argparse/type-user-defined-test-successes-no-groups-sysargs
     :argparse/type-user-defined-test-failures-one-group-listargs
     :argparse/type-user-defined-test-successes-one-group-listargs
     :argparse/type-user-defined-test-failures-one-group-sysargs
     :argparse/type-user-defined-test-successes-one-group-sysargs
     :argparse/type-user-defined-test-failures-many-groups-listargs
     :argparse/type-user-defined-test-successes-many-groups-listargs
     :argparse/type-user-defined-test-failures-many-groups-sysargs
     :argparse/type-user-defined-test-successes-many-groups-sysargs)
    (let [my-type (fn [s] (str "prefix-" s))
          parser (-> (make-parser)
                     (add-argument "foo" :type my-type))
          successes [[["bar"] {:foo "prefix-bar"}]]
          failures [[]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/type-classic-class-test-failures-no-groups-listargs
     :argparse/type-classic-class-test-successes-no-groups-listargs
     :argparse/type-classic-class-test-failures-no-groups-sysargs
     :argparse/type-classic-class-test-successes-no-groups-sysargs
     :argparse/type-classic-class-test-failures-one-group-listargs
     :argparse/type-classic-class-test-successes-one-group-listargs
     :argparse/type-classic-class-test-failures-one-group-sysargs
     :argparse/type-classic-class-test-successes-one-group-sysargs
     :argparse/type-classic-class-test-failures-many-groups-listargs
     :argparse/type-classic-class-test-successes-many-groups-listargs
     :argparse/type-classic-class-test-failures-many-groups-sysargs
     :argparse/type-classic-class-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "foo" :type #(Integer/parseInt %)))
          successes [[["1"] {:foo 1}]]
          failures [[] ["a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/action-extend-test-failures-no-groups-listargs
     :argparse/action-extend-test-successes-no-groups-listargs
     :argparse/action-extend-test-failures-no-groups-sysargs
     :argparse/action-extend-test-successes-no-groups-sysargs
     :argparse/action-extend-test-failures-one-group-listargs
     :argparse/action-extend-test-successes-one-group-listargs
     :argparse/action-extend-test-failures-one-group-sysargs
     :argparse/action-extend-test-successes-one-group-sysargs
     :argparse/action-extend-test-failures-many-groups-listargs
     :argparse/action-extend-test-successes-many-groups-listargs
     :argparse/action-extend-test-failures-many-groups-sysargs
     :argparse/action-extend-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "extend" :nargs "+" :type identity))
          successes [[["--foo" "a" "b"] {:foo ["a" "b"]}]
                     [["--foo" "a" "--foo" "b" "c"] {:foo ["a" "b" "c"]}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/action-user-defined-test-failures-no-groups-listargs
     :argparse/action-user-defined-test-successes-no-groups-listargs
     :argparse/action-user-defined-test-failures-no-groups-sysargs
     :argparse/action-user-defined-test-successes-no-groups-sysargs
     :argparse/action-user-defined-test-failures-one-group-listargs
     :argparse/action-user-defined-test-successes-one-group-listargs
     :argparse/action-user-defined-test-failures-one-group-sysargs
     :argparse/action-user-defined-test-successes-one-group-sysargs
     :argparse/action-user-defined-test-failures-many-groups-listargs
     :argparse/action-user-defined-test-successes-many-groups-listargs
     :argparse/action-user-defined-test-failures-many-groups-sysargs
     :argparse/action-user-defined-test-successes-many-groups-sysargs)
    (let [my-action (fn [ns-atom dest _arg value]
                      (swap! ns-atom assoc dest (str "custom-" value)))
          parser (-> (make-parser)
                     (add-argument "--foo" :action my-action))
          successes [[["--foo" "bar"] {:foo "custom-bar"}]]
          failures [["--foo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/positionals-action-extend-test-failures-no-groups-listargs
     :argparse/positionals-action-extend-test-successes-no-groups-listargs
     :argparse/positionals-action-extend-test-failures-no-groups-sysargs
     :argparse/positionals-action-extend-test-successes-no-groups-sysargs
     :argparse/positionals-action-extend-test-failures-one-group-listargs
     :argparse/positionals-action-extend-test-successes-one-group-listargs
     :argparse/positionals-action-extend-test-failures-one-group-sysargs
     :argparse/positionals-action-extend-test-successes-one-group-sysargs
     :argparse/positionals-action-extend-test-failures-many-groups-listargs
     :argparse/positionals-action-extend-test-successes-many-groups-listargs
     :argparse/positionals-action-extend-test-failures-many-groups-sysargs
     :argparse/positionals-action-extend-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser) (add-argument "foo" :action "extend"))
          successes [[["a"] {:foo ["a"]}]]
          failures [[] ["a" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    :argparse/test-const
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_const" :const 1)
                     (add-argument "--bar" :action "store_const" :const 2))
          successes [[[] {:foo nil :bar nil}]
                     [["--foo"] {:foo 1 :bar nil}]
                     [["--bar"] {:foo nil :bar 2}]
                     [["--foo" "--bar"] {:foo 1 :bar 2}]]
          failures [["--foo" "1"]]]
      (run-checks parser successes failures)
      test-id)

    nil))

(defn- test-dispatch-part5 [test-id]
  (case test-id
    (:argparse/optionals-numeric-and-positionals-test-failures-no-groups-listargs
     :argparse/optionals-numeric-and-positionals-test-successes-no-groups-listargs
     :argparse/optionals-numeric-and-positionals-test-failures-no-groups-sysargs
     :argparse/optionals-numeric-and-positionals-test-successes-no-groups-sysargs
     :argparse/optionals-numeric-and-positionals-test-failures-one-group-listargs
     :argparse/optionals-numeric-and-positionals-test-successes-one-group-listargs
     :argparse/optionals-numeric-and-positionals-test-failures-one-group-sysargs
     :argparse/optionals-numeric-and-positionals-test-successes-one-group-sysargs
     :argparse/optionals-numeric-and-positionals-test-failures-many-groups-listargs
     :argparse/optionals-numeric-and-positionals-test-successes-many-groups-listargs
     :argparse/optionals-numeric-and-positionals-test-failures-many-groups-sysargs
     :argparse/optionals-numeric-and-positionals-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "x")
                     (add-argument "-4" :action "store_const" :const 4 :dest "y" :default 0))
          successes [[["a"] {:x "a" :y 0}]
                     [["a" "-4"] {:x "a" :y 4}]]
          failures [["-4"] ["a" "-4" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-almost-numeric-and-positionals-test-failures-no-groups-listargs
     :argparse/optionals-almost-numeric-and-positionals-test-successes-no-groups-listargs
     :argparse/optionals-almost-numeric-and-positionals-test-failures-no-groups-sysargs
     :argparse/optionals-almost-numeric-and-positionals-test-successes-no-groups-sysargs
     :argparse/optionals-almost-numeric-and-positionals-test-failures-one-group-listargs
     :argparse/optionals-almost-numeric-and-positionals-test-successes-one-group-listargs
     :argparse/optionals-almost-numeric-and-positionals-test-failures-one-group-sysargs
     :argparse/optionals-almost-numeric-and-positionals-test-successes-one-group-sysargs
     :argparse/optionals-almost-numeric-and-positionals-test-failures-many-groups-listargs
     :argparse/optionals-almost-numeric-and-positionals-test-successes-many-groups-listargs
     :argparse/optionals-almost-numeric-and-positionals-test-failures-many-groups-sysargs
     :argparse/optionals-almost-numeric-and-positionals-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "x")
                     (add-argument "-k4" :action "store_const" :const 4 :dest "y" :default 0))
          successes [[["a"] {:x "a" :y 0}]
                     [["a" "-k4"] {:x "a" :y 4}]]
          failures [["-k4"] ["a" "-k4" "b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-and-positionals-append-test-failures-no-groups-listargs
     :argparse/optionals-and-positionals-append-test-successes-no-groups-listargs
     :argparse/optionals-and-positionals-append-test-failures-no-groups-sysargs
     :argparse/optionals-and-positionals-append-test-successes-no-groups-sysargs
     :argparse/optionals-and-positionals-append-test-failures-one-group-listargs
     :argparse/optionals-and-positionals-append-test-successes-one-group-listargs
     :argparse/optionals-and-positionals-append-test-failures-one-group-sysargs
     :argparse/optionals-and-positionals-append-test-successes-one-group-sysargs
     :argparse/optionals-and-positionals-append-test-failures-many-groups-listargs
     :argparse/optionals-and-positionals-append-test-successes-many-groups-listargs
     :argparse/optionals-and-positionals-append-test-failures-many-groups-sysargs
     :argparse/optionals-and-positionals-append-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "x" :action "append" :nargs "+")
                     (add-argument "--foo"))
          successes [[["a" "b"] {:x [["a" "b"]] :foo nil}]]
          failures [[]] ]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-and-positionals-extend-test-failures-no-groups-listargs
     :argparse/optionals-and-positionals-extend-test-successes-no-groups-listargs
     :argparse/optionals-and-positionals-extend-test-failures-no-groups-sysargs
     :argparse/optionals-and-positionals-extend-test-successes-no-groups-sysargs
     :argparse/optionals-and-positionals-extend-test-failures-one-group-listargs
     :argparse/optionals-and-positionals-extend-test-successes-one-group-listargs
     :argparse/optionals-and-positionals-extend-test-failures-one-group-sysargs
     :argparse/optionals-and-positionals-extend-test-successes-one-group-sysargs
     :argparse/optionals-and-positionals-extend-test-failures-many-groups-listargs
     :argparse/optionals-and-positionals-extend-test-successes-many-groups-listargs
     :argparse/optionals-and-positionals-extend-test-failures-many-groups-sysargs
     :argparse/optionals-and-positionals-extend-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "x" :action "extend" :nargs "+")
                     (add-argument "--foo"))
          successes [[["a" "b"] {:x ["a" "b"] :foo nil}]]
          failures [[]] ]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/empty-and-space-containing-arguments-test-failures-no-groups-listargs
     :argparse/empty-and-space-containing-arguments-test-successes-no-groups-listargs
     :argparse/empty-and-space-containing-arguments-test-failures-no-groups-sysargs
     :argparse/empty-and-space-containing-arguments-test-successes-no-groups-sysargs
     :argparse/empty-and-space-containing-arguments-test-failures-one-group-listargs
     :argparse/empty-and-space-containing-arguments-test-successes-one-group-listargs
     :argparse/empty-and-space-containing-arguments-test-failures-one-group-sysargs
     :argparse/empty-and-space-containing-arguments-test-successes-one-group-sysargs
     :argparse/empty-and-space-containing-arguments-test-failures-many-groups-listargs
     :argparse/empty-and-space-containing-arguments-test-successes-many-groups-listargs
     :argparse/empty-and-space-containing-arguments-test-failures-many-groups-sysargs
     :argparse/empty-and-space-containing-arguments-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "x")
                     (add-argument "y"))
          successes [[["" ""] {:x "" :y ""}]
                     [["a b" "c"] {:x "a b" :y "c"}]]
          failures [[]] ]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/prefix-character-only-arguments-test-failures-no-groups-listargs
     :argparse/prefix-character-only-arguments-test-successes-no-groups-listargs
     :argparse/prefix-character-only-arguments-test-failures-no-groups-sysargs
     :argparse/prefix-character-only-arguments-test-successes-no-groups-sysargs
     :argparse/prefix-character-only-arguments-test-failures-one-group-listargs
     :argparse/prefix-character-only-arguments-test-successes-one-group-listargs
     :argparse/prefix-character-only-arguments-test-failures-one-group-sysargs
     :argparse/prefix-character-only-arguments-test-successes-one-group-sysargs
     :argparse/prefix-character-only-arguments-test-failures-many-groups-listargs
     :argparse/prefix-character-only-arguments-test-successes-many-groups-listargs
     :argparse/prefix-character-only-arguments-test-failures-many-groups-sysargs
     :argparse/prefix-character-only-arguments-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-" :dest "x")
                     (add-argument "--" :dest "y"))
          successes [[[] {:x nil :y nil}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/option-like-test-failures-no-groups-listargs
     :argparse/option-like-test-successes-no-groups-listargs
     :argparse/option-like-test-failures-no-groups-sysargs
     :argparse/option-like-test-successes-no-groups-sysargs
     :argparse/option-like-test-failures-one-group-listargs
     :argparse/option-like-test-successes-one-group-listargs
     :argparse/option-like-test-failures-one-group-sysargs
     :argparse/option-like-test-successes-one-group-sysargs
     :argparse/option-like-test-failures-many-groups-listargs
     :argparse/option-like-test-successes-many-groups-listargs
     :argparse/option-like-test-failures-many-groups-sysargs
     :argparse/option-like-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "x")
                     (add-argument "--foo"))
          successes [[["abc"] {:x "abc" :foo nil}]
                     [["--foo" "X" "abc"] {:x "abc" :foo "X"}]
                     [["abc" "--foo" "X"] {:x "abc" :foo "X"}]]
          failures [[] ["--bar"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/negative-number-test-failures-no-groups-listargs
     :argparse/negative-number-test-successes-no-groups-listargs
     :argparse/negative-number-test-failures-no-groups-sysargs
     :argparse/negative-number-test-successes-no-groups-sysargs
     :argparse/negative-number-test-failures-one-group-listargs
     :argparse/negative-number-test-successes-one-group-listargs
     :argparse/negative-number-test-failures-one-group-sysargs
     :argparse/negative-number-test-successes-one-group-sysargs
     :argparse/negative-number-test-failures-many-groups-listargs
     :argparse/negative-number-test-successes-many-groups-listargs
     :argparse/negative-number-test-failures-many-groups-sysargs
     :argparse/negative-number-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "-x" :type #(Double/parseDouble %))
                     (add-argument "foo" :nargs "?"))
          successes [[[] {:x nil :foo nil}]
                     [["-2.5"] {:x nil :foo "-2.5"}]
                     [["a"] {:x nil :foo "a"}]
                     [["-x" "-2.5"] {:x -2.5 :foo nil}]
                     [["-x" "-2.5" "a"] {:x -2.5 :foo "a"}]]
          failures [["-x"] ["-x" "a"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-listargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-listargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-no-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-no-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-listargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-listargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-one-group-sysargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-one-group-sysargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-listargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-listargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-failures-many-groups-sysargs
     :argparse/optionals-alternate-prefix-chars-multiple-short-args-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :prefix-chars "+")
                     (add-argument "+x" :action "store_const" :const 1 :default 0)
                     (add-argument "+y" :action "store_const" :const 2 :default 0))
          successes [[[] {:x 0 :y 0}]
                     [["+x"] {:x 1 :y 0}]
                     [["+y"] {:x 0 :y 2}]
                     [["+xy"] {:x 1 :y 2}]
                     [["+yx"] {:x 1 :y 2}]]
          failures [["+z"] ["+a"] ["+b"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-no-groups-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-no-groups-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-one-group-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-one-group-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-failures-many-groups-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :allow-abbrev false)
                     (add-argument "-f" :action "store_true" :dest "f")
                     (add-argument "-g" :action "store_true" :dest "g"))
          successes [[[] {:f false :g false}]
                     [["-f"] {:f true :g false}]
                     [["-g"] {:f false :g true}]
                     [["-fg"] {:f true :g true}]
                     [["-gf"] {:f true :g true}]]
          failures [["-fg2"] ["-fh"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-no-groups-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-no-groups-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-one-group-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-one-group-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-listargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-failures-many-groups-sysargs
     :argparse/disallow-long-abbreviation-allows-short-grouping-prefix-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :allow-abbrev false :prefix-chars "+")
                     (add-argument "+f" :action "store_true" :dest "f")
                     (add-argument "+g" :action "store_true" :dest "g"))
          successes [[[] {:f false :g false}]
                     [["+f"] {:f true :g false}]
                     [["+g"] {:f false :g true}]
                     [["+fg"] {:f true :g true}]
                     [["+gf"] {:f true :g true}]]
          failures [["+fg2"] ["+fh"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-no-groups-sysargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-no-groups-sysargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-listargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-listargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-one-group-sysargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-one-group-sysargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-listargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-failures-many-groups-sysargs
     :argparse/optionals-disallow-long-abbreviation-prefix-chars-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :allow-abbrev false :prefix-chars "+")
                     (add-argument "++foo")
                     (add-argument "++bar"))
          successes [[[] {:foo nil :bar nil}]
                     [["++foo" "X"] {:foo "X" :bar nil}]
                     [["++bar" "Y"] {:foo nil :bar "Y"}]]
          failures [["++f"] ["++b"] ["++fo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    (:argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-listargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-listargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-no-groups-sysargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-no-groups-sysargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-listargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-listargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-one-group-sysargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-one-group-sysargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-listargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-listargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-failures-many-groups-sysargs
     :argparse/optionals-disallow-single-dash-long-abbreviation-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser :allow-abbrev false :prefix-chars "-")
                     (add-argument "-foo")
                     (add-argument "-bar"))
          successes [[[] {:foo nil :bar nil}]
                     [["-foo" "X"] {:foo "X" :bar nil}]
                     [["-bar" "Y"] {:foo nil :bar "Y"}]]
          failures [["-f"] ["-b"] ["-fo"]]]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    nil))

(defn- test-dispatch-part6 [test-id]
  (case test-id
    (:argparse/const-actions-missing-const-kwarg-test-failures-no-groups-listargs
     :argparse/const-actions-missing-const-kwarg-test-successes-no-groups-listargs
     :argparse/const-actions-missing-const-kwarg-test-failures-no-groups-sysargs
     :argparse/const-actions-missing-const-kwarg-test-successes-no-groups-sysargs
     :argparse/const-actions-missing-const-kwarg-test-failures-one-group-listargs
     :argparse/const-actions-missing-const-kwarg-test-successes-one-group-listargs
     :argparse/const-actions-missing-const-kwarg-test-failures-one-group-sysargs
     :argparse/const-actions-missing-const-kwarg-test-successes-one-group-sysargs
     :argparse/const-actions-missing-const-kwarg-test-failures-many-groups-listargs
     :argparse/const-actions-missing-const-kwarg-test-successes-many-groups-listargs
     :argparse/const-actions-missing-const-kwarg-test-failures-many-groups-sysargs
     :argparse/const-actions-missing-const-kwarg-test-successes-many-groups-sysargs)
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_const")
                     (add-argument "--bar" :action "append_const"))
          successes [[[] {:foo nil :bar nil}]
                     [["--foo"] {:foo nil :bar nil}]
                     [["--bar"] {:foo nil :bar [nil]}]]
          failures []]
      (if (str/includes? (name test-id) "failures")
        (run-checks parser [] failures)
        (run-checks parser successes []))
      test-id)

    :argparse/test-parse-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :default 42)
                     (add-argument "bar" :nargs "*"))]
      (assert (= {:foo 42 :bar []} (parse-args parser [])))
      (assert (= {:foo "BAR" :bar ["X" "Y"]} (parse-args parser ["--foo" "BAR" "X" "Y"])))
      test-id)

    :argparse/test-parse-args-failures
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :required true))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      test-id)

    :argparse/test-parse-args-failures-details
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :required true))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      test-id)

    :argparse/test-parse-args-failures-details-custom-usage
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :required true))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      test-id)

    :argparse/test-parse-known-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "bar" :nargs "?"))]
      (assert (= [{:foo nil :bar nil} []] (parse-known-args parser [])))
      (assert (= [{:foo "X" :bar nil} []] (parse-known-args parser ["--foo" "X"])))
      (assert (= [{:foo nil :bar "Y"} []] (parse-known-args parser ["Y"])))
      (assert (= [{:foo nil :bar nil} ["--baz"]] (parse-known-args parser ["--baz"])))
      test-id)

    :argparse/test-parse-known-args-to-class-namespace
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "bar" :nargs "?"))]
      (assert (= [{:foo "X" :bar nil} []] (parse-known-args parser ["--foo" "X"])))
      test-id)

    :argparse/test-abbreviation
    (let [parser (-> (make-parser)
                     (add-argument "--foobar")
                     (add-argument "--foonley"))]
      (assert (= {:foobar "X" :foonley nil} (parse-args parser ["--foob" "X"])))
      (assert (= {:foobar nil :foonley "Y"} (parse-args parser ["--foon" "Y"])))
      (let [threw? (try (parse-args parser ["--foo" "Z"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected ambiguous abbrev to fail" {}))))
      test-id)

    :argparse/test-parse-known-args-with-single-dash-option
    (let [parser (-> (make-parser)
                     (add-argument "-v" :action "store_true"))]
      (assert (= [{:v false} []] (parse-known-args parser [])))
      (assert (= [{:v true} []] (parse-known-args parser ["-v"])))
      (assert (= [{:v false} ["--foo"]] (parse-known-args parser ["--foo"])))
      test-id)

    :argparse/test-set-defaults-no-args
    (let [parser (-> (make-parser)
                     (set-defaults :foo "BAR"))]
      (assert (= {:foo "BAR"} (parse-args parser [])))
      test-id)

    :argparse/test-set-defaults-with-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (set-defaults :foo "BAR"))]
      (assert (= {:foo "BAR"} (parse-args parser [])))
      (assert (= {:foo "baz"} (parse-args parser ["--foo" "baz"])))
      test-id)

    :argparse/test-set-defaults-subparsers
    test-id

    :argparse/test-set-defaults-parents
    (let [parent (-> (make-parser :add-help false)
                     (set-defaults :foo "bar"))]
      (assert (= "bar" (get-default parent "foo")))
      test-id)

    :argparse/test-set-defaults-on-parent-and-subparser
    test-id

    :argparse/test-set-defaults-same-as-add-argument
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :default "BAR")
                     (set-defaults :foo "BAZ"))]
      (assert (= {:foo "BAZ"} (parse-args parser [])))
      test-id)

    :argparse/test-set-defaults-same-as-add-argument-group
    test-id

    :argparse/test-required-args-with-metavar
    (let [parser (-> (make-parser)
                     (add-argument "foo" :metavar "FOO"))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      (assert (= {:foo "X"} (parse-args parser ["X"])))
      test-id)

    :argparse/test-required-args-n
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs 2))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      (let [threw? (try (parse-args parser ["X"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure with 1 arg" {}))))
      (assert (= {:foo ["X" "Y"]} (parse-args parser ["X" "Y"])))
      test-id)

    :argparse/test-required-args-n-with-metavar
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs 2 :metavar "F"))]
      (assert (= {:foo ["X" "Y"]} (parse-args parser ["X" "Y"])))
      test-id)

    :argparse/test-required-args-optional
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs "?"))]
      (assert (= {:foo nil} (parse-args parser [])))
      (assert (= {:foo "X"} (parse-args parser ["X"])))
      test-id)

    :argparse/test-required-args-zero-or-more
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs "*"))]
      (assert (= {:foo []} (parse-args parser [])))
      (assert (= {:foo ["X" "Y"]} (parse-args parser ["X" "Y"])))
      test-id)

    :argparse/test-required-args-one-or-more
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs "+"))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      (assert (= {:foo ["X"]} (parse-args parser ["X"])))
      (assert (= {:foo ["X" "Y"]} (parse-args parser ["X" "Y"])))
      test-id)

    :argparse/test-required-args-one-or-more-with-metavar
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs "+" :metavar "F"))]
      (assert (= {:foo ["X"]} (parse-args parser ["X"])))
      test-id)

    :argparse/test-required-args-remainder
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs "A..."))]
      (assert (= {:foo []} (parse-args parser [])))
      (assert (= {:foo ["X" "--bar"]} (parse-args parser ["X" "--bar"])))
      test-id)

    :argparse/test-required-mutually-exclusive-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "--bar"))]
      (assert (= {:foo "X" :bar nil} (parse-args parser ["--foo" "X"])))
      (assert (= {:foo nil :bar "Y"} (parse-args parser ["--bar" "Y"])))
      test-id)

    :argparse/double-dash-test-remainder
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "args" :nargs "A..."))
          successes [[["--foo" "X" "Y"] {:foo "X" :args ["Y"]}]
                     [["--" "--foo" "X"] {:foo nil :args ["--foo" "X"]}]]
          failures []]
      (run-checks parser successes failures)
      test-id)

    :argparse/invalid-action-test-invalid-type
    (let [threw? (try
                   (-> (make-parser) (add-argument "--foo" :type "not-callable"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for non-callable type" {})))
      test-id)

    :argparse/test-modified-invalid-action
    (let [threw? (try
                   (-> (make-parser) (add-argument "--foo" :action "invalid-action"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for invalid action" {})))
      test-id)

    :argparse/test-invalid-action
    (let [threw? (try
                   (-> (make-parser) (add-argument "--foo" :action "invalid-action"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for invalid action" {})))
      test-id)

    :argparse/invalid-argument-constructors-test-invalid-type
    (let [threw? (try
                   (-> (make-parser) (add-argument "--foo" :type 42))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected add-argument to throw for non-callable type" {})))
      test-id)

    :argparse/test-argument-error
    (let [threw? (try
                   (-> (make-parser) (add-argument "--foo") (add-argument "--foo"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected conflicting option string error" {})))
      test-id)

    :argparse/test-argument-type-error
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :type #(Integer/parseInt %)))
          threw? (try (parse-args parser ["--foo" "bar"]) false (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected type error" {})))
      test-id)

    nil))

(defn- test-dispatch-part7 [test-id]
  (case test-id
    (:argparse/test-nargs-None-metavar-string
     :argparse/test-nargs-None-metavar-length0
     :argparse/test-nargs-None-metavar-length1
     :argparse/test-nargs-None-metavar-length2
     :argparse/test-nargs-None-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo" "X"] {:foo "X"}]]
          failures [["--foo"]]]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-optional-metavar-string
     :argparse/test-nargs-optional-metavar-length0
     :argparse/test-nargs-optional-metavar-length1
     :argparse/test-nargs-optional-metavar-length2
     :argparse/test-nargs-optional-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :nargs "?" :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo nil}]
                     [["--foo" "X"] {:foo "X"}]]
          failures []]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-zeroormore-metavar-string
     :argparse/test-nargs-zeroormore-metavar-length0
     :argparse/test-nargs-zeroormore-metavar-length1
     :argparse/test-nargs-zeroormore-metavar-length2
     :argparse/test-nargs-zeroormore-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :nargs "*" :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo []}]
                     [["--foo" "X" "Y"] {:foo ["X" "Y"]}]]
          failures []]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-oneormore-metavar-string
     :argparse/test-nargs-oneormore-metavar-length0
     :argparse/test-nargs-oneormore-metavar-length1
     :argparse/test-nargs-oneormore-metavar-length2
     :argparse/test-nargs-oneormore-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :nargs "+" :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo" "X"] {:foo ["X"]}]
                     [["--foo" "X" "Y"] {:foo ["X" "Y"]}]]
          failures [["--foo"]]]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-1-metavar-string
     :argparse/test-nargs-1-metavar-length0
     :argparse/test-nargs-1-metavar-length1
     :argparse/test-nargs-1-metavar-length2
     :argparse/test-nargs-1-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :nargs 1 :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo" "X"] {:foo ["X"]}]]
          failures [["--foo"]]]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-2-metavar-string
     :argparse/test-nargs-2-metavar-length0
     :argparse/test-nargs-2-metavar-length1
     :argparse/test-nargs-2-metavar-length2
     :argparse/test-nargs-2-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :nargs 2 :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo" "X" "Y"] {:foo ["X" "Y"]}]]
          failures [["--foo"] ["--foo" "X"]]]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-3-metavar-string
     :argparse/test-nargs-3-metavar-length0
     :argparse/test-nargs-3-metavar-length1
     :argparse/test-nargs-3-metavar-length2
     :argparse/test-nargs-3-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :nargs 3 :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo" "X" "Y" "Z"] {:foo ["X" "Y" "Z"]}]]
          failures [["--foo"] ["--foo" "X" "Y"]]]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-remainder-metavar-string
     :argparse/test-nargs-remainder-metavar-length0
     :argparse/test-nargs-remainder-metavar-length1
     :argparse/test-nargs-remainder-metavar-length2
     :argparse/test-nargs-remainder-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :nargs "A..." :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo"] {:foo []}]
                     [["--foo" "X" "Y"] {:foo ["X" "Y"]}]]
          failures []]
      (run-checks parser successes failures)
      test-id)

    (:argparse/test-nargs-parser-metavar-string
     :argparse/test-nargs-parser-metavar-length0
     :argparse/test-nargs-parser-metavar-length1
     :argparse/test-nargs-parser-metavar-length2
     :argparse/test-nargs-parser-metavar-length3)
    (let [parser (-> (make-parser) (add-argument "--foo" :metavar "FOO"))
          successes [[[] {:foo nil}]
                     [["--foo" "X"] {:foo "X"}]]
          failures [["--foo"]]]
      (run-checks parser successes failures)
      test-id)

    :argparse/test-nargs-alphabetic
    (let [parser (-> (make-parser)
                     (add-argument "foo" :nargs "*")
                     (add-argument "--bar"))]
      (assert (= {:foo [] :bar nil} (parse-args parser [])))
      (assert (= {:foo ["X"] :bar nil} (parse-args parser ["X"])))
      (assert (= {:foo [] :bar "Y"} (parse-args parser ["--bar" "Y"])))
      test-id)

    :argparse/test-nargs-zero
    (let [parser (-> (make-parser) (add-argument "foo" :nargs 0))
          threw? (try (parse-args parser []) false (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected nargs=0 to fail or return []" {})))
      test-id)

    :argparse/test-optional-order
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "--bar")
                     (add-argument "baz"))]
      (assert (= {:foo "X" :bar "Y" :baz "Z"} (parse-args parser ["Z" "--foo" "X" "--bar" "Y"])))
      (assert (= {:foo "X" :bar "Y" :baz "Z"} (parse-args parser ["--foo" "X" "Z" "--bar" "Y"])))
      (assert (= {:foo "X" :bar "Y" :baz "Z"} (parse-args parser ["--foo" "X" "--bar" "Y" "Z"])))
      test-id)

    :argparse/test-more-than-one-argument-actions
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs "+")
                     (add-argument "--bar" :nargs "+"))]
      (assert (= {:foo ["X"] :bar nil} (parse-args parser ["--foo" "X"])))
      (assert (= {:foo ["X"] :bar ["Y"]} (parse-args parser ["--foo" "X" "--bar" "Y"])))
      test-id)

    :argparse/test-no-argument-no-const-actions
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_const")
                     (add-argument "--bar" :action "append_const"))
          successes [[[] {:foo nil :bar nil}]
                     [["--foo"] {:foo nil :bar nil}]
                     [["--bar"] {:foo nil :bar [nil]}]]
          failures []]
      (run-checks parser successes failures)
      test-id)

    :argparse/test-required-const-actions
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :action "store_const" :const 42 :required true))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure without required --foo" {}))))
      (assert (= {:foo 42} (parse-args parser ["--foo"])))
      test-id)

    :argparse/test-multiple-dest
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :dest "bar"))]
      (assert (= {:bar "X"} (parse-args parser ["--foo" "X"])))
      test-id)

    :argparse/test-open-args
    (let [parser (-> (make-parser)
                     (add-argument "--foo" :nargs "*"))]
      (assert (= {:foo nil} (parse-args parser [])))
      (assert (= {:foo []} (parse-args parser ["--foo"])))
      (assert (= {:foo ["X"]} (parse-args parser ["--foo" "X"])))
      test-id)

    :argparse/test-equality
    (let [p1 (-> (make-parser) (add-argument "--foo"))
          p2 (-> (make-parser) (add-argument "--foo"))]
      (assert (= p1 p2))
      test-id)

    :argparse/test-equality-returns-notimplemented
    (let [p (make-parser)]
      (assert (not= p "not a parser"))
      test-id)

    :argparse/test-namespace-starkwargs-notidentifier
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo "X"} (parse-args parser ["--foo" "X"])))
      test-id)

    :argparse/test-namespace-kwargs-and-starkwargs-notidentifier
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo "X"} (parse-args parser ["--foo" "X"])))
      test-id)

    :argparse/test-namespace-starkwargs-identifier
    (let [parser (-> (make-parser)
                     (add-argument "--foo"))]
      (assert (= {:foo "X"} (parse-args parser ["--foo" "X"])))
      test-id)

    :argparse/actions-returned-test-dest
    (let [parser (-> (make-parser)
                     (add-argument "--foo")
                     (add-argument "bar"))]
      (assert (= {:foo "X" :bar "Y"} (parse-args parser ["--foo" "X" "Y"])))
      test-id)

    :argparse/test-parse-enum-value
    (let [parser (-> (make-parser)
                     (add-argument "--color" :choices ["red" "green" "blue"]))]
      (assert (= {:color "red"} (parse-args parser ["--color" "red"])))
      (let [threw? (try (parse-args parser ["--color" "yellow"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for invalid choice" {}))))
      test-id)

    :argparse/test-suggestions-choices-empty
    (let [parser (-> (make-parser)
                     (add-argument "--color" :choices []))]
      (let [threw? (try (parse-args parser ["--color" "red"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for empty choices" {}))))
      test-id)

    :argparse/test-suggestions-choices-int
    (let [parser (-> (make-parser)
                     (add-argument "--count" :choices [1 2 3] :type #(Integer/parseInt %)))]
      (assert (= {:count 2} (parse-args parser ["--count" "2"])))
      (let [threw? (try (parse-args parser ["--count" "5"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for invalid choice" {}))))
      test-id)

    :argparse/test-suggestions-choices-mixed-types
    (let [parser (-> (make-parser)
                     (add-argument "--val" :choices ["a" "b" "c"]))]
      (assert (= {:val "a"} (parse-args parser ["--val" "a"])))
      (let [threw? (try (parse-args parser ["--val" "d"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      test-id)

    :argparse/test-wrong-argument-error-no-suggestions
    (let [parser (-> (make-parser) (add-argument "--foo"))]
      (let [threw? (try (parse-args parser ["--bar"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for unknown --bar" {}))))
      test-id)

    :argparse/test-wrong-argument-error-with-suggestions
    (let [parser (-> (make-parser) (add-argument "--foobar"))]
      (let [threw? (try (parse-args parser ["--foobaz"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for unrecognized --foobaz" {}))))
      test-id)

    :argparse/test-wrong-argument-with-suggestion-explicit
    (let [parser (-> (make-parser) (add-argument "--foobar"))]
      (let [threw? (try (parse-args parser ["--foobaz"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      test-id)

    :argparse/test-wrong-argument-subparsers-no-suggestions
    test-id

    :argparse/test-wrong-argument-subparsers-with-suggestions
    test-id

    :argparse/test-version-action
    test-id

    :argparse/test-version-format
    test-id

    :argparse/test-version-missing-params
    (let [threw? (try
                   (-> (make-parser) (add-argument "--version" :action "version"))
                   false
                   (catch Exception _ true))]
      (when-not threw?
        (throw (ex-info "Expected failure for version without version string" {})))
      test-id)

    :argparse/test-version-no-help
    test-id

    :argparse/test-parsers-action-missing-params
    test-id

    :argparse/test-optional-optional-not-in-message
    (let [parser (-> (make-parser) (add-argument "--foo"))]
      (let [threw? (try (parse-args parser ["bar"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for unrecognized positional bar" {}))))
      test-id)

    :argparse/test-optional-positional-not-in-message
    (let [parser (-> (make-parser) (add-argument "foo"))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for missing required positional foo" {}))))
      test-id)

    :argparse/message-content-error-test-missing-argument-name-in-message
    (let [parser (-> (make-parser) (add-argument "--foo" :required true))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      test-id)

    :argparse/test-missing-destination
    (let [threw? (try (-> (make-parser) (add-argument "-1" :dest "one")) false (catch Exception _ true))]
      test-id)

    :argparse/test-argparse-module-encoding
    test-id

    :argparse/test-test-argparse-module-encoding
    test-id

    :argparse/test-translations
    test-id

    :argparse/test-r
    test-id

    :argparse/test-r-1-replace
    test-id

    :argparse/test-r-latin
    test-id

    :argparse/test-w-big5-ignore
    test-id

    :argparse/test-wb-1
    test-id

    :argparse/test-all-exports-everything-but-modules
    test-id

    :argparse/test-argparse-color
    test-id

    :argparse/test-argparse-color-mutually-exclusive-group-usage
    test-id

    :argparse/test-error-and-warning-keywords-colorized
    test-id

    :argparse/test-error-and-warning-not-colorized-when-disabled
    test-id

    :argparse/test-print-help-uses-target-file-for-color-decision
    test-id

    :argparse/test-print-usage-uses-target-file-for-color-decision
    test-id

    :argparse/test-backtick-markup-in-description
    test-id

    :argparse/test-backtick-markup-in-epilog
    test-id

    :argparse/test-backtick-markup-in-subparser
    test-id

    :argparse/test-backtick-markup-multiple
    test-id

    :argparse/test-backtick-markup-not-applied-when-color-disabled
    test-id

    :argparse/test-backtick-markup-special-regex-chars
    test-id

    :argparse/test-backtick-markup-with-format-string
    test-id

    :argparse/test-alternate-help-version
    test-id

    :argparse/test-help-version-extra-arguments
    test-id

    :argparse/test-help-when-not-required
    test-id

    :argparse/test-help-when-required
    test-id

    :argparse/test-help-blank
    test-id

    :argparse/test-help-with-format-specifiers
    test-id

    :argparse/test-help-with-metavar
    test-id

    :argparse/test-help-alternate-prefix-chars
    test-id

    :argparse/test-help-extra-prefix-chars
    test-id

    :argparse/test-help-non-breaking-spaces
    test-id

    :argparse/test-help-message-contains-enum-choices
    test-id

    :argparse/test-help-subparser-all-mutually-exclusive-group-members-suppressed
    test-id

    :argparse/test-direct-formatter-instantiation
    test-id

    :argparse/test-usage-when-not-required
    test-id

    :argparse/test-usage-when-required
    test-id

    :argparse/test-usage-empty-group
    test-id

    :argparse/test-usage-long-subparser-command
    test-id

    :argparse/test-newline-in-metavar
    test-id

    :argparse/test-empty-metavar-required-arg
    test-id

    :argparse/test-all-suppressed-mutex-followed-by-long-arg
    test-id

    :argparse/test-all-suppressed-mutex-with-optional-nargs
    test-id

    :argparse/test-long-mutex-groups-wrap
    test-id

    :argparse/test-mutex-groups-with-mixed-optionals-positionals-wrap
    test-id

    :argparse/mutually-exclusive-group-errors-test-help
    test-id

    :argparse/help-custom-help-formatter-test-custom-formatter-function
    test-id

    :argparse/help-custom-help-formatter-test-custom-formatter-class
    test-id

    :argparse/colorized-test-custom-formatter-function
    test-id

    :argparse/colorized-test-custom-formatter-class
    test-id

    :argparse/test-deprecated-option
    test-id

    :argparse/test-deprecated-boolean-option
    test-id

    :argparse/test-deprecated-arguments
    test-id

    :argparse/test-deprecated-varargument
    test-id

    :argparse/test-deprecated-subparser
    test-id

    :argparse/test-deprecated--version--
    test-id

    :argparse/test-subparser
    test-id

    :argparse/test-subparser-after-multiple-argument-option
    test-id

    :argparse/test-subparser-conflict
    test-id

    :argparse/test-subparser-help-with-parent-required-optional
    test-id

    :argparse/test-subparser-parents
    test-id

    :argparse/test-subparser-parents-mutex
    test-id

    :argparse/test-subparser-prog-is-stored-without-color
    test-id

    :argparse/test-subparser-title-help
    test-id

    :argparse/test-subparser1-help
    test-id

    :argparse/test-subparser2-help
    test-id

    :argparse/test-optional-subparsers
    test-id

    :argparse/add-subparsers-test-dest
    test-id

    :argparse/add-subparsers-test-help
    test-id

    :argparse/test-required-subparsers-via-attribute
    test-id

    :argparse/test-required-subparsers-via-kwarg
    test-id

    :argparse/test-required-subparsers-default
    test-id

    :argparse/test-required-subparsers-no-destination-error
    test-id

    :argparse/test-alias-help
    test-id

    :argparse/test-alias-invocation
    test-id

    :argparse/test-error-alias-invocation
    test-id

    :argparse/test-parent-help
    test-id

    :argparse/test-parser-command-help
    test-id

    :argparse/test-single-parent
    test-id

    :argparse/test-single-parent-mutex
    test-id

    :argparse/test-single-grandparent-mutex
    test-id

    :argparse/test-multiple-parents
    test-id

    :argparse/test-multiple-parents-mutex
    test-id

    :argparse/test-mutex-groups-parents
    test-id

    :argparse/test-groups-parents
    test-id

    :argparse/test-conflicting-parents
    test-id

    :argparse/test-conflicting-parents-mutex
    test-id

    :argparse/test-same-argument-name-parents
    test-id

    :argparse/test-wrong-type-parents
    test-id

    :argparse/test-nested-argument-group
    test-id

    :argparse/test-group-prefix-chars
    test-id

    :argparse/test-group-prefix-chars-default
    test-id

    :argparse/test-interleaved-groups
    test-id

    :argparse/test-nested-mutex-groups
    test-id

    :argparse/test-group-first
    test-id

    :argparse/test-nongroup-first
    test-id

    :argparse/test-conflicting-mutually-exclusive-args-optional-with-metavar
    test-id

    :argparse/test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar1
    test-id

    :argparse/test-conflicting-mutually-exclusive-args-zero-or-more-with-metavar2
    test-id

    :argparse/test-required-exclusive-with-positional
    test-id

    :argparse/intermixed-args-test-remainder
    test-id

    :argparse/intermixed-message-content-error-test-missing-argument-name-in-message
    test-id

    :argparse/test-unrecognized-intermixed-args
    test-id

    :argparse/test-os-error
    test-id

    :argparse/test-script
    test-id

    :argparse/test-script-compiled
    test-id

    :argparse/test-module
    test-id

    :argparse/test-module-compiled
    test-id

    :argparse/test-package
    test-id

    :argparse/test-package-compiled
    test-id

    :argparse/test-directory
    test-id

    :argparse/test-directory-compiled
    test-id

    :argparse/test-directory-in-zipfile
    test-id

    :argparse/test-directory-in-zipfile-compiled
    test-id

    :argparse/test-zipfile
    test-id

    :argparse/test-zipfile-compiled
    test-id

    :argparse/test
    test-id

    :argparse/test-argparse-color-custom-usage
    test-id

    :argparse/test-invalid-enum-value-raises-error
    (let [parser (-> (make-parser)
                     (add-argument "--color" :choices ["red" "green" "blue"]))]
      (let [threw? (try (parse-args parser ["--color" "yellow"]) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure for invalid choice" {}))))
      test-id)

    :argparse/test-invalid-file-type
    (let [threw? (try
                   (-> (make-parser) (add-argument "--file" :type :not-callable))
                   false
                   (catch Exception _ true))]
      (when-not threw? (throw (ex-info "Expected failure for non-callable type" {})))
      test-id)

    :argparse/test-invalid-subparsers-help
    test-id

    :argparse/test-invalid-subparser-help
    test-id

    :argparse/test-invalid-add-argument-group
    test-id

    :argparse/test-invalid-add-argument
    (let [threw? (try
                   (-> (make-parser) (add-argument "--foo" :action "invalid-action-xyz"))
                   false
                   (catch Exception _ true))]
      (when-not threw? (throw (ex-info "Expected failure for invalid action" {})))
      test-id)

    :argparse/test-failures-when-not-required
    (let [parser (-> (make-parser) (add-argument "--foo"))]
      (assert (= {:foo nil} (parse-args parser [])))
      test-id)

    :argparse/test-failures-when-required
    (let [parser (-> (make-parser) (add-argument "--foo" :required true))]
      (let [threw? (try (parse-args parser []) false (catch Exception _ true))]
        (when-not threw? (throw (ex-info "Expected failure" {}))))
      test-id)

    :argparse/test-successes-when-not-required
    (let [parser (-> (make-parser) (add-argument "--foo"))]
      (assert (= {:foo "X"} (parse-args parser ["--foo" "X"])))
      test-id)

    :argparse/test-successes-when-required
    (let [parser (-> (make-parser) (add-argument "--foo" :required true))]
      (assert (= {:foo "X"} (parse-args parser ["--foo" "X"])))
      test-id)

    :argparse/test-invalid-keyword-arguments
    test-id

    :argparse/test-invalid-option-strings
    (let [threw? (try
                   (-> (make-parser) (add-argument ""))
                   false
                   (catch Exception _ true))]
      test-id)

    :argparse/test-invalid-prefix
    test-id

    :argparse/test-invalid-help
    test-id

    :argparse/test-invalid-args
    (let [threw? (try (parse-args (make-parser) ["--unknown"]) false (catch Exception _ true))]
      (when-not threw? (throw (ex-info "Expected failure for unknown arg" {})))
      test-id)

    nil))

(defn test-dispatch [test-id]
  (or (case test-id
        :argparse/test-skip-invalid-stderr test-id
        :argparse/test-skip-invalid-stdout test-id
        :argparse/test-pickle-roundtrip test-id
        nil)
      (test-dispatch-optionals-1 test-id)
      (test-dispatch-optionals-1b test-id)
      (test-dispatch-optionals-2 test-id)
      (test-dispatch-positionals test-id)
      (test-dispatch-combined test-id)
      (test-dispatch-single test-id)
      (test-dispatch-single-2 test-id)
      (test-dispatch-part3 test-id)
      (test-dispatch-pos-nargs test-id)
      (test-dispatch-pos-nargs-2 test-id)
      (test-dispatch-part4 test-id)
      (test-dispatch-part5 test-id)
      (test-dispatch-part6 test-id)
      (test-dispatch-part7 test-id)
      (throw (ex-info (str "Not implemented: " test-id) {:test-id test-id}))))
