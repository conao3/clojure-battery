;; Original: Lib/test/test_textwrap.py

(ns conao3.battery.textwrap-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.textwrap :as textwrap])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- show
  [textin]
  (cond
    (vector? textin)
    (if (seq textin)
      (str/join "\n" (map-indexed (fn [idx item] (str "  " idx ": " (pr-str item))) textin))
      "  no lines")
    (string? textin)
    (str "  " (pr-str textin) "\n")
    :else
    (pr-str textin)))

(defn- check
  [expect result]
  (t/is (= expect result) (str "expected:\n" (show expect) "but got:\n" (show result))))

(defn- wrap-text
  [text width opts]
  (if (seq opts)
    (apply textwrap/wrap text width (mapcat identity opts))
    (textwrap/wrap text width)))

(defn- check-wrap
  ([text width expect]
   (check expect (wrap-text text width nil)))
  ([text width expect opts]
   (check expect (wrap-text text width opts))))

(defn- wrap-with-wrapper
  [wrapper text]
  (let [opts (dissoc wrapper :width)]
    (if (contains? wrapper :width)
      (apply textwrap/wrap text (:width wrapper) (mapcat identity opts))
      (apply textwrap/wrap text (mapcat identity opts)))))

(defn- check-split
  [wrapper text expect]
  (check expect (textwrap/_split wrapper text)))

;; WrapTestCase

(t/deftest ^:kaocha/skip test-wrap-simple
  (let [text "Hello there, how are you this fine day?  I'm glad to hear it!"]
    (check-wrap text 12 ["Hello there," "how are you" "this fine" "day?  I'm" "glad to hear" "it!"])
    (check-wrap text 42 ["Hello there, how are you this fine day?" "I'm glad to hear it!"])
    (check-wrap text 80 [text])))

(t/deftest ^:kaocha/skip test-wrap-empty-string
  (check-wrap "" 6 [])
  (check-wrap "" 6 [] {:drop-whitespace false}))

(t/deftest ^:kaocha/skip test-wrap-empty-string-with-initial-indent
  (check-wrap "" 6 [] {:initial-indent "++"})
  (check-wrap "" 6 [] {:initial-indent "++" :drop-whitespace false}))

(t/deftest ^:kaocha/skip test-wrap-whitespace
  (let [text "This is a paragraph that already has\nline breaks.  But some of its lines are much longer than the others,\nso it needs to be wrapped.\nSome lines are \ttabbed too.\nWhat a mess!\n"
        expect ["This is a paragraph that already has line"
                "breaks.  But some of its lines are much"
                "longer than the others, so it needs to be"
                "wrapped.  Some lines are  tabbed too.  What a"
                "mess!"]
        wrapper {:width 45 :fix-sentence-endings true}
        wrapped (wrap-with-wrapper wrapper text)]
    (check expect wrapped)
    (check (str/join "\n" expect) (textwrap/fill text wrapper))
    (check-wrap "\tTest\tdefault\t\ttabsize." 80 ["        Test    default         tabsize."])
    (check-wrap "\tTest\tcustom\t\ttabsize." 80 ["    Test    custom      tabsize."] {:tabsize 4})))

(t/deftest ^:kaocha/skip test-wrap-fix-sentence-endings
  (let [wrapper {:width 60 :fix-sentence-endings true}]
    (let [text "A short line. Note the single space."
          expect ["A short line.  Note the single space."]]
      (check expect (textwrap/wrap text (:width wrapper) :fix-sentence-endings true)))
    (let [text "Well, Doctor? What do you think?"
          expect ["Well, Doctor?  What do you think?"]]
      (check expect (textwrap/wrap text (:width wrapper) :fix-sentence-endings true)))
    (let [text "Well, Doctor?\nWhat do you think?"]
      (check ["Well, Doctor?  What do you think?"] (textwrap/wrap text (:width wrapper) :fix-sentence-endings true)))
    (let [text "I say, chaps! Anyone for \"tennis?\"\nHmmph!"
          expect ["I say, chaps!  Anyone for \"tennis?\"  Hmmph!"]]
      (check expect (textwrap/wrap text (:width wrapper) :fix-sentence-endings true)))
    (let [wrapper (assoc wrapper :width 20)]
      (check ["I say, chaps!" "Anyone for \"tennis?\"" "Hmmph!"]
             (wrap-with-wrapper wrapper "I say, chaps! Anyone for \"tennis?\"\nHmmph!")))
    (let [text "And she said, \"Go to hell!\"\nCan you believe that?"
          expect ["And she said, \"Go to" "hell!\"  Can you" "believe that?"]]
      (check expect (textwrap/wrap text 20 :fix-sentence-endings true))
      (check ["And she said, \"Go to hell!\"  Can you believe that?"]
             (textwrap/wrap text 60 :fix-sentence-endings true)))
    (check ["File stdio.h is nice."] (textwrap/wrap "File stdio.h is nice." 60 :fix-sentence-endings true))))

