package application;

import application.config.ServerConfig;
//import application.controller.deviceapi.TelemetryHttpController;
//import application.netty.library.json.JsonParserDefault;
import application.controller.deviceapi.TelemetryHttpController;
import application.netty.library.json.JsonParserDefault;
import application.service.TelemetryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties
@Slf4j
public class ServerLauncher {

    public static void main(String[] args) {
        SpringApplication.run(ServerLauncher.class, args);
    }

    private final TelemetryService telemetryService;

    @Bean
    public Channel serverBootstrap() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) {
                    JsonParserDefault parser = new JsonParserDefault();
                    TelemetryHttpController telemetryHttpController = new TelemetryHttpController(parser, telemetryService);
                    channel.pipeline()
                        .addLast("HttpServerCodec", new HttpServerCodec())
                        .addLast("HttpServerKeepAlive", new HttpServerKeepAliveHandler())
                        .addLast("HttpObjectAggregator", new HttpObjectAggregator(10 * 1024 * 102, true))
                        .addLast("HttpChunkedWrite", new ChunkedWriteHandler())
                        .addLast("User HttpHandler", telemetryHttpController);
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

        new Thread(() -> {
            try {
                ChannelFuture future = boot.bind(8080).sync();
                log.info("Netty server started on port 8080");
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("Netty failed to start", e);
                Thread.currentThread().interrupt();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();

        return null;
    }
}
