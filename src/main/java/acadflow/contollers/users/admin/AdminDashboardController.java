package acadflow.contollers.users.admin;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.users.Admin;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AdminDashboardController extends CommonUserController {
    @FXML
    private Button logoutBtn;
    @FXML
    private Label userRegNo;

    @Override
    protected void initializeWithUserData(){
        userRegNo.setText(regNo);
    }

    @FXML
    private void onLogoutBtnClick(){
        new Admin().logout(logoutBtn);
    }
}
