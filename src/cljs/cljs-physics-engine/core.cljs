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
  (let [mass (:m particle)]
    ;; F = ma TODO: if mass of particle is zero, divByZero error
    (letfn [(a-with-component [[component force]] [component (/ force mass)])]
      (into {} (map a-with-component f)))))

(defn- calc-displacement [a t particle]
  (letfn [(s-with-component [[component a]] [component (s (get-in particle [:v component]) t a)])]
    (into {} (map s-with-component a))))

(defn- calc-final-velocity [a t particle]
  (letfn [(v-with-component [[component a]] [component (v (get-in particle [:v component]) t a)])]
    (into {} (map v-with-component a))))

(defn- update-particle [env t particle]
  (let [f-bar (calc-gravity env particle)
        a-bar (calc-acceleration f-bar particle)
        s-bar (calc-displacement a-bar t particle)
        v-bar (calc-final-velocity a-bar t particle)]
  (->> particle
       (update-pos :x (:x s-bar))
       (update-pos :y (:y s-bar))
       (update-pos :z (:z s-bar))
       (update-v :x (:x v-bar))
       (update-v :y (:y v-bar))
       (update-v :z (:z v-bar)))))

(defn- validate-env [env]
  (cond (= (:r env) 0) {:error "Must not have env with zero radius, r, use nil instead" :valid false}
        :else {:valid true}))

(defn step-forward [env connections t particles]
  (let [error (:error (validate-env env))]
    (if (not (nil? error))
      error
      {:particles (map (partial update-particle env t) particles)})))
