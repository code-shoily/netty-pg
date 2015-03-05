(ns netty-pg.echo-server
  (:gen-class)
  (:require [netty-pg.utils :as utils])
  (:import [io.netty.channel ChannelHandler ChannelInboundHandlerAdapter ChannelInitializer]))

(declare echo-server-handler)

(defn echo-channel-initializer
  []
  (proxy [ChannelInitializer] []
    (initChannel [ch]
      (.. ch
          (pipeline)
          (addLast (into-array ChannelHandler [(echo-server-handler)]))))))


(defn echo-server-handler
  []
  (proxy [ChannelInboundHandlerAdapter] []
    (channelRead [ctx msg] (.write ctx msg) (.flush ctx))
    (exceptionCaught
     [ctx cause]
       (.printStackTrace cause)
       (.close ctx))))

(defn run [port] (utils/run echo-channel-initializer port))
