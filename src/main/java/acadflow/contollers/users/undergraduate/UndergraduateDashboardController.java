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

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
    }

    @FXML
    private void onLogoutBtnClick(){
        new Undergraduate().logout(logoutBtn);
    }
}
