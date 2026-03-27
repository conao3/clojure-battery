;; Original: Lib/test/test_shutil.py
;; All tests excluded: filesystem-dependent (rmtree, copytree, copy, move, etc.)

(ns conao3.battery.shutil-test
  (:require
   [clojure.test :as t]
   [conao3.battery.shutil :as shutil]))
