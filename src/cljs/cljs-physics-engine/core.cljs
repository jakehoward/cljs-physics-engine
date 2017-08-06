(ns cljs-physics-engine.core)

(defn- update-velocity [t component particle]
  (assoc particle component (+ (component particle) (* (get-in particle [:v component]) t))))

(defn- update-position [t particle]
  (->> particle
       (update-velocity t :x)
       (update-velocity t :y)
       (update-velocity t :z)))

(defn step-forward [environment connections t particles]
  {:particles (map (partial update-position t) particles)})
