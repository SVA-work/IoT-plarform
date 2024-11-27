package test.tables;

import test.DTO.Message;

import java.sql.SQLException;

public interface TableOperations {
  void CreateTable() throws SQLException;

  void CreateForeignKeys() throws SQLException;

  Message[] GetAll() throws SQLException;

  boolean Update(int id, String row, String column, String information);

  boolean Delete(int id, String row);

  Message GetByIdUser(int id) throws SQLException;

  boolean CreateUser();

  Message GetByIdDevices(int id) throws SQLException;

  boolean CreateDevices();
}
