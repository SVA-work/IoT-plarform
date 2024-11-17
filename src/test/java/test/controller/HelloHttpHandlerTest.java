package test.controller;

import com.fasterxml.jackson.core.io.UTF8Writer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.DTO.Message;
import test.config.ServerConfig;
import test.service.UserInfoService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static test.config.ServerConfig.PORT;

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
  void userData() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL("http://localhost:" + PORT + "/test/get/userData");
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

    Message message = null;
    UserInfoService userInfoService = new UserInfoService();

    String result = userInfoService.userData(message);
    assertEquals(result, response.toString());
  }

  @Test
  void deviceInformation() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL("http://localhost:" + PORT + "/test/get/listOfDevices");
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

    String result = userInfoService.listOfDevices();
    assertEquals(response.toString(), result);
  }

  @Test
  void registration() {
    StringBuilder response = new StringBuilder();
    try {
      URL url = new URL("http://localhost:" + PORT + "/test/post/registration");
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
  void entery() {
  }

  @Test
  void addDevices() {
  }

  @Test
  void deleteDevices() {
  }
}