package acadflow.contollers.users.tech_officer;

import acadflow.DAO.AttendanceDAO;
import acadflow.DAO.AttendanceDAO.CourseEntry;
import acadflow.DAO.AttendanceDAO.StudentEntry;
import acadflow.DAO.AttendanceRecord;
import acadflow.DAO.Notice;
import acadflow.DAO.NoticeDAO;
import acadflow.contollers.users.CommonUserController;
import acadflow.models.getterSetter.TechnicalOfficerCurrentData;
import acadflow.models.users.TechnicalOfficer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TechOfficerDashboardController extends CommonUserController {

    // ── Profile Tab ────────────────────────────────────────────────────────────
    @FXML private TextField fullNameField;
    @FXML private Label regNoField;
    @FXML private ComboBox<String> departmentSelect;
    @FXML private Label hiredDateField;
    @FXML private TextField emailField;
    @FXML private TextField genderField;
    @FXML private TextArea addressField;

    // ── Notices Tab ───────────────────────────────────────────────────────────
    @FXML private TableView<Notice> noticesTableView;
    @FXML private TextField searchNoticeField;
    @FXML private Button searchNoticeButton;
    @FXML private ComboBox<String> filterAudienceCombo;
    @FXML private CheckBox showImportantOnlyCheck;
    @FXML private Label totalNoticesLabel;
    @FXML private Label importantNoticesLabel;
    @FXML private TextArea noticeDetailArea;
    @FXML private Label noticeTitleLabel;

    // ── Attendance Tab — Table & Filter ───────────────────────────────────────
    @FXML private TableView<AttendanceRecord> attendanceTableView;
    @FXML private TableColumn<AttendanceRecord, Integer> attIdCol;
    @FXML private TableColumn<AttendanceRecord, String>  attStuIdCol;
    @FXML private TableColumn<AttendanceRecord, String>  attStudentNameCol;
    @FXML private TableColumn<AttendanceRecord, String>  attCourseIdCol;
    @FXML private TableColumn<AttendanceRecord, String>  attCourseNameCol;
    @FXML private TableColumn<AttendanceRecord, String>  attSessionCol;
    @FXML private TableColumn<AttendanceRecord, String>  attSessionTypeCol;
    @FXML private TableColumn<AttendanceRecord, String>  attStatusCol;
    @FXML private TableColumn<AttendanceRecord, String>  attDateCol;

    @FXML private ComboBox<StudentEntry> attStudentFilterCombo;
    @FXML private Button attClearFilterBtn;
    @FXML private Button attRefreshBtn;
    @FXML private Button attEditBtn;
    @FXML private Button attDeleteBtn;
    @FXML private Label  attRecordCountLabel;

    // ── Attendance Tab — Form ─────────────────────────────────────────────────
    @FXML private TitledPane attFormPane;
    @FXML private ComboBox<StudentEntry> attFormStudentCombo;
    @FXML private ComboBox<CourseEntry>  attFormCourseCombo;
    @FXML private ComboBox<String>       attFormSessionCombo;
    @FXML private ComboBox<String>       attFormSessionTypeCombo;
    @FXML private ComboBox<String>       attFormStatusCombo;
    @FXML private DatePicker             attFormDatePicker;
    @FXML private Button                 attSaveBtn;
    @FXML private Button                 attCancelEditBtn;
    @FXML private Label                  attFormModeLabel;

    // ── DAOs & state ──────────────────────────────────────────────────────────
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final NoticeDAO noticeDAO = new NoticeDAO();

    private final ObservableList<AttendanceRecord> attendanceList = FXCollections.observableArrayList();
    private final ObservableList<Notice>           noticeList     = FXCollections.observableArrayList();

    /** Holds the record being edited; null when the form is in Add mode. */
    private AttendanceRecord editingRecord = null;

    private Notice selectedNotice;

    // ══════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        initializeNoticesTab();
        initializeAttendanceTab();
    }

    @Override
    public void initializeWithUserData() {
        userRegNo.setText(regNo);
        username.setText(nameOfUser);

        Image userProfilePic = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);

        departmentSelect.getItems().addAll("ET", "BST", "ICT", "MDS");
        setUsersDetails();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Attendance Tab — Initialisation
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Wires up the attendance TableView columns, populates combo-boxes, and
     * loads the initial full record list.
     */
    private void initializeAttendanceTab() {
        setupAttendanceTableColumns();
        loadStudentsIntoFilterCombo();
        loadStudentsIntoFormCombo();
        loadCoursesIntoFormCombo();
        populateSessionCombo();

        attendanceTableView.setItems(attendanceList);
        loadAllAttendanceRecords();

        // When a row is selected in the table, enable Edit / Delete buttons
        attendanceTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    boolean hasSelection = selected != null;
                    attEditBtn.setDisable(!hasSelection);
                    attDeleteBtn.setDisable(!hasSelection);
                });

        // Start with Edit/Delete disabled until a row is selected
        attEditBtn.setDisable(true);
        attDeleteBtn.setDisable(true);
    }

    /** Binds each TableColumn to the matching JavaFX property on AttendanceRecord. */
    private void setupAttendanceTableColumns() {
        attIdCol.setCellValueFactory(new PropertyValueFactory<>("attendanceId"));
        attStuIdCol.setCellValueFactory(new PropertyValueFactory<>("stuId"));
        attStudentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        attCourseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        attCourseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        attSessionCol.setCellValueFactory(new PropertyValueFactory<>("session"));
        attDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Session Type — show "Theory" / "Practical" instead of raw "T" / "P"
        attSessionTypeCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        attSessionTypeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("T".equalsIgnoreCase(item) ? "Theory" : "Practical");
                }
            }
        });

        // Status — colour-code present (green) / absent (red)
        attStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        attStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean present = "present".equalsIgnoreCase(item);
                    setText(present ? "Present" : "Absent");
                    setStyle(present
                            ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                            : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
    }

    /** Populates the filter combo-box with "All Students" + every undergraduate. */
    private void loadStudentsIntoFilterCombo() {
        ObservableList<StudentEntry> items = FXCollections.observableArrayList();
        items.addAll(attendanceDAO.getAllStudents());
        attStudentFilterCombo.setItems(items);
        attStudentFilterCombo.setPromptText("— All Students —");

        // Re-filter the table whenever the selection changes
        attStudentFilterCombo.setOnAction(e -> applyStudentFilter());
    }

    /** Populates the form's student combo-box with all undergraduates. */
    private void loadStudentsIntoFormCombo() {
        ObservableList<StudentEntry> items =
                FXCollections.observableArrayList(attendanceDAO.getAllStudents());
        attFormStudentCombo.setItems(items);
    }

    /** Populates the form's course combo-box with all courses. */
    private void loadCoursesIntoFormCombo() {
        ObservableList<CourseEntry> items =
                FXCollections.observableArrayList(attendanceDAO.getAllCourses());
        attFormCourseCombo.setItems(items);
    }

    /**
     * Fills the Session combo-box with "Session 1" … "Session 15"
     * (the SRS mandates 15 sessions per component per module).
     */
    private void populateSessionCombo() {
        ObservableList<String> sessions = FXCollections.observableArrayList();
        for (int i = 1; i <= 15; i++) {
            sessions.add("Session " + i);
        }
        attFormSessionCombo.setItems(sessions);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Attendance Tab — Data loading
    // ══════════════════════════════════════════════════════════════════════════

    /** Loads (or reloads) all attendance records into the table. */
    private void loadAllAttendanceRecords() {
        List<AttendanceRecord> records = attendanceDAO.getAllAttendanceRecords();
        Platform.runLater(() -> {
            attendanceList.setAll(records);
            updateRecordCount();
        });
    }

    /** Filters the table by the student selected in the filter combo-box. */
    private void applyStudentFilter() {
        StudentEntry selected = attStudentFilterCombo.getValue();
        if (selected == null) {
            loadAllAttendanceRecords();
            return;
        }
        List<AttendanceRecord> records = attendanceDAO.getAttendanceByStudent(selected.getStuId());
        Platform.runLater(() -> {
            attendanceList.setAll(records);
            updateRecordCount();
        });
    }

    private void updateRecordCount() {
        attRecordCountLabel.setText(attendanceList.size() + " record(s)");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Attendance Tab — Button handlers (FXML @FXML bindings)
    // ══════════════════════════════════════════════════════════════════════════

    /** Clears the student filter and reloads all records. */
    @FXML
    private void handleAttClearFilter() {
        attStudentFilterCombo.setValue(null);
        loadAllAttendanceRecords();
    }

    /** Reloads the table (respecting any active filter). */
    @FXML
    private void handleAttRefresh() {
        if (attStudentFilterCombo.getValue() != null) {
            applyStudentFilter();
        } else {
            loadAllAttendanceRecords();
        }
    }

    /**
     * Fills the form with data from the selected row, switches the form into
     * Edit mode, and opens the TitledPane.
     */
    @FXML
    private void handleAttEdit() {
        AttendanceRecord selected = attendanceTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Record Selected", "Please select an attendance record in the table first.");
            return;
        }

        editingRecord = selected;
        populateFormForEdit(selected);

        attFormPane.setExpanded(true);
        attFormPane.setText("✎  Edit Attendance Record  (ID: " + selected.getAttendanceId() + ")");
        attSaveBtn.setText("💾  Update Record");
        attFormModeLabel.setText("Editing record ID " + selected.getAttendanceId());
        attFormModeLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-style: italic;");
    }

    /**
     * Confirms and deletes the selected attendance record.
     * The linked medical row (if any) is also removed via cascade FK.
     */
    @FXML
    private void handleAttDelete() {
        AttendanceRecord selected = attendanceTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Record Selected", "Please select an attendance record in the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete attendance record?");
        confirm.setContentText("Record ID: " + selected.getAttendanceId()
                + "\nStudent: " + selected.getStudentName() + " (" + selected.getStuId() + ")"
                + "\nCourse: " + selected.getCourseName()
                + "\nSession: " + selected.getSession() + " (" + selected.getSessionTypeLabel() + ")"
                + "\nDate: " + selected.getDate()
                + "\n\nThis action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = attendanceDAO.deleteAttendance(selected.getAttendanceId());
            if (deleted) {
                showInfo("Deleted", "Attendance record deleted successfully.");
                resetForm();
                handleAttRefresh();
            } else {
                showError("Delete Failed", "Could not delete the record. Please try again.");
            }
        }
    }

    /**
     * Validates the form and either inserts a new record or updates the one
     * being edited, depending on the current form mode.
     */
    @FXML
    private void handleAttSave() {
        // ── Validate inputs ──────────────────────────────────────────────────
        StudentEntry studentEntry = attFormStudentCombo.getValue();
        CourseEntry  courseEntry  = attFormCourseCombo.getValue();
        String session     = attFormSessionCombo.getValue();
        String sessionType = attFormSessionTypeCombo.getValue();
        String status      = attFormStatusCombo.getValue();
        LocalDate date     = attFormDatePicker.getValue();

        if (studentEntry == null) { showWarning("Validation Error", "Please select a student."); return; }
        if (courseEntry  == null) { showWarning("Validation Error", "Please select a course."); return; }
        if (session      == null || session.isBlank()) { showWarning("Validation Error", "Please select a session number."); return; }
        if (sessionType  == null) { showWarning("Validation Error", "Please select a session type (Theory / Practical)."); return; }
        if (status       == null) { showWarning("Validation Error", "Please select a status (Present / Absent)."); return; }
        if (date         == null) { showWarning("Validation Error", "Please select the session date."); return; }
        if (date.isAfter(LocalDate.now())) { showWarning("Validation Error", "Date cannot be in the future."); return; }

        // Map display values to DB enum values
        String dbSessionType = "Theory".equals(sessionType) ? "T" : "P";
        String dbStatus      = "Present".equals(status) ? "present" : "absent";

        if (editingRecord == null) {
            // ── ADD mode ────────────────────────────────────────────────────
            boolean duplicate = attendanceDAO.recordExists(
                    studentEntry.getStuId(), courseEntry.getCourseId(), session, dbSessionType);

            if (duplicate) {
                showWarning("Duplicate Record",
                        "An attendance record already exists for:\n"
                                + "Student: " + studentEntry + "\n"
                                + "Course: " + courseEntry + "\n"
                                + "Session: " + session + " (" + sessionType + ")\n\n"
                                + "Please edit the existing record instead.");
                return;
            }

            AttendanceRecord newRecord = new AttendanceRecord(
                    session, dbSessionType, dbStatus, date.toString(),
                    courseEntry.getCourseId(), studentEntry.getStuId());

            boolean added = attendanceDAO.addAttendance(newRecord);
            if (added) {
                showInfo("Success", "Attendance record added successfully (ID: " + newRecord.getAttendanceId() + ").");
                resetForm();
                handleAttRefresh();
            } else {
                showError("Save Failed", "Could not save the attendance record. Please check your inputs and try again.");
            }

        } else {
            // ── EDIT mode ───────────────────────────────────────────────────
            boolean duplicate = attendanceDAO.recordExistsExcluding(
                    studentEntry.getStuId(), courseEntry.getCourseId(), session,
                    dbSessionType, editingRecord.getAttendanceId());

            if (duplicate) {
                showWarning("Duplicate Record",
                        "Another record already exists for this student / course / session / type combination.");
                return;
            }

            editingRecord.setStuId(studentEntry.getStuId());
            editingRecord.setCourseId(courseEntry.getCourseId());
            editingRecord.setSession(session);
            editingRecord.setSessionType(dbSessionType);
            editingRecord.setStatus(dbStatus);
            editingRecord.setDate(date.toString());

            boolean updated = attendanceDAO.updateAttendance(editingRecord);
            if (updated) {
                showInfo("Updated", "Attendance record updated successfully.");
                resetForm();
                handleAttRefresh();
            } else {
                showError("Update Failed", "Could not update the record. Please try again.");
            }
        }
    }

    /** Cancels an active edit and resets the form to Add mode. */
    @FXML
    private void handleAttCancelEdit() {
        resetForm();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Attendance Tab — Form helpers
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Pre-fills the form fields with values from {@code record} so the officer
     * can make changes and save.
     */
    private void populateFormForEdit(AttendanceRecord record) {
        // Select matching student in combo-box
        attFormStudentCombo.getItems().stream()
                .filter(s -> s.getStuId().equals(record.getStuId()))
                .findFirst()
                .ifPresent(attFormStudentCombo::setValue);

        // Select matching course in combo-box
        attFormCourseCombo.getItems().stream()
                .filter(c -> c.getCourseId().equals(record.getCourseId()))
                .findFirst()
                .ifPresent(attFormCourseCombo::setValue);

        attFormSessionCombo.setValue(record.getSession());
        attFormSessionTypeCombo.setValue("T".equals(record.getSessionType()) ? "Theory" : "Practical");
        attFormStatusCombo.setValue("present".equals(record.getStatus()) ? "Present" : "Absent");

        try {
            attFormDatePicker.setValue(LocalDate.parse(record.getDate()));
        } catch (Exception e) {
            attFormDatePicker.setValue(null);
        }
    }

    /** Clears all form fields and switches back to Add mode. */
    private void resetForm() {
        editingRecord = null;
        attFormStudentCombo.setValue(null);
        attFormCourseCombo.setValue(null);
        attFormSessionCombo.setValue(null);
        attFormSessionTypeCombo.setValue(null);
        attFormStatusCombo.setValue(null);
        attFormDatePicker.setValue(null);
        attFormPane.setText("Add New Attendance Record");
        attSaveBtn.setText("💾  Save Record");
        attFormModeLabel.setText("");
        attendanceTableView.getSelectionModel().clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Notices Tab
    // ══════════════════════════════════════════════════════════════════════════

    private void initializeNoticesTab() {
        List<String> audiences = List.of("All", "Students", "Teachers", "Staff", "Parents");
        filterAudienceCombo.setItems(FXCollections.observableArrayList(audiences));
        filterAudienceCombo.setValue("All");

        setupNoticeTableColumns();
        loadNoticesForLecturer();

        searchNoticeButton.setOnAction(e -> searchNotices());
        searchNoticeField.setOnAction(e -> searchNotices());
        filterAudienceCombo.setOnAction(e -> applyNoticeFilters());
        showImportantOnlyCheck.setOnAction(e -> applyNoticeFilters());

        noticesTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        selectedNotice = selected;
                        displayNoticeDetails(selectedNotice);
                    }
                });
    }

    private void setupNoticeTableColumns() {
        noticesTableView.getColumns().clear();

        TableColumn<Notice, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("noticeId"));
        idCol.setPrefWidth(60);

        TableColumn<Notice, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);

        TableColumn<Notice, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Notice, String> audienceCol = new TableColumn<>("Audience");
        audienceCol.setCellValueFactory(new PropertyValueFactory<>("audience"));
        audienceCol.setPrefWidth(100);

        TableColumn<Notice, Boolean> importantCol = new TableColumn<>("Important");
        importantCol.setCellValueFactory(new PropertyValueFactory<>("important"));
        importantCol.setPrefWidth(80);

        noticesTableView.getColumns().addAll(idCol, titleCol, dateCol, audienceCol, importantCol);
        noticesTableView.setItems(noticeList);
    }

    private void loadNoticesForLecturer() {
        List<Notice> notices = noticeDAO.getNoticesByAudience("Teachers");
        Platform.runLater(() -> {
            noticeList.setAll(notices);
            if (totalNoticesLabel != null)
                totalNoticesLabel.setText(String.valueOf(notices.size()));
            if (importantNoticesLabel != null)
                importantNoticesLabel.setText(String.valueOf(
                        notices.stream().filter(Notice::isImportant).count()));
        });
    }

    private void searchNotices() {
        String keyword = searchNoticeField.getText().trim();
        List<Notice> results = keyword.isEmpty()
                ? noticeDAO.getNoticesByAudience("Teachers")
                : noticeDAO.searchNoticesByTitle(keyword).stream()
                .filter(n -> n.getAudience().equals("Teachers") || n.getAudience().equals("All"))
                .toList();
        Platform.runLater(() -> { noticeList.setAll(results); noticesTableView.refresh(); });
    }

    private void applyNoticeFilters() {
        String audience = filterAudienceCombo.getValue();
        List<Notice> filtered = "All".equals(audience)
                ? noticeDAO.getNoticesByAudience("Teachers")
                : noticeDAO.getNoticesByAudience(audience);
        if (showImportantOnlyCheck.isSelected())
            filtered = filtered.stream().filter(Notice::isImportant).toList();
        final List<Notice> result = filtered;
        Platform.runLater(() -> { noticeList.setAll(result); noticesTableView.refresh(); });
    }

    private void displayNoticeDetails(Notice notice) {
        if (noticeTitleLabel != null)
            noticeTitleLabel.setText(notice.isImportant()
                    ? "⚠ IMPORTANT: " + notice.getTitle() : notice.getTitle());
        if (noticeDetailArea != null) {
            noticeDetailArea.setText("Date: " + notice.getDate()
                    + "\nAudience: " + notice.getAudience()
                    + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n"
                    + notice.getContent());
        }
    }

    // Profile Tab
    @FXML
    private void saveProfileDetails() {
        new TechnicalOfficer(regNo).updateProfile(
                fullNameField.getText(),
                departmentSelect.getValue(),
                genderField.getText(),
                emailField.getText(),
                addressField.getText());
    }

    private void setUsersDetails() {
        ArrayList<TechnicalOfficerCurrentData> details =
                new TechnicalOfficer(regNo).getCurrentSelfDetails();

        if (details != null && !details.isEmpty()) {
            TechnicalOfficerCurrentData d = details.get(0);
            String gender = d.getGender().equalsIgnoreCase("male") ? "Male" : "Female";
            fullNameField.setText(d.getFullName());
            regNoField.setText(d.getTechOfficerId());
            departmentSelect.setValue(d.getDepartment());
            hiredDateField.setText(d.getHireDate());
            genderField.setText(gender);
            emailField.setText(d.getEmail());
            addressField.setText(d.getAddress());
        } else {
            System.out.println("\u001B[31mERROR: No technical officer details found for: " + regNo + "\u001B[0m");
        }
    }

    @FXML
    @Override
    protected void cancelSelfFormDetails() {
        fullNameField.setText("");
        departmentSelect.setValue(null);
        genderField.setText("");
        emailField.setText("");
        addressField.setText("");
    }

    // Alert helpers
    private void showInfo(String header, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("AcadFlow");
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }

    private void showWarning(String header, String content) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("AcadFlow");
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }

    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("AcadFlow");
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
