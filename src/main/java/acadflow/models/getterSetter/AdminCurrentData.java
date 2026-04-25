package acadflow.models.getterSetter;

public class AdminCurrentData {
    private String fullName;
    private String adminId;
    private String gender;
    private String email;
    private String address;

    public AdminCurrentData(String fullName, String adminId, String gender, String email, String address) {
        this.fullName = fullName;
        this.adminId = adminId;
        this.gender = gender;
        this.email = email;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
