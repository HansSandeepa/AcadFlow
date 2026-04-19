package acadflow.contollers.users.undergraduate;

import acadflow.models.users.Undergraduate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class UndergraduateDashboardController {
    @FXML
    private Button logoutBtn;

    @FXML
    private void onLogoutBtnClick(){
        new Undergraduate().logout(logoutBtn);
    }
}
