package service;

import config.ServerConfig;

import dto.UserDto;
import tables.UsersRepository;

public class UserService {

  private final UsersRepository usersRepository = new UsersRepository();

  public UserService() {
  }

  public String registration(UserDto message) {
    if (existenceUser(message)) {
      return "Пользователь с таким логином уже существует.";
    }
    usersRepository.create(message);
    return "Вы успешно зарегистрировались.\n" +
            "Ваш логин: " + message.getLogin() + "\n" +
            "Ваш пароль: " + message.getPassword();
  }

  public boolean existenceUser(UserDto message) {
    for (UserDto currentMessage : usersRepository.getAll()) {
      if (message.getLogin().equals(currentMessage.getLogin())) {
        return true;
      }
    }
    return false;
  }

  public String userVerification(UserDto message) {
    for (UserDto currentMessage : usersRepository.getAll()) {
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
}
