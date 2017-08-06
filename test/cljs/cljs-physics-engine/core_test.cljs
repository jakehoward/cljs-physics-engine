(ns cljs-physics-engine.core-test
  (:require [cljs-physics-engine.core :as p]
            [cljs.test :refer-macros [deftest is testing]]))


(deftest hello-physical-world
  (testing "The tests..."
    (is (= (p/hello-physical-world) "Hello, physical world!"))))
