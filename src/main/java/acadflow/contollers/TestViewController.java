package acadflow.contollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class TestViewController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button clickBtn;
    @FXML
    private Label testLabel;

    @FXML
    private void initialize() {
        loadProfileImage();
    }

    //use the code inside this method in Main method to run the testView.fxml
    private void inMain(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/acadflow/testView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("test");
        stage.setScene(scene);
        stage.show();
    }

    private void loadProfileImage() {
        try {
            Image image = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/profile_pics/default_pic.jpg"))
            );
            profileImageView.setImage(image);
            System.out.println("✓ Profile image loaded successfully");
        } catch (NullPointerException e) {
            System.out.println("✗ Error loading image: /profile_pics/default_pic.jpg not found");
        } catch (Exception e) {
            System.out.println("✗ Error loading image: " + e.getMessage());
        }
    }

    @FXML
    private void showText(){
        testLabel.setText("Welcome machan!!!!");
    }

    @FXML
    private void setDefalutText(){
        testLabel.setText("");
    }
}
