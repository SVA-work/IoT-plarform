package tables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  public static final String user = DbConfig.user;
  public static final String password = DbConfig.password;
  public static final String url = "jdbc:postgresql://localhost:" + DbConfig.localHost + "/" + DbConfig.databaseName;

  private static final Logger LOG = LoggerFactory.getLogger(DatabaseConnection.class);

  public static Connection getConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(url, user, password);
      if (connection != null) {
        LOG.info("Вы успешно подключились к базе данных");
      } else {
        LOG.info("Не удалось подключиться к базе данных");
      }
    } catch (SQLException e) {
      LOG.info("Соединение не удалось");
    }
    return connection;
  }

  public DatabaseConnection() {
    try {
      Class.forName(DbConfig.driver);
    } catch (ClassNotFoundException e) {
      LOG.info("PostgreSQL JDBC Driver не найден. Включите его в путь к вашей библиотеке");
      return;
    }
    LOG.info("Драйвер PostgreSQL JDBC успешно подключен");
  }
}
