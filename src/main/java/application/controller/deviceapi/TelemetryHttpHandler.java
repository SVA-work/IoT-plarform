package application.controller.deviceapi;

import application.config.ServerConfig;
import application.dto.DbConnectionDto;
import application.dto.devices.MicroclimateSensor;
import application.netty.library.AbstractHttpMappingHandler;
import application.netty.library.annotation.Post;
import application.netty.library.annotation.RequestBody;
import application.netty.library.json.JsonParser;
import application.service.TelemetryService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@RestController
public class TelemetryHttpHandler extends AbstractHttpMappingHandler {
    private final JsonParser jsonParser;
    private final TelemetryService telemetryService;


    public TelemetryHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
        super(parser);
        this.jsonParser = parser;
        telemetryService = new TelemetryService(dbConnectionDto);
    }

    @Post(ServerConfig.SHORT_LINK_REPORT)
    public DefaultFullHttpResponse report(@RequestBody MicroclimateSensor message) {
        String uuid = message.getUuid();
        String base64Message = message.getBase64Message();
        String decodedMessage = telemetryService.decodeBase64(base64Message);
        ByteBuf byteBuf = Unpooled.copiedBuffer(decodedMessage, StandardCharsets.UTF_8);
        Object parsedObject = jsonParser.parse(decodedMessage, byteBuf, MicroclimateSensor.class);
        MicroclimateSensor deviceDto = (MicroclimateSensor) parsedObject;
        telemetryService.reportProcessing(uuid, deviceDto);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer("", StandardCharsets.UTF_8));
    }
}
