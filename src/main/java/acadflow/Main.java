package acadflow;

import acadflow.db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        //setup view
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage.setTitle("Acadflow");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setupDatabaseConnection();
    }

    private void setupDatabaseConnection() {

        if (DBConnection.getConnection()) {
            //show to login page
        }else{
            //show the unable to connect page
        }
//        Notifications.create()
//                .title("Database Connected!")
//                .text("Welcome to AcadFlow!!!")
//                .position(Pos.CENTER)
//                .showInformation();
//        System.out.println("Database Connected!");
//        //e.printStackTrace();
//        Notifications.create()
//                .title("Unable to Connect ot server")
//                .text(e.getMessage())
//                .position(Pos.CENTER)
//                .showInformation();

    }
}
