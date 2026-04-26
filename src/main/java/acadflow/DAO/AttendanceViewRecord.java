package acadflow.DAO;

import javafx.beans.property.*;

/**
 * Read-only row model for the undergraduate's own attendance view.
 * Combines an attendance row with any linked medical record details.
 */
public class AttendanceViewRecord {

    private final StringProperty  courseId;
    private final StringProperty  courseName;
    private final StringProperty  session;
    private final StringProperty  sessionType;    // "T" or "P"
    private final StringProperty  date;
    private final StringProperty  status;         // "present" | "absent"
    private final BooleanProperty hasMedical;
    private final StringProperty  medicalApproval; // "yes" | "no" | "-"

    public AttendanceViewRecord(String courseId, String courseName,
                                String session, String sessionType,
                                String date, String status,
                                boolean hasMedical, String medicalApproval) {
        this.courseId        = new SimpleStringProperty(courseId);
        this.courseName      = new SimpleStringProperty(courseName);
        this.session         = new SimpleStringProperty(session);
        this.sessionType     = new SimpleStringProperty(sessionType);
        this.date            = new SimpleStringProperty(date);
        this.status          = new SimpleStringProperty(status);
        this.hasMedical      = new SimpleBooleanProperty(hasMedical);
        this.medicalApproval = new SimpleStringProperty(medicalApproval);
    }

    public StringProperty  courseIdProperty()        { return courseId; }
    public StringProperty  courseNameProperty()      { return courseName; }
    public StringProperty  sessionProperty()         { return session; }
    public StringProperty  sessionTypeProperty()     { return sessionType; }
    public StringProperty  dateProperty()            { return date; }
    public StringProperty  statusProperty()          { return status; }
    public BooleanProperty hasMedicalProperty()      { return hasMedical; }
    public StringProperty  medicalApprovalProperty() { return medicalApproval; }

    public String  getCourseId()        { return courseId.get(); }
    public String  getCourseName()      { return courseName.get(); }
    public String  getSession()         { return session.get(); }
    public String  getSessionType()     { return sessionType.get(); }
    public String  getDate()            { return date.get(); }
    public String  getStatus()          { return status.get(); }
    public boolean isHasMedical()       { return hasMedical.get(); }
    public String  getMedicalApproval() { return medicalApproval.get(); }

    /** Human-readable session type: "Theory" or "Practical". */
    public String getSessionTypeLabel() {
        return "T".equalsIgnoreCase(sessionType.get()) ? "Theory" : "Practical";
    }
}
