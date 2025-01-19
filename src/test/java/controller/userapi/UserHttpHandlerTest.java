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

class UserHttpHandlerTest extends BaseHttpHandlerTest {

    @BeforeEach
    void beforeEach() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM telegram_tokens");
            statement.executeUpdate("DELETE FROM rules");
            statement.executeUpdate("DELETE FROM devices");
            statement.executeUpdate("DELETE FROM users");
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
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
                                .uri(URI.create(ServerConfig.LINK_REGISTRATION))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(200, createUserResponse.statusCode());

        String responseText = createUserResponse.body();
        assertEquals("""
                Вы успешно зарегистрировались.
                Ваш логин: testUser
                Ваш пароль: 123""", responseText);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("Select * FROM users");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                assertEquals("testUser", resultSet.getString("login"));
                assertEquals("123", resultSet.getString("password"));
            }
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
        }
    }

    @Test
    void entery() throws IOException, InterruptedException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into users (user_id, login, password) VALUES ('0', 'testUser', '123')");
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
        }
        HttpResponse<String> enteryResponse = HttpClient.newHttpClient()
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
                                .uri(URI.create(ServerConfig.LINK_ENTRY))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(200, enteryResponse.statusCode());

        assertEquals("""
                Вы успешно вошли в систему.
                Список доступных команд:
                1) Добавить устройство.
                Для доступа к команде нужно отправить POST запрос на сервер по адресу:\s
                http://localhost:8091/post/addDevice
                2) Удалить устройство.
                Для доступа к команде нужно отправить POST запрос на сервер по адресу:\s
                http://localhost:8091/post/deleteDevice
                3) Посмотреть список устройств.
                http://localhost:8091/get/listOfDevicesOfUser
                4) Посмотреть список доступных правил для устройств.Для доступа к команде нужно отправить POST запрос на сервер по адресу:\s
                http://localhost:8091/get/deviceRules""", enteryResponse.body());
    }
}