(ns conao3.battery.calendar)

(def ^:private _firstweekday (atom 0))

(defn firstweekday
  []
  @_firstweekday)

(defn setfirstweekday
  [weekday]
  (when (or (< weekday 0) (> weekday 6))
    (throw (ex-info (str "bad weekday number " weekday) {})))
  (reset! _firstweekday weekday))

(defn isleap
  [year]
  (or (and (zero? (mod year 4))
           (not (zero? (mod year 100))))
      (zero? (mod year 400))))

(defn monthrange
  [year month]
  (when (or (< month 1) (> month 12))
    (throw (ex-info (str "bad month number " month) {})))
  (let [date (java.time.LocalDate/of year month 1)
        dow (.getValue (.getDayOfWeek date))
        weekday (mod (- dow 1) 7)
        days (.lengthOfMonth date)]
    [weekday days]))

(defn leapdays
  [y1 y2]
  (count (filter isleap (range y1 y2))))

(defn timegm
  [[year month day hour minute second]]
  (let [cal (doto (java.util.GregorianCalendar. (java.util.TimeZone/getTimeZone "UTC"))
              (.set year (dec month) day hour minute second)
              (.set java.util.Calendar/MILLISECOND 0))]
    (quot (.getTimeInMillis cal) 1000)))

(defn format
  [& _args]
  nil)

(defn main
  [& args]
  (when-not (some #{"-h" "--help"} args)
    (throw (ex-info (str "main: " (first args)) {})))
  nil)
