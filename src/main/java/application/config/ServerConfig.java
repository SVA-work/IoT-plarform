package application.config;

import application.controller.deviceapi.TelemetryHttpController;
import application.netty.library.json.JsonParserDefault;
import application.service.TelemetryService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ServerConfig {

    private final int serverPort;
    private final MeterRegistry meterRegistry;
    private final TelemetryService telemetryService;

    public ServerConfig(@Value("${server.port}") int port,
                        MeterRegistry meterRegistry,
                        TelemetryService telemetryService) {
        this.serverPort = port;
        this.meterRegistry = meterRegistry;
        this.telemetryService = telemetryService;
    }

    @Bean
    public Channel serverBootstrap() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ExecutorServiceMetrics.monitor(meterRegistry, bossGroup, "netty.boss");
        ExecutorServiceMetrics.monitor(meterRegistry, workerGroup, "netty.worker");

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
                ChannelFuture future = boot.bind(serverPort).sync();
                log.info("Netty server started on port " + serverPort);
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
