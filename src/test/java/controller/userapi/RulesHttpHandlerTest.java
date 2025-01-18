package controller.userapi;

import config.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RulesHttpHandlerTest extends BaseHttpHandlerTest {

  @BeforeEach
  void beforeEach() {
    try {
      Statement statement = connection.createStatement();
      statement.executeUpdate("DELETE FROM rules");
      statement.executeUpdate("DELETE FROM devices");
      statement.executeUpdate("insert into devices (device_id, user_id, token) VALUES ('0', '0', 'testDevice')");
      statement.executeUpdate("DELETE FROM users");
      statement.executeUpdate("insert into users (user_id, login, password) VALUES ('0', 'testUser', '123')");
    } catch (SQLException e) {
      LOG.error("Соединение не удалось", e);
    }
  }

  @Test
  void deviceRulesEmpty() throws IOException, InterruptedException {
    HttpResponse<String> getRuleResponse = HttpClient.newHttpClient()
            .send(HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(ServerConfig.LINK_DEVICE_RULES + "?login=testUser&token=testDevice"))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(UTF_8)
            );
    assertEquals(200, getRuleResponse.statusCode());

    String responseText = getRuleResponse.body();
    assertEquals("У данного устройства нет правил.", responseText);
  }

  @Test
  void deviceRules() throws IOException, InterruptedException {
    String sqlCreateUserInfo = "insert into rules (rule_id, device_id, rule) VALUES ('0', '0', 'testRule')";
    try {
      Statement statement = connection.createStatement();
      statement.executeUpdate(sqlCreateUserInfo);
    } catch (SQLException e) {
      LOG.error("Соединение не удалось", e);
    }
    HttpResponse<String> getDeviceResponse = HttpClient.newHttpClient()
            .send(HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(ServerConfig.LINK_DEVICE_RULES + "?login=testUser&token=testDevice"))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(UTF_8)
            );
    assertEquals(200, getDeviceResponse.statusCode());

    String responseText = getDeviceResponse.body();
    assertEquals("testRule", responseText);
  }

  @Test
  void applyRule() throws IOException, InterruptedException {
    HttpResponse<String> addRuleResponse = HttpClient.newHttpClient()
            .send(
                    HttpRequest.newBuilder()
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            """
                                                        {
                                                          "login": "testUser",
                                                          "token": "testDevice",
                                                          "rule": "testRule/1/2"
                                                        }
                                                    """
                                    )
                            )
                            .uri(URI.create(ServerConfig.LINK_APPLY_RULE))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(UTF_8)
            );
    assertEquals(200, addRuleResponse.statusCode());

    String responseText = addRuleResponse.body();
    assertEquals("Правила успешно добавлены.", responseText);

    String sqlGetUserInfo = "Select * FROM rules";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlGetUserInfo);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        assertEquals("testRule/1/2", resultSet.getString("rule"));
      }
    } catch (SQLException e) {
      LOG.error("Соединение не удалось", e);
    }
  }

  @Test
  void deleteDeviceRule() throws IOException, InterruptedException {
    String sqlCreateUserInfo = "insert into rules (rule_id, device_id, rule) VALUES ('0', '0', 'testRule')";
    try {
      Statement statement = connection.createStatement();
      statement.executeUpdate(sqlCreateUserInfo);
    } catch (SQLException e) {
      LOG.error("Соединение не удалось", e);
    }
    HttpResponse<String> deleteDeviceResponse = HttpClient.newHttpClient()
            .send(
                    HttpRequest.newBuilder()
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            """
                                                        {
                                                          "login": "testUser",
                                                          "token": "testDevice",
                                                          "rule": "testRule"
                                                        }
                                                    """
                                    )
                            )
                            .uri(URI.create(ServerConfig.LINK_DELETE_DEVICE_RULE))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(UTF_8)
            );
    assertEquals(200, deleteDeviceResponse.statusCode());

    String responseText = deleteDeviceResponse.body();
    assertEquals("Правило успешно удалено.", responseText);

    String sqlGetUserInfo = "Select * FROM rules";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlGetUserInfo);
      ResultSet resultSet = preparedStatement.executeQuery();
      assertFalse(resultSet.next());
    } catch (SQLException e) {
      LOG.error("Соединение не удалось", e);
    }
  }
}