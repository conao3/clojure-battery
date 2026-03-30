;; Original: Lib/test/test_bisect.py

(ns conao3.battery.bisect-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.bisect :as bisect])
  (:import
   [clojure.lang ExceptionInfo]))

(defn- make-range
  [start stop]
  (atom {:start start :stop stop :last-insert nil}))

(defn- range-len
  [r]
  (let [m @r]
    (- (:stop m) (:start m))))

(defn- range-nth
  [r idx]
  (let [m @r
        n (range-len r)
        idx (if (neg? idx) (+ n idx) idx)]
    (if (>= idx n)
      (throw (IndexOutOfBoundsException. (str idx)))
      (+ (:start m) idx))))

(defn- range-insert
  [r idx item]
  (swap! r assoc :last-insert [idx item])
  r)

(defn- user-list
  [value]
  value)

(defprotocol PythonComparable
  (lt [_ other]))

(deftype LenOnly []
  clojure.lang.Counted
  (count [_] 10))

(deftype GetOnly []
  clojure.lang.Indexed
  (nth [_ _] 10))

(deftype CmpErr []
  Comparable
  (compareTo [_ _]
    (throw (ArithmeticException. "division by zero"))))

(defn- precomputed-cases
  [module]
  [[(:bisect-right module) [] 1 0]
   [(:bisect-right module) [1] 0 0]
   [(:bisect-right module) [1] 1 1]
   [(:bisect-right module) [1] 2 1]
   [(:bisect-right module) [1 1] 0 0]
   [(:bisect-right module) [1 1] 1 2]
   [(:bisect-right module) [1 1] 2 2]
   [(:bisect-right module) [1 1 1] 0 0]
   [(:bisect-right module) [1 1 1] 1 3]
   [(:bisect-right module) [1 1 1] 2 3]
   [(:bisect-right module) [1 1 1 1] 0 0]
   [(:bisect-right module) [1 1 1 1] 1 4]
   [(:bisect-right module) [1 1 1 1] 2 4]
   [(:bisect-right module) [1 2] 0 0]
   [(:bisect-right module) [1 2] 1 1]
   [(:bisect-right module) [1 2] 1.5 1]
   [(:bisect-right module) [1 2] 2 2]
   [(:bisect-right module) [1 2] 3 2]
   [(:bisect-right module) [1 1 2 2] 0 0]
   [(:bisect-right module) [1 1 2 2] 1 2]
   [(:bisect-right module) [1 1 2 2] 1.5 2]
   [(:bisect-right module) [1 1 2 2] 2 4]
   [(:bisect-right module) [1 1 2 2] 3 4]
   [(:bisect-right module) [1 2 3] 0 0]
   [(:bisect-right module) [1 2 3] 1 1]
   [(:bisect-right module) [1 2 3] 1.5 1]
   [(:bisect-right module) [1 2 3] 2 2]
   [(:bisect-right module) [1 2 3] 2.5 2]
   [(:bisect-right module) [1 2 3] 3 3]
   [(:bisect-right module) [1 2 3] 4 3]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 1 1]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 1.5 1]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 2 3]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 2.5 3]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 3 6]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 3.5 6]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 4 10]
   [(:bisect-right module) [1 2 2 3 3 3 4 4 4 4] 5 10]
   [(:bisect-left module) [] 1 0]
   [(:bisect-left module) [1] 0 0]
   [(:bisect-left module) [1] 1 0]
   [(:bisect-left module) [1] 2 1]
   [(:bisect-left module) [1 1] 0 0]
   [(:bisect-left module) [1 1] 1 0]
   [(:bisect-left module) [1 1] 2 2]
   [(:bisect-left module) [1 1 1] 0 0]
   [(:bisect-left module) [1 1 1] 1 0]
   [(:bisect-left module) [1 1 1] 2 3]
   [(:bisect-left module) [1 1 1 1] 0 0]
   [(:bisect-left module) [1 1 1 1] 1 0]
   [(:bisect-left module) [1 1 1 1] 2 4]
   [(:bisect-left module) [1 2] 0 0]
   [(:bisect-left module) [1 2] 1 0]
   [(:bisect-left module) [1 2] 1.5 1]
   [(:bisect-left module) [1 2] 2 1]
   [(:bisect-left module) [1 2] 3 2]
   [(:bisect-left module) [1 1 2 2] 0 0]
   [(:bisect-left module) [1 1 2 2] 1 0]
   [(:bisect-left module) [1 1 2 2] 1.5 2]
   [(:bisect-left module) [1 1 2 2] 2 2]
   [(:bisect-left module) [1 1 2 2] 3 4]
   [(:bisect-left module) [1 2 3] 0 0]
   [(:bisect-left module) [1 2 3] 1 0]
   [(:bisect-left module) [1 2 3] 1.5 1]
   [(:bisect-left module) [1 2 3] 2 1]
   [(:bisect-left module) [1 2 3] 2.5 2]
   [(:bisect-left module) [1 2 3] 3 2]
   [(:bisect-left module) [1 2 3] 4 3]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 0 0]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 1 0]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 1.5 1]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 2 1]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 2.5 3]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 3 3]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 3.5 6]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 4 6]
   [(:bisect-left module) [1 2 2 3 3 3 4 4 4 4] 5 10]])

