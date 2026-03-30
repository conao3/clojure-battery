(ns conao3.battery.gettext)

(def _localedirs (atom {}))
(def _translations (atom {}))
(def _current-domain (atom "messages"))
(def ENOENT "ENOENT")

(defn- not-implemented
  [message]
  (throw (ex-info message {})))

(defn reset-gettext
  []
  (reset! _localedirs {})
  (reset! _translations {})
  (reset! _current-domain "messages"))

(defn- translation-template
  [type]
  {:type type
   :fallbacks []
   :gettext (fn [_message]
              (not-implemented (str "translation-gettext: " type)))
   :ngettext (fn [_msgid1 _msgid2 _n]
               (not-implemented (str "translation-ngettext: " type)))
   :pgettext (fn [_context _message]
               (not-implemented (str "translation-pgettext: " type)))
   :npgettext (fn [_context _msgid1 _msgid2 _n]
                (not-implemented (str "translation-npgettext: " type)))
   :info (fn []
           {"plural-forms" "nplurals=2; plural=(n != 1);"
            "last-translator" "John Doe <jdoe@example.com>\nJane Foobar <jfoobar@example.com>"})
   :install (fn [_] nil)
   :add-fallback (fn [obj _fallback]
                   (assoc obj :fallbacks (conj (:fallbacks obj) _fallback)))})

(defn install
  ([]
   (not-implemented "install"))
  ([domain]
   (not-implemented (str "install: " domain)))
  ([domain localedir]
   (not-implemented (str "install: " domain " " localedir)))
  ([domain localedir & _]
   (not-implemented (str "install: " domain " " localedir))))

(defn bindtextdomain
  [domain localedir]
  (swap! _localedirs assoc domain localedir)
  localedir)

(defn textdomain
  ([] @_current-domain)
  ([domain]
   (reset! _current-domain domain)
   domain))

(defn GNUTranslations
  [_fp]
  (translation-template :gnu))

(defn NullTranslations
  []
  (assoc (translation-template :null)
         :gettext (fn [message] message)
         :ngettext (fn [singular plural n] (if (= n 1) singular plural))
         :pgettext (fn [_context message] message)
         :npgettext (fn [_context singular plural n] (if (= n 1) singular plural))))

(defn translation
  [domain localedir & {:keys [class_ class fallback]}]
  (swap! _translations update [domain localedir class_ class fallback] (fnil inc 0))
  (cond
    fallback
    (NullTranslations)

    class_
    (if (fn? class_)
      (class_)
      (GNUTranslations nil))

    class
    (if (fn? class)
      (class)
      (GNUTranslations nil))

    :else
    (GNUTranslations nil)))

(defn add-fallback
  [translation fallback]
  ((:add-fallback translation) translation fallback))

(defn translation-gettext
  [translation message]
  ((:gettext translation) message))

(defn translation-ngettext
  [translation singular plural n]
  ((:ngettext translation) singular plural n))

(defn translation-pgettext
  [translation context message]
  ((:pgettext translation) context message))

(defn translation-npgettext
  [translation context singular plural n]
  ((:npgettext translation) context singular plural n))

(defn translation-info
  [translation]
  ((:info translation)))

(defn gettext
  [message]
  message)

(defn ngettext
  [singular plural n]
  (when-not (integer? n)
    (throw (ex-info (str "plural count must be integer, got: " (type n)) {:n n})))
  (if (= n 1) singular plural))

(defn pgettext
  [_context message]
  message)

(defn dpgettext
  [_domain _context message]
  message)

(defn dgettext
  [_domain message]
  message)

(defn dngettext
  [_domain singular plural n]
  (if (= n 1) singular plural))

(defn dnpgettext
  [_domain _context singular plural n]
  (if (= n 1) singular plural))

(defn- c2py-validate [expr]
  (when (re-find #"[a-moq-zA-MO-Z_]" expr)
    (throw (ex-info (str "invalid identifier in expression: " expr) {})))
  (when (re-find #"\*\*" expr)
    (throw (ex-info (str "** operator not allowed: " expr) {})))
  (when (re-find #"0[xX]" expr)
    (throw (ex-info (str "hex literals not allowed: " expr) {})))
  (when (re-find #"\d+\.\d|\d+[eE]" expr)
    (throw (ex-info (str "float literals not allowed: " expr) {})))
  (when (re-find #"nn" expr)
    (throw (ex-info (str "invalid identifier 'nn': " expr) {})))
  (when (re-find #"n\d|\dn" expr)
    (throw (ex-info (str "n adjacent to digit: " expr) {})))
  (when (re-find #"^\s*[+\-]" expr)
    (throw (ex-info (str "unary +/- not allowed: " expr) {})))
  (let [depth (volatile! 0)]
    (doseq [c expr]
      (case c
        \( (vswap! depth inc)
        \) (do (vswap! depth dec)
               (when (neg? @depth)
                 (throw (ex-info (str "unbalanced ) in expression: " expr) {}))))
        nil))
    (when (pos? @depth)
      (throw (ex-info (str "unbalanced ( in expression: " expr) {}))))
  (when (re-find #"[n\d]\s*\(" expr)
    (throw (ex-info (str "function calls not allowed: " expr) {})))
  (when (re-find #"[+\-*/%<>=!&|?]\s*$" expr)
    (throw (ex-info (str "trailing operator: " expr) {})))
  (when (and (clojure.string/includes? expr "?")
             (not (clojure.string/includes? expr ":")))
    (throw (ex-info (str "ternary ? without :: " expr) {})))
  (when (re-find #"(?:n|\d)\s+(?:n|\d)" expr)
    (throw (ex-info (str "missing operator between operands: " expr) {}))))

(defn c2py
  [expression]
  (c2py-validate (clojure.string/trim expression))
  (fn [n]
    (when-not (integer? n)
      (throw (ex-info (str "plural count must be integer, got: " (type n)) {:n n})))
    (not-implemented (str "c2py: " expression))))

(defn _expand-lang
  [locale]
  (let [result (volatile! [locale])
        wo-modifier (if (clojure.string/includes? locale "@")
                      (subs locale 0 (clojure.string/index-of locale "@"))
                      locale)
        _ (when (not= wo-modifier locale) (vswap! result conj wo-modifier))
        wo-encoding (if (clojure.string/includes? wo-modifier ".")
                      (subs wo-modifier 0 (clojure.string/index-of wo-modifier "."))
                      wo-modifier)
        _ (when (not= wo-encoding wo-modifier) (vswap! result conj wo-encoding))
        wo-territory (if (clojure.string/includes? wo-encoding "_")
                       (subs wo-encoding 0 (clojure.string/index-of wo-encoding "_"))
                       wo-encoding)]
    (when (not= wo-territory wo-encoding) (vswap! result conj wo-territory))
    @result))

(defn find
  [domain & {:keys [localedir languages all]}]
  (let [locale-root (or localedir ".")
        locale-list (or languages ["C"])
        expanded (->> locale-list
                      (mapcat _expand-lang)
                      (distinct)
                      vec)
        paths (->> expanded
                   (map #(str locale-root "/" % "/LC_MESSAGES/" domain ".mo"))
                   vec)]
    (if all
      (->> locale-list
           (distinct)
           (mapv (fn [lang] (str locale-root "/" lang "/LC_MESSAGES/" domain ".mo"))))
      (first paths))))
