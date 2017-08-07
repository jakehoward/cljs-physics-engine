(ns cljs-physics-engine.core)

(defn- square [n] (* n n))

(defn- neg [a] (* -1 a))

(defn- map-values
  ([f d1 d2]
   (map (fn [[d1-k d1-v] [d2-k d2-v]] (f d1-v d2-v)) d1 d2))
  ([f d]
   (map (fn [[k v]] (f v)) d)))

(defn- reduce-vectors [f spatial-vectors]
  (reduce (fn [total item] (zipmap (keys total) (map-values f total item))) spatial-vectors))

(defn- map-vector [f spatial-vector]
  (zipmap (keys spatial-vector) (map-values f spatial-vector)))

(defn- add-vectors [spatial-vectors]
  (reduce-vectors + spatial-vectors))

(defn- vector-difference [a b]
  (reduce-vectors - [a b]))

(defn- vector-magnitude [r]
  (.sqrt js/Math (+ (square (:z r)) (square (:z r)) (square (:z r)))))

(defn- unit-vector [r]
  (let [magnitude (vector-magnitude r)]
    (map-vector #(/ % magnitude) r)))

(defn- force-gravity [g m-1 m-2 r]
  (/ (* g m-1 m-2)
     (square r)))

(defn- force-electrostatic [k-e q-1 q-2 r]
  (neg (/ (* k-e q-1 q-2)
          (square r))))

(defn- v [u t a]
  (+ u (* a t)))

(defn- s [u t a]
  (+ (* u t) (* 0.5 a (square t))))

(defn- update-v [component final-v p]
  (assoc-in p [:v component] final-v))

(defn- update-pos [component displacement p]
  (assoc p component (+ (component p) displacement)))

(defn- calc-gravity-from-env [env p]
  {:x 0 :y (* -1 (force-gravity (:G env) (:M env) (:m p) (+ (:y p) (:r env)))) :z 0})

(defn- deconstruct-force-into-components [force-fn p-a p-b]
  (let [r (vector-difference (select-keys p-b [:x :y :z]) (select-keys p-a [:x :y :z]))
        mag-r (vector-magnitude r)
        unit-vector-r (unit-vector r)
        force (force-fn mag-r)]
    (map-vector #(* % force) unit-vector-r)))

(defn- gravitational-f-between-particles [g p-a p-b]
  (deconstruct-force-into-components (partial force-gravity g (:m p-a) (:m p-b)) p-a p-b))

(defn- electrostatic-f-between-particles [k-e p-a p-b]
  (deconstruct-force-into-components (partial force-electrostatic k-e (:q p-a) (:q p-b)) p-a p-b))

;; Optimisation of 2x if we don't calculate the same force twice for each particle
(defn- calc-inter-particle-gravity [env p other-ps]
  (if (= 0 (count other-ps))
    {:x 0 :y 0 :z 0}
    (add-vectors (map (partial gravitational-f-between-particles (:G env) p) other-ps))))

(defn- calc-inter-particle-electrostatic [env p other-ps]
  (if (= 0 (count other-ps))
    {:x 0 :y 0 :z 0}
    (add-vectors (map (partial electrostatic-f-between-particles (:k-e env) p) other-ps))))

(defn- calc-acceleration [f-bar p]
  (let [mass (:m p)]
    (into {} (map (fn [[component force]] [component (/ force mass)]) f-bar))))

(defn- calc-displacement [a-bar t p]
  (into {} (map (fn [[component a-c]] [component (s (get-in p [:v component]) t a-c)]) a-bar)))

(defn- calc-final-velocity [a-bar t p]
  (into {} (map (fn [[component a-c]] [component (v (get-in p [:v component]) t a-c)]) a-bar)))


(defn- update-particle [env t ps p]
  (let [other-ps (filter #(not (= (:id %) (:id p))) ps)
        f-bar (add-vectors [(calc-gravity-from-env env p)
                            (calc-inter-particle-gravity env p other-ps)
                            (calc-inter-particle-electrostatic env p other-ps)])
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
  (cond (not (> (:r env) 0)) "Must not have env with zero radius, r"
        (not (every? #(> (:m %) 0) ps)) "Particles must have non-zero mass, m"
        (not (= (count ps) (count (into #{} (map :id ps))))) "All particles must have unique :id keys"
        :else nil))

(defn step-forward [env connections t ps]
  (let [error (validate env ps)]
    (if (not (nil? error))
      {:error error}
      {:particles (into [] (map (partial update-particle env t ps) ps))})))
