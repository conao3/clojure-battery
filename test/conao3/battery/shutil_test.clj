;; Original: Lib/test/test_shutil.py

(ns conao3.battery.shutil-test
  (:require
   [clojure.test :as t]
   [conao3.battery.shutil :as shutil])
  (:import
   [java.io File]))

(t/deftest test-which-existing
  (t/is (string? (shutil/which "ls")))
  (t/is (.endsWith ^String (shutil/which "ls") "ls")))

(t/deftest test-which-nonexistent
  (t/is (nil? (shutil/which "this-executable-does-not-exist-xyz"))))

(t/deftest test-which-with-path
  (t/is (nil? (shutil/which "ls" nil "/nonexistent/path")))
  (t/is (string? (shutil/which "ls" nil "/bin:/usr/bin"))))

(t/deftest test-get-terminal-size-default
  (let [size (shutil/get-terminal-size)]
    (t/is (map? size))
    (t/is (contains? size :columns))
    (t/is (contains? size :lines))
    (t/is (pos? (:columns size)))
    (t/is (pos? (:lines size)))))

(t/deftest test-get-terminal-size-fallback
  (let [size (shutil/get-terminal-size [132 44])]
    (t/is (= 132 (:columns size)))
    (t/is (= 44 (:lines size)))))

(t/deftest test-rmtree
  (let [dir (File/createTempFile "shutil-test" "")
        _ (.delete dir)
        _ (.mkdir dir)
        sub (File. dir "subfile.txt")]
    (.createNewFile sub)
    (t/is (.exists dir))
    (shutil/rmtree (.getAbsolutePath dir))
    (t/is (not (.exists dir)))))

(t/deftest test-copy
  (let [src (File/createTempFile "shutil-src" ".txt")
        dst (File/createTempFile "shutil-dst" ".txt")]
    (spit (.getAbsolutePath src) "hello")
    (shutil/copy (.getAbsolutePath src) (.getAbsolutePath dst))
    (t/is (= "hello" (slurp (.getAbsolutePath dst))))
    (.delete src)
    (.delete dst)))

(t/deftest test-disk-usage
  (let [usage (shutil/disk-usage "/")]
    (t/is (map? usage))
    (t/is (contains? usage :total))
    (t/is (contains? usage :used))
    (t/is (contains? usage :free))
    (t/is (pos? (:total usage)))))
