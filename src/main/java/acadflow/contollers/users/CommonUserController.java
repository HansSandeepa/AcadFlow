package acadflow.contollers.users;

import acadflow.models.users.User;

public abstract class CommonUserController {
    protected String regNo;
    protected String nameOfUser;
    protected String userImagePath;

    public void setUserDetails(String regNo){
        this.regNo = regNo;
        loadTopPanelUserDetails();
        initializeWithUserData();
    }

    /**
     * This method is called after regNo is set. Override this in subclasses
     * instead of using initialize() if you need the regNo value.
     */
    protected void initializeWithUserData(){
        // Subclasses can override this method
    }

    protected void loadTopPanelUserDetails(){
        User user = new User(regNo);
        nameOfUser = user.loadUserName();
        userImagePath = user.loadUserImagePath();
    }
}
