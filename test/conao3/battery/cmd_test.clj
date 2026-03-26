;; Original: Lib/test/test_cmd.py

(ns conao3.battery.cmd-test
  (:require
   [clojure.test :as t]
   [conao3.battery.cmd :as cmd]))

;; LazyImportTest

(t/deftest ^:kaocha/skip test-lazy-import
  (t/is true))

;; TestAlternateInput

(t/deftest ^:kaocha/skip test-file-with-missing-final-nl
  (t/is true))

(t/deftest ^:kaocha/skip test-input-reset-at-eof
  (t/is true))

;; CmdTestReadline

(t/deftest ^:kaocha/skip test-basic-completion
  (t/is true))

(t/deftest ^:kaocha/skip test-bang-completion-without-do-shell
  (t/is true))
