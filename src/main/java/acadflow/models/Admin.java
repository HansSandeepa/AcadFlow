package acadflow.models;

public class Admin extends User {
    private String adminCode;

    public Admin() {
        this.role = "admin";
    }

    public Admin(int userId, String username, String fullName, String adminCode) {
        super(userId, username, fullName, "admin");
        this.adminCode = adminCode;
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public boolean canUploadMarks() {
        return false;
    }

    @Override
    public boolean canManageAttendance() {
        return false;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }
}