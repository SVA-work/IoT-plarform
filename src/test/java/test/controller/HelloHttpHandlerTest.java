package test.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.DTO.Message;
import test.config.ServerConfig;
import test.service.UserInfoService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class HelloHttpHandlerTest {

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
    String result = userInfoService.listOfDevices("123");
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

    Message message = new Message();
    UserInfoService userInfoService = new UserInfoService();
    message.setLogin("123");
    message.setPassword("123");
    String result = userInfoService.successfulRegistration(message);
  
    assertEquals(response.toString(), result);
  }

  @Test
  void entry() {
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
    Message message = new Message();
    UserInfoService user = new UserInfoService();
    message.setLogin("123");
    message.setDeviceId("123");
    String result = user.addDevice(message);
  
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
}