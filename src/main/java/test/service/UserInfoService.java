package test.service;

import test.config.ServerConfig;

import test.DTO.Message;
import test.tables.DevicesRepository;
import test.tables.UsersRepository;
import test.tables.RulesRepository;

import java.util.List;
import java.util.Objects;

public class UserInfoService {

  private final UsersRepository usersRepository = new UsersRepository();
  private final DevicesRepository devicesRepository = new DevicesRepository();
  private final RulesRepository rulesRepository = new RulesRepository();

  public UserInfoService() {
  }

  public String registration(Message message) {
    if (existenceUser(message)) {
      return "Пользователь с таким логином уже существует.";
    }
    usersRepository.create(message);
    return "Вы успешно зарегистрировались.\n" +
            "Ваш логин: " + message.getLogin() + "\n" +
            "Ваш пароль: " + message.getPassword();
  }

  public boolean existenceUser(Message message) {
    for (Message currentMessage : usersRepository.getAll()) {
      if (message.getLogin().equals(currentMessage.getLogin())) {
        return true;
      }
    }
    return false;
  }

  public boolean existenceUserDevice(Message message) {
    List<Message> allDevices = usersRepository.devicesOfUser(message);
    for (Message currentMessage : allDevices) {
      if (message.getLogin().equals(currentMessage.getLogin()) &&
              message.getDeviceId().equals(currentMessage.getDeviceId())) {
        return true;
      }
    }
    return false;
  }

  public String userVerification(Message message) {
    for (Message currentMessage : usersRepository.getAll()) {
      if (message.getLogin().equals(currentMessage.getLogin()) &&
              message.getPassword().equals(currentMessage.getPassword())) {
        return successfulEntry();
      }
    }
    return failedEntry();
  }

  public String successfulEntry() {
    return "Вы успешно вошли в систему.\n" +
            "Список доступных команд:\n" +
            "1) Добавить устройство.\n" +
            "Для доступа к команде нужно отправить POST запрос на сервер по адресу: \n" +
            ServerConfig.LINK_ADD_DEVICE + "\n" +
            "2) Удалить устройство.\n" +
            "Для доступа к команде нужно отправить POST запрос на сервер по адресу: \n" +
            ServerConfig.LINK_DELETE_DEVICE + "\n" +
            "3) Посмотреть список устройств.\n" +
            ServerConfig.LINK_GET_DEVICE_INFORMATION + "\n" +
            "4) Посмотреть список доступных правил для устройств." +
            "Для доступа к команде нужно отправить POST запрос на сервер по адресу: \n" +
            ServerConfig.LINK_DEVICE_RULES;
  }

  public String failedEntry() {
    return "Неверный пароль или логин.\n";
  }

  // Позже выводить правила для всех устройств вместе с именем
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

  public String getDeviceRules() {
    StringBuilder info = new StringBuilder();
    info.append("1) Датчик температуры. \n" +
            "Отправьте запрос на адрес: " + ServerConfig.LINK_DEVICE_RULES + "\n" +
            "Укажите id устройства и приемлемую для вас температуры в таком формате: {login: 99, deviceId: 123, lowTemperature: 15, hightTemperature: 20}");
    return info.toString();
  }

  //public String setDeviceRules() {
  //}

  public String applyRule(Message message) {
    if (existenceUser(message)) {
      if (existenceUserDevice(message)) {

        message.setDeviceId(getDeviceIdByToken(message.getLogin(), message.getToken()));

        rulesRepository.create(message);

        return "Правила успешно добавлены.";
      } else {
        return "У вас нет такого устройства.";
      }
    } else {
      return "Пользователя с таким логином не существует.";
    }
  }

  private String getUserIdByLogin(String userLogin) {
    for (Message currentMessage : usersRepository.getAll()) {
      if (userLogin.equals(currentMessage.getLogin())) {
        return currentMessage.getUserId();
      }
    }
    return null;
  }

  private String getDeviceIdByToken(String userLogin, String deviceToken) {
    for (Message userMessage : usersRepository.getAll()) {
      if (userLogin.equals(userMessage.getLogin())) {
        for (Message deviceMessage : usersRepository.devicesOfUser(userMessage)) {
          if (deviceToken.equals(deviceMessage.getToken())) {
            return deviceMessage.getDeviceId();
          }
        }
      }
    }
    return null;
  }

}
