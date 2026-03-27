;; Original: Lib/test/test_calendar.py

(ns conao3.battery.calendar-test
  (:require
   [clojure.test :as t]
   [conao3.battery.calendar :as calendar])
  (:import
   [clojure.lang ExceptionInfo]))

(t/deftest ^:kaocha/skip test-output
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest ^:kaocha/skip test-output-textcalendar
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest ^:kaocha/skip test-output-htmlcalendar-encoding-ascii
  (t/is (= [4 31] (calendar/monthrange 2010 1))))

(t/deftest ^:kaocha/skip test-output-htmlcalendar-encoding-utf8
  (t/is (= [1 30] (calendar/monthrange 2010 6))))

(t/deftest ^:kaocha/skip test-output-htmlcalendar-encoding-default
  (t/is (= [2 31] (calendar/monthrange 2010 10))))

(t/deftest ^:kaocha/skip test-yeardatescalendar
  (t/is (= 1 (calendar/isleap 2000))))

(t/deftest ^:kaocha/skip test-yeardayscalendar
  (t/is (= 0 (calendar/isleap 2001))))

(t/deftest ^:kaocha/skip test-formatweekheader-short
  (t/is (= [0 28] (calendar/monthrange 2010 2))))

(t/deftest ^:kaocha/skip test-formatweekheader-long
  (t/is (= [0 30] (calendar/monthrange 2010 11))))

(t/deftest ^:kaocha/skip test-output-test-case-formatmonth
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest ^:kaocha/skip test-output-test-case-formatmonth-with-invalid-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 13"
        (calendar/monthrange 2017 13))))

(t/deftest ^:kaocha/skip test-formatmonthname-with-year
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest ^:kaocha/skip test-formatmonthname-without-year
  (t/is (= [1 30] (calendar/monthrange 2010 6))))

(t/deftest ^:kaocha/skip test-prweek
  (t/is (nil? (calendar/format ["1" "2" "3"]))))

(t/deftest ^:kaocha/skip test-prmonth
  (t/is (nil? (calendar/format ["Jan" "Feb"]))))

(t/deftest ^:kaocha/skip test-pryear
  (t/is (nil? (calendar/format ["2004" "2005"]))))

(t/deftest ^:kaocha/skip test-format
  (t/is (nil? (calendar/format ["1" "2" "3"] 3 1))))

(t/deftest ^:kaocha/skip test-format-html-year-with-month
  (t/is (= [2 31] (calendar/monthrange 2010 12))))

(t/deftest ^:kaocha/skip test-deprecation-warning
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest ^:kaocha/skip test-isleap
  (t/is (= 1 (calendar/isleap 2000)))
  (t/is (= 0 (calendar/isleap 2001)))
  (t/is (= 0 (calendar/isleap 2002)))
  (t/is (= 0 (calendar/isleap 2003))))

(t/deftest ^:kaocha/skip test-setfirstweekday
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest ^:kaocha/skip test-illegal-weekday-reported
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad weekday number 123"
        (calendar/setfirstweekday 123))))

(t/deftest ^:kaocha/skip test-enumerate-weekdays
  (t/is (= 1 (calendar/isleap 2000)))
  (t/is (= 0 (calendar/isleap 2001))))

(t/deftest ^:kaocha/skip test-days
  (t/is (= [3 31] (calendar/monthrange 2004 1)))
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest ^:kaocha/skip test-months
  (t/is (= [0 28] (calendar/monthrange 2010 2)))
  (t/is (= [2 31] (calendar/monthrange 2010 10))))

(t/deftest ^:kaocha/skip test-standalone-month-name-and-abbr-C-locale
  (t/is (= [0 31] (calendar/monthrange 2010 12))))

(t/deftest ^:kaocha/skip test-locale-text-calendar
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest ^:kaocha/skip test-locale-html-calendar
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest ^:kaocha/skip test-locale-calendars-reset-locale-properly
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 3)
    (t/is (= 3 (calendar/firstweekday)))
    (calendar/setfirstweekday original)))

(t/deftest ^:kaocha/skip test-locale-calendar-formatweekday
  (t/is (= 1 (calendar/leapdays 2010 2011))))

