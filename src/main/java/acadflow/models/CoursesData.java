package acadflow.models;

public class CoursesData {

    public String course_id;
    public String name;
    public int credit;
    public String type;

    public CoursesData(String course_id, String name, int credit, String type) {
        this.course_id = course_id;
        this.name = name;
        this.credit = credit;
        this.type = type;
    }
}
