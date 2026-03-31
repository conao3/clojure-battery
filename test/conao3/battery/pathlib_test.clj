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

(t/deftest  test-lexical-posix-path-join
  (assert-join pathlib/lexical-posix-path))

(t/deftest  test-lexical-posix-path-div
  (assert-div pathlib/lexical-posix-path))

(when-not pathlib/is-pypi
  (t/deftest  test-pure-posix-path-join
    (assert-join pathlib/pure-posix-path))

  (t/deftest  test-pure-posix-path-div
    (assert-div pathlib/pure-posix-path))

  (when-not pathlib/windows?
    (t/deftest  test-posix-path-join
      (assert-join pathlib/posix-path))

    (t/deftest  test-posix-path-div
      (assert-div pathlib/posix-path))))

(t/deftest test-name
  (t/is (= "bar.txt" (pathlib/name "/foo/bar.txt")))
  (t/is (= "bar.txt" (pathlib/name "bar.txt")))
  (t/is (= "" (pathlib/name "/foo/"))))

(t/deftest test-parent
  (t/is (= "/foo" (pathlib/parent "/foo/bar.txt")))
  (t/is (= "/" (pathlib/parent "/foo")))
  (t/is (= "." (pathlib/parent "bar.txt"))))

(t/deftest test-stem
  (t/is (= "bar" (pathlib/stem "/foo/bar.txt")))
  (t/is (= "bar" (pathlib/stem "bar")))
  (t/is (= ".hidden" (pathlib/stem "/foo/.hidden"))))

(t/deftest test-suffix
  (t/is (= ".txt" (pathlib/suffix "/foo/bar.txt")))
  (t/is (= "" (pathlib/suffix "/foo/bar")))
  (t/is (= ".gz" (pathlib/suffix "/foo/bar.tar.gz"))))

(t/deftest test-parts
  (t/is (= ["/" "foo" "bar"] (pathlib/parts "/foo/bar")))
  (t/is (= ["foo" "bar"] (pathlib/parts "foo/bar")))
  (t/is (= ["foo"] (pathlib/parts "foo"))))

(t/deftest test-is-absolute
  (t/is (true? (pathlib/is-absolute? "/foo/bar")))
  (t/is (false? (pathlib/is-absolute? "foo/bar")))
  (t/is (false? (pathlib/is-absolute? "foo"))))

(t/deftest test-with-name
  (t/is (= "/foo/baz.txt" (pathlib/with-name "/foo/bar.txt" "baz.txt")))
  (t/is (= "baz.txt" (pathlib/with-name "bar.txt" "baz.txt"))))

(t/deftest test-with-suffix
  (t/is (= "/foo/bar.md" (pathlib/with-suffix "/foo/bar.txt" ".md")))
  (t/is (= "/foo/bar" (pathlib/with-suffix "/foo/bar.txt" "")))
  (t/is (= "bar.clj" (pathlib/with-suffix "bar.txt" ".clj"))))