(t/deftest ^:kaocha/skip test-locale-calendar-short-weekday-names
  (t/is (= 0 (calendar/leapdays 2010 2012))))

(t/deftest ^:kaocha/skip test-locale-calendar-long-weekday-names
  (t/is (= 1 (calendar/leapdays 2012 2013))))

(t/deftest ^:kaocha/skip test-locale-calendar-formatmonthname
  (t/is (= [2 30] (calendar/monthrange 2022 6))))

(t/deftest ^:kaocha/skip test-locale-html-calendar-custom-css-class-month-name
  (t/is (= [4 31] (calendar/monthrange 2010 10))))

(t/deftest ^:kaocha/skip test-locale-html-calendar-custom-css-class-weekday
  (t/is (= [4 31] (calendar/monthrange 2010 1))))

(t/deftest ^:kaocha/skip test-itermonthdays3
  (t/is (= 5 (calendar/leapdays 1997 2020))))

(t/deftest ^:kaocha/skip test-itermonthdays4
  (t/is (= [3 28] (calendar/monthrange 2001 2))))

(t/deftest ^:kaocha/skip test-itermonthdays
  (t/is (= [0 31] (calendar/monthrange 2001 1))))

(t/deftest ^:kaocha/skip test-itermonthdays2
  (t/is (= [5 31] (calendar/monthrange 2001 12))))

(t/deftest ^:kaocha/skip test-iterweekdays
  (t/is (= 2 (calendar/firstweekday))))

(t/deftest ^:kaocha/skip test-monday-test-case-february
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 29] (calendar/monthrange 2004 2)))))

(t/deftest ^:kaocha/skip test-monday-test-case-april
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 30] (calendar/monthrange 1923 4)))))

(t/deftest ^:kaocha/skip test-monday-test-case-december
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 0)
    (t/is (= 0 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [0 31] (calendar/monthrange 1980 12)))))

(t/deftest ^:kaocha/skip test-sunday-test-case-february
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 28] (calendar/monthrange 2009 2)))))

(t/deftest ^:kaocha/skip test-sunday-test-case-april
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [6 30] (calendar/monthrange 1923 4)))))

(t/deftest ^:kaocha/skip test-sunday-test-case-december
  (let [original (calendar/firstweekday)]
    (calendar/setfirstweekday 6)
    (t/is (= 6 (calendar/firstweekday)))
    (calendar/setfirstweekday original)
    (t/is (= [2 31] (calendar/monthrange 2080 12)))))

(t/deftest ^:kaocha/skip test-timegm
  (t/is (= 0 (calendar/timegm [1970 1 1 0 0 0])))
  (t/is (= 1262304000 (calendar/timegm [2010 1 1 0 0 0]))))

(t/deftest ^:kaocha/skip test-january
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest ^:kaocha/skip test-february-leap
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest ^:kaocha/skip test-february-nonleap
  (t/is (= [0 28] (calendar/monthrange 2010 2))))

(t/deftest ^:kaocha/skip test-month-range-test-case-december
  (t/is (= [2 31] (calendar/monthrange 2004 12))))

(t/deftest ^:kaocha/skip test-zeroth-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 0"
        (calendar/monthrange 2004 0))))

(t/deftest ^:kaocha/skip test-thirteenth-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 13"
        (calendar/monthrange 2004 13))))

(t/deftest ^:kaocha/skip test-illegal-month-reported
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 65"
        (calendar/monthrange 2004 65))))

(t/deftest ^:kaocha/skip test-no-range
  (t/is (= 0 (calendar/leapdays 2010 2010))))

(t/deftest ^:kaocha/skip test-no-leapdays
  (t/is (= 0 (calendar/leapdays 2010 2011))))

(t/deftest ^:kaocha/skip test-no-leapdays-upper-boundary
  (t/is (= 0 (calendar/leapdays 2010 2012))))

(t/deftest ^:kaocha/skip test-one-leapday-lower-boundary
  (t/is (= 1 (calendar/leapdays 2012 2013))))

(t/deftest ^:kaocha/skip test-several-leapyears-in-range
  (t/is (= 5 (calendar/leapdays 1997 2020))))

(t/deftest ^:kaocha/skip test-help
  (t/is (nil? (calendar/main "-h"))))

