package service;

import dto.DbConnectionDto;
import dto.devices.MicroclimateSensor;
import dto.entity.DeviceDto;
import dto.entity.RuleDto;
import dto.entity.TelegramTokenDto;
import repository.DevicesRepository;
import repository.TelegramTokenRepository;
import telegrambot.bot.IoTServiceBot;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class TelemetryService {

  private final DevicesRepository devicesRepository;
  private final TelegramTokenRepository telegramTokenRepository;
  private final IoTServiceBot iotServiceBot;

  public TelemetryService(DbConnectionDto dbConnectionDto) {
    devicesRepository = new DevicesRepository(dbConnectionDto);
    telegramTokenRepository = new TelegramTokenRepository(dbConnectionDto);
    iotServiceBot = new IoTServiceBot("7614249328:AAF6E3EFO1yLqxlbQhBCR4P977EA8VWxuWY");
  }

  public String decodeBase64(String base64Data) {
    if (base64Data == null || base64Data.isEmpty()) {
      return "";
    }
    byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  public void reportProcessing(String uuid, MicroclimateSensor InfoAboutDevice) {
    DeviceDto device = new DeviceDto();
    device.setToken(uuid);
    device.setDeviceId(getDeviceIdByToken(device));

    TelegramTokenDto telegramToken = new TelegramTokenDto();
    device = devicesRepository.getById(device);
    telegramToken.setUserId(device.getUserId());
    telegramToken.setTelegramToken(getTelegramTokenByUserId(telegramToken));

    List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(device);
    for (RuleDto ruleDto : allRulesOfDevice) {
      String rule = ruleDto.getRule();
      String[] parts = rule.split("/");
      if (parts[0].equals("Temperature")) {
        TemperatureCheck(parts, device, InfoAboutDevice, telegramToken);
      }
    }
  }

  public String getDeviceIdByToken(DeviceDto deviceDto) {
    List<DeviceDto> allDevices = devicesRepository.getAll();

    for (DeviceDto currentDevice : allDevices) {
      if (currentDevice.getToken().equals(deviceDto.getToken())) {
        return currentDevice.getDeviceId();
      }
    }
    return null;
  }

  public String getTelegramTokenByUserId(TelegramTokenDto telegramToken) {
    List<TelegramTokenDto> telegramTokens = telegramTokenRepository.getAll();
    for (TelegramTokenDto telegramTokenDto : telegramTokens) {
      if (telegramToken.getUserId().equals(telegramTokenDto.getUserId())) {
        return telegramTokenDto.getTelegramToken();
      }
    }
    return null;
  }

  public void TemperatureCheck(String[] parts, DeviceDto device, MicroclimateSensor InfoAboutDevice, TelegramTokenDto telegramToken) {
    double deviceTemperature = Float.parseFloat(InfoAboutDevice.getTemperature());
    double lowTemperature = Float.parseFloat(parts[1]);
    double highTemperature = Float.parseFloat(parts[2]);

    if (deviceTemperature < lowTemperature) {
      iotServiceBot.sendLowerTempNotification(telegramToken.getTelegramToken(), device.getToken(), parts[1]);
    }
    if (deviceTemperature > highTemperature) {
      iotServiceBot.sendHighTempNotification(telegramToken.getTelegramToken(), device.getToken(), parts[2]);
    }
  }
}
