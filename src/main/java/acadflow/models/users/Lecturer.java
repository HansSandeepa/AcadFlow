package acadflow.models.users;

import java.time.LocalDate;

public class Lecturer extends User{
    public Lecturer(String regNo) {
        super(regNo);
    }

    //mewidihata userId ganna puluwan
    //remove this method anjana
    public void showLecId(){
        System.out.println("Test Lecturer ID: " + userId);
    }
}
