(ns conao3.battery.statistics)

(defn mean
  "Return arithmetic mean of data."
  [data]
  (throw (ex-info "Not implemented" {})))

(defn fmean
  "Return arithmetic mean as a float."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data weights] (throw (ex-info "Not implemented" {}))))

(defn geometric-mean
  "Return geometric mean of data."
  [data]
  (throw (ex-info "Not implemented" {})))

(defn harmonic-mean
  "Return harmonic mean of data."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data weights] (throw (ex-info "Not implemented" {}))))

(defn median
  "Return median (middle value) of data."
  [data]
  (throw (ex-info "Not implemented" {})))

(defn median-low
  "Return low median of data."
  [data]
  (throw (ex-info "Not implemented" {})))

(defn median-high
  "Return high median of data."
  [data]
  (throw (ex-info "Not implemented" {})))

(defn median-grouped
  "Return median of grouped continuous data using interpolation."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data interval] (throw (ex-info "Not implemented" {}))))

(defn mode
  "Return most common data point from data."
  [data]
  (throw (ex-info "Not implemented" {})))

(defn multimode
  "Return list of most frequently occurring values."
  [data]
  (throw (ex-info "Not implemented" {})))

(defn pvariance
  "Return population variance of data."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data mu] (throw (ex-info "Not implemented" {}))))

(defn variance
  "Return sample variance of data."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data xbar] (throw (ex-info "Not implemented" {}))))

(defn pstdev
  "Return population standard deviation of data."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data mu] (throw (ex-info "Not implemented" {}))))

(defn stdev
  "Return sample standard deviation of data."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data xbar] (throw (ex-info "Not implemented" {}))))

(defn quantiles
  "Divide data into n continuous intervals with equal probability."
  ([data] (throw (ex-info "Not implemented" {})))
  ([data opts] (throw (ex-info "Not implemented" {}))))

(defn correlation
  "Return Pearson's correlation coefficient for two inputs."
  ([x y] (throw (ex-info "Not implemented" {})))
  ([x y method] (throw (ex-info "Not implemented" {}))))

(defn covariance
  "Return sample covariance of two inputs."
  [x y]
  (throw (ex-info "Not implemented" {})))

(defn linear-regression
  "Return the slope and intercept of simple linear regression."
  ([x y] (throw (ex-info "Not implemented" {})))
  ([x y proportional] (throw (ex-info "Not implemented" {}))))
