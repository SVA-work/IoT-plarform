package application.mqtt;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import application.service.TelemetryService;

@Component
public class MqttServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(MqttServerInitializer.class);

    private final ChannelGroup channelGroup;
    private final ObjectMapper objectMapper;
    private final TelemetryService telemetryService;

    @Value("${mqtt.server.max-message-size}")
    private int maxMessageSize;

    @Autowired
    public MqttServerInitializer(ChannelGroup channelGroup, ObjectMapper objectMapper, TelemetryService telemetryService) {
        this.channelGroup = channelGroup;
        this.objectMapper = objectMapper;
        this.telemetryService = telemetryService;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        logger.info("Инициализация канала: {}", ch.remoteAddress());

        ch.pipeline().addLast(new MqttDecoder(maxMessageSize));
        ch.pipeline().addLast(MqttEncoder.INSTANCE);
        ch.pipeline().addLast(new MqttServerHandler(channelGroup, objectMapper, telemetryService));
    }
}
