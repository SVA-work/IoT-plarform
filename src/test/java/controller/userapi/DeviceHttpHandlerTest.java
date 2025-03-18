package controller.userapi;


import application.ServerLauncher;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ServerLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class DeviceHttpHandlerTest extends BaseHttpHandlerTest {

  @LocalServerPort
  private int port;

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  static {
    POSTGRES.start();
  }

  @DynamicPropertySource
  static void configureDataSource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void beforeEach() {
    try {
      jdbcTemplate.update("ALTER TABLE users DROP CONSTRAINT fk_users_telegramtoken_id");
      jdbcTemplate.update("DELETE FROM telegram_tokens");
      jdbcTemplate.update("ALTER TABLE users ADD CONSTRAINT fk_users_telegramtoken_id FOREIGN KEY (id) REFERENCES users(id)");
      jdbcTemplate.update("DELETE FROM rules");
      jdbcTemplate.update("DELETE FROM devices");
      jdbcTemplate.update("DELETE FROM users");
      jdbcTemplate.update("insert into users (id, login, password) VALUES ('1', 'testUser', '123')");
    } catch (Exception e) {
      log.error("Соединение не удалось", e);
    }
  }

  @Test
  void addDevices() throws IOException, InterruptedException {
    HttpResponse<String> addDeviceResponse = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            {
                                "login": "testUser",
                                "deviceName": "testDevice",
                                "type": "temp",
                                "uuid": "55"
                            }
                            """
                    )
                )
                .uri(URI.create("http://localhost:" + port + "/device/add"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, addDeviceResponse.statusCode());

    String responseText = addDeviceResponse.body();
    assertEquals("{\"uuid\":\"55\",\"type\":\"temp\",\"login\":\"testUser\",\"deviceName\":\"testDevice\",\"rules\":[]}", responseText);

    var devices = jdbcTemplate.queryForList("SELECT * FROM devices");
    assertEquals(1, devices.size());
    assertEquals("testDevice", devices.get(0).get("device_name"));
  }

  @Test
  void deviceRulesEmpty() throws IOException, InterruptedException {
    String sqlCreateDevice = "INSERT INTO devices (id, user_id, device_name, type, uuid) VALUES ('1', '1', 'testDevice', 'temp', '55')";
    jdbcTemplate.update(sqlCreateDevice);
    HttpResponse<String> getDeviceResponse = HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/device/testUser/rules/testDevice"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, getDeviceResponse.statusCode());

    String responseText = getDeviceResponse.body();
    assertEquals("[]", responseText);
  }

  @Test
  void deleteDevices() throws IOException, InterruptedException {
    String sqlCreateDevice = "INSERT INTO devices (id, user_id, device_name, type, uuid) VALUES ('1', '1', 'testDevice', 'temp', '55')";
    jdbcTemplate.update(sqlCreateDevice);

    HttpResponse<String> deleteDeviceResponse = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .method("DELETE",
                    HttpRequest.BodyPublishers.ofString(
                        """
                            {
                                "login": "testUser",
                                "deviceName": "testDevice"
                            }
                            """
                    )
                )
                .uri(URI.create("http://localhost:" + port + "/device/delete"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, deleteDeviceResponse.statusCode());

    String responseText = deleteDeviceResponse.body();
    assertEquals("{\"uuid\":\"55\",\"type\":\"temp\",\"login\":\"testUser\",\"deviceName\":\"testDevice\",\"rules\":[]}", responseText);

    var devices = jdbcTemplate.queryForList("SELECT * FROM devices");
    assertEquals(0, devices.size());
  }

  @AfterAll
  static void stopContainer() {
    POSTGRES.stop();
  }
}