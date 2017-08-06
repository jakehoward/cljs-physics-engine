(ns cljs-physics-engine.core)

(defn- square [n] (* n n))

(defn- force-gravity [g m-1 m-2 r]
  (/ (* g m-1 m-2)
     (square r)))

(defn- v [u t a]
  (+ u (* a t)))

(defn- s [u t a]
  (+ (* u t) (* 0.5 a (square t))))

(defn- update-v [component final-v p]
  (assoc-in p [:v component] final-v))

(defn- update-pos [component displacement p]
  (assoc p component (+ (component p) displacement)))

(defn- calc-gravity [env p]
  {:x 0 :y (* -1 (force-gravity (:G env) (:M env) (:m p) (+ (:y p) (:r env)))) :z 0})

(defn- calc-acceleration [f-bar p]
  (let [mass (:m p)]
    (into {} (map (fn [[component force]] [component (/ force mass)]) f-bar))))

(defn- calc-displacement [a-bar t p]
  (into {} (map (fn [[component a-c]] [component (s (get-in p [:v component]) t a-c)]) a-bar)))

(defn- calc-final-velocity [a-bar t p]
  (into {} (map (fn [[component a-c]] [component (v (get-in p [:v component]) t a-c)]) a-bar)))

(defn- update-particle [env t p]
  (let [f-bar (calc-gravity env p)
        a-bar (calc-acceleration f-bar p)
        s-bar (calc-displacement a-bar t p)
        v-bar (calc-final-velocity a-bar t p)]
  (->> p
       (update-pos :x (:x s-bar))
       (update-pos :y (:y s-bar))
       (update-pos :z (:z s-bar))
       (update-v :x (:x v-bar))
       (update-v :y (:y v-bar))
       (update-v :z (:z v-bar)))))

(defn- validate [env ps]
  (cond (= (:r env) 0) {:error "Must not have env with zero radius, r, use nil instead" :valid false}
        (not (every? #(> (:m %) 0) ps)) {:error "Particles must have non-zero mass" :valid false}
        :else {:valid true}))

(defn step-forward [env connections t ps]
  (let [error (:error (validate env ps))]
    (if (not (nil? error))
      error
      {:particles (map (partial update-particle env t) ps)})))
