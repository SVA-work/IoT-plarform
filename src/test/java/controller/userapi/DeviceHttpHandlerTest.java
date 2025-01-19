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

class DeviceHttpHandlerTest extends BaseHttpHandlerTest {

    @BeforeEach
    void beforeEach() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM rules");
            statement.executeUpdate("DELETE FROM devices");
            statement.executeUpdate("DELETE FROM users");
            statement.executeUpdate("insert into users (user_id, login, password) VALUES ('0', 'testUser', '123')");
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
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
                                                              "token": "testDevice",
                                                              "type": "temp"
                                                            }
                                                        """
                                        )
                                )
                                .uri(URI.create(ServerConfig.LINK_ADD_DEVICE))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(200, addDeviceResponse.statusCode());

        String responseText = addDeviceResponse.body();
        assertEquals("""
                Устройство успешно добавлено.
                Список ваших устройств:
                testDevice""", responseText);

        String sqlGetUserInfo = "Select * FROM devices";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlGetUserInfo);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                assertEquals("testDevice", resultSet.getString("token"));
            }
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
        }
    }

    @Test
    void deviceInformationEmpty() throws IOException, InterruptedException {
        HttpResponse<String> getDeviceResponse = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(ServerConfig.LINK_GET_DEVICE_INFORMATION + "?login=testUser"))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(200, getDeviceResponse.statusCode());

        String responseText = getDeviceResponse.body();
        assertEquals("У вас нет устройств.", responseText);
    }

    @Test
    void deviceInformation() throws IOException, InterruptedException {
        String sqlCreateUserInfo = "insert into devices (device_id, user_id, token, type) VALUES ('0', '0', 'testDevice', 'temp')";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlCreateUserInfo);
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
        }
        HttpResponse<String> getDeviceResponse = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create(ServerConfig.LINK_GET_DEVICE_INFORMATION + "?login=testUser"))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(200, getDeviceResponse.statusCode());

        String responseText = getDeviceResponse.body();
        assertEquals("testDevice", responseText);
    }

    @Test
    void deleteDevices() throws IOException, InterruptedException {
        String sqlCreateUserInfo = "insert into devices (device_id, user_id, token, type) VALUES ('1', '0', 'testDevice', 'type')";
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
                                                              "token": "testDevice"
                                                            }
                                                        """
                                        )
                                )
                                .uri(URI.create(ServerConfig.LINK_DELETE_DEVICE))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );
        assertEquals(200, deleteDeviceResponse.statusCode());

        String responseText = deleteDeviceResponse.body();
        assertEquals("У вас больше нет устройств.", responseText);

        String sqlGetUserInfo = "Select * FROM devices";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlGetUserInfo);
            ResultSet resultSet = preparedStatement.executeQuery();
            assertFalse(resultSet.next());
        } catch (SQLException e) {
            LOG.error("Соединение не удалось", e);
        }
    }
}