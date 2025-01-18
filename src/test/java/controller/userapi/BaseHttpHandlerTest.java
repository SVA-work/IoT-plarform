package controller.userapi;

import controller.ServerLauncher;
import dto.DbConnectionDto;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import repository.DatabaseConnection;

import java.sql.Connection;

public abstract class BaseHttpHandlerTest {

  protected static final Logger LOG = LoggerFactory.getLogger(UserHttpHandlerTest.class);

  Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  static {
    POSTGRES.start();
  }

  protected static final DbConnectionDto dbConnectionDto = new DbConnectionDto();

  private static final Thread thread = new Thread(() -> {
    try {
      ServerLauncher.runApplication(new String[0], dbConnectionDto);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  });


  @BeforeAll
  static void beforeAll() throws InterruptedException {
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    dbConnectionDto.url = postgresJdbcUrl;
    dbConnectionDto.user = POSTGRES.getUsername();
    dbConnectionDto.password = POSTGRES.getPassword();
    initFlyway(postgresJdbcUrl);
    startServer();
  }

  private static void initFlyway(String postgresJdbcUrl) {
    Flyway flyway =
            Flyway.configure()
                    .outOfOrder(true)
                    .locations("classpath:db/migrations")
                    .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
                    .load();
    flyway.migrate();
  }

  private static void startServer() throws InterruptedException {
    if (!thread.isAlive()) {
      thread.start();
    }
    Thread.sleep(10000);
  }
}
