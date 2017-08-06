(defproject cljs-physics-engine "0.1.0-SNAPSHOT"

  :source-paths ["src/cljs"]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.854"
                  :exclusions [org.apache.ant/ant]]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.7"]]
  :cljsbuild {
    :builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler {:output-to "test-out/js/testable.js"
                        :target :nodejs
                        :main cljs-physics-engine.runner
                        :optimizations :none}}]}

  ;; Get the test output looking a bit more sane
  :doo {:paths {:karma "karma --colors"}}

  :user {:dependencies [[pjstadig/humane-test-output "0.8.2"]]
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]}
  :profiles {:dev {:dependencies [[pjstadig/humane-test-output "0.8.2"]]}}

  :description "A physics engine to model n particles and simple forces between them.")
