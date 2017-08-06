(ns cljs-physics-engine.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [cljs-physics-engine.core-test]))

(doo-tests 'cljs-physics-engine.core-test)
