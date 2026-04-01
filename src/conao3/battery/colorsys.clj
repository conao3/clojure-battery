(ns conao3.battery.colorsys)

(defn rgb-to-hsv [r g b]
  (let [[r g b] (map double [r g b])
        maxc (max r g b)
        minc (min r g b)]
    (if (= maxc minc)
      [0 0.0 maxc]
      (let [diff (- maxc minc)
            s    (/ diff maxc)
            rc   (/ (- maxc r) diff)
            gc   (/ (- maxc g) diff)
            bc   (/ (- maxc b) diff)
            h    (cond
                   (= r maxc) (- bc gc)
                   (= g maxc) (+ 2.0 rc (- bc))
                   :else      (+ 4.0 gc (- rc)))]
        [(mod (/ h 6.0) 1.0) s maxc]))))

(defn hsv-to-rgb [h s v]
  (let [[h s v] (map double [h s v])]
    (if (zero? s)
      [v v v]
      (let [i (long (* h 6))
            f (- (* h 6) i)
            p (* v (- 1.0 s))
            q (* v (- 1.0 (* s f)))
            t (* v (- 1.0 (* s (- 1.0 f))))]
        (case (mod i 6)
          0 [v t p]
          1 [q v p]
          2 [p v t]
          3 [p q v]
          4 [t p v]
          5 [v p q])))))

(defn rgb-to-hls [r g b]
  (let [[r g b] (map double [r g b])
        maxc   (max r g b)
        minc   (min r g b)
        sumc   (+ maxc minc)
        rangec (- maxc minc)
        l      (/ sumc 2.0)]
    (if (= minc maxc)
      [0.0 l 0.0]
      (let [s  (if (<= l 0.5) (/ rangec sumc) (/ rangec (- 2.0 maxc minc)))
            rc (/ (- maxc r) rangec)
            gc (/ (- maxc g) rangec)
            bc (/ (- maxc b) rangec)
            h  (cond
                 (= r maxc) (- bc gc)
                 (= g maxc) (+ 2.0 rc (- bc))
                 :else      (+ 4.0 gc (- rc)))]
        [(mod (/ h 6.0) 1.0) l s]))))

(defn- hls-v [m1 m2 hue]
  (let [hue (mod (double hue) 1.0)]
    (cond
      (< hue (/ 1.0 6)) (+ m1 (* (- m2 m1) hue 6.0))
      (< hue 0.5)       m2
      (< hue (/ 2.0 3)) (+ m1 (* (- m2 m1) (- (/ 2.0 3) hue) 6.0))
      :else              m1)))

(defn hls-to-rgb [h l s]
  (let [[h l s] (map double [h l s])]
    (if (zero? s)
      [l l l]
      (let [m2 (if (<= l 0.5) (* l (+ 1.0 s)) (- (+ l s) (* l s)))
            m1 (- (* 2.0 l) m2)]
        [(hls-v m1 m2 (+ h (/ 1.0 3)))
         (hls-v m1 m2 h)
         (hls-v m1 m2 (- h (/ 1.0 3)))]))))

(defn rgb-to-yiq [r g b]
  (let [[r g b] (map double [r g b])
        y (+ (* 0.30 r) (* 0.59 g) (* 0.11 b))]
    [y
     (- (* 0.74 (- r y)) (* 0.27 (- b y)))
     (+ (* 0.48 (- r y)) (* 0.41 (- b y)))]))

(defn yiq-to-rgb [y i q]
  (let [[y i q] (map double [y i q])]
    [(min 1.0 (max 0.0 (+ y (* 0.9468822170900693 i) (* 0.6235565819861433 q))))
     (min 1.0 (max 0.0 (+ y (- (* 0.27478764629897834 i)) (- (* 0.6356910791873801 q)))))
     (min 1.0 (max 0.0 (+ y (- (* 1.1085450346420322 i)) (* 1.7090069284064666 q))))]))
