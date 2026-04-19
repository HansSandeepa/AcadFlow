package acadflow.models;

public class TechnicalOfficer extends User {
    private String officerId;
    private String department;

    public TechnicalOfficer() {
        this.role = "technical_officer";
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
        return true;
    }


    public String getOfficerId() {
        return officerId;
    }
    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
}