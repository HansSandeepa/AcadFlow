package acadflow;


import acadflow.models.addDataForUserAndLecturer;
import acadflow.models.addDataForUserAndOfficer;
import acadflow.util.DBConnection;
import acadflow.util.PasswordHash;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (DBConnection.getAndCheckConnection()) {
            createAdminIfNotExists();   //create admin user in database if not exists

            //INSERT TECHNICAL OFFICERS DATA
            addDataForUserAndOfficer addDataForUserAndOfficer = new addDataForUserAndOfficer();
            addDataForUserAndOfficer.addUserAndOfficerData();

            //INSERT LECTURER DATA
            addDataForUserAndLecturer addDataForUserAndLecturer = new addDataForUserAndLecturer();
            addDataForUserAndLecturer.addUserAndLecturerData();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acadflow/login/loginPage.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Acadflow");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection Failed");
        alert.setHeaderText("Unable to connect to the database");
        alert.setContentText("Please check your connection settings and try again.");

        ButtonType retry = new ButtonType("Retry");
        ButtonType exit  = new ButtonType("Exit");
        alert.getButtonTypes().setAll(retry, exit);

        alert.showAndWait().ifPresent(response -> {

            if (response == retry) {
                if (DBConnection.getAndCheckConnection()) {
                    // retry to start util connection
                    Stage newStage = new Stage();
                    try {
                        start(newStage);
                    }
                    catch (Exception e) {
                        System.out.println("\u001B[31mERROR: " + e.getMessage() + "\u001B[0m");
                    }

                } else {
                    //try again if retry failed
                    showErrorDialog();
                }
            } else {
                // exit program in exit button click
                Platform.exit();
                System.exit(0);
            }
        });
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
        String insertUser = "INSERT INTO user (Fullname,Address,Dob,Gender,Password,Email) VALUES (?,?,?,?,?,?)";
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