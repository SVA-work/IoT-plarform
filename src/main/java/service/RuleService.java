package service;

import config.ServerConfig;

import dto.entity.DeviceDto;
import dto.entity.RuleDto;
import dto.entity.UserDto;
import tables.DevicesRepository;
import tables.UsersRepository;
import tables.RulesRepository;

import java.util.List;
import java.util.Objects;

public class RuleService {

  private final UsersRepository usersRepository = new UsersRepository();
  private final DevicesRepository devicesRepository = new DevicesRepository();
  private final RulesRepository rulesRepository = new RulesRepository();

  public String getAllAvailableRules() {
    StringBuilder info = new StringBuilder();
    info.append("1) Датчик температуры. \n" +
            "Отправьте запрос на адрес: " + ServerConfig.LINK_APPLY_RULE + "\n" +
            "Укажите название устройства и приемлемую для вас температуры в таком формате: {login: 99, token: 123, rule: Temparature/lowTemperature/hightTemperature}");
    return info.toString();
  }

  public boolean existenceUser(UserDto userDto) {
    for (UserDto currentUserDto : usersRepository.getAll()) {
      if (userDto.getLogin().equals(currentUserDto.getLogin())) {
        return true;
      }
    }
    return false;
  }

  public boolean existenceUserDevice(UserDto userDto, DeviceDto deviceDto) {
    List<DeviceDto> allDevices = usersRepository.devicesOfUser(userDto);
    if (allDevices != null) {
      for (DeviceDto currentMessage : allDevices) {
        String token = getTokenbyDeviceId(userDto, currentMessage);
        currentMessage.setToken(token);
        if (currentMessage.getToken().equals(deviceDto.getToken())) {
          return true;
        }
      }
      return false;
    } else {
      return false;
    }
  }

  //public String setDeviceRules(RuleDto ruleDto) {
  //}

  public String applyRule(UserDto userDto, DeviceDto deviceDto, RuleDto ruleDto) {
    DeviceService device = new DeviceService();
    userDto.setUserId(device.getUserIdByLogin(userDto));
    deviceDto.setDeviceId(getDeviceIdByToken(userDto, deviceDto));
    if (existenceUser(userDto)) {
      if (existenceUserDevice(userDto, deviceDto)) {

        ruleDto.setDeviceId(getDeviceIdByToken(userDto, deviceDto));

        rulesRepository.create(ruleDto);

        return "Правила успешно добавлены.";
      } else {
        return "У вас нет такого устройства.";
      }
    } else {
      return "Пользователя с таким логином не существует.";
    }
  }

  public String getDeviceRules(UserDto userDto, DeviceDto deviceDto) {
    UserService user = new UserService();
    if (user.existenceUser(userDto)) {
      if (getDeviceIdByToken(userDto, deviceDto) != null) {
        deviceDto.setDeviceId(getDeviceIdByToken(userDto, deviceDto));
  
        List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(deviceDto);
  
        StringBuilder info = new StringBuilder();
        boolean hasAnyRule = false;
  
        for (RuleDto currentRuleDto : allRulesOfDevice) {
          info.append(currentRuleDto.getRule()).append("\n");
          hasAnyRule = true;
        }
        if (hasAnyRule) {
          String result = info.toString();
          return result.substring(0, result.length() - 1);
        }
        return "У данного устройства нет правил.";
      } else {
        return "Такого устройства нет";
      }
    } else {
      return "Такого пользователя нет";
    }
  }

  public String deleteDeviceRule(UserDto userDto, DeviceDto deviceDto, RuleDto ruleDto) {
    if (getDeviceIdByToken(userDto, deviceDto) != null) {
      deviceDto.setDeviceId(getDeviceIdByToken(userDto, deviceDto));
      ruleDto.setRuleId(getRuleIdByRule(deviceDto, ruleDto));
      List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(deviceDto);

      boolean hasDeletedAnyRule = false;
      for (RuleDto currentRuleDto : allRulesOfDevice) {
        if (Objects.equals(currentRuleDto.getRule(), ruleDto.getRule())) {
          rulesRepository.delete(ruleDto);
          hasDeletedAnyRule = true;
          break;
        }
      }
      if (!hasDeletedAnyRule) {
        return "У данного устройства нет правила с таким названием.";
      }
      return "Правило успешно удалено.";
    } else {
      return "Такого устройства не существует";
    }
  }

  public String getDeviceIdByToken(UserDto userDto, DeviceDto deviceDto) {
    for (UserDto currentUserDto : usersRepository.getAll()) {
      if (userDto.getLogin().equals(currentUserDto.getLogin())) {
        for (DeviceDto currentDeviceDto : usersRepository.devicesOfUser(currentUserDto)) {
          if (deviceDto.getToken().equals(currentDeviceDto.getToken())) {
            return currentDeviceDto.getDeviceId();
          }
        }
      }
    }
    return null;
  }

  public String getTokenbyDeviceId(UserDto userDto, DeviceDto deviceDto) {
    for (UserDto currentUserDto : usersRepository.getAll()) {
      if (userDto.getLogin().equals(currentUserDto.getLogin())) {
        for (DeviceDto currentDeviceDto : usersRepository.devicesOfUser(currentUserDto)) {
          if (deviceDto.getDeviceId().equals(currentDeviceDto.getDeviceId())) {
            return currentDeviceDto.getToken();
          }
        }
      }
    }
    return null;
  }

  public String getRuleIdByRule(DeviceDto deviceDto, RuleDto ruleDto) {
    for (RuleDto ruleMessage : rulesRepository.getAll()) {
      if (deviceDto.getDeviceId().equals(ruleMessage.getDeviceId()) && (ruleDto.getRule().equals(ruleMessage.getRule()))) {
        return ruleMessage.getRuleId();
      }
    }
    return null;
  }

}
