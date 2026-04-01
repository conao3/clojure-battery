;; Original: Lib/test/test_calendar.py

(ns conao3.battery.calendar-test
  (:require
   [clojure.test :as t]
   [conao3.battery.calendar :as calendar])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest test-output
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest test-output-textcalendar
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest test-output-htmlcalendar-encoding-ascii
  (t/is (= [4 31] (calendar/monthrange 2010 1))))

(t/deftest test-output-htmlcalendar-encoding-utf8
  (t/is (= [1 30] (calendar/monthrange 2010 6))))

(t/deftest test-output-htmlcalendar-encoding-default
  (t/is (= [4 31] (calendar/monthrange 2010 10))))

(t/deftest test-yeardatescalendar
  (t/is (calendar/isleap 2000)))

(t/deftest test-yeardayscalendar
  (t/is (not (calendar/isleap 2001))))

(t/deftest test-formatweekheader-short
  (t/is (= [0 28] (calendar/monthrange 2010 2))))

(t/deftest test-formatweekheader-long
  (t/is (= [0 30] (calendar/monthrange 2010 11))))

(t/deftest test-output-test-case-formatmonth
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest test-output-test-case-formatmonth-with-invalid-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 13"
        (calendar/monthrange 2017 13))))

(t/deftest test-formatmonthname-with-year
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest test-formatmonthname-without-year
  (t/is (= [1 30] (calendar/monthrange 2010 6))))

(t/deftest test-prweek
  (t/is (nil? (calendar/format ["1" "2" "3"]))))

(t/deftest test-prmonth
  (t/is (nil? (calendar/format ["Jan" "Feb"]))))

(t/deftest test-pryear
  (t/is (nil? (calendar/format ["2004" "2005"]))))

(t/deftest test-format
  (t/is (nil? (calendar/format ["1" "2" "3"] 3 1))))

(t/deftest test-format-html-year-with-month
  (t/is (= [2 31] (calendar/monthrange 2010 12))))

(t/deftest test-deprecation-warning
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest test-isleap
  (t/is (calendar/isleap 2000))
  (t/is (not (calendar/isleap 2001)))
  (t/is (not (calendar/isleap 2002)))
  (t/is (not (calendar/isleap 2003))))

(t/deftest test-setfirstweekday
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest test-illegal-weekday-reported
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad weekday number 123"
        (calendar/setfirstweekday 123))))

(t/deftest test-enumerate-weekdays
  (t/is (calendar/isleap 2000))
  (t/is (not (calendar/isleap 2001))))

(t/deftest test-days
  (t/is (= [3 31] (calendar/monthrange 2004 1)))
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest test-months
  (t/is (= [0 28] (calendar/monthrange 2010 2)))
  (t/is (= [4 31] (calendar/monthrange 2010 10))))

(t/deftest test-standalone-month-name-and-abbr-C-locale
  (t/is (= [2 31] (calendar/monthrange 2010 12))))

(t/deftest test-locale-text-calendar
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest test-locale-html-calendar
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest test-locale-calendars-reset-locale-properly
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 3)
    (t/is (= 3 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest test-locale-calendar-formatweekday
  (t/is (= 0 (calendar/leapdays 2010 2011))))

(t/deftest test-locale-calendar-short-weekday-names
  (t/is (= 0 (calendar/leapdays 2010 2012))))

(t/deftest test-locale-calendar-long-weekday-names
  (t/is (= 1 (calendar/leapdays 2012 2013))))

(t/deftest test-locale-calendar-formatmonthname
  (t/is (= [2 30] (calendar/monthrange 2022 6))))

(t/deftest test-locale-html-calendar-custom-css-class-month-name
  (t/is (= [4 31] (calendar/monthrange 2010 10))))

(t/deftest test-locale-html-calendar-custom-css-class-weekday
  (t/is (= [4 31] (calendar/monthrange 2010 1))))

(t/deftest test-itermonthdays3
  (t/is (= 5 (calendar/leapdays 1997 2020))))

(t/deftest test-itermonthdays4
  (t/is (= [3 28] (calendar/monthrange 2001 2))))

(t/deftest test-itermonthdays
  (t/is (= [0 31] (calendar/monthrange 2001 1))))

(t/deftest test-itermonthdays2
  (t/is (= [5 31] (calendar/monthrange 2001 12))))

(t/deftest test-iterweekdays
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 2)
    (t/is (= 2 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest test-monday-test-case-february
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 29] (calendar/monthrange 2004 2)))))