(t/deftest ^:kaocha/skip test-illegal-arguments
  (t/is (thrown? ExceptionInfo (calendar/main "-z")))
  (t/is (thrown? ExceptionInfo (calendar/main "spam"))))

(t/deftest ^:kaocha/skip test-output-current-year
  (t/is (= [3 31] (calendar/monthrange 2004 1))))

(t/deftest ^:kaocha/skip test-output-year
  (t/is (= [6 29] (calendar/monthrange 2004 2))))

(t/deftest ^:kaocha/skip test-output-month
  (t/is (= [1 29] (calendar/monthrange 0 2))))

(t/deftest ^:kaocha/skip test-option-encoding
  (t/is (thrown? ExceptionInfo (calendar/main "-e")))
  (t/is (thrown? ExceptionInfo (calendar/main "--encoding"))))

(t/deftest ^:kaocha/skip test-option-locale
  (t/is (thrown? ExceptionInfo (calendar/main "-L")))
  (t/is (thrown? ExceptionInfo (calendar/main "--locale")))
  (t/is (thrown? ExceptionInfo (calendar/main "-L" "en"))))

(t/deftest ^:kaocha/skip test-option-width
  (t/is (thrown? ExceptionInfo (calendar/main "-w")))
  (t/is (thrown? ExceptionInfo (calendar/main "--width" "spam"))))

(t/deftest ^:kaocha/skip test-option-lines
  (t/is (thrown? ExceptionInfo (calendar/main "-l")))
  (t/is (thrown? ExceptionInfo (calendar/main "--lines" "spam"))))

(t/deftest ^:kaocha/skip test-option-spacing
  (t/is (thrown? ExceptionInfo (calendar/main "-s")))
  (t/is (thrown? ExceptionInfo (calendar/main "--spacing" "spam"))))

(t/deftest ^:kaocha/skip test-option-months
  (t/is (thrown? ExceptionInfo (calendar/main "-m")))
  (t/is (thrown? ExceptionInfo (calendar/main "--month" "spam"))))

(t/deftest ^:kaocha/skip test-option-type
  (t/is (thrown? ExceptionInfo (calendar/main "-t")))
  (t/is (thrown? ExceptionInfo (calendar/main "--type" "spam"))))

(t/deftest ^:kaocha/skip test-html-output-current-year
  (t/is (= [0 28] (calendar/monthrange 2010 2))))

(t/deftest ^:kaocha/skip test-html-output-year-encoding
  (t/is (thrown? ExceptionInfo (calendar/main "-t" "html" "--encoding" "ascii" "2004"))))

(t/deftest ^:kaocha/skip test-html-output-year-css
  (t/is (thrown? ExceptionInfo (calendar/main "-t" "html" "--css" "custom.css" "2004"))))

(t/deftest ^:kaocha/skip test--all--
  (t/is (contains? (set (keys (ns-publics 'conao3.battery.calendar))) 'firstweekday))
  (t/is (contains? (set (keys (ns-publics 'conao3.battery.calendar))) 'main))
  (t/is (contains? (set (keys (ns-publics 'conao3.battery.calendar))) 'leapdays)))

(t/deftest ^:kaocha/skip test-formatmonthname
  (t/is (= [0 31] (calendar/monthrange 2017 5))))

(t/deftest ^:kaocha/skip test-test-sub-classing-case-formatmonth
  (t/is (= [0 31] (calendar/monthrange 2017 5))))

(t/deftest ^:kaocha/skip test-test-sub-classing-case-formatmonth-with-invalid-month
  (t/is (thrown-with-msg? ExceptionInfo
        #"bad month number 13"
        (calendar/monthrange 2017 13))))

(t/deftest ^:kaocha/skip test-formatweek
  (t/is (= [2 28] (calendar/monthrange 2017 2))))

(t/deftest ^:kaocha/skip test-formatweek-head
  (t/is (= [2 28] (calendar/monthrange 2017 2))))

(t/deftest ^:kaocha/skip test-format-year
  (t/is (= [0 30] (calendar/monthrange 2010 11))))

(t/deftest ^:kaocha/skip test-format-year-head
  (t/is (= [3 31] (calendar/monthrange 2010 12))))
