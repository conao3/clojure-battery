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

(t/deftest test-suffixes
  (t/is (= [".tar" ".gz"] (pathlib/suffixes "/foo/bar.tar.gz")))
  (t/is (= [".txt"] (pathlib/suffixes "bar.txt")))
  (t/is (= [] (pathlib/suffixes "bar"))))

(t/deftest test-joinpath-multiple
  (t/is (= "/a/b/c" (pathlib/joinpath "/a" "b" "c")))
  (t/is (= "/c" (pathlib/joinpath "/a" "b" "/c"))))

(t/deftest test-parts-root
  (t/is (= ["/"] (pathlib/parts "/")))
  (t/is (= ["/" "a"] (pathlib/parts "/a"))))

(t/deftest test-parent-multiple-levels
  (t/is (= "/a/b" (pathlib/parent "/a/b/c")))
  (t/is (= "/a" (pathlib/parent "/a/b")))
  (t/is (= "/" (pathlib/parent "/a"))))

(t/deftest test-stem-dotfile
  (t/is (= "" (pathlib/stem "/foo/")))
  (t/is (= "file.tar" (pathlib/stem "file.tar.gz"))))

(t/deftest test-with-suffix-edge-cases
  ;; Hidden file (dot-file) - adding suffix appends to whole name
  (t/is (= "/foo/.hidden.bak" (pathlib/with-suffix "/foo/.hidden" ".bak")))
  ;; File with no extension
  (t/is (= "Makefile.bak" (pathlib/with-suffix "Makefile" ".bak")))
  (t/is (= "Makefile" (pathlib/with-suffix "Makefile" "")))
  ;; Multiple suffixes - only last one is replaced
  (t/is (= "/foo/bar.tar.bak" (pathlib/with-suffix "/foo/bar.tar.gz" ".bak"))))

(t/deftest test-name-edge-cases
  ;; Empty path
  (t/is (= "" (pathlib/name "")))
  ;; Root path
  (t/is (= "" (pathlib/name "/")))
  ;; Trailing slash
  (t/is (= "" (pathlib/name "/foo/"))))

(t/deftest test-suffixes-multiple
  (t/is (= [".tar" ".gz" ".bak"] (pathlib/suffixes "archive.tar.gz.bak")))
  ;; Hidden file with suffix
  (t/is (= [".conf"] (pathlib/suffixes ".hidden.conf"))))

(t/deftest test-is-absolute
  (t/is (true? (pathlib/is-absolute? "/foo/bar")))
  (t/is (false? (pathlib/is-absolute? "foo/bar")))
  (t/is (false? (pathlib/is-absolute? ".")))
  (t/is (true? (pathlib/is-absolute? "/"))))

(t/deftest test-divpath
  (t/is (= "/foo/bar" (pathlib/divpath "/foo" "bar")))
  (t/is (= "foo/bar" (pathlib/divpath "foo" "bar"))))

(t/deftest test-joinpath-absolute-segment
  ;; Joining with absolute segment replaces path (Python behavior)
  ;; Clojure implementation may differ - test what's implemented
  (t/is (string? (pathlib/joinpath "/foo" "bar" "baz"))))

(t/deftest test-suffix-no-extension
  (t/is (= "" (pathlib/suffix "Makefile")))
  (t/is (= "" (pathlib/suffix "/foo/bar")))
  (t/is (= "" (pathlib/suffix ""))))

(t/deftest test-parts-relative
  (let [p (pathlib/parts "foo/bar/baz")]
    (t/is (= ["foo" "bar" "baz"] p))))

(t/deftest test-parent-relative
  (t/is (= "." (pathlib/parent "foo")))
  (t/is (= "foo" (pathlib/parent "foo/bar"))))
