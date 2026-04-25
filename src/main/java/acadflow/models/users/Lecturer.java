package acadflow.models.users;

import acadflow.models.getterSetter.LecturerCurrentData;
import acadflow.util.DBConnection;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Lecturer extends User {
    public Lecturer(String regNo) {
        super(regNo);
    }

    public void updateProfile(String fullName, String department, String officeRoom, String gender, String email, String address) {

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
        String[] departmentTypes = {"ET","BST","ICT","MDS"};
        if (department == null || department.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Department cannot be empty!");
            errorAlert.showAndWait();
            return;
        }else if (!department.equals(departmentTypes[0]) && !department.equals(departmentTypes[1]) && !department.equals(departmentTypes[2]) && !department.equals(departmentTypes[3])) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Select Valid Department!");
            errorAlert.showAndWait();
            return;
        }

        // Office Room validation
        Pattern officeRoomPattern = Pattern.compile("^\\d{1,3}$");
        if (officeRoom == null || officeRoom.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Office Room cannot be empty!");
            errorAlert.showAndWait();
            return;
        } else if (!officeRoomPattern.matcher(officeRoom).matches()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Office Room must be a number! Example: 101");
            errorAlert.showAndWait();
            return;
            
        }

        // Email validation
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        boolean emailMatcher = emailPattern.matcher(email).matches();
        if (email.trim().isBlank()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Email cannot be empty!");
            errorAlert.showAndWait();
            return;
        } else if (!emailMatcher) {
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
        // Update lecturer table fields
        String updateLecturerQuery = "UPDATE lecturer SET Department = ?, Office_room = ? WHERE User_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement userStmt = conn.prepareStatement(updateUserQuery);
                PreparedStatement lecturerStmt = conn.prepareStatement(updateLecturerQuery)
        ) {
            // Update user table
            String genderVal = (gender.equalsIgnoreCase("male")) ? "M" : "F";

            userStmt.setString(1, fullName);
            userStmt.setString(2, genderVal);
            userStmt.setString(3, email);
            userStmt.setString(4, address);
            userStmt.setString(5, getUserId());
            userStmt.executeUpdate();

            // Update lecturer table
            lecturerStmt.setString(1, department);
            lecturerStmt.setString(2, officeRoom);
            lecturerStmt.setString(3, getUserId());
            lecturerStmt.executeUpdate();

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

    public ArrayList<LecturerCurrentData> getCurrentSelfDetails() {
        String query = "SELECT u.Fullname, l.Lecturer_id, l.Department, l.Office_room, u.Gender, u.Email, u.Address " +
                      "FROM user u INNER JOIN lecturer l ON u.User_id = l.User_id WHERE l.User_id = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, getUserId());
            try (var rs = stmt.executeQuery()) {
                ArrayList<LecturerCurrentData> currentDataList = new ArrayList<>();
                while (rs.next()) {
                    String fullName = rs.getString("Fullname");
                    String lecturerId = rs.getString("Lecturer_id");
                    String department = rs.getString("Department");
                    String officeRoom = rs.getString("Office_room");
                    String gender = rs.getString("Gender");
                    String email = rs.getString("Email");
                    String address = rs.getString("Address");

                    LecturerCurrentData currentData = new LecturerCurrentData(fullName, lecturerId, department, officeRoom, gender, email, address);
                    currentDataList.add(currentData);
                }
                return currentDataList;
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: Failed to load lecturer details! " + e.getMessage() + "\u001B[0m");
            return new ArrayList<>();
        }
    }


public Lecturer(){

}


}

