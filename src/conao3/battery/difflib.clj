(ns conao3.battery.difflib
  (:require [clojure.string :as str]
            [clojure.set :as set]))

;; --- Junk helpers ---

(defn is-line-junk
  ([line] (is-line-junk line nil))
  ([line _]
   (and (< (count line) 1000)
        (boolean (re-matches #"\s*#?\s*" line)))))

(defn is-character-junk
  ([ch] (is-character-junk ch nil))
  ([ch _]
   (contains? #{" " "\t"} ch)))

;; --- SequenceMatcher internals ---

(defn- find-longest-match* [a b b2j bjunk alo ahi blo bhi]
  (let [best-i (atom alo)
        best-j (atom blo)
        best-size (atom 0)
        j2len (atom {})]
    (doseq [i (range alo ahi)]
      (let [new-j2len (volatile! {})
            elt (nth a i)]
        (doseq [j (clojure.core/get b2j elt [])]
          (when (and (>= j blo) (< j bhi))
            (let [k (inc (clojure.core/get @j2len (dec j) 0))]
              (vswap! new-j2len assoc j k)
              (when (> k @best-size)
                (reset! best-i (inc (- i k)))
                (reset! best-j (inc (- j k)))
                (reset! best-size k)))))
        (reset! j2len @new-j2len)))
    (let [i @best-i j @best-j k @best-size
          [i j k] (loop [i i j j k k]
                    (if (and (> i alo) (> j blo)
                             (not (bjunk (nth b (dec j))))
                             (= (nth a (dec i)) (nth b (dec j))))
                      (recur (dec i) (dec j) (inc k))
                      [i j k]))
          [i j k] (loop [i i j j k k]
                    (if (and (< (+ i k) ahi) (< (+ j k) bhi)
                             (not (bjunk (nth b (+ j k))))
                             (= (nth a (+ i k)) (nth b (+ j k))))
                      (recur i j (inc k))
                      [i j k]))
          [i j k] (loop [i i j j k k]
                    (if (and (> i alo) (> j blo)
                             (bjunk (nth b (dec j)))
                             (= (nth a (dec i)) (nth b (dec j))))
                      (recur (dec i) (dec j) (inc k))
                      [i j k]))
          [i j k] (loop [i i j j k k]
                    (if (and (< (+ i k) ahi) (< (+ j k) bhi)
                             (bjunk (nth b (+ j k)))
                             (= (nth a (+ i k)) (nth b (+ j k))))
                      (recur i j (inc k))
                      [i j k]))]
      {:a i :b j :size k})))

(defn- get-matching-blocks* [a b b2j bjunk alo ahi blo bhi]
  (let [{ai :a aj :b k :size} (find-longest-match* a b b2j bjunk alo ahi blo bhi)]
    (if (zero? k)
      []
      (concat
       (when (and (> ai alo) (> aj blo))
         (get-matching-blocks* a b b2j bjunk alo ai blo aj))
       [[ai aj k]]
       (when (and (< (+ ai k) ahi) (< (+ aj k) bhi))
         (get-matching-blocks* a b b2j bjunk (+ ai k) ahi (+ aj k) bhi))))))

(defn- compute-matching-blocks [a b b2j bjunk]
  (let [la (count a) lb (count b)]
    (vec (concat (get-matching-blocks* a b b2j bjunk 0 la 0 lb) [[la lb 0]]))))

(defn- blocks->opcodes [a b matching-blocks]
  (loop [blocks matching-blocks i 0 j 0 opcodes []]
    (if (empty? blocks)
      opcodes
      (let [[ai aj size] (first blocks)
            pre (cond
                  (and (< i ai) (< j aj)) [["replace" i ai j aj]]
                  (< i ai)                [["delete"  i ai j aj]]
                  (< j aj)                [["insert"  i ai j aj]]
                  :else                   [])
            eq  (if (pos? size) [["equal" ai (+ ai size) aj (+ aj size)]] [])]
        (recur (rest blocks) (+ ai size) (+ aj size) (into opcodes (concat pre eq)))))))

(defn- make-sm [isjunk a b autojunk]
  (let [a (vec (if (string? a) (map str a) a))
        b (vec (if (string? b) (map str b) b))
        n (count b)
        counts (frequencies b)
        junk (if isjunk (into #{} (filter isjunk b)) #{})
        popular (if (and autojunk (>= n 200))
                  (let [ntest (quot n 100)]
                    (into #{} (comp (filter #(> (val %) ntest)) (map key)) counts))
                  #{})
        bjunk (set/union junk popular)
        b2j (reduce (fn [m [j elt]]
                      (if (bjunk elt) m (update m elt (fnil conj []) j)))
                    {}
                    (map-indexed vector b))
        mb (atom nil)
        op (atom nil)]
    {:a a :b b :b2j b2j :bjunk bjunk :mb mb :op op}))

(defn sequence-matcher [& args]
  (cond
    (keyword? (first args))
    (let [m (apply hash-map args)]
      (make-sm (:isjunk m) (:a m) (:b m) (clojure.core/get m :autojunk true)))
    (= 2 (count args))
    (let [[a b] args] (make-sm nil a b true))
    :else
    (let [[isjunk a b & rest] args
          kwargs (when (keyword? (first rest)) (apply hash-map rest))
          extra-isjunk (:isjunk kwargs)
          autojunk (if kwargs
                     (clojure.core/get kwargs :autojunk true)
                     (if (and rest (not (keyword? (first rest)))) (first rest) true))]
      (make-sm (or isjunk extra-isjunk) a b autojunk))))

(defn sequence-matcher-get-matching-blocks [sm]
  (let [{:keys [a b b2j bjunk mb]} sm]
    (when (nil? @mb) (reset! mb (compute-matching-blocks a b b2j bjunk)))
    @mb))

(defn sequence-matcher-get-opcodes [sm]
  (let [{:keys [a b op]} sm]
    (when (nil? @op)
      (reset! op (blocks->opcodes a b (sequence-matcher-get-matching-blocks sm))))
    @op))

(defn sequence-matcher-ratio [sm]
  (let [{:keys [a b]} sm
        matches (reduce + 0 (map #(nth % 2) (sequence-matcher-get-matching-blocks sm)))
        total (+ (count a) (count b))]
    (if (zero? total) 1 (/ (* 2.0 matches) total))))

(defn sequence-matcher-quick-ratio [sm]
  (let [{:keys [a b]} sm
        la (count a) lb (count b)]
    (if (zero? (+ la lb)) 1
        (let [avail (volatile! (frequencies a))
              matches (reduce (fn [m elt]
                                (let [n (clojure.core/get @avail elt 0)]
                                  (if (pos? n)
                                    (do (vswap! avail update elt dec) (inc m))
                                    m)))
                              0 b)]
          (/ (* 2.0 matches) (+ la lb))))))

(defn sequence-matcher-real-quick-ratio [sm]
  (let [{:keys [a b]} sm
        la (count a) lb (count b)]
    (if (zero? (+ la lb)) 1 (/ (* 2.0 (min la lb)) (+ la lb)))))

(defn sequence-matcher-bpopular [sm] (:bjunk sm))
(defn sequence-matcher-bjunk [sm] (:bjunk sm))

(defn sequence-matcher-find-longest-match
  ([sm]
   (let [{:keys [a b b2j bjunk]} sm]
     (find-longest-match* a b b2j bjunk 0 (count a) 0 (count b))))
  ([sm alo ahi blo bhi]
   (let [{:keys [a b b2j bjunk]} sm]
     (find-longest-match* a b b2j bjunk alo ahi blo bhi))))

(defn sequence-matcher-get-grouped-opcodes
  ([sm] (sequence-matcher-get-grouped-opcodes sm 3))
  ([sm n]
   (let [codes (vec (sequence-matcher-get-opcodes sm))]
     (if (empty? codes)
       []
       (let [codes (cond-> codes
                     (= "equal" (nth (first codes) 0))
                     (assoc 0 (let [[_ i1 i2 j1 j2] (first codes)]
                                ["equal" (max i1 (- i2 n)) i2 (max j1 (- j2 n)) j2]))
                     (= "equal" (nth (last codes) 0))
                     (update (dec (count codes))
                             (fn [[_ i1 i2 j1 j2]]
                               ["equal" i1 (min i2 (+ i1 n)) j1 (min j2 (+ j1 n))])))
             codes (remove (fn [[tag i1 i2]] (and (= tag "equal") (= i1 i2))) codes)]
         (loop [codes (seq codes) group [] groups []]
           (if (not (seq codes))
             (if (and (seq group)
                      (not (and (= 1 (count group)) (= "equal" (nth (first group) 0)))))
               (conj groups group)
               groups)
             (let [[tag i1 i2 j1 j2 :as code] (first codes)
                   rest-codes (rest codes)]
               (if (and (= tag "equal") (> (- i2 i1) (* 2 n)))
                 (let [grp (conj group ["equal" i1 (min i2 (+ i1 n)) j1 (min j2 (+ j1 n))])
                       new-groups (if (and (seq grp)
                                           (not (and (= 1 (count grp)) (= "equal" (nth (first grp) 0)))))
                                    (conj groups grp)
                                    groups)]
                   (recur rest-codes
                          [["equal" (max i1 (- i2 n)) i2 (max j1 (- j2 n)) j2]]
                          new-groups))
                 (recur rest-codes (conj group code) groups))))))))))

;; --- next-value ---

(defn next-value [iter]
  (let [s (seq iter)]
    (if (nil? s)
      (throw (ex-info "StopIteration" {}))
      (first s))))

;; --- unified/context diff ---

(defn- validate-lines [a b]
  (when-not (sequential? a)
    (throw (ex-info "first argument must be a sequence" {})))
  (when-not (sequential? b)
    (throw (ex-info "second argument must be a sequence" {})))
  (doseq [line (concat a b)]
    (when (nil? line) (throw (ex-info "lines must not be nil" {})))
    (when (bytes? line) (throw (ex-info "use diff-bytes for byte arrays" {})))))

(defn unified-diff [a b & args]
  (validate-lines a b)
  (let [[positional kwargs] (split-with (complement keyword?) args)
        [fromfile tofile fromfiledate tofiledate] positional
        opts (apply hash-map kwargs)
        fromfile (or fromfile "")
        tofile (or tofile "")
        fromfiledate (or fromfiledate "")
        tofiledate (or tofiledate "")
        lineterm (clojure.core/get opts :lineterm "\n")
        n (clojure.core/get opts :n 3)
        color (clojure.core/get opts :color false)
        header-date (fn [name date]
                      (if (str/blank? date) name (str name "\t" date)))
        groups (sequence-matcher-get-grouped-opcodes (sequence-matcher nil a b) n)
        a (vec a)
        b (vec b)]
    (lazy-seq
     (when (seq groups)
       (concat
        [(str "--- " (header-date fromfile fromfiledate) lineterm)
         (str "+++ " (header-date tofile tofiledate) lineterm)]
        (for [group groups
              :let [[_ i1 _ j1 _] (first group)
                    [_ _ i2 _ j2] (last group)
                    i1c (inc i1) i2c (- i2 i1)
                    j1c (inc j1) j2c (- j2 j1)]
              line (concat
                    [(str "@@ -" i1c (when (not= 1 i2c) (str "," i2c))
                          " +" j1c (when (not= 1 j2c) (str "," j2c))
                          " @@" lineterm)]
                    (mapcat (fn [[tag i1 i2 j1 j2]]
                              (concat
                               (when (= tag "equal")
                                 (map #(str " " % lineterm) (subvec a i1 i2)))
                               (when (or (= tag "replace") (= tag "delete"))
                                 (map #(str "-" % lineterm) (subvec a i1 i2)))
                               (when (or (= tag "replace") (= tag "insert"))
                                 (map #(str "+" % lineterm) (subvec b j1 j2)))))
                            group))]
          line))))))

(defn context-diff [a b & args]
  (validate-lines a b)
  (let [[positional kwargs] (split-with (complement keyword?) args)
        [fromfile tofile fromfiledate tofiledate] positional
        opts (apply hash-map kwargs)
        fromfile (or fromfile "")
        tofile (or tofile "")
        fromfiledate (or fromfiledate "")
        tofiledate (or tofiledate "")
        lineterm (clojure.core/get opts :lineterm "\n")
        n (clojure.core/get opts :n 3)
        header-date (fn [name date]
                      (if (str/blank? date) name (str name "\t" date)))
        groups (sequence-matcher-get-grouped-opcodes (sequence-matcher nil a b) n)
        a (vec a)
        b (vec b)]
    (lazy-seq
     (when (seq groups)
       (concat
        [(str "*** " (header-date fromfile fromfiledate) lineterm)
         (str "--- " (header-date tofile tofiledate) lineterm)]
        (for [group groups
              :let [[_ i1 _ j1 _] (first group)
                    [_ _ i2 _ j2] (last group)]
              line (concat
                    [(str "***************" lineterm)
                     (str "*** " (inc i1) "," i2 " ****" lineterm)]
                    (when (some #(contains? #{"replace" "delete"} (first %)) group)
                      (mapcat (fn [[tag i1 i2 _ _]]
                                (case tag
                                  "equal"   (map #(str "  " % lineterm) (subvec a i1 i2))
                                  "replace" (map #(str "! " % lineterm) (subvec a i1 i2))
                                  "delete"  (map #(str "- " % lineterm) (subvec a i1 i2))
                                  []))
                              group))
                    [(str "--- " (inc j1) "," j2 " ----" lineterm)]
                    (when (some #(contains? #{"replace" "insert"} (first %)) group)
                      (mapcat (fn [[tag _ _ j1 j2]]
                                (case tag
                                  "equal"   (map #(str "  " % lineterm) (subvec b j1 j2))
                                  "replace" (map #(str "! " % lineterm) (subvec b j1 j2))
                                  "insert"  (map #(str "+ " % lineterm) (subvec b j1 j2))
                                  []))
                              group)))]
          line))))))

;; --- diff-bytes ---

(defn diff-bytes [dfunc a b & args]
  (when-not (and (sequential? a) (every? bytes? a))
    (throw (ex-info "a must be a sequence of byte arrays" {})))
  (when-not (and (sequential? b) (every? bytes? b))
    (throw (ex-info "b must be a sequence of byte arrays" {})))
  (let [[positional kwargs] (split-with (complement keyword?) args)
        opts (apply hash-map kwargs)
        [fa fb fda fdb] positional
        encode (fn [x] (if (bytes? x) x (.getBytes (str x) "ISO-8859-1")))
        decode (fn [^bytes x] (String. x "ISO-8859-1"))
        fa-s (when fa (decode (encode fa)))
        fb-s (when fb (decode (encode fb)))
        fda-s (when fda (if (bytes? fda) (decode fda) (str fda)))
        fdb-s (when fdb (if (bytes? fdb) (decode fdb) (str fdb)))
        lineterm (clojure.core/get opts :lineterm "\n")
        lineterm-s (if (bytes? lineterm) (decode lineterm) (str lineterm))
        a-str (map decode a)
        b-str (map decode b)
        result (apply dfunc a-str b-str
                      (concat
                       (when fa-s [fa-s fb-s])
                       (when fda-s [fda-s fdb-s])
                       [:lineterm lineterm-s]))]
    (map #(.getBytes (str %) "ISO-8859-1") result)))

;; --- get-close-matches ---

(defn get-close-matches
  ([word possibilities] (get-close-matches word possibilities 3 0.6))
  ([word possibilities n] (get-close-matches word possibilities n 0.6))
  ([word possibilities n cutoff]
   (when (< n 1)
     (throw (ex-info (str "n must be >= 1: " n) {})))
   (when-not (<= 0.0 cutoff 1.0)
     (throw (ex-info (str "cutoff must be in [0.0, 1.0]: " cutoff) {})))
   (->> possibilities
        (map (fn [x] [(sequence-matcher-ratio (sequence-matcher nil word x)) x]))
        (filter #(>= (first %) cutoff))
        (sort-by first >)
        (take n)
        (map second)
        vec)))

;; --- restore ---

(defn restore [delta which]
  (when-not (contains? #{1 2} which)
    (throw (ex-info (str "which must be 1 or 2: " which) {})))
  (let [tag (if (= which 1) "- " "+ ")
        other (if (= which 1) "+ " "- ")]
    (->> delta
         (remove #(str/starts-with? % other))
         (map #(if (or (str/starts-with? % "  ") (str/starts-with? % tag))
                 (subs % 2)
                 %)))))

;; --- Differ ---

(defn- differ-hint [a b]
  (let [a-chars (vec (map str a))
        b-chars (vec (map str b))
        sm (sequence-matcher nil a-chars b-chars)
        ops (sequence-matcher-get-opcodes sm)
        a-hint (volatile! (vec (repeat (count a-chars) " ")))
        b-hint (volatile! (vec (repeat (count b-chars) " ")))]
    (doseq [[op i1 i2 j1 j2] ops]
      (case op
        "equal"
        (do (doseq [i (range i1 i2)]
              (when (= "\t" (nth a-chars i))
                (vswap! a-hint assoc i "\t")))
            (doseq [j (range j1 j2)]
              (when (= "\t" (nth b-chars j))
                (vswap! b-hint assoc j "\t"))))
        "replace"
        (do (doseq [i (range i1 i2)] (vswap! a-hint assoc i "^"))
            (doseq [j (range j1 j2)] (vswap! b-hint assoc j "^")))
        "delete"
        (doseq [i (range i1 i2)] (vswap! a-hint assoc i "-"))
        "insert"
        (doseq [j (range j1 j2)] (vswap! b-hint assoc j "+"))))
    (let [ha (str/trimr (apply str @a-hint))
          hb (str/trimr (apply str @b-hint))
          markers? #(boolean (re-find #"[\^+\-]" %))]
      [(when (markers? ha) ha)
       (when (markers? hb) hb)])))

(defn- differ-replace [a alo ahi b blo bhi]
  (cond
    (= alo ahi) (map #(str "+ " (nth b %)) (range blo bhi))
    (= blo bhi) (map #(str "- " (nth a %)) (range alo ahi))
    :else
    (let [best-ratio (atom 0.749)
          best-i (atom nil)
          best-j (atom nil)]
      (doseq [j (range blo bhi)]
        (doseq [i (range alo ahi)]
          (let [r (sequence-matcher-ratio (sequence-matcher nil (nth a i) (nth b j)))]
            (when (> r @best-ratio)
              (reset! best-ratio r)
              (reset! best-i i)
              (reset! best-j j)))))
      (if (nil? @best-i)
        (concat (map #(str "- " (nth a %)) (range alo ahi))
                (map #(str "+ " (nth b %)) (range blo bhi)))
        (let [bi @best-i bj @best-j
              [ha hb] (differ-hint (nth a bi) (nth b bj))]
          (concat
           (differ-replace a alo bi b blo bj)
           [(str "- " (nth a bi))]
           (when ha [(str "? " ha)])
           [(str "+ " (nth b bj))]
           (when hb [(str "? " hb)])
           (differ-replace a (inc bi) ahi b (inc bj) bhi)))))))

(defn differ-compare [a b]
  (let [a-seq (if (string? a) (map str a) a)
        b-seq (if (string? b) (map str b) b)
        sm (sequence-matcher nil a-seq b-seq)
        opcodes (sequence-matcher-get-opcodes sm)
        a (vec a-seq)
        b (vec b-seq)]
    (mapcat (fn [[tag alo ahi blo bhi]]
              (case tag
                "replace" (differ-replace a alo ahi b blo bhi)
                "delete"  (map #(str "- " %) (subvec a alo ahi))
                "insert"  (map #(str "+ " %) (subvec b blo bhi))
                "equal"   (map #(str "  " %) (subvec a alo ahi))))
            opcodes)))

;; --- HtmlDiff stubs ---

(defn html-diff [] {:type :html-diff})

(defn html-diff-make-file
  [_ fromlines tolines & {:keys [fromdesc todesc context numlines charset]
                           :or {fromdesc "" todesc "" context true numlines 5 charset "utf-8"}}]
  (str "<html>\n<head>\n<meta charset=\"" charset "\">\n</head>\n<body>\n</body>\n</html>"))

(defn html-diff-make-table
  [_ fromlines tolines & {:keys [fromdesc todesc context numlines]
                           :or {fromdesc "" todesc "" context true numlines 5}}]
  "<table></table>")
