package test.controller;

import test.DTO.Message;
import test.tables.DevicesController;
import test.tables.UsersController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.netty.bootstrap.ServerBootstrap;

public class Database {
  public static final String driver = "org.postgresql.Driver";
  public static final String url = "jdbc:postgresql://localhost:5433/postgres";
  public static final String user = "postgres";
  public static final String password = "Safari";

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
      return;
    }
    System.out.println("Драйвер PostgreSQL JDBC успешно подключен");

    users = new UsersController();
    devices = new DevicesController();
  }

  public void CreateTablesAndForeignKeys(){
    System.out.println(users.createTable().isSuccessful());
    System.out.println(devices.createTable().isSuccessful());
    System.out.println(devices.createForeignKeys().isSuccessful());
  }

  public void GetAllAndGetByIdAndUpdateAndDeleteUsers() {
/*
    for (Message message : users.getAll()) {
      System.out.println(message.getUserId() + " " + message.getLogin() + " " + message.getPassword());
    }

    Message message = new Message();
    message.setUserId(String.valueOf(7));
    message.setLogin("S.Coups");
    message.setPassword("08081985");
    message.setDeviceId("1");
    message.setTelegramToken("whyscoups");
    System.out.println(users.create(message).isSuccessful());
 */
  }

  public void GetAllAndGetByIdAndUpdateAndDeleteDevices() {
/*
    for (Message message : devices.getAll()) {
      System.out.println(message.getDeviceId() + " " + message.getUserId() + " " + message.getToken());
    }

    Message message = new Message();
    message.setDeviceId(String.valueOf(1));
    message.setUserId(String.valueOf(2));
    message.setToken("scoups");
    System.out.println(devices.create(message).isSuccessful());

    Message message = new Message();
    message.setDeviceId(String.valueOf(2));
    message.setUserId(String.valueOf(1));
    message.setColumnTitle("user_id");
    System.out.println(devices.update(message).getToken());
    System.out.println(devices.getById(2).getUserId());
 */
  }

  public static void databaseConnection(Database database) {
    try {
      database.CreateTablesAndForeignKeys();
      database.GetAllAndGetByIdAndUpdateAndDeleteUsers();
      database.GetAllAndGetByIdAndUpdateAndDeleteDevices();
    } catch (SQLException e) {
      System.out.println("JDBC драйвер для СУБД не найден!");
      return;
    }
  }
}
