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
  (let [cal (doto (java.util.GregorianCalendar.)
              (.set year (dec month) 1))
        dow (.get cal java.util.Calendar/DAY_OF_WEEK)
        weekday (mod (- dow 2) 7)
        days (.getActualMaximum cal java.util.Calendar/DAY_OF_MONTH)]
    [weekday days]))

(defn leapdays
  [y1 y2]
  (count (filter isleap (range y1 y2))))

(defn timegm
  [& _args]
  (throw (ex-info "Not implemented" {})))

(defn format
  [& _args]
  (throw (ex-info "Not implemented" {})))

(defn main
  [& _args]
  (throw (ex-info "Not implemented" {})))
