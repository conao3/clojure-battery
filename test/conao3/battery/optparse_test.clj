;; Original: Lib/test/test_optparse.py

(ns conao3.battery.optparse-test
  (:require
   [clojure.test :as t]
   [conao3.battery.optparse :as optparse])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest ^:kaocha/skip test-opt-string-empty
  (let [test-id :optparse/test-opt-string-empty]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-opt-string-too-short
  (let [test-id :optparse/test-opt-string-too-short]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-opt-string-short-invalid
  (let [test-id :optparse/test-opt-string-short-invalid]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-opt-string-long-invalid
  (let [test-id :optparse/test-opt-string-long-invalid]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-attr-invalid
  (let [test-id :optparse/test-attr-invalid]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-action-invalid
  (let [test-id :optparse/test-action-invalid]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-type-invalid
  (let [test-id :optparse/test-type-invalid]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-type-for-action
  (let [test-id :optparse/test-no-type-for-action]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-choices-list
  (let [test-id :optparse/test-no-choices-list]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-bad-choices-list
  (let [test-id :optparse/test-bad-choices-list]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-choices-for-type
  (let [test-id :optparse/test-no-choices-for-type]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-const-for-action
  (let [test-id :optparse/test-no-const-for-action]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-nargs-for-action
  (let [test-id :optparse/test-no-nargs-for-action]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback-not-callable
  (let [test-id :optparse/test-callback-not-callable]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback-args-no-tuple
  (let [test-id :optparse/test-callback-args-no-tuple]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback-kwargs-no-dict
  (let [test-id :optparse/test-callback-kwargs-no-dict]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-callback-for-action
  (let [test-id :optparse/test-no-callback-for-action]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-callback-args-for-action
  (let [test-id :optparse/test-no-callback-args-for-action]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-callback-kwargs-for-action
  (let [test-id :optparse/test-no-callback-kwargs-for-action]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-single-dash
  (let [test-id :optparse/test-no-single-dash]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-add-option-no--option
  (let [test-id :optparse/test-add-option-no--option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-add-option-invalid-arguments
  (let [test-id :optparse/test-add-option-invalid-arguments]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-get-option
  (let [test-id :optparse/test-get-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-get-option-equals
  (let [test-id :optparse/test-get-option-equals]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-has-option
  (let [test-id :optparse/test-has-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-remove-short-opt
  (let [test-id :optparse/test-remove-short-opt]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-remove-long-opt
  (let [test-id :optparse/test-remove-long-opt]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-remove-nonexistent
  (let [test-id :optparse/test-remove-nonexistent]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-basics
  (let [test-id :optparse/test-basics]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-str-aliases-string
  (let [test-id :optparse/test-str-aliases-string]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-type-object
  (let [test-id :optparse/test-type-object]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-basic-defaults
  (let [test-id :optparse/test-basic-defaults]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-mixed-defaults-post
  (let [test-id :optparse/test-mixed-defaults-post]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-mixed-defaults-pre
  (let [test-id :optparse/test-mixed-defaults-pre]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-process-default
  (let [test-id :optparse/test-process-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-default-progname
  (let [test-id :optparse/test-default-progname]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-custom-progname
  (let [test-id :optparse/test-custom-progname]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-option-default
  (let [test-id :optparse/test-option-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-parser-default-1
  (let [test-id :optparse/test-parser-default-1]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-parser-default-2
  (let [test-id :optparse/test-parser-default-2]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-default
  (let [test-id :optparse/test-no-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-default-none-1
  (let [test-id :optparse/test-default-none-1]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-default-none-2
  (let [test-id :optparse/test-default-none-2]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-float-default
  (let [test-id :optparse/test-float-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-alt-expand
  (let [test-id :optparse/test-alt-expand]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-expand
  (let [test-id :optparse/test-no-expand]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-required-value
  (let [test-id :optparse/test-required-value]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-invalid-integer
  (let [test-id :optparse/test-invalid-integer]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-such-option
  (let [test-id :optparse/test-no-such-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-long-invalid-integer
  (let [test-id :optparse/test-long-invalid-integer]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-standard-empty
  (let [test-id :optparse/test-standard-empty]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-shortopt-empty-longopt-append
  (let [test-id :optparse/test-shortopt-empty-longopt-append]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-long-option-append
  (let [test-id :optparse/test-long-option-append]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-option-argument-joined
  (let [test-id :optparse/test-option-argument-joined]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-option-argument-split
  (let [test-id :optparse/test-option-argument-split]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-option-argument-joined-integer
  (let [test-id :optparse/test-option-argument-joined-integer]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-option-argument-split-negative-integer
  (let [test-id :optparse/test-option-argument-split-negative-integer]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-long-option-argument-joined
  (let [test-id :optparse/test-long-option-argument-joined]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-long-option-argument-split
  (let [test-id :optparse/test-long-option-argument-split]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-long-option-short-option
  (let [test-id :optparse/test-long-option-short-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-abbrev-long-option
  (let [test-id :optparse/test-abbrev-long-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-defaults
  (let [test-id :optparse/test-defaults]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-ambiguous-option
  (let [test-id :optparse/test-ambiguous-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-short-and-long-option-split
  (let [test-id :optparse/test-short-and-long-option-split]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-short-option-split-long-option-append
  (let [test-id :optparse/test-short-option-split-long-option-append]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-short-option-split-one-positional-arg
  (let [test-id :optparse/test-short-option-split-one-positional-arg]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-short-option-consumes-separator
  (let [test-id :optparse/test-short-option-consumes-separator]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-short-option-joined-and-separator
  (let [test-id :optparse/test-short-option-joined-and-separator]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-hyphen-becomes-positional-arg
  (let [test-id :optparse/test-hyphen-becomes-positional-arg]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-append-versus-append
  (let [test-id :optparse/test-no-append-versus-append]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-option-consumes-optionlike-string
  (let [test-id :optparse/test-option-consumes-optionlike-string]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-combined-single-invalid-option
  (let [test-id :optparse/test-combined-single-invalid-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-bool-default
  (let [test-id :optparse/test-bool-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-bool-false
  (let [test-id :optparse/test-bool-false]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-bool-true
  (let [test-id :optparse/test-bool-true]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-bool-flicker-on-and-off
  (let [test-id :optparse/test-bool-flicker-on-and-off]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-valid-choice
  (let [test-id :optparse/test-valid-choice]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-invalid-choice
  (let [test-id :optparse/test-invalid-choice]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-add-choice-option
  (let [test-id :optparse/test-add-choice-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-empty
  (let [test-id :optparse/test-count-empty]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-one
  (let [test-id :optparse/test-count-one]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-three
  (let [test-id :optparse/test-count-three]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-three-apart
  (let [test-id :optparse/test-count-three-apart]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-override-amount
  (let [test-id :optparse/test-count-override-amount]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-override-quiet
  (let [test-id :optparse/test-count-override-quiet]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-overriding
  (let [test-id :optparse/test-count-overriding]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-interspersed-args
  (let [test-id :optparse/test-count-interspersed-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-no-interspersed-args
  (let [test-id :optparse/test-count-no-interspersed-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-no-such-option
  (let [test-id :optparse/test-count-no-such-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-option-no-value
  (let [test-id :optparse/test-count-option-no-value]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-with-default
  (let [test-id :optparse/test-count-with-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-count-overriding-default
  (let [test-id :optparse/test-count-overriding-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-with-positional-args
  (let [test-id :optparse/test-nargs-with-positional-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-long-opt
  (let [test-id :optparse/test-nargs-long-opt]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-invalid-float-value
  (let [test-id :optparse/test-nargs-invalid-float-value]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-required-values
  (let [test-id :optparse/test-nargs-required-values]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-append
  (let [test-id :optparse/test-nargs-append]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-append-required-values
  (let [test-id :optparse/test-nargs-append-required-values]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-append-simple
  (let [test-id :optparse/test-nargs-append-simple]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-nargs-append-const
  (let [test-id :optparse/test-nargs-append-const]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-version
  (let [test-id :optparse/test-version]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-version
  (let [test-id :optparse/test-no-version]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-default
  (let [test-id :optparse/test-conflict-default]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-default-none
  (let [test-id :optparse/test-conflict-default-none]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-option-group-create-instance
  (let [test-id :optparse/test-option-group-create-instance]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-add-group-no-group
  (let [test-id :optparse/test-add-group-no-group]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-add-group-invalid-arguments
  (let [test-id :optparse/test-add-group-invalid-arguments]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-add-group-wrong-parser
  (let [test-id :optparse/test-add-group-wrong-parser]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-group-manipulate
  (let [test-id :optparse/test-group-manipulate]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-filetype-ok
  (let [test-id :optparse/test-filetype-ok]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-filetype-noexist
  (let [test-id :optparse/test-filetype-noexist]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-filetype-notfile
  (let [test-id :optparse/test-filetype-notfile]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-extend-add-action
  (let [test-id :optparse/test-extend-add-action]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-extend-add-action-normal
  (let [test-id :optparse/test-extend-add-action-normal]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback
  (let [test-id :optparse/test-callback]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback-help
  (let [test-id :optparse/test-callback-help]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback-extra-args
  (let [test-id :optparse/test-callback-extra-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback-meddle-args
  (let [test-id :optparse/test-callback-meddle-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-callback-meddle-args-separator
  (let [test-id :optparse/test-callback-meddle-args-separator]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-many-args
  (let [test-id :optparse/test-many-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-abbrev-callback-expansion
  (let [test-id :optparse/test-abbrev-callback-expansion]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-variable-args
  (let [test-id :optparse/test-variable-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-consume-separator-stop-at-option
  (let [test-id :optparse/test-consume-separator-stop-at-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-positional-arg-and-variable-args
  (let [test-id :optparse/test-positional-arg-and-variable-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-stop-at-option
  (let [test-id :optparse/test-stop-at-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-stop-at-invalid-option
  (let [test-id :optparse/test-stop-at-invalid-option]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-error
  (let [test-id :optparse/test-conflict-error]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-error-group
  (let [test-id :optparse/test-conflict-error-group]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-no-such-conflict-handler
  (let [test-id :optparse/test-no-such-conflict-handler]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-resolve
  (let [test-id :optparse/test-conflict-resolve]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-resolve-help
  (let [test-id :optparse/test-conflict-resolve-help]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-resolve-short-opt
  (let [test-id :optparse/test-conflict-resolve-short-opt]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-resolve-long-opt
  (let [test-id :optparse/test-conflict-resolve-long-opt]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-resolve-long-opts
  (let [test-id :optparse/test-conflict-resolve-long-opts]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-override-opts
  (let [test-id :optparse/test-conflict-override-opts]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-override-help
  (let [test-id :optparse/test-conflict-override-help]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-conflict-override-args
  (let [test-id :optparse/test-conflict-override-args]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-help
  (let [test-id :optparse/test-help]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-help-old-usage
  (let [test-id :optparse/test-help-old-usage]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-help-long-opts-first
  (let [test-id :optparse/test-help-long-opts-first]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-help-title-formatter
  (let [test-id :optparse/test-help-title-formatter]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-wrap-columns
  (let [test-id :optparse/test-wrap-columns]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-help-unicode
  (let [test-id :optparse/test-help-unicode]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-help-unicode-description
  (let [test-id :optparse/test-help-unicode-description]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-help-description-groups
  (let [test-id :optparse/test-help-description-groups]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-match-abbrev
  (let [test-id :optparse/test-match-abbrev]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-match-abbrev-error
  (let [test-id :optparse/test-match-abbrev-error]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-parse-num-fail
  (let [test-id :optparse/test-parse-num-fail]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-parse-num-ok
  (let [test-id :optparse/test-parse-num-ok]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-numeric-options
  (let [test-id :optparse/test-numeric-options]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test--all----
  (let [test-id :optparse/test--all----]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-translations
  (let [test-id :optparse/test-translations]
    (t/is (= test-id (optparse/test-dispatch test-id)))))

(t/deftest ^:kaocha/skip test-deprecated----version----
  (let [test-id :optparse/test-deprecated----version----]
    (t/is (= test-id (optparse/test-dispatch test-id)))))
