;; Original: Lib/test/test_sys.py

(ns conao3.battery.sys-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t]
   [conao3.battery.sys :as sys-m]))

(t/deftest test-version
  (t/is (string? sys-m/version))
  (t/is (pos? (count sys-m/version))))

(t/deftest test-version-info
  (t/is (map? sys-m/version-info))
  (t/is (integer? (:major sys-m/version-info)))
  (t/is (integer? (:minor sys-m/version-info)))
  (t/is (integer? (:micro sys-m/version-info))))

(t/deftest test-platform
  (t/is (string? sys-m/platform))
  (t/is (pos? (count sys-m/platform))))

(t/deftest test-maxsize
  (t/is (= Long/MAX_VALUE sys-m/maxsize)))

(t/deftest test-maxunicode
  (t/is (= 0x10FFFF sys-m/maxunicode)))

(t/deftest test-byteorder
  (t/is (or (= "big" sys-m/byteorder)
            (= "little" sys-m/byteorder))))

(t/deftest test-getdefaultencoding
  (let [enc (sys-m/getdefaultencoding)]
    (t/is (string? enc))
    (t/is (pos? (count enc)))))

(t/deftest test-getrecursionlimit
  (t/is (pos? (sys-m/getrecursionlimit))))

(t/deftest test-setrecursionlimit
  (t/is (nil? (sys-m/setrecursionlimit 2000))))

(t/deftest test-getrefcount
  (t/is (= 1 (sys-m/getrefcount :anything))))

(t/deftest test-intern
  (let [s1 (sys-m/intern "hello")
        s2 (sys-m/intern "hello")]
    (t/is (= s1 s2))
    (t/is (identical? s1 s2))))

(t/deftest test-path
  (let [p (sys-m/path)]
    (t/is (vector? p))
    (t/is (every? string? p))))

(t/deftest test-modules
  (let [m (sys-m/modules)]
    (t/is (map? m))
    (t/is (pos? (count m)))))

(t/deftest test-exc-info
  (let [info (sys-m/exc-info)]
    (t/is (= [nil nil nil] info))))

(t/deftest test-getsizeof
  (t/is (pos? (sys-m/getsizeof "hello")))
  (t/is (pos? (sys-m/getsizeof [1 2 3])))
  (t/is (= 0 (sys-m/getsizeof (Object.) 0))))

(t/deftest test-getfilesystemencoding
  (let [enc (sys-m/getfilesystemencoding)]
    (t/is (string? enc))
    (t/is (pos? (count enc)))))

(t/deftest test-stdin-stdout-stderr
  (t/is (some? sys-m/stdin))
  (t/is (some? sys-m/stdout))
  (t/is (some? sys-m/stderr)))

(t/deftest test-argv
  (t/is (vector? sys-m/argv)))

(t/deftest test-platform-values
  (t/is (or (= "darwin" sys-m/platform)
            (= "linux" sys-m/platform)
            (= "win32" sys-m/platform)
            (string? sys-m/platform))))

(t/deftest test-getrecursionlimit
  (t/is (pos? (sys-m/getrecursionlimit))))

(t/deftest test-setrecursionlimit-noop
  (t/is (nil? (sys-m/setrecursionlimit 500))))

(t/deftest test-getrefcount
  (t/is (= 1 (sys-m/getrefcount "test"))))

(t/deftest test-exc-info-returns-triple
  (let [info (sys-m/exc-info)]
    (t/is (= 3 (count info)))))

(t/deftest test-path
  (let [p (sys-m/path)]
    (t/is (vector? p))))

(t/deftest test-modules
  (let [m (sys-m/modules)]
    (t/is (map? m))
    (t/is (contains? m "clojure.core"))))

(t/deftest test-getsizeof-positive
  (t/is (pos? (sys-m/getsizeof 42)))
  (t/is (pos? (sys-m/getsizeof "hello")))
  (t/is (pos? (sys-m/getsizeof []))))

(t/deftest test-getdefaultencoding
  (let [enc (sys-m/getdefaultencoding)]
    (t/is (string? enc))
    (t/is (pos? (count enc)))))

(t/deftest test-modules-includes-loaded-ns
  ;; modules returns known Clojure core namespaces
  (let [m (sys-m/modules)]
    (t/is (contains? m "clojure.string"))))

(t/deftest test-exc-info-nil-outside-handler
  ;; Outside exception handler, exc-info returns [nil nil nil]
  (let [[t v tb] (sys-m/exc-info)]
    (t/is (nil? t))
    (t/is (nil? v))
    (t/is (nil? tb))))
