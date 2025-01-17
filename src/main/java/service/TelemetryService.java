package service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import dto.devices.MicroclimateSensor;
import dto.DbConnectionDto;
import dto.entity.DeviceDto;
import dto.entity.RuleDto;
import dto.entity.TelegramTokenDto;

import tables.DevicesRepository;
import tables.TelegramTokenRepository;

import telegrambot.bot.IoTServiceBot;

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

  public void reportProcessing(String uuid, MicroclimateSensor InfoAboutdevice) {
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
        TemperatureСheck(parts, device, InfoAboutdevice, telegramToken);
      }
    }
  }

  public String getDeviceIdByToken(DeviceDto deviceDto) {
    List<DeviceDto> allDevices = devicesRepository.getAll();

    for (DeviceDto correntDevice : allDevices) {
      if (correntDevice.getToken().equals(deviceDto.getToken())) {
        return correntDevice.getDeviceId();
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

  public void TemperatureСheck(String[] parts, DeviceDto device, MicroclimateSensor InfoAboutdevice, TelegramTokenDto telegramToken) {
    float deviceTemperature = Float.parseFloat(InfoAboutdevice.getTemperature());
    float lowTemperature = Float.parseFloat(parts[1]);
    float hightTemperature = Float.parseFloat(parts[2]);

    if (deviceTemperature < lowTemperature) {
      iotServiceBot.sendLowerTempNotification(telegramToken.getTelegramToken(), device.getToken(), parts[1]);
    }
    if (deviceTemperature > hightTemperature) {
      iotServiceBot.sendHighTempNotification(telegramToken.getTelegramToken(), device.getToken(), parts[2]);
    }
  }
}
