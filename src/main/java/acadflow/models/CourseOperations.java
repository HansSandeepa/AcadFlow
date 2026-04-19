package acadflow.models;

import java.sql.*;
import acadflow.util.DBConnection;

public class CourseOperations implements CourseOperationsInt {





    //Add Course
    public void addCourse(String cid,String cname,int credits,String type) {

       String  query="INSERT INTO course VALUES (?,?,?,?)";
        try {



            if(cid.isEmpty()){
                System.out.println("Course id Cannot be empty");
            }
            if(cname.isEmpty()){
                System.out.println("Course name cannot be empty");
            }
            if(!(type.equals("T") || type.equals("P"))){
                System.out.println("Please enter a valid course type");
            }

            else{


                DBConnection db = new DBConnection();
                Connection connection = db.getConnection();

                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1,cid);
                ps.setString(2,cname);
                ps.setInt(3,credits);
                ps.setString(4,type.toUpperCase());
                ps.executeUpdate();
                System.out.println("Course added successfully");
                ps.close();



            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    //Delete Course By Course ID
    public void deleteCourse(String cid) {


        String query="DELETE FROM course WHERE Course_id=?";

        if(cid.isEmpty()){
            System.out.println("Course id cannot be empty");
        }else {


            try {
                DBConnection db = new DBConnection();
                Connection connection = db.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, cid);

                ps.executeUpdate();
                System.out.println("Course deleted successfully");

                ps.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());                }
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

    //View All course list
    public void viewCourseList() {


        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();
            String sql = "SELECT * FROM course";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("---------------------------");
                System.out.println("Course ID : " + rs.getString("Course_id"));
                System.out.println("Name      : " + rs.getString("Name"));
                System.out.println("Credits   : " + rs.getInt("Credit"));
                System.out.println("Type      : " + rs.getString("Type"));
            }
            System.out.println("---------------------------");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


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



}
