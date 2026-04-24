package acadflow.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoadLoginUiThread extends Thread {

    private final Stage stage;

    public LoadLoginUiThread(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void run() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/acadflow/login/loginPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            // Wrap UI operations in Platform.runLater() to execute on FX Application Thread
            Platform.runLater(() -> {
                stage.setTitle("Acadflow");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
