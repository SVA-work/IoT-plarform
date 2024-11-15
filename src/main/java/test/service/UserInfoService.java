package test.service;

import test.config.ServerConfig;

import test.entity.Device;
import test.DTO.Message;
import java.util.Map;

public class UserInfoService {

  public UserInfoService() {
  }

  public String userData(Message message) {
    return "{login: " + message.getLogin() + ", password: " + message.getPassword() + "}";
  }

  public String successfulRegistration(Message message) {
    return "Вы успешно зарегистрировались.\n" +
           "Ваш логин: " + message.getLogin() + "\n" +
           "Ваш пароль: " + message.getPassword();
  }

  public String entry() {
    return "Здравствуйте! Вы уже зарегистрированы.\n" +
           "Пожалуйста, отправьте ваш логин и пароль в следующем запросе, чтобы войти в систему.";
  }

  public String successfulEntry() {
    return "Вы успешно вошли в систему.\n" +
           "Список доступных команд:\n" +
           "1)Добавить устройство.\n" +
           "Для доступа к команде нужно отправить POST запрос на сервер по адресу: \n" +
           ServerConfig.LINK_ADD_DEVICE + "\n" +
           "2)Удалить устройство.\n" +
           "Для доступа к команде нужно отправить POST запрос на сервер по адресу: \n" +
           ServerConfig.LINK_DELETE_DEVICE + "\n" +
           "3)Посмотреть список устройств.\n" +
           ServerConfig.LINK_GET_DEVICE_INFORMATION;
  }

  public String failedEntry() {
    return "Неверный пароль или логин.\n";
  }

  public String actionsWithoutLogin() {
    return "Вы не вошли в систему.\n" +
           "Это можно сделать по адресу: \n" +
           ServerConfig.LINK_ENTRY;
  }

  public String ListOfDevices(Map<String, Device> userDevices) {
    if (userDevices.size() == 0) {
      return "У вас больше нет устройств.";
    }
    StringBuilder info = new StringBuilder();
    return(deviceOutput(userDevices, info).toString());
  }
  //  Нужно добавить устройство в БД
  public String addDevice(Map<String, Device> userDevices, Device newDevice) {
    userDevices.put(newDevice.getId(), newDevice);
    StringBuilder info = new StringBuilder("Устройство успешно добавлено.\n");
    info.append("Список ваших устройств:\n");
    return(deviceOutput(userDevices, info).toString());
  }
  //  Нужно удалить устройство из БД
  public String deleteDevice(Map<String, Device> userDevices, Device deleteDevice) {
    Device check = userDevices.remove(deleteDevice.getId());
    if (check == null) {
      return "У вас нет такого устройства.";
    }
    if (userDevices.size() == 0) {
      return "У вас больше нет устройств.";
    }
    StringBuilder info = new StringBuilder("Устройство успешно удалено.\n");
    info.append("Список ваших устройств:\n");
    return(deviceOutput(userDevices, info).toString());
  }
  //  Нужен вывод списка устройств пользователя через БД
  public StringBuilder deviceOutput(Map<String, Device> userDevices, StringBuilder info) {
    for (Map.Entry<String, Device> entry : userDevices.entrySet()) {
      info.append(entry.getValue().getId()).append("\n");
    }
    return info;
  }
  //  Нужна проверка существования логина и пароля пользователя в БД
  public Boolean userVerification(Message message, Message currentMessage) {
    if (message.getLogin().equals(currentMessage.getLogin()) && message.getPassword().equals(currentMessage.getPassword())) {
      return true;
    } else {
      return false;
    }
  }
}
