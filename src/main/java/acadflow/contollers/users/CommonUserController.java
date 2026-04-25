package acadflow.contollers.users;

import acadflow.models.ProfileImagePicker;
import acadflow.models.users.User;
import acadflow.util.FileStorageHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class CommonUserController {
    protected String regNo;
    protected String nameOfUser;
    protected String userImagePath;
    protected String userId;
    private User user;

    //Common fxml components throughout all the user dashboards
    @FXML
    protected Button logoutBtn;
    @FXML
    protected Label userRegNo;
    @FXML
    protected Label username;
    @FXML
    protected ImageView userMainImage;  //user Image on change self profile view
    @FXML
    protected ImageView userImg;    //user Image on top panel

    public void setUserDetails(String regNo){
        this.regNo = regNo;
        loadTopPanelUserDetails();
        initializeWithUserData();
    }

    /**
     * This method is called after regNo is set. Override this in subclasses
     * instead of using initialize() if you need the regNo value.
     */
    protected void initializeWithUserData(){
        // Subclasses can override this method
    }

    protected void loadTopPanelUserDetails(){
        user = new User(regNo);
        nameOfUser = user.loadUserName();
        userImagePath = user.loadUserImagePath();
        userId = user.getUserId();
    }

    @FXML
    protected void handleUploadButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        uploadProfileImage(stage);
    }

    protected void uploadProfileImage(Stage owenerStage){
        File selectedFile = ProfileImagePicker.pickImage(owenerStage);

        if(selectedFile == null){
            return;
        }

        try {
            String savedPath = FileStorageHandler.saveProfileImage(selectedFile);

            if (savedPath != null) {
                user.updateUserImage(savedPath);

                // Success alert
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Profile picture updated successfully!");
                success.showAndWait();

                //set updated profile picture
                File imageFile = new File(savedPath);
                try(InputStream stream = Files.newInputStream(imageFile.toPath())){
                    Image profilePic = new Image(stream);
                    userMainImage.setImage(profilePic);
                    userImg.setImage(profilePic);

                }catch(IOException e){
                    System.out.println("\u001B[31mERROR: Failed to load updated profile image! " + e.getMessage() + "\u001B[0m");
                }
            }

        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("File Error");
            error.setHeaderText(null);
            error.setContentText("An error occurred while saving the image.\n" + e.getMessage());
            error.showAndWait();

        }
    }

    @FXML
    protected void onLogoutBtnClick() {
        new User().logout(logoutBtn);
    }
}
