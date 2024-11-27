package test.tables;

import test.DTO.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DevicesController extends BaseTable {
  public DevicesController() {
    super("devices");
  }

  public void createTable() {
    super.ExecuteSqlStatement("CREATE TABLE IF NOT EXISTS devices(" +
            "device_id INTEGER PRIMARY KEY," +
            "user_id INTEGER NOT NULL," +
            "token VARCHAR(255) NOT NULL)");
  }

  public void createForeignKeys() {
    super.ExecuteSqlStatement(" ALTER TABLE devices ADD FOREIGN KEY (user_id) REFERENCES users(user_id)");
  }

  public Message[] getAll(Message message) {
    String selectAllUsers = "SELECT * FROM " + tableName;
    Statement statement = null;
    try {
      statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
      ResultSet resultSet = statement.executeQuery(selectAllUsers);
    } catch (SQLException e) {
      System.out.println("Failed to execute SQL query %s" + e.getMessage());
    }
    return null;
  }

  public Message getById(Message message, int id) {
    String sql = "SELECT * FROM devices WHERE user_id = ?";
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();

      Message response = new Message();
      while (resultSet.next()) {
        response.setUserId(resultSet.getString("user_id"));
        response.setTelegramToken(resultSet.getString("token"));
      }

      return response;
    } catch (SQLException e) {
      System.out.println("Failed to execute SQL query %s" + e.getMessage());
      return  null;
    }
  }

  public Message update(Message message, String column, String newInformation) {
    String updateUser = "UPDATE devices SET " + column + " = " + newInformation + " WHERE device_id = " + message.getDeviceId();
    Statement statement = null;
    try {
      statement = connection.createStatement();
      statement.executeUpdate(updateUser);

      return message;
    } catch (SQLException e) {
      System.out.println("Failed to execute SQL query %s" + e.getMessage());
      return null;
    }

    @Override
    public boolean Update(int id, String row, String column, String information) throws SQLException {
        String updateUser = "UPDATE " + tableName + " SET " + column + " = " + information + " WHERE " + row + " = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(updateUser);
    }

  public Message delete(Message message, int id) {
    String deleteUser = "DELETE FROM users WHERE device_id = " + id;
    Statement statement = null;
    try {
      statement = connection.createStatement();
      statement.executeUpdate(deleteUser);

    @Override
    public boolean CreateUser() throws SQLException {
    }

    @Override
    public Message GetByIdDevices(int id) throws SQLException {
        String sql = "SELECT * FROM devices WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String userId = resultSet.getString("user_id");
            String token = resultSet.getString("token");
            System.out.println(id + " " + userId + " " + token);
        }
    }

    @Override
    public boolean CreateDevices() throws SQLException {
        String insertDevice = "INSERT INTO devices (device_id, user_id, token) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertDevice);

        preparedStatement.setInt(1, 1);
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "token_token");

        preparedStatement.executeUpdate();
    }
}
