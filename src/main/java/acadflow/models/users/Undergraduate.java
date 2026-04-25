package acadflow.models.users;

import acadflow.models.getterSetter.UndergraduateCurrentData;
import acadflow.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Undergraduate extends User {
    public Undergraduate(String regNo) {
        super(regNo);
    }

    public void updateProfile(String email, String address) {
        // Implement the logic to update the undergraduate's profile in the database
        // This is a placeholder implementation and should be replaced with actual database code
        System.out.println("Updating profile for " + regNo);
        System.out.println("Email: " + email);
        System.out.println("Address: " + address);
    }

    public ArrayList<UndergraduateCurrentData> getCurrentSelfDetails() {

        String query = "SELECT Fullname,Department,Batch,Email,Address FROM user INNER JOIN undergraduate ON user.User_id = undergraduate.User_id WHERE undergraduate.User_id = ?";
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

                    UndergraduateCurrentData currentData = new UndergraduateCurrentData(fullName, regNo, department, batch, email, address);
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
