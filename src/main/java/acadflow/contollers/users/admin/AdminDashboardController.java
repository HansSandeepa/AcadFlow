package acadflow.contollers.users.admin;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.DisplayTimeTable;
import acadflow.models.users.Admin;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import acadflow.DAO.NoticeDAO;
import acadflow.DAO.DisplayUserDAO;
import acadflow.models.Notice;
import acadflow.models.DisplayUser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class AdminDashboardController extends CommonUserController {
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

//        notice section
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
        @FXML private Button clearFormButton;

        //  user section
        @FXML private ComboBox<String> userTypeCombo;
        @FXML private TextField searchUserField;
        @FXML private Button addUserButton;
        @FXML private Button editUserButton;
        @FXML private Button deleteUserButton;
        @FXML private Button refreshUsersButton;
        @FXML private Button searchUserButton;
        @FXML private TableView<DisplayUser> usersTable;

        //  statistics labels
        @FXML private Label totalUsersLabel;
        @FXML private Label studentsCountLabel;
        @FXML private Label teachersCountLabel;
        @FXML private Label staffCountLabel;
        @FXML private Label adminsCountLabel;
        @FXML private Label statusLabel;
        @FXML private Label recordCountLabel;
        @FXML private Label totalNoticesLabel;
        @FXML private Label totalUsersDashboardLabel;
        @FXML private Label importantNoticesLabel;

        // TIMETABLE SECTION
        @FXML private ComboBox<String> timetableDepartmentCombo;
        @FXML private ComboBox<String> timetableSemesterCombo;
        @FXML private Button generateTimetableButton;
        @FXML private Button deleteTimetableButton;
        @FXML private TableView<DisplayTimeTable> timetableTable;

        @Override
        protected void initializeWithUserData(){
            userRegNo.setText(regNo);
            username.setText(nameOfUser);
            //setup user image
            Image userProfilePic = new Image(Objects.requireNonNull(getClass().getResourceAsStream(userImagePath)));
            userImg.setImage(userProfilePic);
            userMainImage.setImage(userProfilePic);
        }

        @FXML
        private void onLogoutBtnClick() {
            new Admin().logout(logoutBtn);
        }

        private NoticeDAO noticeDAO = new NoticeDAO();
        private DisplayUserDAO displayUserDAO = new DisplayUserDAO();

        private ObservableList<Notice> noticeList = FXCollections.observableArrayList();
        private ObservableList<DisplayUser> userList = FXCollections.observableArrayList();
        private ObservableList<DisplayTimeTable> tableList = FXCollections.observableArrayList();


        private Notice selectedNotice;
        private DisplayUser selectedDisplayUser;
        private DisplayTimeTable selectedDisplayTimeTable;

        @FXML
        public void initialize() {
            initializeNoticeSection();
            initializeUserSection();
            updateDashboardStatistics();
            initializerTimeTableSection();
        }

        //  notice section
        private void initializeNoticeSection() {
            List<String> audiences = List.of("All", "Students", "Teachers", "Staff", "Parents");
            if (noticeAudienceCombo != null) {
                noticeAudienceCombo.setItems(FXCollections.observableArrayList(audiences));
            }
            if (filterAudienceCombo != null) {
                filterAudienceCombo.setItems(FXCollections.observableArrayList(audiences));
                filterAudienceCombo.setValue("All");
            }

            setupNoticeTableColumns();
            loadAllNotices();

            if (noticesTable != null) {
                noticesTable.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldSelection, newSelection) -> {
                            if (newSelection != null) {
                                selectedNotice = newSelection;
                                populateNoticeFields(selectedNotice);
                                if (editNoticeButton != null) editNoticeButton.setDisable(false);
                                if (deleteNoticeButton != null) deleteNoticeButton.setDisable(false);
                            } else {
                                if (editNoticeButton != null) editNoticeButton.setDisable(true);
                                if (deleteNoticeButton != null) deleteNoticeButton.setDisable(true);
                            }
                        }
                );
            }

            if (searchButton != null) {
                searchButton.setOnAction(e -> searchNotices());
            }
            if (searchNoticeField != null) {
                searchNoticeField.setOnAction(e -> searchNotices());
            }
            if (filterAudienceCombo != null) {
                filterAudienceCombo.setOnAction(e -> applyNoticeFilters());
            }
            if (showImportantOnlyCheck != null) {
                showImportantOnlyCheck.setOnAction(e -> applyNoticeFilters());
            }

            if (editNoticeButton != null) editNoticeButton.setDisable(true);
            if (deleteNoticeButton != null) deleteNoticeButton.setDisable(true);
        }

        private void setupNoticeTableColumns() {
            if (noticesTable == null) return;
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

        private void loadAllNotices() {
            List<Notice> notices = noticeDAO.getAllNotices();
            updateNoticeTable(notices);
            if (totalNoticesLabel != null) {
                totalNoticesLabel.setText(String.valueOf(notices.size()));
            }
            if (importantNoticesLabel != null) {
                int importantCount = (int) notices.stream().filter(Notice::isImportant).count();
                importantNoticesLabel.setText(String.valueOf(importantCount));
            }
        }

        private void updateNoticeTable(List<Notice> notices) {
            Platform.runLater(() -> {
                noticeList.clear();
                noticeList.addAll(notices);
                noticesTable.refresh();
            });
        }

        @FXML
        private void searchNotices() {
            String keyword = searchNoticeField.getText().trim();
            if (keyword.isEmpty()) {
                loadAllNotices();
            } else {
                List<Notice> searchResults = noticeDAO.searchNoticesByTitle(keyword);
                updateNoticeTable(searchResults);
                if (statusLabel != null) {
                    statusLabel.setText("Found " + searchResults.size() + " notices matching '" + keyword + "' at " + LocalTime.now());
                }
            }
        }

        private void applyNoticeFilters() {
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

        private void populateNoticeFields(Notice notice) {
            noticeTitleField.setText(notice.getTitle());
            noticeContentArea.setText(notice.getContent());
            noticeAudienceCombo.setValue(notice.getAudience());
            importantNoticeCheck.setSelected(notice.isImportant());
        }

        @FXML
        private void publishNotice() {
            if (!validateNoticeFields()) return;

            Notice newNotice = new Notice(0,
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
                applyNoticeFilters();
                loadAllNotices(); // Refresh statistics
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
            if (!validateNoticeFields()) return;

            selectedNotice.setTitle(noticeTitleField.getText().trim());
            selectedNotice.setContent(noticeContentArea.getText().trim());
            selectedNotice.setAudience(noticeAudienceCombo.getValue());
            selectedNotice.setImportant(importantNoticeCheck.isSelected());

            if (noticeDAO.updateNotice(selectedNotice)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Notice updated successfully!");
                clearNoticeFields();
                applyNoticeFilters();
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
                    applyNoticeFilters();
                    loadAllNotices(); // Refresh statistics
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete notice!");
                }
            }
        }

        @FXML
        private void clearForm() {
            clearNoticeFields();
            if (statusLabel != null) {
                statusLabel.setText("Form cleared at " + LocalTime.now());
            }
            showAlert(Alert.AlertType.INFORMATION, "Form Cleared", "Notice form has been cleared!");
        }

        private boolean validateNoticeFields() {
            if (noticeTitleField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a notice title.");
                return false;
            }
            if (noticeContentArea.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter notice content.");
                return false;
            }
            if (noticeAudienceCombo.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an audience.");
                return false;
            }
            return true;
        }

        private void clearNoticeFields() {
            noticeTitleField.clear();
            noticeContentArea.clear();
            noticeAudienceCombo.setValue(null);
            importantNoticeCheck.setSelected(false);
            selectedNotice = null;
            noticesTable.getSelectionModel().clearSelection();
            editNoticeButton.setDisable(true);
            deleteNoticeButton.setDisable(true);
        }

        //  USER SECTION 
        private void initializeUserSection() {
            if (userTypeCombo == null) return;
            userTypeCombo.setItems(FXCollections.observableArrayList(
                    "All", "Student", "Lecturer", "Technical_Officer", "Admin"
            ));
            userTypeCombo.setValue("All");

            setupUserTableColumns();

            if (usersTable != null) {
                usersTable.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldSelection, newSelection) -> {
                            selectedDisplayUser = newSelection;
                            boolean isSelected = newSelection != null;
                            if (editUserButton != null) editUserButton.setDisable(!isSelected);
                            if (deleteUserButton != null) deleteUserButton.setDisable(!isSelected);
                        }
                );
            }

            if (userTypeCombo != null) {
                userTypeCombo.setOnAction(e -> filterDisplayUsers());
            }
            if (searchUserField != null) {
                searchUserField.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.isEmpty()) {
                        filterDisplayUsers();
                    }
                });
            }

            if (editUserButton != null) editUserButton.setDisable(true);
            if (deleteUserButton != null) deleteUserButton.setDisable(true);

            updateUserStatistics();
        }



        private void setupUserTableColumns() {
            if (usersTable == null) return;
            usersTable.getColumns().clear();

            TableColumn<DisplayUser, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
            idCol.setPrefWidth(60);

            TableColumn<DisplayUser, String> nameCol = new TableColumn<>("Full Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullname"));
            nameCol.setPrefWidth(180);

            TableColumn<DisplayUser, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            emailCol.setPrefWidth(200);

            TableColumn<DisplayUser, String> addressCol = new TableColumn<>("Address");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
            addressCol.setPrefWidth(150);

            TableColumn<DisplayUser, LocalDate> dobCol = new TableColumn<>("Date of Birth");
            dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
            dobCol.setPrefWidth(100);

            TableColumn<DisplayUser, String> genderCol = new TableColumn<>("Gender");
            genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
            genderCol.setPrefWidth(70);

