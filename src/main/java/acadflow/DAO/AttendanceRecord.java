package acadflow.DAO;

import javafx.beans.property.*;

public class AttendanceRecord {

    private final IntegerProperty attendanceId;
    private final StringProperty session;
    private final StringProperty sessionType;   // "T" or "P"
    private final StringProperty status;        // "present" or "absent"
    private final StringProperty date;          // stored as String (yyyy-MM-dd)
    private final StringProperty courseId;
    private final StringProperty stuId;

    private final StringProperty studentName;
    private final StringProperty courseName;

    public AttendanceRecord(int attendanceId, String session, String sessionType,
                            String status, String date, String courseId,
                            String stuId, String studentName, String courseName) {
        this.attendanceId  = new SimpleIntegerProperty(attendanceId);
        this.session       = new SimpleStringProperty(session);
        this.sessionType   = new SimpleStringProperty(sessionType);
        this.status        = new SimpleStringProperty(status);
        this.date          = new SimpleStringProperty(date);
        this.courseId      = new SimpleStringProperty(courseId);
        this.stuId         = new SimpleStringProperty(stuId);
        this.studentName   = new SimpleStringProperty(studentName);
        this.courseName    = new SimpleStringProperty(courseName);
    }

    public AttendanceRecord(String session, String sessionType, String status,
                            String date, String courseId, String stuId) {
        this(0, session, sessionType, status, date, courseId, stuId, "", "");
    }

    public IntegerProperty attendanceIdProperty()  { return attendanceId; }
    public StringProperty  sessionProperty()        { return session; }
    public StringProperty  sessionTypeProperty()    { return sessionType; }
    public StringProperty  statusProperty()         { return status; }
    public StringProperty  dateProperty()           { return date; }
    public StringProperty  courseIdProperty()       { return courseId; }
    public StringProperty  stuIdProperty()          { return stuId; }
    public StringProperty  studentNameProperty()    { return studentName; }
    public StringProperty  courseNameProperty()     { return courseName; }

    public int    getAttendanceId()  { return attendanceId.get(); }
    public void   setAttendanceId(int v) { attendanceId.set(v); }

    public String getSession()       { return session.get(); }
    public void   setSession(String v) { session.set(v); }

    public String getSessionType()   { return sessionType.get(); }
    public void   setSessionType(String v) { sessionType.set(v); }

    /** Returns "present" or "absent". */
    public String getStatus()        { return status.get(); }
    public void   setStatus(String v) { status.set(v); }

    /** Returns date as "yyyy-MM-dd" string. */
    public String getDate()          { return date.get(); }
    public void   setDate(String v)  { date.set(v); }

    public String getCourseId()      { return courseId.get(); }
    public void   setCourseId(String v) { courseId.set(v); }

    public String getStuId()         { return stuId.get(); }
    public void   setStuId(String v) { stuId.set(v); }

    public String getStudentName()   { return studentName.get(); }
    public void   setStudentName(String v) { studentName.set(v); }

    public String getCourseName()    { return courseName.get(); }
    public void   setCourseName(String v) { courseName.set(v); }

    // Convenience helpers
    /** Returns a human-readable session type label ("Theory" or "Practical"). */
    public String getSessionTypeLabel() {
        return "T".equalsIgnoreCase(sessionType.get()) ? "Theory" : "Practical";
    }

    /** Returns true when the student was present. */
    public boolean isPresent() {
        return "present".equalsIgnoreCase(status.get());
    }

    @Override
    public String toString() {
        return "AttendanceRecord{id=" + getAttendanceId()
                + ", stu=" + getStuId()
                + ", course=" + getCourseId()
                + ", session=" + getSession()
                + ", type=" + getSessionType()
                + ", status=" + getStatus()
                + ", date=" + getDate() + "}";
    }
}
