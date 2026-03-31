;; Original: Lib/test/test_inspect.py

(ns conao3.battery.inspect-test
  (:require
   [clojure.test :as t]
   [conao3.battery.inspect :as inspect-m])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-isfunction
  (t/is (true? (inspect-m/isfunction? (fn [x] x))))
  (t/is (false? (inspect-m/isfunction? 42)))
  (t/is (false? (inspect-m/isfunction? "string")))
  (t/is (false? (inspect-m/isfunction? nil))))

(t/deftest test-isclass
  (t/is (true? (inspect-m/isclass? String)))
  (t/is (true? (inspect-m/isclass? Long)))
  (t/is (false? (inspect-m/isclass? "string")))
  (t/is (false? (inspect-m/isclass? 42))))

(t/deftest test-ismethod
  (let [m (.getMethod String "length" (into-array Class []))]
    (t/is (true? (inspect-m/ismethod? m))))
  (t/is (false? (inspect-m/ismethod? (fn [] nil)))))

(t/deftest test-getdoc
  (let [f (with-meta (fn [x] x) {:doc "my function doc"})]
    (t/is (= "my function doc" (inspect-m/getdoc f)))))

(t/deftest test-getdoc-nil
  (t/is (nil? (inspect-m/getdoc (fn [x] x)))))

(t/deftest test-stack
  (let [s (inspect-m/stack)]
    (t/is (vector? s))
    (t/is (pos? (count s)))
    (t/is (every? map? s))
    (t/is (every? #(contains? % :filename) s))
    (t/is (every? #(contains? % :lineno) s))))

(t/deftest test-currentframe
  (let [f (inspect-m/currentframe)]
    (t/is (map? f))
    (t/is (contains? f :filename))
    (t/is (contains? f :lineno))))

(t/deftest test-isframe
  (t/is (true? (inspect-m/isframe? {:filename "foo.clj" :lineno 42 :function "bar"})))
  (t/is (false? (inspect-m/isframe? {:foo 1})))
  (t/is (false? (inspect-m/isframe? "not a frame"))))

(t/deftest test-iscoroutinefunction
  (t/is (false? (inspect-m/iscoroutinefunction? (fn [] nil)))))

(t/deftest test-isgeneratorfunction
  (t/is (false? (inspect-m/isgeneratorfunction? (fn [] nil)))))

(t/deftest test-getsource-throws
  (t/is (thrown? ExceptionInfo (inspect-m/getsource (fn [] nil)))))

(t/deftest test-isbuiltin
  (t/is (true? (inspect-m/isbuiltin? (fn [x] x))))
  (t/is (false? (inspect-m/isbuiltin? 42))))

(t/deftest test-ismodule
  (t/is (true? (inspect-m/ismodule? (find-ns 'clojure.core))))
  (t/is (false? (inspect-m/ismodule? "string")))
  (t/is (false? (inspect-m/ismodule? 42))))

(t/deftest test-getmodule
  (let [v #'clojure.core/map]
    (t/is (instance? clojure.lang.Namespace (inspect-m/getmodule v)))))

(t/deftest test-stack-has-function
  (let [s (inspect-m/stack)]
    (t/is (every? #(contains? % :function) s))
    (t/is (every? #(string? (:function %)) s))))

(t/deftest test-getdoc-var
  (t/is (string? (inspect-m/getdoc #'clojure.core/map))))
