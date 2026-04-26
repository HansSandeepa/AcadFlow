package acadflow.contollers.users.undergraduate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import acadflow.DAO.ExamMarkDAO;
import acadflow.DAO.Notice;
import acadflow.DAO.NoticeDAO;
import acadflow.contollers.users.CommonUserController;
import acadflow.models.ExamMark;
import acadflow.models.getterSetter.UndergraduateCurrentData;
import acadflow.models.users.Undergraduate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for the Undergraduate dashboard.
 *
 * INHERITANCE  : extends CommonUserController — inherits regNo, nameOfUser,
 *                userImagePath, userId, logoutBtn, userRegNo, username,
 *                userImg, userMainImage, setUserDetails(), uploadProfileImage().
 *
 * POLYMORPHISM : overrides initializeWithUserData() and cancelSelfFormDetails()
 *                from CommonUserController. The JVM resolves these at runtime
 *                to the undergraduate-specific implementations below.
 *
 * ENCAPSULATION: all FXML fields and helper methods are private.
 *                External code only calls setUserDetails() (inherited).
 *
 * ABSTRACTION  : CommonUserController is abstract — it defines the contract
 *                that this class fulfils.
 *
 * Features implemented:
 *   • My Profile  — view and update own profile details
 *   • My Courses & Grades — live marks, GPA, eligibility from exam_marks table
 *   • Notices     — student-only notices from notice table
 */
public class UndergraduateDashboardController extends CommonUserController {

    // ── Profile tab fields ────────────────────────────────────────────────────
    @FXML private Label    fullName;
    @FXML private Label    regNoLabel;
    @FXML private Label    departmentNo;
    @FXML private Label    batchNo;
    @FXML private TextField emailField;
    @FXML private TextArea  addressField;
    @FXML private Label    genderField;

    // ── Grades tab fields ─────────────────────────────────────────────────────
    // fx:id="gpaLabel"         — shows computed weighted GPA
    @FXML private Label gpaLabel;
    // fx:id="totalMarksLabel"  — shows total number of mark records
    @FXML private Label totalMarksLabel;
    // fx:id="marksTableView"   — table of all exam_marks rows for this student
    @FXML private TableView<ExamMark> marksTableView;
    // Column bindings — matched to ExamMark JavaFX properties
    @FXML private TableColumn<ExamMark, String>  gradesCourseCodeCol;
    @FXML private TableColumn<ExamMark, String>  gradesCourseNameCol;
    @FXML private TableColumn<ExamMark, Integer> gradesCreditsCol;
    @FXML private TableColumn<ExamMark, String>  gradesAssessmentCol;
    @FXML private TableColumn<ExamMark, String>  gradesSessionCol;
    @FXML private TableColumn<ExamMark, Integer> gradesMarkCol;
    @FXML private TableColumn<ExamMark, String>  gradesGradeCol;
    // fx:id="eligibilityBox"   — VBox; rows added programmatically
    @FXML private VBox eligibilityBox;

    // ── Notices tab fields ────────────────────────────────────────────────────
    @FXML private TableView<Notice> noticesTableView;
    @FXML private TextField         searchNoticeField;
    @FXML private Button            searchNoticeButton;
    @FXML private CheckBox          showImportantOnlyCheck;
    @FXML private Label             totalNoticesLabel;
    @FXML private Label             importantNoticesLabel;
    @FXML private TextArea          noticeDetailArea;
    @FXML private Label             noticeTitleLabel;

    // ── Private state (ENCAPSULATION) ─────────────────────────────────────────
    private final ExamMarkDAO examMarkDAO = new ExamMarkDAO();
    private final NoticeDAO   noticeDAO   = new NoticeDAO();
    private final ObservableList<ExamMark> markList   = FXCollections.observableArrayList();
    private final ObservableList<Notice>   noticeList = FXCollections.observableArrayList();
    private Notice selectedNotice;

    // ── JavaFX lifecycle ──────────────────────────────────────────────────────

    /**
     * Called by JavaFX after FXML injection but BEFORE setUserDetails().
     * Only wire up things that do NOT need regNo here.
     * Grades and notices are loaded in initializeWithUserData() once regNo is set.
     */
    @FXML
    public void initialize() {
        // ENCAPSULATION: notice tab wired here; data loaded later in initializeWithUserData()
        initializeNoticesTab();
    }

