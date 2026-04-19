package acadflow.models.users;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class User implements UserImplementations{
    @Override
    public void logout(Button logoutBtn) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/acadflow/login/loginPage.fxml"));
        try {
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Login");
            newStage.show();

            //close the current dashboard page
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println("\u001B[31mERROR: Failed to load user login view! " + e.getMessage() + "\u001B[0m");
        }
    }

    @Override
    public void showUserName() {

    }

    @Override
    public void loadUserName() {

    }

    @Override
    public void loadUserId() {

    }

    @Override
    public void loadUserImage() {

    }

    @Override
    public void updateMyUserImage(String picturePath) {

    }

    @Override
    public void showNotices() {

    }


}
