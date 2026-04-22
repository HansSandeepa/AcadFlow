package acadflow.DAO;

import acadflow.models.Notice;
import acadflow.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    //  all notices
    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        String query = "SELECT * FROM notice ORDER BY Date DESC, Notice_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                notices.add(extractNoticeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all notices: " + e.getMessage());
            e.printStackTrace();
        }
        return notices;
    }

    //  notice ID
    public Notice getNoticeById(int noticeId) {
        String query = "SELECT * FROM notice WHERE Notice_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, noticeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractNoticeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting notice by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Add  notice
    public boolean addNotice(Notice notice) {
        String query = "INSERT INTO notice (Title, Content, Date, Admin_id, Audience, IsImportant) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getContent());
            pstmt.setDate(3, Date.valueOf(notice.getDate()));
            pstmt.setString(4, notice.getAdminId());
            pstmt.setString(5, notice.getAudience());
            pstmt.setBoolean(6, notice.isImportant());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    notice.setNoticeId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding notice: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Update notice
    public boolean updateNotice(Notice notice) {
        String query = "UPDATE notice SET Title=?, Content=?, Audience=?, IsImportant=? WHERE Notice_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getContent());
            pstmt.setString(3, notice.getAudience());
            pstmt.setBoolean(4, notice.isImportant());
            pstmt.setInt(5, notice.getNoticeId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating notice: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Delete notice
    public boolean deleteNotice(int noticeId) {
        String query = "DELETE FROM notice WHERE Notice_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, noticeId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting notice: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // notices audience
    public List<Notice> getNoticesByAudience(String audience) {
        List<Notice> notices = new ArrayList<>();
        String query = "SELECT * FROM notice WHERE Audience = ? OR Audience = 'All' ORDER BY Date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, audience);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notices.add(extractNoticeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting notices by audience: " + e.getMessage());
            e.printStackTrace();
        }
        return notices;
    }

    //  important notices
    public List<Notice> getImportantNotices() {
        List<Notice> notices = new ArrayList<>();
        String query = "SELECT * FROM notice WHERE IsImportant = true ORDER BY Date DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                notices.add(extractNoticeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting important notices: " + e.getMessage());
            e.printStackTrace();
        }
        return notices;
    }

    // Search notices  title
    public List<Notice> searchNoticesByTitle(String keyword) {
        List<Notice> notices = new ArrayList<>();
        String query = "SELECT * FROM notice WHERE Title LIKE ? ORDER BY Date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notices.add(extractNoticeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching notices: " + e.getMessage());
            e.printStackTrace();
        }
        return notices;
    }

    // Notice  ResultSet
    private Notice extractNoticeFromResultSet(ResultSet rs) throws SQLException {
        return new Notice(
                rs.getInt("Notice_id"),
                rs.getString("Title"),
                rs.getString("Content"),
                rs.getDate("Date").toLocalDate(),
                rs.getString("Admin_id"),
                rs.getString("Audience") != null ? rs.getString("Audience") : "All",
                rs.getBoolean("IsImportant")
        );
    }
}