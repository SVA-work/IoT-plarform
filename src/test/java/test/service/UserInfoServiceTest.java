package test.service;

import org.junit.jupiter.api.Test;
import test.DTO.Message;
import test.config.ServerConfig;
import test.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class UserInfoServiceTest {

  @Test
  public void testUserDataForNullMessage() {
    Message message = null;
    UserInfoService userInfoService = new UserInfoService();

    String result = userInfoService.userData(message);
    assertEquals(result,
            "Вы не вошли в систему.\n" +
                    "Это можно сделать по адресу: \n" +
                    ServerConfig.LINK_ENTRY);
  }

  @Test
  public void testUserData() {
    Message message = new Message();
    UserInfoService userInfoService = new UserInfoService();

    message.setLogin("123");
    message.setPassword("123");
    String result = userInfoService.userData(message);

    assertEquals(result, "{login: 123, password: 123}");
  }

  @Test
  public void testSuccessfulRegistration() {
    Message message = new Message();
    UserInfoService userInfoService = new UserInfoService();

    message.setLogin("123");
    message.setPassword("123");
    String result = userInfoService.successfulRegistration(message);

    assertEquals(result,
            "Вы успешно зарегистрировались.\n" +
                    "Ваш логин: 123\n" +
                    "Ваш пароль: 123");
  }

  @Test
  public void testListOfDevices() {
    UserInfoService userInfoService = new UserInfoService();

    String result = userInfoService.listOfDevices();

    assertEquals(result, "У вас нет устройств.");
  }

  @Test
  public void testVerified() {
    User user = new User();
    UserInfoService userInfoService = new UserInfoService();

    user.setVerified(true);
    Boolean result = userInfoService.verified(user);

    assertTrue(result);
  }

  @Test
  public void testAddingDevice() {
    Message message = new Message();
    UserInfoService userInfoService = new UserInfoService();

    message.setDeviceId("1");
    String result = userInfoService.addDevice(message);

    assertEquals(result,
            "Устройство успешно добавлено.\n" +
            "Список ваших устройств:\n");
  }

  @Test
  public void testRemovingDevice() {
    Message message = new Message();
    UserInfoService userInfoService = new UserInfoService();

    message.setDeviceId("1");
    String result = userInfoService.deleteDevice(message);

    assertEquals(result, "У вас нет такого устройства.");
  }
}