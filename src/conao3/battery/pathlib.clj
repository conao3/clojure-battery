(ns conao3.battery.pathlib)

(def is-pypi false)
(def windows? false)

(defn lexical-posix-path [path]
  (str path))

(defn pure-posix-path [path]
  (str path))

(defn posix-path [path]
  (str path))

(defn joinpath
  ([path segment]
   (throw (ex-info "Not implemented" {})))
  ([path segment segment2]
   (throw (ex-info "Not implemented" {})))
  ([path segment segment2 segment3]
   (throw (ex-info "Not implemented" {}))))

(defn divpath [path segment]
  (throw (ex-info "Not implemented" {})))
