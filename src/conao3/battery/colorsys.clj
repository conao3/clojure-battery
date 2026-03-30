(ns conao3.battery.colorsys)

(defn rgb-to-hsv [r g b]
  (let [maxc (max r g b)
        minc (min r g b)]
    (if (= maxc minc)
      [0 0.0 (double maxc)]
      (let [s (/ (- maxc minc) (double maxc))
            rc (/ (- maxc r) (double (- maxc minc)))
            gc (/ (- maxc g) (double (- maxc minc)))
            bc (/ (- maxc b) (double (- maxc minc)))
            h (cond
                (= r maxc) (- bc gc)
                (= g maxc) (+ 2.0 rc (- bc))
                :else (+ 4.0 gc (- rc)))]
        [(mod (/ (double h) 6.0) 1.0) s (double maxc)]))))

(defn hsv-to-rgb [h s v]
  (if (= s 0)
    [(double v) (double v) (double v)]
    (let [i (int (* (double h) 6))
          f (- (* (double h) 6) i)
          p (* (double v) (- 1 (double s)))
          q (* (double v) (- 1 (* (double s) f)))
          t (* (double v) (- 1 (* (double s) (- 1 f))))]
      (case (mod i 6)
        0 [(double v) t p]
        1 [q (double v) p]
        2 [p (double v) t]
        3 [p q (double v)]
        4 [t p (double v)]
        5 [(double v) p q]))))

(defn rgb-to-hls [r g b]
  (let [maxc (max r g b)
        minc (min r g b)
        sumc (+ (double maxc) (double minc))
        rangec (- (double maxc) (double minc))
        l (/ sumc 2.0)]
    (if (= minc maxc)
      [0.0 l 0.0]
      (let [s (if (<= l 0.5)
                (/ rangec sumc)
                (/ rangec (- 2.0 (double maxc) (double minc))))
            rc (/ (- (double maxc) r) rangec)
            gc (/ (- (double maxc) g) rangec)
            bc (/ (- (double maxc) b) rangec)
            h (cond
                (= r maxc) (- bc gc)
                (= g maxc) (+ 2.0 rc (- bc))
                :else (+ 4.0 gc (- rc)))]
        [(mod (/ h 6.0) 1.0) l s]))))

(defn- hls-v [m1 m2 hue]
  (let [hue (mod (double hue) 1.0)]
    (cond
      (< hue (/ 1.0 6)) (+ m1 (* (- m2 m1) hue 6.0))
      (< hue 0.5) m2
      (< hue (/ 2.0 3)) (+ m1 (* (- m2 m1) (- (/ 2.0 3) hue) 6.0))
      :else m1)))

(defn hls-to-rgb [h l s]
  (if (= s 0)
    [(double l) (double l) (double l)]
    (let [m2 (if (<= (double l) 0.5)
               (* (double l) (+ 1.0 (double s)))
               (- (+ (double l) (double s)) (* (double l) (double s))))
          m1 (- (* 2.0 (double l)) m2)]
      [(hls-v m1 m2 (+ (double h) (/ 1.0 3)))
       (hls-v m1 m2 (double h))
       (hls-v m1 m2 (- (double h) (/ 1.0 3)))])))

(defn rgb-to-yiq [r g b]
  (let [y (+ (* 0.30 r) (* 0.59 g) (* 0.11 b))]
    [y
     (- (* 0.74 (- r y)) (* 0.27 (- b y)))
     (+ (* 0.48 (- r y)) (* 0.41 (- b y)))]))

(defn yiq-to-rgb [y i q]
  [(min 1.0 (max 0.0 (+ y (* 0.9468822170900693 i) (* 0.6235565819861433 q))))
   (min 1.0 (max 0.0 (+ y (- (* 0.27478764629897834 i)) (- (* 0.6356910791873801 q)))))
   (min 1.0 (max 0.0 (+ y (- (* 1.1085450346420322 i)) (* 1.7090069284064666 q))))])
