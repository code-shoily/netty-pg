(ns netty-pg.time-server
  (:gen-class)
  (:import [io.netty.channel ChannelHandlerContext ChannelHandler]
           [io.netty.channel ChannelInboundHandlerAdapter]
           [io.netty.bootstrap ServerBootstrap]
           [io.netty.channel ChannelFuture ChannelInitializer ChannelOption EventLoopGroup]
           [io.netty.channel.nio NioEventLoopGroup]
           [io.netty.channel.socket SocketChannel]
           [io.netty.channel.socket.nio NioServerSocketChannel]))

(declare time-server-handler)
(declare time-channel-initializer)

(defn run [port]
  (let [boss-group (NioEventLoopGroup.)
        worker-group (NioEventLoopGroup.)
        b (ServerBootstrap.)]
    (try
      (.. b
        (group boss-group worker-group)
        (channel NioServerSocketChannel)
        (childHandler (time-channel-initializer))
        (option ChannelOption/SO_BACKLOG (int 128))
        (childOption ChannelOption/SO_KEEPALIVE true))
      (-> (-> (.bind b port) (.sync))
          (.channel)
          (.closeFuture)
          (.sync))
      (finally (.shutdown b)))))


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
       (.writeInt time (-> (System/currentTimeMillis)
                           (/ 1000)
                           (+ 2208988800)
                           int))
       (.. (.writeAndFlush ctx time)
           (addListener (proxy [ChannelFutureListener]
                          []
                          (operationComplete [future] (.close ctx)))))))
    (exceptionCaught
     [ctx cause]
       (.printStackTrace cause)
       (.close ctx))))
