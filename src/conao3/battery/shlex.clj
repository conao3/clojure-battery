(ns conao3.battery.shlex)

(defn split
  "Split a string into tokens using POSIX shell rules.
  Options: :comments (default false), :posix (default true)"
  ([s] (split s {}))
  ([s {:keys [comments posix] :or {comments false posix true}}]
   (when (nil? s)
     (throw (ex-info "No string to split" {:type :value-error})))
   (throw (ex-info "Not implemented" {}))))

(defn quote
  "Return a shell-escaped version of the string."
  [s]
  (when-not (string? s)
    (throw (ex-info (str "expected string, got " (type s)) {:type :type-error})))
  (throw (ex-info "Not implemented" {})))

(defn join
  "Concatenate tokens into a single string with shell quoting."
  [tokens]
  (throw (ex-info "Not implemented" {})))

(defn make-shlex
  "Create a shlex tokenizer from a string.
  Options: :posix (default false), :punctuation-chars (default false)"
  ([s] (make-shlex s {}))
  ([s opts]
   {:source s :opts opts}))

(defn tokenize
  "Return a seq of tokens from a shlex tokenizer."
  [shlex-obj]
  (throw (ex-info "Not implemented" {})))
