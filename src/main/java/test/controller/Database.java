package test.controller;

import test.tables.DevicesController;
import test.tables.UsersController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

  public static final String driver = "org.postgresql.Driver";
  public static final String url = "jdbc:postgresql://localhost:5433/test";
  public static final String user = "postgres";
  public static final String password = "scoups";

  UsersController users;
  DevicesController devices;

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

  public Database() throws ClassNotFoundException {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      System.out.println("PostgreSQL JDBC Driver не найден. Включите его в путь к вашей библиотеке");
      e.printStackTrace();
      return;
    }
    System.out.println("Драйвер PostgreSQL JDBC успешно подключен");

    users = new UsersController();
    devices = new DevicesController();
  }

  public void CreateTablesAndForeignKeys() throws SQLException {
    //users.CreateTable();
    //devices.CreateTable();
    //devices.CreateForeignKeys();
  }

  public void GetAllAndGetByIdAndUpdateAndDeleteUsers() throws SQLException {
    //users.GetAll();
    //users.GetByIdUser(id);
    //users.Update(id, row, colum, information);
    //users.GetAll();
    //users.Delete(id);
    //users.CreateUser();

  }

  public void GetAllAndGetByIdAndUpdateAndDeleteDevices() throws SQLException {
    //devices.GetAll();
    //devices.GetByIdDevices(id);
    //devices.Update(id, row, colum, information);
    // devices.Delete(id);
    // devices.CreateDevices();
  }

  public static void main(String[] args) {
    try {
      Database database = new Database();
      database.CreateTablesAndForeignKeys();
      database.GetAllAndGetByIdAndUpdateAndDeleteUsers();
      database.GetAllAndGetByIdAndUpdateAndDeleteDevices();
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Ошибка SQL !");
    } catch (ClassNotFoundException e) {
      System.out.println("JDBC драйвер для СУБД не найден!");
    }
  }
}
