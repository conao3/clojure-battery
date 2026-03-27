;; Original: Lib/test/test_cmd.py

(ns conao3.battery.cmd-test
  (:require
   [clojure.test :as t]
   [conao3.battery.cmd :as cmd]))

;; TestAlternateInput

(t/deftest ^:kaocha/skip test-file-with-missing-final-nl
  (let [handlers {"print" (fn [args out] (.write out (str args "\n")) nil)
                  "EOF"   (fn [_ _] true)}
        c (cmd/make-cmd handlers (fn [] true))
        output (cmd/run-cmdloop c "print test\nprint test2")]
    (t/is (= output "(Cmd) test\n(Cmd) test2\n(Cmd) "))))

(t/deftest ^:kaocha/skip test-input-reset-at-eof
  (let [handlers {"print" (fn [args out] (.write out (str args "\n")) nil)
                  "EOF"   (fn [_ out] (.write out "*** Unknown syntax: EOF\n") true)}
        c (cmd/make-cmd handlers (fn [] true))
        output1 (cmd/run-cmdloop c "print test\nprint test2")]
    (t/is (= output1 "(Cmd) test\n(Cmd) test2\n(Cmd) *** Unknown syntax: EOF\n"))))
