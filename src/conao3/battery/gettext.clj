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
  (translation-template :null))

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
  (not-implemented (str "gettext: " message)))

(defn ngettext
  [singular plural n]
  (not-implemented (str "ngettext: " singular plural n)))

(defn pgettext
  [context message]
  (not-implemented (str "pgettext: " context message)))

(defn dpgettext
  [domain context message]
  (not-implemented (str "dpgettext: " domain context message)))

(defn dgettext
  [domain message]
  (not-implemented (str "dgettext: " domain message)))

(defn dngettext
  [domain singular plural n]
  (not-implemented (str "dngettext: " domain singular plural n)))

(defn dnpgettext
  [domain context singular plural n]
  (not-implemented (str "dnpgettext: " domain context singular plural n)))

(defn c2py
  [expression]
  (fn [_]
    (not-implemented (str "c2py: " expression))))

(defn _expand-lang
  [locale]
  [locale])

(defn find
  [domain & {:keys [localedir languages all]}]
  (let [locale-root (or localedir ".")
        locale-list (or languages ["C"])
        paths (->> locale-list
                   (distinct)
                   (map #(str locale-root "/" % "/LC_MESSAGES/" domain ".mo"))
                   vec)]
    (if all
      paths
      (first paths))))
