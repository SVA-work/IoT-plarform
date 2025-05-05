package application.service;

import application.config.ServerConfig;
import application.dto.request.devices.MicroclimateSensor;
import application.dto.response.TelemetryResponse;
import application.entity.Device;
import application.entity.Rule;
import application.entity.TelegramToken;
import application.entity.Telemetry;
import application.entity.User;
import application.repository.DeviceRepository;
import application.repository.TelemetryRepository;
import application.telegrambot.bot.IoTServiceBot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final TelemetryRepository telemetryRepository;

    public String decodeBase64(String base64Data) {
        if (base64Data == null || base64Data.isEmpty()) {
            return "";
        }
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public void reportProcessingAndSend(MicroclimateSensor message) throws JsonProcessingException {
        MicroclimateSensor infoAboutDevice = getMicroclimateSensorInfoPackage(message);
        infoAboutDevice.setUuid(message.getUuid());

        sendReport(infoAboutDevice);
    }

    @Transactional
    public void sendReport(MicroclimateSensor infoAboutDevice) {
        Optional<Device> optionalDevice = devicesRepository.findByUuidWithRulesAndToken(infoAboutDevice.getUuid());

        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + infoAboutDevice.getUuid() + "\" не найдено");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        log.info("Устройство \"" + infoAboutDevice.getUuid() + "\" найдено");
        Device device = optionalDevice.get();

        Telemetry telemetry = new Telemetry();
        telemetry.setTemperature(infoAboutDevice.getTemperature());
        telemetry.setDevice(device);
        telemetryRepository.save(telemetry);

        User user = device.getUser();
        TelegramToken telegramToken = user.getTelegramToken();
        String token = telegramToken.getToken();
        List<Rule> allRulesOfDevice = device.getRules();

        for (Rule rule : allRulesOfDevice) {
            String ruleName = rule.getRule();
            String value = rule.getValue().toString();
            String compare = rule.getComparison();
            String[] parts = {ruleName, value, compare};
            if (parts[0].equals("Temperature")) {
                temperatureCheck(parts, device, infoAboutDevice, token);
            }
        }
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
        double value = Float.parseFloat(parts[1]);
        String compare = parts[2];

        if (deviceTemperature < value && (compare.equals(">") || compare.equals("="))) {
            log.info("Правило температуры сработало для устройства \"" + device.getUuid() + "\"");
            iotServiceBot.sendLowerTempNotification(token, device.getUuid(), device.getType(), parts[1]);
        }
        if (deviceTemperature > value && (compare.equals("<") || compare.equals("="))) {
            log.info("Правило температуры сработало для устройства \"" + device.getUuid() + "\"");
            iotServiceBot.sendHighTempNotification(token, device.getUuid(), device.getType(), parts[2]);
        }
        if (deviceTemperature == value && (compare.equals("!="))) {
            log.info("Правило температуры сработало для устройства \"" + device.getUuid() + "\"");
            iotServiceBot.sendForbiddenValueTempNotification(token, device.getUuid(), device.getType(), parts[2]);
        }
    }

    public TelemetryResponse buildTelemetryResponse(Telemetry telemetry) {
        TelemetryResponse telemetryResponse = new TelemetryResponse();
        telemetryResponse.setTemperature(telemetry.getTemperature());
        telemetryResponse.setSnr(telemetry.getSnr());
        telemetryResponse.setRssi(telemetry.getRssi());
        telemetryResponse.setPressure(telemetry.getPressure());
        telemetryResponse.setHumidity(telemetry.getHumidity());
        telemetryResponse.setTime(telemetry.getTime());
        return telemetryResponse;

    }
}