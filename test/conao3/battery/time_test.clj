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

(t/deftest test-process-time
  (let [t (time-m/process-time)]
    (t/is (float? t))
    (t/is (>= t 0.0))))

(t/deftest test-timezone-is-integer
  (let [tz (time-m/timezone)]
    (t/is (integer? tz))))

(t/deftest test-daylight-is-0-or-1
  (let [d (time-m/daylight)]
    (t/is (or (= d 0) (= d 1)))))

(t/deftest test-tzname-is-vector-of-strings
  (let [tzn time-m/tzname]
    (t/is (vector? tzn))
    (t/is (= 2 (count tzn)))
    (t/is (every? string? tzn))))

(t/deftest test-clock-constants
  (t/is (= 0 time-m/CLOCK_REALTIME))
  (t/is (= 1 time-m/CLOCK_MONOTONIC))
  (t/is (= 2 time-m/CLOCK_PROCESS_CPUTIME_ID))
  (t/is (= 3 time-m/CLOCK_THREAD_CPUTIME_ID)))

(t/deftest test-altzone-is-integer
  (t/is (integer? (time-m/altzone))))

(t/deftest test-time-ns
  (let [ns1 (time-m/time-ns)
        ns2 (time-m/time-ns)]
    (t/is (integer? ns1))
    (t/is (<= ns1 ns2))))

(t/deftest test-asctime-no-args
  (let [s (time-m/asctime)]
    (t/is (string? s))
    (t/is (= 24 (count s)))))

(t/deftest test-asctime-with-struct-time
  ;; 1973-09-16 01:03:52 Sunday (wday=6), day 259
  (let [st (time-m/->StructTime 1973 9 16 1 3 52 6 259 0)
        s  (time-m/asctime st)]
    (t/is (clojure.string/starts-with? s "Sun Sep 16"))
    (t/is (clojure.string/ends-with? s "1973"))))

(t/deftest test-ctime
  (let [t (time-m/mktime (time-m/->StructTime 1973 9 16 1 3 52 6 259 0))
        s (time-m/ctime t)]
    (t/is (string? s))
    (t/is (= 24 (count s)))
    (t/is (clojure.string/ends-with? s "1973"))))
