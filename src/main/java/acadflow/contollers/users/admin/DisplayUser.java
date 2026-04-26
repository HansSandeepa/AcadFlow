package acadflow.contollers.users.admin;

import java.time.LocalDate;


// INHERITANCE: DisplayUser inherits the BaseEntity contract
public class DisplayUser implements BaseEntity {

    // ── ENCAPSULATION: all fields are private ────────────────────────────────
    private int userId;
    private String fullname;   // cannot be final — setUserId() exists
    private String address;
    private LocalDate dob;
    private String gender;
    private String password;
    private String email;
    private String userType;   // cannot be final — updateDisplayUser() may change it

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

    // ── INHERITANCE + POLYMORPHISM: implementing the BaseEntity contract ──────
    // This method is declared in BaseEntity (abstract contract).
    // DisplayUser provides the concrete implementation here.
    // At runtime, calling getEntityId() on a BaseEntity reference
    // that holds a DisplayUser will execute THIS method — Polymorphism.
    @Override
    public String getEntityId() {
        return String.valueOf(userId); // ENCAPSULATION: reads private field via logic
    }

    // ── ENCAPSULATION: public getters — controlled READ access ───────────────
    public int getUserId()       { return userId; }
    public String getFullname()  { return fullname; }
    public String getAddress()   { return address; }
    public LocalDate getDob()    { return dob; }
    public String getGender()    { return gender; }
    public String getPassword()  { return password; }
    public String getEmail()     { return email; }
    public String getUserType()  { return userType; }

    // ── ENCAPSULATION: public setters — controlled WRITE access ──────────────
    public void setUserId(int userId)       { this.userId = userId; }
    public void setAddress(String address)  { this.address = address; }
    public void setDob(LocalDate dob)       { this.dob = dob; }
    public void setGender(String gender)    { this.gender = gender; }
    public void setPassword(String password){ this.password = password; }
    public void setEmail(String email)      { this.email = email; }
}
