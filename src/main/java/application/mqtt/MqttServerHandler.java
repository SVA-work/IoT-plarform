package application.mqtt;

import application.dto.request.devices.MicroclimateSensor;
import application.service.TelemetryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MqttServerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private static final Logger logger = LoggerFactory.getLogger(MqttServerHandler.class);
    private final ChannelGroup channelGroup;

    private final TelemetryService telemetryService;
    private final ObjectMapper objectMapper;

    public MqttServerHandler(ChannelGroup channelGroup, ObjectMapper objectMapper, TelemetryService telemetryService) {
        this.channelGroup = channelGroup;
        this.objectMapper = objectMapper;
        this.telemetryService = telemetryService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Соединение установлено: {}", ctx.channel().remoteAddress());
        channelGroup.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) {
        logger.info("Получено MQTT сообщение: {}", msg.fixedHeader().messageType());

        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                handleConnect(ctx, (MqttConnectMessage) msg);
                break;
            case SUBSCRIBE:
                logger.info("Получен запрос на подписку", msg.fixedHeader().messageType());
                break;
            case PUBLISH:
                handlePublish(ctx, (MqttPublishMessage) msg);
                break;
            case DISCONNECT:
                handleDisconnect(ctx);
                break;
            case PINGREQ:
                handlePingreq(ctx);
                break;
            default:
                logger.info("Получен неизвестный тип сообщения: {}", msg.fixedHeader().messageType());
                break;
        }
    }

    private void handleConnect(ChannelHandlerContext ctx, MqttConnectMessage msg) {
        logger.info("Получен запрос на подключение");

        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false)
        );
        ctx.writeAndFlush(connAckMessage);
        logger.info("Соединение успешно установлено");
    }

    private void handlePublish(ChannelHandlerContext ctx, MqttPublishMessage msg) {
        String topic = msg.variableHeader().topicName();
        String message = msg.payload().toString(StandardCharsets.UTF_8);

        logger.info("Получено сообщение");
        logger.info("Тема: {}", topic);
        logger.info("Сообщение: {}", message);

        try {
            MicroclimateSensor sensorData = objectMapper.readValue(message, MicroclimateSensor.class);
            telemetryService.reportProcessingAndSend(sensorData);
        } catch (JsonProcessingException e) {
            logger.error("Ошибка при парсинге сообщения: {}", e.getMessage());
        }
    }

    private void handlePingreq(ChannelHandlerContext ctx) {
        MqttMessage pingRespMessage = new MqttMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0)
        );
        ctx.writeAndFlush(pingRespMessage);
        logger.info("Отправлен PINGRESP");
    }

    private void handleDisconnect(ChannelHandlerContext ctx) {
        if (channelGroup.contains(ctx.channel())) {
            channelGroup.remove(ctx.channel());
            logger.info("Канал закрыт: {}", ctx.channel().remoteAddress());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Ошибка в канале: {}, причина: {}", ctx.channel().remoteAddress(), cause.getMessage());

        if (channelGroup.contains(ctx.channel())) {
            channelGroup.remove(ctx.channel());
            logger.info("Канал удален из группы: {}", ctx.channel().remoteAddress());
        }
        ctx.close();
    }
}
