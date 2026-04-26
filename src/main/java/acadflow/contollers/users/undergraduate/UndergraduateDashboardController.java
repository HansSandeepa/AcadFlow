package acadflow.contollers.users.undergraduate;

import acadflow.DAO.*;
import acadflow.contollers.users.CommonUserController;
import acadflow.models.DispayUndergraduateTimeTable;
import acadflow.models.DisplayTimeTable;
import acadflow.models.getterSetter.UndergraduateCurrentData;
import acadflow.models.users.Undergraduate;
import acadflow.util.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import acadflow.models.getterSetter.MedicalRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

public class UndergraduateDashboardController extends CommonUserController {

    @FXML
    private Label fullName;
    @FXML
    private Label regNoLabel;
    @FXML
    private Label departmentNo;
    @FXML
    private Label batchNo;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea addressField;
    @FXML
    private Label genderField;
    @FXML
    private ImageView userMainImage;//user Image on change self profile view

    @FXML private TableColumn<DisplayTimeTable, String> dayCol;
    @FXML private TableColumn<DisplayTimeTable, String> timeCol;
    @FXML private TableColumn<DisplayTimeTable, String> courseCol;
    @FXML private TableColumn<DisplayTimeTable, String> sessionCol;

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
        //setup user image
        Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);
        setUsersDetails();
        initializeAttendanceTab();


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
    @Override
    protected void cancelSelfFormDetails(){
        emailField.setText("");
        addressField.setText("");
    }

    @FXML private TableView<MedicalRecord> medicalTableView;
    @FXML private Button SubmitMedicalBtn;
    @FXML private Button refreshMedicalBtn;
    private MedicalDAO medicalDAO = new MedicalDAO();
    private ObservableList<MedicalRecord> medicalList = FXCollections.observableArrayList();

    //notice
    @FXML private TableView<Notice> noticesTableView;
    @FXML private TextField searchNoticeField;
    @FXML private Button searchNoticeButton;
    @FXML private CheckBox showImportantOnlyCheck;
    @FXML private Label totalNoticesLabel;
    @FXML private Label importantNoticesLabel;
    @FXML private TextArea noticeDetailArea;
    @FXML private Label noticeTitleLabel;

    private NoticeDAO noticeDAO = new NoticeDAO();
    private ObservableList<Notice> noticeList = FXCollections.observableArrayList();
    private Notice selectedNotice;

    @FXML private ComboBox<AttendanceDAO.CourseEntry> attCourseCombo;
    @FXML private ComboBox<String>                    attSessionTypeCombo;
    @FXML private Button                              attViewBtn;

    @FXML private Label       attTheoryPresentLabel;
    @FXML private Label       attTheoryTotalLabel;
    @FXML private Label       attTheoryPctLabel;
    @FXML private ProgressBar attTheoryProgress;

    @FXML private Label       attPracticalPresentLabel;
    @FXML private Label       attPracticalTotalLabel;
    @FXML private Label       attPracticalPctLabel;
    @FXML private ProgressBar attPracticalProgress;

    @FXML private Label       attCombinedPresentLabel;
    @FXML private Label       attCombinedTotalLabel;
    @FXML private Label       attCombinedPctLabel;
    @FXML private ProgressBar attCombinedProgress;

    @FXML private Label attEligibilityLabel;

    @FXML private TableView<AttendanceViewRecord>        attTableView;
    @FXML private TableColumn<AttendanceViewRecord, String> attColCourse;
    @FXML private TableColumn<AttendanceViewRecord, String> attColDate;
    @FXML private TableColumn<AttendanceViewRecord, String> attColSession;
    @FXML private TableColumn<AttendanceViewRecord, String> attColType;
    @FXML private TableColumn<AttendanceViewRecord, String> attColStatus;
    @FXML private TableColumn<AttendanceViewRecord, String> attColMedical;
    @FXML private TableColumn<AttendanceViewRecord, String> attColApproval;

    @FXML
    private TableView<DisplayTimeTable> timetableTable;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ObservableList<AttendanceViewRecord> attendanceList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        initializeNoticesTab();
        initializeMedicalTab();

        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        sessionCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
    }

    private void initializeNoticesTab() {
        if (noticesTableView == null) return;

        setupNoticeTableColumns();
        loadNoticesForStudent();

        if (searchNoticeButton != null) {
            searchNoticeButton.setOnAction(e -> searchNotices());
        }
        if (searchNoticeField != null) {
            searchNoticeField.setOnAction(e -> searchNotices());
        }
        if (showImportantOnlyCheck != null) {
            showImportantOnlyCheck.setOnAction(e -> applyNoticeFilters());
        }
        if (noticesTableView != null) {
            noticesTableView.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            selectedNotice = newSelection;
                            displayNoticeDetails(selectedNotice);
                        }
                    }
            );
        }
    }

    private void setupNoticeTableColumns() {
        if (noticesTableView == null) return;
        noticesTableView.getColumns().clear();

        TableColumn<Notice, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("noticeId"));
        idCol.setPrefWidth(60);

        TableColumn<Notice, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(280);

        TableColumn<Notice, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Notice, Boolean> importantCol = new TableColumn<>("Important");
        importantCol.setCellValueFactory(new PropertyValueFactory<>("important"));
        importantCol.setPrefWidth(80);

        noticesTableView.getColumns().addAll(idCol, titleCol, dateCol, importantCol);
        noticesTableView.setItems(noticeList);
    }

    private void loadNoticesForStudent() {
        List<Notice> notices = noticeDAO.getNoticesByAudience("Students");
        updateNoticeTable(notices);

        if (totalNoticesLabel != null) {
            totalNoticesLabel.setText(String.valueOf(notices.size()));
        }

        int importantCount = (int) notices.stream().filter(Notice::isImportant).count();
        if (importantNoticesLabel != null) {
            importantNoticesLabel.setText(String.valueOf(importantCount));
        }
    }

    private void searchNotices() {
        String keyword = searchNoticeField.getText().trim();
        List<Notice> results;

        if (keyword.isEmpty()) {
            results = noticeDAO.getNoticesByAudience("Students");
        } else {
            results = noticeDAO.searchNoticesByTitle(keyword);
            results.removeIf(n -> !n.getAudience().equals("Students") && !n.getAudience().equals("All"));
        }

        updateNoticeTable(results);
    }

    private void applyNoticeFilters() {
        boolean showImportantOnly = showImportantOnlyCheck.isSelected();

        List<Notice> filteredNotices = noticeDAO.getNoticesByAudience("Students");

        if (showImportantOnly) {
            filteredNotices = filteredNotices.stream()
                    .filter(Notice::isImportant)
                    .collect(java.util.stream.Collectors.toList());
        }

        updateNoticeTable(filteredNotices);
    }

    private void updateNoticeTable(List<Notice> notices) {
        Platform.runLater(() -> {
            noticeList.clear();
            noticeList.addAll(notices);
            if (noticesTableView != null) {
                noticesTableView.refresh();
            }
        });
    }

    private void displayNoticeDetails(Notice notice) {
        if (noticeTitleLabel != null) {
            String title = notice.isImportant() ? "⚠ IMPORTANT: " + notice.getTitle() : notice.getTitle();
            noticeTitleLabel.setText(title);
        }

        if (noticeDetailArea != null) {
            StringBuilder details = new StringBuilder();
            details.append("Date: ").append(notice.getDate()).append("\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            details.append(notice.getContent());
            noticeDetailArea.setText(details.toString());
        }
    }


    @FXML
    private void saveProfileDetails(){
        String email = emailField.getText();
        String address = addressField.getText();
        Undergraduate undergraduate = new Undergraduate(regNo);
        undergraduate.updateProfile(email, address);    //update user details
    }

    private void setUsersDetails(){
        ArrayList<UndergraduateCurrentData> undergradDetails = new Undergraduate(regNo).getCurrentSelfDetails();
        if (undergradDetails != null && !undergradDetails.isEmpty()) {
            String gender = (undergradDetails.get(0).getGender().equalsIgnoreCase("M")) ? "Male" : "Female";

            genderField.setText(gender);
            fullName.setText(undergradDetails.get(0).getFullName());
            regNoLabel.setText(regNo);
            departmentNo.setText(undergradDetails.get(0).getDepartment());
            batchNo.setText(undergradDetails.get(0).getBatch());
            emailField.setText(undergradDetails.get(0).getEmail());
            addressField.setText(undergradDetails.get(0).getAddress());
        } else {
            System.out.println("\u001B[31mERROR: No undergraduate details found for registration number: " + regNo + "\u001B[0m");
        }
    }

    //attendance setup
    private void initializeAttendanceTab() {
        if (attTableView == null) return;

        setupAttendanceTableColumns();
        loadCoursesIntoCombo();

        attSessionTypeCombo.getItems().setAll("All", "Theory", "Practical");
        attSessionTypeCombo.getSelectionModel().selectFirst();

        attViewBtn.setOnAction(e -> loadAttendance());
        attTableView.setItems(attendanceList);
        loadAttendance();
    }

    private void setupAttendanceTableColumns() {
        attColCourse.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        attColDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        attColSession.setCellValueFactory(new PropertyValueFactory<>("session"));

        // Show "Theory" / "Practical" instead of raw "T"/"P"
        attColType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSessionTypeLabel()));

        // Colour-coded status
        attColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        attColStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("present".equalsIgnoreCase(item)
                        ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        });

        attColMedical.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isHasMedical() ? "Yes" : "No"));

        attColApproval.setCellValueFactory(new PropertyValueFactory<>("medicalApproval"));
        attColApproval.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || "-".equals(item)) { setText("-"); setStyle(""); return; }
                if ("yes".equalsIgnoreCase(item)) {
                    setText("Approved"); setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                } else {
                    setText("Pending"); setStyle("-fx-text-fill: #e67e22;");
                }
            }
        });
    }

    private void loadCoursesIntoCombo() {
        if (regNo == null) return;
        List<AttendanceDAO.CourseEntry> courses = attendanceDAO.getCoursesForStudent(regNo);
        Platform.runLater(() -> {
            attCourseCombo.getItems().clear();
            attCourseCombo.getItems().add(new AttendanceDAO.CourseEntry("", "All Courses", "Both"));
            attCourseCombo.getItems().addAll(courses);
            attCourseCombo.getSelectionModel().selectFirst();
        });
    }

    @FXML
    private void loadAttendance() {
        if (regNo == null) return;

        AttendanceDAO.CourseEntry selectedCourse = attCourseCombo.getValue();
        String courseId = (selectedCourse == null || selectedCourse.getCourseId().isEmpty())
                ? null : selectedCourse.getCourseId();

        String selectedType = attSessionTypeCombo.getValue();
        String sessionTypeFilter = null;
        if ("Theory".equals(selectedType))         sessionTypeFilter = "T";
        else if ("Practical".equals(selectedType)) sessionTypeFilter = "P";

        final String finalCourseId   = courseId;
        final String finalTypeFilter = sessionTypeFilter;

        new Thread(() -> {
            List<AttendanceViewRecord> records =
                    attendanceDAO.getAttendanceViewRecords(regNo, finalCourseId, finalTypeFilter);

            int[] theorySummary, practicalSummary, combinedSummary;

            if (finalCourseId != null) {
                theorySummary    = attendanceDAO.getAttendanceSummary(regNo, finalCourseId, "T");
                practicalSummary = attendanceDAO.getAttendanceSummary(regNo, finalCourseId, "P");
                combinedSummary  = attendanceDAO.getAttendanceSummary(regNo, finalCourseId, null);
            } else {
                // Aggregate from the fetched list
                int tP = 0, tT = 0, pP = 0, pT = 0;
                for (AttendanceViewRecord r : records) {
                    if ("T".equals(r.getSessionType())) { tT++; if ("present".equalsIgnoreCase(r.getStatus())) tP++; }
                    else                                { pT++; if ("present".equalsIgnoreCase(r.getStatus())) pP++; }
                }
                theorySummary    = new int[]{tP, tT, 0};
                practicalSummary = new int[]{pP, pT, 0};
                combinedSummary  = new int[]{tP + pP, tT + pT, 0};
            }

            final int[] tS = theorySummary, pS = practicalSummary, cS = combinedSummary;
            Platform.runLater(() -> {
                attendanceList.setAll(records);
                attTableView.refresh();
                updateSummaryCards(tS, pS, cS);
            });
        }).start();
    }

    private void updateSummaryCards(int[] theory, int[] practical, int[] combined) {
        setCard(attTheoryPresentLabel,    attTheoryTotalLabel,    attTheoryPctLabel,    attTheoryProgress,    theory);
        setCard(attPracticalPresentLabel, attPracticalTotalLabel, attPracticalPctLabel, attPracticalProgress, practical);
        setCard(attCombinedPresentLabel,  attCombinedTotalLabel,  attCombinedPctLabel,  attCombinedProgress,  combined);

        double cPct = combined[1] > 0 ? (double) combined[0] / combined[1] : 0;
        boolean eligible = cPct >= 0.8;
        attEligibilityLabel.setText(eligible ? "✓ Eligible (≥80%)" : "✗ Not Eligible (<80%)");
        attEligibilityLabel.setStyle(eligible
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13px;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13px;");
    }

    private void setCard(Label presentLbl, Label totalLbl, Label pctLbl,
                         ProgressBar bar, int[] summary) {
        int present = summary[0], total = summary[1];
        double pct  = total > 0 ? (double) present / total : 0;
        presentLbl.setText(present + " / " + total);
        totalLbl.setText(total + " sessions");
        pctLbl.setText(String.format("%.1f%%", pct * 100));
        bar.setProgress(pct);
        bar.setStyle(pct >= 0.8 ? "-fx-accent: #27ae60;"
                : pct >= 0.6 ? "-fx-accent: #f39c12;"
                : "-fx-accent: #e74c3c;");
    }

    private void initializeMedicalTab() {
        if (medicalTableView == null) return;

        setupMedicalTableColumns();
        loadMedicalRecords();

        if (SubmitMedicalBtn != null) {
            SubmitMedicalBtn.setOnAction(e -> handleSubmitMedical());
        }
    }

    private void setupMedicalTableColumns() {
        medicalTableView.getColumns().clear();

        TableColumn<MedicalRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(120);

        TableColumn<MedicalRecord, String> sessionCol = new TableColumn<>("Session Type");
        sessionCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        sessionCol.setPrefWidth(100);

        TableColumn<MedicalRecord, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseCol.setPrefWidth(150);

        TableColumn<MedicalRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        medicalTableView.getColumns().addAll(dateCol, sessionCol, courseCol, statusCol);
        medicalTableView.setItems(medicalList);
    }

    private void loadMedicalRecords() {
        List<MedicalRecord> records = medicalDAO.getStudentMedicalRecords(regNo);
        medicalList.clear();
        medicalList.addAll(records);
    }

    @FXML
    private void refreshMedicalRecords() {
        loadMedicalRecords();
        showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Medical records have been refreshed.");
    }

    @FXML
    private void handleSubmitMedical() {
        // Get list of absent records that don't have medical submissions yet
        List<MedicalRecord> absentRecords = medicalDAO.getStudentAbsentRecords(regNo);

        if (absentRecords.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION,
                    "No Records Available",
                    "You don't have any absent sessions that require medical submission.");
            return;
        }

        // Create a choice dialog for session selection
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        dialog.setTitle("Submit Medical Record");
        dialog.setHeaderText("Select the absent session to submit medical for:");
        dialog.setContentText("Available Sessions:");

        // Create a list of formatted strings for display
        List<String> sessionChoices = new ArrayList<>();
        for (MedicalRecord record : absentRecords) {
            String choice = String.format("%s - %s (%s)",
                    record.getDate(),
                    record.getCourseName(),
                    record.getSessionType());
            sessionChoices.add(choice);
        }

        dialog.getItems().addAll(sessionChoices);

        // Show dialog and process selection
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(selectedChoice -> {
            // Find the corresponding MedicalRecord
            MedicalRecord selectedRecord = null;
            for (int i = 0; i < sessionChoices.size(); i++) {
                if (sessionChoices.get(i).equals(selectedChoice)) {
                    selectedRecord = absentRecords.get(i);
                    break;
                }
            }

            if (selectedRecord != null) {
                // Confirm the submission
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Medical Submission");
                confirmAlert.setHeaderText("Submit medical record for this session?");
                confirmAlert.setContentText(String.format(
                        "Date: %s\nCourse: %s\nSession Type: %s\n\nNote: This submission will be pending approval.",
                        selectedRecord.getDate(),
                        selectedRecord.getCourseName(),
                        selectedRecord.getSessionType()
                ));

                Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    // Submit the medical record
                    boolean success = medicalDAO.submitMedical(
                            selectedRecord.getAttendanceId(),
                            selectedRecord.getSessionType(),
                            selectedRecord.getDate()
                    );

                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION,
                                "Success",
                                "Medical record submitted successfully!\nIt is now pending approval from your lecturer.");
                        loadMedicalRecords(); // Refresh the table
                    } else {
                        showAlert(Alert.AlertType.ERROR,
                                "Submission Failed",
                                "Failed to submit medical record. Please try again or contact support.");
                    }
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
