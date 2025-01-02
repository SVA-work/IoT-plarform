package service;

import dto.Message;
import tables.DevicesRepository;
import tables.UsersRepository;

import java.util.List;
import java.util.Objects;

public class DeviceService {

  private final UsersRepository usersRepository = new UsersRepository();
  private final DevicesRepository devicesRepository = new DevicesRepository();

  public String listOfDevicesOfUser(String userLogin) {
    Message message = new Message();
    message.setLogin(userLogin);

    message.setUserId(getUserIdByLogin(userLogin));

    List<Message> allDevicesOfUser = usersRepository.devicesOfUser(message);

    StringBuilder info = new StringBuilder();
    boolean hasAnyDevice = false;
    for (Message deviceMessage : allDevicesOfUser) {
      info.append(deviceMessage.getToken()).append('\n');
      for (Message ruleMessage : devicesRepository.rulesOfDevice(deviceMessage)) {
        info.append("  ").append(ruleMessage.getToken()).append("\n");
      }
      hasAnyDevice = true;
    }
    if (hasAnyDevice) {
      return info.toString();
    }
    return "У вас нет устройств.";
  }

  public String addDevice(Message message) {
    message.setUserId(getUserIdByLogin(message.getLogin()));
    devicesRepository.create(message);
    return "Устройство успешно добавлено.\n" + "Список ваших устройств:\n" + listOfDevicesOfUser(message.getLogin());
  }

  public String deleteDevice(Message message) {
    message.setUserId(getUserIdByLogin(message.getLogin()));
    List<Message> allDevicesOfUser = usersRepository.devicesOfUser(message);

    boolean hasDeletedAnyDevice = false;
    for (Message deviceMessage : allDevicesOfUser) {
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
  }

  public String getUserIdByLogin(String userLogin) {
    for (Message currentMessage : usersRepository.getAll()) {
      if (userLogin.equals(currentMessage.getLogin())) {
        return currentMessage.getUserId();
      }
    }
    return null;
  }
}
