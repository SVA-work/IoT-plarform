package application.mqtt;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class MqttServer {

    private static final Logger logger = LoggerFactory.getLogger(MqttServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private final ChannelGroup channelGroup;
    private final MqttServerInitializer mqttServerInitializer;

    public MqttServer(ChannelGroup channelGroup, MqttServerInitializer mqttServerInitializer) {
        this.channelGroup = channelGroup;
        this.mqttServerInitializer = mqttServerInitializer;
    }

    @PostConstruct
    public void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(mqttServerInitializer)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                Channel channel = bootstrap.bind(1884).sync().channel();
                channelGroup.add(channel);
                logger.info("MQTT сервер стартовал на сервере 1884");
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("Старт MQTT сервера прерван", e);
            } catch (Exception e) {
                logger.error("Ошибка старта MQTT сервера", e);
            } finally {
                stop();
            }
        }).start();
    }

    @PreDestroy
    public void stop() {
        logger.info("Остановка MQTT сервера");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (channelGroup != null) {
            channelGroup.close().awaitUninterruptibly();
        }
        logger.info("MQTT сервер остановлен");
    }

    public ChannelGroup getChannels() {
        return channelGroup;
    }
}