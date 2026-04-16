package acadflow.models;

public class Lecturer extends User {
    private String employeeId;
    private String department;
    private String specialization;

    public Lecturer() {
        this.role = "lecturer";
    }

    public Lecturer(int userId, String username, String fullName, String employeeId, String department) {
        super(userId, username, fullName, "lecturer");
        this.employeeId = employeeId;
        this.department = department;
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canUploadMarks() {
        return true;
    }

    @Override
    public boolean canManageAttendance() {
        return false;
    }


    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
}
