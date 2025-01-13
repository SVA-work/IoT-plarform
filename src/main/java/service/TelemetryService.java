package service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import dto.devices.MicroclimateSensor;
import dto.entity.DeviceDto;
import dto.entity.RuleDto;
import tables.DevicesRepository;

public class TelemetryService {

  private final DevicesRepository devicesRepository = new DevicesRepository();

  public TelemetryService() {
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

    List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(device);
    for (RuleDto ruleDto : allRulesOfDevice) {
      String rule = ruleDto.getRule();
      String[] parts = rule.split("/");
      if (parts[0].equals("Temperature")) {
        float deviceTemperature = Float.parseFloat(deviceDto.getTemperature());
        float lowTemperature = Float.parseFloat(parts[1]);
        float hightTemperature = Float.parseFloat(parts[2]);
        if (deviceTemperature < lowTemperature) {
          // говорим пользователю, что температура слишком низкая
        }
        if (deviceTemperature > hightTemperature) {
          // говорим пользователю, что температура слишком высокая
        }
      }
    }
  }
}
