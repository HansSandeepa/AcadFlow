package acadflow.DAO;

import acadflow.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {


    public static class StudentEntry {
        private final String stuId;
        private final String fullName;

        public StudentEntry(String stuId, String fullName) {
            this.stuId     = stuId;
            this.fullName  = fullName;
        }

        public String getStuId()    { return stuId; }
        public String getFullName() { return fullName; }

        @Override
        public String toString() { return stuId + " – " + fullName; }
    }

    public static class CourseEntry {
        private final String courseId;
        private final String name;
        private final String type;  // "T", "P", "Both"

        public CourseEntry(String courseId, String name, String type) {
            this.courseId = courseId;
            this.name     = name;
            this.type     = type;
        }

        public String getCourseId() { return courseId; }
        public String getName()     { return name; }
        public String getType()     { return type; }

        @Override
        public String toString() { return courseId + " – " + name; }
    }

    // Helper queries for populating combo-boxes
    public List<StudentEntry> getStudentsByDepartment(String department) {
        List<StudentEntry> list = new ArrayList<>();
        String sql = "SELECT u.Stu_id, usr.Fullname " +
                "FROM undergraduate u " +
                "JOIN user usr ON u.User_id = usr.User_id " +
                "WHERE u.Department = ? " +
                "ORDER BY u.Stu_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new StudentEntry(rs.getString("Stu_id"),
                        rs.getString("Fullname")));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getStudentsByDepartment error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns courses whose department matches the given department.
     * Used to populate the course combo-box so a Technical Officer can only
     * assign attendance for courses belonging to their department.
     */
    public List<CourseEntry> getCoursesByDepartment(String department) {
        List<CourseEntry> list = new ArrayList<>();
        String sql = "SELECT Course_id, Name, Type FROM course " +
                "WHERE department = ? ORDER BY Course_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CourseEntry(rs.getString("Course_id"),
                        rs.getString("Name"),
                        rs.getString("Type")));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getCoursesByDepartment error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns every undergraduate in the system with their full name.
     * Kept for completeness; prefer getStudentsByDepartment() in the TO dashboard.
     */
    public List<StudentEntry> getAllStudents() {
        List<StudentEntry> list = new ArrayList<>();
        String sql = "SELECT u.Stu_id, usr.Fullname " +
                "FROM undergraduate u " +
                "JOIN user usr ON u.User_id = usr.User_id " +
                "ORDER BY u.Stu_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new StudentEntry(rs.getString("Stu_id"),
                        rs.getString("Fullname")));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAllStudents error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns every course in the system.
     * Used to populate the "Select Course" combo-box.
     */
    public List<CourseEntry> getAllCourses() {
        List<CourseEntry> list = new ArrayList<>();
        String sql = "SELECT Course_id, Name, Type FROM course ORDER BY Course_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new CourseEntry(rs.getString("Course_id"),
                        rs.getString("Name"),
                        rs.getString("Type")));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAllCourses error: " + e.getMessage());
        }
        return list;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Core CRUD operations
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Inserts a new attendance row into the database.
     */
    public boolean addAttendance(AttendanceRecord record) {
        String sql = "INSERT INTO attendance (Session, Session_type, Status, Date, Course_id, Stu_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, record.getSession());
            ps.setString(2, record.getSessionType());
            ps.setString(3, record.getStatus());
            ps.setDate(4, Date.valueOf(record.getDate()));
            ps.setString(5, record.getCourseId());
            ps.setString(6, record.getStuId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    record.setAttendanceId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] addAttendance error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing attendance record (identified by its Attendance_id).
     * All editable fields (Session, Session_type, Status, Date, Course_id, Stu_id)
     * are updated.
     */
    public boolean updateAttendance(AttendanceRecord record) {
        String sql = "UPDATE attendance " +
                "SET Session = ?, Session_type = ?, Status = ?, Date = ?, Course_id = ?, Stu_id = ? " +
                "WHERE Attendance_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, record.getSession());
            ps.setString(2, record.getSessionType());
            ps.setString(3, record.getStatus());
            ps.setDate(4, Date.valueOf(record.getDate()));
            ps.setString(5, record.getCourseId());
            ps.setString(6, record.getStuId());
            ps.setInt(7, record.getAttendanceId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] updateAttendance error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes an attendance record by its primary key.
     * Note: any linked medical records referencing this Attendance_id will be
     * cascade-deleted automatically (FK constraint in the schema).
     */
    public boolean deleteAttendance(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE Attendance_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attendanceId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] deleteAttendance error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Query operations used by the attendance table view
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns all attendance records for a specific undergraduate,
     * joined with course name and student full name for display.
     */
    public List<AttendanceRecord> getAttendanceByStudent(String stuId) {
        return queryRecords(
                "SELECT a.Attendance_id, a.Session, a.Session_type, a.Status, " +
                        "       a.Date, a.Course_id, a.Stu_id, " +
                        "       usr.Fullname AS StudentName, c.Name AS CourseName " +
                        "FROM attendance a " +
                        "JOIN undergraduate u   ON a.Stu_id    = u.Stu_id " +
                        "JOIN user usr          ON u.User_id   = usr.User_id " +
                        "JOIN course c          ON a.Course_id = c.Course_id " +
                        "WHERE a.Stu_id = ? " +
                        "ORDER BY a.Date DESC, a.Attendance_id DESC",
                stuId
        );
    }

    /**
     * Returns all attendance records whose student AND course both belong to the
     * given department. This is the main table-load query for a Technical Officer —
     * they only see records they are responsible for.
     */
    public List<AttendanceRecord> getAttendanceByDepartment(String department) {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql =
                "SELECT a.Attendance_id, a.Session, a.Session_type, a.Status, " +
                        "       a.Date, a.Course_id, a.Stu_id, " +
                        "       usr.Fullname AS StudentName, c.Name AS CourseName " +
                        "FROM attendance a " +
                        "JOIN undergraduate u   ON a.Stu_id    = u.Stu_id " +
                        "JOIN user usr          ON u.User_id   = usr.User_id " +
                        "JOIN course c          ON a.Course_id = c.Course_id " +
                        "WHERE u.Department = ? AND c.department = ? " +
                        "ORDER BY a.Date DESC, a.Attendance_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ps.setString(2, department);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAttendanceByDepartment error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns attendance records for one specific student, scoped to the
     * department the Technical Officer belongs to (filters on course.department
     * so cross-department records are never surfaced).
     */
    public List<AttendanceRecord> getAttendanceByStudentInDepartment(String stuId, String department) {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql =
                "SELECT a.Attendance_id, a.Session, a.Session_type, a.Status, " +
                        "       a.Date, a.Course_id, a.Stu_id, " +
                        "       usr.Fullname AS StudentName, c.Name AS CourseName " +
                        "FROM attendance a " +
                        "JOIN undergraduate u   ON a.Stu_id    = u.Stu_id " +
                        "JOIN user usr          ON u.User_id   = usr.User_id " +
                        "JOIN course c          ON a.Course_id = c.Course_id " +
                        "WHERE a.Stu_id = ? AND c.department = ? " +
                        "ORDER BY a.Date DESC, a.Attendance_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stuId);
            ps.setString(2, department);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAttendanceByStudentInDepartment error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns all attendance records across every student,
     * joined with name and course name — used when no filter is selected.
     */
    public List<AttendanceRecord> getAllAttendanceRecords() {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql =
                "SELECT a.Attendance_id, a.Session, a.Session_type, a.Status, " +
                        "       a.Date, a.Course_id, a.Stu_id, " +
                        "       usr.Fullname AS StudentName, c.Name AS CourseName " +
                        "FROM attendance a " +
                        "JOIN undergraduate u   ON a.Stu_id    = u.Stu_id " +
                        "JOIN user usr          ON u.User_id   = usr.User_id " +
                        "JOIN course c          ON a.Course_id = c.Course_id " +
                        "ORDER BY a.Date DESC, a.Attendance_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAllAttendanceRecords error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns a single attendance record by its primary key.
     * Returns null if not found.
     */
    public AttendanceRecord getAttendanceById(int attendanceId) {
        String sql =
                "SELECT a.Attendance_id, a.Session, a.Session_type, a.Status, " +
                        "       a.Date, a.Course_id, a.Stu_id, " +
                        "       usr.Fullname AS StudentName, c.Name AS CourseName " +
                        "FROM attendance a " +
                        "JOIN undergraduate u   ON a.Stu_id    = u.Stu_id " +
                        "JOIN user usr          ON u.User_id   = usr.User_id " +
                        "JOIN course c          ON a.Course_id = c.Course_id " +
                        "WHERE a.Attendance_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attendanceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractRecord(rs);
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAttendanceById error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks whether an attendance record already exists for the given
     * combination of student + course + session + session-type.
     * Prevents duplicate entries.
     */
    public boolean recordExists(String stuId, String courseId, String session, String sessionType) {
        String sql = "SELECT COUNT(*) FROM attendance " +
                "WHERE Stu_id = ? AND Course_id = ? AND Session = ? AND Session_type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stuId);
            ps.setString(2, courseId);
            ps.setString(3, session);
            ps.setString(4, sessionType);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] recordExists error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Same as recordExists but excludes a specific attendanceId — used when
     * checking for duplicates while *editing* an existing record.
     */
    public boolean recordExistsExcluding(String stuId, String courseId, String session,
                                         String sessionType, int excludeId) {
        String sql = "SELECT COUNT(*) FROM attendance " +
                "WHERE Stu_id = ? AND Course_id = ? AND Session = ? " +
                "  AND Session_type = ? AND Attendance_id <> ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stuId);
            ps.setString(2, courseId);
            ps.setString(3, session);
            ps.setString(4, sessionType);
            ps.setInt(5, excludeId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] recordExistsExcluding error: " + e.getMessage());
        }
        return false;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Undergraduate self-view queries
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns all attendance rows for the given student, enriched with any
     * linked medical record data (joined via medical.Attendance_id).
     */
    public List<AttendanceViewRecord> getAttendanceViewRecords(String stuId,
                                                               String courseId,
                                                               String sessionTypeFilter) {
        List<AttendanceViewRecord> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT a.Session, a.Session_type, a.Status, a.Date, " +
                        "       a.Course_id, c.Name AS CourseName, " +
                        "       CASE WHEN m.Medical_id IS NOT NULL THEN 1 ELSE 0 END AS HasMedical, " +
                        "       COALESCE(m.Approval, '-') AS MedicalApproval " +
                        "FROM attendance a " +
                        "JOIN course c ON a.Course_id = c.Course_id " +
                        "LEFT JOIN medical m ON m.Attendance_id = a.Attendance_id " +
                        "WHERE a.Stu_id = ? "
        );

        if (courseId != null && !courseId.isEmpty()) {
            sql.append("AND a.Course_id = ? ");
        }
        if (sessionTypeFilter != null && !sessionTypeFilter.isEmpty()) {
            sql.append("AND a.Session_type = ? ");
        }
        sql.append("ORDER BY a.Date DESC, a.Session");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setString(idx++, stuId);
            if (courseId != null && !courseId.isEmpty())       ps.setString(idx++, courseId);
            if (sessionTypeFilter != null && !sessionTypeFilter.isEmpty()) ps.setString(idx++, sessionTypeFilter);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new AttendanceViewRecord(
                        rs.getString("Course_id"),
                        rs.getString("CourseName"),
                        rs.getString("Session"),
                        rs.getString("Session_type"),
                        rs.getDate("Date").toString(),
                        rs.getString("Status"),
                        rs.getInt("HasMedical") == 1,
                        rs.getString("MedicalApproval")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAttendanceViewRecords error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns every course that the given student has at least one
     * attendance record for. Used to populate the Course filter combo-box.
     */
    public List<CourseEntry> getCoursesForStudent(String stuId) {
        List<CourseEntry> list = new ArrayList<>();
        String sql =
                "SELECT DISTINCT c.Course_id, c.Name, c.Type " +
                        "FROM attendance a " +
                        "JOIN course c ON a.Course_id = c.Course_id " +
                        "WHERE a.Stu_id = ? " +
                        "ORDER BY c.Course_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stuId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CourseEntry(
                        rs.getString("Course_id"),
                        rs.getString("Name"),
                        rs.getString("Type")));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getCoursesForStudent error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Attendance summary for one student in one course.
     */
    public int[] getAttendanceSummary(String stuId, String courseId, String sessionTypeFilter) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total, " +
                        "       SUM(CASE WHEN a.Status = 'present' THEN 1 ELSE 0 END) AS present_count, " +
                        "       SUM(CASE WHEN m.Medical_id IS NOT NULL THEN 1 ELSE 0 END) AS medical_count " +
                        "FROM attendance a " +
                        "LEFT JOIN medical m ON m.Attendance_id = a.Attendance_id " +
                        "WHERE a.Stu_id = ? AND a.Course_id = ? "
        );
        if (sessionTypeFilter != null && !sessionTypeFilter.isEmpty()) {
            sql.append("AND a.Session_type = ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setString(1, stuId);
            ps.setString(2, courseId);
            if (sessionTypeFilter != null && !sessionTypeFilter.isEmpty()) {
                ps.setString(3, sessionTypeFilter);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new int[]{
                        rs.getInt("present_count"),
                        rs.getInt("total"),
                        rs.getInt("medical_count")
                };
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getAttendanceSummary error: " + e.getMessage());
        }
        return new int[]{0, 0, 0};
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────────────────────────────────

    /** Runs a parameterized query with a single String parameter. */
    private List<AttendanceRecord> queryRecords(String sql, String param) {
        List<AttendanceRecord> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] queryRecords error: " + e.getMessage());
        }
        return list;
    }

    /** Maps the current ResultSet row to an AttendanceRecord object. */
    private AttendanceRecord extractRecord(ResultSet rs) throws SQLException {
        return new AttendanceRecord(
                rs.getInt("Attendance_id"),
                rs.getString("Session"),
                rs.getString("Session_type"),
                rs.getString("Status"),
                rs.getDate("Date").toString(),       // converts java.sql.Date → "yyyy-MM-dd"
                rs.getString("Course_id"),
                rs.getString("Stu_id"),
                rs.getString("StudentName"),
                rs.getString("CourseName")
        );
    }
}