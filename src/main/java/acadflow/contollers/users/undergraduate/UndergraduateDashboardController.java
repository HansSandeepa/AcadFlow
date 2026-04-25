package acadflow.contollers.users.undergraduate;

import acadflow.DAO.NoticeDAO;
import acadflow.contollers.users.CommonUserController;
import acadflow.models.getterSetter.UndergraduateCurrentData;
import acadflow.models.users.Undergraduate;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import java.util.ArrayList;
import acadflow.DAO.Notice;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

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

    @Override
    public void initializeWithUserData(){
        userRegNo.setText(regNo);
        username.setText(nameOfUser);
        //setup user image
        Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
        userImg.setImage(userProfilePic);
        userMainImage.setImage(userProfilePic);
    }

    @FXML
    @Override
    protected void cancelSelfFormDetails(){
        emailField.setText("");
        addressField.setText("");
    }

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


    @FXML
    public void initialize() {
        initializeNoticesTab();
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
}
