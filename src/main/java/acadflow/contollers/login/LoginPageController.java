package acadflow.contollers.login;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPageController {
    @FXML
    private Label regNoValidation;
    @FXML
    private Label pwdValidation;
    @FXML
    private TextField regNoField;
    @FXML
    private PasswordField pwdField;

    @FXML
    private void onLoginBtnClicked(){
        String regNo = regNoField.getText() == null ? "" : regNoField.getText().trim();
        String pwd = pwdField.getText() == null ? "" : pwdField.getText().trim();

        if (validationCheck(regNo,pwd)){
            System.out.println("LOG: end");
        }

    }

    private boolean validationCheck(String regNo, String pwd){
        //if a validation error detected set this to false
        boolean validErrorFlag = true;

        //compile regex and match with registration number
        Pattern pattern = Pattern.compile("^tg[0-9]{4}$");
        Matcher matcher = pattern.matcher(regNo);

        //check if the registration number is empty
        if (regNo.isBlank()){
            regNoValidation.setText("Registration Number is Required!");
            validErrorFlag = false;
        }else if (!matcher.matches()){
            regNoValidation.setText("Enter a valid Registration Number!");
            validErrorFlag = false;
        }else{
            regNoValidation.setText("");
        }

        //check if the password field is empty
        if(pwd.isBlank()){
            pwdValidation.setText("Password is Required!");
            validErrorFlag = false;
        }else{
            pwdValidation.setText("");
        }


        return validErrorFlag;
    }

}
