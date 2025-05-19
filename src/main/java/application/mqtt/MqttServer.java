package application.mqtt;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class MqttServer {

    private static final Logger logger = LoggerFactory.getLogger(MqttServer.class);
    private final ChannelGroup channelGroup;
    private final MqttServerInitializer mqttServerInitializer;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    @Value("${mqtt.server.port}")
    private int mqttPort;

    @Value("${mqtt.server.backlog}")
    private int backlog;

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
                        .option(ChannelOption.SO_BACKLOG, backlog)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                Channel channel = bootstrap.bind(mqttPort).sync().channel();
                channelGroup.add(channel);
                logger.info("MQTT сервер стартовал на порту {}", mqttPort);
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