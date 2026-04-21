package acadflow.contollers.users;

public abstract class CommonUserController {
    public String regNo;

    public void setUserRegNo(String regNo){
        this.regNo = regNo;
        // Call this after regNo is set so initialization happens with valid data
        initializeWithUserData();
    }

    /**
     * This method is called after regNo is set. Override this in subclasses
     * instead of using initialize() if you need the regNo value.
     */
    protected void initializeWithUserData(){
        // Subclasses can override this method
    }
}
