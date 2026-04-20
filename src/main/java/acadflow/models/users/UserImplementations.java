package acadflow.models.users;

import javafx.scene.control.Button;

/**
*  Implementations that every user must have
 */
public interface UserImplementations {

    void logout(Button logoutBtn);

    void loadUserName();

    void loadUserId();

    void loadUserImage();

    void updateMyUserImage(String picturePath);

    void showNotices();

    void showTimetable();
}
