(ns conao3.battery.shlex
  (:require [clojure.string :as str]))

(def ^:private base-wordchars
  (set "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_"))

(def ^:private extra-wordchars (set "~-./*?="))

(def ^:private whitespace #{\space \tab \newline \return})

(def ^:private default-pchars (set "|&;<>()"))

(defn- get-pchars [punctuation-chars]
  (cond
    (= punctuation-chars true) default-pchars
    (string? punctuation-chars) (set punctuation-chars)
    :else #{}))

(defn- get-wordchars [pchars]
  (if (empty? pchars)
    base-wordchars
    (reduce disj (into base-wordchars extra-wordchars) pchars)))

(defn- do-tokenize [source posix? comments? ws-split? pchars]
  (let [wordchars (get-wordchars pchars)
        n (count source)
        pending (java.util.ArrayDeque.)]
    (letfn [(read-char [i]
              (if-let [c (.pollFirst pending)]
                [c i]
                (if (< i n) [(nth source i) (inc i)] [nil i])))
            (push-back [c]
              (.addFirst pending c))]
      (loop [i 0 state \space buf (StringBuilder.) tokens [] quoted false]
        (let [[c ni] (read-char i)]
          (cond
            (nil? c)
            (if (or (pos? (.length buf)) (and posix? quoted))
              (conj tokens (.toString buf))
              tokens)

            (= state \space)
            (cond
              (whitespace c)
              (recur ni \space buf tokens false)
              (and comments? (= c \#))
              (let [ni2 (loop [j ni]
                          (cond (>= j n) n
                                (= (nth source j) \newline) (inc j)
                                :else (recur (inc j))))]
                (recur ni2 \space buf tokens false))
              (and posix? (= c \\))
              (recur ni \\ buf tokens quoted)
              (wordchars c)
              (do (.append buf c) (recur ni \a buf tokens false))
              (pchars c)
              (do (.append buf c) (recur ni \c buf tokens false))
              (or (= c \") (= c \'))
              (do (when-not posix? (.append buf c))
                  (recur ni c buf tokens false))
              ws-split?
              (do (.append buf c) (recur ni \a buf tokens false))
              :else
              (recur ni \space buf (conj tokens (str c)) false))

            (or (= state \") (= state \'))
            (cond
              (= c state)
              (if posix?
                (recur ni \a buf tokens true)
                (do (.append buf c)
                    (let [tok (.toString buf)]
                      (.setLength buf 0)
                      (recur ni \space buf (conj tokens tok) true))))
              (and posix? (= state \") (= c \\))
              (let [[nc nni] (read-char ni)]
                (if (nil? nc)
                  (conj tokens (.toString buf))
                  (do (when-not (#{\\ \" \` \$ \newline} nc)
                        (.append buf c))
                      (.append buf nc)
                      (recur nni state buf tokens true))))
              :else
              (do (.append buf c) (recur ni state buf tokens quoted)))

            (= state \\)
            (do (.append buf c)
                (recur ni \a buf tokens quoted))

            (= state \a)
            (cond
              (whitespace c)
              (if (or (pos? (.length buf)) (and posix? quoted))
                (let [tok (.toString buf)]
                  (.setLength buf 0)
                  (recur ni \space buf (conj tokens tok) false))
                (recur ni \space buf tokens false))
              (and comments? (= c \#))
              (let [ni2 (loop [j ni]
                          (cond (>= j n) n
                                (= (nth source j) \newline) (inc j)
                                :else (recur (inc j))))]
                (if (or (pos? (.length buf)) (and posix? quoted))
                  (let [tok (.toString buf)]
                    (.setLength buf 0)
                    (recur ni2 \space buf (conj tokens tok) false))
                  (recur ni2 \space buf tokens false)))
              (and posix? (or (= c \") (= c \')))
              (recur ni c buf tokens quoted)
              (and (not posix?) (or (= c \") (= c \')))
              (do (.append buf c) (recur ni c buf tokens quoted))
              (and posix? (= c \\))
              (recur ni \\ buf tokens quoted)
              (pchars c)
              (let [tok (.toString buf)]
                (.setLength buf 0)
                (push-back c)
                (if (or (pos? (count tok)) (and posix? quoted))
                  (recur ni \space buf (conj tokens tok) false)
                  (recur ni \space buf tokens false)))
              (or (wordchars c) ws-split?)
              (do (.append buf c) (recur ni \a buf tokens quoted))
              :else
              (let [tok (.toString buf)
                    new-tokens (if (or (pos? (count tok)) (and posix? quoted))
                                 (conj tokens tok)
                                 tokens)]
                (.setLength buf 0)
                (if (empty? pchars)
                  (recur ni \space buf (conj new-tokens (str c)) false)
                  (do (push-back c)
                      (recur ni \space buf new-tokens false)))))

            (= state \c)
            (cond
              (pchars c)
              (do (.append buf c) (recur ni \c buf tokens false))
              :else
              (let [tok (.toString buf)]
                (.setLength buf 0)
                (push-back c)
                (recur ni \space buf (conj tokens tok) false)))))))))

(defn split
  ([s] (split s {}))
  ([s {:keys [comments posix] :or {comments false posix true}}]
   (when (nil? s)
     (throw (ex-info "No string to split" {:type :value-error})))
   (vec (do-tokenize s posix comments false #{}))))

(def ^:private safe-chars-re #"[a-zA-Z0-9@%_\-+=:,./]+")

(defn quote [s]
  (when-not (string? s)
    (throw (ex-info (str "expected string, got " (type s)) {:type :type-error})))
  (if (empty? s)
    "''"
    (if (re-matches safe-chars-re s)
      s
      (str "'" (str/replace s "'" "'\\''") "'"))))

(defn join [tokens]
  (str/join " " (map quote tokens)))

(defn make-shlex
  ([s] (make-shlex s {}))
  ([s opts]
   {:source s :opts opts}))

(defn tokenize [{:keys [source opts] :as shlex-obj}]
  (let [{:keys [posix punctuation-chars whitespace-split]
         :or {posix false punctuation-chars false whitespace-split false}} opts
        pchars (get-pchars punctuation-chars)
        ws-split? (or (boolean whitespace-split)
                      (boolean (:whitespace-split shlex-obj)))]
    (vec (do-tokenize source posix false ws-split? pchars))))
