(ns conao3.battery.gettext
  (:import
   [java.io FileInputStream]
   [java.nio ByteBuffer ByteOrder]))

(def _localedirs (atom {}))
(def _translations (atom {}))
(def _current-domain (atom "messages"))
(def _installed-translation (atom nil))
(def ENOENT "ENOENT")

(defn- not-implemented
  [message]
  (throw (ex-info message {})))

(defn reset-gettext
  []
  (reset! _localedirs {})
  (reset! _translations {})
  (reset! _current-domain "messages")
  (reset! _installed-translation nil))

(defn- parse-metadata
  [raw]
  (let [lines (clojure.string/split raw #"\n")
        result (volatile! {})
        current-key (volatile! nil)]
    (doseq [line lines]
      (if (clojure.string/includes? line ":")
        (let [idx (clojure.string/index-of line ":")
              k (clojure.string/lower-case (subs line 0 idx))
              v (clojure.string/trim (subs line (inc idx)))]
          (vreset! current-key k)
          (vswap! result assoc k v))
        (when (and @current-key (seq line))
          (vswap! result update @current-key str "\n" line))))
    @result))

(defn- read-int
  [^ByteBuffer buf]
  (Integer/toUnsignedLong (.getInt buf)))

(defn- parse-mo-bytes
  [^bytes data]
  (let [buf (ByteBuffer/wrap data)
        _ (.order buf ByteOrder/LITTLE_ENDIAN)
        magic-le (Integer/toUnsignedLong (.getInt buf))
        order (cond
                (= magic-le 0x950412de) ByteOrder/LITTLE_ENDIAN
                (= magic-le 0xde120495) ByteOrder/BIG_ENDIAN
                :else (throw (ex-info (str "bad magic number: " magic-le) {:magic magic-le})))
        _ (.rewind buf)
        _ (.order buf order)
        _magic (.getInt buf)
        revision (Integer/toUnsignedLong (.getInt buf))
        major (bit-shift-right revision 16)
        _minor (bit-and revision 0xffff)
        _ (when (not= major 0)
            (throw (ex-info (str "bad major version number: " major) {:major major})))
        N (read-int buf)
        O (read-int buf)
        T (read-int buf)
        read-at (fn [^long offset ^long length]
                  (when (or (> (+ offset length) (alength data))
                            (neg? offset)
                            (neg? length))
                    (throw (ex-info "corrupt .mo file: offset out of bounds" {:offset offset :length length})))
                  (String. data (int offset) (int length) "UTF-8"))
        catalog (volatile! {})]
    (dotimes [i (int N)]
      (let [o-pos (+ O (* i 8))
            t-pos (+ T (* i 8))
            _ (when (or (> (+ o-pos 8) (alength data))
                        (> (+ t-pos 8) (alength data)))
                (throw (ex-info "corrupt .mo file: table out of bounds" {})))
            _ (.position buf (int o-pos))
            olen (read-int buf)
            ooff (read-int buf)
            _ (.position buf (int t-pos))
            tlen (read-int buf)
            toff (read-int buf)
            msgid (read-at ooff olen)
            msgstr (read-at toff tlen)]
        (vswap! catalog assoc msgid msgstr)))
    @catalog))

(defn- parse-mo-file
  [filepath]
  (let [file (java.io.File. ^String filepath)]
    (when-not (.exists file)
      (throw (ex-info (str "file not found: " filepath) {:filepath filepath})))
    (let [data (with-open [fis (FileInputStream. file)]
                 (let [size (.length file)
                       buf (byte-array size)]
                   (.read fis buf)
                   buf))]
      (parse-mo-bytes data))))

(defn- make-plural-fn
  [catalog]
  (let [metadata-str (get catalog "")
        plural-forms (when metadata-str
                       (get (parse-metadata metadata-str) "plural-forms"))
        nplurals (when plural-forms
                   (when-let [m (re-find #"nplurals\s*=\s*(\d+)" plural-forms)]
                     (Integer/parseInt (second m))))
        plural-expr (when plural-forms
                      (when-let [m (re-find #"plural\s*=\s*([^;]+)" plural-forms)]
                        (clojure.string/trim (second m))))]
    (fn [n]
      (when-not (integer? n)
        (throw (ex-info (str "plural count must be integer, got: " (type n)) {:n n})))
      (if plural-expr
        (let [result (try
                       (let [expr (-> plural-expr
                                      (clojure.string/replace #"\bn\b" (str n))
                                      (clojure.string/replace #"&&" "and")
                                      (clojure.string/replace #"\|\|" "or")
                                      (clojure.string/replace #"!(?!=)" "not "))]
                         (eval (read-string expr)))
                       (catch Exception _
                         (if (= n 1) 0 1)))]
          (cond
            (boolean? result) (if result 1 0)
            (integer? result) result
            :else (if (= n 1) 0 1)))
        (if (= n 1) 0 1)))))

(defn- expand-catalog
  [catalog]
  (reduce
   (fn [acc [k v]]
     (let [nul-in-k (clojure.string/index-of k "\u0000")
           eot-in-k (clojure.string/index-of k "\u0004")
           nul-in-v (clojure.string/index-of v "\u0000")]
       (cond
         (and eot-in-k nul-in-k)
         (let [ctx (subs k 0 eot-in-k)
               singular (subs k (inc eot-in-k) nul-in-k)
               plural (subs k (inc nul-in-k))
               v-parts (clojure.string/split v #"\u0000" -1)
               trans0 (first v-parts)
               trans1 (when (> (count v-parts) 1) (second v-parts))]
           (cond-> acc
             true (assoc k v)
             true (assoc (str ctx "\u0004" singular) v)
             trans1 (assoc (str ctx "\u0004" plural) trans1)))
         nul-in-k
         (let [singular (subs k 0 nul-in-k)]
           (assoc acc k v singular v))
         :else
         (assoc acc k v))))
   {}
   catalog))

(defn- make-gnu-translations
  [catalog]
  (let [expanded (expand-catalog catalog)
        metadata-str (get catalog "")
        info (when metadata-str (parse-metadata metadata-str))
        plural-fn (make-plural-fn catalog)
        lookup (fn [msgid]
                 (get expanded msgid))
        lookup-plural (fn [msgid idx]
                        (when-let [trans (get expanded msgid)]
                          (let [parts (clojure.string/split trans #"\u0000" -1)]
                            (when (< idx (count parts))
                              (nth parts idx)))))
        context-key (fn [ctx msg] (str ctx "\u0004" msg))]
    {:type :gnu
     :fallbacks []
     :catalog catalog
     :gettext (fn [message]
                (or (when-let [t (lookup message)]
                      (first (clojure.string/split t #"\u0000" -1)))
                    message))
     :ngettext (fn [singular plural n]
                 (when-not (integer? n)
                   (throw (ex-info (str "plural count must be integer, got: " (type n)) {:n n})))
                 (let [idx (plural-fn n)
                       key (str singular "\u0000" plural)]
                   (or (lookup-plural key idx)
                       (if (= n 1) singular plural))))
     :pgettext (fn [context message]
                 (or (when-let [t (lookup (context-key context message))]
                       (first (clojure.string/split t #"\u0000" -1)))
                     message))
     :npgettext (fn [context singular plural n]
                  (when-not (integer? n)
                    (throw (ex-info (str "plural count must be integer, got: " (type n)) {:n n})))
                  (let [idx (plural-fn n)
                        key (str (context-key context singular) "\u0000" plural)]
                    (or (lookup-plural key idx)
                        (if (= n 1) singular plural))))
     :info (fn [] (or info {}))
     :install (fn [_] nil)
     :add-fallback (fn [obj fallback]
                     (assoc obj :fallbacks (conj (:fallbacks obj) fallback)))}))

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
  [fp]
  (try
    (make-gnu-translations (parse-mo-file (str fp)))
    (catch clojure.lang.ExceptionInfo e
      (throw e))
    (catch Exception _
      (make-gnu-translations {}))))

(defn NullTranslations
  []
  (assoc (translation-template :null)
         :gettext (fn [message] message)
         :ngettext (fn [singular plural n] (if (= n 1) singular plural))
         :pgettext (fn [_context message] message)
         :npgettext (fn [_context singular plural n] (if (= n 1) singular plural))))

(defn- find-mo-file
  [domain]
  (let [localedir (or (get @_localedirs domain) ".")]
    (str localedir "/" domain ".mo")))

(defn- get-installed-translation
  []
  (or @_installed-translation
      (let [domain @_current-domain
            filepath (find-mo-file domain)]
        (try
          (let [t (make-gnu-translations (parse-mo-file filepath))]
            (reset! _installed-translation t)
            t)
          (catch Exception _
            (NullTranslations))))))

(defn- get-domain-translation
  [domain]
  (let [filepath (find-mo-file domain)]
    (try
      (make-gnu-translations (parse-mo-file filepath))
      (catch Exception _
        nil))))

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
    (let [filepath (str localedir "/" domain ".mo")]
      (try
        (make-gnu-translations (parse-mo-file filepath))
        (catch Exception _
          (GNUTranslations nil))))))

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
  ((:gettext (get-installed-translation)) message))

(defn ngettext
  [singular plural n]
  ((:ngettext (get-installed-translation)) singular plural n))

(defn pgettext
  [context message]
  ((:pgettext (get-installed-translation)) context message))

(defn dpgettext
  [domain context message]
  (if-let [t (get-domain-translation domain)]
    ((:pgettext t) context message)
    message))

(defn dgettext
  [domain message]
  (if-let [t (get-domain-translation domain)]
    ((:gettext t) message)
    message))

(defn dngettext
  [domain singular plural n]
  (if-let [t (get-domain-translation domain)]
    ((:ngettext t) singular plural n)
    (if (= n 1) singular plural)))

(defn dnpgettext
  [domain context singular plural n]
  (if-let [t (get-domain-translation domain)]
    ((:npgettext t) context singular plural n)
    (if (= n 1) singular plural)))

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
