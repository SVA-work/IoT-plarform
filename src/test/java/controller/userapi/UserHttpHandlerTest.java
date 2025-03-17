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

class UserHttpHandlerTest extends BaseHttpHandlerTest {

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
                                .uri(URI.create("http://localhost:8091/user/registration"))
                                .header("Content-Type", "application/json")
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );

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
                                .uri(URI.create("http://localhost:8091/user/entry"))
                                .header("Content-Type", "application/json")
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8)
                );

        assertEquals(200, entryResponse.statusCode());

        assertEquals("{\"login\":\"testUser\",\"password\":\"123\",\"telegramToken\":null,\"devices\":[]}", entryResponse.body());
    }
}