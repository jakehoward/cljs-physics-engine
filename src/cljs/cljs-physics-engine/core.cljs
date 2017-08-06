(ns cljs-physics-engine.core)

;; Equations of motion

;; s: scalar distance moved (displacement)
;; a: acceleration
;; v: final velocity
;; u: initial velocity
;; t: time interval

;; a = (u - v) / t
;; v = u + at
;; s = 1/2(u + v)t
;; s = ut + (1/2)at^2
;; s = vt - (1/2)at^2
;; v^2 = u^2 + 2as

(defn- square [n] (* n n))

;; TODO: if r is 0, divByZero error
(defn- force-gravity [g m-1 m-2 r]
  (/ (* g m-1 m-2)
     (square r)))

(defn- v [u t a]
  (+ u (* a t)))

(defn- s [u t a]
  (+ (* u t) (* 0.5 a (square t))))

(defn- update-v [component final-v particle]
  (assoc-in particle [:v component] final-v))

(defn- update-pos [component displacement particle]
  (assoc particle component (+ (component particle) displacement)))

(defn- calc-gravity [env particle]
  {:x 0 :y (* -1 (force-gravity (:G env) (:M env) (:m particle) (+ (:y particle) (:r env)))) :z 0})

(defn- calc-acceleration [f particle]
  (let [m (:m particle)]
    (into {} (map #(vector (first %) (/ (second %) m)) f)))) ;; F = ma TODO: if mass of particle is zero, divByZero error

(defn- calc-displacement [a t particle]
  (let [s-x (s (get-in particle [:v :x]) t (:x a)) ;; TODO make this generic over componenet
        s-y (s (get-in particle [:v :y]) t (:y a))
        s-z (s (get-in particle [:v :z]) t (:z a))]
    {:x s-x :y s-y :z s-z}))

(defn- calc-final-velocity [a t particle]
  (let [v-x (v (get-in particle [:v :x]) t (:x a)) ;; TODO make this generic over componenet - same as ^^ map-vector? (as in spatial vector)
        v-y (v (get-in particle [:v :y]) t (:y a))
        v-z (v (get-in particle [:v :z]) t (:z a))]
    {:x v-x :y v-y :z v-z}))

(defn- update-particle [env t particle]
  (let [f-gravity (calc-gravity env particle)
        a (calc-acceleration f-gravity particle)
        s (calc-displacement a t particle)
        v (calc-final-velocity a t particle)]
  (->> particle
       (update-pos :x (:x s))
       (update-pos :y (:y s))
       (update-pos :z (:z s))
       (update-v :x (:x v))
       (update-v :y (:y v))
       (update-v :z (:z v)))))

(defn- validate-env [env]
  (cond (= (:r env) 0) {:error "Must not have env with zero radius, r, use nil instead" :valid false}
        :else {:valid true}))

(defn step-forward [env connections t particles]
  (let [error (:error (validate-env env))]
    (if (not (nil? error))
      error
      {:particles (map (partial update-particle env t) particles)})))
