package acadflow.models;

import java.util.List;

import javafx.collections.ObservableList;

public interface CourseOperationsInt {

    public boolean addCourse(String cid,String cname,int credits,String type,String lecturerID,String Department);
    public void deleteCourse(String cid);
    public void updateCourse(String oldcid, String cname, int credits, String type, String Ncid);
    public ObservableList<Course> getAllCourses();
    public  void viewStudentEnrolledCourse(String stid);
    public void getParticularCourseDetails(String cid);
    public List<String> getLecturerNames();


}
