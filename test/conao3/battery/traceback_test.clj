;; Original: Lib/test/test_traceback.py

(ns conao3.battery.traceback-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.traceback :as traceback]))

(t/deftest test-format-exc
  (let [e (RuntimeException. "test error")
        s (traceback/format-exc e)]
    (t/is (string? s))
    (t/is (str/includes? s "test error"))))

(t/deftest test-format-tb
  (let [e (RuntimeException. "test")
        tb (traceback/format-tb e)]
    (t/is (vector? tb))
    (t/is (every? string? tb))))

(t/deftest test-extract-tb
  (let [e (RuntimeException. "test")
        tb (traceback/extract-tb e)]
    (t/is (vector? tb))
    (t/is (every? map? tb))
    (t/is (every? #(contains? % :filename) tb))
    (t/is (every? #(contains? % :lineno) tb))
    (t/is (every? #(contains? % :name) tb))))

(t/deftest test-extract-stack
  (let [stack (traceback/extract-stack)]
    (t/is (vector? stack))
    (t/is (pos? (count stack)))
    (t/is (every? map? stack))))

(t/deftest test-format-list
  (let [entries [{:filename "foo.clj" :lineno 42 :name "my-fn"}
                 {:filename "bar.clj" :lineno 7 :name "other-fn"}]
        result (traceback/format-list entries)]
    (t/is (= 2 (count result)))
    (t/is (str/includes? (first result) "foo.clj"))
    (t/is (str/includes? (first result) "42"))
    (t/is (str/includes? (second result) "bar.clj"))))

(t/deftest test-walk-tb
  (let [e (RuntimeException. "test")
        frames (traceback/walk-tb e)]
    (t/is (vector? frames))
    (t/is (every? vector? frames))
    (t/is (every? #(= 2 (count %)) frames))))

(t/deftest test-clear-frames
  (t/is (nil? (traceback/clear-frames nil))))

(t/deftest test-format-exc-class-name
  (let [e (IllegalArgumentException. "bad arg")
        s (traceback/format-exc e)]
    (t/is (str/includes? s "IllegalArgumentException"))
    (t/is (str/includes? s "bad arg"))))

(t/deftest test-extract-tb-not-empty
  (let [e (try (throw (RuntimeException. "test")) (catch RuntimeException ex ex))
        tb (traceback/extract-tb e)]
    (t/is (pos? (count tb)))
    (t/is (string? (:filename (first tb))))
    (t/is (pos? (:lineno (first tb))))))

(t/deftest test-format-exception
  (let [e (RuntimeException. "format test")
        s (traceback/format-exception e)]
    (t/is (string? s))
    (t/is (str/includes? s "format test"))))

(t/deftest test-print-exc-no-throw
  (t/is (nil? (traceback/print-exc (RuntimeException. "print test")))))
