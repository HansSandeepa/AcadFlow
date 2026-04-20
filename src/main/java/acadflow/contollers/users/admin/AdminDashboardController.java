package acadflow.contollers.users.admin;

import acadflow.models.users.Admin;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminDashboardController {
    @FXML
    private Button logoutBtn;

    @FXML
    private void onLogoutBtnClick(){
        new Admin().logout(logoutBtn);
    }
}
