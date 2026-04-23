package acadflow.models.users;

import acadflow.util.DBConnection;
import acadflow.util.UserType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.controlsfx.dialog.CommandLinksDialog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class User implements UserImplementations {

    protected String userId;
    protected String regNo;

    public User() {
    }

    public User(String regNo) {
        this.regNo = regNo;
        userId = loadUserId(regNo);
    }

    @Override
    public void logout(Button logoutBtn) {
        //show confirmation alert
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Your current session will end.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acadflow/login/loginPage.fxml"));

            try {
                Parent root = loader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Login");
                newStage.show();

                //close the current dashboard page
                Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                System.out.println("\u001B[31mERROR: Failed to load user login view! " + e.getMessage() + "\u001B[0m");
            }
        }
    }

    @Override
    public String loadUserName() {
        String userName = null;
        String query = "SELECT Fullname FROM user WHERE User_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
        ) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userName = rs.getString("Fullname");
                } else {
                    System.out.println("\u001B[33mWARNING: No user found with regNo: " + regNo + "\u001B[0m");
                }
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: Failed to load user name! " + e.getMessage() + "\u001B[0m");
        }
        return userName;
    }

    @Override
    public String loadUserId(String regNo) {
        String userId = null;
        String query = "";
        UserType userType = UserType.findUserTypeFromRegNo(regNo);

        query = switch (userType) {
            case ADMIN -> "SELECT User_id FROM admin WHERE Admin_id = ?";
            case LECTURER -> "SELECT User_id FROM lecturer WHERE Lecturer_id = ?";
            case STUDENT -> "SELECT User_id FROM undergraduate WHERE Stu_id = ?";
            case TECHNICAL_OFFICER -> "SELECT User_id FROM tec_officer WHERE T_officer_id = ?";
        };

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getString("User_id");
                } else {
                    System.out.println("\u001B[33mWARNING: No user found with regNo: " + regNo + "\u001B[0m");
                }
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: Failed to load user ID! " + e.getMessage() + "\u001B[0m");
        }
        return userId;
    }

    @Override
    public void loadUserImagePath(String regNo) {

    }

    @Override
    public void updateMyUserImage(String picturePath) {

    }

    @Override
    public void showNotices() {

    }

    @Override
    public void showTimetable() {

    }
}
