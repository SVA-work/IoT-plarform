package service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import dto.DbConnectionDto;
import dto.devices.MicroclimateSensor;
import dto.entity.DeviceDto;
import dto.entity.RuleDto;
import dto.entity.TelegramTokenDto;
import tables.DevicesRepository;
import tables.TelegramTokenRepository;
import telegrambot.bot.IoTServiceBot;

public class TelemetryService {

  private final DevicesRepository devicesRepository;
  //private final IoTServiceBot iotServiceBot;
  private final TelegramTokenRepository telegramTokenRepository;

  public TelemetryService(DbConnectionDto dbConnectionDto) {
    devicesRepository = new DevicesRepository(dbConnectionDto);
    //iotServiceBot = new IoTServiceBot("7614249328:AAF6E3EFO1yLqxlbQhBCR4P977EA8VWxuWY");
    telegramTokenRepository = new TelegramTokenRepository(dbConnectionDto);
  }

  public String decodeBase64(String base64Data) {
    if (base64Data == null || base64Data.isEmpty()) {
      return "";
    }
    byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  public void reportProcessing(String uuid, MicroclimateSensor deviceDto) {
    DeviceDto device = new DeviceDto();

    // Предполагается, что uuid == token
    device.setToken(uuid);
    List<DeviceDto> allDevices = devicesRepository.getAll();
    for (DeviceDto correntDevice : allDevices) {
      if (correntDevice.getToken().equals(uuid)) {
        device.setDeviceId(correntDevice.getDeviceId());
      }
    }

    TelegramTokenDto telegramToken = new TelegramTokenDto();
    device = devicesRepository.getById(device);
    telegramToken.setUserId(device.getUserId());
    List<TelegramTokenDto> telegramTokens = telegramTokenRepository.getAll();
    for (TelegramTokenDto telegramTokennDto : telegramTokens) {
      if (telegramToken.getUserId().equals(telegramTokennDto.getUserId())) {
        telegramToken.setTelegramToken(telegramTokennDto.getTelegramToken());
      }
    }

    List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(device);
    for (RuleDto ruleDto : allRulesOfDevice) {
      String rule = ruleDto.getRule();
      String[] parts = rule.split("/");
      if (parts[0].equals("Temperature")) {
        float deviceTemperature = Float.parseFloat(deviceDto.getTemperature());
        float lowTemperature = Float.parseFloat(parts[1]);
        float hightTemperature = Float.parseFloat(parts[2]);
        if (deviceTemperature < lowTemperature) {
          //iotServiceBot.sendLowerTempNotification(telegramToken.getTelegramToken(), device.getToken(), parts[1]);
        }
        if (deviceTemperature > hightTemperature) {
          //iotServiceBot.sendLowerTempNotification(telegramToken.getTelegramToken(), device.getToken(), parts[2]);
        }
      }
    }
  }
}
