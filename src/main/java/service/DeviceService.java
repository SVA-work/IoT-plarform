package service;

import dto.DbConnectionDto;
import dto.entity.UserDto;
import dto.entity.DeviceDto;
import dto.entity.RuleDto;
import repository.DevicesRepository;
import repository.UsersRepository;

import java.util.List;
import java.util.Objects;

public class DeviceService {

  private final UsersRepository usersRepository;
  private final DevicesRepository devicesRepository;
  private final DbConnectionDto dbConnectionDto;

  public DeviceService(DbConnectionDto dbConnectionDto) {
    usersRepository = new UsersRepository(dbConnectionDto);
    devicesRepository = new DevicesRepository(dbConnectionDto);
    this.dbConnectionDto = dbConnectionDto;
  }

  public String listOfDevicesOfUser(UserDto userDto) {

    userDto.setUserId(getUserIdByLogin(userDto));

    List<DeviceDto> allDevicesOfUser = usersRepository.devicesOfUser(userDto);

    StringBuilder info = new StringBuilder();
    boolean hasAnyDevice = false;
    for (DeviceDto currentDeviceDto : allDevicesOfUser) {
      info.append(currentDeviceDto.getToken()).append('\n');
      for (RuleDto ruleDto : devicesRepository.rulesOfDevice(currentDeviceDto)) {
        info.append("  ").append(ruleDto.getRule()).append("\n");
      }
      hasAnyDevice = true;
    }
    if (hasAnyDevice) {
      String result = info.toString();
      return result.substring(0, result.length() - 1);
    }
    return "У вас нет устройств.";
  }

  public String addDevice(UserDto userDto, DeviceDto deviceDto) {
    if (getUserIdByLogin(userDto) != null) {
      RuleService rule = new RuleService(dbConnectionDto);
      userDto.setUserId(getUserIdByLogin(userDto));
      if (!(rule.existenceUserDevice(userDto, deviceDto))) {
        deviceDto.setUserId(getUserIdByLogin(userDto));
        devicesRepository.create(deviceDto);
        return "Устройство успешно добавлено.\n" + "Список ваших устройств:\n" + listOfDevicesOfUser(userDto);
      } else {
        return "У этого пользователя уже есть устройство с таким названием";
      }
    } else {
      return "Нет такого пользователя";
    }
  }

  public String deleteDevice(UserDto userDto, DeviceDto deviceDto) {
    if (getUserIdByLogin(userDto) != null) {
      userDto.setUserId(getUserIdByLogin(userDto));
      List<DeviceDto> allDevicesOfUser = usersRepository.devicesOfUser(userDto);

      boolean hasDeletedAnyDevice = false;
      for (DeviceDto currentDeviceDto : allDevicesOfUser) {
        if (Objects.equals(currentDeviceDto.getToken(), deviceDto.getToken())) {
          devicesRepository.delete(currentDeviceDto);
          hasDeletedAnyDevice = true;
          break;
        }
      }
      if (!hasDeletedAnyDevice) {
        return "У вас нет такого устройства.";
      }

      String infoAboutDevices = listOfDevicesOfUser(userDto);
      if (Objects.equals(infoAboutDevices, "У вас нет устройств.")) {
        return "У вас больше нет устройств.";
      }
      return ("Список ваших устройств:\n" +
              infoAboutDevices);
    } else {
      return "Нет такого пользователя";
    }
  }

  public String getUserIdByLogin(UserDto userDto) {
    for (UserDto currentUserDto : usersRepository.getAll()) {
      if (userDto.getLogin().equals(currentUserDto.getLogin())) {
        return currentUserDto.getUserId();
      }
    }
    return null;
  }
}
