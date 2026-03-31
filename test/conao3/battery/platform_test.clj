;; Original: Lib/test/test_platform.py

(ns conao3.battery.platform-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.platform :as platform-m]))

(t/deftest test-system
  (let [s (platform-m/system)]
    (t/is (string? s))
    (t/is (pos? (count s)))))

(t/deftest test-release
  (let [r (platform-m/release)]
    (t/is (string? r))))

(t/deftest test-machine
  (let [m (platform-m/machine)]
    (t/is (string? m))
    (t/is (pos? (count m)))))

(t/deftest test-processor
  (let [p (platform-m/processor)]
    (t/is (string? p))))

(t/deftest test-node
  (let [n (platform-m/node)]
    (t/is (string? n))))

(t/deftest test-uname
  (let [u (platform-m/uname)]
    (t/is (map? u))
    (t/is (contains? u :system))
    (t/is (contains? u :node))
    (t/is (contains? u :release))
    (t/is (contains? u :machine))
    (t/is (contains? u :processor))
    (t/is (string? (:system u)))))

(t/deftest test-architecture
  (let [[bits linkage] (platform-m/architecture)]
    (t/is (string? bits))
    (t/is (string? linkage))
    (t/is (or (= bits "32bit") (= bits "64bit")))))

(t/deftest test-platform-string
  (let [p (platform-m/platform)]
    (t/is (string? p))
    (t/is (pos? (count p)))))

(t/deftest test-java-ver
  (let [v (platform-m/java-ver)]
    (t/is (string? v))
    (t/is (pos? (count v)))))

(t/deftest test-python-version
  (let [v (platform-m/python-version)]
    (t/is (string? v))))

(t/deftest test-python-version-tuple
  (let [vt (platform-m/python-version-tuple)]
    (t/is (vector? vt))
    (t/is (pos? (count vt)))
    (t/is (every? string? vt))))

(t/deftest test-node
  (let [n (platform-m/node)]
    (t/is (string? n))))

(t/deftest test-uname-all-keys
  (let [u (platform-m/uname)]
    (t/is (contains? u :node))
    (t/is (contains? u :version))
    (t/is (string? (:version u)))))

(t/deftest test-architecture-64bit
  (let [[bits _] (platform-m/architecture)]
    (t/is (str/ends-with? bits "bit"))))

(t/deftest test-platform-with-arg
  (let [p (platform-m/platform false)]
    (t/is (string? p))
    (t/is (pos? (count p)))))

(t/deftest test-system-known-values
  (let [s (platform-m/system)]
    (t/is (or (= s "Linux") (= s "Darwin") (= s "Windows") (string? s)))))

(t/deftest test-uname-keys
  (let [u (platform-m/uname)]
    (t/is (map? u))
    (t/is (contains? u :system))
    (t/is (contains? u :node))
    (t/is (contains? u :release))
    (t/is (contains? u :machine))
    (t/is (contains? u :processor))))

(t/deftest test-architecture-returns-pair
  (let [a (platform-m/architecture)]
    (t/is (vector? a))
    (t/is (= 2 (count a)))
    (t/is (string? (first a)))))

(t/deftest test-python-version-tuple
  (let [t (platform-m/python-version-tuple)]
    (t/is (sequential? t))
    (t/is (every? string? t))))

(t/deftest test-java-ver
  (let [v (platform-m/java-ver)]
    (t/is (string? v))
    (t/is (pos? (count v)))))

(t/deftest test-python-version
  (let [v (platform-m/python-version)]
    (t/is (string? v))
    (t/is (pos? (count v)))))
