package controller.userapi;

import application.ServerLauncher;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ServerLauncher.class)
@Testcontainers
public abstract class BaseHttpHandlerTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");
    protected static final Logger LOG = LoggerFactory.getLogger(UserHttpHandlerTest.class);

    private static boolean serverStarted = false;
    private static ExecutorService executorService;

    static {
        POSTGRES.start();
    }

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        if (!serverStarted) {
            startServer();
            serverStarted = true;
        }
    }

    private static void startServer() throws InterruptedException {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                ServerLauncher.main(new String[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        waitForServerToStart();
    }

    private static void waitForServerToStart() throws InterruptedException {
        Thread.sleep(10000);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(POSTGRES.getJdbcUrl());
        dataSource.setUsername(POSTGRES.getUsername());
        dataSource.setPassword(POSTGRES.getPassword());
        return dataSource;
    }

    @AfterAll
    static void afterAll() {
        executorService.shutdownNow();
    }
}
