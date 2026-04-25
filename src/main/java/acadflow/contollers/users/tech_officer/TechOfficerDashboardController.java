package acadflow.contollers.users.tech_officer;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.users.TechnicalOfficer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class TechOfficerDashboardController extends CommonUserController {

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
        new TechnicalOfficer().logout(logoutBtn);
    }
}
