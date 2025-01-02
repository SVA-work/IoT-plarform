package test.config;

public class DbConfig {
  public static final String driver = "org.postgresql.Driver";
  public static final String localHost = "5433";
  public static final String databaseName = "test4";
  public static final String url = "jdbc:postgresql://localhost:" + localHost + "/" + databaseName;
  public static final String user = "postgres";
  public static final String password = "scoups";
}
