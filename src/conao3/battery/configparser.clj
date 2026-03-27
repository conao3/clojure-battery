(ns conao3.battery.configparser)

(defn read-string
  ([s] (throw (ex-info "Not implemented" {}))))

(defn sections
  ([cfg] (throw (ex-info "Not implemented" {}))))

(defn items
  ([cfg section] (throw (ex-info "Not implemented" {}))))

(defn get
  ([cfg section key] (throw (ex-info "Not implemented" {})))
  ([cfg section key fallback] (throw (ex-info "Not implemented" {}))))

(defn get-int
  ([cfg section key] (throw (ex-info "Not implemented" {})))
  ([cfg section key fallback] (throw (ex-info "Not implemented" {}))))

(defn get-float
  ([cfg section key] (throw (ex-info "Not implemented" {})))
  ([cfg section key fallback] (throw (ex-info "Not implemented" {}))))

(defn get-boolean
  ([cfg section key] (throw (ex-info "Not implemented" {})))
  ([cfg section key fallback] (throw (ex-info "Not implemented" {}))))

(defn has-section
  ([cfg section] (throw (ex-info "Not implemented" {}))))

(defn has-option
  ([cfg section key] (throw (ex-info "Not implemented" {}))))

(defn remove-section
  ([cfg section] (throw (ex-info "Not implemented" {}))))

(defn remove-option
  ([cfg section key] (throw (ex-info "Not implemented" {}))))
