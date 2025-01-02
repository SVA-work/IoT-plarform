package service;

import config.ServerConfig;

import dto.DeviceDto;
import dto.RuleDto;
import dto.UserDto;
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
            "Отправьте запрос на адрес: " + ServerConfig.LINK_ALL_AVAILABLE_RULES + "\n" +
            "Укажите название устройства и приемлемую для вас температуры в таком формате: {login: 99, token: 123, lowTemperature: 15, hightTemperature: 20}");
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
    for (DeviceDto currentDeviceDto : allDevices) {
      if (deviceDto.getDeviceId().equals(currentDeviceDto.getDeviceId())) {
        return true;
      }
    }
    return false;
  }

  //public String setDeviceRules() {
  //}

  public String applyRule(UserDto userDto, DeviceDto deviceDto, RuleDto ruleDto) {
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
    deviceDto.setDeviceId(getDeviceIdByToken(userDto, deviceDto));

    List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(deviceDto);

    StringBuilder info = new StringBuilder();
    boolean hasAnyRule = false;

    for (RuleDto currentRuleDto : allRulesOfDevice) {
      info.append(currentRuleDto.getRule()).append("\n");
      hasAnyRule = true;
    }
    if (hasAnyRule) {
      return info.toString();
    }
    return "У данного устройства нет правил.";

  }

  public String deleteDeviceRule(UserDto userDto, DeviceDto deviceDto, RuleDto ruleDto) {
    deviceDto.setDeviceId(getDeviceIdByToken(userDto, deviceDto));
    List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(deviceDto);

    boolean hasDeletedAnyRule = false;
    for (RuleDto currentRuleDto : allRulesOfDevice) {
      if (Objects.equals(currentRuleDto.getRule(), ruleDto.getRule())) {
        rulesRepository.delete(currentRuleDto);
        hasDeletedAnyRule = true;
        break;
      }
    }
    if (!hasDeletedAnyRule) {
      return "У данного устройства нет правила с таким названием.";
    }
    return "Правило успешно удалено.";
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

}
