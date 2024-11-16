package test.service;

import test.config.ServerConfig;

import test.entity.User;
import test.DTO.Message;

public class UserInfoService {

  public UserInfoService() {
  }

  public String userData(Message message) {
    System.out.println(1);
    if (message == null) return actionsWithoutLogin();
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

  public String listOfDevices() {
    //  Если у пользователя нет устройств в БД
    return "У вас нет устройств.";
//    StringBuilder info = new StringBuilder();
//    return (deviceOutput(info).toString());
  }

  public Boolean verified(User currentUser) {
    return currentUser.getVerified();
  }

  //  Нужно добавить устройство в БД
  public String addDevice(Message message) {
    //  добавление устройства
    StringBuilder info = new StringBuilder("Устройство успешно добавлено.\n");
    info.append("Список ваших устройств:\n");
    return (deviceOutput(info).toString());
  }

  //  Нужно удалить устройство из БД
  public String deleteDevice(Message message) {
    //  если устройств нет
    return "У вас нет такого устройства.";
    //  если после удаления не осталось устройств
//    return "У вас больше нет устройств.";
//    StringBuilder info = new StringBuilder("Устройство успешно удалено.\n");
//    info.append("Список ваших устройств:\n");
//    return (deviceOutput(info).toString());
  }

  //  Нужен вывод списка устройств пользователя через БД
  public StringBuilder deviceOutput(StringBuilder info) {
//    info.append();
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
