package test.tables;

import test.DTO.Message;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RulesController extends AbstractController<Message>{
  private final String tableName = "rules";
  private final String tableID = "rule_id";

  @Override
  public Message createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "rule_id SERIAL PRIMARY KEY," +
            "device_id INTEGER REFERENCES devices(device_id)," +
            "rule VARCHAR(255) NOT NULL)";
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
        response.setRuleId(resultSet.getString(tableID));
        response.setDeviceId(resultSet.getString("device_id"));
        response.setRule(resultSet.getString("rule"));
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
        response.setRuleId(resultSet.getString(tableID));
        response.setDeviceId(resultSet.getString("device_id"));
        response.setRule(resultSet.getString("rule"));
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
        case "device_id" -> preparedStatement.setInt(1, Integer.parseInt(entity.getDeviceId()));
        case "rule" -> preparedStatement.setString(1, entity.getRule());
      }
      preparedStatement.setInt(2, Integer.parseInt(entity.getRuleId()));
      preparedStatement.executeUpdate();

      sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
      preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, Integer.parseInt(entity.getRuleId()));
      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        response.setRuleId(resultSet.getString(tableID));
        response.setDeviceId(resultSet.getString("device_id"));
        response.setRule(resultSet.getString("rule"));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  @Override
  public Message delete(Message message) {
    int id = Integer.parseInt(message.getRuleId());
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
    String sql = "INSERT INTO " + tableName + " (device_id, rule) VALUES (?, ?)";
    Message response = new Message();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, Integer.parseInt(entity.getDeviceId()));
      preparedStatement.setString(2, entity.getRule());
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        long ruleId = generatedKeys.getLong(1);
        response.setRuleId(String.valueOf(ruleId));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      response.setSuccessful(false);
    }
    return response;
  }
}
