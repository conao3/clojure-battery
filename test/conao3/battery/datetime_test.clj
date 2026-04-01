;; Original: Lib/test/test_datetime.py

(ns conao3.battery.datetime-test
  (:require
   [clojure.test :as t]
   [conao3.battery.datetime :as dt-m]))

(t/deftest test-date-create
  (let [d (dt-m/date 2024 1 15)]
    (t/is (= 2024 (dt-m/get-year d)))
    (t/is (= 1 (dt-m/get-month d)))
    (t/is (= 15 (dt-m/get-day d)))))

(t/deftest test-date-isoformat
  (t/is (= "2024-01-15" (dt-m/date-isoformat (dt-m/date 2024 1 15))))
  (t/is (= "2024-12-31" (dt-m/date-isoformat (dt-m/date 2024 12 31)))))

(t/deftest test-date-strftime
  (let [d (dt-m/date 2024 1 15)]
    (t/is (= "2024-01-15" (dt-m/date-strftime d "%Y-%m-%d")))
    (t/is (= "15/01/2024" (dt-m/date-strftime d "%d/%m/%Y")))))

(t/deftest test-date-weekday
  (let [d (dt-m/date 2024 1 15)]
    (t/is (= 0 (dt-m/date-weekday d)))
    (t/is (= 1 (dt-m/date-isoweekday d))))
  (let [d (dt-m/date 2024 1 21)]
    (t/is (= 6 (dt-m/date-weekday d)))
    (t/is (= 7 (dt-m/date-isoweekday d)))))

(t/deftest test-date-fromisoformat
  (let [d (dt-m/date-fromisoformat "2024-01-15")]
    (t/is (= 2024 (dt-m/get-year d)))
    (t/is (= 1 (dt-m/get-month d)))
    (t/is (= 15 (dt-m/get-day d)))))

(t/deftest test-date-toordinal-fromordinal
  (let [d (dt-m/date 2024 1 15)]
    (let [ordinal (dt-m/date-toordinal d)]
      (t/is (pos? ordinal))
      (let [d2 (dt-m/date-fromordinal ordinal)]
        (t/is (= 2024 (dt-m/get-year d2)))
        (t/is (= 1 (dt-m/get-month d2)))
        (t/is (= 15 (dt-m/get-day d2)))))))

(t/deftest test-date-today
  (let [d (dt-m/date-today)]
    (t/is (pos? (dt-m/get-year d)))
    (t/is (<= 1 (dt-m/get-month d) 12))
    (t/is (<= 1 (dt-m/get-day d) 31))))

(t/deftest test-timedelta-create
  (let [td (dt-m/timedelta 5)]
    (t/is (= 5 (:days td)))
    (t/is (= 0 (:seconds td)))
    (t/is (= 0 (:microseconds td))))
  (let [td (dt-m/timedelta 5 3600)]
    (t/is (= 5 (:days td)))
    (t/is (= 3600 (:seconds td)))))

(t/deftest test-timedelta-normalization
  (let [td (dt-m/timedelta 0 90000)]
    (t/is (= 1 (:days td)))
    (t/is (= 3600 (:seconds td))))
  (let [td (dt-m/timedelta 1 -1)]
    (t/is (= 0 (:days td)))
    (t/is (= 86399 (:seconds td)))))

(t/deftest test-timedelta-total-seconds
  (let [td (dt-m/timedelta 1)]
    (t/is (= 86400.0 (dt-m/timedelta-total-seconds td))))
  (let [td (dt-m/timedelta 0 3600)]
    (t/is (= 3600.0 (dt-m/timedelta-total-seconds td))))
  (let [td (dt-m/timedelta 5 3 100000)]
    (t/is (= (+ (* 5 86400) 3 0.1) (dt-m/timedelta-total-seconds td)))))

(t/deftest test-date-subtraction
  (let [d1 (dt-m/date 2024 2 15)
        d2 (dt-m/date 2024 1 15)
        td (dt-m/date-sub d1 d2)]
    (t/is (= 31 (:days td)))
    (t/is (= 0 (:seconds td)))))

