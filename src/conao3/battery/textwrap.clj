(ns conao3.battery.textwrap
  (:require [clojure.string :as str]))

(def ^:private wordsep-re
  #"(?U)([\t\n\x0B\f\r ]+|(?<=[\w!\"'&.,?])-{2,}(?=\w)|[^\t\n\x0B\f\r ]+?(?:-(?:(?<=[^\d\W]{2}-)|(?<=[^\d\W]-[^\d\W]-))(?=[^\d\W]-?[^\d\W])|(?=[\t\n\x0B\f\r ]|\z)|(?<=[\w!\"'&.,?])(?=-{2,}\w)))")

(def ^:private sentence-end-re
  #"[a-z][\.\!\?][\"\']?\Z")

(defn- expand-tabs-str [s tabsize]
  (let [col (atom 0)
        sb (StringBuilder.)]
    (doseq [c s]
      (cond
        (= c \tab)
        (let [n (- tabsize (mod @col tabsize))]
          (dotimes [_ n] (.append sb \space))
          (swap! col + n))
        (or (= c \newline) (= c \return))
        (do (.append sb c) (reset! col 0))
        :else
        (do (.append sb c) (swap! col inc))))
    (.toString sb)))

(defn- munge-whitespace [text {:keys [expand-tabs tabsize replace-whitespace]
                                :or {expand-tabs true tabsize 8 replace-whitespace true}}]
  (cond-> text
    expand-tabs (expand-tabs-str tabsize)
    replace-whitespace (str/replace #"[\n\r\f\u000B]" " ")))

(defn- split-text [text {:keys [break-on-hyphens] :or {break-on-hyphens true}}]
  (if break-on-hyphens
    (mapv first (re-seq wordsep-re text))
    (filterv (complement empty?) (re-seq #"[\t\n\x0B\f\r ]+|[^\t\n\x0B\f\r ]+" text))))

(defn- fix-sentence-endings [chunks]
  (loop [i 0 chunks (vec chunks)]
    (if (>= i (count chunks))
      chunks
      (if (and (< i (dec (count chunks)))
               (= " " (get chunks (inc i)))
               (re-find sentence-end-re (get chunks i)))
        (recur (+ i 2) (assoc chunks (inc i) "  "))
        (recur (inc i) chunks)))))

(defn- handle-long-word [chunks cur-line width break-long-words]
  (let [space-left (if (< width 1) 1 (- width (count (str/join cur-line))))
        reversed-chunks (last chunks)]
    (if break-long-words
      (if (> (count reversed-chunks) space-left)
        [(subs reversed-chunks space-left)
         (conj cur-line (subs reversed-chunks 0 space-left))]
        ["" (conj cur-line reversed-chunks)])
      [reversed-chunks (if (empty? cur-line) (conj cur-line reversed-chunks) cur-line)])))

(defn- wrap-chunks [chunks {:keys [width initial-indent subsequent-indent
                                    drop-whitespace break-long-words max-lines placeholder]
                             :or {width 70 initial-indent "" subsequent-indent ""
                                  drop-whitespace true break-long-words true
                                  max-lines nil placeholder " [...]"}}]
  (when (< width 1)
    (throw (ex-info (str "invalid width " width " (must be > 0)") {})))
  (let [lines (atom [])
        chunks (atom (vec (reverse chunks)))]
    (while (seq @chunks)
      (let [cur-line (atom [])
            cur-len (atom 0)
            line-num (count @lines)
            indent (if (zero? line-num) initial-indent subsequent-indent)
            width-avail (- width (count indent))
            at-max (and max-lines (>= (inc line-num) max-lines))]
        (when (and drop-whitespace
                   (seq @chunks)
                   (re-matches #"\s+" (last @chunks))
                   (seq @lines))
          (swap! chunks pop))
        (while (and (seq @chunks)
                    (let [l (count (last @chunks))]
                      (or (<= (+ @cur-len l) width-avail)
                          (and (empty? @cur-line)
                               (not break-long-words)
                               (not (re-matches #"\s+" (last @chunks)))))))
          (let [chunk (last @chunks)]
            (swap! cur-len + (count chunk))
            (swap! cur-line conj chunk)
            (swap! chunks pop)))
        (when (and (seq @chunks) (> (count (last @chunks)) width-avail))
          (let [result (handle-long-word @chunks @cur-line width-avail break-long-words)]
            (reset! cur-line (second result))
            (reset! cur-len (count (str/join (second result))))
            (if (= "" (first result))
              (swap! chunks pop)
              (swap! chunks #(conj (pop %) (first result))))))
        (while (and drop-whitespace (seq @cur-line) (str/blank? (last @cur-line)))
          (swap! cur-len - (count (last @cur-line)))
          (swap! cur-line pop))
        (if (and at-max (seq @chunks)
                 (not (and drop-whitespace
                           (= 1 (count @chunks))
                           (str/blank? (last @chunks)))))
          (do
            (when (> (+ (count indent) (count (str/triml placeholder))) width)
              (throw (ex-info "placeholder too large for max width" {})))
            (loop []
              (if (empty? @cur-line)
                (if (and (seq @lines)
                         (<= (+ (count (str/trimr (last @lines))) (count placeholder))
                             (- width (count subsequent-indent))))
                  (swap! lines #(assoc % (dec (count %)) (str (str/trimr (last %)) placeholder)))
                  (swap! lines conj (str indent (str/triml placeholder))))
                (if (and (not (str/blank? (last @cur-line)))
                         (<= (+ @cur-len (count placeholder)) width-avail))
                  (swap! lines conj (str indent (str/join @cur-line) placeholder))
                  (do
                    (swap! cur-len - (count (last @cur-line)))
                    (swap! cur-line pop)
                    (recur)))))
            (reset! chunks []))
          (when (seq @cur-line)
            (swap! lines conj (str indent (str/join @cur-line)))))))
    @lines))

(defn- split-chunks [text opts]
  (-> text
      (munge-whitespace opts)
      (split-text opts)
      (cond-> (:fix-sentence-endings opts) fix-sentence-endings)))

(defn wrap [text width & opts]
  (let [options (apply hash-map opts)
        opts (merge {:width width} options)]
    (when (< width 1)
      (throw (ex-info (str "invalid width " width " (must be > 0)") {})))
    (if (empty? text)
      []
      (-> text
          (split-chunks opts)
          (wrap-chunks opts)))))

(defn fill [text & args]
  (let [[width-or-wrapper & rest-args] args]
    (if (map? width-or-wrapper)
      (str/join "\n" (apply wrap text (:width width-or-wrapper 70)
                            (mapcat identity (dissoc width-or-wrapper :width))))
      (str/join "\n" (apply wrap text width-or-wrapper rest-args)))))

(defn dedent [text]
  (when-not (string? text)
    (throw (ex-info "dedent requires a string" {})))
  (let [normalized (if (str/includes? text "\n")
                     (str/replace text #"(?m)^[ \t]+$" "")
                     text)
        indents (map second (re-seq #"(?m)^([ \t]*)(?:[^ \t\n])" normalized))
        margin (when (seq indents)
                 (reduce (fn [m indent]
                           (cond
                             (str/starts-with? indent m) m
                             (str/starts-with? m indent) indent
                             :else (loop [i 0]
                                     (if (and (< i (count m))
                                              (< i (count indent))
                                              (= (nth m i) (nth indent i)))
                                       (recur (inc i))
                                       (subs m 0 i)))))
                         (first indents)
                         (rest indents)))]
    (if (or (nil? margin) (empty? margin))
      normalized
      (str/replace normalized
                   (re-pattern (str "(?m)^" (java.util.regex.Pattern/quote margin)))
                   ""))))

(defn indent [text prefix & [predicate]]
  (let [pred (or predicate #(not (str/blank? %)))
        lines (re-seq #"[^\r\n]*(?:\r\n|\r|\n)|[^\r\n]+" text)]
    (->> lines
         (map (fn [line] (if (pred line) (str prefix line) line)))
         (str/join))))

(defn shorten [text width & opts]
  (let [options (apply hash-map opts)
        placeholder (get options :placeholder " [...]")
        opts (merge {:width width :max-lines 1} options)
        chunks (split-chunks (str/join " " (str/split (str/trim text) #"\s+")) opts)]
    (str/join "\n" (wrap-chunks chunks opts))))

(defn _split [wrapper text]
  (split-text (munge-whitespace text wrapper) wrapper))
