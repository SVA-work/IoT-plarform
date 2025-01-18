package repository;

import dto.DbConnectionDto;
import dto.entity.TelegramTokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelegramTokenRepository extends AbstractRepository<TelegramTokenDto> {
  private final String tableName = "telegram_tokens";
  private final String tableID = "telegram_token_id";

  private static final Logger LOG = LoggerFactory.getLogger(TelegramTokenRepository.class);

  private final DbConnectionDto dbConnectionDto;

  public TelegramTokenRepository(DbConnectionDto dbConnectionDto) {
    this.dbConnectionDto = dbConnectionDto;
  }

  public void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "telegram_token_id SERIAL PRIMARY KEY," +
            "user_id INTEGER REFERENCES users(user_id)," +
            "telegram_token VARCHAR(255) NOT NULL)";
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
      Statement statement = connection.createStatement();
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      LOG.error("Соединение не удалось", e);
    }
  }

  @Override
  public List<TelegramTokenDto> getAll() {
    String sql = "SELECT * FROM " + tableName;
    List<TelegramTokenDto> list = new ArrayList<>();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        TelegramTokenDto response = new TelegramTokenDto();
        response.setTelegramTokenId(resultSet.getString(tableID));
        response.setUserId(resultSet.getString("user_id"));
        response.setTelegramToken(resultSet.getString("telegram_token"));
        list.add(response);
      }
      resultSet.close();
    } catch (SQLException e) {
      LOG.error("Не удалось получить информацию из таблицы telegram_tokens", e);
    }
    return list;
  }

  @Override
  public TelegramTokenDto getById(TelegramTokenDto message) {
    int id = Integer.parseInt(message.getTelegramTokenId());
    String sql = "SELECT * FROM " + tableName + " WHERE " + tableID + " = ?";
    TelegramTokenDto response = new TelegramTokenDto();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
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
      LOG.error("Не удалось получить информацию о telegram-токене.", e);
    }
    return response;
  }

  @Override
  public TelegramTokenDto delete(TelegramTokenDto message) {
    int id = Integer.parseInt(message.getTelegramTokenId());
    TelegramTokenDto response = new TelegramTokenDto();
    String sql = "DELETE FROM " + tableName + " WHERE " + tableID + " = ?";
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
    } catch (SQLException e) {
      LOG.error("Не удалось удалить telegram_токен", e);
      response.setSuccessful(false);
    }
    return response;
  }

  @Override
  public TelegramTokenDto create(TelegramTokenDto entity) {
    String sql = "INSERT INTO " + tableName + " (user_id, telegram_token) VALUES (?, ?)";
    TelegramTokenDto response = new TelegramTokenDto();
    try {
      Connection connection = DatabaseConnection.getConnection(dbConnectionDto);
      PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, Integer.parseInt(entity.getUserId()));
      preparedStatement.setString(2, entity.getTelegramToken());
      preparedStatement.executeUpdate();
      response.setSuccessful(true);
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        long telegramTokenId = generatedKeys.getLong(1);
        response.setTelegramTokenId(String.valueOf(telegramTokenId));
      }
    } catch (SQLException e) {
      LOG.error("Не удалось создать telegram-токен", e);
      response.setSuccessful(false);
    }
    return response;
  }
}
