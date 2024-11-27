package test.tables;

import test.DTO.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsersController extends BaseTable{
  public UsersController() {
    super("users");
  }

  public void createTable() {
      super.ExecuteSqlStatement("CREATE TABLE IF NOT EXISTS users(" +
              "user_id INTEGER PRIMARY KEY," +
              "login VARCHAR(255) NOT NULL," +
              "password VARCHAR(255) NOT NULL," +
              "device VARCHAR(255), " +
              "telegram_token VARCHAR(255) NOT NULL)", "Создана таблица " + tableName);

  }

  public void getAll() {
    Message response = new Message();
    getAll(response);
  }

  // что это выводит
  private Message[] getAll(Message message) {

    try {
      String selectAllUsers = "SELECT * FROM " + tableName;
      Statement statement = null;


    resultSet.first();
    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
      System.out.print(resultSet.getMetaData().getColumnName(i) + "\t\t\t");
    }
    System.out.println();

    resultSet.beforeFirst();
    while (resultSet.next()) {
      for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
        System.out.print(resultSet.getString(i) + "\t\t\t");
      }
      System.out.println();
    }
  }

  @Override
  public Message GetByIdUser(int id) throws SQLException {
    String sql = "SELECT * FROM users WHERE user_id = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, id);
    ResultSet resultSet = preparedStatement.executeQuery();

    Message response = new Message();
    while (resultSet.next()) {
      response.setLogin(resultSet.getString("login"));
      response.setPassword(resultSet.getString("password"));
      response.setDeviceId(resultSet.getString("device"));
      response.setTelegramToken(resultSet.getString("telegram_token"));
    }
    return response;
  }

  @Override
  public boolean Update(int id, String row, String column, String information) {
    try {
      String updateUser = "UPDATE " + tableName + " SET " + column + " = '" + information + "' WHERE " + row + " = " + id;
      Statement statement = connection.createStatement();
      statement.executeUpdate(updateUser);
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public boolean Delete(int id, String row) {
    try {
      String deleteUser = "DELETE FROM " + tableName + " WHERE " + row + " = " + id;
      Statement statement = connection.createStatement();
      statement.executeUpdate(deleteUser);
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public boolean CreateUser() {
    try {
      String insertUser = "INSERT INTO users (user_id, login, password, device, telegram_token) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(insertUser);

      preparedStatement.setInt(1, 1);
      preparedStatement.setString(2, "login_user");
      preparedStatement.setString(3, "password_user");
      preparedStatement.setString(4, "devices_user");
      preparedStatement.setString(5, "telegram_token_");

      preparedStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public Message GetByIdDevices(int idUser) throws SQLException {
  }

  @Override
  public boolean CreateDevices() throws SQLException {
  }
}