(t/deftest test-date-add-timedelta
  (let [d  (dt-m/date 2024 1 15)
        td (dt-m/timedelta 31)
        d2 (dt-m/date-add-timedelta d td)]
    (t/is (= 2024 (dt-m/get-year d2)))
    (t/is (= 2 (dt-m/get-month d2)))
    (t/is (= 15 (dt-m/get-day d2)))))

(t/deftest test-date-sub-timedelta
  (let [d2 (dt-m/date 2024 2 15)
        td (dt-m/timedelta 31)
        d  (dt-m/date-sub-timedelta d2 td)]
    (t/is (= 2024 (dt-m/get-year d)))
    (t/is (= 1 (dt-m/get-month d)))
    (t/is (= 15 (dt-m/get-day d)))))

(t/deftest test-timedelta-add
  (let [td1 (dt-m/timedelta 3)
        td2 (dt-m/timedelta 4)
        td  (dt-m/timedelta-add td1 td2)]
    (t/is (= 7 (:days td)))))

(t/deftest test-timedelta-sub
  (let [td1 (dt-m/timedelta 10)
        td2 (dt-m/timedelta 4)
        td  (dt-m/timedelta-sub td1 td2)]
    (t/is (= 6 (:days td)))))

(t/deftest test-timedelta-neg
  (let [td (dt-m/timedelta 5)
        neg (dt-m/timedelta-neg td)]
    (t/is (= -5 (:days neg)))))

(t/deftest test-time-create
  (let [t (dt-m/time 10 30 45 123456)]
    (t/is (= 10 (dt-m/get-hour t)))
    (t/is (= 30 (dt-m/get-minute t)))
    (t/is (= 45 (dt-m/get-second t)))
    (t/is (= 123456 (dt-m/get-microsecond t)))))

(t/deftest test-time-isoformat
  (t/is (= "10:30:45" (dt-m/time-isoformat (dt-m/time 10 30 45))))
  (t/is (= "10:30:45.123456" (dt-m/time-isoformat (dt-m/time 10 30 45 123456)))))

(t/deftest test-datetime-create
  (let [dt (dt-m/datetime 2024 1 15 10 30 45)]
    (t/is (= 2024 (dt-m/get-year dt)))
    (t/is (= 1 (dt-m/get-month dt)))
    (t/is (= 15 (dt-m/get-day dt)))
    (t/is (= 10 (dt-m/get-hour dt)))
    (t/is (= 30 (dt-m/get-minute dt)))
    (t/is (= 45 (dt-m/get-second dt)))))

(t/deftest test-datetime-isoformat
  (t/is (= "2024-01-15T10:30:45" (dt-m/datetime-isoformat (dt-m/datetime 2024 1 15 10 30 45)))))

(t/deftest test-datetime-strptime
  (let [dt (dt-m/datetime-strptime "2024-01-15 10:30:45" "%Y-%m-%d %H:%M:%S")]
    (t/is (= 2024 (dt-m/get-year dt)))
    (t/is (= 1 (dt-m/get-month dt)))
    (t/is (= 15 (dt-m/get-day dt)))
    (t/is (= 10 (dt-m/get-hour dt)))
    (t/is (= 30 (dt-m/get-minute dt)))
    (t/is (= 45 (dt-m/get-second dt)))))

(t/deftest test-datetime-fromisoformat
  (let [dt (dt-m/datetime-fromisoformat "2024-01-15T10:30:45")]
    (t/is (= 2024 (dt-m/get-year dt)))
    (t/is (= 10 (dt-m/get-hour dt))))
  (let [dt (dt-m/datetime-fromisoformat "2024-01-15 10:30:45")]
    (t/is (= 2024 (dt-m/get-year dt)))
    (t/is (= 10 (dt-m/get-hour dt)))))

