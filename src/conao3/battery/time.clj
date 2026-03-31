(ns conao3.battery.time
  (:import
   [java.time Instant LocalDateTime ZonedDateTime ZoneId ZoneOffset]
   [java.time.format DateTimeFormatter]
   [java.util Calendar TimeZone]))

(defrecord StructTime
  [tm_year tm_mon tm_mday tm_hour tm_min tm_sec tm_wday tm_yday tm_isdst])

(defn- strftime-to-java [fmt]
  (-> fmt
      (clojure.string/replace "%Y" "yyyy")
      (clojure.string/replace "%m" "MM")
      (clojure.string/replace "%d" "dd")
      (clojure.string/replace "%H" "HH")
      (clojure.string/replace "%M" "mm")
      (clojure.string/replace "%S" "ss")
      (clojure.string/replace "%j" "DDD")
      (clojure.string/replace "%A" "EEEE")
      (clojure.string/replace "%a" "EEE")
      (clojure.string/replace "%B" "MMMM")
      (clojure.string/replace "%b" "MMM")
      (clojure.string/replace "%p" "a")
      (clojure.string/replace "%I" "hh")
      (clojure.string/replace "%Z" "z")
      (clojure.string/replace "%%" "%")))

(defn- calendar->struct-time [^Calendar cal]
  (let [year    (.get cal Calendar/YEAR)
        month   (.get cal Calendar/MONTH)
        mday    (.get cal Calendar/DAY_OF_MONTH)
        hour    (.get cal Calendar/HOUR_OF_DAY)
        minute  (.get cal Calendar/MINUTE)
        second  (.get cal Calendar/SECOND)
        dow     (.get cal Calendar/DAY_OF_WEEK)
        doy     (.get cal Calendar/DAY_OF_YEAR)
        wday    (mod (- dow 2) 7)
        dst     (.inDaylightTime (.getTimeZone cal) (.getTime cal))]
    (->StructTime year (inc month) mday hour minute second wday doy (if dst 1 0))))

(defn time []
  (/ (System/currentTimeMillis) 1000.0))

(defn monotonic []
  (/ (System/nanoTime) 1000000000.0))

(defn perf-counter []
  (/ (System/nanoTime) 1000000000.0))

(defn process-time []
  (let [bean (java.lang.management.ManagementFactory/getThreadMXBean)]
    (/ (.getCurrentThreadCpuTime bean) 1000000000.0)))

(defn sleep [secs]
  (Thread/sleep (long (* secs 1000))))

(defn gmtime
  ([] (gmtime (time)))
  ([secs]
   (let [cal (doto (Calendar/getInstance (TimeZone/getTimeZone "UTC"))
               (.setTimeInMillis (long (* secs 1000))))]
     (calendar->struct-time cal))))

(defn localtime
  ([] (localtime (time)))
  ([secs]
   (let [cal (doto (Calendar/getInstance)
               (.setTimeInMillis (long (* secs 1000))))]
     (calendar->struct-time cal))))

(defn mktime [^StructTime st]
  (let [cal (doto (Calendar/getInstance)
              (.set (:tm_year st) (dec (:tm_mon st)) (:tm_mday st)
                    (:tm_hour st) (:tm_min st) (:tm_sec st))
              (.set Calendar/MILLISECOND 0))]
    (/ (.getTimeInMillis cal) 1000.0)))

(defn strftime [fmt ^StructTime st]
  (let [cal (doto (Calendar/getInstance (TimeZone/getTimeZone "UTC"))
              (.set (:tm_year st) (dec (:tm_mon st)) (:tm_mday st)
                    (:tm_hour st) (:tm_min st) (:tm_sec st))
              (.set Calendar/MILLISECOND 0))
        ldt (LocalDateTime/ofInstant (.toInstant cal) ZoneOffset/UTC)
        jfmt (DateTimeFormatter/ofPattern (strftime-to-java fmt))]
    (.format ldt jfmt)))

(defn strptime [s fmt]
  (let [jfmt (DateTimeFormatter/ofPattern (strftime-to-java fmt))
        ldt  (try
               (LocalDateTime/parse ^String s jfmt)
               (catch Exception _
                 (.atStartOfDay (java.time.LocalDate/parse ^String s jfmt))))
        cal  (doto (Calendar/getInstance (TimeZone/getTimeZone "UTC"))
               (.set (.getYear ldt) (dec (.getMonthValue ldt)) (.getDayOfMonth ldt)
                     (.getHour ldt) (.getMinute ldt) (.getSecond ldt))
               (.set Calendar/MILLISECOND 0))]
    (calendar->struct-time cal)))

(defn timezone []
  (let [tz (TimeZone/getDefault)]
    (- (.getRawOffset tz) 3600000)))

(defn altzone []
  (let [tz (TimeZone/getDefault)]
    (- (.getRawOffset tz))))

(defn daylight []
  (if (.observesDaylightTime (TimeZone/getDefault)) 1 0))

(def tzname
  (let [tz (TimeZone/getDefault)]
    [(.getDisplayName tz false TimeZone/SHORT)
     (.getDisplayName tz true TimeZone/SHORT)]))

(def ^:const CLOCK_REALTIME  0)
(def ^:const CLOCK_MONOTONIC 1)
(def ^:const CLOCK_PROCESS_CPUTIME_ID 2)
(def ^:const CLOCK_THREAD_CPUTIME_ID  3)