    /**
     * POLYMORPHISM: overrides CommonUserController.initializeWithUserData().
     * Called by setUserDetails() after regNo is injected.
     * Loads profile, grades, and notices for this specific student.
     */
    @Override
    public void initializeWithUserData() {
        // Inherited fields from CommonUserController
        userRegNo.setText(regNo);
        username.setText(nameOfUser);

        // Setup profile image (inherited fields: userImg, userMainImage, userImagePath)
        Image userProfilePic = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);

        // Load profile details into the My Profile tab
        setUsersDetails();

        // Load grades and GPA into the My Courses & Grades tab
        // ABSTRACTION: loadGradesForStudent() hides all DB + GPA calculation logic
        loadGradesForStudent();
    }

    /**
     * POLYMORPHISM: overrides CommonUserController.cancelSelfFormDetails().
     * Clears the undergraduate profile form fields.
     */
    @FXML
    @Override
    protected void cancelSelfFormDetails() {
        emailField.setText("");
        addressField.setText("");
    }

    // ── Profile tab ───────────────────────────────────────────────────────────

    /**
     * Saves updated email and address to the database.
     * ENCAPSULATION: Undergraduate model handles the SQL internally.
     */
    @FXML
    private void saveProfileDetails() {
        String email   = emailField.getText();
        String address = addressField.getText();
        Undergraduate undergraduate = new Undergraduate(regNo);
        undergraduate.updateProfile(email, address);
    }

    /**
     * Loads the current student's details from the DB and populates the form.
     * ENCAPSULATION: all DB logic is inside Undergraduate model.
     */
    private void setUsersDetails() {
        ArrayList<UndergraduateCurrentData> details =
                new Undergraduate(regNo).getCurrentSelfDetails();

        if (details != null && !details.isEmpty()) {
            String gender = details.get(0).getGender().equalsIgnoreCase("M") ? "Male" : "Female";
            genderField.setText(gender);
            fullName.setText(details.get(0).getFullName());
            regNoLabel.setText(regNo);
            departmentNo.setText(details.get(0).getDepartment());
            batchNo.setText(details.get(0).getBatch());
            emailField.setText(details.get(0).getEmail());
            addressField.setText(details.get(0).getAddress());
        } else {
            System.out.println("\u001B[31mERROR: No undergraduate details found for: " + regNo + "\u001B[0m");
        }
    }

    // ── Grades tab ────────────────────────────────────────────────────────────

    /**
     * Sets up the marks TableView columns and loads all exam_marks rows
     * for this student, then computes and displays the weighted GPA.
     *
     * ABSTRACTION: ExamMarkDAO.getMarksByStudent() hides the SQL JOIN.
     * ENCAPSULATION: markList is private; only this method populates it.
     */
    private void loadGradesForStudent() {
        if (marksTableView == null) return;

        // Bind each column to the matching JavaFX property in ExamMark
        // ENCAPSULATION: ExamMark fields are private; PropertyValueFactory
        //                accesses them through the property accessors.
        gradesCourseCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        gradesCourseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        gradesCreditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        gradesAssessmentCol.setCellValueFactory(new PropertyValueFactory<>("assessmentType"));
        gradesSessionCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        gradesMarkCol.setCellValueFactory(new PropertyValueFactory<>("mark"));
        gradesGradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        marksTableView.setItems(markList);

        // Load from DB on a background thread to keep UI responsive
        new Thread(() -> {
            // ABSTRACTION: DAO hides the SQL; we just get a list back
            List<ExamMark> marks = examMarkDAO.getMarksByStudent(regNo);

            // Compute weighted GPA: sum(gradePoint * credits) / sum(credits)
            double weightedSum = 0;
            int    totalCredits = 0;
            for (ExamMark m : marks) {
                double gp = ExamMark.gradeToGpaPoint(m.getGrade());
                weightedSum  += gp * m.getCredits();
                totalCredits += m.getCredits();
            }
            double gpa = totalCredits > 0 ? weightedSum / totalCredits : 0.0;
            String gpaText = String.format("%.2f", gpa);

            int total = marks.size();

            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
                markList.setAll(marks);
                marksTableView.refresh();

                if (gpaLabel != null)        gpaLabel.setText(gpaText);
                if (totalMarksLabel != null) totalMarksLabel.setText(String.valueOf(total));

                // Build eligibility rows dynamically
                buildEligibilityRows(marks);
            });
        }).start();
    }

    /**
     * Populates the eligibility VBox with one row per mark record.
     * A student is eligible if their CA (Assessment_type='T') mark >= 40.
     *
     * ENCAPSULATION: eligibilityBox children are managed only here.
     * ABSTRACTION: ExamMark.isEligible() encapsulates the eligibility rule.
     */
    private void buildEligibilityRows(List<ExamMark> marks) {
        if (eligibilityBox == null) return;
        eligibilityBox.getChildren().clear();

        if (marks.isEmpty()) {
            eligibilityBox.getChildren().add(
                    new Label("No marks uploaded yet."));
            return;
        }

        for (ExamMark m : marks) {
            // ABSTRACTION: isEligible() hides the CA >= 40 rule
            boolean eligible = m.isEligible();
            String symbol = eligible ? "✓" : "✗";
            String color  = eligible ? "#27ae60" : "#e74c3c";
            String status = eligible ? "Eligible" : "Not Eligible";

            String text = String.format("%s (%s) — CA Mark: %d  %s %s",
                    m.getCourseName().isEmpty() ? m.getCourseId() : m.getCourseName(),
                    m.getCourseId(),
                    m.getMark(),
                    symbol, status);

            Label row = new Label(text);
            row.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
            eligibilityBox.getChildren().add(row);
        }
    }

    // ── Notices tab ───────────────────────────────────────────────────────────

    /**
     * Wires up the notices tab event handlers and loads student notices.
     * Called from initialize() — does not need regNo.
     *
     * ENCAPSULATION: noticeList and selectedNotice are private.
     */
    private void initializeNoticesTab() {
        if (noticesTableView == null) return;

        setupNoticeTableColumns();
        loadNoticesForStudent();

        if (searchNoticeButton != null)
            searchNoticeButton.setOnAction(e -> searchNotices());
        if (searchNoticeField != null)
            searchNoticeField.setOnAction(e -> searchNotices());
        if (showImportantOnlyCheck != null)
            showImportantOnlyCheck.setOnAction(e -> applyNoticeFilters());

        noticesTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        selectedNotice = newSel;
                        displayNoticeDetails(selectedNotice);
                    }
                });
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

    /** Loads only "Students" audience notices — undergraduates cannot see others. */
    private void loadNoticesForStudent() {
        List<Notice> notices = noticeDAO.getNoticesByAudience("Students");
        updateNoticeTable(notices);
        if (totalNoticesLabel != null)
            totalNoticesLabel.setText(String.valueOf(notices.size()));
        long imp = notices.stream().filter(Notice::isImportant).count();
        if (importantNoticesLabel != null)
            importantNoticesLabel.setText(String.valueOf(imp));
    }

    private void searchNotices() {
        String keyword = searchNoticeField.getText().trim();
        List<Notice> results = keyword.isEmpty()
                ? noticeDAO.getNoticesByAudience("Students")
                : noticeDAO.searchNoticesByTitle(keyword);
        results.removeIf(n -> !n.getAudience().equals("Students") && !n.getAudience().equals("All"));
        updateNoticeTable(results);
    }

    private void applyNoticeFilters() {
        boolean importantOnly = showImportantOnlyCheck.isSelected();
        List<Notice> filtered = noticeDAO.getNoticesByAudience("Students");
        if (importantOnly)
            filtered = filtered.stream().filter(Notice::isImportant)
                    .collect(java.util.stream.Collectors.toList());
        updateNoticeTable(filtered);
    }

    private void updateNoticeTable(List<Notice> notices) {
        Platform.runLater(() -> {
            noticeList.setAll(notices);
            if (noticesTableView != null) noticesTableView.refresh();
        });
    }

    private void displayNoticeDetails(Notice notice) {
        if (noticeTitleLabel != null) {
            noticeTitleLabel.setText(notice.isImportant()
                    ? "⚠ IMPORTANT: " + notice.getTitle()
                    : notice.getTitle());
        }
        if (noticeDetailArea != null) {
            noticeDetailArea.setText(
                    "Date: " + notice.getDate() + "\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    notice.getContent());
        }
    }
}
