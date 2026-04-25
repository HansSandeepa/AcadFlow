package acadflow.models;

import acadflow.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//lecture course model class
public class LectureCourse {
    private final StringProperty cid;
    private final StringProperty cName;
    private final StringProperty cCredits;

    // Default constructor
    public LectureCourse() {
        this.cid = new SimpleStringProperty("");
        this.cName = new SimpleStringProperty("");
        this.cCredits = new SimpleStringProperty("");
    }

    // Parameterized constructor
    public LectureCourse(String cid, String cName, String cCredits) {
        this.cid = new SimpleStringProperty(cid);
        this.cName = new SimpleStringProperty(cName);
        this.cCredits = new SimpleStringProperty(cCredits);
    }

    // Getters and Setters with JavaFX Properties
    public String getCid() {
        return cid.get();
    }

    public StringProperty cidProperty() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid.set(cid);
    }

    public String getCName() {
        return cName.get();
    }

    public StringProperty cNameProperty() {
        return cName;
    }

    public void setCName(String cName) {
        this.cName.set(cName);
    }

    public String getCCredits() {
        return cCredits.get();
    }

    public StringProperty cCreditsProperty() {
        return cCredits;
    }

    public void setCCredits(String cCredits) {
        this.cCredits.set(cCredits);
    }

    // Static method to fetch courses for a specific lecturer by userId
    public static ObservableList<LectureCourse> getLectureCourse(String userId) {
        ObservableList<LectureCourse> lectureCoursesList = FXCollections.observableArrayList();

        String query = "SELECT c.Course_id, c.Name AS Course_Name, c.Credit FROM course c INNER JOIN conducted_courses cc ON c.Course_id = cc.Course_id INNER JOIN lecturer l ON cc.Lecturer_Id = l.Lecturer_Id INNER JOIN user u ON l.User_id = u.User_id WHERE u.User_id = ?";

        try {
            DBConnection db = new DBConnection();
            Connection connection = db.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                LectureCourse lc = new LectureCourse(
                        resultSet.getString("Course_id"),
                        resultSet.getString("Course_Name"),
                        String.valueOf(resultSet.getInt("Credit"))  // Convert int to String
                );
                lectureCoursesList.add(lc);
            }

            resultSet.close();
            ps.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return lectureCoursesList;
    }
}