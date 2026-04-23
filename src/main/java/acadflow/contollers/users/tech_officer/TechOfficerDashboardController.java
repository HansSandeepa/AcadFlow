package acadflow.contollers.users.tech_officer;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.users.TechnicalOfficer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TechOfficerDashboardController extends CommonUserController {
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
        new TechnicalOfficer().logout(logoutBtn);
    }
}
