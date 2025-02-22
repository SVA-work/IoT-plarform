package application.service;

import application.config.ServerConfig;
import application.dto.request.devices.MicroclimateSensor;
import application.entity.Device;
import application.entity.Rule;
import application.entity.TelegramToken;
import application.entity.User;
import application.repository.DeviceRepository;
import application.telegrambot.bot.IoTServiceBot;
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

    public String decodeBase64(String base64Data) {
        if (base64Data == null || base64Data.isEmpty()) {
            return "";
        }
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public ResponseEntity<Void> reportProcessing(MicroclimateSensor infoAboutDevice) {
        Optional<Device> optionalDevice = devicesRepository.findByUuid(infoAboutDevice.getUuid());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + infoAboutDevice.getUuid() + "\" не найдено");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        log.info("Устройство \"" + infoAboutDevice.getUuid() + "\" найдено");

        Device device = optionalDevice.get();
        User user = device.getUser();
        TelegramToken telegramToken = user.getTelegramToken();
        String token = telegramToken.getToken();
        List<Rule> allRulesOfDevice = device.getRules();

        for (Rule rule : allRulesOfDevice) {
            String[] parts = rule.getRule().split("/");
            if (parts[0].equals("Temperature")) {
                TemperatureCheck(parts, device, infoAboutDevice, token);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.noContent().build();
    }

    private void TemperatureCheck(String[] parts, Device device, MicroclimateSensor InfoAboutDevice, String token) {
        IoTServiceBot iotServiceBot = new IoTServiceBot(ServerConfig.botToken);
        double deviceTemperature = Float.parseFloat(InfoAboutDevice.getTemperature());
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
}