package test.tables;

import test.controller.Database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseTable implements Closeable {
    Connection connection;
    String tableName;

    BaseTable(String tableName) throws SQLException {
        this.tableName = tableName;
        this.connection = Database.GetConnection();
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            System.out.println("Ошибка закрытия SQL соединения!");
        }
    }

    void ExecuteSqlStatement(String sql, String description) throws SQLException {
        ReopenConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
        if (description != null)
            System.out.println(description);
    }

    void ExecuteSqlStatement(String sql) throws SQLException {
        ExecuteSqlStatement(sql, null);
    }

    void ReopenConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = Database.GetConnection();
        }
    }
}