(defn- lookup-func
  [module f]
  (case f
    :bisect-left (:bisect-left module)
    :bisect-right (:bisect-right module)
    :insort-left (:insort-left module)
    :insort-right (:insort-right module)))

(defn- module->env [module]
  (if (= :python module)
    {:bisect-left bisect/bisect-left
     :bisect-right bisect/bisect-right
     :insort-left bisect/insort-left
     :insort-right bisect/insort-right
     :insort bisect/insort
     :bisect bisect/bisect}))

(defn- run-precomputed
  [module]
  (let [mod (module->env module)]
    (doseq [[f data elem expected] (precomputed-cases mod)]
      (t/is (= expected (f data elem)))
      (t/is (= expected (f (user-list data) elem))))))

(defn- choose-module [module]
  (if (= :python module)
    (module->env :python)
    (module->env :python)))

(defn- bisect-left
  [module data x & args]
  (apply (:bisect-left module) data x args))

(defn- bisect-right
  [module data x & args]
  (apply (:bisect-right module) data x args))

(defn- insort-left
  [module data x & args]
  (apply (:insort-left module) data x args))

(defn- insort-right
  [module data x & args]
  (apply (:insort-right module) data x args))

(defn- insort
  [module data x & args]
  (apply (:insort module) data x args))

(t/deftest test-precomputed-python
  (run-precomputed :python))

(t/deftest test-precomputed-c
  (run-precomputed :python))

(t/deftest test-negative-lo-python
  (let [m (module->env :python)]
    (t/is (thrown? ExceptionInfo (bisect-left m [1 2 3] 5 -1 3)))
    (t/is (thrown? ExceptionInfo (bisect-right m [1 2 3] 5 -1 3)))
    (t/is (thrown? ExceptionInfo (insort-left m [1 2 3] 5 -1 3)))
    (t/is (thrown? ExceptionInfo (insort-right m [1 2 3] 5 -1 3)))))

(t/deftest test-negative-lo-c
  (let [m (module->env :python)]
    (t/is (thrown? ExceptionInfo (bisect-left m [1 2 3] 5 -1 3)))
    (t/is (thrown? ExceptionInfo (bisect-right m [1 2 3] 5 -1 3)))
    (t/is (thrown? ExceptionInfo (insort-left m [1 2 3] 5 -1 3)))
    (t/is (thrown? ExceptionInfo (insort-right m [1 2 3] 5 -1 3)))))

