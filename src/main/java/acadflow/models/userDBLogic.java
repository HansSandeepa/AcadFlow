package acadflow.models;

import acadflow.models.users.NoticeData;
import acadflow.util.PasswordHash;

import java.sql.*;

public class userDBLogic {

        //CHECKIN EMAIL FOR DATA INSERT
        public boolean isEmailExists(Connection conn, String email) throws SQLException {
            String query = "SELECT 1 FROM `user` WHERE Email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            rs.close();
            stmt.close();
            return exists;
        }

        //CHECK IS COURSES EXISTS
        public boolean isCourseExists(Connection conn, String Title) throws SQLException {
            String query = "SELECT 2 FROM `course` WHERE Course_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, Title);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            rs.close();
            stmt.close();
            return exists;
        }

    //CHECK NOTICE IS EXISTS
    public  boolean isNoticeExists(Connection conn, String title) throws SQLException {
        String query = "SELECT 2 FROM `notice` WHERE Title = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, title);

        ResultSet rs = stmt.executeQuery();
        boolean exists = rs.next();

        rs.close();
        stmt.close();
        return exists;
    }


        //INSERT USERS AND TECHNICAL OFFICERS DATA
        public void insertUserAndOfficer(Connection conn, UserAndOfficerData u, String address, PasswordHash passwordHash) throws SQLException {

            String insertUser = "INSERT INTO `user` (Fullname,Address,Dob,Gender,Password,Email) VALUES (?,?,?,?,?,?)";
            String insertOfficer = "INSERT INTO tec_officer (T_officer_id, Hire_date, User_id, Department) VALUES (?, ?, ?, ?)";

            PreparedStatement userStmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, u.name);
            userStmt.setString(2, address);
            userStmt.setDate(3, Date.valueOf(u.dob));
            userStmt.setString(4, u.gender);
            userStmt.setString(5, passwordHash.createHash());
            userStmt.setString(6, u.email);
            userStmt.executeUpdate();

            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);

                PreparedStatement techStmt = conn.prepareStatement(insertOfficer);
                techStmt.setString(1, u.officerId);
                techStmt.setDate(2, Date.valueOf(u.hireDate));
                techStmt.setInt(3, userId);
                techStmt.setString(4, u.dept);
                techStmt.executeUpdate();
                techStmt.close();
            }

            rs.close();
            userStmt.close();
        }

        public void insertUserAndLecturer(Connection conn, UserAndLecturerData u, String address, PasswordHash passwordHash) throws SQLException{
            String insertUser = "INSERT INTO `user` (Fullname,Address,Dob,Gender,Password,Email) VALUES (?,?,?,?,?,?)";
            String insertLecture = "INSERT INTO `lecturer` (Lecturer_id, Office_room, Department, User_id) VALUES (?, ?, ?, ?)";

            PreparedStatement userStmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, u.name);
            userStmt.setString(2, address);
            userStmt.setDate(3, Date.valueOf(u.dob));
            userStmt.setString(4, u.gender);
            userStmt.setString(5, passwordHash.createHash());
            userStmt.setString(6, u.email);
            userStmt.executeUpdate();

            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                PreparedStatement lecStmt = conn.prepareStatement(insertLecture);
                lecStmt.setString(1, u.lec_id);
                lecStmt.setInt(2, u.office_room);
                lecStmt.setString(3, u.dept);
                lecStmt.setInt(4, userId);
                lecStmt.executeUpdate();
                lecStmt.close();
            }
            rs.close();
            userStmt.close();
        }

        //INSERT DATA TO COURSES TABLE
        public void insertCourses(Connection conn, CoursesData d) throws SQLException {
            String insertCourse =  "INSERT INTO `course` (Course_id, Name, Credit, Type) VALUES (?, ?, ?, ?)";

            PreparedStatement courseStmt = conn.prepareStatement(insertCourse);
            courseStmt.setString(1, d.course_id);
            courseStmt.setString(2, d.name);
            courseStmt.setInt(3, d.credit);
            courseStmt.setString(4, d.type);
            courseStmt.executeUpdate();
            courseStmt.close();
        }

        //INSERT DATA TO UNDERGRADUATE TABLE
        public void insertUserAndUndergraduate(Connection conn, UserAndUndergraduateData d, String address, PasswordHash passwordHash) throws SQLException {

            String insertUser = "INSERT INTO `user` (Fullname,Address,Dob,Gender,Password,Email) VALUES (?,?,?,?,?,?)";
            String insertUndergraduate = "INSERT INTO `undergraduate` (Stu_id, Batch, Level, Semester, Department, User_id) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement userStmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, d.name);
            userStmt.setString(2, address);
            userStmt.setDate(3, Date.valueOf(d.dob));
            userStmt.setString(4, d.gender);
            userStmt.setString(5, passwordHash.createHash());
            userStmt.setString(6, d.email);
            userStmt.executeUpdate();

            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);

                PreparedStatement undergraduateStmt = conn.prepareStatement(insertUndergraduate);
                undergraduateStmt.setString(1, d.stu_id);
                undergraduateStmt.setInt(2, d.batch);
                undergraduateStmt.setInt(3, d.level);
                undergraduateStmt.setInt(4, d.semester);
                undergraduateStmt.setString(5, d.dept);
                undergraduateStmt.setInt(6, userId);
                undergraduateStmt.executeUpdate();
                undergraduateStmt.close();
            }
            rs.close();
            userStmt.close();

        }

        public void insertNotices(Connection conn, NoticeData d, String admin) throws SQLException {
            String insertNotice = "INSERT INTO `notice` (Title, Content, Date, Admin_id, Audience, IsImportant) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement noticeStmt = conn.prepareStatement(insertNotice, Statement.RETURN_GENERATED_KEYS);
            noticeStmt.setString(1, d.noticeTitle);
            noticeStmt.setString(2, d.noticeContent);
            noticeStmt.setDate(3, Date.valueOf(d.noticeDate));
            noticeStmt.setString(4, admin);
            noticeStmt.setString(5, d.audience);
            noticeStmt.setInt(6, d.isImportant);
            noticeStmt.executeUpdate();
            noticeStmt.close();
        }
    }

