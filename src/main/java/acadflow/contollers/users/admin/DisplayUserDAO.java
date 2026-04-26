package acadflow.contollers.users.admin;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import acadflow.util.DBConnection;



// INHERITANCE: DisplayUserDAO inherits the BaseDAO<DisplayUser> contract
public class DisplayUserDAO implements BaseDAO<DisplayUser> {

    // ── INHERITANCE + POLYMORPHISM: implementing BaseDAO contract ────────────
    // getAll() is declared in BaseDAO. This is the concrete implementation.
    // ABSTRACTION: caller just asks for "all users" — SQL is hidden.
    @Override
    public List<DisplayUser> getAll() {
        return getAllDisplayUsers(); // delegates to the existing named method
    }

    // ── INHERITANCE + POLYMORPHISM: implementing BaseDAO contract ────────────
    @Override
    public DisplayUser getById(int id) {
        return getDisplayUserById(id);
    }

    // ── INHERITANCE + POLYMORPHISM: implementing BaseDAO contract ────────────
    @Override
    public boolean add(DisplayUser entity) {
        return addDisplayUser(entity);
    }

    // ── INHERITANCE + POLYMORPHISM: implementing BaseDAO contract ────────────
    @Override
    public boolean update(DisplayUser entity) {
        return updateDisplayUser(entity);
    }

    // ── INHERITANCE + POLYMORPHISM: implementing BaseDAO contract ────────────
    @Override
    public boolean delete(int id) {
        return deleteDisplayUser(id);
    }

    // ── Existing named methods preserved exactly — no code removed ────────────

    // ABSTRACTION: caller asks for all users — SQL ORDER BY logic is hidden
    public List<DisplayUser> getAllDisplayUsers() {
        List<DisplayUser> users = new ArrayList<>();
        String query = "SELECT * FROM user ORDER BY User_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(extractDisplayUserFromResultSet(rs)); // ENCAPSULATION: mapping hidden
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // ABSTRACTION: caller passes a type string — SQL filtering is hidden
    public List<DisplayUser> getDisplayUsersByType(String userType) {
        List<DisplayUser> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE User_type = ? ORDER BY User_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(extractDisplayUserFromResultSet(rs)); // ENCAPSULATION: mapping hidden
            }
        } catch (SQLException e) {
            System.err.println("Error getting users by type: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // ABSTRACTION: caller passes an id — SQL SELECT is hidden
    public DisplayUser getDisplayUserById(int userId) {
        String query = "SELECT * FROM user WHERE User_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractDisplayUserFromResultSet(rs); // ENCAPSULATION: mapping hidden
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ENCAPSULATION: DisplayUser fields accessed only through getters (they are private)
    public boolean addDisplayUser(DisplayUser user) {
        String query = "INSERT INTO user (Fullname, Address, Dob, Gender, Password, Email, User_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFullname());   // ENCAPSULATION: getter used
            pstmt.setString(2, user.getAddress());    // ENCAPSULATION: getter used
            pstmt.setDate(3, Date.valueOf(user.getDob()));
            pstmt.setString(4, user.getGender());     // ENCAPSULATION: getter used
            pstmt.setString(5, user.getPassword());   // ENCAPSULATION: getter used
            pstmt.setString(6, user.getEmail());      // ENCAPSULATION: getter used
            pstmt.setString(7, user.getUserType());   // ENCAPSULATION: getter used

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1)); // ENCAPSULATION: setter used
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ENCAPSULATION: DisplayUser fields accessed only through getters/setters
    public boolean updateDisplayUser(DisplayUser user) {
        String query = "UPDATE user SET Fullname=?, Address=?, Dob=?, Gender=?, " +
                "Password=?, Email=?, User_type=? WHERE User_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFullname());   // ENCAPSULATION: getter used
            pstmt.setString(2, user.getAddress());    // ENCAPSULATION: getter used
            pstmt.setDate(3, Date.valueOf(user.getDob()));
            pstmt.setString(4, user.getGender());     // ENCAPSULATION: getter used
            pstmt.setString(5, user.getPassword());   // ENCAPSULATION: getter used
            pstmt.setString(6, user.getEmail());      // ENCAPSULATION: getter used
            pstmt.setString(7, user.getUserType());   // ENCAPSULATION: getter used
            pstmt.setInt(8, user.getUserId());        // ENCAPSULATION: getter used

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
                users.add(extractDisplayUserFromResultSet(rs)); // ENCAPSULATION: mapping hidden
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

    // ── ENCAPSULATION: private helper — internal detail, not exposed ──────────
    // Converts a raw database row (ResultSet) into a DisplayUser object.
    // PRIVATE because no class outside DisplayUserDAO should ever call this.
    // All public methods above reuse it — avoids code duplication.
    private DisplayUser extractDisplayUserFromResultSet(ResultSet rs) throws SQLException {
        return new DisplayUser(
                rs.getInt("User_id"),
                rs.getString("Fullname"),
                rs.getString("Address"),
                rs.getDate("Dob").toLocalDate(),
                rs.getString("Gender"),
                rs.getString("Password"),
                rs.getString("Email"),
                rs.getString("User_type")
        );
    }
}
