;; Original: Lib/test/test_pathlib/test_join_posix.py

(ns conao3.battery.pathlib-test
  (:require
   [clojure.test :as t]
   [conao3.battery.pathlib :as pathlib]))

(defn- assert-join [path-f]
  (let [p (path-f "//a")
        pp (pathlib/joinpath p "b")]
    (t/is (= "//a/b" pp))
    (t/is (= "//c" (pathlib/joinpath (path-f "/a") "//c")))
    (t/is (= "/c" (pathlib/joinpath (path-f "//a") "/c")))))

(defn- assert-div [path-f]
  (let [p (path-f "//a")
        pp (pathlib/divpath p "b")]
    (t/is (= "//a/b" pp))
    (t/is (= "//c" (pathlib/divpath (path-f "/a") "//c")))
    (t/is (= "/c" (pathlib/divpath (path-f "//a") "/c")))))

(t/deftest ^:kaocha/skip test-lexical-posix-path-join
  (assert-join pathlib/lexical-posix-path))

(t/deftest ^:kaocha/skip test-lexical-posix-path-div
  (assert-div pathlib/lexical-posix-path))

(when-not pathlib/is-pypi
  (t/deftest ^:kaocha/skip test-pure-posix-path-join
    (assert-join pathlib/pure-posix-path))

  (t/deftest ^:kaocha/skip test-pure-posix-path-div
    (assert-div pathlib/pure-posix-path))

  (when-not pathlib/windows?
    (t/deftest ^:kaocha/skip test-posix-path-join
      (assert-join pathlib/posix-path))

    (t/deftest ^:kaocha/skip test-posix-path-div
      (assert-div pathlib/posix-path))))
