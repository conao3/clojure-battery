;; Original: Lib/signal.py

(ns conao3.battery.signal
  (:import
   [sun.misc Signal SignalHandler]))

(def SIG_DFL :SIG_DFL)
(def SIG_IGN :SIG_IGN)

(def SIGTERM 15)
(def SIGINT 2)
(def SIGABRT 6)
(def SIGFPE 8)
(def SIGILL 4)
(def SIGSEGV 11)
(def SIGBREAK (if (= "Windows" (System/getProperty "os.name")) 21 nil))
(def SIGHUP (if (not= "Windows" (System/getProperty "os.name")) 1 nil))

(def ^:private handlers (atom {}))

(defn- signum->name [signum]
  (case signum
    1 "HUP"
    2 "INT"
    6 "ABRT"
    8 "FPE"
    9 "KILL"
    11 "SEGV"
    14 "ALRM"
    15 "TERM"
    (str signum)))

(defn signal
  "Sets the handler for signal signum. Returns the previous handler."
  [signum handler]
  (let [old-handler (get @handlers signum SIG_DFL)
        sig-name (signum->name signum)]
    (swap! handlers assoc signum handler)
    (when (not= handler SIG_DFL)
      (try
        (Signal/handle
         (Signal. sig-name)
         (reify SignalHandler
           (handle [_ _sig]
             (when (fn? handler)
               (handler signum)))))
        (catch Exception _)))
    old-handler))

(defn getsignal
  "Returns the current signal handler for signum."
  [signum]
  (get @handlers signum SIG_DFL))

(defn raise-signal
  "Raises a signal."
  [signum]
  (let [handler (get @handlers signum SIG_DFL)]
    (cond
      (= handler SIG_IGN) nil
      (= handler SIG_DFL) nil
      (fn? handler) (handler signum)
      :else nil)))

(defn valid-signals
  "Returns the set of valid signal numbers."
  []
  #{SIGTERM SIGINT SIGABRT SIGFPE SIGILL SIGSEGV})

(defn strsignal
  "Returns a string description of signal signum."
  [signum]
  (case signum
    1 "Hangup"
    2 "Interrupt"
    6 "Aborted"
    8 "Floating point exception"
    9 "Killed"
    11 "Segmentation fault"
    14 "Alarm clock"
    15 "Terminated"
    (str "Signal " signum)))

(defn set-wakeup-fd
  "Sets the wakeup file descriptor (no-op in JVM)."
  [_fd]
  -1)
