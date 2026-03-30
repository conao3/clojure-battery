(ns conao3.battery.fileinput
  (:require [clojure.string :as str]))

(def default-state
  {:files ["-"]
   :inplace false
   :backup ""
   :mode "r"
   :openhook nil
   :encoding nil
   :errors nil
   :_file nil
   :filename nil
   :lineno 0
   :filelineno 0
   :fileno -1
   :isfirstline false
   :isstdin false})

(def _state (atom nil))

(def __all__
  ["file-input"
   "input"
   "close"
   "nextfile"
   "filename"
   "lineno"
   "filelineno"
   "fileno"
   "isfirstline"
   "isstdin"
   "hook-compressed"
   "hook-encoded"])

(defn- normalize-files [files]
  (let [values (or files ["-"])]
    (cond
      (and (sequential? values) (empty? values)) ["-"]
      (sequential? values) (vec values)
      :else [values])))

(defn- validate-mode! [mode]
  (when (contains? #{"w" "rU" "U"} mode)
    (throw (ex-info "ValueError" {:value mode}))))

(defn- validate-openhook! [inplace openhook]
  (when (and inplace openhook)
    (throw (ex-info "ValueError" {:reason "openhook and inplace"})))
  (when (and (some? openhook) (not (fn? openhook)))
    (throw (ex-info "ValueError" {:reason "openhook must be callable"}))))

(defn file-input
  [& {:keys [files inplace backup mode openhook encoding errors] :as _opts}]
  (let [mode (or mode (:mode default-state))
        files (normalize-files files)]
    (validate-mode! mode)
    (validate-openhook! (boolean inplace) openhook)
    (merge default-state
           {:files files
            :inplace (boolean inplace)
            :backup (or backup (:backup default-state))
            :mode mode
            :openhook openhook
            :encoding encoding
            :errors errors})))

(defn FileInput
  [& opts]
  (apply file-input opts))

(defn input
  [& {:keys [files inplace backup mode openhook encoding errors] :as opts}]
  (let [state @conao3.battery.fileinput/_state
        _file (:_file state)]
    (if (and (some? state) (some? _file))
      (throw (ex-info "input() already active" {}))
      (let [new-state (file-input
                       :files (or files [])
                       :inplace (boolean inplace)
                       :backup backup
                       :mode mode
                       :openhook openhook
                       :encoding encoding
                       :errors errors)]
        (reset! _state new-state)
        new-state))))

(defn- active-state []
  (if-some [state @conao3.battery.fileinput/_state]
    state
    (throw (ex-info "no active input()" {}))))

(defn close
  []
  (when-some [state @conao3.battery.fileinput/_state]
    (when (fn? (:close state))
      ((:close state)))
    (reset! _state nil))
  nil)

(defn nextfile
  []
  (:nextfile (active-state)))

(defn filename
  []
  (:filename (active-state)))

(defn lineno
  []
  (:lineno (active-state)))

(defn filelineno
  []
  (:filelineno (active-state)))

(defn fileno
  []
  (or (:fileno @conao3.battery.fileinput/_state) -1))

(defn isfirstline
  []
  (boolean (:isfirstline (active-state))))

(defn isstdin
  []
  (boolean (:isstdin (active-state))))

(defn hook-compressed
  [filename mode & {:keys [errors] :as _opts}]
  (let [encoding (if (str/includes? mode "b") nil "locale")]
    (fn [path mode']
      [path mode' encoding errors filename])))

(defn hook-encoded
  [encoding & {:keys [errors] :or {errors nil}}]
  (fn [path mode']
    [path mode' encoding errors]))
