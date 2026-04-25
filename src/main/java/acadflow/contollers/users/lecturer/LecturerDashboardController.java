package acadflow.contollers.users.lecturer;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.users.Lecturer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class LecturerDashboardController extends CommonUserController {

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
        //setup user image
        Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);
    }

    @FXML
    private void onLogoutBtnClick(){
        new Lecturer().logout(logoutBtn);
    }
}
