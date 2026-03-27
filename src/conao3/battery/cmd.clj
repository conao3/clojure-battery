(ns conao3.battery.cmd)

(defn make-cmd
  "Create a command interpreter.
  handlers: map of command-name -> fn(args-str -> any)
  eof-handler: fn() -> truthy to stop loop"
  [handlers eof-handler]
  {:handlers handlers
   :eof-handler eof-handler
   :prompt "(Cmd) "})

(defn run-cmdloop
  "Run a command loop reading from stdin-str, writing to out (Writer).
  Returns output string."
  [cmd stdin-str]
  (let [out (java.io.StringWriter.)
        reader (java.io.BufferedReader. (java.io.StringReader. stdin-str))
        {:keys [handlers eof-handler prompt]} cmd]
    (loop []
      (.write out prompt)
      (let [line (.readLine reader)]
        (if (nil? line)
          (when-let [stop (eof-handler)]
            (when-not stop
              (recur)))
          (let [[cmd-name & rest-parts] (clojure.string/split (clojure.string/trim line) #"\s+" 2)
                args (or (first rest-parts) "")
                handler (get handlers cmd-name)]
            (when handler
              (let [result (handler args out)]
                (when result
                  (.write out (str result "\n")))))
            (recur)))))
    (.toString out)))
