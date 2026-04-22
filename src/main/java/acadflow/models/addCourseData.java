package acadflow.models;

import acadflow.util.DBConnection;

import java.sql.Connection;

public class addCourseData {

    public void addDataForCourse() {

        CoursesData[] data = {
               new CoursesData("TCS2112", "Business Economics", 2, "T","MDS"),
                new CoursesData("ICT2132", "Object Oriented Programming Practicum", 2, "P","ICT"),
                new CoursesData("ICT2122", "Object Oriented Programming", 2, "T","ICT"),
                new CoursesData("BST2112", "Application of Biosystems Technology", 2, "Both","BST"),
                new CoursesData("BST2123", "Engineering Properties of Biomaterials", 3, "Both","BST"),
                new CoursesData("ENT2122", "Thermodynamics", 2, "T","ET"),
                new CoursesData("ENT2152", "Properties of Materials and Application", 2, "Both","ET")
        };

        try(Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);

            userDBLogic udbl = new userDBLogic();

            for (CoursesData d : data) {
                if (udbl.isCourseExists(conn, d.course_id)) {
                    System.out.println("Course with ID " + d.course_id + " already exists");
                    continue;
                }

                udbl.insertCourses(conn, d);
            }

            conn.commit();
            System.out.println("Courses are added");

        } catch (Exception e) {
            System.out.println("Error in insert Courses Data: " + e.getMessage());
        }
    }
}
