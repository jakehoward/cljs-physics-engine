(ns cljs-physics-engine.core-test
  (:require [cljs-physics-engine.core :as p]
            [cljs.test :refer-macros [deftest is testing]]))

;; TODO
;; - duplicate id's in particle list => error
;; - missing id in particle => error
;; - missing m, q, r are assumed to be 0, 0 and point respectively
;; - particles are not allowed to inhabit the same space (generically define and enforce laws?)
;; - particles are not allowed to live outside the universe

(deftest physics-engine
  (testing "In a world with no forces, nothing moves"
    (let [connections [{:from 1 :to 2 :k 0}]
          environment {:G 0 :k-e 0 :size {:x 100 :y 100 :z 100} :M 100000}
          particles [{:id 1 :m 100 :q 1 :r 10 :x 10 :y 10 :z 10} {:id 2 :m 50 :q -3 :r 5 :x 5 :y 5 :z 5}]]
      (is (= (:particles (p/step-forward environment connections particles)) particles)))))
