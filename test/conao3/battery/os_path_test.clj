;; Original: Lib/test/test_genericpath.py, Lib/test/test_posixpath.py

(ns conao3.battery.os-path-test
  (:require
   [clojure.test :as t]
   [conao3.battery.os-path :as osp-m]))

(t/deftest test-join-basic
  (t/is (= "/foo/bar/baz" (osp-m/join "/foo" "bar" "baz")))
  (t/is (= "/foo/bar" (osp-m/join "/foo" "bar")))
  (t/is (= "foo/bar" (osp-m/join "foo" "bar"))))

(t/deftest test-join-abs-override
  (t/is (= "/bar" (osp-m/join "/foo" "/bar")))
  (t/is (= "/baz" (osp-m/join "/foo" "/bar" "/baz"))))

(t/deftest test-join-trailing-slash
  (t/is (= "foo/bar" (osp-m/join "foo/" "bar")))
  (t/is (= "foo/bar" (osp-m/join "" "foo" "bar"))))

(t/deftest test-split-basic
  (t/is (= ["/foo/bar" "baz"] (osp-m/split "/foo/bar/baz")))
  (t/is (= ["/" "foo"] (osp-m/split "/foo")))
  (t/is (= ["" "foo"] (osp-m/split "foo"))))

(t/deftest test-split-trailing-slash
  (t/is (= ["/foo/bar" ""] (osp-m/split "/foo/bar/"))))

(t/deftest test-splitext-basic
  (t/is (= ["/foo/bar" ".txt"] (osp-m/splitext "/foo/bar.txt")))
  (t/is (= ["/foo/bar" ""] (osp-m/splitext "/foo/bar")))
  (t/is (= ["/foo/.hidden" ""] (osp-m/splitext "/foo/.hidden"))))

(t/deftest test-basename
  (t/is (= "baz.txt" (osp-m/basename "/foo/bar/baz.txt")))
  (t/is (= "foo" (osp-m/basename "foo")))
  (t/is (= "" (osp-m/basename "/foo/"))))

(t/deftest test-dirname
  (t/is (= "/foo/bar" (osp-m/dirname "/foo/bar/baz.txt")))
  (t/is (= "/" (osp-m/dirname "/foo")))
  (t/is (= "" (osp-m/dirname "foo"))))

(t/deftest test-normpath
  (t/is (= "/foo/baz" (osp-m/normpath "/foo/./bar/../baz")))
  (t/is (= "foo/bar" (osp-m/normpath "foo/./bar")))
  (t/is (= "/" (osp-m/normpath "/."))))

(t/deftest test-isabs
  (t/is (true? (osp-m/isabs "/foo")))
  (t/is (false? (osp-m/isabs "foo")))
  (t/is (false? (osp-m/isabs "./foo"))))

(t/deftest test-exists
  (t/is (true? (osp-m/exists "/tmp")))
  (t/is (false? (osp-m/exists "/nonexistent/path/xyz"))))

(t/deftest test-isdir
  (t/is (true? (osp-m/isdir "/tmp")))
  (t/is (false? (osp-m/isdir "/nonexistent/path"))))

(t/deftest test-isfile
  (t/is (true? (osp-m/isfile "/etc/hosts")))
  (t/is (false? (osp-m/isfile "/tmp"))))

(t/deftest test-expanduser
  (let [home (System/getProperty "user.home")]
    (t/is (= (str home "/foo") (osp-m/expanduser "~/foo")))
    (t/is (= "/absolute/path" (osp-m/expanduser "/absolute/path")))))

(t/deftest test-commonprefix
  (t/is (= "/foo/ba" (osp-m/commonprefix ["/foo/bar" "/foo/baz"])))
  (t/is (= "" (osp-m/commonprefix [])))
  (t/is (= "/foo/" (osp-m/commonprefix ["/foo/bar" "/foo/baz" "/foo/qux"]))))

(t/deftest test-commonpath
  (t/is (= "/foo" (osp-m/commonpath ["/foo/bar" "/foo/baz"])))
  (t/is (= "/foo/bar" (osp-m/commonpath ["/foo/bar/baz" "/foo/bar/qux"]))))

(t/deftest test-sep
  (t/is (= "/" osp-m/sep)))

(t/deftest test-relpath
  (t/is (= "bar" (osp-m/relpath "/foo/bar" "/foo")))
  (t/is (= "baz" (osp-m/relpath "/foo/bar/baz" "/foo/bar"))))
