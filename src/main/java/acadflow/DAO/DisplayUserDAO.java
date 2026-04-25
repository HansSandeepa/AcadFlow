package acadflow.DAO;

import acadflow.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayUserDAO {

    public List<DisplayUser> getAllDisplayUsers() {
        List<DisplayUser> users = new ArrayList<>();
        String query = "SELECT * FROM user ORDER BY User_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(extractDisplayUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public List<DisplayUser> getDisplayUsersByType(String userType) {
        List<DisplayUser> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE User_type = ? ORDER BY User_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(extractDisplayUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting users by type: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public DisplayUser getDisplayUserById(int userId) {
        String query = "SELECT * FROM user WHERE User_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractDisplayUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addDisplayUser(DisplayUser user) {
        String query = "INSERT INTO user (Fullname, Address, Dob, Gender, Password, Email, /*Profile_picture,*/ User_type) " +
                "VALUES (?, ?, ?, ?, ?, ?/*, ?*/, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFullname());
            pstmt.setString(2, user.getAddress());
            pstmt.setDate(3, Date.valueOf(user.getDob()));
            pstmt.setString(4, user.getGender());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getEmail());
           /* pstmt.setString(7, user.getProfilePicture());*/
            pstmt.setString(8, user.getUserType());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDisplayUser(DisplayUser user) {
        String query = "UPDATE user SET Fullname=?, Address=?, Dob=?, Gender=?, " +
                "Password=?, Email=?, Profile_picture=?, User_type=? WHERE User_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFullname());
            pstmt.setString(2, user.getAddress());
            pstmt.setDate(3, Date.valueOf(user.getDob()));
            pstmt.setString(4, user.getGender());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getProfilePicture());
            pstmt.setString(8, user.getUserType());
            pstmt.setInt(9, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDisplayUser(int userId) {
        String query = "DELETE FROM user WHERE User_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<DisplayUser> searchDisplayUsers(String keyword) {
        List<DisplayUser> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE Fullname LIKE ? OR Email LIKE ? OR User_type LIKE ? ORDER BY User_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(extractDisplayUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public int getDisplayUserCountByType(String userType) {
        String query = "SELECT COUNT(*) FROM user WHERE User_type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private DisplayUser extractDisplayUserFromResultSet(ResultSet rs) throws SQLException {
        return new DisplayUser(
                rs.getInt("User_id"),
                rs.getString("Fullname"),
                rs.getString("Address"),
                rs.getDate("Dob").toLocalDate(),
                rs.getString("Gender"),
                rs.getString("Password"),
                rs.getString("Email"),
                rs.getString("Profile_picture") != null ? rs.getString("Profile_picture") : "/profile_pics/default_pic.jpg",
                rs.getString("User_type")
        );
    }
}