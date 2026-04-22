package acadflow.contollers.users.lecturer;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.users.Lecturer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LecturerDashboardController extends CommonUserController {
    @FXML
    private Button logoutBtn;
    @FXML
    private Label userRegNo;
    @FXML
    private Label username;

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
    }

    @FXML
    private void onLogoutBtnClick(){
        new Lecturer().logout(logoutBtn);
    }
}
