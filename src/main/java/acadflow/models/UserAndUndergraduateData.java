package acadflow.models;

public class UserAndUndergraduateData {

    public String stu_id, dept;
    public int batch, level, semester;
    public String name, dob, gender, email;

    public UserAndUndergraduateData(String stu_id, String dept, int batch, int level, int semester, String name, String dob, String gender, String email) {
        this.stu_id = stu_id;
        this.dept = dept;
        this.batch = batch;
        this.level = level;
        this.semester = semester;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
    }
}
