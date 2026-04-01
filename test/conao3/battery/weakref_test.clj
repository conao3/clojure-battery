;; Original: Lib/test/test_weakref.py

(ns conao3.battery.weakref-test
  (:require
   [clojure.test :as t]
   [conao3.battery.weakref :as weakref]))

(t/deftest test-basic-ref
  (let [obj (Object.)
        r (weakref/ref obj)]
    (t/is (some? (weakref/deref-ref r)))
    (t/is (identical? obj (weakref/deref-ref r)))
    (t/is (weakref/alive? r))))

(t/deftest test-ref-with-callback
  (let [obj (Object.)
        called (atom false)
        r (weakref/ref obj (fn [] (reset! called true)))]
    (t/is (some? (weakref/deref-ref r)))))

(t/deftest test-weak-dict
  (let [d (weakref/make-weak-dict)
        k (Object.)]
    (.put d k "value")
    (t/is (= "value" (.get d k)))
    (t/is (= 1 (.size d)))))

(t/deftest test-weak-value-dict
  (let [d (weakref/make-weak-value-dict)
        obj (Object.)]
    (weakref/weak-value-dict-put d "key" obj)
    (t/is (identical? obj (weakref/weak-value-dict-get d "key")))
    (t/is (true? (weakref/weak-value-dict-contains? d "key")))
    (t/is (nil? (weakref/weak-value-dict-get d "missing")))
    (t/is (= :default (weakref/weak-value-dict-get d "missing" :default)))))

(t/deftest test-finalize
  (let [called (atom false)
        obj (Object.)
        fin (weakref/finalize obj #(reset! called true))]
    (t/is (weakref/finalize-alive? fin))
    (weakref/finalize-call fin)
    (t/is (true? @called))
    (t/is (not (weakref/finalize-alive? fin)))))

(t/deftest test-finalize-idempotent
  (let [call-count (atom 0)
        obj (Object.)
        fin (weakref/finalize obj #(swap! call-count inc))]
    (weakref/finalize-call fin)
    (weakref/finalize-call fin)
    (t/is (= 1 @call-count))))

(t/deftest test-ref-returns-weakreference
  (let [obj (Object.)
        r (weakref/ref obj)]
    (t/is (instance? java.lang.ref.WeakReference r))))

(t/deftest test-deref-ref-returns-obj
  (let [obj (java.util.ArrayList.)
        r (weakref/ref obj)]
    (t/is (= obj (weakref/deref-ref r)))))

(t/deftest test-weak-dict-remove
  (let [d (weakref/make-weak-dict)
        k (Object.)]
    (.put d k "value")
    (t/is (= 1 (.size d)))
    (.remove d k)
    (t/is (= 0 (.size d)))))

(t/deftest test-weak-value-dict-multiple
  (let [d (weakref/make-weak-value-dict)
        a (Object.)
        b (Object.)]
    (weakref/weak-value-dict-put d "a" a)
    (weakref/weak-value-dict-put d "b" b)
    (t/is (identical? a (weakref/weak-value-dict-get d "a")))
    (t/is (identical? b (weakref/weak-value-dict-get d "b")))
    (t/is (true? (weakref/weak-value-dict-contains? d "a")))
    (t/is (false? (boolean (weakref/weak-value-dict-contains? d "c"))))))

(t/deftest test-alive-returns-boolean
  (let [obj (Object.)
        r (weakref/ref obj)]
    (t/is (true? (weakref/alive? r)))))

(t/deftest test-finalize-returns-map
  (let [obj (Object.)
        fin (weakref/finalize obj (fn []))]
    (t/is (map? fin))
    (t/is (contains? fin :alive))
    (t/is (contains? fin :func))
    (t/is (contains? fin :obj))))

(t/deftest test-weak-value-dict-overwrite
  (let [d (weakref/make-weak-value-dict)
        a (Object.)
        b (Object.)]
    (weakref/weak-value-dict-put d "key" a)
    (weakref/weak-value-dict-put d "key" b)
    (t/is (identical? b (weakref/weak-value-dict-get d "key")))))

(t/deftest test-make-weak-dict-is-map
  (let [d (weakref/make-weak-dict)]
    (t/is (instance? java.util.Map d))))

(t/deftest test-finalize-alive
  (let [obj (Object.)
        fin (weakref/finalize obj (fn []))]
    (t/is (true? (weakref/finalize-alive? fin)))))

(t/deftest test-finalize-call-runs-func
  (let [called (atom false)
        obj (Object.)
        fin (weakref/finalize obj (fn [] (reset! called true)))]
    (weakref/finalize-call fin)
    (t/is (true? @called))
    (t/is (false? (weakref/finalize-alive? fin)))))

(t/deftest test-finalize-call-idempotent
  (let [call-count (atom 0)
        obj (Object.)
        fin (weakref/finalize obj (fn [] (swap! call-count inc)))]
    (weakref/finalize-call fin)
    (weakref/finalize-call fin)
    (t/is (= 1 @call-count))))

(t/deftest test-ref-alive-check
  ;; a live reference returns true for alive?
  (let [obj (atom {:value 42})
        r (weakref/ref @obj)]
    (t/is (boolean? (weakref/alive? r)))))

(t/deftest test-weak-value-dict-empty
  ;; empty weak-value-dict has no contents
  (let [d (weakref/make-weak-value-dict)]
    (t/is (nil? (weakref/weak-value-dict-get d "any")))
    (t/is (not (weakref/weak-value-dict-contains? d "any")))))

(t/deftest test-weak-dict-multiple-entries
  (let [d (weakref/make-weak-dict)
        k1 (Object.)
        k2 (Object.)]
    (.put d k1 "v1")
    (.put d k2 "v2")
    (t/is (= "v1" (.get d k1)))
    (t/is (= "v2" (.get d k2)))
    (t/is (= 2 (.size d)))))

(t/deftest test-finalize-with-nil-func
  ;; finalize with a no-op function works
  (let [obj (Object.)
        fin (weakref/finalize obj (fn []))]
    (t/is (map? fin))
    (t/is (true? (weakref/finalize-alive? fin)))))
