package acadflow.models.getterSetter;

public class LecturerCurrentData {
    private String fullName;
    private String lecturerId;
    private String department;
    private String officeRoom;
    private String gender;
    private String email;
    private String address;

    public LecturerCurrentData(String fullName, String lecturerId, String department, String officeRoom, String gender, String email, String address) {
        this.fullName = fullName;
        this.lecturerId = lecturerId;
        this.department = department;
        this.officeRoom = officeRoom;
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

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOfficeRoom() {
        return officeRoom;
    }

    public void setOfficeRoom(String officeRoom) {
        this.officeRoom = officeRoom;
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
