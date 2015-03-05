(ns netty-pg.utils
  (:gen-class)
  (:import [io.netty.bootstrap ServerBootstrap]
           [io.netty.channel ChannelOption]
           [io.netty.channel.nio NioEventLoopGroup]
           [io.netty.channel.socket SocketChannel]
           [io.netty.channel.socket.nio NioServerSocketChannel]))

(defn run [initializer port]
  (let [boss-group (NioEventLoopGroup.)
        worker-group (NioEventLoopGroup.)
        b (ServerBootstrap.)]
    (try
      (.. b
        (group boss-group worker-group)
        (channel NioServerSocketChannel)
        (childHandler (initializer))
        (option ChannelOption/SO_BACKLOG (int 128))
        (childOption ChannelOption/SO_KEEPALIVE true))
      (-> (-> (.bind b port) (.sync))
          (.channel)
          (.closeFuture)
          (.sync))
      (finally
       (.shutdownGracefully worker-group)
       (.shutdownGracefully boss-group)))))
