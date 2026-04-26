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

import acadflow.DAO.MedicalDAO;
import acadflow.models.getterSetter.MedicalRecord;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class TechOfficerDashboardController extends CommonUserController {

    // ── Medical Tab ───────────────────────────────────────────────────────────
    @FXML private TableView<MedicalRecord> medicalTableView;
    @FXML private TableColumn<MedicalRecord, String> medStuIdCol;
    @FXML private TableColumn<MedicalRecord, String> medStuNameCol;
    @FXML private TableColumn<MedicalRecord, String> medDateCol;
    @FXML private TableColumn<MedicalRecord, String> medCourseCol;
    @FXML private TableColumn<MedicalRecord, String> medSessionCol;
    @FXML private TableColumn<MedicalRecord, String> medStatusCol;
    @FXML private TableColumn<MedicalRecord, String> medActionCol;

    @FXML private TextField medSearchField;
    @FXML private Button medSearchBtn;
    @FXML private Button medRefreshBtn;
    @FXML private TitledPane medDetailPane;
    @FXML private Label medDetailStudent;
    @FXML private Label medDetailDate;
    @FXML private Label medDetailCourse;
    @FXML private Label medDetailSession;
    @FXML private Button medApproveBtn;
    @FXML private Button medDenyBtn;

    private final MedicalDAO medicalDAO = new MedicalDAO();
    private final ObservableList<MedicalRecord> medicalList = FXCollections.observableArrayList();


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

    /**
     * The department this Technical Officer belongs to (e.g. "ICT").
     * Set once in initializeWithUserData() after the profile is fetched from DB,
     * then used to scope all student/course/attendance queries.
     */
    private String officerDepartment = null;

    private Notice selectedNotice;

    // ══════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        initializeNoticesTab();
        initializeAttendanceTab();
        initializeMedicalTab();
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

        // Load profile details first — this populates officerDepartment
        setUsersDetails();

        // Now reload attendance combo-boxes scoped to the officer's department.
        // initializeAttendanceTab() already ran in initialize() with officerDepartment=null
        // (no DB calls were made yet for combos), so we repopulate them here once
        // the department is known.
        reloadDepartmentScopedAttendanceData();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Attendance Tab — Initialisation
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Sets up the attendance TableView columns and listener wiring only.
     * Combo-box population and initial data load are deferred to
     * reloadDepartmentScopedAttendanceData(), called once officerDepartment is known.
     */
    private void initializeAttendanceTab() {
        setupAttendanceTableColumns();
        attendanceTableView.setItems(attendanceList);

        // When a row is selected in the table, enable Edit / Delete buttons
        attendanceTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    boolean hasSelection = selected != null;
                    attEditBtn.setDisable(!hasSelection);
                    attDeleteBtn.setDisable(!hasSelection);
                });

        attEditBtn.setDisable(true);
        attDeleteBtn.setDisable(true);
    }

    /**
     * Populates every attendance combo-box and the main table using the officer's
     * department. Called from initializeWithUserData() after the profile fetch has
     * set officerDepartment.
     */
    private void reloadDepartmentScopedAttendanceData() {
        if (officerDepartment == null || officerDepartment.isBlank()) {
            System.err.println("[TechOfficer] officerDepartment not set — attendance combos will be empty.");
            return;
        }
        loadStudentsIntoFilterCombo();
        loadStudentsIntoFormCombo();
        loadCoursesIntoFormCombo();
        populateSessionCombo();
        loadAllAttendanceRecords();
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

    /** Populates the filter combo-box with undergraduates from the officer's department only. */
    private void loadStudentsIntoFilterCombo() {
        ObservableList<StudentEntry> items = FXCollections.observableArrayList(
                attendanceDAO.getStudentsByDepartment(officerDepartment));
        attStudentFilterCombo.setItems(items);
        attStudentFilterCombo.setPromptText("— All " + officerDepartment + " Students —");

        // Re-filter the table whenever the selection changes
        attStudentFilterCombo.setOnAction(e -> applyStudentFilter());
    }

    /** Populates the form's student combo-box with undergraduates from the officer's department only. */
    private void loadStudentsIntoFormCombo() {
        ObservableList<StudentEntry> items = FXCollections.observableArrayList(
                attendanceDAO.getStudentsByDepartment(officerDepartment));
        attFormStudentCombo.setItems(items);
    }

    /** Populates the form's course combo-box with courses from the officer's department only. */
    private void loadCoursesIntoFormCombo() {
        ObservableList<CourseEntry> items = FXCollections.observableArrayList(
                attendanceDAO.getCoursesByDepartment(officerDepartment));
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

    /** Loads attendance records scoped to the officer's department into the table. */
    private void loadAllAttendanceRecords() {
        List<AttendanceRecord> records = attendanceDAO.getAttendanceByDepartment(officerDepartment);
        Platform.runLater(() -> {
            attendanceList.setAll(records);
            updateRecordCount();
        });
    }

    /** Filters the table by the student selected in the filter combo-box,
     *  still scoped to the officer's department (courses must also match). */
    private void applyStudentFilter() {
        StudentEntry selected = attStudentFilterCombo.getValue();
        if (selected == null) {
            loadAllAttendanceRecords();
            return;
        }
        List<AttendanceRecord> records =
                attendanceDAO.getAttendanceByStudentInDepartment(selected.getStuId(), officerDepartment);
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


    private void initializeMedicalTab() {
        if (medicalTableView == null) return;

        setupMedicalTableColumns();
        loadAllMedicalRecords();

        // Add Approve/Deny buttons inside the Actions column
        medActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");

            private final HBox pane = new HBox(5, approveBtn);

            {
                approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11;");

            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                MedicalRecord record = (MedicalRecord) getTableRow().getItem();

                // Only show buttons for pending records
                if ("Pending".equals(record.getStatus())) {
                    approveBtn.setOnAction(e -> {
                        handleMedicalDecision(record, true);
                    });

                    setGraphic(pane);
                } else {
                    setGraphic(null);
                    setText(record.getStatus());
                }
            }
        });

        // Double-click to see details
        medicalTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                MedicalRecord selected = medicalTableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showMedicalDetails(selected);
                }
            }
        });
    }

    private void setupMedicalTableColumns() {
        medicalTableView.getColumns().clear();

        medStuIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        medStuNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        medDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        medCourseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        medSessionCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        medStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Status color coding
        medStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Approved" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "Pending"  -> setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    case "Denied"   -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    default         -> setStyle("");
                }
            }
        });

        medicalTableView.getColumns().addAll(medStuIdCol, medStuNameCol, medDateCol,
                medCourseCol, medSessionCol, medStatusCol, medActionCol);
        medicalTableView.setItems(medicalList);
    }

    private void loadAllMedicalRecords() {
        // Load medical records for students in officer's department
        List<MedicalRecord> records = medicalDAO.getMedicalRecordsByDepartment(officerDepartment);
        Platform.runLater(() -> {
            medicalList.setAll(records);
        });
    }

    @FXML
    private void handleMedSearch() {
        String keyword = medSearchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllMedicalRecords();
            return;
        }
        List<MedicalRecord> results = medicalDAO.searchMedicalRecords(keyword, officerDepartment);
        Platform.runLater(() -> {
            medicalList.setAll(results);
        });
    }

    @FXML
    private void handleMedRefresh() {
        medSearchField.clear();
        loadAllMedicalRecords();
    }

    private void handleMedicalDecision(MedicalRecord record, boolean approve) {
        String action = approve ? "approve" : "deny";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm " + (approve ? "Approval" : "Denial"));
        confirm.setHeaderText((approve ? "Approve" : "Deny") + " this medical record?");
        confirm.setContentText(
                "Student: " + record.getCourseName() + "\n" +  // Will need proper student name
                        "Date: " + record.getDate() + "\n" +
                        "Course: " + record.getCourseName() + "\n" +
                        "Session: " + record.getSessionType()
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = medicalDAO.updateMedicalApproval(record.getMedicalId(), approve);
            if (success) {
                showInfo("Success", "Medical record " + (approve ? "approved" : "denied") + " successfully.");
                loadAllMedicalRecords();
            } else {
                showError("Error", "Failed to update medical record. Please try again.");
            }
        }
    }

    private void showMedicalDetails(MedicalRecord record) {
        medDetailStudent.setText(record.getStudentId());
        medDetailDate.setText(record.getDate());
        medDetailCourse.setText(record.getCourseName());
        medDetailSession.setText(record.getSessionType());
        medDetailPane.setExpanded(true);
    }

    @FXML
    private void handleMedApprove() {
        MedicalRecord selected = medicalTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a medical record first.");
            return;
        }
        handleMedicalDecision(selected, true);
    }

    @FXML
    private void handleMedDeny() {
        MedicalRecord selected = medicalTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a medical record first.");
            return;
        }
        handleMedicalDecision(selected, false);
    }


    // ══════════════════════════════════════════════════════════════════════════
    // Profile Tab
    // ══════════════════════════════════════════════════════════════════════════

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

            // Store department for scoping attendance queries
            officerDepartment = d.getDepartment();
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

    // ══════════════════════════════════════════════════════════════════════════
    // Alert helpers
    // ══════════════════════════════════════════════════════════════════════════

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
