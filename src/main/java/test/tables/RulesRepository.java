package test.tables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DTO.RulesDto;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RulesRepository extends AbstractRepository<RulesDto>{
  private final String tableName = "rules";
  private final String tableID = "rule_id";

  private static final Logger LOG = LoggerFactory.getLogger(RulesRepository.class);

  @Override
  public RulesDto createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "rule_id SERIAL PRIMARY KEY," +
            "device_id INTEGER REFERENCES devices(device_id)," +
            "rule VARCHAR(255) NOT NULL)";
    RulesDto response = new RulesDto();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();
      statement.executeUpdate(sql);
      response.setSuccessful(true);
    } catch (SQLException e) {
      LOG.error("Соединение не удалось", e);
      response.setSuccessful(false);
    }
    return response;
  }

  @Override
  public List<RulesDto> getAll() {
    String sql = "SELECT * FROM " + tableName;
    List<RulesDto> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        RulesDto response = new RulesDto();
        response.setRuleId(resultSet.getString(tableID));
        response.setDeviceId(resultSet.getString("device_id"));
        response.setRule(resultSet.getString("rule"));
        list.add(response);
      }
      resultSet.close();
    } catch (SQLException e) {
      LOG.error("Не удалось получить информацию из таблицы rules", e);
    }
    return list;
  }

  @Override
  public RulesDto getById(RulesDto message) {
    int id = Integer.parseInt(message.getRuleId());
    String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
    RulesDto response = new RulesDto();
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
      LOG.error("Не удалось получить информацию о правиле", e);
    }
    return response;
  }

  @Override
  public RulesDto update(RulesDto entity) {
    String sql = "UPDATE " + tableName + " SET " + entity.getColumnTitle() + " = ? WHERE " + tableID + " = ?";
    RulesDto response = new RulesDto();
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
      LOG.error("Не удалось изменить данные о правиле", e);
    }
    return response;
  }

  @Override
  public RulesDto delete(RulesDto message) {
    int id = Integer.parseInt(message.getRuleId());
    RulesDto response = new RulesDto();
    String sql = "DELETE FROM " + tableName + " WHERE " + tableID + " = ?";
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
    } catch (SQLException e) {
      LOG.error("Не удалось удалить правило", e);
      response.setSuccessful(false);
    }
    return response;
  }

  @Override
  public RulesDto create(RulesDto entity) {
    String sql = "INSERT INTO " + tableName + " (device_id, rule) VALUES (?, ?)";
    RulesDto response = new RulesDto();
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
      LOG.error("Не удалось создать правило", e);
      response.setSuccessful(false);
    }
    return response;
  }
}
