package acadflow.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import acadflow.models.ExamMark;
import acadflow.util.DBConnection;

/**
 * Data Access Object for the exam_marks table.
 *
 * ABSTRACTION   : callers ask for "marks for student X" or
 *                 "upload mark for course Y" — all SQL is hidden.
 * ENCAPSULATION : extractFromResultSet() is private; internal
 *                 state is not exposed.
 */
public class ExamMarkDAO {

    // ── Undergraduate: read own marks ─────────────────────────────────────────

    /**
     * Returns all exam marks for a given student, joined with course name
     * and credits so the UI can display them without extra queries.
     *
     * ABSTRACTION: caller passes stuId, gets a ready-to-display list back.
     */
    public List<ExamMark> getMarksByStudent(String stuId) {
        List<ExamMark> list = new ArrayList<>();
        // Join with course to get name and credits for GPA calculation
        String query =
            "SELECT em.*, c.Name AS CourseName, c.Credit " +
            "FROM exam_marks em " +
            "JOIN course c ON em.Course_id = c.Course_id " +
            "WHERE em.Stu_id = ? " +
            "ORDER BY c.Name, em.Assessment_type";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, stuId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractFromResultSet(rs)); // ENCAPSULATION: mapping hidden
            }
        } catch (SQLException e) {
            System.err.println("ExamMarkDAO.getMarksByStudent: " + e.getMessage());
        }
        return list;
    }

    // ── Lecturer: read marks for a course they teach ──────────────────────────

    /**
     * Returns all marks uploaded for a specific course by this lecturer,
     * joined with student id for display.
     *
     * ABSTRACTION: caller passes courseId + lecturerId, gets list back.
     */
    public List<ExamMark> getMarksByCourseAndLecturer(String courseId, String lecturerId) {
        List<ExamMark> list = new ArrayList<>();
        String query =
            "SELECT em.*, c.Name AS CourseName, c.Credit " +
            "FROM exam_marks em " +
            "JOIN course c ON em.Course_id = c.Course_id " +
            "WHERE em.Course_id = ? AND em.Lecturer_id = ? " +
            "ORDER BY em.Stu_id, em.Assessment_type";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, courseId);
            ps.setString(2, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractFromResultSet(rs)); // ENCAPSULATION: mapping hidden
            }
        } catch (SQLException e) {
            System.err.println("ExamMarkDAO.getMarksByCourseAndLecturer: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns all marks for all students in a course (any lecturer).
     * Used by lecturer to view undergraduate details.
     */
    public List<ExamMark> getMarksByCourse(String courseId) {
        List<ExamMark> list = new ArrayList<>();
        String query =
            "SELECT em.*, c.Name AS CourseName, c.Credit " +
            "FROM exam_marks em " +
            "JOIN course c ON em.Course_id = c.Course_id " +
            "WHERE em.Course_id = ? " +
            "ORDER BY em.Stu_id, em.Assessment_type";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractFromResultSet(rs)); // ENCAPSULATION: mapping hidden
            }
        } catch (SQLException e) {
            System.err.println("ExamMarkDAO.getMarksByCourse: " + e.getMessage());
        }
        return list;
    }

    // ── Lecturer: upload / update marks ──────────────────────────────────────

    /**
     * Inserts a new mark record.
     * Grade is auto-calculated from the mark value.
     *
     * ABSTRACTION: caller passes an ExamMark object — INSERT SQL is hidden.
     * ENCAPSULATION: ExamMark fields accessed through getters (they are private).
     */
    public boolean addMark(ExamMark em) {
        // Auto-calculate grade before inserting
        String grade = ExamMark.calculateGrade(em.getMark());
        em.setGrade(grade);

        String query =
            "INSERT INTO exam_marks (Grade, Session_type, Assessment_type, Mark, Stu_id, Lecturer_id, Course_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, em.getGrade());          // ENCAPSULATION: getter used
            ps.setString(2, em.getSessionType());    // ENCAPSULATION: getter used
            ps.setString(3, em.getAssessmentType()); // ENCAPSULATION: getter used
            ps.setInt(4,    em.getMark());            // ENCAPSULATION: getter used
            ps.setString(5, em.getStuId());          // ENCAPSULATION: getter used
            ps.setString(6, em.getLecturerId());     // ENCAPSULATION: getter used
            ps.setString(7, em.getCourseId());       // ENCAPSULATION: getter used

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) em.setExamId(keys.getInt(1)); // ENCAPSULATION: setter used
                return true;
            }
        } catch (SQLException e) {
            System.err.println("ExamMarkDAO.addMark: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates an existing mark record.
     * Grade is recalculated automatically.
     */
    public boolean updateMark(ExamMark em) {
        String grade = ExamMark.calculateGrade(em.getMark());
        em.setGrade(grade);

        String query =
            "UPDATE exam_marks SET Grade=?, Session_type=?, Assessment_type=?, Mark=? " +
            "WHERE Exam_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, em.getGrade());
            ps.setString(2, em.getSessionType());
            ps.setString(3, em.getAssessmentType());
            ps.setInt(4,    em.getMark());
            ps.setInt(5,    em.getExamId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ExamMarkDAO.updateMark: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a mark record by its primary key.
     */
    public boolean deleteMark(int examId) {
        String query = "DELETE FROM exam_marks WHERE Exam_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ExamMarkDAO.deleteMark: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks whether a mark already exists for a given student + course +
     * assessment type combination (prevents duplicate entries).
     */
    public boolean markExists(String stuId, String courseId, String assessmentType) {
        String query =
            "SELECT COUNT(*) FROM exam_marks " +
            "WHERE Stu_id=? AND Course_id=? AND Assessment_type=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, stuId);
            ps.setString(2, courseId);
            ps.setString(3, assessmentType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("ExamMarkDAO.markExists: " + e.getMessage());
        }
        return false;
    }

    // ── ENCAPSULATION: private mapping helper ─────────────────────────────────
    // Converts a raw ResultSet row into an ExamMark object.
    // PRIVATE — no class outside ExamMarkDAO should call this.
    private ExamMark extractFromResultSet(ResultSet rs) throws SQLException {
        ExamMark em = new ExamMark(
                rs.getInt("Exam_id"),
                rs.getString("Grade"),
                rs.getString("Session_type"),
                rs.getString("Assessment_type"),
                rs.getInt("Mark"),
                rs.getString("Stu_id"),
                rs.getString("Lecturer_id"),
                rs.getString("Course_id")
        );
        // Set joined fields if present in the result set
        try { em.setCourseName(rs.getString("CourseName")); } catch (SQLException ignored) {}
        try { em.setCredits(rs.getInt("Credit")); }           catch (SQLException ignored) {}
        return em;
    }
}
