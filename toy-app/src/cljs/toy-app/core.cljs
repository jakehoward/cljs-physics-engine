(ns toy-app.core
  (:require [quiescent.core :as q]
            [quiescent.dom :as d]
            [cljs-physics-engine.core :as physics]
            [clojure.core.async :refer [>! <! chan close! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


(defn- build-circle-element [node]
  (d/circle {:cx (str (:x node))
             :cy (str (:y node))
             :r "50"
             :key (:id node)
             :stroke "yellow" :strokeWidth "5" :fill "blue"}))

(defn- create-dom-elements [nodes]
  (map build-circle-element nodes))

(q/defcomponent Canvas [particles]
  (d/div {:className "canvas"}
         (d/svg {:width "1000" :height "600" :viewBox "0 0 1000 1000"}
                (create-dom-elements particles))))

(defn tick-every [ms]
  (let [c (chan)]
    (go-loop []
      (<! (timeout ms))
      (when (>! c :tick)
        (recur)))
    c))

(def t 0.2)
(def env {:G 10 :k-e 8.99e9 :size {:x 1000 :y 1000 :z 1000} :M 0 :r 1})
(def conns [])

(defn play-animation []
  (go
    (let [tick-chan (tick-every 20)
          particles [{:id 1 :m 10000 :q 0 :x 500 :y 500 :z 0 :v {:x 0 :y 0 :z 0}}
                     {:id 2 :m 5 :q 0 :x 950 :y 500 :z 0 :v {:x 0 :y 14.5 :z 0}}]]
      (loop [iteration 1 ps particles]
        (when (and (<! tick-chan) (< iteration 10000))
          (q/render (Canvas ps) (.getElementById js/document "content"))
          (recur (inc iteration) (:particles (physics/step-forward env conns t ps)))))
      (close! tick-chan))))

(play-animation)
