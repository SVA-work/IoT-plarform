package test.service;

import org.junit.jupiter.api.Test;
import test.DTO.Message;
import test.config.ServerConfig;

import static org.junit.jupiter.api.Assertions.*;

class UserInfoServiceTest {

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
    Message message = new Message();
    message.setLogin("123");
    String result = userInfoService.listOfDevices("123");
    assertEquals(result, "У вас нет устройств.");
  }

  @Test
  public void testAddingDevice() {
    Message message = new Message();
    UserInfoService userInfoService = new UserInfoService();

    message.setDeviceId("1");
    String result = userInfoService.addDevice(message);

    assertEquals(result,
            "Устройство успешно добавлено.\n" +
            //"Список ваших устройств:\n");
            "Список ваших устройств:");
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