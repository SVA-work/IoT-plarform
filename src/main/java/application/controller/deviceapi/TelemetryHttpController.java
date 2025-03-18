package application.controller.deviceapi;

import application.dto.request.devices.MicroclimateSensor;
import application.netty.library.AbstractHttpMappingHandler;
import application.netty.library.annotation.Post;
import application.netty.library.annotation.RequestBody;
import application.netty.library.json.JsonParserDefault;
import application.service.TelemetryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TelemetryHttpController extends AbstractHttpMappingHandler {

    @Autowired
    private final TelemetryService telemetryService;

    public TelemetryHttpController(JsonParserDefault parser, TelemetryService telemetryService) {
        super(parser);
        this.telemetryService = telemetryService;
    }

    @Post("/telemetry")
    public ResponseEntity<Void> report(@RequestBody MicroclimateSensor message) throws JsonProcessingException {
        log.info("Полученно сообщение от устройства по http");
        return telemetryService.reportProcessingAndSend(message);
    }
}
