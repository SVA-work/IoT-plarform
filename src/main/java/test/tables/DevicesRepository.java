package test.tables;

import test.DTO.Message;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DevicesRepository extends AbstractRepository<Message> {
  private final String tableName = "devices";
  private final String tableID = "device_id";

  @Override
  public Message createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "device_id SERIAL PRIMARY KEY," +
            "user_id INTEGER REFERENCES users(user_id)," +
            "token VARCHAR(255) NOT NULL)";
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
  public List<Message> getAll() {
    String sql = "SELECT * FROM " + tableName;
    List<Message> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        Message response = new Message();
        response.setDeviceId(resultSet.getString(tableID));
        response.setUserId(resultSet.getString("user_id"));
        response.setToken(resultSet.getString("token"));
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
    int id = Integer.parseInt(message.getDeviceId());
    String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
    Message response = new Message();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        response.setDeviceId(resultSet.getString(tableID));
        response.setUserId(resultSet.getString("user_id"));
        response.setToken(resultSet.getString("token"));
      }
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  public List<Message> ruleOfDevice(Message message) {
    int id = Integer.parseInt(message.getDeviceId());
    String sql = "SELECT r.* " +
            "FROM rules r " +
            "JOIN devices d ON r.device_id = d.device_id " +
            "WHERE d.device_id = ?";
    List<Message> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Message response = new Message();
        response.setDeviceId(resultSet.getString("rule_id"));
        response.setToken(resultSet.getString("rule"));
        list.add(response);
      }
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return list;
  }

  @Override
  public Message update(Message entity) {
    String sql = "UPDATE " + tableName + " SET " + entity.getColumnTitle() + " = ? WHERE " + tableID + " = ?";
    Message response = new Message();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      switch (entity.getColumnTitle()) {
        case "user_id" -> preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
        case "token" -> preparedStatement.setString(1, entity.getToken());
      }
      preparedStatement.setInt(2, Integer.parseInt(entity.getDeviceId()));
      preparedStatement.executeUpdate();

      sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
      preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, Integer.parseInt(entity.getDeviceId()));
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        response.setDeviceId(resultSet.getString(tableID));
        response.setUserId(resultSet.getString("user_id"));
        response.setToken(resultSet.getString("token"));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  @Override
  public Message delete(Message message) {
    int id = Integer.parseInt(message.getDeviceId());
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
    String sql = "INSERT INTO " + tableName + " (user_id, token) VALUES (?, ?)";
    Message response = new Message();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
      preparedStatement.setString(2, entity.getToken());
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        long deviceId = generatedKeys.getLong(1);
        response.setDeviceId(String.valueOf(deviceId));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      response.setSuccessful(false);
    }
    return response;
  }
}
