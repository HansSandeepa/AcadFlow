package acadflow.contollers.users.undergraduate;

import acadflow.DAO.DisplayTimeTableDAO;
import acadflow.contollers.users.CommonUserController;
import acadflow.models.DispayUndergraduateTimeTable;
import acadflow.models.DisplayTimeTable;
import acadflow.models.users.Undergraduate;
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

public class UndergraduateDashboardController extends CommonUserController {
    @FXML
    private Button logoutBtn;
    @FXML
    private Label userRegNo;
    @FXML
    private Label username;
    @FXML
    private ImageView userImg;  //user Image on top panel
    @FXML
    private ImageView userMainImage;//user Image on change self profile view

    @FXML private TableColumn<DisplayTimeTable, String> dayCol;
    @FXML private TableColumn<DisplayTimeTable, String> timeCol;
    @FXML private TableColumn<DisplayTimeTable, String> courseCol;
    @FXML private TableColumn<DisplayTimeTable, String> sessionCol;

    public void initialize() {
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        sessionCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
    }


    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
        //setup user image
        Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);

        super.initializeWithUserData();

        // Fetch the logged-in undergraduate's data
        DispayUndergraduateTimeTable student = getLoggedInUndergraduate();

        if (student == null) {
            System.out.println("ERROR: Failed to load undergraduate data");
            return;
        }

        System.out.println("DEBUG: Loading timetable for - Dept: " + student.getDept() +
                         ", Level: " + student.getLevel() + ", Semester: " + student.getSemester());

        DisplayTimeTableDAO dao = new DisplayTimeTableDAO();
        List<DisplayTimeTable> timetable = dao.getTimetableForUndergraduate(
                student.getDept(),
                student.getLevel(),
                student.getSemester()
        );

        if (timetable == null || timetable.isEmpty()) {
            System.out.println("WARNING: No timetable data found for this student");
        } else {
            System.out.println("DEBUG: Timetable loaded with " + timetable.size() + " entries");
        }

        if (timetableTable != null) {
            timetableTable.setItems(FXCollections.observableArrayList(timetable));
        } else {
            System.out.println("ERROR: timetableTable is null - FXML injection failed!");
        }
    }

    @FXML
    private TableView<DisplayTimeTable> timetableTable;

    @FXML
    private void onLogoutBtnClick(){
        new Undergraduate().logout(logoutBtn);
    }



    private DispayUndergraduateTimeTable getLoggedInUndergraduate() {
        // Fetch real data from database using the logged-in student's registration number (regNo)
        String query = "SELECT Batch, Level, Semester, Department, User_id FROM undergraduate WHERE Stu_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, regNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int batch = rs.getInt("Batch");
                int level = rs.getInt("Level");
                int semester = rs.getInt("Semester");
                String department = rs.getString("Department");
                int userId = rs.getInt("User_id");

                return new DispayUndergraduateTimeTable(regNo, batch, level, semester, department, userId);
            } else {
                System.out.println("WARNING: Undergraduate record not found for Stu_id: " + regNo);
                // Return default values as fallback
                return new DispayUndergraduateTimeTable(regNo, 2, 2, 1, "ICT", 0);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to fetch undergraduate data: " + e.getMessage());
            e.printStackTrace();
            // Return default values as fallback
            return new DispayUndergraduateTimeTable(regNo, 2, 2, 1, "ICT", 0);
        }
    }
}