(t/deftest test-datetime-date-time-parts
  (let [dt (dt-m/datetime 2024 1 15 10 30 45)
        d  (dt-m/datetime-date dt)
        t  (dt-m/datetime-time dt)]
    (t/is (= 2024 (dt-m/get-year d)))
    (t/is (= 1 (dt-m/get-month d)))
    (t/is (= 15 (dt-m/get-day d)))
    (t/is (= 10 (dt-m/get-hour t)))
    (t/is (= 30 (dt-m/get-minute t)))
    (t/is (= 45 (dt-m/get-second t)))))

(t/deftest test-datetime-add-timedelta
  (let [dt (dt-m/datetime 2024 1 15 10 30 45)
        td (dt-m/timedelta 1 3600)
        dt2 (dt-m/datetime-add-timedelta dt td)]
    (t/is (= 2024 (dt-m/get-year dt2)))
    (t/is (= 1 (dt-m/get-month dt2)))
    (t/is (= 16 (dt-m/get-day dt2)))
    (t/is (= 11 (dt-m/get-hour dt2)))
    (t/is (= 30 (dt-m/get-minute dt2)))))

(t/deftest test-datetime-subtraction
  (let [dt1 (dt-m/datetime 2024 1 16 12 0 0)
        dt2 (dt-m/datetime 2024 1 15 10 0 0)
        td  (dt-m/datetime-sub dt1 dt2)]
    (t/is (= 1 (:days td)))
    (t/is (= 7200 (:seconds td)))))

(t/deftest test-date-min-max
  (t/is (= 1 (dt-m/get-year dt-m/date-min)))
  (t/is (= 9999 (dt-m/get-year dt-m/date-max))))

(t/deftest test-datetime-min-max
  (t/is (= 1 (dt-m/get-year dt-m/datetime-min)))
  (t/is (= 9999 (dt-m/get-year dt-m/datetime-max)))
  (t/is (= 23 (dt-m/get-hour dt-m/datetime-max))))

(t/deftest test-datetime-now
  (let [dt (dt-m/datetime-now)]
    (t/is (pos? (dt-m/get-year dt)))))

(t/deftest test-datetime-strftime
  (let [dt (dt-m/datetime 2024 1 15 10 30 45)]
    (t/is (= "2024-01-15 10:30:45" (dt-m/datetime-strftime dt "%Y-%m-%d %H:%M:%S")))))

(t/deftest test-datetime-strptime
  (let [dt (dt-m/datetime-strptime "2024-01-15 10:30:45" "%Y-%m-%d %H:%M:%S")]
    (t/is (= 2024 (dt-m/get-year dt)))
    (t/is (= 1 (dt-m/get-month dt)))
    (t/is (= 15 (dt-m/get-day dt)))
    (t/is (= 10 (dt-m/get-hour dt)))))

(t/deftest test-datetime-fromisoformat
  (let [dt (dt-m/datetime-fromisoformat "2024-01-15T10:30:45")]
    (t/is (= 2024 (dt-m/get-year dt)))
    (t/is (= 10 (dt-m/get-hour dt)))))

(t/deftest test-datetime-isoformat
  (let [dt (dt-m/datetime 2024 1 15 10 30 45)]
    (t/is (= "2024-01-15T10:30:45" (dt-m/datetime-isoformat dt)))))

(t/deftest test-timedelta-min-max
  (t/is (= -999999999 (:days dt-m/timedelta-min)))
  (t/is (= 999999999 (:days dt-m/timedelta-max))))

(t/deftest test-date-fromtimestamp
  (let [d (dt-m/date-fromtimestamp (* 365 86400))]
    (t/is (= 1971 (dt-m/get-year d)))))

(t/deftest test-date-replace
  (let [d (dt-m/date 2024 6 15)
        d2 (dt-m/date-replace d :year 2025)
        d3 (dt-m/date-replace d :month 3 :day 20)]
    (t/is (= 2025 (dt-m/get-year d2)))
    (t/is (= 6 (dt-m/get-month d2)))
    (t/is (= 3 (dt-m/get-month d3)))
    (t/is (= 20 (dt-m/get-day d3)))))

