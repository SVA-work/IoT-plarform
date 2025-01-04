package test.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import dto.UserDto;
import config.ServerConfig;
import controller.ServerLauncher;
import service.DeviceService;
import service.RuleService;
import service.UserService;
import tables.DevicesRepository;
import tables.UsersRepository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class HelloHttpHandlerTest {

  private final UsersRepository usersController = new UsersRepository();
  private final DevicesRepository devicesController = new DevicesRepository();

  @BeforeAll
  static void server() {
    Thread thread = new Thread(() -> {
      ServerLauncher serverLauncher = new ServerLauncher();
      try {
        serverLauncher.main(new String[0]);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    thread.start();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void deviceInformation() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_GET_DEVICE_INFORMATION + "?login=123");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    DeviceService device = new DeviceService();

    UserDto message = new UserDto();
    message.setLogin("123");
    String result = device.listOfDevicesOfUser("123");
    assertEquals(response.toString(), result);
  }

  @Test
  void registration() {

    UserService userService = new UserService();

    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_REGISTRATION);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      String params = "{\"login\": \"123\", \"password\": \"123\"}";

      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.writeBytes(params);
      }

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    DeviceService device = new DeviceService();

    UserDto deleteMessage1 = new UserDto();
    deleteMessage1.setLogin("123");
    deleteMessage1.setUserId(device.getUserIdByLogin("123"));
    usersController.delete(deleteMessage1);

    UserDto message = new UserDto();
    message.setLogin("123");
    message.setPassword("123");
    String result = userService.registration(message);

    UserDto deleteMessage2 = new UserDto();
    deleteMessage2.setLogin("123");
    usersController.delete(deleteMessage2);

    assertEquals(response.toString(), result);
  }

  @Test
  void userVerification() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_ENTRY);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      String params = "{\"login\": \"123\", \"password\": \"123\"}";
      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.writeBytes(params);
      }

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    UserDto message = new UserDto();
    UserService user = new UserService();
    message.setLogin("123");
    message.setPassword("123");
    String result = user.userVerification(message);

    assertEquals(response.toString(), result);
  }

  @Test
  void addDevices() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_ADD_DEVICE);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      String params = "{\"login\": \"123\", \"token\": \"123\"}";
      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.writeBytes(params);
      }

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    DeviceService device = new DeviceService();

    UserDto deleteMessage1 = new UserDto();
    deleteMessage1.setLogin("123");
    deleteMessage1.setToken("123");
    devicesController.delete(deleteMessage1);

    UserDto message = new UserDto();
    message.setLogin("123");
    message.setDeviceId("123");
    String result = device.addDevice(message);

    UserDto deleteMessage2 = new UserDto();
    deleteMessage2.setLogin("123");
    deleteMessage2.setToken("123");
    devicesController.delete(deleteMessage2);

    assertEquals(response.toString(), result);
  }

  @Test
  void deleteDevices() {

    DeviceService device = new DeviceService();

    UserDto addMessage1 = new UserDto();
    addMessage1.setLogin("123");
    addMessage1.setToken("123");
    //devicesController.create(addMessage1);
    device.addDevice(addMessage1);



    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_DELETE_DEVICE);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      String params = "{\"login\": \"123\", \"token\": \"123\"}";
      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.writeBytes(params);
      }

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    UserDto addMessage2 = new UserDto();
    addMessage2.setLogin("123");
    addMessage2.setToken("123");
    //devicesController.create(addMessage2);
    device.addDevice(addMessage2);
  
    UserDto message = new UserDto();
    message.setLogin("123");
    message.setToken("123");
    String result = device.deleteDevice(message);

    assertEquals(response.toString(), result);
  }

  @Test
  void AllAvailableRules() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_ALL_AVAILABLE_RULES);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    RuleService rule = new RuleService();
    String result = rule.getAllAvailableRules();
    assertEquals(response.toString(), result);
  }

  @Test
  void deviceRules() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_DEVICE_RULES + "?login=123&token=123");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    RuleService rule = new RuleService();
    String result = rule.getDeviceRules("123", "123");
    assertEquals(response.toString(), result);
  }

  @Test
  void applyRule() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_APPLY_RULE);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      String params = "{\"login\": \"123\", \"deviceId\": \"123\", \"rule\": \"1\"}";
      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.writeBytes(params);
      }

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    RuleService rule = new RuleService();

    UserDto deleteMessage1 = new UserDto();
    deleteMessage1.setLogin("123");
    deleteMessage1.setToken("123");
    deleteMessage1.setRule("123");
    rule.deleteDeviceRule(deleteMessage1);

    UserDto message = new UserDto();
    message.setLogin("123");
    message.setToken("123");
    deleteMessage1.setRule("123");
    message.setLowTemperature("15");
    message.setHightTemperature("20");
    String result = rule.applyRule(message);

    UserDto deleteMessage2 = new UserDto();
    deleteMessage2.setLogin("123");
    deleteMessage2.setToken("123");
    deleteMessage2.setRule("123");
    rule.deleteDeviceRule(deleteMessage2);

    assertEquals(response.toString(), result);
  }

  @Test
  void deleteDeviceRule() {

    RuleService rule = new RuleService();

    UserDto addMessage1 = new UserDto();
    addMessage1.setLogin("123");
    addMessage1.setToken("123");
    addMessage1.setRule("123");
    rule.applyRule(addMessage1);

    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_DELETE_DEVICE_RULE);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      String params = "{\"login\": \"123\", \"token\": \"123\", \"rule\": \"123\"}";
      try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
        wr.writeBytes(params);
      }

      try (Scanner scanner =
                   new Scanner(
                           new BufferedInputStream(
                                   connection.getInputStream()
                           )
                   )
      ) {
        while (scanner.hasNextLine()) {
          if (!response.toString().equals("")) response.append("\n");
          response.append(scanner.nextLine());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    UserDto addMessage2 = new UserDto();
    addMessage2.setLogin("123");
    addMessage2.setToken("123");
    addMessage2.setRule("123");
    rule.applyRule(addMessage2);

    UserDto message = new UserDto();
    message.setLogin("123");
    message.setToken("123");
    message.setRule("123");
    String result = rule.deleteDeviceRule(message);

    assertEquals(response.toString(), result);
  }
}