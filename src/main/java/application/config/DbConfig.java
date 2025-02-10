package application.config;

public class DbConfig {
    public static final String DRIVER = "org.postgresql.Driver";
    public static final String LOCAL_HOST = "5432";
    public static final String DATABASE_NAME = "postgres";
    public static final String URL = "jdbc:postgresql://localhost:" + LOCAL_HOST + "/" + DATABASE_NAME;
    public static final String USER = "postgres";
    public static final String PASSWORD = "123";
}
