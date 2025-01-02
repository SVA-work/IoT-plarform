package service;

import config.ServerConfig;

import dto.Message;
import tables.DevicesRepository;
import tables.UsersRepository;
import tables.RulesRepository;

import java.util.List;
import java.util.Objects;

public class RuleService {

  private final UsersRepository usersRepository = new UsersRepository();
  private final DevicesRepository devicesRepository = new DevicesRepository();
  private final RulesRepository rulesRepository = new RulesRepository();

  public String getAllAvailableRules() {
    StringBuilder info = new StringBuilder();
    info.append("1) Датчик температуры. \n" +
            "Отправьте запрос на адрес: " + ServerConfig.LINK_ALL_AVAILABLE_RULES + "\n" +
            "Укажите название устройства и приемлемую для вас температуры в таком формате: {login: 99, token: 123, lowTemperature: 15, hightTemperature: 20}");
    return info.toString();
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

  public String getDeviceRules(String login, String token) {
    Message message = new Message();
    message.setDeviceId(getDeviceIdByToken(login, token));

    List<Message> allRulesOfDevice = devicesRepository.rulesOfDevice(message);

    StringBuilder info = new StringBuilder();
    boolean hasAnyRule = false;

    for (Message ruleMessage : allRulesOfDevice) {
      info.append(ruleMessage.getToken());
      hasAnyRule = true;
    }
    if (hasAnyRule) {
      return info.toString();
    }
    return "У данного устройства нет правил.";

  }

  public String deleteDeviceRule(Message message) {
    message.setDeviceId(getDeviceIdByToken(message.getLogin(), message.getToken()));
    List<Message> allRulesOfDevice = devicesRepository.rulesOfDevice(message);

    boolean hasDeletedAnyRule = false;
    for (Message ruleMessage : allRulesOfDevice) {
      if (Objects.equals(ruleMessage.getToken(), message.getRule())) {
        devicesRepository.delete(ruleMessage);
        hasDeletedAnyRule = true;
        break;
      }
    }
    if (!hasDeletedAnyRule) {
      return "У данного устройства нет правила с таким названием.";
    }
    return "Правило успешно удалено.";
  }

  public String getDeviceIdByToken(String userLogin, String deviceToken) {
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