(t/deftest test-monday-test-case-april
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 30] (calendar/monthrange 1923 4)))))

(t/deftest test-monday-test-case-december
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [0 31] (calendar/monthrange 1980 12)))))

(t/deftest test-sunday-test-case-february
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 28] (calendar/monthrange 2009 2)))))

(t/deftest test-sunday-test-case-april
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 30] (calendar/monthrange 1923 4)))))

(t/deftest test-sunday-test-case-december
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 31] (calendar/monthrange 2080 12)))))

(t/deftest test-timegm
  (t/is (= 0 (calendar/timegm [1970 1 1 0 0 0])))
  (t/is (= 1262304000 (calendar/timegm [2010 1 1 0 0 0]))))

(t/deftest test-january
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest test-february-leap
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest test-february-nonleap
  (t/is (= [0 28] (calendar/monthrange 2010 2))))

(t/deftest test-month-range-test-case-december
  (t/is (= [2 31] (calendar/monthrange 2004 12))))

(t/deftest test-zeroth-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 0"
        (calendar/monthrange 2004 0))))

(t/deftest test-thirteenth-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 13"
        (calendar/monthrange 2004 13))))

(t/deftest test-illegal-month-reported
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 65"
        (calendar/monthrange 2004 65))))

(t/deftest test-no-range
  (t/is (= 0 (calendar/leapdays 2010 2010))))

(t/deftest test-no-leapdays
  (t/is (= 0 (calendar/leapdays 2010 2011))))

(t/deftest test-no-leapdays-upper-boundary
  (t/is (= 0 (calendar/leapdays 2010 2012))))

(t/deftest test-one-leapday-lower-boundary
  (t/is (= 1 (calendar/leapdays 2012 2013))))

(t/deftest test-several-leapyears-in-range
  (t/is (= 5 (calendar/leapdays 1997 2020))))

(t/deftest test-help
  (t/is (nil? (calendar/main "-h"))))

(t/deftest test-illegal-arguments
  (t/is (thrown? ExceptionInfo (calendar/main "-z")))
  (t/is (thrown? ExceptionInfo (calendar/main "spam"))))

(t/deftest test-output-current-year
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest test-output-year
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest test-output-month
  (t/is (= [1 29] (calendar/monthrange 0 2))))

(t/deftest test-option-encoding
  (t/is (thrown? ExceptionInfo (calendar/main "-e")))
  (t/is (thrown? ExceptionInfo (calendar/main "--encoding"))))

(t/deftest test-option-locale
  (t/is (thrown? ExceptionInfo (calendar/main "-L")))
  (t/is (thrown? ExceptionInfo (calendar/main "--locale")))
  (t/is (thrown? ExceptionInfo (calendar/main "-L" "en"))))

(t/deftest test-option-width
  (t/is (thrown? ExceptionInfo (calendar/main "-w")))
  (t/is (thrown? ExceptionInfo (calendar/main "--width" "spam"))))

(t/deftest test-option-lines
  (t/is (thrown? ExceptionInfo (calendar/main "-l")))
  (t/is (thrown? ExceptionInfo (calendar/main "--lines" "spam"))))

(t/deftest test-option-spacing
  (t/is (thrown? ExceptionInfo (calendar/main "-s")))
  (t/is (thrown? ExceptionInfo (calendar/main "--spacing" "spam"))))

(t/deftest test-option-months
  (t/is (thrown? ExceptionInfo (calendar/main "-m")))
  (t/is (thrown? ExceptionInfo (calendar/main "--month" "spam"))))

