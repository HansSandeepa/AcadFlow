package acadflow.models;

import java.time.LocalDate;

public class DisplayUser {
    private int userId;
    private String fullname;
    private String address;
    private LocalDate dob;
    private String gender;
    private String password;
    private String email;
    private String userType;

    public DisplayUser(int userId, String fullname, String address, LocalDate dob,
                       String gender, String password, String email, String userType) {
        this.userId = userId;
        this.fullname = fullname;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.password = password;
        this.email = email;
        this.userType = userType;
    }

    public int getUserId() { return userId; }
    public String getFullname() { return fullname; }
    public String getAddress() { return address; }
    public LocalDate getDob() { return dob; }
    public String getGender() { return gender; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getUserType() { return userType; }


    public void setUserId(int userId) { this.userId = userId; }
    public void setAddress(String address) { this.address = address; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }

}