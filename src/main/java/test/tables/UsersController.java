
      int rowCount = 0;
      if (resultSet.next()) {
        rowCount = resultSet.getInt(1);
      }

      System.out.println(rowCount);
      Message[] result = new Message[rowCount];

      System.out.println(message.getUserId());
      System.out.println(message.getLogin());
      System.out.println(message.getPassword());

      return result;
    } catch (SQLException e) {
      System.out.println("Failed to execute SQL query %s" + e.getMessage());
      return null;
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

  public Message getById(Message message, int id) {
    String sql = "SELECT * FROM users WHERE user_id = ?";
    PreparedStatement preparedStatement = null;
    Message response = new Message();

    try {
      preparedStatement = connection.prepareStatement(sql);
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


  public Message create(Message message) {
    Message response = new Message();

    try {
      String insertUser = "INSERT INTO users (user_id, login, password, device, telegram_token) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(insertUser);

      preparedStatement.setInt(1, 4);
      preparedStatement.setString(2, "login_u");
      preparedStatement.setString(3, "password_r");
      preparedStatement.setString(4, "devices_u");
      preparedStatement.setString(5, "telegram_to");

      preparedStatement.executeUpdate();

      message.setLogin(preparedStatement.getGeneratedKeys().getString("user_id"));
      message.setLogin(preparedStatement.getGeneratedKeys().getString("login"));
      message.setLogin(preparedStatement.getGeneratedKeys().getString("password"));
      message.setLogin(preparedStatement.getGeneratedKeys().getString("device"));
      message.setLogin(preparedStatement.getGeneratedKeys().getString("telegram_token"));
    } catch (SQLException e) {
      System.out.println("Failed to execute SQL query %s " + e.getMessage());
    }
    return response;
  }

  @Override
  public Message GetByIdDevices(int idUser) throws SQLException {
  }

  @Override
  public boolean CreateDevices() throws SQLException {
  }
}