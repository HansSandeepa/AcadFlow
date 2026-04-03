package acadflow;

import acadflow.db.DBConnection;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage){
        try {
            DBConnection.getConnection();
            System.out.println("Database Connected!");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        stage.setTitle("Acadflow");
        stage.show();
    }
}
