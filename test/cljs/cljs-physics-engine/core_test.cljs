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
;; - could you end up with a 0 from rounding error?

(deftest physics-engine-no-forces
  (let [environment {:G 0 :k-e 0 :size {:x 100 :y 100 :z 100} :M 10000 :r 100}
        connections []
        time-step 1
        particles []
        velocity {:x 0 :y 0 :z 0}]

    (testing "In a world with no forces, nothing moves"
      (let [connections [{:from 1 :to 2 :k 0 :l 1}]
            particles [{:id 1 :m 100 :q 1 :x 10 :y 10 :z 10 :v velocity}
                       {:id 2 :m 50 :q -3 :x 5 :y 5 :z 5 :v velocity}]
            actual (:particles (p/step-forward environment connections time-step particles))]
        (is (= particles actual))))

    (testing "Newton's first law: Particles with velocity move the requisite amount"
      (let [particles [{:id 1 :m 100 :q 1 :x 10 :y 10 :z 10 :v {:x 1 :y 1 :z 1}}
                       {:id 2 :m 100 :q 1 :x 12 :y 12 :z 12 :v {:x 2 :y 2 :z 2}}]
            time-step 0.5
            actual (:particles (p/step-forward environment connections time-step particles))
            expected [{:id 1 :m 100 :q 1 :x 10.5 :y 10.5 :z 10.5 :v {:x 1 :y 1 :z 1}}
                      {:id 2 :m 100 :q 1 :x 13 :y 13 :z 13 :v {:x 2 :y 2 :z 2}}]]
        (is (= expected actual))))))

(deftest physics-engine-environmental-gravity
    (testing "The environment's gravity moves a particle with mass"
      ;; G/r^2 cancels out (taking y into consideration) , particle :m 1 => F = 100 N
      (let [environment {:G (* 260 260) :k-e 0 :size {:x 1000 :y 1000 :z 1000} :M 100 :r 10}
            time-step 2 ;; ensure it's being used by not being equal to 1
            connections []
            particles [{:id 1 :m 1 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}]
            actual (first (:particles (p/step-forward environment connections time-step particles)))
            expected {:id 1 :m 1 :q 0 :x 0 :y 30 :z 0 :v {:x 0 :y -210 :z 0}}]
        (is (= expected actual)))))

(deftest physics-engine-inter-particle-gravity
    (testing "In a simple binary system, the particles are gravitationally attracted to each other"
      (let [environment {:G 10 :k-e 0 :size {:x 1000 :y 1000 :z 1000} :M 0 :r 1} ;; No planetary gravity
            time-step 2 ;; ensure it's being used by not being equal to 1
            connections []
            particles [{:id 1 :m 5 :q 0 :x 0 :y 0 :z 0 :v {:x 0 :y 0 :z 0}}
                       {:id 2 :m 20 :q 0 :x 10 :y 10 :z 10 :v {:x 0 :y 0 :z 0}}]
            actual (:particles (p/step-forward environment connections time-step particles))
            expected [{:id 1 :m 5 :q 0 :x 0.7698003589195008 :y 0.7698003589195008 :z 0.7698003589195008 :v {:x 0.7698003589195008 :y 0.7698003589195008 :z 0.7698003589195008}}
                      {:id 2 :m 20 :q 0 :x (- 10 0.1924500897298752) :y (- 10 0.1924500897298752) :z (- 10 0.1924500897298752) :v {:x -0.1924500897298752 :y -0.1924500897298752 :z -0.1924500897298752}}]]
        (is (= expected actual)))))

(deftest physics-engine-constraints
  (testing "Particles with no mass are not allowed"
    (let [environment {:G 0 :k-e 0 :size {:x 1000 :y 1000 :z 1000} :M 100 :r 10}
          time-step 1
          connections []
          particles [{:id 1 :m 0 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}
                     {:id 2 :m 10 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}
                     {:id 3 :m 5 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}]
          actual (:error (p/step-forward environment connections time-step particles))
          expected "Particles must have non-zero mass, m"]
      (is (= expected actual))))
  (testing "Planets with no size are not allowed"
    (let [environment {:G 0 :k-e 0 :size {:x 1000 :y 1000 :z 1000} :M 100 :r 0}
          particles [{:id 1 :m 100 :q 1 :x 12 :y 12 :z 12 :v {:x 2 :y 2 :z 2}}]
          connections []
          time-step 0.5
          actual (:error (p/step-forward environment connections time-step particles))
          expected "Must not have env with zero radius, r"]
        (is (= expected actual))))
  (testing "Particles must have unique id's"
    (let [environment {:G 0 :k-e 0 :size {:x 1000 :y 1000 :z 1000} :M 100 :r 1}
          particles [{:id 1 :m 8 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}
                     {:id 2 :m 10 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}
                     {:id 1 :m 5 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}]
          connections []
          time-step 0.5
          actual (:error (p/step-forward environment connections time-step particles))
          expected "All particles must have unique :id keys"]
        (is (= expected actual)))))
