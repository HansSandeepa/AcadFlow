package acadflow.models.users;

import acadflow.models.LectureCourse;
import acadflow.util.DBConnection;
import java.sql.*;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Lecturer extends User implements SelfProfileUpdate{
    public Lecturer(String regNo) {
        super(regNo);
    }

    @Override
    public void updateProfile(String address, LocalDate dob, Gender gender, String email) {

    }

    //mewidihata userId ganna puluwan
    //remove this method anjana
    public void showLecId(){
        System.out.println("Test Lecturer ID: " + userId);
    }
}
