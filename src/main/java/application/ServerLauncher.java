package application;

import application.config.DbConfig;
import application.config.ServerConfig;
import application.controller.deviceapi.TelemetryHttpHandler;
import application.controller.userapi.DeviceHttpHandler;
import application.controller.userapi.RulesHttpHandler;
import application.controller.userapi.UserHttpHandler;
import application.dto.DbConnectionDto;
import application.netty.library.json.JsonParserDefault;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties
public class ServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(ServerLauncher.class, args);
    }

    @Bean
    public Channel serverBootstrap() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        DbConnectionDto dbConnectionDto = new DbConnectionDto();
        dbConnectionDto.url = DbConfig.URL;
        dbConnectionDto.user = DbConfig.USER;
        dbConnectionDto.password = DbConfig.PASSWORD;

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            JsonParserDefault parser = new JsonParserDefault();
                            UserHttpHandler userHandler = new UserHttpHandler(parser, dbConnectionDto);
                            DeviceHttpHandler deviceHandler = new DeviceHttpHandler(parser, dbConnectionDto);
                            RulesHttpHandler rulesHandler = new RulesHttpHandler(parser, dbConnectionDto);
                            TelemetryHttpHandler telemetryHandler = new TelemetryHttpHandler(parser, dbConnectionDto);
                            userHandler.setNextHandler(deviceHandler);
                            deviceHandler.setNextHandler(rulesHandler);
                            rulesHandler.setNextHandler(telemetryHandler);
                            channel
                                    .pipeline()
                                    .addLast("HttpServerCodec", new HttpServerCodec())
                                    .addLast("HttpServerKeepAlive", new HttpServerKeepAliveHandler())
                                    .addLast("HttpObjectAggregator", new HttpObjectAggregator(ServerConfig.MAX_CONTENT_LENGTH, true))
                                    .addLast("HttpChunkedWrite", new ChunkedWriteHandler())
                                    .addLast("User HttpHandler", userHandler);
                        }
                    }).option(ChannelOption.SO_BACKLOG, ServerConfig.MAX_BACK_LOG_SIZE)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = boot.bind(ServerConfig.PORT).sync();

            future.channel().closeFuture().sync();
            return future.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

//    private void runTelegramBot(String[] args) {
//        IoTServiceBotApplication.main(args);
//    }
//
//    private void initDataBase(DbConnectionDto dbConnectionDto) {
//        UsersRepository createUserTable = new UsersRepository(dbConnectionDto);
//        createUserTable.createTable();
//        DevicesRepository createDeviceTable = new DevicesRepository(dbConnectionDto);
//        createDeviceTable.createTable();
//        RulesRepository createRuleTable = new RulesRepository(dbConnectionDto);
//        createRuleTable.createTable();
//        TelegramTokenRepository telegramTokenRepository = new TelegramTokenRepository(dbConnectionDto);
//        telegramTokenRepository.createTable();
//    }
}
