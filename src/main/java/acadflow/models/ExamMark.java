package acadflow.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class representing one row in the exam_marks table.
 *
 * ENCAPSULATION: all fields are private; access is through
 *                JavaFX properties (used by TableView) and
 *                plain getters/setters.
 *
 * DB columns mapped:
 *   Exam_id, Grade, Session_type, Assessment_type,
 *   Mark, Stu_id, Lecturer_id, Course_id
 *
 * Extra computed fields (not stored in DB):
 *   courseName  — joined from course table for display
 *   credits     — joined from course table for GPA calculation
 */
public class ExamMark {

    // ── ENCAPSULATION: private fields ────────────────────────────────────────
    private final IntegerProperty examId        = new SimpleIntegerProperty();
    private final StringProperty  grade         = new SimpleStringProperty();
    private final StringProperty  sessionType   = new SimpleStringProperty(); // 'T' or 'P'
    private final StringProperty  assessmentType= new SimpleStringProperty(); // 'T' or 'P'
    private final IntegerProperty mark          = new SimpleIntegerProperty();
    private final StringProperty  stuId         = new SimpleStringProperty();
    private final StringProperty  lecturerId    = new SimpleStringProperty();
    private final StringProperty  courseId      = new SimpleStringProperty();

    // Computed / joined display fields
    private final StringProperty  courseName    = new SimpleStringProperty();
    private final IntegerProperty credits       = new SimpleIntegerProperty();

    // ── Constructors ─────────────────────────────────────────────────────────

    public ExamMark() {}

    public ExamMark(int examId, String grade, String sessionType, String assessmentType,
                    int mark, String stuId, String lecturerId, String courseId) {
        this.examId.set(examId);
        this.grade.set(grade);
        this.sessionType.set(sessionType);
        this.assessmentType.set(assessmentType);
        this.mark.set(mark);
        this.stuId.set(stuId);
        this.lecturerId.set(lecturerId);
        this.courseId.set(courseId);
    }

    // ── JavaFX property accessors (required by PropertyValueFactory) ─────────
    public IntegerProperty examIdProperty()         { return examId; }
    public StringProperty  gradeProperty()          { return grade; }
    public StringProperty  sessionTypeProperty()    { return sessionType; }
    public StringProperty  assessmentTypeProperty() { return assessmentType; }
    public IntegerProperty markProperty()           { return mark; }
    public StringProperty  stuIdProperty()          { return stuId; }
    public StringProperty  lecturerIdProperty()     { return lecturerId; }
    public StringProperty  courseIdProperty()       { return courseId; }
    public StringProperty  courseNameProperty()     { return courseName; }
    public IntegerProperty creditsProperty()        { return credits; }

    // ── Plain getters ─────────────────────────────────────────────────────────
    public int    getExamId()         { return examId.get(); }
    public String getGrade()          { return grade.get(); }
    public String getSessionType()    { return sessionType.get(); }
    public String getAssessmentType() { return assessmentType.get(); }
    public int    getMark()           { return mark.get(); }
    public String getStuId()          { return stuId.get(); }
    public String getLecturerId()     { return lecturerId.get(); }
    public String getCourseId()       { return courseId.get(); }
    public String getCourseName()     { return courseName.get(); }
    public int    getCredits()        { return credits.get(); }

    // ── Plain setters ─────────────────────────────────────────────────────────
    public void setExamId(int v)          { examId.set(v); }
    public void setGrade(String v)        { grade.set(v); }
    public void setSessionType(String v)  { sessionType.set(v); }
    public void setAssessmentType(String v){ assessmentType.set(v); }
    public void setMark(int v)            { mark.set(v); }
    public void setStuId(String v)        { stuId.set(v); }
    public void setLecturerId(String v)   { lecturerId.set(v); }
    public void setCourseId(String v)     { courseId.set(v); }
    public void setCourseName(String v)   { courseName.set(v); }
    public void setCredits(int v)         { credits.set(v); }

    // ── Grade calculation helper ──────────────────────────────────────────────

    /**
     * Converts a numeric mark (0-100) to a letter grade.
     * Called by ExamMarkDAO after computing the mark.
     *
     * ABSTRACTION: callers just pass a mark and get a grade string back.
     */
    public static String calculateGrade(int mark) {
        if (mark >= 75) return "A+";
        if (mark >= 70) return "A";
        if (mark >= 65) return "A-";
        if (mark >= 60) return "B+";
        if (mark >= 55) return "B";
        if (mark >= 50) return "B-";
        if (mark >= 45) return "C+";
        if (mark >= 40) return "C";
        if (mark >= 35) return "C-";
        if (mark >= 30) return "D";
        return "F";
    }

    /**
     * Converts a letter grade to a GPA point value (4.0 scale).
     * Used by the undergraduate dashboard to compute overall GPA.
     */
    public static double gradeToGpaPoint(String grade) {
        if (grade == null) return 0.0;
        switch (grade) {
            case "A+": return 4.0;
            case "A":  return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B":  return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C":  return 2.0;
            case "C-": return 1.7;
            case "D":  return 1.0;
            default:   return 0.0;
        }
    }

    /**
     * Returns true if the student is eligible to sit the final exam.
     * Rule: CA mark must be >= 40.
     * Assessment_type 'T' = CA (continuous assessment).
     */
    public boolean isEligible() {
        return "T".equals(assessmentType.get()) && mark.get() >= 40;
    }
}
