package acadflow.models.users;

import javafx.scene.control.Button;

/**
*  Implementations that every user must have
 */
public interface UserImplementations {

    void logout(Button logoutBtn);

    String loadUserName(String regNo);

    void loadUserImagePath(String regNo);

    void updateMyUserImage(String picturePath);

    void showNotices();

    void showTimetable();
}
