package controller.userapi;

import application.ServerLauncher;
import dto.DbConnectionDto;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import application.repository.DatabaseConnection;

import java.sql.Connection;

public abstract class BaseHttpHandlerTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");
    protected static final Logger LOG = LoggerFactory.getLogger(UserHttpHandlerTest.class);
    protected static final DbConnectionDto dbConnectionDto = new DbConnectionDto();
    private static final Thread thread = new Thread(() -> {
        try {
            ServerLauncher.runApplication(new String[0], dbConnectionDto);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

    static {
        POSTGRES.start();
    }

    Connection connection = DatabaseConnection.getConnection(dbConnectionDto);

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        dbConnectionDto.url = POSTGRES.getJdbcUrl();
        dbConnectionDto.user = POSTGRES.getUsername();
        dbConnectionDto.password = POSTGRES.getPassword();
        startServer();
    }

    private static void startServer() throws InterruptedException {
        if (!thread.isAlive()) {
            thread.start();
        }
        Thread.sleep(10000);
    }
}
