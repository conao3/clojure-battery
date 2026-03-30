;; Original: Lib/test/test_string/test_string.py

(ns conao3.battery.string-test
  (:require
   [clojure.test :as t]
   [conao3.battery.string :as string])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-attrs
  (t/is (= " \t\n\r\u000b\u000c" string/whitespace))
  (t/is (= "abcdefghijklmnopqrstuvwxyz" string/ascii-lowercase))
  (t/is (= "ABCDEFGHIJKLMNOPQRSTUVWXYZ" string/ascii-uppercase))
  (t/is (= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" string/ascii-letters))
  (t/is (= "0123456789" string/digits))
  (t/is (= "0123456789abcdefABCDEF" string/hexdigits))
  (t/is (= "01234567" string/octdigits))
  (t/is (= "!\u0022#$%&'()*+,-./:;<=>?@[\\]^_`{|}~" string/punctuation))
  (t/is (= (str string/digits string/ascii-lowercase string/ascii-uppercase string/punctuation string/whitespace) string/printable)))

(t/deftest test-capwords
  (t/is (= "Abc Def Ghi" (string/capwords "abc def ghi")))
  (t/is (= "Abc Def Ghi" (string/capwords "abc\tdef\nghi")))
  (t/is (= "Abc Def Ghi" (string/capwords "abc\t   def  \nghi")))
  (t/is (= "Abc Def Ghi" (string/capwords "ABC DEF GHI")))
  (t/is (= "Abc-Def-Ghi" (string/capwords "ABC-DEF-GHI" "-")))
  (t/is (= "Abc-def Def-ghi Ghi" (string/capwords "ABC-def DEF-ghi GHI")))
  (t/is (= "Abc Def" (string/capwords "   aBc  DeF   ")))
  (t/is (= "Abc Def" (string/capwords "\taBc\tDeF\t")))
  (t/is (= "\tAbc\tDef\t" (string/capwords "\taBc\tDeF\t" "\t"))))

(t/deftest ^:kaocha/skip test-basic-formatter
  (t/is (= "foo" (string/formatter-format "foo")))
  (t/is (= "foobar" (string/formatter-format "foo{0}" "bar")))
  (t/is (= "foo6bar-6" (string/formatter-format "foo{1}{num}-{1}" "bar" 6)))
  (t/is (thrown? ExceptionInfo (string/formatter-format)))
  (t/is (thrown? ExceptionInfo (string/formatter-format-keyword))))

(t/deftest test-format-keyword-arguments
  (t/is (= "-test-" (string/formatter-format-keyword "-{arg}-" :arg "test")))
  (t/is (thrown? ExceptionInfo (string/formatter-format-keyword "-{arg}-")))
  (t/is (= "-test-" (string/formatter-format-keyword "-{self}-" :self "test")))
  (t/is (thrown? ExceptionInfo (string/formatter-format-keyword "-{self}-")))
  (t/is (= "-test-" (string/formatter-format-keyword "-{format_string}-" :format-string "test")))
  (t/is (thrown? ExceptionInfo (string/formatter-format-keyword "-{format_string}-"))))

(t/deftest ^:kaocha/skip test-auto-numbering
  (t/is (= "foo{}{}" (string/formatter-format "foo{}{}" "bar" 6)))
  (t/is (= "foobar6bar" (string/formatter-format "foo{1}{num}{1}" nil "bar" :num 6)))
  (t/is (thrown? ExceptionInfo (string/formatter-format "foo{1}{}" "bar" 6)))
  (t/is (thrown? ExceptionInfo (string/formatter-format "foo{}{1}" "bar" 6))))

(t/deftest ^:kaocha/skip test-conversion-specifiers
  (t/is (= "-'test'-" (string/formatter-format "-{arg!r}-" :arg "test")))
  (t/is (= "test" (string/formatter-format "{0!s}" "test")))
  (t/is (thrown? ExceptionInfo (string/formatter-format "{0!h}" "test")))
  (t/is (= "42" (string/formatter-format "{0!a}" 42)))
  (t/is (= (str "'" string/ascii-letters "'") (string/formatter-format "{0!a}" string/ascii-letters)))
  (t/is (= "'\\xff'" (string/formatter-format "{0!a}" (char 255))))
  (t/is (= "'\\u0100'" (string/formatter-format "{0!a}" (char 256)))))

(t/deftest ^:kaocha/skip test-name-lookup
  (t/is (= "lumberjack" (string/formatter-format "{0.lumber}{0.jack}" {"x" {"lumber" "lumber" "jack" "jack"}})))
  (t/is (thrown? ExceptionInfo (string/formatter-format "{0.lumber}{0.jack}" ""))))

(t/deftest ^:kaocha/skip test-index-lookup
  (t/is (= "spameggs" (string/formatter-format "{0[2]}{0[0]}" ["eggs" "and" "spam"])))
  (t/is (thrown? ExceptionInfo (string/formatter-format "{0[2]}{0[0]}" [])))
  (t/is (thrown? ExceptionInfo (string/formatter-format "{0[2]}{0[0]}" {}))))

(t/deftest ^:kaocha/skip test-auto-numbering-lookup
  (t/is (= "baz " (string/formatter-format "{.foo.bar:{[1].qux}}" {:foo {:bar "baz"}} [{:qux 4}])))
  (t/is (thrown? ExceptionInfo (string/formatter-format "{.foo.bar:{[1].qux}}" "" ""))))

(t/deftest ^:kaocha/skip test-auto-numbering-reenterability
  (t/is (= "X!!!" (string/formatter-format "{.a:{}}" {:a "X"} 3)))
  (t/is (thrown? ExceptionInfo (string/formatter-format "{.a:{}}" {:a "X"} 0))))

(t/deftest ^:kaocha/skip test-override-get-value
  (t/is (= "hello, world!" (string/formatter-get-value "greeting" :greeting {})))
  (t/is (thrown? ExceptionInfo (string/formatter-get-value "greeting" nil {}))))

(t/deftest ^:kaocha/skip test-override-format-field
  (t/is (= "*result*" (string/formatter-format-field "foo")))
  (t/is (thrown? ExceptionInfo (string/formatter-format-field))))

(t/deftest ^:kaocha/skip test-override-convert-field
  (t/is (= "'foo':None" (string/formatter-format "{0!r}:{0!x}" "foo" "foo")))
  (t/is (thrown? ExceptionInfo (string/formatter-format "{0!r}:{0!x}" "foo"))))

(t/deftest ^:kaocha/skip test-override-parse
  (t/is (= "*   foo    *" (string/formatter-parse "*|+0:^10s|*" "foo")))
  (t/is (thrown? ExceptionInfo (string/formatter-parse ""))))

(t/deftest test-check-unused-args
  (t/is (= "10" (string/formatter-check-unused-args "{0}" 10)))
  (t/is (= "10100" (string/formatter-check-unused-args "{0}{i}" 10 :i 100)))
  (t/is (= "1010020" (string/formatter-check-unused-args "{0}{i}{1}" 10 20 :i 100)))
  (t/is (thrown? ExceptionInfo (string/formatter-check-unused-args "{0}{i}{1}" 10 20 :i 100 :j 0)))
  (t/is (thrown? ExceptionInfo (string/formatter-check-unused-args "{0}" 10 20)))
  (t/is (thrown? ExceptionInfo (string/formatter-check-unused-args "{0}" 10 20 :i 100)))
  (t/is (thrown? ExceptionInfo (string/formatter-check-unused-args "{i}" 10 20 :i 100))))

(t/deftest test-vformat-recursion-limit
  (t/is (thrown? ExceptionInfo (string/formatter-vformat-recursion-limit "{i}" () {:i 100} -1)))
  (t/is (string? (string/formatter-vformat-recursion-limit "{i}" () {:i 100} 10))))

(t/deftest test-regular-templates
  (t/is (= "tim likes to eat a bag of ham worth $100"
           (string/template-substitute "$who likes to eat a bag of $what worth $$100" {"who" "tim" "what" "ham"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$who likes to eat a bag of $what worth $$100" {"who" "tim"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute))))

(t/deftest test-regular-templates-with-braces
  (t/is (= "tim likes ham for dinner" (string/template-substitute "$who likes ${what} for ${meal}" {"who" "tim" "what" "ham" "meal" "dinner"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$who likes ${what} for ${meal}" {"who" "tim" "what" "ham"}))))

(t/deftest test-regular-templates-with-upper-case
  (t/is (= "tim likes ham for dinner" (string/template-substitute "$WHO likes ${WHAT} for ${MEAL}" {"WHO" "tim" "WHAT" "ham" "MEAL" "dinner"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$WHO likes ${WHAT} for ${MEAL}" {}))))

(t/deftest test-regular-templates-with-non-letters
  (t/is (= "tim likes ham for dinner"
           (string/template-substitute "$_wh0_ likes ${_w_h_a_t_} for ${mea1}" {"_wh0_" "tim" "_w_h_a_t_" "ham" "mea1" "dinner"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$_wh0_ likes ${_w_h_a_t_} for ${mea1}" {"_wh0_" "tim"}))))

(t/deftest test-escapes
  (t/is (= "tim likes to eat a bag of ham worth $100" (string/template-substitute "$who likes to eat a bag of $what worth $$100" {"who" "tim" "what" "ham"})))
  (t/is (= "tim likes $" (string/template-substitute "$who likes $$" {"who" "tim"}))))

(t/deftest test-percents
  (t/is (= "%(foo)s baz baz" (string/template-safe-substitute "%(foo)s $foo ${foo}" {"foo" "baz"})))
  (t/is (= "%(foo)s baz" (string/template-safe-substitute "%(foo)s ${foo}" {"foo" "baz"}))))

(t/deftest test-stringification
  (t/is (= "tim has eaten 7 bags of ham today"
           (string/template-substitute "tim has eaten $count bags of ham today" {"count" 7})))
  (t/is (= "tim has eaten 7 bags of ham today"
           (string/template-safe-substitute "tim has eaten ${count} bags of ham today" {"count" 7}))))

(t/deftest ^:kaocha/skip test-tupleargs
  (t/is (= "('tim', 'fred') ate ('ham', 'kung pao')" (string/template-substitute "$who likes to eat a bag of $what" {"who" ["tim" "fred"] "what" ["ham" "kung pao"]})))
  (t/is (= "('tim', 'fred') ate ('ham', 'kung pao')" (string/template-safe-substitute "$who likes to eat a bag of $what" {"who" ["tim" "fred"] "what" ["ham" "kung pao"]}))))

(t/deftest test-safe-template
  (t/is (= "tim likes ${what} for ${meal}" (string/template-safe-substitute "$who likes ${what} for ${meal}" {"who" "tim"})))
  (t/is (= "tim likes ham for dinner" (string/template-safe-substitute "$who likes ham for dinner" {"who" "tim"})))
  (t/is (= "tim likes ham for dinner" (string/template-safe-substitute "$who likes $what for $meal" {"who" "tim" "what" "ham" "meal" "dinner"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$who likes $meal for dinner" {"who" "tim"}))))

(t/deftest test-invalid-placeholders
  (t/is (thrown? ExceptionInfo (string/template-substitute "$who likes $" {"who" "tim"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$who likes ${what)" {"who" "tim"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$who likes $100" {"who" "tim"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$who likes $\\u0131" {"who" "tim"}))))

(t/deftest ^:kaocha/skip test-idpattern-override
  (t/is (= "tim likes to eat a bag of ham" (string/template-substitute "$bag.foo.who likes to eat a bag of $bag.what" {"bag.foo.who" "tim" "bag.what" "ham"}))))

(t/deftest ^:kaocha/skip test-flags-override
  (t/is (= "tim likes ${WHAT} for dinner" (string/template-safe-substitute "$wHO likes ${WHAT} for ${meal}" {"wHO" "tim" "WHAT" "ham" "meal" "dinner"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "$wHO likes ${WHAT} for ${meal}" {"wHO" "tim"})))
  (t/is (= "tim likes ham for dinner" (string/template-safe-substitute "$wHO likes ${WHAT} for ${meal}" {"wHO" "tim" "WHAT" "ham" "meal" "dinner"}))))

(t/deftest test-idpattern-override-inside-outside
  (t/is (= "foo BAR" (string/template-substitute "$foo ${BAR}" {"foo" "foo" "BAR" "BAR"})))
  (t/is (= "baz" (string/template-substitute "$BAR" {"BAR" "baz"}))))

(t/deftest test-idpattern-override-inside-outside-invalid-unbraced
  (t/is (thrown? ExceptionInfo (string/template-substitute "$FOO" {"foo" "foo" "BAR" "BAR"})))
  (t/is (= "$FOO" (string/template-safe-substitute "$FOO" {"foo" "foo"}))))

(t/deftest ^:kaocha/skip test-pattern-override
  (t/is (= "tim likes to eat a bag of ham" (string/template-substitute "@bag.foo.who likes to eat a bag of @bag.what" {"bag.foo.who" "tim" "bag.what" "ham"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "@bag.foo.who likes to eat a bag of @bag.what" {})))
  (t/is (thrown? ExceptionInfo (string/template-safe-substitute "@bag.foo.who likes to eat a bag of @bag.what" {}))))

(t/deftest ^:kaocha/skip test-braced-override
  (t/is (thrown? ExceptionInfo (string/template-substitute "PyCon in $@@location@@" {})))
  (t/is (= "PyCon in Cleveland" (string/template-substitute "PyCon in $@@location@@" {"location" "Cleveland"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "PyCon in $@@location@@" {}))))

(t/deftest ^:kaocha/skip test-braced-override-safe
  (t/is (= "PyCon in $@@location@@" (string/template-safe-substitute "PyCon in $@@location@@" {})))
  (t/is (= "PyCon in Cleveland" (string/template-safe-substitute "PyCon in $@@location@@" {"location" "Cleveland"}))))

(t/deftest test-invalid-with-no-lines
  (t/is (thrown-with-msg? ExceptionInfo #"line 1, col 1" (string/template-substitute "$" {}))))

(t/deftest test-unicode-values
  (t/is (= (str "t" (str (char 255)) "m likes " (str "f" (char 254) (char 12) "d")) (string/template-substitute "$who likes $what" {"who" (str "t" (char 255) "m") "what" (str "f" (char 254) (char 12) "d")})))
  (t/is (= (str "t" (str (char 255)) "m likes " (str "f" (char 254) (char 12) "d")) (string/template-safe-substitute "$who likes $what" {"who" (str "t" (char 255) "m") "what" (str "f" (char 254) (char 12) "d")}))))

(t/deftest test-keyword-arguments
  (t/is (= "tim likes ham" (string/template-substitute "$who likes $what" {"who" "tim" "what" "ham"})))
  (t/is (= "tim likes ham" (string/template-substitute "$who likes $what" {:who "tim" :what "ham"})))
  (t/is (= "tim likes ham" (string/template-substitute "$who likes $what" {"who" "fred" "what" "kung pao"} :who "tim" :what "ham")))
  (t/is (= "the mapping is bozo" (string/template-substitute "the mapping is $mapping" {"foo" "none"} :mapping "bozo")))
  (t/is (= "the mapping is two" (string/template-substitute "the mapping is $mapping" {"foo" "none" "mapping" "two"}))))

(t/deftest test-keyword-arguments-safe
  (t/is (= "tim likes ham" (string/template-safe-substitute "$who likes $what" {"who" "tim" "what" "ham"})))
  (t/is (= "tim likes ham" (string/template-safe-substitute "$who likes $what" {"who" "tim" :what "ham"})))
  (t/is (= "the mapping is bozo" (string/template-safe-substitute "the mapping is $mapping" {"foo" "none"} :mapping "bozo")))
  (t/is (= "the self is bozo" (string/template-safe-substitute "the self is $self" {"self" "bozo"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "the mapping is $mapping" {"mapping" "one"} {})))
  (t/is (thrown? ExceptionInfo (string/template-safe-substitute "the mapping is $mapping" {"mapping" "one"} {}))))

(t/deftest ^:kaocha/skip test-delimiter-override
  (t/is (= "this bud is for you &" (string/template-substitute "this &gift is for &{who} &&" {"gift" "bud" "who" "you"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "this &gift is for &{who} &" {"gift" "bud" "who" "you"})))
  (t/is (= "this &gift is for &{who} &" (string/template-safe-substitute "this &gift is for &{who} &" {"gift" "bud" "who" "you"})))
  (t/is (= "this bud is for you &" (string/template-safe-substitute "this &gift is for &{who} &" {"gift" "bud" "who" "you"})))
  (t/is (thrown? ExceptionInfo (string/template-substitute "this &gift is for &{who} &" {"gift" "bud" "who" "you"})))
  (t/is (= "tim likes to eat a bag of ham worth $100" (string/template-substitute "@who likes to eat a bag of @{what} worth $100" {"who" "tim" "what" "ham"}))))

(t/deftest ^:kaocha/skip test-is-valid
  (t/is (true? (string/template-is-valid "$who likes to eat a bag of ${what} worth $$100")))
  (t/is (false? (string/template-is-valid "$who likes to eat a bag of ${what} worth $100")))
  (t/is (thrown? ExceptionInfo (string/template-is-valid "@bag.foo.who likes to eat a bag of @bag.what")))
  (t/is (false? (string/template-is-valid "$FOO"))))

(t/deftest ^:kaocha/skip test-get-identifiers
  (t/is (= ["who" "what"] (string/template-get-identifiers "$who likes to eat a bag of ${what} worth $$100")))
  (t/is (= ["who" "what"] (string/template-get-identifiers "$who likes to eat a bag of ${what} worth $$100; ${who} likes to eat a bag of $what worth $$100")))
  (t/is (= ["who" "what"] (string/template-get-identifiers "$who likes to eat a bag of ${what} worth $100")))
  (t/is (thrown? ExceptionInfo (string/template-get-identifiers "@bag.foo.who likes to eat a bag of @bag.what"))))
