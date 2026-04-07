package acadflow.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBConnection {

    private final static Dotenv dotenv = Dotenv.load();

    private static final String DB_NAME = "acadflow_db";
    private static final String URL = "jdbc:mysql://localhost:3306/"+ DB_NAME +"?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = dotenv.get("DB_PASSWORD") != null ? dotenv.get("DB_PASSWORD") : "";

    public static boolean getConnection(){
        try {
            DriverManager.getConnection(URL,USER,PASSWORD);
            return true;
        } catch (SQLException e) {
            System.out.println("\u001B[31mERROR: " + e.getMessage() + "\u001B[0m");
            return false;
        }
    }

}
