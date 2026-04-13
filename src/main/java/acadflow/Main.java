package acadflow;

import acadflow.db.DBConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (DBConnection.getConnection()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acadflow/login/loginPage.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Acadflow");
            stage.setScene(scene);
            stage.show();
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection Failed");
        alert.setHeaderText("Unable to connect to the database");
        alert.setContentText("Please check your connection settings and try again.");

        ButtonType retry = new ButtonType("Retry");
        ButtonType exit  = new ButtonType("Exit");
        alert.getButtonTypes().setAll(retry, exit);

        alert.showAndWait().ifPresent(response -> {

            if (response == retry) {
                if (DBConnection.getConnection()) {
                    // retry to start db connection
                    Stage newStage = new Stage();
                    try {
                        start(newStage);
                    }
                    catch (Exception e) {
                        System.out.println("\u001B[31mERROR: " + e.getMessage() + "\u001B[0m");
                    }

                } else {
                    //try again if retry failed
                    showErrorDialog();
                }
            } else {
                // exit program in exit button click
                Platform.exit();
                System.exit(0);
            }
        });
    }
}