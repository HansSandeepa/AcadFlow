package acadflow.models.getterSetter;

public class UndergraduateCurrentData {
    private final String fullName;
    private final String regNo;
    private final String department;
    private final String batch;
    private final String email;
    private final String address;
    private final String gender;

    public UndergraduateCurrentData(String fullName, String regNo,String department, String batch, String email, String address,String gender) {
        this.fullName = fullName;
        this.regNo = regNo;
        this.department = department;
        this.batch = batch;
        this.email = email;
        this.address = address;
        this.gender = gender;
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

    public String getGender(){
        return gender;
    }
}
