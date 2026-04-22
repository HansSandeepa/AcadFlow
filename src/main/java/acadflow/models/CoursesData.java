package acadflow.models;

public class CoursesData {

    public String course_id;
    public String name;
    public int credit;
    public String type;
    public String department;

    public CoursesData(String course_id, String name, int credit, String type,String department) {
        this.course_id = course_id;
        this.name = name;
        this.credit = credit;
        this.type = type;
        this.department = department;
    }
}
