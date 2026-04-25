package acadflow.models;

import acadflow.util.DBConnection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//course material class
public class CourseMaterial {
    private final SimpleIntegerProperty materialId;
    private final SimpleStringProperty materialPath;
    private final SimpleStringProperty courseId;
    private final SimpleStringProperty courseName;

    public CourseMaterial() {
        this.materialId = new SimpleIntegerProperty(0);
        this.materialPath = new SimpleStringProperty("");
        this.courseId = new SimpleStringProperty("");
        this.courseName = new SimpleStringProperty("");
    }

    public CourseMaterial(int materialId, String materialPath, String courseId, String courseName) {
        this.materialId = new SimpleIntegerProperty(materialId);
        this.materialPath = new SimpleStringProperty(materialPath);
        this.courseId = new SimpleStringProperty(courseId);
        this.courseName = new SimpleStringProperty(courseName);
    }


    public int getMaterialId() {
        return materialId.get();
    }

    public SimpleIntegerProperty materialIdProperty() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId.set(materialId);
    }

    public String getMaterialPath() {
        return materialPath.get();
    }

    public SimpleStringProperty materialPathProperty() {
        return materialPath;
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath.set(materialPath);
    }

    public String getCourseId() {
        return courseId.get();
    }

    public SimpleStringProperty courseIdProperty() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId.set(courseId);
    }


    public String getCourseName() {
        return courseName.get();
    }

    public SimpleStringProperty courseNameProperty() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName.set(courseName);
    }


    public String getFileName() {
        String path = getMaterialPath();
        if (path != null && path.contains("/")) {
            return path.substring(path.lastIndexOf('/') + 1);
        }
        return path;
    }


    public static boolean saveMaterialRecord(String materialPath, String courseId) {
        String query = "INSERT INTO course_material (Material_path, Course_id) VALUES (?, ?)";

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, materialPath);
            ps.setString(2, courseId);

            int rowsAffected = ps.executeUpdate();

            ps.close();
            connection.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(" Failed to save material record! " + e.getMessage());
            return false;
        }
    }


    public static ObservableList<CourseMaterial> getMaterialsByCourse(String courseId) {
        ObservableList<CourseMaterial> materialsList = FXCollections.observableArrayList();

        String query = "SELECT cm.Material_id, cm.Material_path, cm.Course_id, c.Name AS Course_Name FROM course_material cm INNER JOIN course c ON cm.Course_id = c.Course_id WHERE cm.Course_id = ?";

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, courseId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CourseMaterial material = new CourseMaterial(
                        rs.getInt("Material_id"),
                        rs.getString("Material_path"),
                        rs.getString("Course_id"),
                        rs.getString("Course_Name")
                );
                materialsList.add(material);
            }

            rs.close();
            ps.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Failed to fetch materials! " + e.getMessage());
        }

        return materialsList;
    }


    public static boolean deleteMaterial(int materialId) {
        String query = "DELETE FROM course_material WHERE Material_id = ?";

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, materialId);

            int rowsAffected = ps.executeUpdate();

            ps.close();
            connection.close();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(" Failed to delete material! " + e.getMessage() );
            return false;
        }
    }


    public static boolean deleteMaterialFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println(" Failed to delete material file! " + e.getMessage() );
            return false;
        }
    }
}