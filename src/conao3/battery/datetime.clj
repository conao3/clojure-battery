(ns conao3.battery.datetime
  (:import
   [java.time LocalDate LocalTime LocalDateTime ZoneId]
   [java.time.format DateTimeFormatter]
   [java.time.temporal ChronoUnit]))

(defn- strftime-to-java [fmt]
  (-> fmt
      (clojure.string/replace "%Y" "yyyy")
      (clojure.string/replace "%m" "MM")
      (clojure.string/replace "%d" "dd")
      (clojure.string/replace "%H" "HH")
      (clojure.string/replace "%M" "mm")
      (clojure.string/replace "%S" "ss")
      (clojure.string/replace "%f" "SSSSSS")
      (clojure.string/replace "%A" "EEEE")
      (clojure.string/replace "%a" "EEE")
      (clojure.string/replace "%B" "MMMM")
      (clojure.string/replace "%b" "MMM")
      (clojure.string/replace "%j" "DDD")
      (clojure.string/replace "%p" "a")
      (clojure.string/replace "%I" "hh")
      (clojure.string/replace "%Z" "z")
      (clojure.string/replace "%%" "%")))

(defprotocol IDate
  (get-year  [this])
  (get-month [this])
  (get-day   [this])
  (to-local-date [this]))

(defprotocol ITime
  (get-hour        [this])
  (get-minute      [this])
  (get-second      [this])
  (get-microsecond [this]))

(defrecord Date [^LocalDate _ld]
  IDate
  (get-year  [_] (.getYear _ld))
  (get-month [_] (.getMonthValue _ld))
  (get-day   [_] (.getDayOfMonth _ld))
  (to-local-date [_] _ld))

(defrecord Time [^LocalTime _lt]
  ITime
  (get-hour        [_] (.getHour _lt))
  (get-minute      [_] (.getMinute _lt))
  (get-second      [_] (.getSecond _lt))
  (get-microsecond [_] (quot (.getNano _lt) 1000)))

(defrecord DateTime [^LocalDateTime _ldt]
  IDate
  (get-year  [_] (.getYear _ldt))
  (get-month [_] (.getMonthValue _ldt))
  (get-day   [_] (.getDayOfMonth _ldt))
  (to-local-date [_] (.toLocalDate _ldt))
  ITime
  (get-hour        [_] (.getHour _ldt))
  (get-minute      [_] (.getMinute _ldt))
  (get-second      [_] (.getSecond _ldt))
  (get-microsecond [_] (quot (.getNano _ldt) 1000)))

(defrecord Timedelta [days seconds microseconds])

(defn- normalize-timedelta [days seconds microseconds]
  (let [total-us   (+ (* (long days) 86400000000)
                      (* (long seconds) 1000000)
                      (long microseconds))
        d  (Math/floorDiv total-us 86400000000)
        r  (Math/floorMod total-us 86400000000)
        s  (Math/floorDiv r 1000000)
        us (Math/floorMod r 1000000)]
    (->Timedelta d s us)))

(defn timedelta
  ([] (->Timedelta 0 0 0))
  ([days] (normalize-timedelta days 0 0))
  ([days seconds] (normalize-timedelta days seconds 0))
  ([days seconds microseconds] (normalize-timedelta days seconds microseconds))
  ([days seconds microseconds milliseconds] (normalize-timedelta days (+ seconds milliseconds) microseconds))
  ([days seconds microseconds milliseconds minutes]
   (normalize-timedelta days (+ seconds (* 60 minutes) milliseconds) microseconds))
  ([days seconds microseconds milliseconds minutes hours]
   (normalize-timedelta days (+ seconds (* 60 minutes) (* 3600 hours) milliseconds) microseconds))
  ([days seconds microseconds milliseconds minutes hours weeks]
   (normalize-timedelta (+ days (* 7 weeks)) (+ seconds (* 60 minutes) (* 3600 hours) milliseconds) microseconds)))

(defn timedelta-total-seconds [^Timedelta td]
  (+ (* (:days td) 86400.0)
     (:seconds td)
     (/ (:microseconds td) 1000000.0)))

(defn date
  ([year month day] (->Date (LocalDate/of year month day)))
  ([^LocalDate ld] (->Date ld)))

(defn date-today []
  (->Date (LocalDate/now (ZoneId/systemDefault))))

(defn date-fromordinal [n]
  (->Date (.plusDays (LocalDate/of 1 1 1) (- n 1))))

(defn date-fromtimestamp [ts]
  (->Date (LocalDate/ofEpochDay (long (/ ts 86400)))))

(defn date-fromisoformat [s]
  (->Date (LocalDate/parse ^String s)))

(defn date-toordinal [^Date d]
  (+ 1 (.until (LocalDate/of 1 1 1) ^LocalDate (:_ld d) ChronoUnit/DAYS)))

(defn date-weekday [^Date d]
  (dec (.getValue (.getDayOfWeek ^LocalDate (:_ld d)))))

(defn date-isoweekday [^Date d]
  (.getValue (.getDayOfWeek ^LocalDate (:_ld d))))

(defn date-isoformat [^Date d]
  (.format ^LocalDate (:_ld d) DateTimeFormatter/ISO_LOCAL_DATE))

(defn date-strftime [^Date d fmt]
  (.format ^LocalDate (:_ld d) (DateTimeFormatter/ofPattern (strftime-to-java fmt))))

