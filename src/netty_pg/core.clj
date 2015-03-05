(ns netty-pg.core
  (:gen-class)
  (:require [netty-pg.discard-server :as discard-server]
            [netty-pg.echo-server :as echo-server]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (echo-server/run 8080))
