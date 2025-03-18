package controller.userapi;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
class RulesHttpHandlerTest extends BaseHttpHandlerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        try {
            jdbcTemplate.update("DELETE FROM rules");
            jdbcTemplate.update("DELETE FROM devices");
            jdbcTemplate.update("DELETE FROM users");
            jdbcTemplate.update("INSERT INTO users (id, login, password) VALUES ('1', 'testUser ', '123')");
            jdbcTemplate.update("INSERT INTO devices (id, user_id, device_name, type, uuid) VALUES ('1', '1', 'testDevice', 'temp', '55')");
        } catch (Exception e) {
            log.error("Соединение не удалось", e);
        }
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
                                                    "login": "testUser ",
                                                    "deviceName": "testDevice",
                                                    "rule": "Temperature",
                                                    "lowestValue": 10,
                                                    "highestValue": 100
                                                }
                                                """
                                        )
                                )
                                .uri(URI.create("http://localhost:8091/rule/apply"))
                                .header("Content-Type", "application/json")
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );

        System.out.println(addRuleResponse.body());
        assertEquals(200, addRuleResponse.statusCode());
        assertEquals("{\"rule\":\"Temperature\",\"lowestValue\":10,\"highestValue\":100,\"deviceName\":\"testDevice\"}", addRuleResponse.body());

        var rules = jdbcTemplate.queryForList("SELECT * FROM rules");
        assertEquals(1, rules.size());
        assertEquals("Temperature", rules.get(0).get("rule"));
    }

    @Test
    void deleteDeviceRule() throws IOException, InterruptedException {
        jdbcTemplate.update("INSERT INTO rules (id, device_id, rule, lowest_value, highest_value) VALUES ('1', '1', 'testRule', 10, 100)");

        HttpResponse<String> deleteRuleResponse = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .method("DELETE",
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                {
                                                    "login": "testUser ",
                                                    "deviceName": "testDevice",
                                                    "rule": "testRule",
                                                    "lowestValue": 10,
                                                    "highestValue": 100
                                                }
                                                """
                                        )
                                )
                                .uri(URI.create("http://localhost:8091/rule/delete"))
                                .header("Content-Type", "application/json")
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );

        assertEquals(200, deleteRuleResponse.statusCode());
        assertEquals("{\"rule\":\"testRule\",\"lowestValue\":10,\"highestValue\":100,\"deviceName\":\"testDevice\"}", deleteRuleResponse.body());

        var rules = jdbcTemplate.queryForList("SELECT * FROM rules");
        assertEquals(0, rules.size());
    }
}