package test.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.DTO.Message;
import test.config.ServerConfig;
import test.service.UserInfoService;
import test.tables.DevicesRepository;
import test.tables.RulesRepository;
import test.tables.UsersRepository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class HelloHttpHandlerTest {

  private final UsersRepository usersController = new UsersRepository();
  private final DevicesRepository devicesController = new DevicesRepository();
  private final RulesRepository rulesController = new RulesRepository();

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
    UserInfoService userInfoService = new UserInfoService();
    Message message = new Message();
    message.setLogin("123");
    String result = userInfoService.listOfDevicesOfUser("123");
    assertEquals(response.toString(), result);
  }

  @Test
  void registration() {
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

    Message deleteMessage1 = new Message();
    deleteMessage1.setLogin("123");
    usersController.delete(deleteMessage1);

    Message message = new Message();
    UserInfoService userInfoService = new UserInfoService();
    message.setLogin("123");
    message.setPassword("123");
    String result = userInfoService.registration(message);

    Message deleteMessage2 = new Message();
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

    Message message = new Message();
    UserInfoService user = new UserInfoService();
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
      String params = "{\"login\": \"123\", \"deviceId\": \"123\"}";
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

    Message deleteMessage1 = new Message();
    deleteMessage1.setLogin("123");
    deleteMessage1.setDeviceId("123");
    devicesController.delete(deleteMessage1);

    Message message = new Message();
    UserInfoService user = new UserInfoService();
    message.setLogin("123");
    message.setDeviceId("123");
    String result = user.addDevice(message);

    Message deleteMessage2 = new Message();
    deleteMessage2.setLogin("123");
    deleteMessage2.setDeviceId("123");
    devicesController.delete(deleteMessage2);

    assertEquals(response.toString(), result);
  }

  @Test
  void deleteDevices() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_DELETE_DEVICE);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      String params = "{\"login\": \"123\", \"deviceId\": \"123\"}";
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

    Message message = new Message();
    UserInfoService user = new UserInfoService();
    message.setLogin("123");
    message.setDeviceId("123");
    String result = user.deleteDevice(message);

    assertEquals(response.toString(), result);
  }

  @Test
  void deviceRules() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL(ServerConfig.LINK_DEVICE_RULES);
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
    UserInfoService userInfoService = new UserInfoService();
    String result = userInfoService.getDeviceRules();
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
      String params = "{\"login\": \"123\", \"deviceId\": \"123\", \"lowTemperature\": \"15\", \"hightTemperature\": \"20\"}";
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

    Message deleteMessage1 = new Message();
    deleteMessage1.setLogin("123");
    deleteMessage1.setDeviceId("123");
    deleteMessage1.setLowTemperature("15");
    deleteMessage1.setHightTemperature("20");
    rulesController.delete(deleteMessage1);

    Message message = new Message();
    UserInfoService user = new UserInfoService();
    message.setLogin("123");
    message.setDeviceId("123");
    message.setLowTemperature("15");
    message.setHightTemperature("20");
    String result = user.deleteDevice(message);

    Message deleteMessage2 = new Message();
    deleteMessage2.setLogin("123");
    deleteMessage2.setDeviceId("123");
    deleteMessage2.setLowTemperature("15");
    deleteMessage2.setHightTemperature("20");
    rulesController.delete(deleteMessage2);

    assertEquals(response.toString(), result);
  }

}