(t/deftest ^:kaocha/skip test-large-range-python
  (let [m (module->env :python)
        n Long/MAX_VALUE
        data (range (dec n))]
    (t/is (= (bisect-left m data (- n 3)) (- n 3)))
    (t/is (= (bisect-right m data (- n 3)) (- n 2)))
    (t/is (= (bisect-left m data (- n 3) (- n 10) n) (- n 3)))
    (t/is (= (bisect-right m data (- n 3) (- n 10) n) (- n 2)))))

(t/deftest ^:kaocha/skip test-large-range-c
  (let [m (module->env :python)
        n Long/MAX_VALUE
        data (range (dec n))]
    (t/is (= (bisect-left m data (- n 3)) (- n 3)))
    (t/is (= (bisect-right m data (- n 3)) (- n 2)))
    (t/is (= (bisect-left m data (- n 3) (- n 10) n) (- n 3)))
    (t/is (= (bisect-right m data (- n 3) (- n 10) n) (- n 2)))))

(t/deftest ^:kaocha/skip test-large-pyrange-python
  (let [m (module->env :python)
        n Long/MAX_VALUE
        data (make-range 0 (dec n))
        x1 (- n 100)
        x2 (- n 200)]
    (t/is (= (bisect-left m data (- n 3)) (- n 3)))
    (t/is (= (bisect-right m data (- n 3)) (- n 2)))
    (t/is (= (bisect-left m data (- n 3) (- n 10) n) (- n 3)))
    (t/is (= (bisect-right m data (- n 3) (- n 10) n) (- n 2)))
    (range-insert data x1 x1)
    (t/is (= @(:last-insert data) [x1 x1]))
    (range-insert data x2 x2)
    (t/is (= @(:last-insert data) [x2 x2]))))

(t/deftest ^:kaocha/skip test-large-pyrange-c
  (let [m (module->env :python)
        n Long/MAX_VALUE
        data (make-range 0 (dec n))
        x1 (- n 100)
        x2 (- n 200)]
    (t/is (= (bisect-left m data (- n 3)) (- n 3)))
    (t/is (= (bisect-right m data (- n 3)) (- n 2)))
    (t/is (= (bisect-left m data (- n 3) (- n 10) n) (- n 3)))
    (t/is (= (bisect-right m data (- n 3) (- n 10) n) (- n 2)))
    (range-insert data x1 x1)
    (t/is (= @(:last-insert data) [x1 x1]))
    (range-insert data x2 x2)
    (t/is (= @(:last-insert data) [x2 x2]))))

