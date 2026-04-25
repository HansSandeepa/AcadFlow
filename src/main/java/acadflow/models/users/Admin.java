package acadflow.models.users;

import acadflow.models.getterSetter.AdminCurrentData;
import acadflow.util.DBConnection;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Admin extends User {
    public Admin(String regNo) {
        super(regNo);
    }

    public void updateProfile(String fullName, String gender, String email, String address) {

        // Full Name validation
        if (fullName == null || fullName.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Full Name cannot be empty!");
            errorAlert.showAndWait();
            return;
        }

        // Email validation
        if (email == null || email.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Email cannot be empty!");
            errorAlert.showAndWait();
            return;
        }

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        boolean emailMatcher = emailPattern.matcher(email).matches();
        if (!emailMatcher) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Enter valid Email Pattern!");
            errorAlert.showAndWait();
            return;
        }

        // Gender validation
        if (gender == null || gender.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Gender cannot be empty!");
            errorAlert.showAndWait();
            return;
        } else if (!(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female"))) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Gender must be male or female!");
            errorAlert.showAndWait();
            return;
        }

        // Address validation
        if (address == null || address.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Address cannot be empty!");
            errorAlert.showAndWait();
            return;
        }

        // Update user table fields
        String updateUserQuery = "UPDATE user SET Fullname = ?, Gender = ?, Email = ?, Address = ? WHERE User_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement userStmt = conn.prepareStatement(updateUserQuery)
        ) {
            String genderVal = (gender.equalsIgnoreCase("male")) ? "M" : "F";

            // Update user table
            userStmt.setString(1, fullName);
            userStmt.setString(2, genderVal);
            userStmt.setString(3, email);
            userStmt.setString(4, address);
            userStmt.setString(5, getUserId());
            userStmt.executeUpdate();

            // Show success alert
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Profile Updated");
            successAlert.setContentText("Your profile details have been updated successfully!");
            successAlert.showAndWait();

        } catch (SQLException e) {
            // Show error alert
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Unable to update profile details: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    public ArrayList<AdminCurrentData> getCurrentSelfDetails() {
        String query = "SELECT u.Fullname, a.Admin_id, u.Gender, u.Email, u.Address " +
                      "FROM user u INNER JOIN admin a ON u.User_id = a.User_id WHERE a.User_id = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, getUserId());
            try (var rs = stmt.executeQuery()) {
                ArrayList<AdminCurrentData> currentDataList = new ArrayList<>();
                while (rs.next()) {
                    String fullName = rs.getString("Fullname");
                    String adminId = rs.getString("Admin_id");
                    String gender = rs.getString("Gender");
                    String email = rs.getString("Email");
                    String address = rs.getString("Address");

                    AdminCurrentData currentData = new AdminCurrentData(fullName, adminId, gender, email, address);
                    currentDataList.add(currentData);
                }
                return currentDataList;
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: Failed to load admin details! " + e.getMessage() + "\u001B[0m");
            return new ArrayList<>();
        }
    }
}
