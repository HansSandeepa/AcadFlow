package acadflow.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBConnection {

    private final static Dotenv dotenv = Dotenv.load();

    private static final String DB_NAME = "sakila";
    private static final String URL = "jdbc:mysql://localhost:3306/"+ DB_NAME +"?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = dotenv.get("DB_PASSWORD") != null ? dotenv.get("DB_PASSWORD") : "";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }

}
