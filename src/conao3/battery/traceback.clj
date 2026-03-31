;; Original: Lib/traceback.py

(ns conao3.battery.traceback
  (:import
   [java.io PrintWriter StringWriter]))

(defn format-exc
  "Returns the current exception's stack trace as a string."
  ([] (format-exc nil))
  ([^Throwable e]
   (let [sw (StringWriter.)
         pw (PrintWriter. sw)
         t (or e *e (RuntimeException. "No exception"))]
     (.printStackTrace t pw)
     (.toString sw))))

(defn print-exc
  "Prints the current exception's stack trace."
  ([] (print-exc nil))
  ([^Throwable e]
   (println (format-exc e))))

(defn format-tb
  "Returns the traceback of an exception as a vector of strings."
  [^Throwable e]
  (->> (.getStackTrace e)
       (mapv #(str "  File \"" (.getFileName %) "\", line " (.getLineNumber %)
                   ", in " (.getClassName %) "." (.getMethodName %)))))

(defn extract-tb
  "Returns a vector of stack trace elements from an exception."
  [^Throwable e]
  (->> (.getStackTrace e)
       (mapv (fn [^StackTraceElement ste]
               {:filename (.getFileName ste)
                :lineno (.getLineNumber ste)
                :name (.getMethodName ste)
                :classname (.getClassName ste)}))))

(defn extract-stack
  "Returns the current thread's stack trace."
  []
  (->> (.getStackTrace (Thread/currentThread))
       (mapv (fn [^StackTraceElement ste]
               {:filename (.getFileName ste)
                :lineno (.getLineNumber ste)
                :name (.getMethodName ste)
                :classname (.getClassName ste)}))))

(defn format-exception
  "Returns a formatted exception string."
  [^Throwable e]
  (format-exc e))

(defn print-exception
  "Prints a formatted exception."
  [^Throwable e]
  (print-exc e))

(defn format-list
  "Formats a list of stack trace entries."
  [entries]
  (mapv (fn [{:keys [filename lineno name]}]
          (str "  File \"" filename "\", line " lineno ", in " name "\n"))
        entries))

(defn clear-frames
  "Clears local variables from all frames in traceback (no-op in JVM)."
  [_tb]
  nil)

(defn walk-tb
  "Yields (frame, lineno) pairs for the exception's traceback."
  [^Throwable e]
  (->> (.getStackTrace e)
       (mapv (fn [ste] [ste (.getLineNumber ^StackTraceElement ste)]))))
