package acadflow.contollers.users.admin;

import acadflow.models.users.Admin;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import acadflow.DAO.NoticeDAO;
import acadflow.models.Notice;
import acadflow.models.users.Admin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private Button logoutBtn;

    @FXML
    private void onLogoutBtnClick(){
        new Admin().logout(logoutBtn);
    }

    // FXML Components
    @FXML private TextField noticeTitleField;
    @FXML private ComboBox<String> noticeAudienceCombo;
    @FXML private CheckBox importantNoticeCheck;
    @FXML private TextArea noticeContentArea;
    @FXML private Button publishNoticeButton;
    @FXML private Button editNoticeButton;
    @FXML private Button deleteNoticeButton;
    @FXML private TableView<Notice> noticesTable;
    @FXML private TextField searchNoticeField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> filterAudienceCombo;
    @FXML private CheckBox showImportantOnlyCheck;

    // Data and DAO
    private ObservableList<Notice> noticeList = FXCollections.observableArrayList();
    private NoticeDAO noticeDAO = new NoticeDAO();
    private Notice selectedNotice;

    @FXML
    public void initialize() {
        // Setup audience combo boxes
        List<String> audiences = List.of("All", "Students", "Teachers", "Staff", "Parents");
        noticeAudienceCombo.setItems(FXCollections.observableArrayList(audiences));
        filterAudienceCombo.setItems(FXCollections.observableArrayList(audiences));
        filterAudienceCombo.setValue("All");

        // Setup table columns
        setupTableColumns();

        // Load all notices
        loadAllNotices();

        // Add selection listener
        noticesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedNotice = newSelection;
                        populateNoticeFields(selectedNotice);
                        enableEditDeleteButtons(true);
                    } else {
                        enableEditDeleteButtons(false);
                    }
                }
        );

        // Setup search and filter listeners
        setupSearchAndFilter();

        // Initially disable edit/delete buttons
        enableEditDeleteButtons(false);
    }

    private void setupTableColumns() {
        noticesTable.getColumns().clear();

        TableColumn<Notice, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("noticeId"));
        idCol.setPrefWidth(60);

        TableColumn<Notice, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Notice, String> contentCol = new TableColumn<>("Content");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentCol.setPrefWidth(350);

        TableColumn<Notice, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);

        TableColumn<Notice, String> audienceCol = new TableColumn<>("Audience");
        audienceCol.setCellValueFactory(new PropertyValueFactory<>("audience"));
        audienceCol.setPrefWidth(100);

        TableColumn<Notice, Boolean> importantCol = new TableColumn<>("Important");
        importantCol.setCellValueFactory(new PropertyValueFactory<>("important"));
        importantCol.setPrefWidth(80);

        TableColumn<Notice, String> adminCol = new TableColumn<>("Admin ID");
        adminCol.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        adminCol.setPrefWidth(100);

        noticesTable.getColumns().addAll(idCol, titleCol, contentCol, dateCol,
                audienceCol, importantCol, adminCol);

        noticesTable.setItems(noticeList);
    }

    private void setupSearchAndFilter() {
        // Search button action
        searchButton.setOnAction(e -> performSearch());
        searchNoticeField.setOnAction(e -> performSearch());

        // Filter combo box action
        filterAudienceCombo.setOnAction(e -> applyFilters());
        showImportantOnlyCheck.setOnAction(e -> applyFilters());
    }

    private void performSearch() {
        String keyword = searchNoticeField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllNotices();
        } else {
            List<Notice> searchResults = noticeDAO.searchNoticesByTitle(keyword);
            updateNoticeTable(searchResults);
        }
    }

    private void applyFilters() {
        String audience = filterAudienceCombo.getValue();
        boolean showImportantOnly = showImportantOnlyCheck.isSelected();

        List<Notice> filteredNotices;

        if (showImportantOnly) {
            filteredNotices = noticeDAO.getImportantNotices();
        } else if (!"All".equals(audience)) {
            filteredNotices = noticeDAO.getNoticesByAudience(audience);
        } else {
            filteredNotices = noticeDAO.getAllNotices();
        }

        updateNoticeTable(filteredNotices);
    }

    private void loadAllNotices() {
        List<Notice> notices = noticeDAO.getAllNotices();
        updateNoticeTable(notices);
    }

    private void updateNoticeTable(List<Notice> notices) {
        Platform.runLater(() -> {
            noticeList.clear();
            noticeList.addAll(notices);
            noticesTable.refresh();
        });
    }

    private void populateNoticeFields(Notice notice) {
        noticeTitleField.setText(notice.getTitle());
        noticeContentArea.setText(notice.getContent());
        noticeAudienceCombo.setValue(notice.getAudience());
        importantNoticeCheck.setSelected(notice.isImportant());
    }

    private void enableEditDeleteButtons(boolean enable) {
        editNoticeButton.setDisable(!enable);
        deleteNoticeButton.setDisable(!enable);
    }

    @FXML
    private void publishNotice() {
        if (!validateNoticeFields()) {
            return;
        }

        Notice newNotice = new Notice(
                0,
                noticeTitleField.getText().trim(),
                noticeContentArea.getText().trim(),
                LocalDate.now(),
                getCurrentAdminId(),
                noticeAudienceCombo.getValue(),
                importantNoticeCheck.isSelected()
        );

        if (noticeDAO.addNotice(newNotice)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Notice published successfully!");
            clearNoticeFields();
            applyFilters(); // Refresh with current filters
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to publish notice!");
        }
    }

    @FXML
    private void editNotice() {
        if (selectedNotice == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a notice to edit.");
            return;
        }

        if (!validateNoticeFields()) {
            return;
        }

        selectedNotice.setTitle(noticeTitleField.getText().trim());
        selectedNotice.setContent(noticeContentArea.getText().trim());
        selectedNotice.setAudience(noticeAudienceCombo.getValue());
        selectedNotice.setImportant(importantNoticeCheck.isSelected());

        if (noticeDAO.updateNotice(selectedNotice)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Notice updated successfully!");
            clearNoticeFields();
            applyFilters(); // Refresh with current filters
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update notice!");
        }
    }

    @FXML
    private void deleteNotice() {
        if (selectedNotice == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a notice to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Notice");
        alert.setContentText("Are you sure you want to delete the notice: \"" + selectedNotice.getTitle() + "\"?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (noticeDAO.deleteNotice(selectedNotice.getNoticeId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Notice deleted successfully!");
                clearNoticeFields();
                applyFilters(); // Refresh with current filters
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete notice!");
            }
        }
    }

    @FXML
    private void clearForm() {
        clearNoticeFields();
    }

    private boolean validateNoticeFields() {
        if (noticeTitleField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a notice title.");
            noticeTitleField.requestFocus();
            return false;
        }
        if (noticeContentArea.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter notice content.");
            noticeContentArea.requestFocus();
            return false;
        }
        if (noticeAudienceCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an audience.");
            noticeAudienceCombo.requestFocus();
            return false;
        }
        return true;
    }

    private String getCurrentAdminId() {
        // TODO: Get from session or login context
        // For now, return a default or get from logged-in user
        return "ADMIN001";
    }

    private void clearNoticeFields() {
        noticeTitleField.clear();
        noticeContentArea.clear();
        noticeAudienceCombo.setValue(null);
        importantNoticeCheck.setSelected(false);
        selectedNotice = null;
        noticesTable.getSelectionModel().clearSelection();
        enableEditDeleteButtons(false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

}