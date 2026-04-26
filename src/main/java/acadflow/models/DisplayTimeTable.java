package acadflow.models;

public class DisplayTimeTable {
    private String timetableId;
    private String day;
    private String time;
    private String levelAndSem;
    private String sessionType;
    private String dept;
    private String courseId;
    private String adminId;

    public DisplayTimeTable(String timetableId, String day, String time, String levelAndSem, String session_type, String dept, String courseId, String adminId) {
        this.timetableId = timetableId;
        this.day = day;
        this.time = time;
        this.levelAndSem = levelAndSem;
        this.sessionType = session_type;
        this.dept = dept;
        this.courseId = courseId;
        this.adminId = adminId;
    }

    public String getTimetableId() {return timetableId;}
    public String getDay() {return day;}
    public String getTime() {return time;}
    public String getLevelAndSem() {return levelAndSem;}
    public String getSessionType() {return sessionType;}
    public String getDept() {return dept;}
    public String getCourseId() {return courseId;}
    public String getAdminId() {return adminId;}

    public void setTimetableId(String timetableId) {this.timetableId = timetableId;}
    public void setDay(String day) {this.day = day;}
    public void setTime(String time) {this.time = time;}
    public void setLevelAndSem(String levelAndSem) {this.levelAndSem = levelAndSem;}
    public void setSessionType(String sessionType) {this.sessionType = sessionType;}
    public void setDept(String dept) {this.dept = dept;}
    public void setCourseId(String courseId) {this.courseId = courseId;}
    public void setAdminId(String adminId) {this.adminId = adminId;}
}
