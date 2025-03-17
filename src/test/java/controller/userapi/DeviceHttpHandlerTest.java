package controller.userapi;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeviceHttpHandlerTest extends BaseHttpHandlerTest {

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
                                                    "deviceName": "testDevice",
                                                    "type": "temp",
                                                    "uuid": "55"
                                                }
                                                """
                                        )
                                )
                                .uri(URI.create("http://localhost:8091/device/add"))
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
                                .uri(URI.create("http://localhost:8091/device/testUser/rules/testDevice"))
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
                                .uri(URI.create("http://localhost:8091/device/delete"))
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
}