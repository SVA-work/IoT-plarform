package application.controller.deviceapi;

import application.dto.request.devices.MicroclimateSensor;
import application.service.TelemetryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;
    private final ObjectMapper objectMapper;

    @PostMapping()
    public ResponseEntity<Void> report(@RequestBody MicroclimateSensor message) throws JsonProcessingException {

        log.info("Полученно сообщение от устройства по http");

        String base64Message = message.getMessage();
        String decodedMessage = telemetryService.decodeBase64(base64Message);
        MicroclimateSensor deviceRequest = objectMapper.readValue(decodedMessage, MicroclimateSensor.class);
        deviceRequest.setUuid(message.getUuid());
        return telemetryService.reportProcessing(deviceRequest);
    }
}
