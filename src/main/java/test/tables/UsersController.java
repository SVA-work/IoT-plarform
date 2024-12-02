package test.tables;

import test.DTO.Message;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsersController extends AbstractController<Message> {
  private final String tableName = "users";
  private final String tableID = "user_id";

  @Override
  public Message createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "user_id INTEGER PRIMARY KEY," +
            "login VARCHAR(255) NOT NULL," +
            "password VARCHAR(255) NOT NULL," +
            "telegram_token VARCHAR(255) NOT NULL)";
    Message response = new Message();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();
      statement.executeUpdate(sql);
      response.setSuccessful(true);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      response.setSuccessful(false);
    }
    return response;
  }

  @Override
  public Message createForeignKeys() {
    Message response = new Message();
    response.setSuccessful(false);
    return response;
  }

  @Override
  public List<Message> getAll() {
    String sql = "SELECT * FROM " + tableName;
    List<Message> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        Message response = new Message();
        response.setUserId(resultSet.getString(tableID));
        response.setLogin(resultSet.getString("login"));
        response.setPassword(resultSet.getString("password"));
        response.setTelegramToken(resultSet.getString("telegram_token"));
        list.add(response);
      }
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return list;
  }

  @Override
  public Message getById(Message message) {
    int id = Integer.parseInt(message.getUserId());
    String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
    Message response = new Message();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        response.setUserId(resultSet.getString(tableID));
        response.setLogin(resultSet.getString("login"));
        response.setPassword(resultSet.getString("password"));
        response.setTelegramToken(resultSet.getString("telegram_token"));
      }
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  @Override
  public Message update(Message entity) {
    String sql = "UPDATE " + tableName + " SET " + entity.getColumnTitle() + " = ? WHERE " + tableID + " = ?";
    Message response = new Message();

    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      switch (entity.getColumnTitle()) {
        case "login" -> preparedStatement.setString(1, entity.getLogin());
        case "password" -> preparedStatement.setString(1, entity.getPassword());
        case "telegram_token" -> preparedStatement.setString(1, entity.getTelegramToken());
      }
      preparedStatement.setInt(2, Integer.parseInt(entity.getUserId()));
      preparedStatement.executeUpdate();

      sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
      preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        response.setUserId(resultSet.getString(tableID));
        response.setLogin(resultSet.getString("login"));
        response.setPassword(resultSet.getString("password"));
        response.setTelegramToken(resultSet.getString("telegram_token"));
      }
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  @Override
  public Message delete(Message message) {
    int id = Integer.parseInt(message.getUserId());
    Message response = new Message();
    String sql = "DELETE FROM " + tableName + " WHERE " + tableID + " = ?";
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      response.setSuccessful(false);
    }
    return response;
  }

  @Override
  public Message create(Message entity) {
    String sql = "INSERT INTO " + tableName + " (user_id, login, password, telegram_token) VALUES (?, ?, ?, ?)";
    Message response = new Message();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
      preparedStatement.setString(2, entity.getLogin());
      preparedStatement.setString(3, entity.getPassword());
      preparedStatement.setString(4, entity.getTelegramToken());
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      response.setSuccessful(false);
    }
    return response;
  }
}