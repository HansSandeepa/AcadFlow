package acadflow.models.getterSetter;

public class UndergraduateCurrentData {
    private String fullName;
    private String regNo;
    private String department;
    private String batch;
    private String email;
    private String address;

    public UndergraduateCurrentData(String fullName, String regNo,String department, String batch, String email, String address) {
        this.fullName = fullName;
        this.regNo = regNo;
        this.department = department;
        this.batch = batch;
        this.email = email;
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public String getBatch() {
        return batch;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
