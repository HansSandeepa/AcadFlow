package acadflow.models;

public abstract class User {
    protected int userId;
    protected String username;
    protected String password;
    protected String fullName;
    protected String email;
    protected String contact;
    protected String role;


    public User() {}

    public User(int userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public abstract boolean canManageUsers();
    public abstract boolean canUploadMarks();
    public abstract boolean canManageAttendance();


    public void updateProfile(String email, String contact) {
        this.email = email;
        this.contact = contact;
    }
}