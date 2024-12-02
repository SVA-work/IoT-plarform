package test.controller;

import test.DTO.Message;
import test.config.*;

import test.library.json.JsonParserDefault;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import test.tables.DatabaseConnection;
import test.tables.RulesController;
import test.tables.UsersController;


public class ServerLauncher {

  public static void main(String[] args) throws Exception {
    System.out.println(1);
    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap boot = new ServerBootstrap();
      DatabaseConnection.getConnection();
      boot.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                  channel.pipeline()
                          .addLast("HttpServerCodec", new HttpServerCodec())
                          .addLast("HttpServerKeepAlive", new HttpServerKeepAliveHandler())
                          .addLast("HttpObjectAggregator", new HttpObjectAggregator(ServerConfig.MAX_CONTENT_LENGHT, true))
                          .addLast("HttpChunkedWrite", new ChunkedWriteHandler())
                          .addLast("HelloHttpHandler", new HelloHttpHandler(new JsonParserDefault()));
                }
              })
              .option(ChannelOption.SO_BACKLOG, ServerConfig.MAX_BACK_LOG_SIZE)
              .childOption(ChannelOption.SO_KEEPALIVE, true);
      ChannelFuture future = boot.bind(ServerConfig.PORT).sync();

      future.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