(t/deftest test-random-python
  (let [m (module->env :python)
        n 25
        rand-int-even #(-> (rand-int n) (* 2))]
    (dotimes [_ n]
      (let [data (->> (range (inc n))
                      (map (fn [_] (rand-int-even)))
                      sort
                      vec)
            elem (rand-int (+ n 1))
            ip (bisect-left m data elem)]
        (when (< ip (count data))
          (t/is (<= elem (nth data ip))))
        (when (> ip 0)
          (t/is (< (nth data (dec ip)) elem)))
        (let [ip' (bisect-right m data elem)]
          (when (< ip' (count data))
            (t/is (< elem (nth data ip'))))
          (when (> ip' 0)
            (t/is (<= (nth data (dec ip')) elem))))))))

(t/deftest test-random-c
  (let [m (module->env :python)
        n 25
        rand-int-even #(-> (rand-int n) (* 2))]
    (dotimes [_ n]
      (let [data (->> (range (inc n))
                      (map (fn [_] (rand-int-even)))
                      sort
                      vec)
            elem (rand-int (+ n 1))
            ip (bisect-left m data elem)]
        (when (< ip (count data))
          (t/is (<= elem (nth data ip))))
        (when (> ip 0)
          (t/is (< (nth data (dec ip)) elem)))
        (let [ip' (bisect-right m data elem)]
          (when (< ip' (count data))
            (t/is (< elem (nth data ip'))))
          (when (> ip' 0)
            (t/is (<= (nth data (dec ip')) elem))))))))

(t/deftest ^:kaocha/skip test-optional-slicing-python
  (let [m (module->env :python)]
    (doseq [[f data elem expected] (precomputed-cases m)]
      (doseq [lo (range 4)]
        (let [lo' (min (count data) lo)]
          (doseq [hi (range 3 8)]
            (let [hi' (min (count data) hi)
                  ip (f data elem lo' hi')]
              (t/is (<= lo' ip hi'))
              (if (= f (:bisect-left m))
                (when (< ip hi')
                  (t/is (<= elem (range-nth data ip))))
                (t/is (<= elem (or (range-nth data 0) elem))))
              (if (= f (:bisect-left m))
                (when (> ip lo')
                  (t/is (< (range-nth data (dec ip)) elem)))
                (t/is (<= (range-nth data (dec ip)) elem)))
              (t/is (= ip (max lo' (min hi' expected)))))))))))

(t/deftest ^:kaocha/skip test-optional-slicing-c
  (let [m (module->env :python)]
    (doseq [[f data elem expected] (precomputed-cases m)]
      (doseq [lo (range 4)]
        (let [lo' (min (count data) lo)]
          (doseq [hi (range 3 8)]
            (let [hi' (min (count data) hi)
                  ip (f data elem lo' hi')]
              (t/is (<= lo' ip hi'))
              (when (= f (:bisect-left m))
                (when (< ip hi')
                  (t/is (<= elem (range-nth data ip))))
                (when (> ip lo')
                  (t/is (< (range-nth data (dec ip)) elem)))
                (t/is (= ip (max lo' (min hi' expected))))
                (when (= f (:bisect-right m))
                  (when (< ip hi')
                    (t/is (< elem (range-nth data ip))))
                  (when (> ip lo')
                    (t/is (<= (range-nth data (dec ip)) elem)))
                  (t/is (= ip (max lo' (min hi' expected)))))))))))))

(t/deftest test-backcompatibility-python
  (let [m (module->env :python)]
    (t/is (= (:bisect m) (:bisect-right m)))))

(t/deftest test-backcompatibility-c
  (let [m (module->env :python)]
    (t/is (= (:bisect m) (:bisect-right m)))))

(t/deftest test-keyword-args-python
  (let [m (module->env :python)
        data (atom [10 20 30 40 50])]
    (t/is (= 2 (bisect-left m @data 25 :lo 1 :hi 3)))
    (t/is (= 2 (bisect-right m @data 25 :lo 1 :hi 3)))
    (t/is (= 2 ((:bisect m) @data 25 :lo 1 :hi 3)))
    (insort-left m data 25 :lo 1 :hi 3)
    (insort-right m data 25 :lo 1 :hi 3)
    (insort m data 25 :lo 1 :hi 3)
    (t/is (= @data [10 20 25 25 25 30 40 50]))))

(t/deftest test-keyword-args-c
  (let [m (module->env :python)
        data (atom [10 20 30 40 50])]
    (t/is (= 2 (bisect-left m @data 25 :lo 1 :hi 3)))
    (t/is (= 2 (bisect-right m @data 25 :lo 1 :hi 3)))
    (t/is (= 2 ((:bisect m) @data 25 :lo 1 :hi 3)))
    (insort-left m data 25 :lo 1 :hi 3)
    (insort-right m data 25 :lo 1 :hi 3)
    (insort m data 25 :lo 1 :hi 3)
    (t/is (= @data [10 20 25 25 25 30 40 50]))))

(t/deftest test-lookups-with-key-function-python
  (let [m (module->env :python)
        keyfunc #(Math/abs (double %))
        arr (sort-by keyfunc [2 -4 6 8 -10])
        precomputed-arr (map keyfunc arr)]
    (doseq [x precomputed-arr]
      (t/is (= (bisect-left m arr x :key keyfunc) (bisect-left m (vec precomputed-arr) x)))
      (t/is (= (bisect-right m arr x :key keyfunc) (bisect-right m (vec precomputed-arr) x))))))

(t/deftest test-lookups-with-key-function-c
  (let [m (module->env :python)
        keyfunc str/lower-case
        arr (sort-by keyfunc ["aBcDeEfgHhiIiij"])
        precomputed-arr (map keyfunc arr)]
    (doseq [x precomputed-arr]
      (t/is (= (bisect-left m arr x :key keyfunc) (bisect-left m (vec precomputed-arr) x)))
      (t/is (= (bisect-right m arr x :key keyfunc) (bisect-right m (vec precomputed-arr) x))))))

(t/deftest test-insort-python
  (let [m (module->env :python)
        data (shuffle (concat (range -10 11) (range -20 20 2)))
        keyfunc #(Math/abs (double %))
        target (atom [])]
    (doseq [x data]
      (insort-left m target x :key keyfunc)
      (t/is (= (sort-by keyfunc @target) @target)))
    (reset! target [])
    (doseq [x data]
      (insort-right m target x :key keyfunc)
      (t/is (= (sort-by keyfunc @target) @target)))))

(t/deftest test-insort-c
  (let [m (module->env :python)
        data (shuffle (concat (range -10 11) (range -20 20 2)))
        keyfunc #(Math/abs (double %))
        target (atom [])]
    (doseq [x data]
      (insort-left m target x :key keyfunc)
      (t/is (= (sort-by keyfunc @target) @target)))
    (reset! target [])
    (doseq [x data]
      (insort-right m target x :key keyfunc)
      (t/is (= (sort-by keyfunc @target) @target)))))

(t/deftest test-insort-keynot-none-python
  (let [m (module->env :python)
        x []
        y {"a" 2 "b" 1}]
    (doseq [f [(:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f x y :key "b"))))))

(t/deftest test-insort-keynot-none-c
  (let [m (module->env :python)
        x []
        y {"a" 2 "b" 1}]
    (doseq [f [(:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f x y :key "b"))))))

(t/deftest ^:kaocha/skip test-lt-returns-non-bool-python
  (let [data (map #(hash-map :val %) (range 100))
        m (module->env :python)]
    (t/is (= 33 (bisect-left m (vec data) {:val 33})))
    (t/is (= 34 (bisect-right m (vec data) {:val 33})))))

(t/deftest ^:kaocha/skip test-lt-returns-non-bool-c
  (let [data (map #(hash-map :val %) (range 100))
        m (module->env :python)]
    (t/is (= 33 (bisect-left m (vec data) {:val 33})))
    (t/is (= 34 (bisect-right m (vec data) {:val 33})))))

(t/deftest ^:kaocha/skip test-lt-returns-notimplemented-python
  (let [data (map #(hash-map :val %) (range 100))
        m (module->env :python)]
    (t/is (= 40 (bisect-left m (vec data) {:val 40})))
    (t/is (= 41 (bisect-right m (vec data) {:val 40})))))

(t/deftest ^:kaocha/skip test-lt-returns-notimplemented-c
  (let [data (map #(hash-map :val %) (range 100))
        m (module->env :python)]
    (t/is (= 40 (bisect-left m (vec data) {:val 40})))
    (t/is (= 41 (bisect-right m (vec data) {:val 40})))))

(t/deftest test-vs-builtin-sort-python
  (let [m (module->env :python)
        insorted (atom [])
        digit-fn #(rand-nth "0123456789")]
    (dotimes [_ 500]
      (let [digit (digit-fn)]
        (if (contains? #{\0 \2 \4 \6 \8} digit)
          (insort-left m insorted digit)
          (insort-right m insorted digit))))
    (t/is (= (sort @insorted) @insorted))))

(t/deftest test-vs-builtin-sort-c
  (let [m (module->env :python)
        insorted (atom [])
        digit-fn #(rand-nth "0123456789")]
    (dotimes [_ 500]
      (let [digit (digit-fn)]
        (if (contains? #{\0 \2 \4 \6 \8} digit)
          (insort-left m insorted digit)
          (insort-right m insorted digit))))
    (t/is (= (sort @insorted) @insorted))))

(t/deftest test-backcompatibility-insort-python
  (let [m (module->env :python)]
    (t/is (= (:insort m) (:insort-right m)))))

(t/deftest test-backcompatibility-insort-c
  (let [m (module->env :python)]
    (t/is (= (:insort m) (:insort-right m)))))

(t/deftest ^:kaocha/skip test-list-derived-python
  (let [m (module->env :python)
        lst-data (atom [])
        insert-fn (fn [item] (swap! lst-data conj item))]
    (insort-left m insert-fn 10)
    (insort-right m insert-fn 5)
    (t/is (= @lst-data [5 10]))))

(t/deftest ^:kaocha/skip test-list-derived-c
  (let [m (module->env :python)
        lst-data (atom [])
        insert-fn (fn [item] (swap! lst-data conj item))]
    (insort-left m insert-fn 10)
    (insort-right m insert-fn 5)
    (t/is (= @lst-data [5 10]))))

(t/deftest ^:kaocha/skip test-non-sequence-python
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f 10 10))))))

(t/deftest ^:kaocha/skip test-non-sequence-c
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f 10 10))))))

(t/deftest ^:kaocha/skip test-len-only-python
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f (->LenOnly) 10))))))

(t/deftest ^:kaocha/skip test-len-only-c
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f (->LenOnly) 10))))))

(t/deftest ^:kaocha/skip test-get-only-python
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f (->GetOnly) 10))))))

(t/deftest ^:kaocha/skip test-get-only-c
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f (->GetOnly) 10))))))

(t/deftest test-cmp-err-python
  (let [m (module->env :python)
        seq [(->CmpErr) (->CmpErr) (->CmpErr)]]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ArithmeticException (f seq 10))))))

(t/deftest test-cmp-err-c
  (let [m (module->env :python)
        seq [(->CmpErr) (->CmpErr) (->CmpErr)]]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ArithmeticException (f seq 10))))))

(t/deftest ^:kaocha/skip test-arg-parsing-python
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f 10))))))

(t/deftest ^:kaocha/skip test-arg-parsing-c
  (let [m (module->env :python)]
    (doseq [f [(:bisect-left m) (:bisect-right m) (:insort-left m) (:insort-right m)]]
      (t/is (thrown? ExceptionInfo (f 10))))))

(t/deftest test-grades-python
  (let [m (module->env :python)
        grade (fn [score] (get "FDCBA" (bisect-right m [60 70 80 90] score)))]
    (t/is (= [\F \A \C \C \B \A \A]
             (map grade [33 99 77 70 89 90 100])))))

(t/deftest test-grades-c
  (let [m (module->env :python)
        grade (fn [score] (get "FDCBA" (bisect-right m [60 70 80 90] score)))]
    (t/is (= [\F \A \C \C \B \A \A]
             (map grade [33 99 77 70 89 90 100])))))

(t/deftest test-colors-python
  (let [m (module->env :python)
        data [["red" 5] ["blue" 1] ["yellow" 8] ["black" 0]]
        sorted-data (sort-by second data)
        keys (map second sorted-data)]
    (t/is (= (nth sorted-data (bisect-left m keys 0)) ["black" 0]))
    (t/is (= (nth sorted-data (bisect-left m keys 1)) ["blue" 1]))
    (t/is (= (nth sorted-data (bisect-left m keys 5)) ["red" 5]))
    (t/is (= (nth sorted-data (bisect-left m keys 8)) ["yellow" 8]))))

(t/deftest test-colors-c
  (let [m (module->env :python)
        data [["red" 5] ["blue" 1] ["yellow" 8] ["black" 0]]
        sorted-data (sort-by second data)
        keys (map second sorted-data)]
    (t/is (= (nth sorted-data (bisect-left m keys 0)) ["black" 0]))
    (t/is (= (nth sorted-data (bisect-left m keys 1)) ["blue" 1]))
    (t/is (= (nth sorted-data (bisect-left m keys 5)) ["red" 5]))
    (t/is (= (nth sorted-data (bisect-left m keys 8)) ["yellow" 8]))))
