;; Original: Lib/test/test_time.py

(ns conao3.battery.time-test
  (:require
   [clojure.test :as t]
   [conao3.battery.time :as time-m]))

(t/deftest test-time-returns-float
  (let [t (time-m/time)]
    (t/is (float? t))
    (t/is (pos? t))))

(t/deftest test-monotonic-increasing
  (let [t1 (time-m/monotonic)
        t2 (time-m/monotonic)]
    (t/is (>= t2 t1))))

(t/deftest test-perf-counter-positive
  (t/is (pos? (time-m/perf-counter))))

(t/deftest test-sleep-basic
  (let [start (time-m/monotonic)]
    (time-m/sleep 0.05)
    (let [elapsed (- (time-m/monotonic) start)]
      (t/is (>= elapsed 0.02)))))

(t/deftest test-gmtime-basic
  (let [st (time-m/gmtime 0)]
    (t/is (= 1970 (:tm_year st)))
    (t/is (= 1 (:tm_mon st)))
    (t/is (= 1 (:tm_mday st)))
    (t/is (= 0 (:tm_hour st)))
    (t/is (= 0 (:tm_min st)))
    (t/is (= 0 (:tm_sec st)))))

(t/deftest test-gmtime-epoch-weekday
  (let [st (time-m/gmtime 0)]
    (t/is (= 3 (:tm_wday st)))))

(t/deftest test-gmtime-current
  (let [st (time-m/gmtime)]
    (t/is (>= (:tm_year st) 2024))
    (t/is (<= 1 (:tm_mon st) 12))
    (t/is (<= 1 (:tm_mday st) 31))))

(t/deftest test-localtime-basic
  (let [st (time-m/localtime)]
    (t/is (>= (:tm_year st) 2024))
    (t/is (<= 1 (:tm_mon st) 12))))

(t/deftest test-struct-time-fields
  (let [st (time-m/gmtime 86400)]
    (t/is (= 1970 (:tm_year st)))
    (t/is (= 1 (:tm_mon st)))
    (t/is (= 2 (:tm_mday st)))))

(t/deftest test-mktime-roundtrip
  (let [st (time-m/localtime)
        ts (time-m/mktime st)
        st2 (time-m/localtime ts)]
    (t/is (= (:tm_year st) (:tm_year st2)))
    (t/is (= (:tm_mon st) (:tm_mon st2)))
    (t/is (= (:tm_mday st) (:tm_mday st2)))))

(t/deftest test-strftime-basic
  (let [st (time-m/gmtime 0)]
    (t/is (= "1970" (time-m/strftime "%Y" st)))
    (t/is (= "01" (time-m/strftime "%m" st)))
    (t/is (= "01" (time-m/strftime "%d" st)))))

(t/deftest test-strftime-datetime
  (let [st (time-m/gmtime 0)]
    (t/is (= "1970-01-01" (time-m/strftime "%Y-%m-%d" st)))
    (t/is (= "00:00:00" (time-m/strftime "%H:%M:%S" st)))))

(t/deftest test-strptime-basic
  (let [st (time-m/strptime "2024-01-15" "%Y-%m-%d")]
    (t/is (= 2024 (:tm_year st)))
    (t/is (= 1 (:tm_mon st)))
    (t/is (= 15 (:tm_mday st)))))

(t/deftest test-strptime-datetime
  (let [st (time-m/strptime "2024-01-15 10:30:45" "%Y-%m-%d %H:%M:%S")]
    (t/is (= 2024 (:tm_year st)))
    (t/is (= 10 (:tm_hour st)))
    (t/is (= 30 (:tm_min st)))
    (t/is (= 45 (:tm_sec st)))))

(t/deftest test-gmtime-tm-yday
  (let [st (time-m/gmtime 0)]
    (t/is (= 1 (:tm_yday st))))
  (let [st (time-m/gmtime (* 365 86400))]
    (t/is (= 1 (:tm_yday st)))))
