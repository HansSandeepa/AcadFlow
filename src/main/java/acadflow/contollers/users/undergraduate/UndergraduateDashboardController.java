package acadflow.contollers.users.undergraduate;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.users.Undergraduate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UndergraduateDashboardController extends CommonUserController {
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
        new Undergraduate().logout(logoutBtn);
    }
}
