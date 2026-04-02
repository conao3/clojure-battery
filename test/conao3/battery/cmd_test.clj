;; Original: Lib/test/test_cmd.py

(ns conao3.battery.cmd-test
  (:require
   [clojure.test :as t]
   [conao3.battery.cmd :as cmd]))

;; TestAlternateInput

(t/deftest  test-file-with-missing-final-nl
  (let [handlers {"print" (fn [args out] (.write out (str args "\n")) nil)
                  "EOF"   (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))
        output (cmd/run-cmdloop c "print test\nprint test2")]
    (t/is (= output "(Cmd) test\n(Cmd) test2\n(Cmd) "))))

(t/deftest  test-input-reset-at-eof
  (let [handlers {"print" (fn [args out] (.write out (str args "\n")) nil)
                  "EOF"   (fn [_ out] (.write out "*** Unknown syntax: EOF\n") true)}
        c (cmd/make-cmd handlers (fn [] true))
        output1 (cmd/run-cmdloop c "print test\nprint test2")]
    (t/is (= output1 "(Cmd) test\n(Cmd) test2\n(Cmd) *** Unknown syntax: EOF\n"))))

(t/deftest test-unknown-command-ignored
  (let [handlers {"print" (fn [args out] (.write out (str args "\n")) nil)
                  "EOF"   (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))
        output (cmd/run-cmdloop c "unknown foo\nprint bar")]
    (t/is (clojure.string/includes? output "bar"))
    (t/is (not (clojure.string/includes? output "foo")))))

(t/deftest test-empty-input
  (let [handlers {"EOF" (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))
        output (cmd/run-cmdloop c "")]
    (t/is (clojure.string/starts-with? output "(Cmd) "))))

(t/deftest test-multiple-commands
  (let [results (atom [])
        handlers {"cmd1" (fn [_ _] (swap! results conj "cmd1") nil)
                  "cmd2" (fn [_ _] (swap! results conj "cmd2") nil)
                  "EOF"  (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (cmd/run-cmdloop c "cmd1\ncmd2\ncmd1")
    (t/is (= @results ["cmd1" "cmd2" "cmd1"]))))

(t/deftest test-command-with-args
  (let [received (atom nil)
        handlers {"echo" (fn [args _] (reset! received args) nil)
                  "EOF"  (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (cmd/run-cmdloop c "echo hello world")
    (t/is (= "hello world" @received))))

(t/deftest test-make-cmd-returns-map
  (let [handlers {"EOF" (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (t/is (map? c))
    (t/is (contains? c :handlers))
    (t/is (contains? c :prompt))))

(t/deftest test-cmd-prompt-default
  (let [handlers {"EOF" (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (t/is (= "(Cmd) " (:prompt c)))))

(t/deftest test-output-written-to-handler
  (let [captured (atom "")
        handlers {"echo" (fn [args out] (.write out (str "ECHO:" args)) nil)
                  "EOF"  (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))
        output (cmd/run-cmdloop c "echo hello")]
    (t/is (clojure.string/includes? output "ECHO:hello"))))

(t/deftest test-eof-terminates-loop
  (let [call-count (atom 0)
        handlers {"tick" (fn [_ _] (swap! call-count inc) nil)
                  "EOF"  (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (cmd/run-cmdloop c "tick\ntick")
    (t/is (= 2 @call-count))))

(t/deftest test-handler-return-value-written
  ;; A non-nil return from a handler is written to the output
  (let [handlers {"greet" (fn [_ _] "hello!")
                  "EOF"   (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))
        output (cmd/run-cmdloop c "greet")]
    (t/is (clojure.string/includes? output "hello!"))))

(t/deftest test-cmd-whitespace-trimmed
  ;; Leading/trailing whitespace in the command line is trimmed
  (let [called (atom false)
        handlers {"hi" (fn [_ _] (reset! called true) nil)
                  "EOF" (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (cmd/run-cmdloop c "  hi  ")
    (t/is (true? @called))))

(t/deftest test-cmd-args-multiple-words
  ;; All tokens after the command name are passed as args string
  (let [received (atom nil)
        handlers {"say" (fn [args _] (reset! received args) nil)
                  "EOF" (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (cmd/run-cmdloop c "say one two three")
    (t/is (= "one two three" @received))))

(t/deftest test-cmd-empty-line-no-crash
  ;; Sending empty lines should not crash
  (let [handlers {"EOF" (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (t/is (string? (cmd/run-cmdloop c "\n\n\n")))))

(t/deftest test-handler-called-once-per-line
  ;; Each input line invokes the handler exactly once
  (let [call-log (atom [])
        handlers {"log" (fn [args _] (swap! call-log conj args) nil)
                  "EOF" (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))]
    (cmd/run-cmdloop c "log a\nlog b\nlog c")
    (t/is (= ["a" "b" "c"] @call-log))))