(defn date-replace [^Date d & {:keys [year month day]}]
  (->Date (.withYear (.withMonth (.withDayOfMonth ^LocalDate (:_ld d)
                                                  (or day (get-day d)))
                                 (or month (get-month d)))
                     (or year (get-year d)))))

(defn date-sub [^Date d1 ^Date d2]
  (let [days (.until ^LocalDate (:_ld d2) ^LocalDate (:_ld d1) ChronoUnit/DAYS)]
    (->Timedelta days 0 0)))

(defn date-add-timedelta [^Date d ^Timedelta td]
  (->Date (.plusDays ^LocalDate (:_ld d) (long (:days td)))))

(defn date-sub-timedelta [^Date d ^Timedelta td]
  (->Date (.minusDays ^LocalDate (:_ld d) (long (:days td)))))

(defn timedelta-add [^Timedelta td1 ^Timedelta td2]
  (normalize-timedelta (+ (:days td1) (:days td2))
                       (+ (:seconds td1) (:seconds td2))
                       (+ (:microseconds td1) (:microseconds td2))))

(defn timedelta-sub [^Timedelta td1 ^Timedelta td2]
  (normalize-timedelta (- (:days td1) (:days td2))
                       (- (:seconds td1) (:seconds td2))
                       (- (:microseconds td1) (:microseconds td2))))

(defn timedelta-neg [^Timedelta td]
  (normalize-timedelta (- (:days td)) (- (:seconds td)) (- (:microseconds td))))

(defn time
  ([] (->Time (LocalTime/of 0 0 0)))
  ([hour] (->Time (LocalTime/of hour 0 0)))
  ([hour minute] (->Time (LocalTime/of hour minute 0)))
  ([hour minute second] (->Time (LocalTime/of hour minute second)))
  ([hour minute second microsecond]
   (->Time (LocalTime/of hour minute second (* microsecond 1000)))))

(defn time-isoformat [^Time t]
  (let [lt ^LocalTime (:_lt t)]
    (if (pos? (.getNano lt))
      (let [us (quot (.getNano lt) 1000)]
        (format "%02d:%02d:%02d.%06d" (.getHour lt) (.getMinute lt) (.getSecond lt) us))
      (.format lt DateTimeFormatter/ISO_LOCAL_TIME))))

(defn time-strftime [^Time t fmt]
  (.format ^LocalTime (:_lt t) (DateTimeFormatter/ofPattern (strftime-to-java fmt))))

(defn datetime
  ([year month day] (->DateTime (LocalDateTime/of year month day 0 0 0)))
  ([year month day hour] (->DateTime (LocalDateTime/of year month day hour 0 0)))
  ([year month day hour minute] (->DateTime (LocalDateTime/of year month day hour minute 0)))
  ([year month day hour minute second] (->DateTime (LocalDateTime/of year month day hour minute second)))
  ([year month day hour minute second microsecond]
   (->DateTime (LocalDateTime/of year month day hour minute second (* microsecond 1000)))))

(defn datetime-now []
  (->DateTime (LocalDateTime/now (ZoneId/systemDefault))))

(defn datetime-fromisoformat [s]
  (->DateTime (LocalDateTime/parse ^String (clojure.string/replace s " " "T"))))

(defn datetime-strptime [s fmt]
  (->DateTime (LocalDateTime/parse ^String s (DateTimeFormatter/ofPattern (strftime-to-java fmt)))))

(defn datetime-isoformat [^DateTime dt]
  (.format ^LocalDateTime (:_ldt dt) DateTimeFormatter/ISO_LOCAL_DATE_TIME))

(defn datetime-strftime [^DateTime dt fmt]
  (.format ^LocalDateTime (:_ldt dt) (DateTimeFormatter/ofPattern (strftime-to-java fmt))))

(defn datetime-date [^DateTime dt]
  (->Date (.toLocalDate ^LocalDateTime (:_ldt dt))))

(defn datetime-time [^DateTime dt]
  (->Time (.toLocalTime ^LocalDateTime (:_ldt dt))))

(defn datetime-add-timedelta [^DateTime dt ^Timedelta td]
  (->DateTime (.plusNanos (.plusSeconds (.plusDays ^LocalDateTime (:_ldt dt)
                                                   (:days td))
                                        (:seconds td))
                          (* (:microseconds td) 1000))))

(defn datetime-sub-timedelta [^DateTime dt ^Timedelta td]
  (->DateTime (.minusNanos (.minusSeconds (.minusDays ^LocalDateTime (:_ldt dt)
                                                       (:days td))
                                          (:seconds td))
                           (* (:microseconds td) 1000))))

(defn datetime-sub [^DateTime dt1 ^DateTime dt2]
  (let [nanos (.until ^LocalDateTime (:_ldt dt2) ^LocalDateTime (:_ldt dt1) ChronoUnit/NANOS)
        total-us (quot nanos 1000)
        d  (quot total-us 86400000000)
        r  (mod  total-us 86400000000)
        s  (quot r 1000000)
        us (mod  r 1000000)]
    (->Timedelta d s us)))

(def date-min (date 1 1 1))
(def date-max (date 9999 12 31))
(def datetime-min (datetime 1 1 1 0 0 0))
(def datetime-max (datetime 9999 12 31 23 59 59 999999))
(def timedelta-min (->Timedelta -999999999 0 0))
(def timedelta-max (->Timedelta 999999999 86399 999999))