//            TableColumn<DisplayUser, String> typeCol = new TableColumn<>("User Type");
//            typeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
//            typeCol.setPrefWidth(100);

//            TableColumn<DisplayUser, String> pictureCol = new TableColumn<>("Profile Picture");
//            pictureCol.setCellValueFactory(new PropertyValueFactory<>("profilePicture"));
//            pictureCol.setPrefWidth(120);

            usersTable.getColumns().addAll(idCol, nameCol, emailCol, addressCol,
                    dobCol, genderCol/*, typeCol*//*, pictureCol*/);
            usersTable.setItems(userList);
        }

        private void loadAllDisplayUsers() {
            List<DisplayUser> users = displayUserDAO.getAllDisplayUsers();
            updateUserTable(users);
            updateUserStatistics();
        }

        private void updateUserTable(List<DisplayUser> users) {
            Platform.runLater(() -> {
                userList.clear();
                userList.addAll(users);
                usersTable.refresh();
                if (recordCountLabel != null) {
                    recordCountLabel.setText("Showing " + users.size() + " records");
                }
            });
        }


        private void filterDisplayUsers() {
            String userType = userTypeCombo.getValue();
            List<DisplayUser> users;

            if (userType == null || userType.equals("All")) {
                users = displayUserDAO.getAllDisplayUsers();
            } else {
                users = displayUserDAO.getDisplayUsersByType(userType);
            }

            updateUserTable(users);
            updateUserStatistics();

            if (statusLabel != null) {
                statusLabel.setText("Filtered by: " + userType + " at " + LocalTime.now());
            }
        }


    @FXML
        private void searchUsers() {
            String keyword = searchUserField.getText().trim();
            if (keyword.isEmpty()) {
                filterDisplayUsers();
            } else {
                List<DisplayUser> searchResults = displayUserDAO.searchDisplayUsers(keyword);
                updateUserTable(searchResults);
                if (statusLabel != null) {
                    statusLabel.setText("Found " + searchResults.size() + " users matching '" + keyword + "' at " + LocalTime.now());
                }
                if (recordCountLabel != null) {
                    recordCountLabel.setText("Found " + searchResults.size() + " records");
                }
            }
        }

        @FXML
        private void addUser() {
            Dialog<DisplayUser> dialog = createDisplayUserDialog("Add New User", null);
            Optional<DisplayUser> result = dialog.showAndWait();
            result.ifPresent(user -> {
                if (displayUserDAO.addDisplayUser(user)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                    filterDisplayUsers();
                    updateDashboardStatistics();
                    if (statusLabel != null) {
                        statusLabel.setText("User added: " + user.getFullname() + " at " + LocalTime.now());
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user!");
                }
            });
        }

        @FXML
        private void editUser() {
            if (selectedDisplayUser == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to edit.");
                return;
            }

            Dialog<DisplayUser> dialog = createDisplayUserDialog("Edit User", selectedDisplayUser);
            Optional<DisplayUser> result = dialog.showAndWait();
            result.ifPresent(updatedUser -> {
                updatedUser.setUserId(selectedDisplayUser.getUserId());
                if (displayUserDAO.updateDisplayUser(updatedUser)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                    filterDisplayUsers();
                    updateDashboardStatistics();
                    if (statusLabel != null) {
                        statusLabel.setText("User updated: " + updatedUser.getFullname() + " at " + LocalTime.now());
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user!");
                }
            });
        }

        @FXML
        private void deleteUser() {
            if (selectedDisplayUser == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to delete.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete User");
            alert.setContentText("Are you sure you want to delete user: \"" + selectedDisplayUser.getFullname() + "\"?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                if (displayUserDAO.deleteDisplayUser(selectedDisplayUser.getUserId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                    filterDisplayUsers();
                    updateDashboardStatistics();
                    selectedDisplayUser = null;
                    if (statusLabel != null) {
                        statusLabel.setText("User deleted at " + LocalTime.now());
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user!");
                }
            }
        }


        @FXML
        private void refreshUsers() {
            filterDisplayUsers();
            updateDashboardStatistics();
            if (statusLabel != null) {
                statusLabel.setText("User list refreshed at " + LocalTime.now());
            }
            showAlert(Alert.AlertType.INFORMATION, "Refreshed", "User list has been refreshed!");
        }

        private void updateUserStatistics() {
            try {
                int total = displayUserDAO.getAllDisplayUsers().size();
                int students = displayUserDAO.getDisplayUserCountByType("Student");
                int teachers = displayUserDAO.getDisplayUserCountByType("Teacher");
                int staff = displayUserDAO.getDisplayUserCountByType("Staff");
                int admins = displayUserDAO.getDisplayUserCountByType("Admin");

                Platform.runLater(() -> {
                    if (totalUsersLabel != null) totalUsersLabel.setText("Total Users: " + total);
                    if (studentsCountLabel != null) studentsCountLabel.setText("Students: " + students);
                    if (teachersCountLabel != null) teachersCountLabel.setText("Teachers: " + teachers);
                    if (staffCountLabel != null) staffCountLabel.setText("Staff: " + staff);
                    if (adminsCountLabel != null) adminsCountLabel.setText("Admins: " + admins);
                });
            } catch (Exception e) {
                System.err.println("Error updating user statistics: " + e.getMessage());
            }
        }

        private void updateDashboardStatistics() {
            try {
                int totalUsers = displayUserDAO.getAllDisplayUsers().size();
                int totalNotices = noticeDAO.getAllNotices().size();
                int importantNotices = noticeDAO.getImportantNotices().size();

                Platform.runLater(() -> {
                    if (totalUsersDashboardLabel != null) totalUsersDashboardLabel.setText(String.valueOf(totalUsers));
                    if (totalNoticesLabel != null) totalNoticesLabel.setText(String.valueOf(totalNotices));
                    if (importantNoticesLabel != null) importantNoticesLabel.setText(String.valueOf(importantNotices));
                });
            } catch (Exception e) {
                System.err.println("Error updating dashboard statistics: " + e.getMessage());
            }
        }

        private Dialog<DisplayUser> createDisplayUserDialog(String title, DisplayUser existingUser) {
            Dialog<DisplayUser> dialog = new Dialog<>();
            dialog.setTitle(title);
            dialog.setHeaderText(null);

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField fullnameField = new TextField();
            TextField emailField = new TextField();
            TextField addressField = new TextField();
            DatePicker dobPicker = new DatePicker();
            ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("M", "F"));
            PasswordField passwordField = new PasswordField();
            ComboBox<String> userTypeCombo = new ComboBox<>(FXCollections.observableArrayList("Student", "Teacher", "Staff", "Admin"));
            TextField profilePictureField = new TextField("/profile_pics/default_pic.jpg");

            if (existingUser != null) {
                fullnameField.setText(existingUser.getFullname());
                emailField.setText(existingUser.getEmail());
                addressField.setText(existingUser.getAddress());
                dobPicker.setValue(existingUser.getDob());
                genderCombo.setValue(existingUser.getGender());
                passwordField.setText(existingUser.getPassword());
                userTypeCombo.setValue(existingUser.getUserType());
                profilePictureField.setText(existingUser.getProfilePicture());
            }

            grid.add(new Label("Full Name:"), 0, 0);
            grid.add(fullnameField, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(emailField, 1, 1);
            grid.add(new Label("Address:"), 0, 2);
            grid.add(addressField, 1, 2);
            grid.add(new Label("Date of Birth:"), 0, 3);
            grid.add(dobPicker, 1, 3);
            grid.add(new Label("Gender:"), 0, 4);
            grid.add(genderCombo, 1, 4);
            grid.add(new Label("Password:"), 0, 5);
            grid.add(passwordField, 1, 5);
            grid.add(new Label("User Type:"), 0, 6);
            grid.add(userTypeCombo, 1, 6);
//            grid.add(new Label("Profile Picture:"), 0, 7);
//            grid.add(profilePictureField, 1, 7);

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(() -> fullnameField.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    return new DisplayUser(
                            existingUser != null ? existingUser.getUserId() : 0,
                            fullnameField.getText(),
                            addressField.getText(),
                            dobPicker.getValue(),
                            genderCombo.getValue(),
                            passwordField.getText(),
                            emailField.getText(),
                            profilePictureField.getText(),
                            userTypeCombo.getValue()
                    );
                }
                return null;
            });

            return dialog;
        }

        //  COMMON METHODS 
        private String getCurrentAdminId() {
            // TODO: Get from session or login context
            return "ADMIN001";
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


/***************************************************************************************************/

        //FILTER TIME TABLE BY DEPARTMENT
        private void filterDisplayTimeTable() {
            String dept = timetableDepartmentCombo.getValue();
            String sem = timetableSemesterCombo.getValue();
            List<DisplayTimeTable> tables;

            if ((dept == null || dept.equals("ALL")) &&
                    (sem == null || sem.isEmpty())) {

                tables = displayUserDAO.getAllDisplayTimeTable();

            } else if (dept != null && !dept.equals("ALL") &&
                    sem != null && !sem.isEmpty()) {

                tables = displayUserDAO.getDisplayTablesByDept(dept, sem);

            } else if (dept != null && !dept.equals("ALL")) {

                tables = displayUserDAO.getDisplayTablesByDept(dept, sem);

            } else {
                tables = displayUserDAO.getAllDisplayTimeTable();
            }

            tableList.setAll(tables);
        }

        //ADD TIMETABLE
        @FXML
        private void addTimeTable() {
            Dialog<DisplayTimeTable> dialog = createTimetableDialog("Add New Time Table", null);
            Optional<DisplayTimeTable> result = dialog.showAndWait();
            result.ifPresent(time_table -> {
                if (displayUserDAO.addDisplayTimeTable(time_table)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Time table added successfully!");
                    filterDisplayTimeTable();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add Time Table!(Please enter the validate data)");
                }
            });
        }

        //TIMETABLE FORM
        private Dialog<DisplayTimeTable> createTimetableDialog(String title, DisplayTimeTable existingTable) {
            Dialog<DisplayTimeTable> dialog = new Dialog<>();
            dialog.setTitle(title);
            dialog.setHeaderText(null);

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField timetableidField = new TextField();
            ComboBox<String> dayCombo = new ComboBox<>(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            TextField timeField = new TextField();
            ComboBox<String> levelSemCol = new ComboBox<>(FXCollections.observableArrayList("Level 1 Semester 1", "Level 1 Semester 2", "Level 2 Semester 1", "Level 2 Semester 2", "Level 3 Semester 1", "Level 3 Semester 2", "Level 4 Semester 1", "Level 4 Semester 2"));
            ComboBox<String> SessionCombo = new ComboBox<>(FXCollections.observableArrayList("T", "P", "Both"));
            ComboBox<String> deptCombo = new ComboBox<>(FXCollections.observableArrayList("ICT", "ET", "BST"));
            TextField courseldField = new TextField();
            TextField adminidField = new TextField();


            if (existingTable != null) {
                timetableidField.setText(String.valueOf(existingTable.getTimetableId()));
                dayCombo.setValue(existingTable.getDay());
                timeField.setText(String.valueOf(existingTable.getTime()));
                levelSemCol.setValue(existingTable.getLevelAndSem());
                SessionCombo.setValue(existingTable.getSessionType());
                deptCombo.setValue(existingTable.getDept());
                courseldField.setText(existingTable.getCourseId());
                adminidField.setText(existingTable.getAdminId());
            }

            grid.add(new Label("Timetable_ Id:"), 0, 0);
            grid.add(timetableidField, 1, 0);
            grid.add(new Label("Day:"), 0, 1);
            grid.add(dayCombo, 1, 1);
            grid.add(new Label("Time:"), 0, 2);
            grid.add(timeField, 1, 2);
            grid.add(new Label("Level_and_Semester:"), 0, 3);
            grid.add(levelSemCol, 1, 3);
            grid.add(new Label("Session_Type:"), 0, 4);
            grid.add(SessionCombo, 1, 4);
            grid.add(new Label("Department:"), 0, 5);
            grid.add(deptCombo, 1, 5);
            grid.add(new Label("Course_id:"), 0, 6);
            grid.add(courseldField, 1, 6);
            grid.add(new Label("Admin_id:"), 0, 7);
            grid.add(adminidField, 1, 7);

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(() -> timetableidField.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    return new DisplayTimeTable(
                            timetableidField.getText(),
                            dayCombo.getValue(),
                            timeField.getText(),
                            levelSemCol.getValue(),
                            SessionCombo.getValue(),
                            deptCombo.getValue(),
                            courseldField.getText(),
                            adminidField.getText()
                    );
                }
                return null;
            });

            return dialog;
        }

        //DEPARTMENT SECTION, LEVEL, AND SEMESTER FOR TIME TABLE
        private void initializerTimeTableSection() {
            if (timetableDepartmentCombo == null) return;
            timetableDepartmentCombo.setItems(FXCollections.observableArrayList("ALL", "ICT", "ET", "BST"));
            timetableDepartmentCombo.setValue("ALL");

            if (timetableSemesterCombo == null) return;
            timetableSemesterCombo.setItems(FXCollections.observableArrayList("Level 1 Semester 1", "Level 1 Semester 2", "Level 2 Semester 1", "Level 2 Semester 2", "Level 3 Semester 1", "Level 3 Semester 2", "Level 4 Semester 1", "Level 4 Semester 2"));
            timetableSemesterCombo.setValue("Level 1 Semester I");

            setupTimeTableColumns();

            if (timetableTable != null) {
                timetableTable.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldSelection, newSelection) -> {
                            selectedDisplayTimeTable = newSelection;
                            boolean isSelected = newSelection != null;
                            // if (deleteUserButton != null) deleteUserButton.setDisable(!isSelected); //create delete button to time table
                        }
                );
            }

            if (timetableDepartmentCombo != null) {
                timetableDepartmentCombo.setOnAction(e -> filterDisplayTimeTable());
            }

            if (timetableSemesterCombo != null) {
                timetableSemesterCombo.setOnAction(e -> filterDisplayTimeTable());
            }
        }

        //SETUP TIME TABLE COLUMNS
        private void setupTimeTableColumns() {
            if (timetableTable == null) return;
            timetableTable.getColumns().clear();

            TableColumn<DisplayTimeTable, String> tableidCol = new TableColumn<>("Timetable_id");
            tableidCol.setCellValueFactory(new PropertyValueFactory<>("timetableId"));
            tableidCol.setPrefWidth(200);

            TableColumn<DisplayTimeTable, String> dayCol = new TableColumn<>("Day");
            dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
            dayCol.setPrefWidth(180);

            TableColumn<DisplayTimeTable, String> timeCol = new TableColumn<>("Time");
            timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
            timeCol.setPrefWidth(200);

            TableColumn<DisplayTimeTable, String> levelSemCol = new TableColumn<>("Level_and_Semester");
            levelSemCol.setCellValueFactory(new PropertyValueFactory<>("levelAndSem"));
            levelSemCol.setPrefWidth(200);

            TableColumn<DisplayTimeTable, String> sessionTypeCol = new TableColumn<>("Session_type");
            sessionTypeCol.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
            sessionTypeCol.setPrefWidth(150);

            TableColumn<DisplayTimeTable, String> deptCol = new TableColumn<>("Department");
            deptCol.setCellValueFactory(new PropertyValueFactory<>("dept"));
            deptCol.setPrefWidth(100);

            TableColumn<DisplayTimeTable, String> courseIdCol = new TableColumn<>("Course_id");
            courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
            courseIdCol.setPrefWidth(70);

            TableColumn<DisplayTimeTable, String> adminIdCol = new TableColumn<>("Admin_id");
            adminIdCol.setCellValueFactory(new PropertyValueFactory<>("adminId"));
            adminIdCol.setPrefWidth(70);

            timetableTable.getColumns().addAll(tableidCol, dayCol, timeCol, levelSemCol, sessionTypeCol, deptCol, courseIdCol, adminIdCol);
            timetableTable.setItems(tableList);
        }

        //TABLE DELETE FUNCTION
        @FXML
        private void deleteTable() {
            if (selectedDisplayTimeTable == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please Select a Timetable To Delete.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Table");
            alert.setContentText("Are you sure you want to delete Table: \"" + selectedDisplayTimeTable.getTimetableId() + "\"?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                if (displayUserDAO.deleteDisplayTable(selectedDisplayTimeTable.getTimetableId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Table Row Deleted Successfully!");
                    filterDisplayTimeTable();
                    selectedDisplayTimeTable = null;

                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed To Delete Table Raw!");
                }
            }
        }

}

