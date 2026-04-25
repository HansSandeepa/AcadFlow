package acadflow.contollers.users.lecturer;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.getterSetter.LecturerCurrentData;
import acadflow.models.users.Lecturer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Objects;
import acadflow.DAO.NoticeDAO;
import acadflow.models.Notice;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class LecturerDashboardController extends CommonUserController {

    // Profile form fields
    @FXML
    private TextField fullNameField;
    @FXML
    private ComboBox<String> departmentSelect;
    @FXML
    private TextField officeRoomFiled;
    @FXML
    private TextField genderField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea addressField;

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
        //setup user image
        Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);

        //add combo box items
        departmentSelect.getItems().addAll("ET","BST","ICT","MDS");
        //setup user(self) details
        setUsersDetails();
    }


    // Notices tab elements (READ ONLY for Lecturer)
    @FXML private TableView<Notice> noticesTableView;
    @FXML private TextField searchNoticeField;
    @FXML private Button searchNoticeButton;
    @FXML private ComboBox<String> filterAudienceCombo;
    @FXML private CheckBox showImportantOnlyCheck;
    @FXML private Label totalNoticesLabel;
    @FXML private Label importantNoticesLabel;
    @FXML private TextArea noticeDetailArea;
    @FXML private Label noticeTitleLabel;

    private NoticeDAO noticeDAO = new NoticeDAO();
    private ObservableList<Notice> noticeList = FXCollections.observableArrayList();
    private Notice selectedNotice;

    @FXML
    public void initialize() {
        initializeNoticesTab();
    }

    // NOTICES TAB - READ ONLY METHODS
    private void initializeNoticesTab() {
        // Setup audience filter
        List<String> audiences = List.of("All", "Students", "Teachers", "Staff", "Parents");
        filterAudienceCombo.setItems(FXCollections.observableArrayList(audiences));
        filterAudienceCombo.setValue("All");

        // Setup table columns
        setupNoticeTableColumns();

        // Load notices for Lecturer (Teacher audience)
        loadNoticesForLecturer();

        // Search functionality
        searchNoticeButton.setOnAction(e -> searchNotices());
        searchNoticeField.setOnAction(e -> searchNotices());

        // Filter functionality
        filterAudienceCombo.setOnAction(e -> applyNoticeFilters());
        showImportantOnlyCheck.setOnAction(e -> applyNoticeFilters());

        // Show notice details when selected
        noticesTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedNotice = newSelection;
                        displayNoticeDetails(selectedNotice);
                    }
                }
        );
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
        // Lecturer sees notices meant for "Teachers" audience
        List<Notice> notices = noticeDAO.getNoticesByAudience("Teachers");
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
            results = noticeDAO.getNoticesByAudience("Teachers");
        } else {
            results = noticeDAO.searchNoticesByTitle(keyword);
            // Filter only Teacher notices
            results.removeIf(n -> !n.getAudience().equals("Teachers") && !n.getAudience().equals("All"));
        }

        updateNoticeTable(results);
    }

    private void applyNoticeFilters() {
        String audience = filterAudienceCombo.getValue();
        boolean showImportantOnly = showImportantOnlyCheck.isSelected();

        List<Notice> filteredNotices;

        if ("All".equals(audience)) {
            filteredNotices = noticeDAO.getNoticesByAudience("Teachers");
        } else {
            filteredNotices = noticeDAO.getNoticesByAudience(audience);
        }

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
            noticesTableView.refresh();
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
            details.append("Audience: ").append(notice.getAudience()).append("\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            details.append(notice.getContent());
            noticeDetailArea.setText(details.toString());
        }
    }

    @FXML
    private void saveProfileDetails(){
        String fullName = fullNameField.getText();
        String department = departmentSelect.getValue().toString();
        String officeRoom = officeRoomFiled.getText();
        String gender = genderField.getText();
        String email = emailField.getText();
        String address = addressField.getText();

        Lecturer lecturer = new Lecturer(regNo);
        lecturer.updateProfile(fullName, department, officeRoom, gender, email, address);
    }

    private void setUsersDetails(){
        ArrayList<LecturerCurrentData> lecturerDetails = new Lecturer(regNo).getCurrentSelfDetails();
        if (lecturerDetails != null && !lecturerDetails.isEmpty()) {
            fullNameField.setText(lecturerDetails.get(0).getFullName());
            departmentSelect.setValue(lecturerDetails.get(0).getDepartment());
            officeRoomFiled.setText(lecturerDetails.get(0).getOfficeRoom());
            genderField.setText(lecturerDetails.get(0).getGender());
            emailField.setText(lecturerDetails.get(0).getEmail());
            addressField.setText(lecturerDetails.get(0).getAddress());
        } else {
            System.out.println("\u001B[31mERROR: No lecturer details found for registration number: " + regNo + "\u001B[0m");
        }
    }

    @FXML
    @Override
    protected void cancelSelfFormDetails(){
        fullNameField.setText("");
        officeRoomFiled.setText("");
        genderField.setText("");
        emailField.setText("");
        addressField.setText("");
    }
}
