(ns cljs-physics-engine.core-test
  (:require [cljs-physics-engine.core :as p]
            [cljs.test :refer-macros [deftest is testing]]))

;; TODO
;; - duplicate id's in particle list => error
;; - missing id in particle => error
;; - missing m, q, r are assumed to be 0, 0 and point respectively
;; - particles are not allowed to inhabit the same space (generically define and enforce laws?)
;; - particles are not allowed to live outside the universe
;; - asserting on particles probably shouldn't care about order, define (same? ) which sortsBy :id then compares
;; - can use integers in code for convenience, but does arithmetic on rationals when possible and otherwise doubles
;; - add a parameter that doesn't allow particles to get any closer to each other than Min_Distance.
;; - make optimisations over a certain number of particles (nearest neighbours have the biggest effect)
;; - Should it add defaults if some args aren't provided ? (e.g. user can provide 2-d world, will work with same code)

(deftest physics-engine-no-forces
  (let [environment {:G 0 :k-e 0 :size {:x 100 :y 100 :z 100} :M 10000}
        connections []
        time-step 1
        particles []
        velocity {:x 0 :y 0 :z 0}]
    (testing "In a world with no forces, nothing moves"
      (let [connections [{:from 1 :to 2 :k 0 :l 1}]
            particles [{:id 1 :m 100 :q 1 :x 10 :y 10 :z 10 :v velocity} {:id 2 :m 50 :q -3 :x 5 :y 5 :z 5 :v velocity}]]
        (is (= (:particles (p/step-forward environment connections time-step particles)) particles))))

    (testing "Particles with velocity moves the requisite amount"
      (let [particles [{:id 1 :m 100 :q 1 :x 10 :y 10 :z 10 :v {:x 1 :y 1 :z 1}} {:id 1 :m 100 :q 1 :x 10 :y 10 :z 10 :v {:x 2 :y 2 :z 2}}]
            time-step 0.5 ;; second
            actual (:particles (p/step-forward environment connections time-step particles))
            expected [{:id 1 :m 100 :q 1 :x 10.5 :y 10.5 :z 10.5 :v {:x 1 :y 1 :z 1}} {:id 1 :m 100 :q 1 :x 11 :y 11 :z 11 :v {:x 2 :y 2 :z 2}}]]
        (is (= actual expected))))))
