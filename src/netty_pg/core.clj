(ns netty-pg.core
  (:gen-class)
  (:require [netty-pg.discard-server :as discard-server]
            [netty-pg.echo-server :as echo-server]
            [netty-pg.time-server :as time-server]))

(defn -main
  "Run the server"
  [& args]
  (let [port (or (first args) 8080)]
    (println "Server running on port" port)
    (time-server/run (Integer/parseInt port))))
