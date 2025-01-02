package service;

import config.ServerConfig;

import dto.UserDto;
import tables.UsersRepository;

public class UserService {

  private final UsersRepository usersRepository = new UsersRepository();

  public String registration(UserDto userDto) {
    if (existenceUser(userDto)) {
      return "Пользователь с таким логином уже существует.";
    }
    usersRepository.create(userDto);
    return "Вы успешно зарегистрировались.\n" +
            "Ваш логин: " + userDto.getLogin() + "\n" +
            "Ваш пароль: " + userDto.getPassword();
  }

  public boolean existenceUser(UserDto userDto) {
    for (UserDto currentUser : usersRepository.getAll()) {
      if (userDto.getLogin().equals(currentUser.getLogin())) {
        return true;
      }
    }
    return false;
  }

  public String userVerification(UserDto userDto) {
    for (UserDto currentUser : usersRepository.getAll()) {
      if (userDto.getLogin().equals(currentUser.getLogin()) &&
              userDto.getPassword().equals(currentUser.getPassword())) {
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
