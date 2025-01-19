package controller.deviceapi;

import config.ServerConfig;
import dto.DbConnectionDto;
import dto.devices.MicroclimateSensor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import netty.library.AbstractHttpMappingHandler;
import netty.library.annotation.Post;
import netty.library.annotation.RequestBody;
import netty.library.json.JsonParser;
import service.TelemetryService;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class TelemetryHttpHandler extends AbstractHttpMappingHandler {
    private final JsonParser jsonParser;
    private final TelemetryService telemetryService;


    public TelemetryHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
        super(parser);
        this.jsonParser = parser;
        telemetryService = new TelemetryService(dbConnectionDto);
    }

    @Post(ServerConfig.SHORT_LINK_REPORT)
    public DefaultFullHttpResponse report(@RequestBody MicroclimateSensor report) {
        String uuid = report.getUuid();
        String message = report.getMessage();
        String decodedMessage = telemetryService.decodeBase64(message);
        ByteBuf byteBuf = Unpooled.copiedBuffer(decodedMessage, StandardCharsets.UTF_8);
        Object parsedObject = jsonParser.parse(decodedMessage, byteBuf, MicroclimateSensor.class);
        MicroclimateSensor deviceDto = (MicroclimateSensor) parsedObject;
        telemetryService.reportProcessing(uuid, deviceDto);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer("", StandardCharsets.UTF_8));
    }
}
