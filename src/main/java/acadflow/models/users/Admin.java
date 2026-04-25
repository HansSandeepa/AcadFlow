package acadflow.models.users;

import java.time.LocalDate;

public class Admin extends User implements SelfProfileUpdate{
    public Admin(String regNo) {
        super(regNo);
    }

    @Override
    public void updateProfile(String address, LocalDate dob, Gender gender, String email) {

    }
}