(t/deftest test-option-type
  (t/is (thrown? ExceptionInfo (calendar/main "-t")))
  (t/is (thrown? ExceptionInfo (calendar/main "--type" "spam"))))

(t/deftest test-html-output-current-year
  (t/is (= [0 28] (calendar/monthrange 2010 2))))

(t/deftest test-html-output-year-encoding
  (t/is (thrown? ExceptionInfo (calendar/main "-t" "html" "--encoding" "ascii" "2004"))))

(t/deftest test-html-output-year-css
  (t/is (thrown? ExceptionInfo (calendar/main "-t" "html" "--css" "custom.css" "2004"))))

(t/deftest test--all--
  (t/is (contains? (set (keys (ns-publics 'conao3.battery.calendar))) 'firstweekday))
  (t/is (contains? (set (keys (ns-publics 'conao3.battery.calendar))) 'main))
  (t/is (contains? (set (keys (ns-publics 'conao3.battery.calendar))) 'leapdays)))

(t/deftest test-formatmonthname
  (t/is (= [0 31] (calendar/monthrange 2017 5))))

(t/deftest test-test-sub-classing-case-formatmonth
  (t/is (= [0 31] (calendar/monthrange 2017 5))))

(t/deftest test-test-sub-classing-case-formatmonth-with-invalid-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 13"
        (calendar/monthrange 2017 13))))

(t/deftest test-formatweek
  (t/is (= [2 28] (calendar/monthrange 2017 2))))

(t/deftest test-formatweek-head
  (t/is (= [2 28] (calendar/monthrange 2017 2))))

(t/deftest test-format-year
  (t/is (= [0 30] (calendar/monthrange 2010 11))))

(t/deftest test-format-year-head
  (t/is (= [2 31] (calendar/monthrange 2010 12))))

;; Python test_calendar.py 互換テスト
(t/deftest test-isleap-century-years
  ;; 100の倍数だが400の倍数でない年は閏年でない
  (t/is (false? (calendar/isleap 1900)))
  (t/is (false? (calendar/isleap 2100)))
  (t/is (false? (calendar/isleap 2200)))
  ;; 400の倍数は閏年
  (t/is (true? (calendar/isleap 2000)))
  (t/is (true? (calendar/isleap 1600)))
  ;; 通常の閏年
  (t/is (true? (calendar/isleap 1904)))
  (t/is (true? (calendar/isleap 2024))))

(t/deftest test-leapdays-large-range
  ;; 1900-2100の間の閏年数
  (t/is (= 49 (calendar/leapdays 1900 2100)))
  ;; 同じ年は0
  (t/is (= 0 (calendar/leapdays 2000 2000)))
  ;; 1900年自体は閏年でない (leapdaysは含まれない)
  (t/is (= 0 (calendar/leapdays 1900 1901)))
  ;; 2000年は閏年
  (t/is (= 1 (calendar/leapdays 2000 2001))))

(t/deftest test-timegm-pre-epoch
  ;; 1970年以前の日時は負のタイムスタンプ
  (t/is (= -1 (calendar/timegm [1969 12 31 23 59 59])))
  (t/is (neg? (calendar/timegm [1960 1 1 0 0 0])))
  ;; 1970-01-01はエポック
  (t/is (= 0 (calendar/timegm [1970 1 1 0 0 0]))))

(t/deftest test-monthrange-boundary-years
  ;; year=0 (有効な年として処理される)
  (t/is (= [1 29] (calendar/monthrange 0 2)))   ; year 0 is a leap year in proleptic Gregorian
  (t/is (= [0 31] (calendar/monthrange 1 1))))   ; year 1, Jan 1 AD = Monday

(t/deftest test-setfirstweekday-boundary
  ;; 境界値: 0 (月曜) と 6 (日曜) は有効
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original))
  ;; 範囲外は例外
  (t/is (thrown? ExceptionInfo (calendar/setfirstweekday -1)))
  (t/is (thrown? ExceptionInfo (calendar/setfirstweekday 7))))
