(ns netty-pg.discard-server
  (:gen-class)
  (:require [netty-pg.utils :as utils])
  (:import [io.netty.channel ChannelHandler ChannelInboundHandlerAdapter ChannelInitializer]))

(declare discard-server-handler)

(defn discard-channel-initializer
  []
  (proxy [ChannelInitializer] []
    (initChannel [ch]
      (.. ch
          (pipeline)
          (addLast (into-array ChannelHandler [(discard-server-handler)]))))))


(defn discard-server-handler
  []
  (proxy [ChannelInboundHandlerAdapter] []
    (channelRead [ctx msg] (.release msg))
    (exceptionCaught
     [ctx cause]
       (.printStackTrace cause)
       (.close ctx))))

(defn run [port] (utils/run discard-channel-initializer port))
