package acadflow.contollers.users.undergraduate;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.users.Undergraduate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class UndergraduateDashboardController extends CommonUserController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNoFiled;
    @FXML
    private TextArea addressField;

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
    @Override
    protected void cancelSelfFormDetails(){
        emailField.setText("");
        phoneNoFiled.setText("");
        addressField.setText("");
    }
}
