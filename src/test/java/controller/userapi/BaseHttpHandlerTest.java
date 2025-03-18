package controller.userapi;

import application.ServerLauncher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public abstract class BaseHttpHandlerTest {
//
//  @BeforeAll
//  private static void waitForServerToStart() throws InterruptedException {
//    int maxAttempts = 30;
//    for (int i = 0; i < maxAttempts; i++) {
//      try (Connection connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
//        log.info("PostgreSQL is ready to accept connections");
//        return; // База данных готова
//      } catch (SQLException e) {
//        log.warn("PostgreSQL is not ready yet, retrying...");
//        Thread.sleep(1000); // Пауза 1 секунда
//      }
//    }
//    throw new RuntimeException("PostgreSQL did not start in time");
//  }
}
