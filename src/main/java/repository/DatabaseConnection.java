package repository;

import config.DbConfig;
import dto.DbConnectionDto;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  private static final Logger LOG = LoggerFactory.getLogger(DatabaseConnection.class);

  public static Connection getConnection(DbConnectionDto dbConnectionDto) {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(dbConnectionDto.url, dbConnectionDto.user, dbConnectionDto.password);
      if (connection != null) {
        LOG.info("Вы успешно подключились к базе данных");
      } else {
        LOG.error("Не удалось подключиться к базе данных");
      }
    } catch (SQLException e) {
      LOG.error("Соединение не удалось");
    }
    return connection;
  }

  public DatabaseConnection() {
    try {
      Class.forName(DbConfig.driver);
    } catch (ClassNotFoundException e) {
      LOG.error("PostgreSQL JDBC Driver не найден. Включите его в путь к вашей библиотеке");
      return;
    }
    LOG.info("Драйвер PostgreSQL JDBC успешно подключен");
  }
}
