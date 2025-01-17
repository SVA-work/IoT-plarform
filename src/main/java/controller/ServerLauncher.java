package controller;

import config.*;
import controller.deviceapi.TelemetryHttpHandler;
import controller.userapi.DeviceHttpHandler;
import controller.userapi.RulesHttpHandler;
import controller.userapi.UserHttpHandler;
import dto.DbConnectionDto;
import library.json.JsonParserDefault;
import org.springframework.boot.SpringApplication;
import tables.*;
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
    DbConnectionDto dbConnectionDto = new DbConnectionDto();
    dbConnectionDto.url = DbConfig.url;
    dbConnectionDto.user = DbConfig.user;
    dbConnectionDto.password = DbConfig.password;

    runApplication(args, dbConnectionDto);
  }

  public static void runApplication(String[] args, DbConnectionDto dbConnectionDto) throws InterruptedException {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    runTelegramBot(args);

    try {
      ServerBootstrap boot = new ServerBootstrap();
      boot.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                  JsonParserDefault parser = new JsonParserDefault();
                  UserHttpHandler userHandler = new UserHttpHandler(parser, dbConnectionDto);
                  DeviceHttpHandler deviceHandler = new DeviceHttpHandler(parser, dbConnectionDto);
                  RulesHttpHandler rulesHandler = new RulesHttpHandler(parser, dbConnectionDto);
                  TelemetryHttpHandler telemetryHandler = new TelemetryHttpHandler(parser, dbConnectionDto);
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

      initDataBase(dbConnectionDto);

      ChannelFuture future = boot.bind(ServerConfig.PORT).sync();

      future.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  private static void runTelegramBot(String[] args) {
    IoTServiceBotApplication.main(args);
  }

  private static void initDataBase(DbConnectionDto dbConnectionDto) {
    UsersRepository createUserTable = new UsersRepository(dbConnectionDto);
    createUserTable.createTable();
    DevicesRepository createDeviceTable = new DevicesRepository(dbConnectionDto);
    createDeviceTable.createTable();
    RulesRepository createRuleTable = new RulesRepository(dbConnectionDto);
    createRuleTable.createTable();
    TelegramTokenRepository telegramTokenRepository = new TelegramTokenRepository(dbConnectionDto);
    telegramTokenRepository.createTable();
  }
}
