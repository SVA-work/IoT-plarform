package service;

import dto.UserDto;
import tables.DevicesRepository;
import tables.UsersRepository;

import java.util.List;
import java.util.Objects;

public class DeviceService {

  private final UsersRepository usersRepository = new UsersRepository();
  private final DevicesRepository devicesRepository = new DevicesRepository();

  public String listOfDevicesOfUser(String userLogin) {
    UserDto message = new UserDto();
    message.setLogin(userLogin);
    if (getUserIdByLogin(userLogin) != null) {
      message.setUserId(getUserIdByLogin(userLogin));

      List<UserDto> allDevicesOfUser = usersRepository.devicesOfUser(message);

      StringBuilder info = new StringBuilder();
      boolean hasAnyDevice = false;
      for (UserDto deviceMessage : allDevicesOfUser) {
        info.append(deviceMessage.getToken()).append('\n');
        for (UserDto ruleMessage : devicesRepository.rulesOfDevice(deviceMessage)) {
          info.append("  ").append(ruleMessage.getToken()).append("\n");
        }
        hasAnyDevice = true;
      }
      if (hasAnyDevice) {
        return info.toString();
      }
      return "У вас нет устройств.";
    } else {
      return "Такого пользователя не существует";
    }
  }

  public String addDevice(UserDto message) {
    if (getUserIdByLogin(message.getLogin()) != null) {
      message.setUserId(getUserIdByLogin(message.getLogin()));
      devicesRepository.create(message);
      return "Устройство успешно добавлено.\n" + "Список ваших устройств:\n" + listOfDevicesOfUser(message.getLogin());
    } else {
      return "Нет такого пользователя";
    }
  }

  public String deleteDevice(UserDto message) {
    if (getUserIdByLogin(message.getLogin()) != null) {
      message.setUserId(getUserIdByLogin(message.getLogin()));
      List<UserDto> allDevicesOfUser = usersRepository.devicesOfUser(message);

      boolean hasDeletedAnyDevice = false;
      for (UserDto deviceMessage : allDevicesOfUser) {
        if (Objects.equals(deviceMessage.getToken(), message.getToken())) {
          devicesRepository.delete(deviceMessage);
          hasDeletedAnyDevice = true;
          break;
        }
      }
      if (!hasDeletedAnyDevice) {
        return "У вас нет такого устройства.";
      }

      String infoAboutDevices = listOfDevicesOfUser(message.getLogin());
      if (Objects.equals(infoAboutDevices, "У вас нет устройств.")) {
        return "У вас больше нет устройств.";
      }
      return ("Список ваших устройств:\n" +
              infoAboutDevices);
    } else {
      return "Нет такого пользователя";
    }
  }

  public String getUserIdByLogin(String userLogin) {
    for (UserDto currentMessage : usersRepository.getAll()) {
      if (userLogin.equals(currentMessage.getLogin())) {
        return currentMessage.getUserId();
      }
    }
    return null;
  }
}
