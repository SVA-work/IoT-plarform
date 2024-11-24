package test.DAO;

import java.sql.SQLException;

public interface TableOperations {
  void CreateTable() throws SQLException;
  void CreateForeignKeys() throws SQLException;

  void GetAll() throws SQLException;
  void Update(int id, String row, String column, String information) throws SQLException;
  void Delete(int id, String row) throws SQLException;

  void GetByIdUser(int id) throws SQLException;
  void CreateUser()  throws SQLException;

  void GetByIdDevices(int id) throws SQLException;
  void CreateDevices()  throws SQLException;


}
