package acadflow.DAO;

import acadflow.models.getterSetter.MedicalRecord;
import acadflow.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {


    public List<MedicalRecord> getStudentAbsentRecords(String stuId) {
        List<MedicalRecord> records = new ArrayList<>();
        String query = """
        SELECT a.Attendance_id, a.Date, a.Session_type, a.Course_id, c.Name as Course_Name, 
               u.Fullname as Student_Name
        FROM attendance a
        JOIN course c ON a.Course_id = c.Course_id
        JOIN undergraduate ug ON a.Stu_id = ug.Stu_id
        JOIN user u ON ug.User_id = u.User_id
        WHERE a.Stu_id = ? AND a.Status = 'absent'
        AND a.Attendance_id NOT IN (SELECT Attendance_id FROM medical)
        ORDER BY a.Date DESC
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, stuId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                records.add(new MedicalRecord(
                        0,  // medicalId not applicable here
                        rs.getString("Date"),
                        rs.getString("Session_type").equals("T") ? "Theory" : "Practical",
                        rs.getString("Course_id"),
                        rs.getString("Course_Name"),
                        "Pending",
                        rs.getInt("Attendance_id"),
                        stuId,
                        rs.getString("Student_Name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    // Submit new medical record
    public boolean submitMedical(int attendanceId, String session, String date) {
        String query = "INSERT INTO medical (Session, Date, Approval, Attendance_id) VALUES (?, ?, 'no', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, session);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setInt(3, attendanceId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get student's existing medical records
    public List<MedicalRecord> getStudentMedicalRecords(String stuId) {
        List<MedicalRecord> records = new ArrayList<>();
        String query = """
        SELECT m.Medical_id, m.Session, m.Date, m.Approval, 
               a.Session_type, a.Course_id, c.Name as Course_Name, a.Attendance_id,
               u.Fullname as Student_Name
        FROM medical m
        JOIN attendance a ON m.Attendance_id = a.Attendance_id
        JOIN course c ON a.Course_id = c.Course_id
        JOIN undergraduate ug ON a.Stu_id = ug.Stu_id
        JOIN user u ON ug.User_id = u.User_id
        WHERE a.Stu_id = ?
        ORDER BY m.Date DESC
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, stuId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                records.add(new MedicalRecord(
                        rs.getInt("Medical_id"),
                        rs.getString("Date"),
                        rs.getString("Session_type").equals("T") ? "Theory" : "Practical",
                        rs.getString("Course_id"),
                        rs.getString("Course_Name"),
                        rs.getString("Approval").equals("yes") ? "Approved" : "Pending",
                        rs.getInt("Attendance_id"),
                        stuId,                              // studentId - use the parameter
                        rs.getString("Student_Name")        // studentName - from query
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<MedicalRecord> getMedicalRecordsByDepartment(String department) {
        List<MedicalRecord> records = new ArrayList<>();
        String query = """
    SELECT m.Medical_id, m.Session, m.Date, m.Approval, 
           a.Session_type, a.Course_id, c.Name as Course_Name, 
           a.Attendance_id, a.Stu_id, u.Fullname as Student_Name
    FROM medical m
    JOIN attendance a ON m.Attendance_id = a.Attendance_id
    JOIN course c ON a.Course_id = c.Course_id
    JOIN undergraduate ug ON a.Stu_id = ug.Stu_id
    JOIN user u ON u.User_id = ug.User_id  -- Changed: use ug.User_id instead of User_id
    WHERE ug.Department = ?
    ORDER BY m.Date DESC
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String approval = rs.getString("Approval");
                String status = approval.equals("yes") ? "Approved" : "Pending";

                records.add(new MedicalRecord(
                        rs.getInt("Medical_id"),
                        rs.getString("Date"),
                        rs.getString("Session_type").equals("T") ? "Theory" : "Practical",
                        rs.getString("Course_id"),
                        rs.getString("Course_Name"),
                        status,
                        rs.getInt("Attendance_id"),
                        rs.getString("Stu_id"),
                        rs.getString("Student_Name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }


     // Search medical records by student ID or name within department

    public List<MedicalRecord> searchMedicalRecords(String keyword, String department) {
        List<MedicalRecord> records = new ArrayList<>();
        String query = """
        SELECT m.Medical_id, m.Session, m.Date, m.Approval, 
               a.Session_type, a.Course_id, c.Name as Course_Name, 
               a.Attendance_id, a.Stu_id, u.Fullname as Student_Name
        FROM medical m
        JOIN attendance a ON m.Attendance_id = a.Attendance_id
        JOIN course c ON a.Course_id = c.Course_id
        JOIN undergraduate ug ON a.Stu_id = ug.Stu_id
        JOIN user u ON ug.User_id = u.User_id
        WHERE ug.Department = ?
        AND (a.Stu_id LIKE ? OR u.Fullname LIKE ?)
        ORDER BY m.Date DESC
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, department);
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String approval = rs.getString("Approval");
                String status = approval.equals("yes") ? "Approved" : "Pending";

                records.add(new MedicalRecord(
                        rs.getInt("Medical_id"),
                        rs.getString("Date"),
                        rs.getString("Session_type").equals("T") ? "Theory" : "Practical",
                        rs.getString("Course_id"),
                        rs.getString("Course_Name"),
                        status,
                        rs.getInt("Attendance_id"),
                        rs.getString("Stu_id"),           // Add this (8th parameter)
                        rs.getString("Student_Name")      // Add this (9th parameter)
                ));}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }


     // Update medical approval status

    public boolean updateMedicalApproval(int medicalId, boolean approve) {
        String query = "UPDATE medical SET Approval = ? WHERE Medical_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, approve ? "yes" : "no");
            stmt.setInt(2, medicalId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}