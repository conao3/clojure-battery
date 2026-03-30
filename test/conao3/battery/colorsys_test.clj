;; Original: Lib/test/test_colorsys.py

(ns conao3.battery.colorsys-test
  (:require
   [clojure.test :as t]
   [conao3.battery.colorsys :as colorsys]))

(defn- frange [start stop step]
  (take-while #(<= % stop) (iterate (partial + step) start)))

(defn- assert-triple-equal [tr1 tr2]
  (t/is (= 3 (count tr1)))
  (t/is (= 3 (count tr2)))
  (t/is (< (abs (- (nth tr1 0) (nth tr2 0))) 1e-7))
  (t/is (< (abs (- (nth tr1 1) (nth tr2 1))) 1e-7))
  (t/is (< (abs (- (nth tr1 2) (nth tr2 2))) 1e-7)))

(t/deftest test-hsv-roundtrip
  (doseq [r (frange 0.0 1.0 0.2)
          g (frange 0.0 1.0 0.2)
          b (frange 0.0 1.0 0.2)]
    (let [rgb [r g b]
          hsv (apply colorsys/rgb-to-hsv rgb)]
      (assert-triple-equal rgb (apply colorsys/hsv-to-rgb hsv)))))

(t/deftest test-hsv-values
  (let [values
        [[ [0.0 0.0 0.0] [0 0.0 0.0]]
         [[0.0 0.0 1.0] [4/6 1.0 1.0]]
         [[0.0 1.0 0.0] [2/6 1.0 1.0]]
         [[0.0 1.0 1.0] [3/6 1.0 1.0]]
         [[1.0 0.0 0.0] [0 1.0 1.0]]
         [[1.0 0.0 1.0] [5/6 1.0 1.0]]
         [[1.0 1.0 0.0] [1/6 1.0 1.0]]
         [[1.0 1.0 1.0] [0 0.0 1.0]]
         [[0.5 0.5 0.5] [0 0.0 0.5]]]]
    (doseq [[rgb hsv] values]
      (assert-triple-equal hsv (apply colorsys/rgb-to-hsv rgb))
      (assert-triple-equal rgb (apply colorsys/hsv-to-rgb hsv)))))

(t/deftest test-hls-roundtrip
  (doseq [r (frange 0.0 1.0 0.2)
          g (frange 0.0 1.0 0.2)
          b (frange 0.0 1.0 0.2)]
    (let [rgb [r g b]
          hls (apply colorsys/rgb-to-hls rgb)]
      (assert-triple-equal rgb (apply colorsys/hls-to-rgb hls)))))

(t/deftest test-hls-values
  (let [values
        [[ [0.0 0.0 0.0] [0 0.0 0.0]]
         [[0.0 0.0 1.0] [4/6 1/2 1.0]]
         [[0.0 1.0 0.0] [2/6 1/2 1.0]]
         [[0.0 1.0 1.0] [3/6 1/2 1.0]]
         [[1.0 0.0 0.0] [0 1/2 1.0]]
         [[1.0 0.0 1.0] [5/6 1/2 1.0]]
         [[1.0 1.0 0.0] [1/6 1/2 1.0]]
         [[1.0 1.0 1.0] [0 1.0 0.0]]
         [[0.5 0.5 0.5] [0 1/2 0.0]]]]
    (doseq [[rgb hls] values]
      (assert-triple-equal hls (apply colorsys/rgb-to-hls rgb))
      (assert-triple-equal rgb (apply colorsys/hls-to-rgb hls)))))

(t/deftest ^:kaocha/skip test-hls-nearwhite
  (let [values
        [[[0.9999999999999999 1 1] [1/2 1.0 1.0]]
         [[1 0.9999999999999999 0.9999999999999999] [0 1.0 1.0]]]]
    (doseq [[rgb hls] values]
      (assert-triple-equal hls (apply colorsys/rgb-to-hls rgb))
      (assert-triple-equal [1.0 1.0 1.0] (apply colorsys/hls-to-rgb hls)))))

(t/deftest test-yiq-roundtrip
  (doseq [r (frange 0.0 1.0 0.2)
          g (frange 0.0 1.0 0.2)
          b (frange 0.0 1.0 0.2)]
    (let [rgb [r g b]
          yiq (apply colorsys/rgb-to-yiq rgb)]
      (assert-triple-equal rgb (apply colorsys/yiq-to-rgb yiq)))))

(t/deftest test-yiq-values
  (let [values
        [[ [0.0 0.0 0.0] [0.0 0.0 0.0]]
         [[0.0 0.0 1.0] [0.11 -0.3217 0.3121]]
         [[0.0 1.0 0.0] [0.59 -0.2773 -0.5251]]
         [[0.0 1.0 1.0] [0.7 -0.599 -0.213]]
         [[1.0 0.0 0.0] [0.3 0.599 0.213]]
         [[1.0 0.0 1.0] [0.41 0.2773 0.5251]]
         [[1.0 1.0 0.0] [0.89 0.3217 -0.3121]]
         [[1.0 1.0 1.0] [1.0 0.0 0.0]]
         [[0.5 0.5 0.5] [0.5 0.0 0.0]]]]
    (doseq [[rgb yiq] values]
      (assert-triple-equal yiq (apply colorsys/rgb-to-yiq rgb))
      (assert-triple-equal rgb (apply colorsys/yiq-to-rgb yiq)))))