(t/deftest test-datetime-sub-timedelta
  (let [dt (dt-m/datetime 2024 6 15 12 0 0)
        td (dt-m/timedelta 1)
        result (dt-m/datetime-sub-timedelta dt td)]
    (t/is (= 2024 (dt-m/get-year result)))
    (t/is (= 6 (dt-m/get-month result)))
    (t/is (= 14 (dt-m/get-day result)))
    (t/is (= 12 (dt-m/get-hour result)))))

;; timedelta normalization: negative seconds → carry to days
(t/deftest test-timedelta-negative-seconds
  ;; timedelta(0, -1) → days=-1, seconds=86399 (Python semantics)
  (let [td (dt-m/timedelta 0 -1)]
    (t/is (= -1 (:days td)))
    (t/is (= 86399 (:seconds td)))
    (t/is (= 0 (:microseconds td))))
  ;; timedelta(0, -86400) → days=-1, seconds=0
  (let [td (dt-m/timedelta 0 -86400)]
    (t/is (= -1 (:days td)))
    (t/is (= 0 (:seconds td))))
  ;; timedelta(1, -1) → days=0, seconds=86399
  (let [td (dt-m/timedelta 1 -1)]
    (t/is (= 0 (:days td)))
    (t/is (= 86399 (:seconds td)))))

;; date weekday for multiple dates  
(t/deftest test-date-weekday-multiple
  (t/is (= 0 (dt-m/date-weekday (dt-m/date 2024 1 15))))  ; Monday
  (t/is (= 1 (dt-m/date-weekday (dt-m/date 2024 1 16))))  ; Tuesday
  (t/is (= 4 (dt-m/date-weekday (dt-m/date 2024 1 19))))  ; Friday
  (t/is (= 5 (dt-m/date-weekday (dt-m/date 2024 1 20))))  ; Saturday
  (t/is (= 6 (dt-m/date-weekday (dt-m/date 2024 1 21))))) ; Sunday

;; date strftime with %A, %B, %a, %b
(t/deftest test-date-strftime-extended
  (let [d (dt-m/date 2024 1 15)]  ; Monday, January
    (t/is (= "Monday" (dt-m/date-strftime d "%A")))
    (t/is (= "Mon" (dt-m/date-strftime d "%a")))
    (t/is (= "January" (dt-m/date-strftime d "%B")))
    (t/is (= "Jan" (dt-m/date-strftime d "%b"))))
  (let [d (dt-m/date 2024 7 4)]   ; Thursday, July
    (t/is (= "Thursday" (dt-m/date-strftime d "%A")))
    (t/is (= "July" (dt-m/date-strftime d "%B")))))

(t/deftest test-timedelta-with-weeks
  ;; timedelta with weeks parameter
  (let [td (dt-m/timedelta 0 0 0 0 0 0 1)]  ; 1 week
    (t/is (= 7 (:days td))))
  (let [td (dt-m/timedelta 1 0 0 0 0 0 1)]  ; 1 day + 1 week
    (t/is (= 8 (:days td)))))

(t/deftest test-timedelta-carries
  ;; timedelta carries: large normalization
  (let [td1 (dt-m/timedelta 0 0 1)
        td2 (dt-m/timedelta 0 1 0)]
    (t/is (= 0 (:days td1)))
    (t/is (= 0 (:seconds td1)))
    (t/is (= 1 (:microseconds td1)))
    (t/is (= 0 (:days td2)))
    (t/is (= 1 (:seconds td2)))
    (t/is (= 0 (:microseconds td2)))))

(t/deftest test-timedelta-bool
  ;; non-zero timedeltas are truthy, zero is falsy
  (t/is (not= (dt-m/timedelta 1) (dt-m/timedelta 0)))
  (t/is (not= (dt-m/timedelta 0 1) (dt-m/timedelta 0)))
  (t/is (not= (dt-m/timedelta 0 0 1) (dt-m/timedelta 0)))
  (t/is (= (dt-m/timedelta 0) (dt-m/timedelta 0 0 0))))

