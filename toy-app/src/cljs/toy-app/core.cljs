(ns toy-app.core
  (:require [quiescent.core :as q]
            [quiescent.dom :as d]
            [cljs-physics-engine.core :as physics]))

(defn- get-x-pos [nodes id]
  (:x (first (filter #(= (:id %) id) nodes))))

(defn- get-y-pos [nodes id]
  (:y (first (filter #(= (:id %) id) nodes))))

(defn- build-line-element [nodes] ;; Is this a legit pattern?
  (fn [line]
    (d/line {:x1 (get-x-pos nodes (:from line))
             :y1 (get-y-pos nodes (:from line))
             :x2 (get-x-pos nodes (:to line))
             :y2 (get-y-pos nodes (:to line))
             :key (str (:from line) (:to line))
             :style {:stroke "rgb(59,59,59)" :strokeWidth "0.5"}})))

(defn- build-circle-element [node]
  (d/circle {:cx (str (:x node))
             :cy (str (:y node))
             :r "5"
             :key (:id node)
             :stroke "green" :strokeWidth "0.25" :fill "yellow"}))

(defn- create-dom-elements [lines nodes]
  (concat (map (build-line-element nodes) lines)
          (map build-circle-element nodes)))

(defn add-positions [nodes]
  (map #(merge %2 {:id (:id %1)}) nodes [{:x 75 :y 50} {:x 50 :y 75} {:x 25 :y 50} {:x 50 :y 25}]))

(defn- lines-from-node [node]
  (let [id (:id node)]
    (map #(identity {:from id :to %}) (:links node))))

(defn- layout-graph [graph] ;; Better name?
  (let [lines (mapcat lines-from-node (:nodes graph))
        nodes (add-positions (:nodes graph))]
    {:lines lines :nodes nodes}))

(q/defcomponent Canvas
  "A home for our graph"
  [graph]
  (let [{:keys [lines nodes]} (layout-graph graph)]
    (d/div {:className "canvas"}
           (d/svg {:width "1000" :height "600" :viewBox "0 0 100 100"}
                  (create-dom-elements lines nodes)))))

(def graph {:nodes [{:id 1 :links [2]}
                    {:id 2 :links []}
                    {:id 3 :links [2]}
                    {:id 4 :links [1]}]})

(println "Hey: " (physics/step-forward {:G 0 :k-e 0 :size {:x 1000 :y 1000 :z 1000} :M 100 :r 10} [] 2 [{:id 1 :m 0 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}
                     {:id 2 :m 10 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}
                     {:id 3 :m 5 :q 0 :x 0 :y 250 :z 0 :v {:x 0 :y -10 :z 0}}]))

(q/render (Canvas graph) (.getElementById js/document "content"))
