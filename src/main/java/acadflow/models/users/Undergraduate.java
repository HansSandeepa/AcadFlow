package acadflow.models.users;

import acadflow.models.getterSetter.UndergraduateCurrentData;
import acadflow.util.DBConnection;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Undergraduate extends User {
    public Undergraduate(String regNo) {
        super(regNo);
    }

    public void updateProfile(String email, String address) {

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        boolean emailMatcher = emailPattern.matcher(email).matches();

        //email validate
        if (email.isBlank()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Email cannot be empty!");
            errorAlert.showAndWait();
            return;
        }else if(!emailMatcher){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Enter valid Email Pattern!");
            errorAlert.showAndWait();
            return;
        }

        //Address validate
        if (address.isBlank()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Update Failed");
            errorAlert.setContentText("Address cannot be empty!");
            errorAlert.showAndWait();
            return;
        }

        String query = "UPDATE user SET Email = ?, Address = ? WHERE User_id = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, email);
            stmt.setString(2, address);
            stmt.setString(3, getUserId());
            stmt.executeUpdate();

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

    public ArrayList<UndergraduateCurrentData> getCurrentSelfDetails() {

        String query = "SELECT Fullname,Department,Batch,Email,Address,Gender FROM user INNER JOIN undergraduate ON user.User_id = undergraduate.User_id WHERE undergraduate.User_id = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, getUserId());
            try (var rs = stmt.executeQuery()) {
                ArrayList<UndergraduateCurrentData> currentDataList = new ArrayList<>();
                while (rs.next()) {
                    String fullName = rs.getString("Fullname");
                    String department = rs.getString("Department");
                    String batch = rs.getString("Batch");
                    String email = rs.getString("Email");
                    String address = rs.getString("Address");
                    String gender = rs.getString("Gender");

                    UndergraduateCurrentData currentData = new UndergraduateCurrentData(fullName, regNo, department, batch, email, address,gender);
                    currentDataList.add(currentData);
                }
                return currentDataList;
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: Failed to load undergraduate details! " + e.getMessage() + "\u001B[0m");
            return new ArrayList<>();
        }
    }
}
