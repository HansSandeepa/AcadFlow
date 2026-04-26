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
                new CoursesData("ENT2152", "Properties of Materials and Application", 2, "Both","ET"),
                new CoursesData("ICT1212", "Database Management Systems", 2, "T","ICT"),
                new CoursesData("ICT1222", "Database Management Systems Practicum", 2, "P","ICT"),
                new CoursesData("ICT1233", "Server Side Web Development", 3, "Both","ICT"),
                new CoursesData("ICT1242", "Computer Architecture", 2, "T","ICT"),
                new CoursesData("TMS1133", "Physics of Mechanical Systems", 1, "Both", "ET"),
                new CoursesData("TMS1141", "Physics of Mechanical Systems Laboratory", 1, "P", "ET"),
                new CoursesData("TMS1152", "Applied Calculus I", 2, "T", "ET"),
                new CoursesData("ENG1212", "Language Skill Enhancement II", 2, "T", "ET"),
                new CoursesData("ENT1212", "Advance Manufacturing Technology", 2, "Both", "ET"),
                new CoursesData("BST1222", "Electronics for Biosystems Technology", 2, "Both", "BST"),
                new CoursesData("BST1232", "Organic Chemistry", 2, "T", "BST"),
                new CoursesData("BST1242", "Introduction to Environmental Science", 2, "T", "BST"),
                new CoursesData("BST1253", "Biology II", 2, "Both", "BST"),
                new CoursesData("BST1262", "Introduction to Fisheries Biology", 2, "Both", "BST")

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
