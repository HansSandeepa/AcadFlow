package acadflow.models.users;

import java.time.LocalDate;

public class TechnicalOfficer extends User implements SelfProfileUpdate{
    @Override
    public void updateProfile(String address, LocalDate dob, Gender gender, String email) {

    }
}
