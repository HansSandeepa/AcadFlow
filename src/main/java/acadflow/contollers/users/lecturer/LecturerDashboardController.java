package acadflow.contollers.users.lecturer;

import acadflow.contollers.users.CommonUserController;
import acadflow.models.getterSetter.LecturerCurrentData;
import acadflow.models.users.Lecturer;
import acadflow.models.CourseMaterial;
import acadflow.models.LectureCourse;
import acadflow.util.FileStorageHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import acadflow.DAO.NoticeDAO;
import acadflow.DAO.Notice;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

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
        loadLecturerCourses();
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


    //Lecture Course Section
    @FXML private Button AddMaterialBtn;
    @FXML private Button DeleteMaterialBtn;
    @FXML private Button RefreshBtn;
    @FXML private Button ShowMaterialBtn;

    @FXML private TableView<LectureCourse> courseTableview;
    @FXML private TableColumn<LectureCourse, String> CourseCodeColomn;
    @FXML private TableColumn<LectureCourse, String> CourseNameColomn;
    @FXML private TableColumn<LectureCourse, String> CreditsColomn;

    private ObservableList<LectureCourse> courseList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        initializeCourseTable();
        initializeNoticesTab();
    }



    private void initializeCourseTable() {
        CourseCodeColomn.setCellValueFactory(cellData -> cellData.getValue().cidProperty());
        CourseNameColomn.setCellValueFactory(cellData -> cellData.getValue().cNameProperty());
        CreditsColomn.setCellValueFactory(cellData -> cellData.getValue().cCreditsProperty());

        courseTableview.setItems(courseList);

        // button handlers

        RefreshBtn.setOnAction(e -> loadLecturerCourses());
        AddMaterialBtn.setOnAction(e -> handleAddMaterial());
        DeleteMaterialBtn.setOnAction(e -> handleDeleteMaterial());
        ShowMaterialBtn.setOnAction(e -> handleShowMaterials());


        AddMaterialBtn.setDisable(true);
        DeleteMaterialBtn.setDisable(true);
        ShowMaterialBtn.setDisable(true);

        // Add listener to enable/disable buttons based on selection
        courseTableview.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean isCourseSelected = (newSelection != null);
                    AddMaterialBtn.setDisable(!isCourseSelected);
                    DeleteMaterialBtn.setDisable(!isCourseSelected);
                    ShowMaterialBtn.setDisable(!isCourseSelected);

                    if (isCourseSelected) {
                        System.out.println("Selected course: " + newSelection.getCName() + " (" + newSelection.getCid() + ")");
                    }
                }
        );
    }


    @FXML
    private void handleDeleteMaterial() {
        LectureCourse selectedCourse = courseTableview.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "No Course Selected",
                    "Please select a course first to manage its materials.");
            return;
        }

        // Get materials for this course
        ObservableList<CourseMaterial> materials = CourseMaterial.getMaterialsByCourse(selectedCourse.getCid());

        if (materials.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Materials",
                    "No materials found for " + selectedCourse.getCName() + " (" + selectedCourse.getCid() + ")");
            return;
        }

        // Create dialog to show materials for deletion
        Dialog<CourseMaterial> dialog = new Dialog<>();
        dialog.setTitle("Delete Material");
        dialog.setHeaderText("Select material to delete from: " + selectedCourse.getCName());

        // Set the button types
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

        // Style the Delete button
        Button deleteButton = (Button) dialog.getDialogPane().lookupButton(deleteButtonType);
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        // Create a ListView to display materials
        ListView<CourseMaterial> listView = new ListView<>();
        listView.setItems(materials);
        listView.setCellFactory(lv -> new ListCell<CourseMaterial>() {
            @Override
            protected void updateItem(CourseMaterial item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label nameLabel = new Label(item.getFileName());
                    Label idLabel = new Label("(ID: " + item.getMaterialId() + ")");
                    idLabel.setStyle("-fx-text-fill: #7f8c8d;");
                    hbox.getChildren().addAll(nameLabel, idLabel);
                    setGraphic(hbox);
                }
            }
        });

        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().setPrefHeight(300);

        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<CourseMaterial> result = dialog.showAndWait();

        result.ifPresent(material -> {
            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Are you sure you want to delete this material?");
            confirmAlert.setContentText("File: " + material.getFileName() + "\nCourse: " + selectedCourse.getCName() + "\n\nThis action cannot be undone.");

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {

                boolean dbDeleted = CourseMaterial.deleteMaterial(material.getMaterialId());
                boolean fileDeleted = CourseMaterial.deleteMaterialFile(material.getMaterialPath());

                if (dbDeleted && fileDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Material deleted successfully!\n\nFile: " + material.getFileName());
                    System.out.println("SUCCESS: Material deleted - ID: " + material.getMaterialId());
                } else if (dbDeleted) {
                    showAlert(Alert.AlertType.WARNING, "Partial Success", "Material record deleted from database but file could not be removed.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete material. Please try again.");
                }
            }
        });
    }


    @FXML
    private void handleShowMaterials() {
        LectureCourse selectedCourse = courseTableview.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "No Course Selected",
                    "Please select a course first to view its materials.");
            return;
        }

        //materials for this course
        ObservableList<CourseMaterial> materials = CourseMaterial.getMaterialsByCourse(selectedCourse.getCid());

        if (materials.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Materials",
                    "No materials found for " + selectedCourse.getCName() + " (" + selectedCourse.getCid() + ")");
            return;
        }

        //dialog to show materials
        Dialog<CourseMaterial> dialog = new Dialog<>();
        dialog.setTitle("Course Materials");
        dialog.setHeaderText("Materials for: " + selectedCourse.getCName() + " (" + selectedCourse.getCid() + ")");

        //button types
        ButtonType openButtonType = new ButtonType("Open File", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(openButtonType, ButtonType.CLOSE);

        //Material list
        ListView<CourseMaterial> listView = new ListView<>();
        listView.setItems(materials);
        listView.setCellFactory(lv -> new ListCell<CourseMaterial>() {
            @Override
            protected void updateItem(CourseMaterial item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label nameLabel = new Label(item.getFileName());
                    Label idLabel = new Label("(ID: " + item.getMaterialId() + ")");
                    idLabel.setStyle("-fx-text-fill: #7f8c8d;");
                    hbox.getChildren().addAll(nameLabel, idLabel);
                    setGraphic(hbox);
                }
            }
        });

        //double-click to open file
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                CourseMaterial selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openMaterialFile(selected);
                }
            }
        });

        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().setPrefHeight(300);

        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == openButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<CourseMaterial> result = dialog.showAndWait();

        result.ifPresent(material -> {
            openMaterialFile(material);
        });
    }


     // Open a material file with the default system application
    private void openMaterialFile(CourseMaterial material) {
        try {
            Path filePath = Paths.get(material.getMaterialPath());

            if (Files.exists(filePath)) {
                File file = filePath.toFile();
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
                System.out.println("Opening file: " + material.getFileName());
            } else {
                showAlert(Alert.AlertType.ERROR, "File Not Found", "The material file could not be found at:\n" + material.getMaterialPath() + "\n\nIt may have been moved or deleted.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error Opening File", "Could not open the file.\nError: " + e.getMessage());
            System.out.println("ERROR: Could not open file - " + e.getMessage());
        }
    }


     // Helper method to show alerts
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadLecturerCourses() {
        ObservableList<LectureCourse> courses = LectureCourse.getLectureCourse(userId);
        System.out.println("Loading courses for userId: " + userId);

        Platform.runLater(() -> {
            courseList.clear();
            courseList.addAll(courses);
            courseTableview.refresh();
            System.out.println("Loaded " + courseList.size() + " courses");
        });
    }

    @FXML
    private void handleAddMaterial() {
        LectureCourse selectedCourse = courseTableview.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "No Course Selected",
                    "Please select a course from the table first before adding material.");
            return;
        }

        //chooser for course materials
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Course Material for " + selectedCourse.getCName());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Course Materials", "*.pdf", "*.pptx", "*.docx"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("PowerPoint Files", "*.pptx"),
                new FileChooser.ExtensionFilter("Word Documents", "*.docx")
        );

        Stage stage = (Stage) AddMaterialBtn.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                //FileStorageHandler to save the file
                String savedPath = FileStorageHandler.addMaterial(selectedFile);

                if (savedPath != null) {
                    // Save record to database
                    boolean saved = CourseMaterial.saveMaterialRecord(savedPath, selectedCourse.getCid());

                    if (saved) {
                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Material uploaded successfully!\n\n" +
                                        "Course: " + selectedCourse.getCName() + "\n" +
                                        "Course Code: " + selectedCourse.getCid() + "\n" +
                                        "File: " + selectedFile.getName());

                        System.out.println("SUCCESS: Material added - " + savedPath);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Database Error",
                                "Failed to save material record to database.\nPlease try again.");
                    }
                }

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Upload Failed",
                        "Failed to upload material.\nError: " + e.getMessage());
                System.out.println("ERROR: Material upload failed - " + e.getMessage());
            }
        }
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
            String gender = (lecturerDetails.get(0).getGender().equalsIgnoreCase("male")) ? "Male" : "Female";
            fullNameField.setText(lecturerDetails.get(0).getFullName());
            departmentSelect.setValue(lecturerDetails.get(0).getDepartment());
            officeRoomFiled.setText(lecturerDetails.get(0).getOfficeRoom());
            genderField.setText(gender);
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
