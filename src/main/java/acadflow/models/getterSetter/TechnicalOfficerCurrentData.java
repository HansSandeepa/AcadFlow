package acadflow.models.getterSetter;

public class TechnicalOfficerCurrentData {
    private String fullName;
    private String techOfficerId;
    private String department;
    private String hireDate;
    private String gender;
    private String email;
    private String address;

    public TechnicalOfficerCurrentData(String fullName, String techOfficerId, String department, String hireDate, String gender, String email, String address) {
        this.fullName = fullName;
        this.techOfficerId = techOfficerId;
        this.department = department;
        this.hireDate = hireDate;
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

    public String getTechOfficerId() {
        return techOfficerId;
    }

    public void setTechOfficerId(String techOfficerId) {
        this.techOfficerId = techOfficerId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
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
