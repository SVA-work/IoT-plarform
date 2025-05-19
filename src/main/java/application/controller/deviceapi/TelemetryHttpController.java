package application.controller.deviceapi;

import application.dto.request.devices.MicroclimateSensor;
import application.netty.library.AbstractHttpMappingHandler;
import application.netty.library.annotation.Post;
import application.netty.library.annotation.RequestBody;
import application.netty.library.json.JsonParserDefault;
import application.service.TelemetryService;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TelemetryHttpController extends AbstractHttpMappingHandler {

    @Autowired
    private final TelemetryService telemetryService;

    public TelemetryHttpController(JsonParserDefault parser,
                                   TelemetryService telemetryService) {
        super(parser);
        this.telemetryService = telemetryService;
    }

    @Post("/telemetry")
    public FullHttpResponse report(@RequestBody MicroclimateSensor message) {
        log.info("Полученно сообщение от устройства по http");
        try {
            telemetryService.reportProcessingAndSend(message);
            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK
            );
        } catch (Exception e) {
            return new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.wrappedBuffer(e.getMessage().getBytes())
            );
        }
    }
}
