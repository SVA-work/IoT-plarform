package test.tables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  public static final String driver = "org.postgresql.Driver";
  public static final String url = "jdbc:postgresql://localhost:5433/postgres";
  public static final String user = "postgres";
  public static final String password = "Safari";

  public static Connection getConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(url, user, password);
      if (connection != null) {
        System.out.println("Вы успешно подключились к базе данных");
      } else {
        System.out.println("Не удалось подключиться к базе данных");
      }
    } catch (SQLException e) {
      System.out.println("Соединение не удалось");
    }
    return connection;
  }

  public DatabaseConnection() {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      System.out.println("PostgreSQL JDBC Driver не найден. Включите его в путь к вашей библиотеке");
      return;
    }
    System.out.println("Драйвер PostgreSQL JDBC успешно подключен");
  }

}