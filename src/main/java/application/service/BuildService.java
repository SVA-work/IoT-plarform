package application.service;

import application.dto.response.DeviceResponse;
import application.dto.response.RuleResponse;
import application.dto.response.UserResponse;
import application.entity.Device;
import application.entity.Rule;
import application.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuildService {

  public DeviceResponse buildDeviceResponse(@NotNull Device device) {
    DeviceResponse deviceResponse = new DeviceResponse();
    deviceResponse.setUuid(device.getUuid());
    deviceResponse.setType(device.getType());
    deviceResponse.setDeviceName(device.getDeviceName());
    deviceResponse.setLogin(device.getUser().getLogin());

    List<RuleResponse> rulesResponse = new ArrayList<>();
    List<Rule> rules = device.getRules();

    if (rules != null) {
      for (Rule rule : rules) {
        rulesResponse.add(buildRuleResponse(rule));
      }
    }
    deviceResponse.setRules(rulesResponse);

    return deviceResponse;
  }

  public RuleResponse buildRuleResponse(@NotNull Rule rule) {
    RuleResponse ruleResponse = new RuleResponse();
    ruleResponse.setRule(rule.getRuleType());
    ruleResponse.setValue(rule.getValue());
    ruleResponse.setComparison(rule.getComparison());
    ruleResponse.setDeviceName(rule.getDevice().getDeviceName());
    return ruleResponse;
  }

  public UserResponse buildUserResponse(@NotNull User user) {
    UserResponse userResponse = new UserResponse();
    userResponse.setLogin(user.getLogin());
    userResponse.setPassword(user.getPassword());
    userResponse.setTelegramToken(user.getTelegramToken().getToken());

    List<DeviceResponse> devicesResponse = new ArrayList<>();
    List<Device> devices = user.getDevices();

    if (devices != null) {
      for (Device device : devices) {
        devicesResponse.add(buildDeviceResponse(device));
      }
    }
    userResponse.setDevices(devicesResponse);
    return userResponse;
  }
}
