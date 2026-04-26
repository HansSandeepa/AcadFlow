package acadflow.contollers.users.tech_officer;

import acadflow.DAO.DisplayTimeTableDAO;
import acadflow.contollers.users.CommonUserController;
import acadflow.models.DisplayTimeTable;
import acadflow.models.users.TechnicalOfficer;
import acadflow.util.DBConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class TechOfficerDashboardController extends CommonUserController {
    @FXML
    private Button logoutBtn;
    @FXML
    private Label userRegNo;
    @FXML
    private Label username;
    @FXML
    private ImageView userImg;  //user Image on top panel
    @FXML
    private ImageView userMainImage;    //user Image on change self profile view

    @FXML private TableColumn<DisplayTimeTable, String> dayCol;
    @FXML private TableColumn<DisplayTimeTable, String> timeCol;
    @FXML private TableColumn<DisplayTimeTable, String> courseCol;
    @FXML private TableColumn<DisplayTimeTable, String> sessionCol;

    @FXML
    private TableView<DisplayTimeTable> timetableTable;

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
        //setup user image
        Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);

        super.initializeWithUserData();

        // Load timetable for the tech officer's department
        String department = getTechOfficerDepartment();

        if (department != null) {
            System.out.println("DEBUG: Loading timetable for Tech Officer - Dept: " + department);

            DisplayTimeTableDAO dao = new DisplayTimeTableDAO();
            List<DisplayTimeTable> timetable = dao.getTimetableForDepartment(department);

            if (timetable == null || timetable.isEmpty()) {
                System.out.println("WARNING: No timetable data found for department: " + department);
            } else {
                System.out.println("DEBUG: Timetable loaded with " + timetable.size() + " entries");
            }

            if (timetableTable != null) {
                timetableTable.setItems(FXCollections.observableArrayList(timetable));
            } else {
                System.out.println("ERROR: timetableTable is null - FXML injection failed!");
            }
        } else {
            System.out.println("ERROR: Could not determine tech officer's department");
        }
    }

    public void initialize() {
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        sessionCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
    }

    @FXML
    private void onLogoutBtnClick(){
        new TechnicalOfficer().logout(logoutBtn);
    }

    private String getTechOfficerDepartment() {
        // Fetch the tech officer's department from database using the logged-in user's registration number (regNo)
        String query = "SELECT Department FROM tec_officer WHERE T_officer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, regNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("Department");
            } else {
                System.out.println("WARNING: Tech officer record not found for T_officer_id: " + regNo);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to fetch tech officer department: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
