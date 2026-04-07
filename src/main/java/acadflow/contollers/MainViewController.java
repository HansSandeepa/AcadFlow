package acadflow.contollers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainViewController {


    @FXML
    private Button cancelBtn;
    @FXML
    private Button clickBtn;
    @FXML
    private Label testLabel;

    @FXML
    private void showText(){
        testLabel.setText("Welcome machan!!!!");
    }

    @FXML
    private void setDefalutText(){
        testLabel.setText("");
    }
}
