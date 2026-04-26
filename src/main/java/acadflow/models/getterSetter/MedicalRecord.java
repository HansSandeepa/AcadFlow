package acadflow.models.getterSetter;

import javafx.beans.property.*;

public class MedicalRecord {
    private final StringProperty studentId;
    private final StringProperty studentName;
    private final IntegerProperty medicalId;
    private final StringProperty date;
    private final StringProperty sessionType;
    private final StringProperty courseId;
    private final StringProperty courseName;
    private final StringProperty status;
    private final IntegerProperty attendanceId;


    public MedicalRecord(int medicalId, String date, String sessionType,
                         String courseId, String courseName, String status,
                         int attendanceId, String studentId, String studentName) {
        this.medicalId = new SimpleIntegerProperty(medicalId);
        this.date = new SimpleStringProperty(date);
        this.sessionType = new SimpleStringProperty(sessionType);
        this.courseId = new SimpleStringProperty(courseId);
        this.courseName = new SimpleStringProperty(courseName);
        this.status = new SimpleStringProperty(status);
        this.attendanceId = new SimpleIntegerProperty(attendanceId);
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
    }

    // Getters and property methods

    public String getStudentId() {
        return studentId.get();
    }

    public StringProperty studentIdProperty() {
        return studentId;
    }

    public String getStudentName() {
        return studentName.get();
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public int getMedicalId() {
        return medicalId.get();
    }

    public IntegerProperty medicalIdProperty() {
        return medicalId;
    }

    public String getDate() {
        return date.get();
    }
    public StringProperty dateProperty() {
        return date;
    }

    public String getSessionType() {
        return sessionType.get();
    }

    public StringProperty sessionTypeProperty() {
        return sessionType;
    }

    public String getCourseId() {
        return courseId.get();
    }
    public StringProperty courseIdProperty() {
        return courseId;
    }

    public String getCourseName() {
        return courseName.get();
    }

    public StringProperty courseNameProperty() {
        return courseName;
    }


    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }


    public int getAttendanceId() {
        return attendanceId.get();
    }


    public IntegerProperty attendanceIdProperty() {
        return attendanceId;
    }

}