package acadflow.contollers.login;

import acadflow.models.LoadUserViewModel;
import acadflow.util.UserType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

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
            //determine the user type from the registration number
            UserType userType = UserType.findUserTypeFromRegNo(regNo);

            if (userType == null) {
                // Display error message for unknown user type
                regNoValidation.setText("Invalid user type for registration number!");
                return;
            }

            //load corresponding view for the user
            switch (userType){
                case STUDENT:
                    System.out.println("Student type");
                    //load undergraduate view here
                    loadCorrespondingUserPage(userType, regNo, pwd);
                    break;
                case ADMIN:
                    System.out.println("Admin type");
                    //load admin view here
                    loadCorrespondingUserPage(userType, regNo, pwd);
                    break;
                case LECTURER:
                    System.out.println("Lecturer type");
                    //load lecturer view here
                    loadCorrespondingUserPage(userType, regNo, pwd);
                    break;
                case TEACHER_OFFICER:
                    System.out.println("Teacher Officer type");
                    //load technical officer view here
                    loadCorrespondingUserPage(userType, regNo, pwd);
                    break;
                default:
                    // This should not happen since we check for null above
                    regNoValidation.setText("Unknown user type error!");
            }

        }

    }

    private boolean validationCheck(String regNo, String pwd){
        //if a validation error detected set this to false
        boolean validErrorFlag = true;

        //check if the registration number is empty
        if (regNo.isBlank()){
            regNoValidation.setText("Registration Number is Required!");
            validErrorFlag = false;
        }else if (!UserType.isValidRegistrationNumber(regNo)){
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

    /**
     * Handle the result of the load page attempt and display appropriate validation messages
     */
    private boolean handleLoadResult(LoadUserViewModel.LoadResult result) {
        switch (result) {
            case SUCCESS:
                // User found and page can be loaded
                // You can add navigation logic here if needed
                System.out.println("User authenticated successfully!");
                return true;
            case USER_NOT_FOUND_IN_TABLE:
                // User not found in their specific table (student/lecturer/admin/etc.)
                regNoValidation.setText("Unable to find user!");
                break;
            case USER_NOT_FOUND_IN_COMMON_TABLE:
                // User exists in specific table but not in common user table
                regNoValidation.setText("User account not properly configured!");
                break;
            case INVALID_TABLE_NAME:
                // Invalid table name (shouldn't happen in normal flow)
                regNoValidation.setText("Invalid user type configuration!");
                break;
            case DATABASE_ERROR:
                // Database connection or query error
                regNoValidation.setText("Database error occurred!");
                break;
            case PASSWORD_MISMATCH:
                // Password does not match
                pwdValidation.setText("Incorrect password!");
                break;
             default:
                 regNoValidation.setText("An unknown error occurred!");
        }
        return false;
    }

    private void loadCorrespondingUserPage(UserType userType,String regNo,String pwd){
        LoadUserViewModel.LoadResult result = new LoadUserViewModel(userType.getTableName(), regNo, pwd).loadPage();    //handle logical requirements to load the login page

        //handle validation messages based on the load result
        if (handleLoadResult(result)) {
            //set corresponding view resources
            String viewResource;
            switch (userType){
                case STUDENT:
                   viewResource = "/acadflow/users/undergraduate/undergraduate_dashboard.fxml";
                    break;
                case ADMIN:
                    viewResource = "/acadflow/users/admin/admin_dashboard.fxml";
                    break;
                case LECTURER:
                    viewResource = "/acadflow/users/lecturer/lecturer_dashboard.fxml";
                    break;
                case TEACHER_OFFICER:
                    viewResource = "/acadflow/users/tech_officer/tech_officer_dashboard.fxml";
                    break;
                default:
                    System.out.println("\u001B[31mERROR: Invalid user type for view loading!\u001B[0m");
                     return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewResource));
            try {
                Parent root = loader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Dashboard");
                newStage.show();

                //close the current login window
                Stage currentStage = (Stage) regNoField.getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                System.out.println("\u001B[31mERROR: Failed to load user dashboard view! " + e.getMessage() + "\u001B[0m");
            }
        }
    }
}
