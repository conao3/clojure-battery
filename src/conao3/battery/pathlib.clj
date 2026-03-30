(ns conao3.battery.pathlib
  (:require [clojure.string :as str]))

(def is-pypi false)
(def windows? false)

(defn lexical-posix-path [path]
  (str path))

(defn pure-posix-path [path]
  (str path))

(defn posix-path [path]
  (str path))

(defn joinpath [path & segments]
  (reduce (fn [base seg]
            (if (str/starts-with? seg "/")
              seg
              (str base "/" seg)))
          path
          segments))

(defn divpath [path segment]
  (joinpath path segment))
