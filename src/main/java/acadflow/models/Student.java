package acadflow.models;

public class Student extends User {
    private String studentId;
    private int batchYear;
    private boolean isRepeat;
    private boolean isBatchMissed;
    private double gpa;


    public Student() {
        this.role = "student";

    }

    public Student(int userId, String username, String fullName, String studentId) {
        super(userId, username, fullName, "student");
        this.studentId = studentId;
    }


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getBatchYear() {
        return batchYear;
    }

    public void setBatchYear(int batchYear) {
        this.batchYear = batchYear;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }


    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canUploadMarks() {
        return false;
    }

    @Override
    public boolean canManageAttendance() {
        return false;
    }

}