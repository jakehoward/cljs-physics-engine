(defproject toy-app "0.1.0-SNAPSHOT"

  :source-paths ["src/cljs"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.854"
                  :exclusions [org.apache.ant/ant]]
                 [quiescent "0.3.2"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.12"]
            [lein-doo "0.1.7"]]
  :cljsbuild {
    :builds [{:source-paths ["src/cljs"]
              :figwheel true
              :id "dev"
              :compiler {:main "toy-app.core"
                         :output-to "resources/public/js/main.js"
                         :asset-path "js/out"
                         :output-dir "resources/public/js/out"
                         :pretty-print true}}]}

  :description "A small app used to sanity check the physics enginge.")


