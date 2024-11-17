package test.service;

import test.config.ServerConfig;
import test.DTO.Message;

public class UserInfoService {

  public UserInfoService() {
  }

  public String successfulRegistration(Message message) {
    // если такого пользователя нет
    // Добавляем логин и пароль в БД
    return "Вы успешно зарегистрировались.\n" +
            "Ваш логин: " + message.getLogin() + "\n" +
            "Ваш пароль: " + message.getPassword();
    //  Если пользователь с таким логином уже есть
    //  return "Такой пользователь уже существует.";
  }

  public String userVerification(Message message) {
    String login = message.getLogin();
    String password = message.getPassword();
    //  Если такой логин и пароль есть в БД
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
    //  Если такого пользователя нет в БД
    //  return "Неверный логин или пароль.\n";
  }

  public String listOfDevices(String login) {
    //  Проверяем, что пользователь существует
    //  Если у пользователя нет устройств в БД
      return "У вас нет устройств.";
  //  StringBuilder info = new StringBuilder();
  //  return (deviceOutput(login, info).toString());
  }

  public String addDevice(Message message) {
    String login = message.getLogin();
    String deviceId = message.getDeviceId();
    //  Проверяем, что пользователь с таким логином существует
    //  Добавляем устройсво в БД
    StringBuilder info = new StringBuilder("Устройство успешно добавлено.\n");
    //info.append("Список ваших устройств:\n");
    info.append("Список ваших устройств:");
    return (deviceOutput(login, info).toString());
  }

  public String deleteDevice(Message message) {
    String login = message.getLogin();
    String deviceId = message.getDeviceId();
    //  если такого устройства нет у данного пользователя
    return "У вас нет такого устройства.";
    //  удаляем устройство из БД
    //  StringBuilder info = new StringBuilder("Устройство успешно удалено.\n");
    //  если после удаления не осталось устройств
//    return "У вас больше нет устройств.";
//    если устройства ещё есть
//    info.append("Список ваших устройств:\n");
//    return (deviceOutput(login, info).toString());
  }

  //  Нужен вывод списка устройств пользователя через БД
  public StringBuilder deviceOutput(String login, StringBuilder info) {
//    info.append();
    return info;
  }
}
