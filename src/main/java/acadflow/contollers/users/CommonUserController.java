package acadflow.contollers.users;

import acadflow.models.users.User;

public abstract class CommonUserController {
    public String regNo;
    public String nameOfUser;

    public void setUserRegNo(String regNo){
        this.regNo = regNo;
        // Call this after regNo is set so initialization happens with valid data
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
    }
}
