package acadflow.contollers.users.undergraduate;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.getterSetter.UndergraduateCurrentData;
import acadflow.models.users.Undergraduate;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Objects;

public class UndergraduateDashboardController extends CommonUserController {

    @FXML
    private Label fullName;
    @FXML
    private Label regNoLabel;
    @FXML
    private Label departmentNo;
    @FXML
    private Label batchNo;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea addressField;
    @FXML
    private Label genderField;

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
        //setup user image
        Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);
        setUsersDetails();
    }

    @FXML
    @Override
    protected void cancelSelfFormDetails(){
        emailField.setText("");
        addressField.setText("");
    }

    @FXML
    private void saveProfileDetails(){
        String email = emailField.getText();
        String address = addressField.getText();

        Undergraduate undergraduate = new Undergraduate(regNo);
        undergraduate.updateProfile(email, address);    //update user details
    }

    private void setUsersDetails(){
        ArrayList<UndergraduateCurrentData> undergradDetails = new Undergraduate(regNo).getCurrentSelfDetails();
        if (undergradDetails != null && !undergradDetails.isEmpty()) {
            String gender = (undergradDetails.get(0).getGender().equalsIgnoreCase("M")) ? "Male" : "Female";

            genderField.setText(gender);
            fullName.setText(undergradDetails.get(0).getFullName());
            regNoLabel.setText(regNo);
            departmentNo.setText(undergradDetails.get(0).getDepartment());
            batchNo.setText(undergradDetails.get(0).getBatch());
            emailField.setText(undergradDetails.get(0).getEmail());
            addressField.setText(undergradDetails.get(0).getAddress());
        } else {
            System.out.println("\u001B[31mERROR: No undergraduate details found for registration number: " + regNo + "\u001B[0m");
        }
    }
}
