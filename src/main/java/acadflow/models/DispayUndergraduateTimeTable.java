package acadflow.models;

public class DispayUndergraduateTimeTable {
    private String stu_id;
    private int batch;
    private int level;
    private int semester;
    private String dept;
    private int user_id;

    public DispayUndergraduateTimeTable(String stu_id, int batch, int level, int semester,
                                        String dept, int user_id) {
        this.stu_id = stu_id;
        this.batch = batch;
        this.level = level;
        this.semester = semester;
        this.dept = dept;
        this.user_id = user_id;
    }

    public String getStu_id() { return stu_id; }
    public int getBatch() { return batch; }
    public int getLevel() { return level; }
    public int getSemester() { return semester; }
    public String getDept() { return dept; }
    public int getUser_id() { return user_id; }
}
