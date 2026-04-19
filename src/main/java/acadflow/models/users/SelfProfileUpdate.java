package acadflow.models.users;

import java.time.LocalDate;

/**
* For updating self profile updating details
 * Don't implement for the Undergraduates
 */
public interface SelfProfileUpdate {
    enum Gender{
        MALE, FEMALE
    }

    void updateProfile(String address, LocalDate dob,Gender gender,String email);
}
