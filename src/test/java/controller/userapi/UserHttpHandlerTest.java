package controller.userapi;


import application.ServerLauncher;
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
import java.sql.SQLException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ServerLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserHttpHandlerTest {

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
    } catch (Exception e) {
        try {
            log.error("connection failed" + jdbcTemplate.getDataSource().getConnection().getMetaData().getURL(), e);
        } catch (SQLException ignored) {}
    }
  }

  @Test
  void registration() throws IOException, InterruptedException {
    HttpResponse<String> createUserResponse = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            {
                                "login": "testUser",
                                "password": "123",
                                "telegramToken": "123"
                            }
                            """
                    )
                )
                .uri(URI.create("http://localhost:" + port + "/user/registration"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    System.out.println(createUserResponse.body());
    assertEquals(200, createUserResponse.statusCode());

    String responseText = createUserResponse.body();

    assertEquals("{\"login\":\"testUser\",\"password\":\"123\",\"telegramToken\":\"123\",\"devices\":[]}", responseText);

    var users = jdbcTemplate.queryForList("SELECT * FROM users");
    assertEquals(1, users.size());
    assertEquals("testUser", users.get(0).get("login"));
    assertEquals("123", users.get(0).get("password"));
  }

  @Test
  void entry() throws IOException, InterruptedException {
    jdbcTemplate.update("INSERT INTO users (id, login, password) VALUES ('1', 'testUser', '123')");

    HttpResponse<String> entryResponse = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            {
                                "login": "testUser",
                                "password": "123"
                            }
                            """
                    )
                )
                .uri(URI.create("http://localhost:" + port + "/user/entry"))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, entryResponse.statusCode());

    assertEquals("{\"login\":\"testUser\",\"password\":\"123\",\"telegramToken\":null,\"devices\":[]}", entryResponse.body());
  }

  @AfterAll
  static void stopContainer() {
    POSTGRES.stop();
  }
}