(t/deftest ^:kaocha/skip test-wrap-wrap-short
  (let [text "This is a\nshort paragraph."]
    (check-wrap text 20 ["This is a short" "paragraph."])
    (check-wrap text 40 ["This is a short paragraph."])))

(t/deftest ^:kaocha/skip test-wrap-wrap-short-1line
  (let [text "This is a short line."]
    (check-wrap text 30 ["This is a short line."])
    (check-wrap text 30 ["(1) This is a short line."] {:initial-indent "(1) "})))

(t/deftest ^:kaocha/skip test-wrap-hyphenated
  (let [text "this-is-a-useful-feature-for-reformatting-posts-from-tim-peters'ly"]
    (check-wrap text 40 ["this-is-a-useful-feature-for-" "reformatting-posts-from-tim-peters'ly"])
    (check-wrap text 41 ["this-is-a-useful-feature-for-" "reformatting-posts-from-tim-peters'ly"])
    (check-wrap text 42 ["this-is-a-useful-feature-for-reformatting-" "posts-from-tim-peters'ly"])
      (let [expect (str/split "this-|is-|a-|useful-|feature-|for-|reformatting-|posts-|from-|tim-|peters'ly" #"\|")
          wrapper {:width 1 :break-long-words false}]
      (check-wrap text 1 expect {:break-long-words false})
      (check-split wrapper text expect)))
  (check-split {:width 45} "e-mail" ["e-mail"])
  (check-split {:width 45} "Jelly-O" ["Jelly-O"])
  (check-split {:width 45} "half-a-crown" ["half-" "a-" "crown"]))

(t/deftest ^:kaocha/skip test-wrap-hyphenated-numbers
  (let [text "Python 1.0.0 was released on 1994-01-26.  Python 1.0.1 was\nreleased on 1994-02-15."]
    (check-wrap text 30 ["Python 1.0.0 was released on" "1994-01-26.  Python 1.0.1 was" "released on 1994-02-15."])
    (check-wrap text 40 ["Python 1.0.0 was released on 1994-01-26." "Python 1.0.1 was released on 1994-02-15."])
    (check-wrap text 1 (str/split text #" ") {:break-long-words false}))
  (let [text "I do all my shopping at 7-11."]
    (check-wrap text 25 ["I do all my shopping at" "7-11."])
    (check-wrap text 27 ["I do all my shopping at" "7-11."])
    (check-wrap text 29 ["I do all my shopping at 7-11."])
    (check-wrap text 1 (str/split text #" ") {:break-long-words false})))

(t/deftest ^:kaocha/skip test-wrap-em-dash
  (let [text "Em-dashes should be written -- thus."]
    (check-wrap text 25 ["Em-dashes should be" "written -- thus."])
    (check-wrap text 29 ["Em-dashes should be written" "-- thus."])
    (check-wrap text 30 ["Em-dashes should be written --" "thus."])
    (check-wrap text 35 ["Em-dashes should be written --" "thus."])
    (check-wrap text 36 ["Em-dashes should be written -- thus."])
    (check-wrap text 30 ["You can also do--" "this or even---" "this."])
    (check-wrap text 15 ["You can also do" "--this or even" "---this."])
    (check-wrap text 16 ["You can also do" "--this or even" "---this."])
    (check-wrap text 17 ["You can also do--" "this or even---" "this."])
    (check-wrap text 19 ["You can also do--" "this or even---" "this."])
    (check-wrap text 29 ["You can also do--this or even" "---this."])
    (check-wrap text 31 ["You can also do--this or even" "---this."])
    (check-wrap text 32 ["You can also do--this or even---" "this."])
    (check-wrap text 35 ["You can also do--this or even---" "this."])
    (check-split {:width 45}
                 "Here's an -- em-dash and--here's another---and another!"
                 ["Here's" " " "an" " " "--" " " "em-" "dash" " "
                  "and" "--" "here's" " " "another" "---" "and" " " "another!"]) 
    (check-split {:width 45} "and then--bam!--he was gone"
                 ["and" " " "then" "--" "bam!" "--" "he" " " "was" " " "gone"])))

(t/deftest ^:kaocha/skip test-wrap-unix-options
  (let [text "You should use the -n option, or --dry-run in its long form."]
    (check-wrap text 20 ["You should use the" "-n option, or --dry-" "run in its long" "form."])
    (check-wrap text 21 ["You should use the -n" "option, or --dry-run" "in its long form."])
    (let [expect ["You should use the -n option, or" "--dry-run in its long form."]]
      (check-wrap text 32 expect)
      (check-wrap text 34 expect)
      (check-wrap text 35 expect)
      (check-wrap text 38 expect)
      (check-wrap text 39 ["You should use the -n option, or --dry-" "run in its long form."])
      (check-wrap text 41 ["You should use the -n option, or --dry-run" "in its long form."])
      (check-wrap text 42 ["You should use the -n option, or --dry-run" "in its long form."])
      (check-wrap "the -n option, or --dry-run or --dryrun" 100
                 ["the" " " "-n" " " "option," " " "or" " " "--dry-" "run"
                  " " "or" " " "--dryrun"]
                 {:break-long-words false}))
    ))

(t/deftest ^:kaocha/skip test-wrap-funky-hyphens
  (let [wrapper {:width 45}]
    (check-split wrapper "what the--hey!" ["what" " " "the" " " "--" " " "hey!"])
    (check-split wrapper "what the--" ["what" " " "the--"])
    (check-split wrapper "what the--." ["what" " " "the--."])
    (check-split wrapper "--text--." ["--text--."])
    (check-split wrapper "--option" ["--option"])
    (check-split wrapper "--option-opt" ["--option-" "opt"])
    (check-split wrapper "foo --option-opt bar" ["foo" " " "--option-" "opt" " " "bar"])))

(t/deftest ^:kaocha/skip test-wrap-punct-hyphens
  (let [wrapper {:width 45}]
    (check-split wrapper "the 'wibble-wobble' widget" ["the" " " "'wibble-" "wobble'" " " "widget"])
    (check-split wrapper "the \"wibble-wobble\" widget" ["the" " " "\"wibble-" "wobble\"" " " "widget"])
    (check-split wrapper "the (wibble-wobble) widget" ["the" " " "(wibble-" "wobble)" " " "widget"])
    (check-split wrapper "the ['wibble-wobble'] widget" ["the" " " "['wibble-" "wobble']" " " "widget"])
    (check-split wrapper "what-d'you-call-it." ["what-" "d'you-" "call-" "it."])))

(t/deftest ^:kaocha/skip test-wrap-funky-parens
  (let [wrapper {:width 45}]
    (check-split wrapper "foo (--option) bar" ["foo" " " "(--option)" " " "bar"])
    (check-split wrapper "foo (bar) baz" ["foo" " " "(bar)" " " "baz"])
    (check-split wrapper "blah (ding dong), wubba" ["blah" " " "(ding" " " "dong)," " " "wubba"])))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-false
  (check-wrap " This is a    sentence with     much whitespace." 10
             [" This is a" "    " "sentence " "with     " "much white" "space."]
             {:drop-whitespace false}))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-false-whitespace-only
  (check-wrap "   " 6 ["   "] {:drop-whitespace false}))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-false-whitespace-only-with-indent
  (check-wrap "   " 6 ["     "] {:drop-whitespace false :initial-indent "  "}))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-whitespace-only
  (check-wrap "  " 6 []))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-leading-whitespace
  (let [text " This is a sentence with leading whitespace."]
    (check-wrap text 50 [" This is a sentence with leading whitespace."])
    (check-wrap text 30 [" This is a sentence with" "leading whitespace."])))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-whitespace-line
  (let [text "abcd    efgh"]
    (check-wrap text 6 ["abcd" "    " "efgh"] {:drop-whitespace false})
    (check-wrap text 6 ["abcd" "efgh"])))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-whitespace-only-with-indent-2
  (check-wrap "  " 6 [] {:initial-indent "++"}))

(t/deftest ^:kaocha/skip test-wrap-drop-whitespace-whitespace-indent
  (check-wrap "abcd efgh" 6 ["  abcd" "  efgh"] {:initial-indent "  " :subsequent-indent "  "}))

(t/deftest ^:kaocha/skip test-wrap-split
  (let [wrapper {:width 45}
        text "Hello there -- you goof-ball, use the -b option!"
        expected ["Hello" " " "there" " " "--" " " "you" " " "goof-" "ball," " " "use" " " "the" " " "-b" " " "option!"]]
    (check-split wrapper text expected)))

(t/deftest ^:kaocha/skip test-wrap-break-on-hyphens
  (check-wrap "yaba daba-doo" 10 ["yaba daba-" "doo"] {:break-on-hyphens true})
  (check-wrap "yaba daba-doo" 10 ["yaba" "daba-doo"] {:break-on-hyphens false}))

(t/deftest ^:kaocha/skip test-wrap-bad-width
  (let [text "Whatever, it doesn't matter."]
    (t/is (thrown? ExceptionInfo (textwrap/wrap text 0)))
    (t/is (thrown? ExceptionInfo (textwrap/wrap text -1)))))

(t/deftest ^:kaocha/skip test-wrap-no-split-at-umlaut
  (check-wrap "Die Empfänger-Auswahl" 13 ["Die" "Empfänger-" "Auswahl"]))

(t/deftest ^:kaocha/skip test-wrap-umlaut-followed-by-dash
  (check-wrap "aa ää-ää" 7 ["aa ää-" "ää"]))

(t/deftest ^:kaocha/skip test-wrap-non-breaking-space
  (check-wrap (str "This is a sentence with non-breaking" "\u00a0" "space.") 20
             ["This is a sentence" "with non-" "breaking\u00a0space."] {:break-on-hyphens true})
  (check-wrap (str "This is a sentence with non-breaking" "\u00a0" "space.") 20
             ["This is a sentence" "with" "non-breaking\u00a0space."] {:break-on-hyphens false}))

(t/deftest ^:kaocha/skip test-wrap-narrow-non-breaking-space
  (check-wrap (str "This is a sentence with non-breaking" "\u202f" "space.") 20
             ["This is a sentence" "with non-" "breaking\u202fspace."] {:break-on-hyphens true})
  (check-wrap (str "This is a sentence with non-breaking" "\u202f" "space.") 20
             ["This is a sentence" "with" "non-breaking\u202fspace."] {:break-on-hyphens false}))

;; MaxLinesTestCase

(t/deftest ^:kaocha/skip test-max-lines-simple
  (let [text "Hello there, how are you this fine day?  I'm glad to hear it!"]
    (check-wrap text 12 ["Hello [...]"] {:max-lines 0})
    (check-wrap text 12 ["Hello [...]"] {:max-lines 1})
    (check-wrap text 12 ["Hello there," "how [...]"] {:max-lines 2})
    (check-wrap text 13 ["Hello there," "how are [...]"] {:max-lines 2})
    (check-wrap text 80 [text] {:max-lines 1})
    (check-wrap text 12 ["Hello there," "how are you" "this fine" "day?  I'm" "glad to hear" "it!"] {:max-lines 6})))

(t/deftest ^:kaocha/skip test-max-lines-spaces
  (let [text "Hello there, how are you this fine day?  I'm glad to hear it!"]
    (check-wrap text 12 ["Hello there," "how are you" "this fine" "day? [...]"] {:max-lines 4})
    (check-wrap text 6 ["Hello" "[...]"] {:max-lines 2})
    (check-wrap (str text (str/join (repeat 10 " "))) 12
               ["Hello there," "how are you" "this fine" "day?  I'm" "glad to hear" "it!"] {:max-lines 6})))

(t/deftest ^:kaocha/skip test-max-lines-placeholder
  (let [text "Hello there, how are you this fine day?  I'm glad to hear it!"]
    (check-wrap text 12 ["Hello..."] {:max-lines 1 :placeholder "..."})
    (check-wrap text 12 ["Hello there," "how are..."] {:max-lines 2 :placeholder "..."})
    (t/is (thrown? ExceptionInfo (textwrap/wrap text 16 :initial-indent "    " :max-lines 1 :placeholder " [truncated]...")))
    (t/is (thrown? ExceptionInfo (textwrap/wrap text 16 :subsequent-indent "    " :max-lines 2 :placeholder " [truncated]...")))
    (check-wrap text 16 ["    Hello there," "  [truncated]..."]
               {:max-lines 2 :initial-indent "    " :subsequent-indent "  " :placeholder " [truncated]..."})
    (check-wrap text 16 ["  [truncated]..."]
               {:max-lines 1 :initial-indent "  " :subsequent-indent "    " :placeholder " [truncated]..."})
    (check-wrap text 80 [text] {:placeholder (str (repeat 1000 "."))})))

(t/deftest ^:kaocha/skip test-max-lines-placeholder-backtrack
  (check-wrap "Good grief Python features are advancing quickly!" 12 ["Good grief" "Python*****"]
             {:max-lines 3 :placeholder "*****"}))

;; LongWordTestCase

(t/deftest ^:kaocha/skip test-long-word-break-long
  (let [text "Did you say \"supercalifragilisticexpialidocious?\"\nHow *do* you spell that odd word, anyways?\n"]
    (check-wrap text 30 ["Did you say \"supercalifragilis" "ticexpialidocious?\" How *do*" "you spell that odd word," "anyways?"])
    (check-wrap text 50 ["Did you say \"supercalifragilisticexpialidocious?\"" "How *do* you spell that odd word, anyways?"])
    (check-wrap (str (repeat 10 "-") "hello") 10 ["----------"
                        "               h"
                        "               e"
                        "               l"
                        "               l"
                        "               o"]
               {:subsequent-indent "               "})
    (check-wrap text 12 ["Did you say" "\"supercalifr" "agilisticexp" "ialidocious?" "\" How *do*" "you spell" "that odd" "word," "anyways?"])))

(t/deftest ^:kaocha/skip test-long-word-nobreak-long
  (let [text "Did you say \"supercalifragilisticexpialidocious?\"\nHow *do* you spell that odd word, anyways?\n"
        wrapper {:width 30 :break-long-words 0}
        expect ["Did you say" "\"supercalifragilisticexpialidocious?\"" "How *do* you spell that odd" "word, anyways?"]]
    (check expect (wrap-with-wrapper wrapper text))
    (check expect (textwrap/wrap text 30 :break-long-words 0))))

(t/deftest ^:kaocha/skip test-long-word-max-lines-long
  (let [text "Did you say \"supercalifragilisticexpialidocious?\"\nHow *do* you spell that odd word, anyways?\n"]
    (check-wrap text 12 ["Did you say" "\"supercalifr" "agilisticexp" "[...]"] {:max-lines 4})))

;; LongWordWithHyphensTestCase

(t/deftest ^:kaocha/skip test-long-word-with-hyphens-on-hyphen
  (let [text1 (str "We used enyzme 2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate synthase.\n")
        text2 "1234567890-1234567890--this_is_a_very_long_option_indeed-good-bye\"\n"]
    (check-wrap text1 50 ["We used enyzme 2-succinyl-6-hydroxy-2,4-" "cyclohexadiene-1-carboxylate synthase."])
    (check-wrap text1 10 ["We used" "enyzme 2-" "succinyl-" "6-hydroxy-" "2,4-" "cyclohexad" "iene-1-" "carboxylat" "e" "synthase."])
    (check-wrap text2 10 ["1234567890" "-123456789" "0--this_is" "_a_very_lo" "ng_option_" "indeed-" "good-bye\""])))

(t/deftest ^:kaocha/skip test-long-word-with-hyphens-not-on-hyphen
  (let [text1 (str "We used enyzme 2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate synthase.\n")
        text2 "1234567890-1234567890--this_is_a_very_long_option_indeed-good-bye\"\n"]
    (check-wrap text1 50 ["We used enyzme 2-succinyl-6-hydroxy-2,4-cyclohexad" "iene-1-carboxylate synthase."] {:break-on-hyphens false})
    (check-wrap text1 10 ["We used" "enyzme 2-s" "uccinyl-6-" "hydroxy-2," "4-cyclohex" "adiene-1-c" "arboxylate" "synthase."] {:break-on-hyphens false})
    (check-wrap text2 10 ["1234567890" "-123456789" "0--this_is" "_a_very_lo" "ng_option_" "indeed-" "good-bye\""] {:break-on-hyphens false})))

(t/deftest ^:kaocha/skip test-long-word-with-hyphen-not-on-long-words
  (let [text1 (str "We used enyzme 2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate synthase.\n")
        text2 "1234567890-1234567890--this_is_a_very_long_option_indeed-good-bye\"\n"]
    (check-wrap text1 50 ["We used enyzme" "2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate" "synthase."] {:break-long-words false})
    (check-wrap text1 10 ["We used" "enyzme" "2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate" "synthase."] {:break-long-words false})
    (check-wrap text2 10 ["1234567890" "-123456789" "0--this_is" "_a_very_lo" "ng_option_" "indeed-" "good-bye\""] {:break-long-words false})))

(t/deftest ^:kaocha/skip test-long-word-with-no-break-long-words-or-hyphens
  (let [text1 (str "We used enyzme 2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate synthase.\n")
        text2 "1234567890-1234567890--this_is_a_very_long_option_indeed-good-bye\"\n"]
    (check-wrap text1 50 ["We used enyzme" "2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate" "synthase."] {:break-long-words false :break-on-hyphens false})
    (check-wrap text1 10 ["We used" "enyzme" "2-succinyl-6-hydroxy-2,4-cyclohexadiene-1-carboxylate" "synthase."] {:break-long-words false :break-on-hyphens false})
    (check-wrap text2 10 ["1234567890" "-123456789" "0--this_is" "_a_very_lo" "ng_option_" "indeed-" "good-bye\""] {:break-long-words false :break-on-hyphens false})))

;; IndentTestCases

(t/deftest ^:kaocha/skip test-indent-cases-fill
  (let [text "This paragraph will be filled, first\nwithout any indentation, and then with\nsome (including a hanging indent)."
        expect "This paragraph will be filled, first\nwithout any indentation, and then with\nsome (including a hanging indent)."]
    (check expect (textwrap/fill text 40)) ))

(t/deftest ^:kaocha/skip test-indent-cases-initial-indent
  (let [text "This paragraph will be filled, first\nwithout any indentation, and then with\nsome (including a hanging indent)."
        expect ["     This paragraph will be filled," "first without any indentation, and then" "with some (including a hanging indent)."]
        expect-text (str/join "\n" expect)]
    (check expect (textwrap/wrap text 40 :initial-indent "     "))
    (check expect-text (textwrap/fill text 40 :initial-indent "     "))))

(t/deftest ^:kaocha/skip test-indent-cases-subsequent-indent
  (let [text "This paragraph will be filled, first\nwithout any indentation, and then with\nsome (including a hanging indent)."
        expect "  * This paragraph will be filled, first\n    without any indentation, and then\n    with some (including a hanging\n    indent)."]
    (check expect (textwrap/fill text 40 :initial-indent "  * " :subsequent-indent "    "))))

;; DedentTestCase

(defn- assert-unchanged [text]
  (check text (textwrap/dedent text)))

(t/deftest ^:kaocha/skip test-dedent-type-error
  (t/is (thrown? ExceptionInfo (textwrap/dedent 0)))
  (t/is (thrown? ExceptionInfo (textwrap/dedent (byte-array 0)))))

(t/deftest ^:kaocha/skip test-dedent-whitespace
  (let [text ""]
    (assert-unchanged text))
  (assert-unchanged "    ")
  (assert-unchanged "\t\t\t\t")
  (assert-unchanged " \t  \t\t  \t ")
  (check "\n" (textwrap/dedent "\f\n\r\t\u000B "))
  (assert-unchanged "\n")
  (check "\n\n\n\n\n" (textwrap/dedent "\r\n"))
  (check "\n\n\n\n\n\n" (textwrap/dedent "    \n\t\n  \n\t\t\n\n\n       ")))

(t/deftest ^:kaocha/skip test-dedent-nomargin
  (assert-unchanged "Hello there.\nHow are you?\nOh good, I'm glad.")
  (assert-unchanged "Hello there.\n\nBoo!")
  (assert-unchanged "Hello there.\n  This is indented.")
  (assert-unchanged "Hello there.\n\n  Boo!\n"))

(t/deftest ^:kaocha/skip test-dedent-even
  (check "Hello there.\nHow are ya?\nOh good." (textwrap/dedent "  Hello there.\n  How are ya?\n  Oh good."))
  (check "Hello there.\n\nHow are ya?\nOh good.\n" (textwrap/dedent "  Hello there.\n\n  How are ya?\n  Oh good.\n"))
  (check "Hello there.\n\nHow are ya?\nOh good.\n" (textwrap/dedent "  Hello there.\n  \n  How are ya?\n  Oh good.\n")))

(t/deftest ^:kaocha/skip test-dedent-uneven
  (let [text (str "        def foo():\n            while 1:\n                return foo\n")]
    (check "def foo():\n    while 1:\n        return foo\n" (textwrap/dedent text)))
  (check "Foo\n  Bar\n\n Baz\n" (textwrap/dedent "  Foo\n    Bar\n\n   Baz\n"))
  (check "Foo\n  Bar\n\n Baz\n" (textwrap/dedent "  Foo\n    Bar\n \n   Baz\n")))

(t/deftest ^:kaocha/skip test-dedent-declining
  (check " Foo\nBar\n" (textwrap/dedent "     Foo\n    Bar\n"))
  (check " Foo\n\nBar\n" (textwrap/dedent "     Foo\n\n    Bar\n"))
  (check " Foo\n\nBar\n" (textwrap/dedent "     Foo\n    \n    Bar\n")))

(t/deftest ^:kaocha/skip test-dedent-preserve-internal-tabs
  (let [text "  hello\tthere\n  how are\tyou?"
        expect "hello\tthere\nhow are\tyou?"]
    (check expect (textwrap/dedent text))
    (check expect (textwrap/dedent expect))))

(t/deftest ^:kaocha/skip test-dedent-preserve-margin-tabs
  (let [tests ["  hello there\n\thow are you?"
               "        hello there\n\thow are you?"
               "\thello there\n\thow are you?"
               "  \thello there\n  \thow are you?"
               "  \t  hello there\n  \t  how are you?"
               "  \thello there\n  \t  how are you?"
               "  \thello there\n   \thow are you?\n \tI'm fine, thanks"]
        expects ["hello there\nhow are you?"
                 "        hello there\n\thow are you?"
                 "hello there\nhow are you?"
                 "  \thello there\n  \thow are you?"
                 "hello there\n  how are you?"
                 "hello there\n  how are you?"
                 " \thello there\n  \thow are you?\n\tI'm fine, thanks"]]
    (doseq [[input expect] (map vector tests expects)]
      (assert-unchanged input)
      (check expect (textwrap/dedent input)))))

;; IndentTestCase

(def indent-roundtrip-cases
  ["Hi.\nThis is a test.\nTesting."
   "Hi.\nThis is a test.\n\nTesting."
   "\nHi.\nThis is a test.\nTesting.\n"])

(def indent-cases
  (concat indent-roundtrip-cases
          ["Hi.\r\nThis is a test.\r\nTesting.\r\n"
           "\nHi.\r\nThis is a test.\n\r\nTesting.\r\n\n"]))

(t/deftest ^:kaocha/skip test-indent-nomargin-default
  (doseq [text indent-cases]
    (check text (textwrap/indent text ""))))

(t/deftest ^:kaocha/skip test-indent-nomargin-explicit-default
  (doseq [text indent-cases]
    (check text (textwrap/indent text "" nil))))

(t/deftest ^:kaocha/skip test-indent-nomargin-all-lines
  (doseq [text indent-cases]
    (check text (textwrap/indent text "" (constantly true)))))

(t/deftest ^:kaocha/skip test-indent-no-lines
  (doseq [text indent-cases]
    (check text (textwrap/indent text "    " (constantly false)))))

(t/deftest ^:kaocha/skip test-roundtrip-spaces
  (doseq [text indent-roundtrip-cases]
    (check text (textwrap/dedent (textwrap/indent text "    ")))))

(t/deftest ^:kaocha/skip test-roundtrip-tabs
  (doseq [text indent-roundtrip-cases]
    (check text (textwrap/dedent (textwrap/indent text "\t\t")))))

(t/deftest ^:kaocha/skip test-roundtrip-mixed
  (doseq [text indent-roundtrip-cases]
    (check text (textwrap/dedent (textwrap/indent text " \t  \t ")))))

(t/deftest ^:kaocha/skip test-indent-default
  (let [prefix "  "
        expected ["  Hi.\n  This is a test.\n  Testing."
                  "  Hi.\n  This is a test.\n\n  Testing."
                  "\n  Hi.\n  This is a test.\n  Testing.\n"
                  "  Hi.\r\n  This is a test.\r\n  Testing.\r\n"
                  "\n  Hi.\r\n  This is a test.\n\r\n  Testing.\r\n\n"]]
    (doseq [[text expect] (map vector indent-cases expected)]
      (check expect (textwrap/indent text prefix)))))

(t/deftest ^:kaocha/skip test-indent-explicit-default
  (let [prefix "  "
        expected ["  Hi.\n  This is a test.\n  Testing."
                  "  Hi.\n  This is a test.\n\n  Testing."
                  "\n  Hi.\n  This is a test.\n  Testing.\n"
                  "  Hi.\r\n  This is a test.\r\n  Testing.\r\n"
                  "\n  Hi.\r\n  This is a test.\n\r\n  Testing.\r\n\n"]]
    (doseq [[text expect] (map vector indent-cases expected)]
      (check expect (textwrap/indent text prefix nil)))))

(t/deftest ^:kaocha/skip test-indent-all-lines
  (let [prefix "  "
        expected ["  Hi.\n  This is a test.\n  Testing."
                  "  Hi.\n  This is a test.\n  \n  Testing."
                  "  \n  Hi.\n  This is a test.\n  Testing.\n"
                  "  Hi.\r\n  This is a test.\r\n  Testing.\r\n"
                  "  \n  Hi.\r\n  This is a test.\n  \r\n  Testing.\r\n  \n"]]
    (doseq [[text expect] (map vector indent-cases expected)]
      (check expect (textwrap/indent text prefix (constantly true))))))

(t/deftest ^:kaocha/skip test-indent-empty-lines
  (let [prefix "  "
        expected ["Hi.\nThis is a test.\nTesting."
                  "Hi.\nThis is a test.\n  \nTesting."
                  "  \nHi.\nThis is a test.\nTesting.\n"
                  "Hi.\r\nThis is a test.\r\nTesting.\r\n"
                  "  \nHi.\r\nThis is a test.\n  \r\nTesting.\r\n  \n"]]
    (doseq [[text expect] (map vector indent-cases expected)]
      (check expect (textwrap/indent text prefix #(not (str/blank? %)))))))

;; ShortenTestCase

(defn- check-shorten
  [text width expect & [opts]]
  (let [result (if (seq opts)
                 (apply textwrap/shorten text width (mapcat identity opts))
                 (textwrap/shorten text width))]
    (check expect result)))

(t/deftest ^:kaocha/skip test-shorten-simple
  (let [text "Hello there, how are you this fine day? I'm glad to hear it!"]
    (check-shorten text 18 "Hello there, [...]")
    (check-shorten text (count text) text)
    (check-shorten text (dec (count text)) "Hello there, how are you this fine day? I'm glad to [...]") ))

(t/deftest ^:kaocha/skip test-shorten-placeholder
  (let [text "Hello there, how are you this fine day? I'm glad to hear it!"]
    (check-shorten text 17 "Hello there,$$" {:placeholder "$$"})
    (check-shorten text 18 "Hello there, how$$" {:placeholder "$$"})
    (check-shorten text 18 "Hello there, $$" {:placeholder " $$"})
    (check-shorten text (count text) text {:placeholder "$$"})
    (check-shorten text (dec (count text)) "Hello there, how are you this fine day? I'm glad to hear$$" {:placeholder "$$"})))

(t/deftest ^:kaocha/skip test-shorten-empty-string
  (check-shorten "" 6 ""))

(t/deftest ^:kaocha/skip test-shorten-whitespace
  (let [text "\n            This is a  paragraph that  already has\n            line breaks and \t tabs too."
        
        expected-62 "This is a paragraph that already has line breaks and tabs too."]
    (check-shorten text 62 expected-62)
    (check-shorten text 61 "This is a paragraph that already has line breaks and [...]")
    (check-shorten "hello      world!  " 12 "hello world!" )
    (check-shorten "hello      world!  " 11 "hello [...]")
    (check-shorten "hello      world!  " 10 "[...]")))

(t/deftest ^:kaocha/skip test-shorten-width-too-small-for-placeholder
  (textwrap/shorten (str/join (repeat 20 "x")) 8 :placeholder "(......)")
  (t/is (thrown? ExceptionInfo (textwrap/shorten (str/join (repeat 20 "x")) 8 :placeholder "(.......)")))
  )

(t/deftest ^:kaocha/skip test-shorten-first-word-too-long-but-placeholder-fits
  (check-shorten "Helloo" 5 "[...]"))
