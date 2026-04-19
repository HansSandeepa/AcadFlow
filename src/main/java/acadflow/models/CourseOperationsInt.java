package acadflow.models;

public interface CourseOperationsInt {

    public void addCourse(String cid,String cname,int credits,String type);
    public void deleteCourse(String cid);
    public void updateCourse(String oldcid, String cname, int credits, String type, String Ncid);
    public void viewCourseList();
    public  void viewStudentEnrolledCourse(String stid);
    public void getParticularCourseDetails(String cid);


}
