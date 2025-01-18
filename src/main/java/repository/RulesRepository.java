package repository;

import dto.DbConnectionDto;
import dto.entity.RuleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RulesRepository extends AbstractRepository<RuleDto> {
  private final String tableName = "rules";
  private final String tableID = "rule_id";

  private static final Logger LOG = LoggerFactory.getLogger(RulesRepository.class);

  private final DbConnectionDto dbConnectionDto;

  public RulesRepository(DbConnectionDto dbConnectionDto) {
    this.dbConnectionDto = dbConnectionDto;
  }

  @Override
  public RuleDto createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "rule_id SERIAL PRIMARY KEY," +
            "device_id INTEGER REFERENCES devices(device_id)," +
            "rule VARCHAR(255) NOT NULL)";
    RuleDto response = new RuleDto();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
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
  public List<RuleDto> getAll() {
    String sql = "SELECT * FROM " + tableName;
    List<RuleDto> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        RuleDto response = new RuleDto();
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
  public RuleDto getById(RuleDto message) {
    int id = Integer.parseInt(message.getRuleId());
    String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
    RuleDto response = new RuleDto();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
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
  public RuleDto update(RuleDto entity) {
    String sql = "UPDATE " + tableName + " SET " + entity.getColumnTitle() + " = ? WHERE " + tableID + " = ?";
    RuleDto response = new RuleDto();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
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
  public RuleDto delete(RuleDto message) {
    int id = Integer.parseInt(message.getRuleId());
    RuleDto response = new RuleDto();
    String sql = "DELETE FROM " + tableName + " WHERE " + tableID + " = ?";
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
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
  public RuleDto create(RuleDto entity) {
    String sql = "INSERT INTO " + tableName + " (device_id, rule) VALUES (?, ?)";
    RuleDto response = new RuleDto();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
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
