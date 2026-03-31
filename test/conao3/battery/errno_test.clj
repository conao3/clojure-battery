;; Original: Lib/test/test_errno.py

(ns conao3.battery.errno-test
  (:require
   [clojure.test :as t]
   [conao3.battery.errno :as errno-m]))

(t/deftest test-constants
  (t/is (= 1 errno-m/EPERM))
  (t/is (= 2 errno-m/ENOENT))
  (t/is (= 13 errno-m/EACCES))
  (t/is (= 22 errno-m/EINVAL))
  (t/is (= 111 errno-m/ECONNREFUSED))
  (t/is (= 11 errno-m/EAGAIN))
  (t/is (= 11 errno-m/EWOULDBLOCK)))

(t/deftest test-errorcode
  (t/is (map? errno-m/errorcode))
  (t/is (= "EPERM" (get errno-m/errorcode 1)))
  (t/is (= "ENOENT" (get errno-m/errorcode 2)))
  (t/is (= "EACCES" (get errno-m/errorcode 13)))
  (t/is (= "EINVAL" (get errno-m/errorcode 22))))

(t/deftest test-strerror
  (t/is (string? (errno-m/strerror errno-m/ENOENT)))
  (t/is (string? (errno-m/strerror errno-m/EACCES)))
  (t/is (string? (errno-m/strerror errno-m/EINVAL)))
  (t/is (string? (errno-m/strerror 9999))))

(t/deftest test-strerror-enoent
  (t/is (= "No such file or directory" (errno-m/strerror 2))))

(t/deftest test-strerror-eperm
  (t/is (= "Operation not permitted" (errno-m/strerror 1))))
