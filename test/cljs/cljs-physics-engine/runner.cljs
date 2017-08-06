(ns cljs-physics-engine.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [pjstadig.humane-test-output]
              [cljs-physics-engine.core-test]))

(doo-tests 'cljs-physics-engine.core-test)
