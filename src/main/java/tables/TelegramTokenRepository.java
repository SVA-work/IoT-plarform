package tables;

import dto.UserDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelegramTokenRepository extends AbstractRepository<UserDto>{
  private final String tableName = "telegram_tokens";
  private final String tableID = "telegram_token_id";

  public UserDto createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "telegram_token_id SERIAL PRIMARY KEY," +
            "user_id INTEGER REFERENCES users(user_id)," +
            "telegram_token VARCHAR(255) NOT NULL)";
    UserDto response = new UserDto();
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
  public List<UserDto> getAll() {
    String sql = "SELECT * FROM " + tableName;
    List<UserDto> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        UserDto response = new UserDto();
        response.setTelegramTokenId(resultSet.getString(tableID));
        response.setUserId(resultSet.getString("user_id"));
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
  public UserDto getById(UserDto message) {
    int id = Integer.parseInt(message.getDeviceId());
    String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
    UserDto response = new UserDto();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        response.setTelegramTokenId(resultSet.getString(tableID));
        response.setUserId(resultSet.getString("user_id"));
        response.setTelegramToken(resultSet.getString("telegram_token"));
      }
      resultSet.close();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  @Override
  public UserDto update(UserDto entity) {
    String sql = "UPDATE " + tableName + " SET " + entity.getColumnTitle() + " = ? WHERE " + tableID + " = ?";
    UserDto response = new UserDto();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      switch (entity.getColumnTitle()) {
        case "user_id" -> preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
        case "telegram_token" -> preparedStatement.setString(1, entity.getTelegramToken());
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
        response.setTelegramToken(resultSet.getString("telegram_token"));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return response;
  }

  @Override
  public UserDto delete(UserDto message) {
    int id = Integer.parseInt(message.getDeviceId());
    UserDto response = new UserDto();
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
  public UserDto create(UserDto entity) {
    String sql = "INSERT INTO " + tableName + " (user_id, telegram_token) VALUES (?, ?)";
    UserDto response = new UserDto();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
      preparedStatement.setString(2, entity.getTelegramToken());
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

  public void createFunction() {
    String sql = "CREATE OR REPLACE FUNCTION insert_into_telegram_tokens() " +
            "RETURNS TRIGGER AS $$ " +
            "BEGIN " +
            "INSERT INTO telegram_tokens (user_id, telegram_token)" +
            "VALUES (NEW.user_id, NEW.telegram_token);" +
            "RETURN NEW;" +
            "END;" +
            "$$ LANGUAGE plpgsql;";
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e);
    }
  }

  public void createTrigger() {
    String sql = "CREATE TRIGGER after_insert_users " +
            "AFTER INSERT ON users " +
            "FOR EACH ROW " +
            "EXECUTE FUNCTION insert_into_telegram_tokens();";
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e);
    }
  }

}