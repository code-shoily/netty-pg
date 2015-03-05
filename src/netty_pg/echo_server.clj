(ns netty-pg.echo-server
  (:gen-class)
  (:import [io.netty.channel ChannelHandlerContext ChannelHandler]
           [io.netty.channel ChannelInboundHandlerAdapter]
           [io.netty.bootstrap ServerBootstrap]
           [io.netty.channel ChannelFuture ChannelInitializer ChannelOption EventLoopGroup]
           [io.netty.channel.nio NioEventLoopGroup]
           [io.netty.channel.socket SocketChannel]
           [io.netty.channel.socket.nio NioServerSocketChannel]))

(declare echo-server-handler)
(declare echo-channel-initializer)

(defn run [port]
  (let [boss-group (NioEventLoopGroup.)
        worker-group (NioEventLoopGroup.)
        b (ServerBootstrap.)]
    (try
      (.. b
        (group boss-group worker-group)
        (channel NioServerSocketChannel)
        (childHandler (echo-channel-initializer))
        (option ChannelOption/SO_BACKLOG (int 128))
        (childOption ChannelOption/SO_KEEPALIVE true))
      (-> (-> (.bind b port) (.sync))
          (.channel)
          (.closeFuture)
          (.sync))
      (finally (.shutdown b)))))


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
