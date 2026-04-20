package acadflow.contollers.users.lecturer;

import acadflow.models.users.Lecturer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LecturerDashboardController {
    @FXML
    private Button logoutBtn;

    @FXML
    private void onLogoutBtnClick(){
        new Lecturer().logout(logoutBtn);
    }
}
