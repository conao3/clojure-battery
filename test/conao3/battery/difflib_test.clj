;; Original: Lib/test/test_difflib.py

(ns conao3.battery.difflib-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.difflib :as difflib])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- ->bytes
  ([s]
   (.getBytes s "UTF-8"))
  ([s encoding]
   (.getBytes s encoding)))


;; Excluded: load_tests (doctest registration)
;; Excluded: TestSFbugs.test_mdiff_catch_stop_iteration (_mdiff)
;; Excluded: TestOutputFormat.test_range_format_unified (_format_range_unified)
;; Excluded: TestOutputFormat.test_range_format_context (_format_range_context)

;; ---- TestWithAscii ----

(t/deftest test-with-ascii-one-insert
  (let [seq-a (str/join (repeat 100 "b"))
        seq-b (str "a" seq-a)
        sm (difflib/sequence-matcher nil seq-a seq-b)]
    (t/is (< (Math/abs (- (difflib/sequence-matcher-ratio sm) 0.995)) 1e-3))
    (t/is (= [["insert" 0 0 0 1] ["equal" 0 100 1 101]]
             (difflib/sequence-matcher-get-opcodes sm)))
    (t/is (= #{} (difflib/sequence-matcher-bpopular sm)))
    (let [seq2-a (str (str/join (repeat 50 "b")) "a" (str/join (repeat 50 "b")))
          sm2 (difflib/sequence-matcher nil seq2-a seq-a)]
      (t/is (< (Math/abs (- (difflib/sequence-matcher-ratio sm2) 0.995)) 1e-3))
      (t/is (= [["equal" 0 50 0 50]
                ["delete" 50 51 50 50]
                ["equal" 51 101 50 100]]
               (difflib/sequence-matcher-get-opcodes sm2)))
      (t/is (= #{} (difflib/sequence-matcher-bpopular sm2))))))

(t/deftest test-with-ascii-one-delete
  (let [a (str (str/join (repeat 40 "a")) "c" (str/join (repeat 40 "b")))
        b (str (str/join (repeat 40 "a")) (str/join (repeat 40 "b")))
        sm (difflib/sequence-matcher nil a b)]
    (t/is (< (Math/abs (- (difflib/sequence-matcher-ratio sm) 0.994)) 1e-3))
    (t/is (= [["equal" 0 40 0 40]
              ["delete" 40 41 40 40]
              ["equal" 41 81 40 80]]
             (difflib/sequence-matcher-get-opcodes sm)))))

(t/deftest test-with-ascii-opcode-caching
  (let [sm (difflib/sequence-matcher nil (str/join (repeat 100 "b")) (str "a" (str/join (repeat 100 "b")))
                              :isjunk #(= % ""))
        op (difflib/sequence-matcher-get-opcodes sm)]
    (t/is (= [["insert" 0 0 0 1] ["equal" 0 100 1 101]] op))
    (t/is (identical? op (difflib/sequence-matcher-get-opcodes sm)))))

(t/deftest test-with-ascii-bjunk
  (let [a (str (str/join (repeat 40 "a")) (str/join (repeat 40 "b"))
               (str/join (repeat 40 "b")))
        b (str (str/join (repeat 44 "a")) (str/join (repeat 40 "b")))]
    (t/is (= #{} (difflib/sequence-matcher-bjunk (difflib/sequence-matcher nil a b))))
    (t/is (= #{" "} (difflib/sequence-matcher-bjunk (difflib/sequence-matcher #(= % " ") a
                                                          (str b (str/join (repeat 20 " ")))))))
    (t/is (= #{" " "b"} (difflib/sequence-matcher-bjunk (difflib/sequence-matcher #(contains? #{" " "b"} %) a
                                                            (str b (str/join (repeat 20 " ")))))))))

;; ---- TestAutojunk ----

(t/deftest test-autojunk-one-insert-homogenous-sequence
  (let [seq1 (str/join (repeat 200 "b"))
        seq2 (str "a" (str/join (repeat 200 "b")))
        sm (difflib/sequence-matcher nil seq1 seq2)
        sm2 (difflib/sequence-matcher nil seq1 seq2 false)]
    (t/is (< (Math/abs (- (difflib/sequence-matcher-ratio sm) 0.0)) 1e-3))
    (t/is (= #{"b"} (difflib/sequence-matcher-bpopular sm)))
    (t/is (< (Math/abs (- (difflib/sequence-matcher-ratio sm2) 0.9975)) 1e-4))
    (t/is (= #{} (difflib/sequence-matcher-bpopular sm2)))))

;; ---- TestSFbugs ----

(t/deftest test-ratio-for-null-seqn
  (let [s (difflib/sequence-matcher nil [] [])]
    (t/is (= 1 (difflib/sequence-matcher-ratio s)))
    (t/is (= 1 (difflib/sequence-matcher-quick-ratio s)))
    (t/is (= 1 (difflib/sequence-matcher-real-quick-ratio s)))))

(t/deftest test-comparing-empty-lists
  (let [group (difflib/sequence-matcher-get-grouped-opcodes (difflib/sequence-matcher nil [] []))
        diff (difflib/unified-diff [] [])]
    (t/is (thrown? ExceptionInfo (difflib/next-value group)))
    (t/is (thrown? ExceptionInfo (difflib/next-value diff)))))

(t/deftest test-matching-blocks-cache
  (let [s (difflib/sequence-matcher "abxcd" "abcd")
        blocks (difflib/sequence-matcher-get-matching-blocks s)
        blocks2 (difflib/sequence-matcher-get-matching-blocks s)]
    (t/is (= (nth (nth blocks 0) 2) (nth (nth blocks2 0) 2)))
    (t/is (= (count blocks) (count blocks2)))))

(t/deftest test-added-tab-hint
  (let [diff (vec (difflib/differ-compare ["\tI am a buggy"] ["\t\tI am a bug"]))]
    (t/is (= "- \tI am a buggy" (nth diff 0)))
    (t/is (= "? \t          --" (nth diff 1)))
    (t/is (= "+ \t\tI am a bug" (nth diff 2)))))

(t/deftest test-hint-indented-properly-with-tabs
  (let [diff (vec (difflib/differ-compare ["\t \t \t^"] ["\t \t \t^\n"]))]
    (t/is (= "- \t \t \t^" (nth diff 0)))
    (t/is (= "+ \t \t \t^\n" (nth diff 1)))
    (t/is (= "? \t \t \t +" (nth diff 2)))))

;; ---- TestSFpatches ----

(t/deftest ^:kaocha/skip test-html-diff
  (let [from1 ["line1" "line2" "line3"]
        to1 ["line1" "line2x" "line3"]
        i (difflib/html-diff)
        html (difflib/html-diff-make-file i from1 to1 "from" "to" :context false :numlines 5)
        table (difflib/html-diff-make-table i from1 to1 "from" "to" :context true)
        expect (str/replace (str html "") "</body>" (str "\n" table "\n</body>"))
        fixture (slurp "/Users/conao/ghq/github.com/python/cpython/Lib/test/test_difflib_expect.html")]
    (t/is (= expect fixture))))

(t/deftest test-recursion-limit
  (let [n 2000
        a (vec (for [i (range (* n 2))] (str "K:" i)))
        b (vec (for [i (range (* n 2))] (str "V:" i)))
        sm (difflib/sequence-matcher nil a b)]
    (t/is (not (nil? sm)))))

(t/deftest test-make-file-default-charset
  (let [i (difflib/html-diff)
        out (difflib/html-diff-make-file i ["a"] ["b"] "from" "to")]
    (t/is (str/includes? out "charset=\"utf-8\""))))

(t/deftest test-make-file-iso88591-charset
  (let [i (difflib/html-diff)
        out (difflib/html-diff-make-file i ["a"] ["b"] "from" "to" :charset "iso-8859-1")]
    (t/is (str/includes? out "charset=\"iso-8859-1\""))))

(t/deftest test-make-file-usascii-charset-with-nonascii-input
  (let [i (difflib/html-diff)
        out (difflib/html-diff-make-file i ["bügel"] ["bügel2"] "from" "to" :charset "us-ascii")]
    (t/is (str/includes? out "charset=\"us-ascii\""))))

;; ---- TestDiffer ----

(t/deftest test-close-matches-aligned
  (let [a ["cat\n" "dog\n" "close match 1\n" "close match 2\n"]
        b ["close match 3\n" "close match 4\n" "kitten\n" "puppy\n"]
        m (vec (difflib/differ-compare a b))]
    (t/is (= 12 (count m)))
    (t/is (= "- cat\n" (nth m 0)))
    (t/is (= "+ puppy\n" (nth m 11)))))

(t/deftest test-differ-one-insert
  (let [m (vec (difflib/differ-compare "bb" "abb"))]
    (t/is (= "+ a" (nth m 0)))))

(t/deftest test-differ-one-delete
  (let [m (vec (difflib/differ-compare "abb" "bb"))]
    (t/is (= "- a" (nth m 0)))))

;; ---- TestOutputFormat ----

(t/deftest test-tab-delimiter
  (let [ud (vec (difflib/unified-diff ["one"] ["two"] "Original" "Current" "2005-01-26" "2010-04-02" :lineterm ""))
        cd (vec (difflib/context-diff ["one"] ["two"] "Original" "Current" "2005-01-26" "2010-04-02" :lineterm ""))]
    (t/is (= ["--- Original\t2005-01-26" "+++ Current\t2010-04-02"] (take 2 ud)))
    (t/is (= ["*** Original\t2005-01-26" "--- Current\t2010-04-02"] (take 2 cd)))))

(t/deftest test-no-trailing-tab-on-empty-filedate
  (let [ud (vec (difflib/unified-diff ["one"] ["two"] "Original" "Current" :lineterm ""))
        cd (vec (difflib/context-diff ["one"] ["two"] "Original" "Current" :lineterm ""))]
    (t/is (= ["--- Original" "+++ Current"] (take 2 ud)))
    (t/is (= ["*** Original" "--- Current"] (take 2 cd)))))

(t/deftest test-unified-diff-colored-output
  (let [actual (vec (difflib/unified-diff ["one" "three"] ["two" "three"] "Original" "Current" :lineterm "" :color true))]
    (t/is (= 6 (count actual)))))

;; ---- TestBytes ----

(t/deftest test-byte-content
  (let [a [(->bytes "hello" "ISO-8859-1") (.getBytes "andr\u00e9" "ISO-8859-1")]
        bb [(->bytes "hello") (.getBytes "andr\u00e9" "UTF-8")]]
    (doseq [elem (difflib/diff-bytes difflib/unified-diff a a)]
      (t/is (instance? (class (byte-array 0)) elem)))
    (doseq [elem (difflib/diff-bytes difflib/unified-diff a bb)]
      (t/is (instance? (class (byte-array 0)) elem)))
    (doseq [elem (difflib/diff-bytes difflib/context-diff a bb)]
      (t/is (instance? (class (byte-array 0)) elem)))))

(t/deftest test-byte-filenames
  (let [fna (byte-array (map unchecked-byte [0xB3 49 50 51 52]))
        fnb (byte-array (map unchecked-byte [0xC5 0x82 111 100 122 46 116 120 116]))
        a [(->bytes "hello")]
        b [(->bytes "hello")]
        out (vec (difflib/diff-bytes difflib/unified-diff a b fna fnb "2024" "2025" :lineterm (->bytes "")))]
    (t/is (empty? out))
    (doseq [elem out]
      (t/is (instance? (class (byte-array 0)) elem)))))

;; ---- TestInputTypes ----

(t/deftest test-input-type-checks
  (let [unified difflib/unified-diff
        context difflib/context-diff]
    (t/is (thrown? ExceptionInfo (unified "a" ["b"])))
    (t/is (thrown? ExceptionInfo (context "a" ["b"])))
    (t/is (thrown? ExceptionInfo (unified ["a"] [nil])))
    (t/is (thrown? ExceptionInfo (context ["a"] [nil])))
    (t/is (thrown? ExceptionInfo (unified [(->bytes "hello")] [(->bytes "hello")])))
    (t/is (thrown? ExceptionInfo (unified [(->bytes "hello")] ["hello"])))))

(t/deftest test-mixed-types-content
  (let [a [(->bytes "hello")]
        b ["hello"]
        unified difflib/unified-diff
        context difflib/context-diff]
    (t/is (thrown? ExceptionInfo (unified a b)))
    (t/is (thrown? ExceptionInfo (context a b)))))

(t/deftest test-mixed-types-filenames
  (let [a [(->bytes "hello\n")]
        b [(->bytes "ohell\n")]
        fna (->bytes "ol\u00e9.txt" "ISO-8859-1")
        fnb (->bytes "ol\u00e9.txt" "UTF-8")
        unified difflib/unified-diff]
    (t/is (thrown? ExceptionInfo (unified a b fna fnb)))
    (t/is (thrown? ExceptionInfo (unified b a fna fnb)))))

(t/deftest test-mixed-types-dates
  (let [a [(->bytes "foo\n")]
        b [(->bytes "bar\n")]
        unified difflib/unified-diff]
    (t/is (thrown? ExceptionInfo (unified a b "a" "b" "1 fév" "3 fév")))))

;; ---- TestJunkAPIs ----

(t/deftest test-is-line-junk-true
  (doseq [line ["#" "  " " #" "# " " # " ""]]
    (t/is (difflib/is-line-junk line ""))))

(t/deftest test-is-line-junk-false
  (doseq [line ["##" " ##" "## " "abc " "abc #" "Mr. Moose is up!"]]
    (t/is (not (difflib/is-line-junk line "")))))

(t/deftest test-is-line-junk-redos
  (let [x (str/join (repeat 1000000 "\t"))]
    (t/is (not (difflib/is-line-junk x "")))))

(t/deftest test-is-character-junk-true
  (doseq [ch [" " "\t"]]
    (t/is (difflib/is-character-junk ch ""))))

(t/deftest test-is-character-junk-false
  (doseq [ch ["a" "#" "\n" "\f" "\r" "\u000B"]]
    (t/is (not (difflib/is-character-junk ch "")))))

;; ---- TestFindLongest ----

(t/deftest test-find-longest-default-args
  (let [a "foo bar"
        b "foo baz bar"
        sm (difflib/sequence-matcher :a a :b b)
        match (difflib/sequence-matcher-find-longest-match sm)]
    (t/is (= 0 (:a match)))
    (t/is (= 0 (:b match)))
    (t/is (= 6 (:size match)))
    (t/is (contains? (set ["foo ba" "foo ba "]) (subs a (:a match) (+ (:a match) (:size match)))))))

(t/deftest test-find-longest-with-popular-chars
  (let [a "dabcd"
        b (str (str/join (repeat 100 "d")) "abc" (str/join (repeat 100 "d"))
               )
        sm (difflib/sequence-matcher :a a :b b)
        match (difflib/sequence-matcher-find-longest-match sm 0 (count a) 0 (count b))]
    (t/is (= 0 (:a match)))
    (t/is (= 99 (:b match)))
    (t/is (= 5 (:size match)))))

;; ---- TestCloseMatches ----

(t/deftest test-close-matches-invalid-inputs
  (let [words ["egg"]]
    (t/is (thrown? ExceptionInfo (difflib/get-close-matches "spam" words 0)))
    (t/is (thrown? ExceptionInfo (difflib/get-close-matches "spam" words -1)))
    (t/is (thrown? ExceptionInfo (difflib/get-close-matches "spam" words 3 1.1)))
    (t/is (thrown? ExceptionInfo (difflib/get-close-matches "spam" words 3 -0.1)))))

;; ---- TestRestore ----

(t/deftest test-restore-invalid-input
  (t/is (thrown? ExceptionInfo (str/join "" (difflib/restore [] 0))))
  (t/is (thrown? ExceptionInfo (str/join "" (difflib/restore [] 3)))))
