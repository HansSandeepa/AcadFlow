package acadflow;

import acadflow.models.addCourseData;
import acadflow.models.addDataForUserAndLecturer;
import acadflow.models.addDataForUserAndOfficer;
import acadflow.models.addDataForUserAndUndergraduate;
import acadflow.models.users.addNoticeData;
import acadflow.util.DBConnection;
import acadflow.util.PasswordHash;

import java.sql.*;

public class LoadIniDbDataThread extends Thread{

    @Override
    public void run(){
        createAdminIfNotExists();   //create admin user in database if not exist

        //INSERT TECHNICAL OFFICERS DATA
        addDataForUserAndOfficer addDataForUserAndOfficer = new addDataForUserAndOfficer();
        addDataForUserAndOfficer.addUserAndOfficerData();

        //INSERT LECTURERS DATA
        addDataForUserAndLecturer addDataForUserAndLecturer = new addDataForUserAndLecturer();
        addDataForUserAndLecturer.addUserAndLecturerData();

        //INSERT COURSES DATA
        addCourseData addCourseData = new addCourseData();
        addCourseData.addDataForCourse();

        //INSERT UNDERGRADUATES DATA
        addDataForUserAndUndergraduate addDataForUserAndUndergraduate = new addDataForUserAndUndergraduate();
        addDataForUserAndUndergraduate.addUserAndUndergraduateData();

        //INSERT NOTICE DATA
        addNoticeData addNoticeData = new addNoticeData();
        addNoticeData.addDataForNotice();
    }

    //setup admin in database if not exists
    private void createAdminIfNotExists(){

        //check if there is an admin user already exists in the database
        String checkQuery = "SELECT EXISTS(SELECT 1 FROM user WHERE Fullname = ?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1,"admin");
            ResultSet rs =  checkStmt.executeQuery();
            while (rs.next()){
                if (rs.getInt(1) == 1){
                    System.out.println("Admin already exists!");
                    rs.close();
                    checkStmt.close();
                    conn.close();
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: " + e.getMessage() + "\u001B[0m");
        }

        //create admin if not exists
        String insertUser = "INSERT INTO user (Fullname,Address,Dob,Gender,Password,Email,User_type) VALUES (?,?,?,?,?,?,?)";
        String insertAdmin = "INSERT INTO admin VALUES (?,?)";
        String adminId = "admin0001";

        //setup password hash
        String adminPassword = "admin@123";
        PasswordHash passwordHash = new PasswordHash(adminPassword);

        try {
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  //disable auto-commit for handle 2 insert statements

            PreparedStatement userStmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1,"admin");
            userStmt.setString(2,"Faculty of Technology,University of Ruhuna, Karagoda, Uyangoda");
            userStmt.setDate(3, java.sql.Date.valueOf("2000-01-01"));
            userStmt.setString(4,"M");
            userStmt.setString(5,passwordHash.createHash());
            userStmt.setString(6,"admin_acadflow@fot.ruh.ac.lk");
            userStmt.setString(7,"Admin");
            userStmt.executeUpdate();

            //get generated user id
            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                //insert user id to admin table
                int userId = rs.getInt(1);

                PreparedStatement adminStmt = conn.prepareStatement(insertAdmin);
                adminStmt.setString(1,adminId);
                adminStmt.setInt(2,userId);
                adminStmt.executeUpdate();
                adminStmt.close();
            }

            conn.commit();  //add both statements to the database
            rs.close();
            userStmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: " + e.getMessage() + "\u001B[0m");
        }
    }
}
