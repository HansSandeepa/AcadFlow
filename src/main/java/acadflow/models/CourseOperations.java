package acadflow.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import acadflow.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CourseOperations implements CourseOperationsInt {


    public String deletestatus="";


    //Add Course
    public boolean addCourse(String cid,String cname,int credits,String type,String lecturerID,String department) {

        String  query="INSERT INTO course VALUES (?,?,?,?,?)";
        String query2="INSERT INTO conducted_courses VALUES(?,?)";
        try {


            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,cid);
            ps.setString(2,cname);
            ps.setInt(3,credits);
            ps.setString(4,type.toUpperCase());
            ps.setString(5,department);
            ps.executeUpdate();
            System.out.println("Course added successfully");

            PreparedStatement ps2 = connection.prepareStatement(query2);
            ps2.setString(1,lecturerID);
            ps2.setString(2,cid);
            ps2.executeUpdate();
            System.out.println("Updated conducted course successfully");
            ps.close();

            return true;



        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }



    }

    //Delete Course By Course ID
    public void deleteCourse(String cid) {


        String query="DELETE FROM course WHERE Course_id=?";

        if(cid.isEmpty()){
            System.out.println("Course id cannot be empty");
            deletestatus="empty";
        }else {


            try {
                DBConnection db = new DBConnection();
                Connection connection = db.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, cid);


                int affectedRows = ps.executeUpdate();

                if(affectedRows>0){
                    System.out.println("Course deleted successfully");
                    deletestatus = "true";
                }else{
                    deletestatus="false";
                }


                ps.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                deletestatus = "false";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                deletestatus = "false";
            }
        }

    }


    //Update Course Details, Can Update Individually
    public void updateCourse(String oldcid, String cname, int credits, String type, String Ncid) {

        String currentCid     = oldcid;
        String currentName    = null;
        int    currentCredits = 0;
        String currentType    = null;

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();

            String selectQuery = "SELECT * FROM course WHERE Course_id = ?";
            PreparedStatement ps = connection.prepareStatement(selectQuery);
            ps.setString(1, oldcid);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentCid     = rs.getString("Course_id");
                currentName    = rs.getString("Name");
                currentCredits = rs.getInt("Credit");
                currentType    = rs.getString("Type");
            } else {
                System.out.println("No course found with ID: " + oldcid);
                return;
            }

            if (Ncid != null && !Ncid.isEmpty()) {
                currentCid = Ncid;
            }

            if (cname != null && !cname.isEmpty()) {
                currentName = cname;
            }

            if (credits > 0) {
                currentCredits = credits;
            }

            if (type != null && !type.isEmpty()) {
                if (!type.equalsIgnoreCase("T") && !type.equalsIgnoreCase("P")) {
                    System.out.println("Type must be T or P");
                    return;
                }
                currentType = type;
            }

            String updateQuery = "UPDATE course SET Course_id=?, Name=?, Credit=?, Type=? WHERE Course_id=?";
            PreparedStatement upPs = connection.prepareStatement(updateQuery);
            upPs.setString(1, currentCid);
            upPs.setString(2, currentName);
            upPs.setInt   (3, currentCredits);
            upPs.setString(4, currentType);
            upPs.setString(5, oldcid);

            upPs.executeUpdate();
            System.out.println("Course updated successfully.");

            ps.close();
            upPs.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }




    }

    public ObservableList<Course> getAllCourses() {
        ObservableList<Course> courseList = FXCollections.observableArrayList();

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();
            String sql = "SELECT * FROM course";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                        rs.getString("Course_id"),
                        rs.getString("Name"),
                        rs.getInt("Credit"),
                        rs.getString("Type"),
                        rs.getString("department")
                );
                courseList.add(course);
            }

            ps.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return courseList;
    }

    //View Course list of an individual undergraduate
    public void viewStudentEnrolledCourse(String stid) {

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();

            String selectQuery = "SELECT c.Course_id, c.Name " +
                    "FROM undergraduate u JOIN user usr ON u.User_id = usr.User_id JOIN student_courses sc " +
                    "ON u.Stu_id = sc.Stu_id JOIN course c ON sc.Course_id = c.Course_id WHERE u.Stu_id = ?;";

            PreparedStatement ps = connection.prepareStatement(selectQuery);
            ps.setString(1, stid);

            ResultSet rs = ps.executeQuery();

            System.out.println("+------------+--------------------------------+");
            System.out.printf("| %-10s | %-30s |%n", "Course ID", "Course Name");
            System.out.println("+------------+--------------------------------+");

            while (rs.next()) {
                System.out.printf("| %-10s | %-30s |%n",
                        rs.getString("Course_id"),
                        rs.getString("Name"));
            }

            System.out.println("+------------+--------------------------------+");



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public void getParticularCourseDetails(String cid) {

        if(cid.isEmpty()) {
            System.out.println("Please enter a course ID");
        }else {
            try {
                DBConnection db = new DBConnection();
                Connection connection = db.getConnection();

                String sql = "SELECT * FROM course WHERE Course_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, cid);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    System.out.println("--------------------------");
                    System.out.println("Course ID : " + rs.getString("Course_id"));
                    System.out.println("Name      : " + rs.getString("Name"));
                    System.out.println("Credits   : " + rs.getInt("Credit"));
                    System.out.println("Type      : " + rs.getString("Type"));
                }
                System.out.println("----------------------------");

                ps.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public List<String> getLecturerNames() {
        List<String> lecturerNames = new ArrayList<>();

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();

            String sql = "SELECT Lecturer_Id from lecturer";

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lecturerNames.add(rs.getString("Lecturer_Id"));
            }

            ps.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error fetching lecturer names: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return lecturerNames;
    }
}



