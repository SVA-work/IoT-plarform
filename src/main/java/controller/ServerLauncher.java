package controller;

import config.*;
import controller.deviceapi.TelemetryHttpHandler;
import controller.userapi.DeviceHttpHandler;
import controller.userapi.RulesHttpHandler;
import controller.userapi.UserHttpHandler;
import library.json.JsonParserDefault;
import org.springframework.boot.SpringApplication;
import tables.DatabaseConnection;
import tables.DevicesRepository;
import tables.RulesRepository;
import tables.UsersRepository;
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
import telegrambot.IoTServiceBotApplication;

public class ServerLauncher {
  public static void main(String[] args) throws Exception {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    IoTServiceBotApplication.main(args);

    try {
      ServerBootstrap boot = new ServerBootstrap();
      DatabaseConnection data = new DatabaseConnection();
      data.getConnection();
      boot.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                  JsonParserDefault parser = new JsonParserDefault();
                  UserHttpHandler userHandler = new UserHttpHandler(parser);
                  DeviceHttpHandler deviceHandler = new DeviceHttpHandler(parser);
                  RulesHttpHandler rulesHandler = new RulesHttpHandler(parser);
                  TelemetryHttpHandler telemetryHandler = new TelemetryHttpHandler(parser);
                  userHandler.setNextHandler(deviceHandler);
                  deviceHandler.setNextHandler(rulesHandler);
                  rulesHandler.setNextHandler(telemetryHandler);
                  channel.pipeline()
                          .addLast("HttpServerCodec", new HttpServerCodec())
                          .addLast("HttpServerKeepAlive", new HttpServerKeepAliveHandler())
                          .addLast("HttpObjectAggregator", new HttpObjectAggregator(ServerConfig.MAX_CONTENT_LENGHT, true))
                          .addLast("HttpChunkedWrite", new ChunkedWriteHandler())
                          .addLast("User HttpHandler", userHandler);
                }
              })
              .option(ChannelOption.SO_BACKLOG, ServerConfig.MAX_BACK_LOG_SIZE)
              .childOption(ChannelOption.SO_KEEPALIVE, true);
      System.out.println("Сервер запущен на порту: " + ServerConfig.PORT);

      initDataBase();

      ChannelFuture future = boot.bind(ServerConfig.PORT).sync();

      future.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  private static void initDataBase() {
    UsersRepository createUserTable = new UsersRepository();
    createUserTable.createTable();
    DevicesRepository createDeviceTable = new DevicesRepository();
    createDeviceTable.createTable();
    RulesRepository createRuleTable = new RulesRepository();
    createRuleTable.createTable();
  }
}
