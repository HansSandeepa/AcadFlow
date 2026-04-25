package acadflow.models.users;

import acadflow.models.getterSetter.TechnicalOfficerCurrentData;
import acadflow.util.DBConnection;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TechnicalOfficer extends User {
    public TechnicalOfficer(String regNo) {
        super(regNo);
    }

    public void updateProfile(String fullName, String department, String gender, String email, String address) {

        // Full Name validation
        if (fullName == null || fullName.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Full Name cannot be empty!");
            errorAlert.showAndWait();
            return;
        }

        // Department validation
        if (department == null || department.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Department cannot be empty!");
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
        // Update tec_officer table fields
        String updateTechOfficerQuery = "UPDATE tec_officer SET Department = ? WHERE User_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement userStmt = conn.prepareStatement(updateUserQuery);
                PreparedStatement techOfficerStmt = conn.prepareStatement(updateTechOfficerQuery)
        ) {
            // Update user table
            String genderVal = (gender.equalsIgnoreCase("male")) ? "M" : "F";

            // Update user table
            userStmt.setString(1, fullName);
            userStmt.setString(2, genderVal);
            userStmt.setString(3, email);
            userStmt.setString(4, address);
            userStmt.setString(5, getUserId());
            userStmt.executeUpdate();

            // Update tec_officer table
            techOfficerStmt.setString(1, department);
            techOfficerStmt.setString(2, getUserId());
            techOfficerStmt.executeUpdate();

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

    public ArrayList<TechnicalOfficerCurrentData> getCurrentSelfDetails() {
        String query = "SELECT u.Fullname, t.T_officer_id, t.Department, t.Hire_date, u.Gender, u.Email, u.Address " +
                      "FROM user u INNER JOIN tec_officer t ON u.User_id = t.User_id WHERE t.User_id = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, getUserId());
            try (var rs = stmt.executeQuery()) {
                ArrayList<TechnicalOfficerCurrentData> currentDataList = new ArrayList<>();
                while (rs.next()) {
                    String fullName = rs.getString("Fullname");
                    String techOfficerId = rs.getString("T_officer_id");
                    String department = rs.getString("Department");
                    String hireDate = rs.getString("Hire_date");
                    String gender = rs.getString("Gender");
                    String email = rs.getString("Email");
                    String address = rs.getString("Address");

                    TechnicalOfficerCurrentData currentData = new TechnicalOfficerCurrentData(fullName, techOfficerId, department, hireDate, gender, email, address);
                    currentDataList.add(currentData);
                }
                return currentDataList;
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: Failed to load technical officer details! " + e.getMessage() + "\u001B[0m");
            return new ArrayList<>();
        }
    }
}
