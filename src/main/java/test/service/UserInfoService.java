package test.service;

import test.config.ServerConfig;
import test.DTO.Message;
import test.tables.DevicesController;
import test.tables.UsersController;

import java.util.List;
import java.util.Objects;

public class UserInfoService {

  private final UsersController usersController = new UsersController();
  private final DevicesController devicesController = new DevicesController();

  public UserInfoService() {
  }
  
  public String userData(Message message) {
    if (message == null) return actionsWithoutLogin();
    if (userVerification(message)) {
      return "{login: " + message.getLogin() + ", password: " + message.getPassword() + "}";
    }
    return "No user";
  }

  public String successfulRegistration(Message message) {
    if (userVerification(message)) {
      return "Вы успешно зарегистрировались.\n" +
              "Ваш логин: " + message.getLogin() + "\n" +
              "Ваш пароль: " + message.getPassword();
    }
    return "Что-то пошло не так, попробуйте еще раз.";
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

  // Позже выводить правила для всех устройств вместе с именем
  public String listOfDevices(Message message) {
    List<Message> allDevices = devicesController.getAll();
    StringBuilder info = new StringBuilder();
    boolean hasAnyDevice = false;
    for (Message deviceMessage : allDevices) {
      if (Objects.equals(deviceMessage.getUserId(), message.getUserId())) {
        info.append(deviceMessage.getToken()).append('\n');
        hasAnyDevice = true;
      }
    }
    if (hasAnyDevice) {
      return info.toString();
    }
    return "У вас нет устройств.";
  }

  public Boolean verified(User currentUser) {
    return currentUser.getVerified();
  }

  public String addDevice(Message message) {
    devicesController.create(message);
    return "Устройство успешно добавлено.\n" + "Список ваших устройств:\n" + listOfDevices(message);

  }

  public String deleteDevice(Message message) {
    List<Message> allDevices = devicesController.getAll();
    boolean hasDeletedAnyDevice = false;
    for (Message deviceMessage : allDevices) {
      if (Objects.equals(deviceMessage.getUserId(), message.getUserId()) &&
              Objects.equals(deviceMessage.getDeviceId(), message.getDeviceId())) {
        devicesController.delete(message);
        hasDeletedAnyDevice = true;
        break;
      }
    }
    if (!hasDeletedAnyDevice) {
      return "У вас нет такого устройства.";
    }
    String infoAboutDevices = listOfDevices(message);
    if (Objects.equals(infoAboutDevices, "")) {
      return "У вас больше нет устройств.";
    }
    StringBuilder info = new StringBuilder("Устройство успешно добавлено.\n");
    info.append("Список ваших устройств:\n");
    info.append(infoAboutDevices);
    return (info.toString());
  }

  //  Нужна проверка существования логина и пароля пользователя в БД
  public Boolean userVerification(Message message) {
    for (Message currentMessage : usersController.getAll()) {
      if (message.getLogin().equals(currentMessage.getLogin()) &&
              message.getPassword().equals(currentMessage.getPassword())) {
        return true;
      }
    }
    return false;
  }
}
