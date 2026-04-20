package acadflow.contollers.users.tech_officer;

import acadflow.models.users.TechnicalOfficer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TechOfficerDashboardController {
    @FXML
    private Button logoutBtn;

    @FXML
    private void onLogoutBtnClick(){
        new TechnicalOfficer().logout(logoutBtn);
    }
}
