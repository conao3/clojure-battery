(ns conao3.battery.urllib-parse
  (:import
   [java.net URI URLEncoder URLDecoder]
   [java.nio.charset StandardCharsets]))

(defrecord ParseResult [scheme netloc path params query fragment])
(defrecord SplitResult [scheme netloc path query fragment])

(defn- split-netloc [netloc]
  (if (empty? netloc)
    [nil nil nil nil]
    (let [[userinfo host-part] (if (clojure.string/includes? netloc "@")
                                 (let [idx (.lastIndexOf netloc "@")]
                                   [(subs netloc 0 idx) (subs netloc (inc idx))])
                                 [nil netloc])
          [username password] (when userinfo
                                (if (clojure.string/includes? userinfo ":")
                                  (let [idx (.indexOf userinfo ":")]
                                    [(subs userinfo 0 idx) (subs userinfo (inc idx))])
                                  [userinfo nil]))
          [host port] (if (and (clojure.string/starts-with? host-part "[")
                               (clojure.string/includes? host-part "]"))
                        [host-part nil]
                        (if (clojure.string/includes? host-part ":")
                          (let [idx (.lastIndexOf host-part ":")]
                            [(subs host-part 0 idx)
                             (Integer/parseInt (subs host-part (inc idx)))])
                          [host-part nil]))]
      [username password host port])))

(defn urlparse [url]
  (let [^URI uri (try (URI. ^String url) (catch Exception _ (URI. "")))
        scheme   (or (.getScheme uri) "")
        netloc   (let [auth (.getAuthority uri)] (or auth ""))
        path     (or (.getPath uri) "")
        query    (or (.getQuery uri) "")
        fragment (or (.getFragment uri) "")
        [_ _ hostname port] (split-netloc netloc)]
    (-> (->ParseResult scheme netloc path "" query fragment)
        (assoc :hostname hostname)
        (assoc :port port))))

(defn urlsplit [url]
  (let [result (urlparse url)]
    (->SplitResult (:scheme result) (:netloc result) (:path result) (:query result) (:fragment result))))

(defn urlunparse [[scheme netloc path params query fragment]]
  (str (when (seq scheme) (str scheme "://"))
       netloc
       (when (seq params) (str ";" params))
       path
       (when (seq query) (str "?" query))
       (when (seq fragment) (str "#" fragment))))

(defn urlunparse [components]
  (let [[scheme netloc path params query fragment] components]
    (str (when (seq scheme) (str scheme "://"))
         netloc
         path
         (when (seq params) (str ";" params))
         (when (seq query) (str "?" query))
         (when (seq fragment) (str "#" fragment)))))

(defn- quote-impl [s safe]
  (let [encoded (URLEncoder/encode ^String s "UTF-8")]
    (-> encoded
        (clojure.string/replace "+" "%20")
        (clojure.string/replace #"%2[Ff]" (if (clojure.string/includes? safe "/") "/" "%2F"))
        (clojure.string/replace "*" "%2A")
        (clojure.string/replace "%7E" "~"))))

(defn quote
  ([s] (quote-impl s "/"))
  ([s safe] (quote-impl s safe)))

(defn quote-plus [s]
  (URLEncoder/encode ^String s "UTF-8"))

(defn unquote [s]
  (URLDecoder/decode ^String s "UTF-8"))

(defn unquote-plus [s]
  (URLDecoder/decode ^String (clojure.string/replace s "+" " ") "UTF-8"))

(defn urlencode
  ([query] (urlencode query false))
  ([query doseq-flag]
   (let [pairs (if (map? query)
                 (seq query)
                 query)]
     (clojure.string/join "&"
                          (map (fn [[k v]]
                                 (str (quote-plus (str k)) "=" (quote-plus (str v))))
                               pairs)))))

(defn parse-qsl [qs]
  (when (seq qs)
    (mapv (fn [pair]
            (let [idx (.indexOf ^String pair "=")]
              (if (= idx -1)
                [(unquote-plus pair) ""]
                [(unquote-plus (subs pair 0 idx))
                 (unquote-plus (subs pair (inc idx)))])))
          (clojure.string/split qs #"&"))))

(defn parse-qs
  ([qs] (parse-qs qs false))
  ([qs keep-blank-values]
   (reduce (fn [m [k v]]
             (if (or keep-blank-values (seq v))
               (update m k (fnil conj []) v)
               m))
           {}
           (parse-qsl qs))))

(defn urljoin [base url]
  (if (empty? url)
    base
    (try
      (let [base-uri (URI. ^String base)
            result   (.resolve base-uri ^String url)]
        (.toString result))
      (catch Exception _
        url))))

(defn urldefrag [url]
  (let [idx (.indexOf ^String url "#")]
    (if (= idx -1)
      [url ""]
      [(subs url 0 idx) (subs url (inc idx))])))

(defn splittype [url]
  (let [idx (.indexOf ^String url ":")]
    (if (= idx -1)
      [nil url]
      [(subs url 0 idx) (subs url (inc idx))])))

(defn splithost [url]
  (if (clojure.string/starts-with? url "//")
    (let [path-start (or (let [i (.indexOf url "/" 2)] (when (>= i 0) i)) (count url))]
      [(subs url 2 path-start) (subs url path-start)])
    [nil url]))
