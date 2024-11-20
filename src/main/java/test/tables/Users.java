package test.tabels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Users extends BaseTable implements TableOperations {
    public Users() throws SQLException {
        super("users");
    }

    @Override
    public void CreateTable() throws SQLException {
        super.ExecuteSqlStatement("CREATE TABLE IF NOT EXISTS users(" +
                "user_id INTEGER PRIMARY KEY," +
                "login VARCHAR(255) NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "device VARCHAR(255), " +
                "telegram_token VARCHAR(255) NOT NULL)", "Создана таблица " + tableName);
    }

    @Override
    public void CreateForeignKeys() throws SQLException {
    }

    @Override
    public void GetAll() throws SQLException {
        String selectAllUsers = "SELECT * FROM " + tableName;
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = statement.executeQuery(selectAllUsers);

        resultSet.first();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            System.out.print(resultSet.getMetaData().getColumnName(i) + "\t\t\t");
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

    @Override
    public void GetByIdUser(int id) throws SQLException{
        String sql = "SELECT * FROM users WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String login = resultSet.getString("login");
            String password = resultSet.getString("password");
            String device = resultSet.getString("device");
            String telegramToken = resultSet.getString("telegram_token");
            System.out.println(id + " " + login + " " + password + " " + device + " " + telegramToken);
        }
    }

    @Override
    public void Update(int id, String row, String column, String information) throws SQLException {
        String updateUser = "UPDATE " + tableName +" SET " + column + " = '" + information + "' WHERE " + row + " = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(updateUser);
    }

    @Override
    public void Delete(int id, String row) throws SQLException {
        String deleteUser = "DELETE FROM " + tableName + " WHERE " + row + " = " + id;
        Statement statement = connection.createStatement();
        statement.executeUpdate(deleteUser);
    }

    @Override
    public void CreateUser() throws SQLException {
        String insertUser = "INSERT INTO users (user_id, login, password, device, telegram_token) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertUser);

        preparedStatement.setInt(1, 1);
        preparedStatement.setString(2, "login_user");
        preparedStatement.setString(3, "password_user");
        preparedStatement.setString(4, "devices_user");
        preparedStatement.setString(5, "telegram_token_");

        preparedStatement.executeUpdate();
    }

    @Override
    public void GetByIdDevices (int idUser) throws SQLException {
    }

    @Override
    public void CreateDevices() throws SQLException {
    }
}