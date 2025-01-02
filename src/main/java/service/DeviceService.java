package service;

import dto.DeviceDto;
import dto.RuleDto;
import dto.UserDto;
import tables.DevicesRepository;
import tables.UsersRepository;

import java.util.List;
import java.util.Objects;

public class DeviceService {

  private final UsersRepository usersRepository = new UsersRepository();
  private final DevicesRepository devicesRepository = new DevicesRepository();

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
      return info.toString();
    }
    return "У вас нет устройств.";
  }

  public String addDevice(UserDto userDto, DeviceDto deviceDto) {
    deviceDto.setUserId(getUserIdByLogin(userDto));
    devicesRepository.create(deviceDto);
    return "Устройство успешно добавлено.\n" + "Список ваших устройств:\n" + listOfDevicesOfUser(userDto);
  }

  public String deleteDevice(UserDto userDto, DeviceDto deviceDto) {
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
