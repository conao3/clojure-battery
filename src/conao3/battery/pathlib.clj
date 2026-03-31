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

(defn name [path]
  (let [p (str path)
        idx (str/last-index-of p "/")]
    (if (nil? idx)
      p
      (subs p (inc idx)))))

(defn parent [path]
  (let [p (str path)
        idx (str/last-index-of p "/")]
    (cond
      (nil? idx) "."
      (zero? idx) "/"
      :else (subs p 0 idx))))

(defn stem [path]
  (let [n (name path)
        idx (str/last-index-of n ".")]
    (if (or (nil? idx) (zero? idx))
      n
      (subs n 0 idx))))

(defn suffix [path]
  (let [n (name path)
        idx (str/last-index-of n ".")]
    (if (or (nil? idx) (zero? idx))
      ""
      (subs n idx))))

(defn suffixes [path]
  (let [n (name path)
        parts (str/split n #"(?=\.)")]
    (if (> (count parts) 1)
      (vec (rest parts))
      [])))

(defn parts [path]
  (let [p (str path)]
    (if (str/starts-with? p "/")
      (into ["/"] (remove empty? (str/split (subs p 1) #"/")))
      (vec (remove empty? (str/split p #"/"))))))

(defn is-absolute? [path]
  (str/starts-with? (str path) "/"))

(defn with-name [path new-name]
  (let [par (parent path)]
    (if (= par ".")
      new-name
      (str par "/" new-name))))

(defn with-suffix [path new-suffix]
  (let [par (parent path)
        base (str (stem path) new-suffix)]
    (if (= par ".")
      base
      (str par "/" base))))
