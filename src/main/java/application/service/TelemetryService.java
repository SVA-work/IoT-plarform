package application.service;

import application.config.ServerConfig;
import application.dto.request.devices.MicroclimateSensor;
import application.dto.response.TelemetryResponse;
import application.entity.Device;
import application.entity.Rule;
import application.entity.TelegramToken;
import application.entity.User;
import application.kafka.Command;
import application.kafka.dto.KafkaMessage;
import application.kafka.KafkaProducerService;
import application.kafka.dto.SaveTelemetryDto;
import application.kafka.dto.Telemetry;
import application.repository.DeviceRepository;
import application.telegrambot.bot.IoTServiceBot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class TelemetryService {

  private final DeviceRepository devicesRepository;
  private final KafkaProducerService kafkaProducerService;

  public String decodeBase64(String base64Data) {
    if (base64Data == null || base64Data.isEmpty()) {
      return "";
    }
    byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  public ResponseEntity<Void> reportProcessingAndSend(MicroclimateSensor message) throws JsonProcessingException {

    MicroclimateSensor infoAboutDevice = getMicroclimateSensorInfoPackage(message);
    infoAboutDevice.setUuid(message.getUuid());

    return sendReport(infoAboutDevice);
  }

  @Transactional
  public ResponseEntity<Void> sendReport(MicroclimateSensor infoAboutDevice) {

    Optional<Device> optionalDevice = devicesRepository.findByUuid(infoAboutDevice.getUuid());
    if (optionalDevice.isEmpty()) {
      log.error("Устройство \"" + infoAboutDevice.getUuid() + "\" не найдено");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
    }

    log.info("Устройство \"" + infoAboutDevice.getUuid() + "\" найдено");

    Device device = optionalDevice.get();

    saveTelemetry(infoAboutDevice, device);

    User user = device.getUser();
    TelegramToken telegramToken = user.getTelegramToken();
    String token = telegramToken.getToken();
    List<Rule> allRulesOfDevice = device.getRules();

    for (Rule rule : allRulesOfDevice) {
      String ruleName = rule.getRule();
      String lowestValue = rule.getLowestValue().toString();
      String highestValue = rule.getHighestValue().toString();
      String[] parts = {ruleName, lowestValue, highestValue};
      if (parts[0].equals("Temperature")) {
        temperatureCheck(parts, device, infoAboutDevice, token);
        return ResponseEntity.ok().build();
      }
    }
    return ResponseEntity.noContent().build();
  }

  private MicroclimateSensor getMicroclimateSensorInfoPackage(MicroclimateSensor message) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String base64Message = message.getMessage();
    String decodedMessage = decodeBase64(base64Message);
    return objectMapper.readValue(decodedMessage, MicroclimateSensor.class);
  }

  private void temperatureCheck(String[] parts, Device device, MicroclimateSensor infoAboutDevice, String token) {
    IoTServiceBot iotServiceBot = new IoTServiceBot(ServerConfig.BOT_TOKEN);
    double deviceTemperature = Float.parseFloat(infoAboutDevice.getTemperature());
    double lowTemperature = Float.parseFloat(parts[1]);
    double highTemperature = Float.parseFloat(parts[2]);


    if (deviceTemperature < lowTemperature) {
      log.info("Правило температуры сработало для устройства \"" + device.getUuid() + "\"");
      iotServiceBot.sendLowerTempNotification(token, device.getUuid(), device.getType(), parts[1]);
    }
    if (deviceTemperature > highTemperature) {
      log.info("Правило температуры сработало для устройства \"" + device.getUuid() + "\"");
      iotServiceBot.sendHighTempNotification(token, device.getUuid(), device.getType(), parts[2]);
    }
  }

  private void saveTelemetry(MicroclimateSensor infoAboutDevice, Device device) {
    Telemetry telemetry = new Telemetry();
    telemetry.setTemperature(infoAboutDevice.getTemperature());
    telemetry.setDeviceId(device.getId().toString());
    SaveTelemetryDto saveTelemetryDto =
        new SaveTelemetryDto(Command.WRITE_TELEMETRY, telemetry);
    kafkaProducerService.sendMessageWriteTelemetry(saveTelemetryDto);
  }
}