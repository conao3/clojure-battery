(ns conao3.battery.optparse
  (:require
   [clojure.string :as str]))

(def ^:private NO_DEFAULT ::no-default)

(def ^:private SUPPRESS_HELP "SUPPRESS")
(def ^:private SUPPRESS_USAGE "SUPPRESS")

(def ^:private VALID_ACTIONS
  #{"store" "store_const" "store_true" "store_false"
    "append" "append_const" "count" "callback" "help" "version"})

(def ^:private TYPED_ACTIONS #{"store" "append" "callback"})
(def ^:private STORE_ACTIONS #{"store" "store_const" "store_true" "store_false" "append" "append_const" "count"})

(def ^:private NO_DEFAULT_VALUE ::no-default-value)

(defn- option-error
  [msg opt-id]
  (ex-info msg {:type ::option-error :msg msg :option-id opt-id}))

(defn- option-value-error
  [msg]
  (ex-info msg {:type ::option-value-error :msg msg}))

(defn- bad-option-error
  [opt-str]
  (ex-info (str "no such option: " opt-str) {:type ::bad-option-error :opt-str opt-str}))

(defn- ambiguous-option-error
  [opt-str possibilities]
  (let [possibilities-str (str/join ", " (sort possibilities))]
    (ex-info (str "ambiguous option: " opt-str " (" possibilities-str "?)")
             {:type ::ambiguous-option-error :opt-str opt-str :possibilities possibilities})))

(defn- option-conflict-error
  [msg opt-id]
  (ex-info msg {:type ::option-conflict-error :msg msg :option-id opt-id}))

(defn- get-opt-id [opt-strings]
  (str/join "/" opt-strings))

(defn- validate-opt-string [opt-str opt-id]
  (cond
    (< (count opt-str) 2)
    (throw (option-error (str "invalid option string '" opt-str
                              "': must be at least two characters long")
                         opt-str))

    (and (= (subs opt-str 0 2) "--") (= (count opt-str) 2))
    (throw (option-error (str "invalid short option string '--': must be "
                              "of the form -x, (x any non-dash char)")
                         opt-str))

    (and (= (subs opt-str 0 1) "-") (not= (subs opt-str 0 2) "--") (> (count opt-str) 2))
    (throw (option-error (str "invalid long option string '" opt-str
                              "': must start with --, followed by non-dash")
                         opt-str))

    (and (= (subs opt-str 0 2) "--") (= (count opt-str) 3) (= (nth opt-str 2) \-))
    (throw (option-error (str "invalid long option string '" opt-str
                              "': must start with --, followed by non-dash")
                         opt-str))

    (and (= (subs opt-str 0 1) "-") (not= (subs opt-str 0 2) "--") (> (count opt-str) 2)
         (= (nth opt-str 1) \-))
    nil

    :else nil))

(defn- validate-opt-strings! [opt-strings]
  (when (empty? opt-strings)
    (throw (ex-info "at least one option string must be supplied"
                    {:type ::type-error})))
  (loop [remaining opt-strings
         valid-so-far []]
    (when (seq remaining)
      (let [s (first remaining)
            opt-prefix (if (seq valid-so-far) (str "option " (str/join "/" valid-so-far) ": ") "")]
        (cond
          (< (count s) 2)
          (throw (option-error (str opt-prefix "invalid option string '" s
                                    "': must be at least two characters long")
                               s))

          (and (str/starts-with? s "--") (= (count s) 2))
          (throw (option-error (str opt-prefix "invalid short option string '--': must be "
                                    "of the form -x, (x any non-dash char)")
                               s))

          (and (str/starts-with? s "--") (>= (count s) 3) (= (nth s 2) \-))
          (throw (option-error (str opt-prefix "invalid long option string '" s
                                    "': must start with --, followed by non-dash")
                               s))

          (and (str/starts-with? s "-") (not (str/starts-with? s "--")) (> (count s) 2))
          (throw (option-error (str opt-prefix "invalid long option string '" s
                                    "': must start with --, followed by non-dash")
                               s))

          :else
          (recur (rest remaining) (conj valid-so-far s)))))))

(defn- normalize-type [t]
  (cond
    (= t "str") "string"
    (= t str) "string"
    (= t int) "int"
    (= t float) "float"
    (keyword? t) (name t)
    (string? t) t
    (nil? t) nil
    :else nil))

(defn- valid-type? [t valid-types]
  (contains? (set valid-types) t))

(def ^:private DEFAULT_TYPES ["string" "int" "float" "complex" "choice"])

(defn- parse-num [s type-fn]
  (let [digits #(if (= % "") "0" %)]
    (cond
      (str/starts-with? s "0x") (type-fn (Long/parseLong (digits (subs s 2)) 16))
      (str/starts-with? s "0X") (type-fn (Long/parseLong (digits (subs s 2)) 16))
      (str/starts-with? s "0b") (type-fn (Long/parseLong (digits (subs s 2)) 2))
      (str/starts-with? s "0B") (type-fn (Long/parseLong (digits (subs s 2)) 2))
      (and (str/starts-with? s "0") (> (count s) 1) (every? #(Character/isDigit %) (subs s 1)))
      (type-fn (Long/parseLong s 8))
      :else (type-fn (Long/parseLong s 10)))))

(defn- check-builtin [option opt value type-fn type-name]
  (try
    (parse-num value type-fn)
    (catch NumberFormatException _
      (throw (option-value-error
              (str "option " opt ": invalid " type-name " value: '" value "'"))))))

(defn- check-int [option opt value]
  (check-builtin option opt value long "integer"))

(defn- check-float [option opt value]
  (try
    (Double/parseDouble value)
    (catch NumberFormatException _
      (throw (option-value-error
              (str "option " opt ": invalid floating-point value: '" value "'"))))))

(defn- check-choice [option opt value]
  (let [choices (:choices option)]
    (if (contains? (set choices) value)
      value
      (throw (option-value-error
              (str "option " opt ": invalid choice: '" value "' (choose from "
                   (str/join ", " (map #(str "'" % "'") choices)) ")"))))))

(def ^:private DEFAULT_TYPE_CHECKER
  {"int" check-int
   "float" check-float
   "choice" check-choice
   "string" nil
   "complex" nil})

(defn make-option
  [& args]
  (when (empty? args)
    (throw (ex-info "at least one option string must be supplied"
                    {:type ::type-error})))
  (let [opt-strings (vec (take-while string? args))
        rest-args (drop-while string? args)
        opts (if (seq rest-args)
               (apply hash-map rest-args)
               {})
        _ (when (empty? opt-strings)
            (throw (ex-info "at least one option string must be supplied"
                            {:type ::type-error})))
        _ (validate-opt-strings! opt-strings)
        opt-id (get-opt-id opt-strings)
        short-opts (filterv #(and (str/starts-with? % "-") (not (str/starts-with? % "--"))) opt-strings)
        long-opts (filterv #(str/starts-with? % "--") opt-strings)
        valid-keys #{:action :type :dest :default :choices :const :nargs :help
                     :metavar :callback :callback-args :callback-kwargs}
        _ (let [invalid-keys (sort (map name (remove valid-keys (keys opts))))]
            (when (seq invalid-keys)
              (throw (option-error
                      (str "option " opt-id ": invalid keyword arguments: "
                           (str/join ", " invalid-keys))
                      opt-id))))
        action (get opts :action "store")
        _ (when-not (contains? VALID_ACTIONS action)
            (throw (option-error (str "option " opt-id ": invalid action: '" action "'")
                                 opt-id)))
        raw-type (:type opts)
        type-str (normalize-type raw-type)
        _ (when (and (some? raw-type) (nil? type-str))
            (throw (option-error (str "option " opt-id ": invalid option type: '"
                                      (if (class? raw-type) (.getName ^Class raw-type) raw-type)
                                      "'")
                                 opt-id)))
        _ (when (and type-str (not (valid-type? type-str DEFAULT_TYPES)))
            (throw (option-error (str "option " opt-id ": invalid option type: '" type-str "'")
                                 opt-id)))
        _ (when (and type-str (contains? #{"store_true" "store_false" "store_const"
                                           "count" "help" "version"} action))
            (throw (option-error (str "option " opt-id ": must not supply a type for action '"
                                      action "'")
                                 opt-id)))
        effective-type (if (and (nil? type-str) (some? (:choices opts)))
                         "choice"
                         type-str)
        _ (when (and (= effective-type "choice") (nil? (:choices opts)))
            (throw (option-error (str "option " opt-id
                                      ": must supply a list of choices for type 'choice'")
                                 opt-id)))
        _ (when (and (= effective-type "choice") (string? (:choices opts)))
            (throw (option-error (str "option " opt-id
                                      ": choices must be a list of strings ('String' supplied)")
                                 opt-id)))
        _ (when (and (some? (:choices opts)) (not= effective-type "choice"))
            (throw (option-error (str "option " opt-id
                                      ": must not supply choices for type '" effective-type "'")
                                 opt-id)))
        _ (when (and (contains? opts :const) (= action "store"))
            (throw (option-error (str "option " opt-id
                                      ": 'const' must not be supplied for action 'store'")
                                 opt-id)))
        _ (when (and (contains? opts :nargs) (= action "count"))
            (throw (option-error (str "option " opt-id
                                      ": 'nargs' must not be supplied for action 'count'")
                                 opt-id)))
        _ (when (and (= action "callback") (not (fn? (:callback opts))))
            (throw (option-error (str "option " opt-id ": callback not callable: '"
                                      (:callback opts) "'")
                                 opt-id)))
        _ (when (and (= action "callback")
                     (contains? opts :callback-args)
                     (not (sequential? (:callback-args opts))))
            (throw (option-error (str "option " opt-id
                                      ": callback_args, if supplied, must be a tuple: not '"
                                      (:callback-args opts) "'")
                                 opt-id)))
        _ (when (and (= action "callback")
                     (contains? opts :callback-kwargs)
                     (not (map? (:callback-kwargs opts))))
            (throw (option-error (str "option " opt-id
                                      ": callback_kwargs, if supplied, must be a dict: not '"
                                      (:callback-kwargs opts) "'")
                                 opt-id)))
        _ (when (and (not= action "callback") (some? (:callback opts)))
            (throw (option-error (str "option " opt-id
                                      ": callback supplied ('" (:callback opts)
                                      "') for non-callback option")
                                 opt-id)))
        _ (when (and (not= action "callback") (contains? opts :callback-args))
            (throw (option-error (str "option " opt-id
                                      ": callback_args supplied for non-callback option")
                                 opt-id)))
        _ (when (and (not= action "callback") (contains? opts :callback-kwargs))
            (throw (option-error (str "option " opt-id
                                      ": callback_kwargs supplied for non-callback option")
                                 opt-id)))
        dest (or (:dest opts)
                 (when (seq long-opts)
                   (-> (first long-opts) (subs 2) (str/replace "-" "_")))
                 (when (seq short-opts)
                   (subs (first short-opts) 1)))
        default (get opts :default NO_DEFAULT_VALUE)
        nargs (get opts :nargs 1)]
    {:opt-strings opt-strings
     :short-opts short-opts
     :long-opts long-opts
     :action action
     :type effective-type
     :dest dest
     :default default
     :choices (:choices opts)
     :const (:const opts)
     :nargs nargs
     :help (:help opts)
     :metavar (:metavar opts)
     :callback (:callback opts)
     :callback-args (get opts :callback-args [])
     :callback-kwargs (get opts :callback-kwargs {})}))

(defn- get-metavar [opt]
  (or (:metavar opt)
      (when (:dest opt)
        (str/upper-case (:dest opt)))))

(defn- format-option-strings [opt short-first]
  (let [needs-arg? (or (contains? #{"store" "append"} (:action opt))
                       (and (= (:action opt) "callback") (some? (:type opt))))
        mv (when needs-arg? (get-metavar opt))
        nargs (:nargs opt)
        format-short (fn [s]
                       (if needs-arg?
                         (str s " " mv)
                         s))
        format-long (fn [s]
                      (if needs-arg?
                        (str s "=" mv)
                        s))
        shorts (map format-short (:short-opts opt))
        longs (map format-long (:long-opts opt))
        all (if short-first
              (concat shorts longs)
              (concat longs shorts))]
    (str/join ", " all)))

(defn- wrap-text [text width indent]
  (if (or (nil? text) (= text ""))
    [""]
    (let [words (str/split text #"\s+")
          lines (reduce (fn [acc word]
                          (let [current (last acc)
                                line (if (= current "") word (str current " " word))]
                            (if (> (count line) width)
                              (if (= current "")
                                (conj (vec (butlast acc)) word)
                                (conj acc word))
                              (conj (vec (butlast acc)) line))))
                        [""]
                        words)]
      lines)))

(defn- format-help-text [text width current-col indent]
  (if (nil? text)
    ""
    (let [avail (- width (count indent))
          lines (if (<= avail 0)
                  (wrap-text text 10 indent)
                  (wrap-text text avail indent))]
      (str/join (str "\n" indent) lines))))

(defn make-parser
  [& args]
  (let [opts (apply hash-map args)
        usage (get opts :usage "%prog [options]")
        prog (get opts :prog nil)
        description (get opts :description nil)
        epilog (get opts :epilog nil)
        version (get opts :version nil)
        conflict-handler (get opts :conflict-handler "error")
        option-list (get opts :option-list [])
        formatter (get opts :formatter nil)
        option-class (get opts :option-class nil)
        add-help-option (get opts :add-help-option true)
        process-default-values (get opts :process-default-values true)
        parser (atom {:id (gensym "parser")
                      :option-list []
                      :option-map {}
                      :defaults {}
                      :prog prog
                      :usage usage
                      :description description
                      :epilog epilog
                      :version version
                      :conflict-handler conflict-handler
                      :option-groups []
                      :allow-interspersed-args true
                      :formatter (or formatter {:short-first true
                                                :default-tag "%default"
                                                :type :indent})
                      :option-class option-class
                      :process-default-values process-default-values})]
    (when add-help-option
      (swap! parser assoc :option-map
             (merge (:option-map @parser)
                    {"-h" ::help-option
                     "--help" ::help-option}))
      (swap! parser update :option-list conj
             {:opt-strings ["-h" "--help"]
              :short-opts ["-h"]
              :long-opts ["--help"]
              :action "help"
              :type nil
              :dest nil
              :default NO_DEFAULT_VALUE
              :choices nil
              :const nil
              :nargs 1
              :help "show this help message and exit"
              :metavar nil
              :callback nil
              :callback-args []
              :callback-kwargs {}}))
    (when version
      (swap! parser assoc :option-map
             (merge (:option-map @parser)
                    {"--version" ::version-option}))
      (swap! parser update :option-list conj
             {:opt-strings ["--version"]
              :short-opts []
              :long-opts ["--version"]
              :action "version"
              :type nil
              :dest nil
              :default NO_DEFAULT_VALUE
              :choices nil
              :const nil
              :nargs 1
              :help "show program's version number and exit"
              :metavar nil
              :callback nil
              :callback-args []
              :callback-kwargs {}}))
    (doseq [opt option-list]
      (let [p @parser
            conflict-handler (:conflict-handler p)]
        (doseq [s (:opt-strings opt)]
          (when (contains? (:option-map p) s)
            (let [existing (get (:option-map p) s)
                  existing-opt (when (map? existing)
                                 existing)
                  opt-id (get-opt-id (:opt-strings opt))]
              (if (= conflict-handler "error")
                (throw (option-conflict-error
                        (str "option " opt-id ": conflicting option string(s): " s)
                        opt-id))
                (when existing-opt
                  (swap! parser update :option-map
                         (fn [om]
                           (reduce (fn [m os]
                                     (if (= (get m os) existing-opt)
                                       (dissoc m os)
                                       m))
                                   om
                                   (:opt-strings existing-opt))))
                  (swap! parser update :option-list
                         (fn [ol]
                           (mapv (fn [o]
                                   (if (= o existing-opt)
                                     (update o :opt-strings
                                             (fn [ss] (filterv #(not (contains? (set (:opt-strings opt)) %)) ss)))
                                     o))
                                 ol)))
                  (swap! parser update :option-list
                         (fn [ol]
                           (mapv (fn [o]
                                   (if (= o (first (filter #(and (map? %) (some (fn [s2] (= s s2)) (:opt-strings %))) (:option-list @parser))))
                                     (update o :short-opts
                                             (fn [ss] (filterv #(not (contains? (set (:opt-strings opt)) %)) ss)))
                                     o))
                                 ol))))))))
        (doseq [s (:opt-strings opt)]
          (when (contains? (:option-map @parser) s)
            (let [existing (get (:option-map @parser) s)]
              (when (map? existing)
                (when (= conflict-handler "resolve")
                  (let [e-opt existing
                        new-strings (filterv #(not (= % s)) (:opt-strings e-opt))
                        new-short (filterv #(not (= % s)) (:short-opts e-opt))
                        new-long (filterv #(not (= % s)) (:long-opts e-opt))]
                    (swap! parser update :option-map dissoc s)
                    (swap! parser update :option-list
                           (fn [ol]
                             (mapv (fn [o]
                                     (if (= o e-opt)
                                       (assoc o
                                              :opt-strings new-strings
                                              :short-opts new-short
                                              :long-opts new-long)
                                       o))
                                   ol)))))))))
        (swap! parser update :option-map
               (fn [om]
                 (reduce (fn [m s] (assoc m s opt)) om (:opt-strings opt))))
        (swap! parser update :option-list conj opt)
        (when (not= (:default opt) NO_DEFAULT_VALUE)
          (swap! parser assoc-in [:defaults (:dest opt)] (:default opt)))))
    @parser))

(defn- parser-add-option [parser opt]
  (let [conflict-handler (:conflict-handler parser)
        new-opt-strings (set (:opt-strings opt))
        conflicts (filterv #(contains? new-opt-strings %) (keys (:option-map parser)))]
    (when (and (seq conflicts) (= conflict-handler "error"))
      (let [opt-id (get-opt-id (:opt-strings opt))]
        (throw (option-conflict-error
                (str "option " opt-id ": conflicting option string(s): " (first conflicts))
                opt-id))))
    (let [parser (if (and (seq conflicts) (= conflict-handler "resolve"))
                   (let [conflicting-opts (distinct (filterv map? (map #(get (:option-map parser) %) conflicts)))]
                     (reduce (fn [p conflict-opt]
                               (let [new-strings (filterv #(not (contains? new-opt-strings %))
                                                          (:opt-strings conflict-opt))
                                     new-short (filterv #(not (contains? new-opt-strings %))
                                                        (:short-opts conflict-opt))
                                     new-long (filterv #(not (contains? new-opt-strings %))
                                                       (:long-opts conflict-opt))
                                     updated-opt (assoc conflict-opt
                                                        :opt-strings new-strings
                                                        :short-opts new-short
                                                        :long-opts new-long)]
                                 (-> p
                                     (update :option-map
                                             (fn [om]
                                               (as-> om m
                                                 (reduce (fn [m2 s] (dissoc m2 s)) m (:opt-strings conflict-opt))
                                                 (reduce (fn [m2 s] (assoc m2 s updated-opt)) m new-strings))))
                                     (update :option-list
                                             (fn [ol]
                                               (mapv (fn [o]
                                                       (if (= o conflict-opt)
                                                         updated-opt
                                                         o))
                                                     ol))))))
                             parser
                             conflicting-opts))
                   parser)]
      (-> parser
          (update :option-map
                  (fn [om]
                    (reduce (fn [m s] (assoc m s opt)) om (:opt-strings opt))))
          (update :option-list conj opt)
          (cond-> (and (:dest opt) (not= (:default opt) NO_DEFAULT_VALUE))
            (assoc-in [:defaults (:dest opt)] (:default opt)))))))

(defn add-option [parser & args]
  (cond
    (and (= (count args) 1) (nil? (first args)))
    (throw (ex-info "not an Option instance: None" {:type ::type-error}))

    (and (>= (count args) 2) (every? nil? args))
    (throw (ex-info "invalid arguments" {:type ::type-error}))

    (and (= (count args) 1) (map? (first args)))
    (parser-add-option parser (first args))

    :else
    (let [opt (apply make-option args)]
      (parser-add-option parser opt))))

(defn get-option [parser opt-str]
  (let [opt (get (:option-map parser) opt-str)]
    (when (map? opt) opt)))

(defn has-option [parser opt-str]
  (contains? (:option-map parser) opt-str))

(defn remove-option [parser opt-str]
  (when-not (contains? (:option-map parser) opt-str)
    (throw (ex-info (str "no such option '" opt-str "'")
                    {:type ::value-error})))
  (let [opt (get (:option-map parser) opt-str)]
    (if (map? opt)
      (-> parser
          (update :option-map
                  (fn [om]
                    (reduce (fn [m s] (dissoc m s)) om (:opt-strings opt))))
          (update :option-list
                  (fn [ol]
                    (mapv (fn [o]
                            (if (= o opt)
                              (let [new-strings (filterv #(not= % opt-str) (:opt-strings o))
                                    new-short (filterv #(not= % opt-str) (:short-opts o))
                                    new-long (filterv #(not= % opt-str) (:long-opts o))]
                                (assoc o
                                       :opt-strings new-strings
                                       :short-opts new-short
                                       :long-opts new-long))
                              o))
                          ol))))
      parser)))

(defn set-conflict-handler [parser handler]
  (when-not (contains? #{"error" "resolve"} handler)
    (throw (ex-info (str "invalid conflict_resolution value '" handler "'")
                    {:type ::value-error})))
  (assoc parser :conflict-handler handler))

(defn set-default [parser key value]
  (assoc-in parser [:defaults key] value))

(defn set-defaults [parser & kvs]
  (reduce (fn [p [k v]] (assoc-in p [:defaults k] v)) parser (partition 2 kvs)))

(defn disable-interspersed-args [parser]
  (assoc parser :allow-interspersed-args false))

(defn enable-interspersed-args [parser]
  (assoc parser :allow-interspersed-args true))

(defn set-usage [parser usage]
  (assoc parser :usage usage))

(defn set-description [parser desc]
  (assoc parser :description desc))

(defn set-process-default-values [parser v]
  (assoc parser :process-default-values v))

(defn- get-prog [parser]
  (or (:prog parser)
      (-> (first *command-line-args*)
          (or (System/getProperty "sun.java.command" "clojure"))
          (str/split #"[\\/]")
          last)))

(defn- expand-prog [s prog]
  (str/replace s "%prog" prog))

(defn get-usage [parser]
  (let [usage (:usage parser)
        prog (get-prog parser)]
    (if (= usage SUPPRESS_USAGE)
      ""
      (str "Usage: " (expand-prog usage prog) "\n"))))

(defn get-version [parser]
  (when-let [v (:version parser)]
    (expand-prog v (get-prog parser))))

(defn- collect-all-opts [parser]
  (concat (:option-list parser)
          (mapcat :option-list (:option-groups parser))))

(defn get-default-values [parser]
  (let [all-opts (collect-all-opts parser)
        base-defaults (reduce (fn [m opt]
                                (if (and (:dest opt)
                                         (not= (:default opt) NO_DEFAULT_VALUE))
                                  (assoc m (:dest opt) (:default opt))
                                  m))
                              {}
                              all-opts)
        merged (merge base-defaults (:defaults parser))
        system-actions #{"help" "version"}
        dest-set (set (keep #(when (and (:dest %) (not (contains? system-actions (:action %)))) (:dest %))
                            all-opts))
        with-nils (reduce (fn [m dest]
                            (if (contains? m dest)
                              m
                              (assoc m dest nil)))
                          merged
                          dest-set)]
    (if (:process-default-values parser)
      (reduce-kv (fn [m k v]
                   (let [matching-opt (first (filter #(= (:dest %) (name k)) all-opts))]
                     (if (and matching-opt (:type matching-opt) (string? v))
                       (let [checker (get DEFAULT_TYPE_CHECKER (:type matching-opt))]
                         (if checker
                           (try
                             (assoc m k (checker matching-opt (str "--" (name k)) v))
                             (catch Exception _ (assoc m k v)))
                           (assoc m k v)))
                       (assoc m k v))))
                 {}
                 with-nils)
      with-nils)))

(defn add-option-group [parser & args]
  (cond
    (and (= (count args) 1) (nil? (first args)))
    (throw (ex-info "not an OptionGroup instance: None" {:type ::type-error}))

    (and (>= (count args) 2) (every? nil? args))
    (throw (ex-info "invalid arguments" {:type ::type-error}))

    (and (= (count args) 1) (map? (first args)) (= ::option-group (:type (first args))))
    (let [group (first args)]
      (when (not= (:id (:parser group)) (:id parser))
        (throw (ex-info "invalid OptionGroup (wrong parser)"
                        {:type ::value-error})))
      (update parser :option-groups conj group))

    :else
    (throw (ex-info "not an OptionGroup instance" {:type ::type-error}))))

(defn make-option-group [parser title & args]
  (let [opts (apply hash-map args)
        description (:description opts)]
    {:type ::option-group
     :title title
     :description description
     :option-list []
     :option-map {}
     :parser parser
     :conflict-handler (:conflict-handler parser "error")}))

(defn option-group-add-option [group & args]
  (cond
    (and (= (count args) 1) (nil? (first args)))
    (throw (ex-info "not an Option instance: None" {:type ::type-error}))

    (and (= (count args) 1) (map? (first args)))
    (let [opt (first args)
          parser (:parser group)
          conflict-handler (:conflict-handler group "error")
          conflicts (filter #(has-option parser %) (:opt-strings opt))]
      (when (seq conflicts)
        (if (= conflict-handler "error")
          (let [opt-id (get-opt-id (:opt-strings opt))
                conflict-str (first conflicts)]
            (throw (option-conflict-error
                    (str "option " opt-id ": conflicting option string(s): " conflict-str)
                    opt-id)))))
      (-> group
          (update :option-map
                  (fn [om] (reduce (fn [m s] (assoc m s opt)) om (:opt-strings opt))))
          (update :option-list conj opt)))

    :else
    (let [opt (apply make-option args)
          parser (:parser group)
          conflict-handler (:conflict-handler group "error")
          conflicts (filter #(has-option parser %) (:opt-strings opt))]
      (when (seq conflicts)
        (if (= conflict-handler "error")
          (let [opt-id (get-opt-id (:opt-strings opt))
                conflict-str (first conflicts)]
            (throw (option-conflict-error
                    (str "option " opt-id ": conflicting option string(s): " conflict-str)
                    opt-id)))))
      (-> group
          (update :option-map
                  (fn [om] (reduce (fn [m s] (assoc m s opt)) om (:opt-strings opt))))
          (update :option-list conj opt)))))

(defn get-option-group [parser opt-str]
  (first (filter #(contains? (:option-map %) opt-str) (:option-groups parser))))

(defn- match-abbrev [s wordmap]
  (if (contains? wordmap s)
    s
    (let [matches (filterv #(str/starts-with? % s) (keys wordmap))]
      (cond
        (= (count matches) 1) (first matches)
        (> (count matches) 1)
        (throw (ambiguous-option-error s matches))
        :else
        (throw (bad-option-error s))))))

(defn- get-all-option-map [parser]
  (let [base (:option-map parser)
        group-opts (mapcat (fn [g]
                             (map (fn [[k v]] [k v]) (:option-map g)))
                           (:option-groups parser))]
    (into base group-opts)))

(defn- convert-value [opt opt-str value]
  (let [type-str (:type opt)
        checker (get DEFAULT_TYPE_CHECKER type-str)]
    (if checker
      (checker opt opt-str value)
      value)))

(declare format-help)

(defn- take-action [opt opt-str value values rargs largs parser]
  (let [action (:action opt)
        dest (:dest opt)]
    (case action
      "store"
      (assoc values dest (if (vector? value) value (convert-value opt opt-str value)))

      "store_const"
      (assoc values dest (:const opt))

      "store_true"
      (assoc values dest true)

      "store_false"
      (assoc values dest false)

      "append"
      (let [converted (if (vector? value) value (convert-value opt opt-str value))
            current (or (get values dest) [])]
        (assoc values dest (conj (vec current) converted)))

      "append_const"
      (let [current (or (get values dest) [])]
        (assoc values dest (conj (vec current) (:const opt))))

      "count"
      (let [current (get values dest 0)
            new-val (if (nil? current) 1 (inc current))]
        (assoc values dest new-val))

      "callback"
      (let [cb (:callback opt)
            cb-args (:callback-args opt [])
            cb-kwargs (:callback-kwargs opt {})
            parser-ref (get parser :parse-state-ref)]
        (when cb
          (apply cb opt opt-str value parser (concat cb-args (mapcat identity cb-kwargs))))
        values)

      "help"
      (do
        (println (format-help parser))
        (throw (ex-info "exit" {:type ::exit :status 0})))

      "version"
      (do
        (println (get-version parser))
        (throw (ex-info "exit" {:type ::exit :status 0})))

      values)))

(declare format-help)

(defn- process-short-opt [parser opt-str rargs values largs]
  (let [opt-char (str "-" (nth opt-str 1))
        rest-str (when (> (count opt-str) 2) (subs opt-str 2))
        option-map (get-all-option-map parser)
        opt (get option-map opt-char)]
    (when-not opt
      (throw (bad-option-error opt-char)))
    (let [needs-arg? (contains? #{"store" "append"} (:action opt))
          needs-typed-arg? (and (not (nil? (:type opt)))
                                (= (:action opt) "callback"))
          effective-needs-arg? (or needs-arg? needs-typed-arg?)
          nargs (when effective-needs-arg? (:nargs opt 1))
          [value new-rargs new-rest]
          (cond
            (and effective-needs-arg? rest-str (= nargs 1))
            [rest-str rargs nil]

            (and effective-needs-arg? (= nargs 1))
            (if (empty? rargs)
              (throw (ex-info (str opt-char " option requires 1 argument")
                              {:type ::parse-error :msg (str opt-char " option requires 1 argument")}))
              [(first rargs) (vec (rest rargs)) nil])

            (and effective-needs-arg? (> nargs 1))
            (let [available (if rest-str
                              (concat [rest-str] rargs)
                              (vec rargs))
                  _ (when (< (count available) nargs)
                      (throw (ex-info (str opt-char " option requires " nargs " arguments")
                                      {:type ::parse-error
                                       :msg (str opt-char " option requires " nargs " arguments")})))
                  taken (vec (take nargs available))
                  remaining (drop nargs available)
                  typed-val (try
                              (mapv #(convert-value opt opt-char %) taken)
                              (catch Exception e (throw e)))]
              [(apply vector typed-val) (vec remaining) nil])

            :else
            [nil rargs rest-str])]
      {:opt opt
       :opt-str opt-char
       :value value
       :nargs nargs
       :new-rargs new-rargs
       :rest-str new-rest})))

(defn- process-long-opt [parser opt-str rargs values]
  (let [[opt-name explicit-value]
        (if (str/includes? opt-str "=")
          (let [idx (str/index-of opt-str "=")]
            [(subs opt-str 0 idx) (subs opt-str (inc idx))])
          [opt-str nil])
        option-map (get-all-option-map parser)
        long-options (filterv #(str/starts-with? % "--") (keys option-map))
        matched-name (try
                       (match-abbrev opt-name (zipmap long-options (repeat nil)))
                       (catch Exception e
                         (let [data (ex-data e)]
                           (case (:type data)
                             ::bad-option-error (throw (bad-option-error opt-name))
                             ::ambiguous-option-error
                             (throw (ambiguous-option-error opt-name (:possibilities data)))
                             (throw e)))))
        opt (get option-map matched-name)
        needs-arg? (contains? #{"store" "append"} (:action opt))
        needs-typed-arg? (and (not (nil? (:type opt))) (= (:action opt) "callback"))
        effective-needs-arg? (or needs-arg? needs-typed-arg?)
        nargs (:nargs opt 1)]
    (when (and (not effective-needs-arg?) explicit-value)
      (throw (ex-info (str matched-name " option does not take a value")
                      {:type ::parse-error
                       :msg (str matched-name " option does not take a value")})))
    (let [[value new-rargs]
          (cond
            (and effective-needs-arg? explicit-value (= nargs 1))
            [explicit-value rargs]

            (and effective-needs-arg? (= nargs 1))
            (if (empty? rargs)
              (throw (ex-info (str matched-name " option requires 1 argument")
                              {:type ::parse-error
                               :msg (str matched-name " option requires 1 argument")}))
              [(first rargs) (vec (rest rargs))])

            (and effective-needs-arg? (> nargs 1))
            (let [available (if explicit-value
                              (concat [explicit-value] rargs)
                              (vec rargs))
                  _ (when (< (count available) nargs)
                      (throw (ex-info (str matched-name " option requires " nargs " arguments")
                                      {:type ::parse-error
                                       :msg (str matched-name " option requires " nargs " arguments")})))
                  taken (vec (take nargs available))
                  remaining (drop nargs available)
                  typed-val (mapv #(convert-value opt matched-name %) taken)]
              [(apply vector typed-val) (vec remaining)])

            :else
            [nil rargs])]
      {:opt opt
       :opt-str matched-name
       :value value
       :nargs nargs
       :new-rargs new-rargs})))

(defn parse-args [parser args]
  (let [defaults (get-default-values parser)
        values (atom defaults)
        rargs (atom (vec args))
        largs (atom [])
        parse-state (atom {:rargs rargs :largs largs :values values})
        parser-with-state (assoc parser :parse-state parse-state)]
    (try
      (loop []
        (when (seq @rargs)
          (let [arg (first @rargs)]
            (swap! rargs #(vec (rest %)))
            (cond
              (= arg "--")
              (do
                (swap! largs into @rargs)
                (reset! rargs []))

              (and (str/starts-with? arg "--") (> (count arg) 2))
              (let [result (process-long-opt parser arg @rargs @values)]
                (reset! rargs (vec (:new-rargs result)))
                (let [opt (:opt result)
                      opt-str (:opt-str result)
                      value (:value result)
                      action (:action opt)]
                  (if (= action "callback")
                    (let [cb (:callback opt)
                          cb-args (:callback-args opt [])
                          parse-proxy {:rargs rargs
                                       :largs largs
                                       :values values}]
                      (when cb
                        (apply cb opt opt-str value parse-proxy
                               (concat cb-args (mapcat identity (:callback-kwargs opt {}))))))
                    (reset! values (take-action opt opt-str value @values @rargs @largs parser))))
                (recur))

              (and (str/starts-with? arg "-") (> (count arg) 1))
              (let [result (process-short-opt parser arg @rargs @values @largs)]
                (reset! rargs (vec (:new-rargs result)))
                (let [opt (:opt result)
                      opt-str (:opt-str result)
                      value (:value result)
                      rest-str (:rest-str result)
                      action (:action opt)]
                  (if (= action "callback")
                    (let [cb (:callback opt)
                          cb-args (:callback-args opt [])
                          parse-proxy {:rargs rargs
                                       :largs largs
                                       :values values}]
                      (when cb
                        (apply cb opt opt-str value parse-proxy
                               (concat cb-args (mapcat identity (:callback-kwargs opt {}))))))
                    (reset! values (take-action opt opt-str value @values @rargs @largs parser)))
                  (when rest-str
                    (swap! rargs #(into [(str "-" rest-str)] %))))
                (recur))

              :else
              (do
                (if (:allow-interspersed-args parser)
                  (do
                    (swap! largs conj arg)
                    (recur))
                  (do
                    (swap! largs conj arg)
                    (swap! largs into @rargs)
                    (reset! rargs []))))))))
      (catch Exception e
        (let [data (ex-data e)]
          (cond
            (= (:type data) ::exit) nil
            (= (:type data) ::parse-error) (throw e)
            (= (:type data) ::bad-option-error) (throw e)
            (= (:type data) ::ambiguous-option-error) (throw e)
            (= (:type data) ::option-value-error) (throw e)
            :else (throw e)))))
    [@values (vec @largs)]))

(defn- format-description [parser prog width]
  (when-let [desc (:description parser)]
    (let [expanded (expand-prog desc prog)
          words (str/split expanded #"\s+")
          lines (reduce (fn [acc word]
                          (let [current (last acc)
                                line (if (= current "") word (str current " " word))]
                            (if (> (count line) width)
                              (if (= current "")
                                (conj (vec (butlast acc)) word)
                                (conj acc word))
                              (conj (vec (butlast acc)) line))))
                        [""]
                        words)]
      (str (str/join "\n" lines) "\n"))))

(defn- format-option-help [opt short-first width indent]
  (let [opt-str (format-option-strings opt short-first)
        help-text (let [h (:help opt)
                        default-tag (get-in {} [:formatter :default-tag] "%default")]
                    h)
        opt-col-width 24
        opt-str-padded (if (<= (count opt-str) (- opt-col-width 2))
                         (format (str "%-" (- opt-col-width 1) "s") opt-str)
                         (str opt-str "\n" (apply str (repeat opt-col-width " "))))]
    (if help-text
      (let [avail (- width opt-col-width)
            words (str/split help-text #"\s+")
            first-line-avail (- width opt-col-width)
            lines (reduce (fn [acc word]
                            (let [current (last acc)
                                  line (if (= current "") word (str current " " word))]
                              (if (and (pos? avail) (> (count line) (if (pos? avail) avail 10)))
                                (if (= current "")
                                  (conj (vec (butlast acc)) word)
                                  (conj acc word))
                                (conj (vec (butlast acc)) line))))
                          [""]
                          words)
            formatted-lines (str/join (str "\n" (apply str (repeat opt-col-width " "))) lines)]
        (str indent opt-str-padded formatted-lines))
      (str indent opt-str))))

(defn format-help [parser]
  (let [prog (get-prog parser)
        usage-str (get-usage parser)
        desc (format-description parser prog 79)
        formatter (:formatter parser)
        short-first (get formatter :short-first true)
        default-tag (get formatter :default-tag "%default")
        formatter-type (get formatter :type :indent)
        width (or (when-let [cols (System/getenv "COLUMNS")]
                    (try (Integer/parseInt cols) (catch Exception _ nil)))
                  80)
        usable-width (- width 1)
        indent "  "
        opt-col-width 24
        help-col-start opt-col-width

        format-opt-strings (fn [opt]
                             (format-option-strings opt short-first))

        expand-default (fn [help-str opt-map default-values]
                         (if (and default-tag help-str)
                           (let [dest (:dest opt-map)
                                 dval (get default-values dest)]
                             (str/replace help-str default-tag
                                          (if (nil? dval) "none" (str dval))))
                           help-str))

        format-single-opt (fn [opt default-values]
                            (let [opt-str (format-opt-strings opt)
                                  help-raw (:help opt)
                                  help-expanded (if (and default-tag help-raw)
                                                  (let [dest (:dest opt)
                                                        dval (get default-values (keyword dest)
                                                                  (get default-values dest))]
                                                    (str/replace help-raw
                                                                 (or default-tag "%default")
                                                                 (if (nil? dval) "none" (str dval))))
                                                  help-raw)
                                  opt-col-w (case formatter-type
                                              :titled 0
                                              opt-col-width)
                                  this-indent (case formatter-type
                                                :titled ""
                                                indent)
                                  opt-plus-indent (str this-indent opt-str)]
                              (if (nil? help-expanded)
                                (str opt-plus-indent "\n")
                                (let [help-indent-str (apply str (repeat (+ (count this-indent) opt-col-w) " "))
                                      avail-w (- usable-width (+ (count this-indent) opt-col-w))
                                      effective-w (if (<= avail-w 0) 11 avail-w)]
                                  (if (< (count opt-str) opt-col-w)
                                    (let [padded (format (str "%-" opt-col-w "s") opt-str)
                                          wrapped (wrap-text help-expanded effective-w "")]
                                      (str this-indent padded
                                           (str/join (str "\n" help-indent-str) wrapped)
                                           "\n"))
                                    (let [wrapped (wrap-text help-expanded effective-w "")]
                                      (str this-indent opt-str "\n"
                                           help-indent-str
                                           (str/join (str "\n" help-indent-str) wrapped)
                                           "\n")))))))

        default-values (get-default-values parser)

        opts-text (str/join ""
                            (map #(format-single-opt % default-values)
                                 (:option-list parser)))

        groups-text (str/join ""
                              (map (fn [group]
                                     (let [group-opts (str/join ""
                                                                (map #(format-single-opt % default-values)
                                                                     (:option-list group)))]
                                       (str "\n"
                                            (case formatter-type
                                              :titled ""
                                              "  ")
                                            (:title group) ":\n"
                                            (when-let [gd (:description group)]
                                              (let [words (str/split gd #"\s+")
                                                    lines (reduce (fn [acc word]
                                                                    (let [current (last acc)
                                                                          line (if (= current "") word (str current " " word))]
                                                                      (if (> (count line) (- usable-width 4))
                                                                        (if (= current "")
                                                                          (conj (vec (butlast acc)) word)
                                                                          (conj acc word))
                                                                        (conj (vec (butlast acc)) line))))
                                                                  [""]
                                                                  words)]
                                                (str "    " (str/join "\n    " lines) "\n")))
                                            group-opts)))
                                   (:option-groups parser)))

        header (case formatter-type
                 :titled (str "Usage\n" (apply str (repeat 5 "=")) "\n"
                              "  " (expand-prog (if (= (:usage parser) SUPPRESS_USAGE) "" (:usage parser)) prog) "\n")
                 (str usage-str))

        opts-header (case formatter-type
                      :titled "\nOptions\n=======\n"
                      "\nOptions:\n")]
    (str header
         (when desc (str "\n" desc))
         opts-header
         opts-text
         groups-text
         (when-let [epi (:epilog parser)]
           (str "\n" epi "\n")))))

(defn- intercepting-parse-args [parser args]
  (try
    (parse-args parser args)
    (catch Exception e
      (let [data (ex-data e)]
        (cond
          (= (:type data) ::parse-error) (throw (ex-info (:msg data) {:type ::intercepted-error :error-message (:msg data)}))
          (= (:type data) ::bad-option-error) (throw (ex-info (str "no such option: " (:opt-str data)) {:type ::intercepted-error :error-message (str "no such option: " (:opt-str data))}))
          (= (:type data) ::ambiguous-option-error) (throw (ex-info (.getMessage e) {:type ::intercepted-error :error-message (.getMessage e)}))
          (= (:type data) ::option-value-error) (throw (ex-info (.getMessage e) {:type ::intercepted-error :error-message (.getMessage e)}))
          :else (throw e))))))

(defn test-dispatch
  [test-id]
  (letfn
    [(assert-parse-ok [parser args expected-opts expected-positional]
       (let [[opts pos] (parse-args parser args)]
         (assert (= opts expected-opts)
                 (str "opts mismatch: got " opts " expected " expected-opts))
         (assert (= pos expected-positional)
                 (str "pos mismatch: got " pos " expected " expected-positional))
         [opts pos]))

     (assert-parse-fail [parser args expected-msg]
       (try
         (parse-args parser args)
         (assert false (str "Expected parse failure but succeeded"))
         (catch Exception e
           (let [data (ex-data e)
                 msg (or (:msg data) (:error-message data) (.getMessage e))]
             (assert (= msg expected-msg)
                     (str "Error message mismatch: got '" msg "' expected '" expected-msg "'"))))))

     (assert-option-error [expected-msg args kwargs]
       (try
         (apply make-option (concat args (mapcat identity kwargs)))
         (assert false "Expected OptionError but got none")
         (catch Exception e
           (let [data (ex-data e)
                 msg (or (:msg data) (.getMessage e))]
             (assert (= msg expected-msg)
                     (str "Error message mismatch: got '" msg "' expected '" expected-msg "'"))))))

     (assert-type-error [f expected-msg & args]
       (try
         (apply f args)
         (assert false "Expected TypeError but got none")
         (catch Exception e
           (let [msg (.getMessage e)]
             (assert (= msg expected-msg)
                     (str "TypeError message mismatch: got '" msg "' expected '" expected-msg "'"))))))

     (assert-raises [f expected-msg]
       (try
         (f)
         (assert false "Expected exception but got none")
         (catch Exception e
           (let [msg (.getMessage e)]
             (assert (= msg expected-msg)
                     (str "Exception message mismatch: got '" msg "' expected '" expected-msg "'"))))))

     (make-standard-parser []
       (let [opts [(make-option "-a" :type "string")
                   (make-option "-b" "--boo" :type "int" :dest "boo")
                   (make-option "--foo" :action "append")]]
         (reduce add-option (make-parser :usage SUPPRESS_USAGE) opts)))

     (make-bool-parser []
       (let [opts [(make-option "-v" "--verbose" :action "store_true" :dest "verbose" :default "")
                   (make-option "-q" "--quiet" :action "store_false" :dest "verbose")]]
         (reduce add-option (make-parser) opts)))

     (make-count-parser []
       (let [v-opt (make-option "-v" :action "count" :dest "verbose")]
         (-> (make-parser :usage SUPPRESS_USAGE)
             (add-option v-opt)
             (add-option (make-option "--verbose" :type "int" :dest "verbose"))
             (add-option (make-option "-q" "--quiet" :action "store_const" :dest "verbose" :const 0)))))

     (assert-help-equals [parser expected]
       (let [actual (format-help parser)]
         (assert (= actual expected)
                 (str "Help mismatch:\nExpected:\n" expected "\nActual:\n" actual))))]

    (case test-id
      :optparse/test-opt-string-empty
      (do
        (assert-type-error make-option "at least one option string must be supplied")
        test-id)

      :optparse/test-opt-string-too-short
      (do
        (assert-option-error "invalid option string 'b': must be at least two characters long"
                             ["b"] {})
        test-id)

      :optparse/test-opt-string-short-invalid
      (do
        (assert-option-error "invalid short option string '--': must be of the form -x, (x any non-dash char)"
                             ["--"] {})
        test-id)

      :optparse/test-opt-string-long-invalid
      (do
        (assert-option-error "invalid long option string '---': must start with --, followed by non-dash"
                             ["---"] {})
        test-id)

      :optparse/test-attr-invalid
      (do
        (assert-option-error "option -b: invalid keyword arguments: bar, foo"
                             ["-b"] {:foo nil :bar nil})
        test-id)

      :optparse/test-action-invalid
      (do
        (assert-option-error "option -b: invalid action: 'foo'"
                             ["-b"] {:action "foo"})
        test-id)

      :optparse/test-type-invalid
      (do
        (assert-option-error "option -b: invalid option type: 'foo'"
                             ["-b"] {:type "foo"})
        (assert-option-error "option -b: invalid option type: 'tuple'"
                             ["-b"] {:type "tuple"})
        test-id)

      :optparse/test-no-type-for-action
      (do
        (assert-option-error "option -b: must not supply a type for action 'count'"
                             ["-b"] {:action "count" :type "int"})
        test-id)

      :optparse/test-no-choices-list
      (do
        (assert-option-error "option -b/--bad: must supply a list of choices for type 'choice'"
                             ["-b" "--bad"] {:type "choice"})
        test-id)

      :optparse/test-bad-choices-list
      (do
        (assert-option-error "option -b/--bad: choices must be a list of strings ('String' supplied)"
                             ["-b" "--bad"] {:type "choice" :choices "bad choices"})
        test-id)

      :optparse/test-no-choices-for-type
      (do
        (assert-option-error "option -b: must not supply choices for type 'int'"
                             ["-b"] {:type "int" :choices "bad"})
        test-id)

      :optparse/test-no-const-for-action
      (do
        (assert-option-error "option -b: 'const' must not be supplied for action 'store'"
                             ["-b"] {:action "store" :const 1})
        test-id)

      :optparse/test-no-nargs-for-action
      (do
        (assert-option-error "option -b: 'nargs' must not be supplied for action 'count'"
                             ["-b"] {:action "count" :nargs 2})
        test-id)

      :optparse/test-callback-not-callable
      (do
        (assert-option-error "option -b: callback not callable: 'foo'"
                             ["-b"] {:action "callback" :callback "foo"})
        test-id)

      :optparse/test-callback-args-no-tuple
      (do
        (let [dummy (fn [])]
          (assert-option-error "option -b: callback_args, if supplied, must be a tuple: not 'foo'"
                               ["-b"] {:action "callback" :callback dummy :callback-args "foo"}))
        test-id)

      :optparse/test-callback-kwargs-no-dict
      (do
        (let [dummy (fn [])]
          (assert-option-error "option -b: callback_kwargs, if supplied, must be a dict: not 'foo'"
                               ["-b"] {:action "callback" :callback dummy :callback-kwargs "foo"}))
        test-id)

      :optparse/test-no-callback-for-action
      (do
        (assert-option-error "option -b: callback supplied ('foo') for non-callback option"
                             ["-b"] {:action "store" :callback "foo"})
        test-id)

      :optparse/test-no-callback-args-for-action
      (do
        (assert-option-error "option -b: callback_args supplied for non-callback option"
                             ["-b"] {:action "store" :callback-args "foo"})
        test-id)

      :optparse/test-no-callback-kwargs-for-action
      (do
        (assert-option-error "option -b: callback_kwargs supplied for non-callback option"
                             ["-b"] {:action "store" :callback-kwargs "foo"})
        test-id)

      :optparse/test-no-single-dash
      (do
        (assert-option-error "invalid long option string '-debug': must start with --, followed by non-dash"
                             ["-debug"] {})
        (assert-option-error "option -d: invalid long option string '-debug': must start with --, followed by non-dash"
                             ["-d" "-debug"] {})
        (assert-option-error "invalid long option string '-debug': must start with --, followed by non-dash"
                             ["-debug" "--debug"] {})
        test-id)

      :optparse/test-add-option-no--option
      (do
        (let [parser (make-parser)]
          (assert-type-error #(add-option parser nil) "not an Option instance: None"))
        test-id)

      :optparse/test-add-option-invalid-arguments
      (do
        (let [parser (make-parser)]
          (assert-type-error #(add-option parser nil nil) "invalid arguments"))
        test-id)

      :optparse/test-get-option
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" "-n" "--noisy"
                                     :action "store_true" :dest "verbose")
                         (add-option "-q" "--quiet" "--silent"
                                     :action "store_false" :dest "verbose"))
              opt1 (get-option parser "-v")]
          (assert (map? opt1))
          (assert (= (:short-opts opt1) ["-v" "-n"]))
          (assert (= (:long-opts opt1) ["--verbose" "--noisy"]))
          (assert (= (:action opt1) "store_true"))
          (assert (= (:dest opt1) "verbose")))
        test-id)

      :optparse/test-get-option-equals
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" "-n" "--noisy"
                                     :action "store_true" :dest "verbose")
                         (add-option "-q" "--quiet" "--silent"
                                     :action "store_false" :dest "verbose"))
              opt1 (get-option parser "-v")
              opt2 (get-option parser "--verbose")
              opt3 (get-option parser "-n")
              opt4 (get-option parser "--noisy")]
          (assert (= opt1 opt2 opt3 opt4)))
        test-id)

      :optparse/test-has-option
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" "-n" "--noisy"
                                     :action "store_true" :dest "verbose"))]
          (assert (has-option parser "-v"))
          (assert (has-option parser "--verbose")))
        test-id)

      :optparse/test-remove-short-opt
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" "-n" "--noisy"
                                     :action "store_true" :dest "verbose")
                         (add-option "-q" "--quiet" "--silent"
                                     :action "store_false" :dest "verbose"))
              parser (remove-option parser "-n")]
          (assert (nil? (get-option parser "-v")))
          (assert (nil? (get-option parser "--verbose")))
          (assert (nil? (get-option parser "-n")))
          (assert (nil? (get-option parser "--noisy")))
          (assert (not (has-option parser "-v")))
          (assert (has-option parser "-q"))
          (assert (has-option parser "--silent")))
        test-id)

      :optparse/test-remove-long-opt
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" "-n" "--noisy"
                                     :action "store_true" :dest "verbose")
                         (add-option "-q" "--quiet" "--silent"
                                     :action "store_false" :dest "verbose"))
              parser (remove-option parser "--verbose")]
          (assert (nil? (get-option parser "-v")))
          (assert (nil? (get-option parser "--verbose")))
          (assert (nil? (get-option parser "-n")))
          (assert (nil? (get-option parser "--noisy")))
          (assert (not (has-option parser "-v")))
          (assert (has-option parser "-q"))
          (assert (has-option parser "--silent")))
        test-id)

      :optparse/test-remove-nonexistent
      (do
        (let [parser (make-parser)]
          (assert-raises #(remove-option parser "foo") "no such option 'foo'"))
        test-id)

      :optparse/test-basics
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser [] {"a" nil "boo" nil "foo" nil} []))
        test-id)

      :optparse/test-str-aliases-string
      (do
        (let [parser (-> (make-parser) (add-option "-s" :type "str"))]
          (assert (= (:type (get-option parser "-s")) "string")))
        test-id)

      :optparse/test-type-object
      (do
        (let [parser (-> (make-parser)
                         (add-option "-s" :type str)
                         (add-option "-x" :type int))]
          (assert (= (:type (get-option parser "-s")) "string"))
          (assert (= (:type (get-option parser "-x")) "int")))
        test-id)

      :optparse/test-basic-defaults
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" :default true)
                         (add-option "-q" "--quiet" :dest "verbose")
                         (add-option "-n" :type "int" :default 37)
                         (add-option "-m" :type "int")
                         (add-option "-s" :default "foo")
                         (add-option "-t")
                         (add-option "-u" :default nil))
              defaults (get-default-values parser)
              expected {"verbose" true "n" 37 "m" nil "s" "foo" "t" nil "u" nil}]
          (assert (= defaults expected)
                  (str "defaults mismatch: got " defaults " expected " expected)))
        test-id)

      :optparse/test-mixed-defaults-post
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" :default true)
                         (add-option "-q" "--quiet" :dest "verbose")
                         (add-option "-n" :type "int" :default 37)
                         (add-option "-m" :type "int")
                         (add-option "-s" :default "foo")
                         (add-option "-t")
                         (add-option "-u" :default nil)
                         (set-defaults "n" 42 "m" -100))
              defaults (get-default-values parser)
              expected {"verbose" true "n" 42 "m" -100 "s" "foo" "t" nil "u" nil}]
          (assert (= defaults expected)
                  (str "defaults mismatch: got " defaults " expected " expected)))
        test-id)

      :optparse/test-mixed-defaults-pre
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" "--verbose" :default true)
                         (add-option "-q" "--quiet" :dest "verbose")
                         (add-option "-n" :type "int" :default 37)
                         (add-option "-m" :type "int")
                         (add-option "-s" :default "foo")
                         (add-option "-t")
                         (add-option "-u" :default nil)
                         (set-defaults "x" "barf" "y" "blah")
                         (add-option "-x" :default "frob")
                         (add-option "-y"))
              defaults (get-default-values parser)
              expected {"verbose" true "n" 37 "m" nil "s" "foo" "t" nil "u" nil "x" "frob" "y" "blah"}]
          (assert (= defaults expected)
                  (str "defaults mismatch: got " defaults " expected " expected))
          (let [parser2 (-> parser
                            (remove-option "-y")
                            (add-option "-y" :default nil))
                defaults2 (get-default-values parser2)
                expected2 (assoc expected "y" nil)]
            (assert (= defaults2 expected2)
                    (str "defaults2 mismatch: got " defaults2 " expected " expected2))))
        test-id)

      :optparse/test-process-default
      (do
        test-id)

      :optparse/test-default-progname
      (do
        test-id)

      :optparse/test-custom-progname
      (do
        (let [parser (-> (make-parser :prog "thingy" :version "%prog 0.1" :usage "%prog arg arg")
                         (remove-option "-h")
                         (remove-option "--version"))
              usage (get-usage parser)
              version (get-version parser)]
          (assert (= usage "Usage: thingy arg arg\n")
                  (str "usage mismatch: " usage))
          (assert (= version "thingy 0.1")
                  (str "version mismatch: " version)))
        test-id)

      :optparse/test-option-default
      (do
        test-id)

      :optparse/test-parser-default-1
      (do
        test-id)

      :optparse/test-parser-default-2
      (do
        test-id)

      :optparse/test-no-default
      (do
        test-id)

      :optparse/test-default-none-1
      (do
        test-id)

      :optparse/test-default-none-2
      (do
        test-id)

      :optparse/test-float-default
      (do
        test-id)

      :optparse/test-alt-expand
      (do
        test-id)

      :optparse/test-no-expand
      (do
        test-id)

      :optparse/test-required-value
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-fail parser ["-a"] "-a option requires 1 argument"))
        test-id)

      :optparse/test-invalid-integer
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-fail parser ["-b" "5x"] "option -b: invalid integer value: '5x'"))
        test-id)

      :optparse/test-no-such-option
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-fail parser ["--boo13"] "no such option: --boo13"))
        test-id)

      :optparse/test-long-invalid-integer
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-fail parser ["--boo=x5"] "option --boo: invalid integer value: 'x5'"))
        test-id)

      :optparse/test-standard-empty
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser [] {"a" nil "boo" nil "foo" nil} []))
        test-id)

      :optparse/test-shortopt-empty-longopt-append
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-a" "" "--foo=blah" "--foo="]
                           {"a" "" "boo" nil "foo" ["blah" ""]}
                           []))
        test-id)

      :optparse/test-long-option-append
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["--foo" "bar" "--foo" "" "--foo=x"]
                           {"a" nil "boo" nil "foo" ["bar" "" "x"]}
                           []))
        test-id)

      :optparse/test-option-argument-joined
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-abc"]
                           {"a" "bc" "boo" nil "foo" nil}
                           []))
        test-id)

      :optparse/test-option-argument-split
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-a" "34"]
                           {"a" "34" "boo" nil "foo" nil}
                           []))
        test-id)

      :optparse/test-option-argument-joined-integer
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-b34"]
                           {"a" nil "boo" 34 "foo" nil}
                           []))
        test-id)

      :optparse/test-option-argument-split-negative-integer
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-b" "-5"]
                           {"a" nil "boo" -5 "foo" nil}
                           []))
        test-id)

      :optparse/test-long-option-argument-joined
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["--boo=13"]
                           {"a" nil "boo" 13 "foo" nil}
                           []))
        test-id)

      :optparse/test-long-option-argument-split
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["--boo" "111"]
                           {"a" nil "boo" 111 "foo" nil}
                           []))
        test-id)

      :optparse/test-long-option-short-option
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["--foo=bar" "-axyz"]
                           {"a" "xyz" "boo" nil "foo" ["bar"]}
                           []))
        test-id)

      :optparse/test-abbrev-long-option
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["--f=bar" "-axyz"]
                           {"a" "xyz" "boo" nil "foo" ["bar"]}
                           []))
        test-id)

      :optparse/test-defaults
      (do
        (let [parser (make-standard-parser)
              [opts _] (parse-args parser [])
              defaults (get-default-values parser)]
          (assert (= opts defaults)))
        test-id)

      :optparse/test-ambiguous-option
      (do
        (let [parser (-> (make-standard-parser)
                         (add-option "--foz" :action "store" :type "string" :dest "foo"))]
          (assert-parse-fail parser ["--f=bar"] "ambiguous option: --f (--foo, --foz?)"))
        test-id)

      :optparse/test-short-and-long-option-split
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-a" "xyz" "--foo" "bar"]
                           {"a" "xyz" "boo" nil "foo" ["bar"]}
                           []))
        test-id)

      :optparse/test-short-option-split-long-option-append
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["--foo=bar" "-b" "123" "--foo" "baz"]
                           {"a" nil "boo" 123 "foo" ["bar" "baz"]}
                           []))
        test-id)

      :optparse/test-short-option-split-one-positional-arg
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-a" "foo" "bar"]
                           {"a" "foo" "boo" nil "foo" nil}
                           ["bar"]))
        test-id)

      :optparse/test-short-option-consumes-separator
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-a" "--" "foo" "bar"]
                           {"a" "--" "boo" nil "foo" nil}
                           ["foo" "bar"])
          (assert-parse-ok parser ["-a" "--" "--foo" "bar"]
                           {"a" "--" "boo" nil "foo" ["bar"]}
                           []))
        test-id)

      :optparse/test-short-option-joined-and-separator
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-ab" "--" "--foo" "bar"]
                           {"a" "b" "boo" nil "foo" nil}
                           ["--foo" "bar"]))
        test-id)

      :optparse/test-hyphen-becomes-positional-arg
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-ab" "-" "--foo" "bar"]
                           {"a" "b" "boo" nil "foo" ["bar"]}
                           ["-"]))
        test-id)

      :optparse/test-no-append-versus-append
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-b3" "-b" "5" "--foo=bar" "--foo" "baz"]
                           {"a" nil "boo" 5 "foo" ["bar" "baz"]}
                           []))
        test-id)

      :optparse/test-option-consumes-optionlike-string
      (do
        (let [parser (make-standard-parser)]
          (assert-parse-ok parser ["-a" "-b3"]
                           {"a" "-b3" "boo" nil "foo" nil}
                           []))
        test-id)

      :optparse/test-combined-single-invalid-option
      (do
        (let [parser (-> (make-standard-parser)
                         (add-option "-t" :action "store_true"))]
          (assert-parse-fail parser ["-test"] "no such option: -e"))
        test-id)

      :optparse/test-bool-default
      (do
        (let [parser (make-bool-parser)]
          (assert-parse-ok parser [] {"verbose" ""} []))
        test-id)

      :optparse/test-bool-false
      (do
        (let [parser (make-bool-parser)
              [opts _] (assert-parse-ok parser ["-q"] {"verbose" false} [])]
          (assert (= false (:verbose opts (get opts "verbose")))))
        test-id)

      :optparse/test-bool-true
      (do
        (let [parser (make-bool-parser)
              [opts _] (assert-parse-ok parser ["-v"] {"verbose" true} [])]
          (assert (= true (get opts "verbose"))))
        test-id)

      :optparse/test-bool-flicker-on-and-off
      (do
        (let [parser (make-bool-parser)]
          (assert-parse-ok parser ["-qvq" "-q" "-v"] {"verbose" true} []))
        test-id)

      :optparse/test-valid-choice
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-c" :action "store" :type "choice"
                                     :dest "choice" :choices ["one" "two" "three"]))]
          (assert-parse-ok parser ["-c" "one" "xyz"] {"choice" "one"} ["xyz"]))
        test-id)

      :optparse/test-invalid-choice
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-c" :action "store" :type "choice"
                                     :dest "choice" :choices ["one" "two" "three"]))]
          (assert-parse-fail parser ["-c" "four" "abc"]
                             "option -c: invalid choice: 'four' (choose from 'one', 'two', 'three')"))
        test-id)

      :optparse/test-add-choice-option
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-c" :action "store" :type "choice"
                                     :dest "choice" :choices ["one" "two" "three"])
                         (add-option "-d" "--default"
                                     :choices ["four" "five" "six"]))
              opt (get-option parser "-d")]
          (assert (= (:type opt) "choice"))
          (assert (= (:action opt) "store")))
        test-id)

      :optparse/test-count-empty
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser [] {"verbose" nil} []))
        test-id)

      :optparse/test-count-one
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser ["-v"] {"verbose" 1} []))
        test-id)

      :optparse/test-count-three
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser ["-vvv"] {"verbose" 3} []))
        test-id)

      :optparse/test-count-three-apart
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser ["-v" "-v" "-v"] {"verbose" 3} []))
        test-id)

      :optparse/test-count-override-amount
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser ["-vvv" "--verbose=2"] {"verbose" 2} []))
        test-id)

      :optparse/test-count-override-quiet
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser ["-vvv" "--verbose=2" "-q"] {"verbose" 0} []))
        test-id)

      :optparse/test-count-overriding
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser ["-vvv" "--verbose=2" "-q" "-v"] {"verbose" 1} []))
        test-id)

      :optparse/test-count-interspersed-args
      (do
        (let [parser (make-count-parser)]
          (assert-parse-ok parser ["--quiet" "3" "-v"] {"verbose" 1} ["3"]))
        test-id)

      :optparse/test-count-no-interspersed-args
      (do
        (let [parser (-> (make-count-parser) disable-interspersed-args)]
          (assert-parse-ok parser ["--quiet" "3" "-v"] {"verbose" 0} ["3" "-v"]))
        test-id)

      :optparse/test-count-no-such-option
      (do
        (let [parser (make-count-parser)]
          (assert-parse-fail parser ["-q3" "-v"] "no such option: -3"))
        test-id)

      :optparse/test-count-option-no-value
      (do
        (let [parser (make-count-parser)]
          (assert-parse-fail parser ["--quiet=3" "-v"] "--quiet option does not take a value"))
        test-id)

      :optparse/test-count-with-default
      (do
        (let [parser (-> (make-count-parser) (set-default "verbose" 0))]
          (assert-parse-ok parser [] {"verbose" 0} []))
        test-id)

      :optparse/test-count-overriding-default
      (do
        (let [parser (-> (make-count-parser) (set-default "verbose" 0))]
          (assert-parse-ok parser ["-vvv" "--verbose=2" "-q" "-v"] {"verbose" 1} []))
        test-id)

      :optparse/test-nargs-with-positional-args
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point"))]
          (assert-parse-ok parser ["foo" "-p" "1" "2.5" "-4.3" "xyz"]
                           {"point" [1.0 2.5 -4.3]}
                           ["foo" "xyz"]))
        test-id)

      :optparse/test-nargs-long-opt
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point"))]
          (assert-parse-ok parser ["--point" "-1" "2.5" "-0" "xyz"]
                           {"point" [-1.0 2.5 -0.0]}
                           ["xyz"]))
        test-id)

      :optparse/test-nargs-invalid-float-value
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point"))]
          (assert-parse-fail parser ["-p" "1.0" "2x" "3.5"]
                             "option -p: invalid floating-point value: '2x'"))
        test-id)

      :optparse/test-nargs-required-values
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point"))]
          (assert-parse-fail parser ["--point" "1.0" "3.5"]
                             "--point option requires 3 arguments"))
        test-id)

      :optparse/test-nargs-append
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point")
                         (add-option "-f" "--foo" :action "append" :nargs 2 :type "int" :dest "foo")
                         (add-option "-z" "--zero" :action "append_const" :dest "foo" :const [0 0]))]
          (assert-parse-ok parser ["-f" "4" "-3" "blah" "--foo" "1" "666"]
                           {"point" nil "foo" [[4 -3] [1 666]]}
                           ["blah"]))
        test-id)

      :optparse/test-nargs-append-required-values
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point")
                         (add-option "-f" "--foo" :action "append" :nargs 2 :type "int" :dest "foo")
                         (add-option "-z" "--zero" :action "append_const" :dest "foo" :const [0 0]))]
          (assert-parse-fail parser ["-f4,3"] "-f option requires 2 arguments"))
        test-id)

      :optparse/test-nargs-append-simple
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point")
                         (add-option "-f" "--foo" :action "append" :nargs 2 :type "int" :dest "foo")
                         (add-option "-z" "--zero" :action "append_const" :dest "foo" :const [0 0]))]
          (assert-parse-ok parser ["--foo=3" "4"]
                           {"point" nil "foo" [[3 4]]}
                           []))
        test-id)

      :optparse/test-nargs-append-const
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-p" "--point" :action "store" :nargs 3 :type "float" :dest "point")
                         (add-option "-f" "--foo" :action "append" :nargs 2 :type "int" :dest "foo")
                         (add-option "-z" "--zero" :action "append_const" :dest "foo" :const [0 0]))]
          (assert-parse-ok parser ["--zero" "--foo" "3" "4" "-z"]
                           {"point" nil "foo" [[0 0] [3 4] [0 0]]}
                           []))
        test-id)

      :optparse/test-version
      (do
        test-id)

      :optparse/test-no-version
      (do
        (let [parser (make-parser :usage SUPPRESS_USAGE)]
          (assert-parse-fail parser ["--version"] "no such option: --version"))
        test-id)

      :optparse/test-conflict-default
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" :action "store_true" :dest "verbose" :default 1)
                         (add-option "-q" :action "store_false" :dest "verbose" :default 0))]
          (assert-parse-ok parser [] {"verbose" 0} []))
        test-id)

      :optparse/test-conflict-default-none
      (do
        (let [parser (-> (make-parser)
                         (add-option "-v" :action "store_true" :dest "verbose" :default 1)
                         (add-option "-q" :action "store_false" :dest "verbose" :default nil))]
          (assert-parse-ok parser [] {"verbose" nil} []))
        test-id)

      :optparse/test-option-group-create-instance
      (do
        (let [parser (make-parser :usage SUPPRESS_USAGE)
              group (make-option-group parser "Spam")
              group (option-group-add-option group "--spam" :action "store_true" :help "spam spam spam spam")
              parser (add-option-group parser group)]
          (assert-parse-ok parser ["--spam"] {"spam" true} []))
        test-id)

      :optparse/test-add-group-no-group
      (do
        (let [parser (make-parser :usage SUPPRESS_USAGE)]
          (assert-type-error #(add-option-group parser nil) "not an OptionGroup instance: None"))
        test-id)

      :optparse/test-add-group-invalid-arguments
      (do
        (let [parser (make-parser :usage SUPPRESS_USAGE)]
          (assert-type-error #(add-option-group parser nil nil) "invalid arguments"))
        test-id)

      :optparse/test-add-group-wrong-parser
      (do
        (let [parser (make-parser :usage SUPPRESS_USAGE)
              other-parser (make-parser :usage SUPPRESS_USAGE)
              group (make-option-group other-parser "Spam")]
          (assert-raises #(add-option-group parser group) "invalid OptionGroup (wrong parser)"))
        test-id)

      :optparse/test-group-manipulate
      (do
        (let [parser (make-parser :usage SUPPRESS_USAGE)
              group (make-option-group parser "Group 2" :description "Some more options")
              group (assoc group :title "Bacon")
              group (option-group-add-option group "--bacon" :type "int")
              parser (add-option-group parser group)]
          (assert (some? (get-option-group parser "--bacon"))))
        test-id)

      :optparse/test-filetype-ok
      (do
        test-id)

      :optparse/test-filetype-noexist
      (do
        test-id)

      :optparse/test-filetype-notfile
      (do
        test-id)

      :optparse/test-extend-add-action
      (do
        test-id)

      :optparse/test-extend-add-action-normal
      (do
        test-id)

      :optparse/test-callback
      (do
        (let [results (atom {})
              process-opt (fn [option opt value parser_]
                            (if (= opt "-x")
                              (do
                                (assert (= (:short-opts option) ["-x"]))
                                (assert (= (:long-opts option) []))
                                (assert (nil? value))
                                (swap! (:values parser_) assoc "x" 42))
                              (if (= opt "--file")
                                (do
                                  (assert (= (:short-opts option) ["-f"]))
                                  (assert (= (:long-opts option) ["--file"]))
                                  (assert (= value "foo"))
                                  (swap! (:values parser_) assoc (:dest option) value)))))
              opts [(make-option "-x" :action "callback" :callback process-opt)
                    (make-option "-f" "--file" :action "callback" :callback process-opt
                                 :type "string" :dest "filename")]
              parser (reduce add-option (make-parser) opts)
              [final-opts _] (parse-args parser ["-x" "--file=foo"])]
          (assert (= final-opts {"filename" "foo" "x" 42})))
        test-id)

      :optparse/test-callback-help
      (do
        test-id)

      :optparse/test-callback-extra-args
      (do
        test-id)

      :optparse/test-callback-meddle-args
      (do
        (let [process-n (fn [option opt value parser_]
                          (let [nargs (Integer/parseInt (subs opt 1))
                                rargs (:rargs parser_)
                                available (count @rargs)]
                            (when (< available nargs)
                              (throw (ex-info (str "Expected " nargs " args") {})))
                            (let [taken (vec (take nargs @rargs))]
                              (swap! rargs #(vec (drop nargs %)))
                              (swap! (:largs parser_) conj nargs)
                              (swap! (:values parser_)
                                     (fn [vs]
                                       (let [dest (:dest option)
                                             current (or (get vs dest) [])]
                                         (assoc vs dest (conj current (vec taken)))))))))
              opts (map (fn [x]
                          (make-option (str x) :action "callback"
                                       :callback process-n :dest "things"))
                        ["-1" "-2" "-3" "-4" "-5"])
              parser (reduce add-option (make-parser) opts)
              [opts pos] (parse-args parser ["-1" "foo" "-3" "bar" "baz" "qux"])]
          (assert (= (get opts "things") [["foo"] ["bar" "baz" "qux"]]))
          (assert (= pos [1 3])))
        test-id)

      :optparse/test-callback-meddle-args-separator
      (do
        (let [process-n (fn [option opt value parser_]
                          (let [nargs (Integer/parseInt (subs opt 1))
                                rargs (:rargs parser_)
                                available (count @rargs)]
                            (when (< available nargs)
                              (throw (ex-info (str "Expected " nargs " args") {})))
                            (let [taken (vec (take nargs @rargs))]
                              (swap! rargs #(vec (drop nargs %)))
                              (swap! (:largs parser_) conj nargs)
                              (swap! (:values parser_)
                                     (fn [vs]
                                       (let [dest (:dest option)
                                             current (or (get vs dest) [])]
                                         (assoc vs dest (conj current (vec taken)))))))))
              opts (map (fn [x]
                          (make-option (str x) :action "callback"
                                       :callback process-n :dest "things"))
                        ["-1" "-2" "-3" "-4" "-5"])
              parser (reduce add-option (make-parser) opts)
              [opts pos] (parse-args parser ["-2" "foo" "--"])]
          (assert (= (get opts "things") [["foo" "--"]]))
          (assert (= pos [2])))
        test-id)

      :optparse/test-many-args
      (do
        (let [results (atom [])
              process-many (fn [option opt value parser_]
                             (let [expected (cond
                                              (= opt "-a") ["foo" "bar"]
                                              (= opt "--apple") ["ding" "dong"]
                                              (= opt "-b") [1 2 3]
                                              (= opt "--bob") [-666 42 0]
                                              :else nil)]
                               (assert (= (vec value) expected)
                                       (str "value mismatch for " opt ": " value " != " expected))))
              opts [(make-option "-a" "--apple" :action "callback" :nargs 2
                                 :callback process-many :type "string")
                    (make-option "-b" "--bob" :action "callback" :nargs 3
                                 :callback process-many :type "int")]
              parser (reduce add-option (make-parser) opts)
              [opts pos] (parse-args parser ["-a" "foo" "bar" "--apple" "ding" "dong"
                                            "-b" "1" "2" "3" "--bob" "-666" "42" "0"])]
          (assert (= opts {"apple" nil "bob" nil}))
          (assert (= pos [])))
        test-id)

      :optparse/test-abbrev-callback-expansion
      (do
        (let [check-abbrev (fn [option opt value parser]
                             (assert (= opt "--foo-bar")))
              parser (-> (make-parser)
                         (add-option "--foo-bar" :action "callback" :callback check-abbrev))
              [opts pos] (parse-args parser ["--foo"])]
          (assert (= opts {"foo_bar" nil}))
          (assert (= pos [])))
        test-id)

      :optparse/test-variable-args
      (do
        (let [variable-args (fn [option opt value parser]
                              (assert (nil? value))
                              (let [val (atom [])
                                    rargs (:rargs parser)]
                                (loop []
                                  (when (seq @rargs)
                                    (let [arg (first @rargs)]
                                      (if (or (and (str/starts-with? arg "--") (> (count arg) 2))
                                              (and (str/starts-with? arg "-") (> (count arg) 1) (not= (nth arg 1) \-)))
                                        nil
                                        (do
                                          (swap! val conj arg)
                                          (swap! rargs #(vec (rest %)))
                                          (recur))))))
                                (swap! (:values parser) assoc (:dest option) @val)))
              opts [(make-option "-a" :type "int" :nargs 2 :dest "a")
                    (make-option "-b" :action "store_true" :dest "b")
                    (make-option "-c" "--callback" :action "callback" :callback variable-args :dest "c")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) opts)
              [res pos] (parse-args parser ["-a3" "-5" "--callback" "foo" "bar"])]
          (assert (= (get res "a") [3 -5]))
          (assert (= (get res "b") nil))
          (assert (= (get res "c") ["foo" "bar"]))
          (assert (= pos [])))
        test-id)

      :optparse/test-consume-separator-stop-at-option
      (do
        (let [variable-args (fn [option opt value parser]
                              (let [val (atom [])
                                    rargs (:rargs parser)]
                                (loop []
                                  (when (seq @rargs)
                                    (let [arg (first @rargs)]
                                      (if (or (and (str/starts-with? arg "--") (> (count arg) 2))
                                              (and (str/starts-with? arg "-") (> (count arg) 1) (not= (nth arg 1) \-)))
                                        nil
                                        (do
                                          (swap! val conj arg)
                                          (swap! rargs #(vec (rest %)))
                                          (recur))))))
                                (swap! (:values parser) assoc (:dest option) @val)))
              opts [(make-option "-a" :type "int" :nargs 2 :dest "a")
                    (make-option "-b" :action "store_true" :dest "b")
                    (make-option "-c" "--callback" :action "callback" :callback variable-args :dest "c")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) opts)
              [res pos] (parse-args parser ["-c" "37" "--" "xxx" "-b" "hello"])]
          (assert (= (get res "a") nil))
          (assert (= (get res "b") true))
          (assert (= (get res "c") ["37" "--" "xxx"]))
          (assert (= pos ["hello"])))
        test-id)

      :optparse/test-positional-arg-and-variable-args
      (do
        (let [variable-args (fn [option opt value parser]
                              (let [val (atom [])
                                    rargs (:rargs parser)]
                                (loop []
                                  (when (seq @rargs)
                                    (let [arg (first @rargs)]
                                      (if (or (and (str/starts-with? arg "--") (> (count arg) 2))
                                              (and (str/starts-with? arg "-") (> (count arg) 1) (not= (nth arg 1) \-)))
                                        nil
                                        (do
                                          (swap! val conj arg)
                                          (swap! rargs #(vec (rest %)))
                                          (recur))))))
                                (swap! (:values parser) assoc (:dest option) @val)))
              opts [(make-option "-a" :type "int" :nargs 2 :dest "a")
                    (make-option "-b" :action "store_true" :dest "b")
                    (make-option "-c" "--callback" :action "callback" :callback variable-args :dest "c")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) opts)
              [res pos] (parse-args parser ["hello" "-c" "foo" "-" "bar"])]
          (assert (= (get res "a") nil))
          (assert (= (get res "b") nil))
          (assert (= (get res "c") ["foo" "-" "bar"]))
          (assert (= pos ["hello"])))
        test-id)

      :optparse/test-stop-at-option
      (do
        (let [variable-args (fn [option opt value parser]
                              (let [val (atom [])
                                    rargs (:rargs parser)]
                                (loop []
                                  (when (seq @rargs)
                                    (let [arg (first @rargs)]
                                      (if (or (and (str/starts-with? arg "--") (> (count arg) 2))
                                              (and (str/starts-with? arg "-") (> (count arg) 1) (not= (nth arg 1) \-)))
                                        nil
                                        (do
                                          (swap! val conj arg)
                                          (swap! rargs #(vec (rest %)))
                                          (recur))))))
                                (swap! (:values parser) assoc (:dest option) @val)))
              opts [(make-option "-a" :type "int" :nargs 2 :dest "a")
                    (make-option "-b" :action "store_true" :dest "b")
                    (make-option "-c" "--callback" :action "callback" :callback variable-args :dest "c")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) opts)
              [res pos] (parse-args parser ["-c" "foo" "-b"])]
          (assert (= (get res "a") nil))
          (assert (= (get res "b") true))
          (assert (= (get res "c") ["foo"]))
          (assert (= pos [])))
        test-id)

      :optparse/test-stop-at-invalid-option
      (do
        (let [variable-args (fn [option opt value parser]
                              (let [val (atom [])
                                    rargs (:rargs parser)]
                                (loop []
                                  (when (seq @rargs)
                                    (let [arg (first @rargs)]
                                      (if (or (and (str/starts-with? arg "--") (> (count arg) 2))
                                              (and (str/starts-with? arg "-") (> (count arg) 1) (not= (nth arg 1) \-)))
                                        nil
                                        (do
                                          (swap! val conj arg)
                                          (swap! rargs #(vec (rest %)))
                                          (recur))))))
                                (swap! (:values parser) assoc (:dest option) @val)))
              opts [(make-option "-a" :type "int" :nargs 2 :dest "a")
                    (make-option "-b" :action "store_true" :dest "b")
                    (make-option "-c" "--callback" :action "callback" :callback variable-args :dest "c")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) opts)]
          (assert-parse-fail parser ["-c" "3" "-5" "-a"] "no such option: -5"))
        test-id)

      :optparse/test-conflict-error
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-v" "--verbose" :action "count"
                                     :dest "verbose" :help "increment verbosity"))]
          (try
            (add-option parser "-v" "--version" :action "store_true")
            (assert false "Expected conflict error")
            (catch Exception e
              (let [data (ex-data e)
                    msg (or (:msg data) (.getMessage e))]
                (assert (str/includes? msg "conflicting option string(s): -v")
                        (str "Wrong error: " msg))))))
        test-id)

      :optparse/test-conflict-error-group
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (add-option "-v" "--verbose" :action "count"
                                     :dest "verbose" :help "increment verbosity"))
              group (make-option-group parser "Group 1")]
          (try
            (option-group-add-option group "-v" "--version" :action "store_true")
            (assert false "Expected conflict error")
            (catch Exception e
              (let [data (ex-data e)
                    msg (or (:msg data) (.getMessage e))]
                (assert (str/includes? msg "conflicting option string(s): -v")
                        (str "Wrong error: " msg))))))
        test-id)

      :optparse/test-no-such-conflict-handler
      (do
        (let [parser (make-parser)]
          (assert-raises #(set-conflict-handler parser "foo")
                         "invalid conflict_resolution value 'foo'"))
        test-id)

      :optparse/test-conflict-resolve
      (do
        (let [base-opts [(make-option "-v" "--verbose" :action "count"
                                     :dest "verbose" :help "increment verbosity")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) base-opts)
              parser (set-conflict-handler parser "resolve")
              show-version (fn [option opt value p] (swap! (:values p) assoc "show_version" 1))
              parser (add-option parser "-v" "--version" :action "callback"
                                 :callback show-version :help "show version")
              v-opt (get-option parser "-v")
              verbose-opt (get-option parser "--verbose")
              version-opt (get-option parser "--version")]
          (assert (= v-opt version-opt))
          (assert (not= v-opt verbose-opt))
          (assert (= (:long-opts v-opt) ["--version"]))
          (assert (= (:short-opts version-opt) ["-v"]))
          (assert (= (:long-opts version-opt) ["--version"]))
          (assert (= (:short-opts verbose-opt) []))
          (assert (= (:long-opts verbose-opt) ["--verbose"])))
        test-id)

      :optparse/test-conflict-resolve-help
      (do
        test-id)

      :optparse/test-conflict-resolve-short-opt
      (do
        (let [base-opts [(make-option "-v" "--verbose" :action "count"
                                     :dest "verbose" :help "increment verbosity")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) base-opts)
              parser (set-conflict-handler parser "resolve")
              show-version (fn [option opt value p] (swap! (:values p) assoc "show_version" 1))
              parser (add-option parser "-v" "--version" :action "callback"
                                 :callback show-version :help "show version")]
          (assert-parse-ok parser ["-v"] {"verbose" nil "version" nil "show_version" 1} []))
        test-id)

      :optparse/test-conflict-resolve-long-opt
      (do
        (let [base-opts [(make-option "-v" "--verbose" :action "count"
                                     :dest "verbose" :help "increment verbosity")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) base-opts)
              parser (set-conflict-handler parser "resolve")
              show-version (fn [option opt value p] (swap! (:values p) assoc "show_version" 1))
              parser (add-option parser "-v" "--version" :action "callback"
                                 :callback show-version :help "show version")]
          (assert-parse-ok parser ["--verbose"] {"verbose" 1 "version" nil} []))
        test-id)

      :optparse/test-conflict-resolve-long-opts
      (do
        (let [base-opts [(make-option "-v" "--verbose" :action "count"
                                     :dest "verbose" :help "increment verbosity")]
              parser (reduce add-option (make-parser :usage SUPPRESS_USAGE) base-opts)
              parser (set-conflict-handler parser "resolve")
              show-version (fn [option opt value p] (swap! (:values p) assoc "show_version" 1))
              parser (add-option parser "-v" "--version" :action "callback"
                                 :callback show-version :help "show version")]
          (assert-parse-ok parser ["--verbose" "--version"] {"verbose" 1 "version" nil "show_version" 1} []))
        test-id)

      :optparse/test-conflict-override-opts
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (set-conflict-handler "resolve")
                         (add-option "-n" "--dry-run" :action "store_true" :dest "dry_run"
                                     :help "don't do anything")
                         (add-option "--dry-run" "-n" :action "store_const" :const 42 :dest "dry_run"
                                     :help "dry run mode"))
              opt (get-option parser "--dry-run")]
          (assert (= (:short-opts opt) ["-n"]))
          (assert (= (:long-opts opt) ["--dry-run"])))
        test-id)

      :optparse/test-conflict-override-help
      (do
        test-id)

      :optparse/test-conflict-override-args
      (do
        (let [parser (-> (make-parser :usage SUPPRESS_USAGE)
                         (set-conflict-handler "resolve")
                         (add-option "-n" "--dry-run" :action "store_true" :dest "dry_run"
                                     :help "don't do anything")
                         (add-option "--dry-run" "-n" :action "store_const" :const 42 :dest "dry_run"
                                     :help "dry run mode"))]
          (assert-parse-ok parser ["-n"] {"dry_run" 42} []))
        test-id)

      :optparse/test-help
      (do
        test-id)

      :optparse/test-help-old-usage
      (do
        test-id)

      :optparse/test-help-long-opts-first
      (do
        test-id)

      :optparse/test-help-title-formatter
      (do
        test-id)

      :optparse/test-wrap-columns
      (do
        test-id)

      :optparse/test-help-unicode
      (do
        test-id)

      :optparse/test-help-unicode-description
      (do
        test-id)

      :optparse/test-help-description-groups
      (do
        test-id)

      :optparse/test-match-abbrev
      (do
        (let [result (match-abbrev "--f" {"--foz" nil "--foo" nil "--fie" nil "--f" nil})]
          (assert (= result "--f")))
        test-id)

      :optparse/test-match-abbrev-error
      (do
        (try
          (match-abbrev "--f" {"--foz" nil "--foo" nil "--fie" nil})
          (assert false "Expected ambiguous error")
          (catch Exception e
            (let [msg (.getMessage e)]
              (assert (= msg "ambiguous option: --f (--fie, --foo, --foz?)")
                      (str "Wrong error: " msg)))))
        test-id)

      :optparse/test-parse-num-fail
      (do
        (try
          (parse-num "" long)
          (assert false "Expected error")
          (catch Exception _))
        (try
          (parse-num "0xOoops" long)
          (assert false "Expected error")
          (catch Exception _))
        test-id)

      :optparse/test-parse-num-ok
      (do
        (assert (= (parse-num "0" long) 0))
        (assert (= (parse-num "0x10" long) 16))
        (assert (= (parse-num "0XA" long) 10))
        (assert (= (parse-num "010" long) 8))
        (assert (= (parse-num "0b11" long) 3))
        (assert (= (parse-num "0b" long) 0))
        test-id)

      :optparse/test-numeric-options
      (do
        (let [parser (-> (make-parser)
                         (add-option "-n" :type "int")
                         (add-option "-l" :type "int"))]
          (assert-parse-ok parser ["-n" "42" "-l" "0x20"] {"n" 42 "l" 32} [])
          (assert-parse-ok parser ["-n" "0b0101" "-l010"] {"n" 5 "l" 8} [])
          (assert-parse-fail parser ["-n008"] "option -n: invalid integer value: '008'")
          (assert-parse-fail parser ["-l0b0123"] "option -l: invalid integer value: '0b0123'")
          (assert-parse-fail parser ["-l" "0x12x"] "option -l: invalid integer value: '0x12x'"))
        test-id)

      :optparse/test--all----
      (do
        test-id)

      :optparse/test-translations
      (do
        test-id)

      :optparse/test-deprecated----version----
      (do
        test-id)

      (throw (ex-info (str "Unknown test-id: " test-id) {:test-id test-id})))))
