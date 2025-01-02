package tables;

import dto.DeviceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dto.UserDto;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsersRepository extends AbstractRepository<UserDto> {
  private final String tableName = "users";
  private final String tableID = "user_id";

  private static final Logger LOG = LoggerFactory.getLogger(UsersRepository.class);

  @Override
  public UserDto createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "user_id SERIAL PRIMARY KEY," +
            "login VARCHAR(255) UNIQUE NOT NULL," +
            "password VARCHAR(255) NOT NULL)";
    UserDto response = new UserDto();
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
  public List<UserDto> getAll() {
    String sql = "SELECT * FROM " + tableName;
    List<UserDto> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        UserDto response = new UserDto();
        response.setUserId(resultSet.getString(tableID));
        response.setLogin(resultSet.getString("login"));
        response.setPassword(resultSet.getString("password"));
        list.add(response);
      }
      resultSet.close();
    } catch (SQLException e) {
      LOG.error("Не удалось получить информацию из таблицы users", e);
    }
    return list;
  }

  @Override
  public UserDto getById(UserDto message) {
    int id = Integer.parseInt(message.getUserId());
    String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
    UserDto response = new UserDto();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        response.setUserId(resultSet.getString(tableID));
        response.setLogin(resultSet.getString("login"));
        response.setPassword(resultSet.getString("password"));
      }
      resultSet.close();
    } catch (SQLException e) {
      LOG.error("Не удалось получить информацию о пользователе", e);
    }
    return response;
  }

  public List<DeviceDto> devicesOfUser(UserDto message) {
    int id = Integer.parseInt(message.getUserId());
    String sql = "SELECT d.* " +
            "FROM devices d " +
            "JOIN users u ON d.user_id = u.user_id " +
            "WHERE u.user_id = ?";
    List<DeviceDto> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        DeviceDto response = new DeviceDto();
        response.setDeviceId(resultSet.getString("device_id"));
        response.setToken(resultSet.getString("token"));
        list.add(response);
      }
      resultSet.close();
    } catch (SQLException e) {
      LOG.error("Не удалось получить информацию об устройствах пользователя", e);
    }
    return list;
  }

  @Override
  public UserDto update(UserDto entity) {
    String sql = "UPDATE " + tableName + " SET " + entity.getColumnTitle() + " = ? WHERE " + tableID + " = ?";
    UserDto response = new UserDto();

    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      switch (entity.getColumnTitle()) {
        case "login" -> preparedStatement.setString(1, entity.getLogin());
        case "password" -> preparedStatement.setString(1, entity.getPassword());
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
      }
      resultSet.close();
    } catch (SQLException e) {
      LOG.error("Не удалось изменить данные о пользователе", e);
    }
    return response;
  }

  @Override
  public UserDto delete(UserDto message) {
    int id = Integer.parseInt(message.getUserId());
    UserDto response = new UserDto();
    String sql = "DELETE FROM " + tableName + " WHERE " + tableID + " = ?";
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
    } catch (SQLException e) {
      LOG.error("Не удалось удалить пользователя", e);
      response.setSuccessful(false);
    }
    return response;
  }

  @Override
  public UserDto create(UserDto entity) {
    String sql = "INSERT INTO " + tableName + " (login, password) VALUES (?, ?)";
    UserDto response = new UserDto();
    try {
      Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, entity.getLogin());
      preparedStatement.setString(2, entity.getPassword());
      preparedStatement.executeUpdate();
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        long userId = generatedKeys.getLong(1);
        response.setUserId(String.valueOf(userId));
      }
      response.setSuccessful(true);
    } catch (SQLException e) {
      LOG.error("Не удалось создать пользователя", e);
      response.setSuccessful(false);
    }
    return response;
  }
}
