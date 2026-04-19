package acadflow.models;

public class UserAndLecturerData {
    public String lec_id, dept, name, dob, gender, email;
    public int office_room;

    public UserAndLecturerData(String lec_id, String dept, String name, String dob, String gender, String email, int office_room) {
        this.lec_id = lec_id;
        this.dept = dept;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.office_room = office_room;
    }
}
