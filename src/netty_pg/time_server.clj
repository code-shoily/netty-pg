(ns netty-pg.time-server
  (:gen-class)
  (:require [netty-pg.utils :as utils])
  (:import [java.util Date]
           [io.netty.channel ChannelHandler ChannelInboundHandlerAdapter
            ChannelFutureListener ChannelInitializer]))

(declare time-server-handler)

(defn time-channel-initializer
  []
  (proxy [ChannelInitializer] []
    (initChannel [ch]
      (.. ch
          (pipeline)
          (addLast (into-array ChannelHandler [(time-server-handler)]))))))


(defn time-server-handler
  []
  (proxy [ChannelInboundHandlerAdapter] []
    (channelActive
     [ctx]
     (let [time (.. ctx (alloc) (buffer 4))]
       (.writeLong time (-> (System/currentTimeMillis)
                           (/ 1000)
                           (+ 2208988800)))

       (.. (.writeAndFlush ctx time)
           (addListener (proxy [ChannelFutureListener]
                          []
                          (operationComplete [future] (.close ctx)))))))
    (exceptionCaught
     [ctx cause]
       (.printStackTrace cause)
       (.close ctx))))

(defn run [port] (utils/run time-channel-initializer port))
