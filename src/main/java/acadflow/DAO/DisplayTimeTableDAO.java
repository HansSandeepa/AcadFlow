package acadflow.DAO;

import acadflow.models.DisplayTimeTable;
import acadflow.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayTimeTableDAO {

    public List<DisplayTimeTable> getTimetableForUndergraduate(String dept, int level, int semester) {
        // Query to get timetable entries for the student's department
        // The timetable is organized by department, and students take courses based on their level/semester
        String sql = "SELECT * FROM time_table WHERE Department = ? AND Level_and_Semester = ?";
        String levelAndSemester = "Level " + level + " Semester " + semester;  // Construct the level/semester string for the query
        List<DisplayTimeTable> timetableList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dept);
            stmt.setString(2, levelAndSemester);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DisplayTimeTable tt = new DisplayTimeTable(
                        rs.getString("Timetable_id"),
                        rs.getString("Day"),
                        rs.getString("Time"),
                        "Level " + level + " Semester " + semester,  // Construct the level/semester info
                        rs.getString("Session_type"),
                        rs.getString("Department"),
                        rs.getString("Course_id"),
                        rs.getString("Admin_id")
                );
                timetableList.add(tt);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to fetch timetable for undergraduate - " + e.getMessage());
            e.printStackTrace();
        }

        return timetableList;
    }

    public List<DisplayTimeTable> getTimetableForDepartment(String dept) {
        // Query to get timetable entries for a specific department
        String sql = "SELECT * FROM time_table WHERE Department = ?";

        List<DisplayTimeTable> timetableList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dept);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DisplayTimeTable tt = new DisplayTimeTable(
                        rs.getString("Timetable_id"),
                        rs.getString("Day"),
                        rs.getString("Time"),
                        "Department: " + dept,  // Show department info
                        rs.getString("Session_type"),
                        rs.getString("Department"),
                        rs.getString("Course_id"),
                        rs.getString("Admin_id")
                );
                timetableList.add(tt);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to fetch timetable for department - " + e.getMessage());
            e.printStackTrace();
        }

        return timetableList;
    }
}