(t/deftest test-timedelta-total-seconds-extended
  ;; total_seconds for various values
  (t/is (= 0.0 (dt-m/timedelta-total-seconds (dt-m/timedelta 0))))
  (t/is (= 1.0 (dt-m/timedelta-total-seconds (dt-m/timedelta 0 1))))
  (t/is (= 0.000001 (dt-m/timedelta-total-seconds (dt-m/timedelta 0 0 1))))
  (t/is (= (* 7 86400.0) (dt-m/timedelta-total-seconds (dt-m/timedelta 7)))))

(t/deftest test-date-equality
  ;; dates with same values are equal
  (t/is (= (dt-m/date 2024 1 15) (dt-m/date 2024 1 15)))
  (t/is (not= (dt-m/date 2024 1 15) (dt-m/date 2024 1 16))))

(t/deftest test-datetime-replace
  (let [dt (dt-m/datetime 2024 6 15 12 30 45)
        dt2 (dt-m/datetime-add-timedelta dt (dt-m/timedelta 0))
        dt3 (dt-m/datetime-add-timedelta dt (dt-m/timedelta 1))]
    (t/is (= 2024 (dt-m/get-year dt2)))
    (t/is (= 6 (dt-m/get-month dt2)))
    (t/is (= 16 (dt-m/get-day dt3)))))

(t/deftest test-time-strftime
  (let [t (dt-m/time 10 30 45)]
    (t/is (= "10:30:45" (dt-m/time-strftime t "%H:%M:%S")))
    (t/is (= "10" (dt-m/time-strftime t "%H")))))

(t/deftest test-date-compare
  ;; use date-sub to compare dates (days > 0 means d1 > d2)
  (let [d1 (dt-m/date 2024 6 1)
        d2 (dt-m/date 2024 1 15)
        d3 (dt-m/date 2024 6 1)]
    (t/is (pos? (:days (dt-m/date-sub d1 d2))))
    (t/is (neg? (:days (dt-m/date-sub d2 d1))))
    (t/is (= 0  (:days (dt-m/date-sub d1 d3))))))

(t/deftest test-timedelta-abs-via-neg
  ;; timedelta-neg of a negative timedelta returns positive
  (let [td (dt-m/timedelta-neg (dt-m/timedelta 5))]
    (t/is (= -5 (:days td)))
    (let [abs (dt-m/timedelta-neg td)]
      (t/is (= 5 (:days abs))))))

(t/deftest test-time-equality
  ;; same time values are equal
  (t/is (= (dt-m/time 10 30 45) (dt-m/time 10 30 45)))
  (t/is (not= (dt-m/time 10 30 45) (dt-m/time 10 30 46)))
  (t/is (= (dt-m/time 0 0 0) (dt-m/time))))

(t/deftest test-timedelta-microseconds-normalization
  ;; 1000000 microseconds == 1 second
  (let [td (dt-m/timedelta 0 0 1000000)]
    (t/is (= 0 (:days td)))
    (t/is (= 1 (:seconds td)))
    (t/is (= 0 (:microseconds td))))
  ;; 1000000000 microseconds == 1000 seconds
  (let [td (dt-m/timedelta 0 0 1000000000)]
    (t/is (= 0 (:days td)))
    (t/is (= 1000 (:seconds td)))
    (t/is (= 0 (:microseconds td)))))

(t/deftest test-datetime-microsecond
  ;; datetime with microsecond field
  (let [dt (dt-m/datetime 2024 1 15 10 30 45 500000)]
    (t/is (= 500000 (dt-m/get-microsecond dt)))
    ;; isoformat includes fractional seconds (Java may trim trailing zeros)
    (t/is (clojure.string/starts-with? (dt-m/datetime-isoformat dt) "2024-01-15T10:30:45."))))
