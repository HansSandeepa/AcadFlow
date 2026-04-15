package acadflow.models;

import acadflow.util.DBConnection;
import acadflow.util.PasswordHash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoadUserViewModel {
    private final String tableName;
    private final String regNo;
    private final String password;

    public LoadUserViewModel(String tableName, String regNo, String password) {
        this.tableName = tableName;
        this.regNo = regNo;
        this.password = password;
    }

    /**
     * Result enum to indicate the outcome of loading a user page
     */
    public enum LoadResult {
        SUCCESS,
        USER_NOT_FOUND_IN_TABLE,
        USER_NOT_FOUND_IN_COMMON_TABLE,
        INVALID_TABLE_NAME,
        DATABASE_ERROR,
        PASSWORD_MISMATCH
    }

    /**
     * Loads the user page and returns the result
     * @return LoadResult indicating success or type of failure
     */
    public LoadResult loadPage(){
        try (Connection conn = DBConnection.getConnection()) {
            // select query according to table
            String selectUserIdQuery = buildSelectQuery(tableName);

            if (selectUserIdQuery == null) {
                System.out.println("\u001B[31mERROR: Invalid table name: " + tableName + "\u001B[0m");
                return LoadResult.INVALID_TABLE_NAME;
            }

            try (PreparedStatement stmt = conn.prepareStatement(selectUserIdQuery)) {
                stmt.setString(1, regNo);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String userId = rs.getString("User_id");

                        // Check if user exists in common user table
                        String checkUserExistsQuery = "SELECT EXISTS(SELECT 1 FROM user WHERE User_id = ?)";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkUserExistsQuery)) {
                            checkStmt.setString(1, userId);

                            try (ResultSet checkRs = checkStmt.executeQuery()) {
                                if (checkRs.next() && checkRs.getInt(1) == 1) {

                                    //check weather the password match for that user
                                    if (passwordMatch(userId)) {
                                        System.out.println("Password match");
                                        System.out.println("Loading page for " + tableName + " with registration number: " + regNo);
                                        return LoadResult.SUCCESS;
                                    }else{
                                        System.out.println("\u001B[31mERROR: Password Mismatch\u001B[0m");
                                        return LoadResult.PASSWORD_MISMATCH;
                                    }


                                } else {
                                    System.out.println("\u001B[31mERROR: No corresponding user found in the common user table for " + tableName + " with registration number: " + regNo + "\u001B[0m");
                                    return LoadResult.USER_NOT_FOUND_IN_COMMON_TABLE;
                                }
                            }
                        }
                    } else {
                        System.out.println("\u001B[31mERROR: No user found in " + tableName + " with registration number: " + regNo + "\u001B[0m");
                        return LoadResult.USER_NOT_FOUND_IN_TABLE;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: " + e.getMessage() + "\u001B[0m");
            return LoadResult.DATABASE_ERROR;
        }
    }

    /**
     * Builds a parameterized SQL query to select the User_id based on the table name.
     */
    private String buildSelectQuery(String tableName) {
        switch (tableName.toLowerCase()) {
            case "undergraduate":
                return "SELECT User_id FROM undergraduate WHERE Stu_id = ?";
            case "lecturer":
                return "SELECT User_id FROM lecturer WHERE Lecturer_Id = ?";
            case "admin":
                return "SELECT User_id FROM admin WHERE Admin_id = ?";
            case "tec_officer":
                return "SELECT User_id FROM tec_officer WHERE T_officer_id = ?";
            default:
                return null; // Invalid table name
        }
    }

    /**
     * Checks if the entered password matches the stored password for the user.
     * @return boolean - true if password match false otherwise
     */
    private boolean passwordMatch(String userId){
        boolean isPwdMatch = false;
        try(Connection conn = DBConnection.getConnection()) {
            String passwordQuery = "SELECT Password FROM user WHERE User_id = ?";
            PreparedStatement stmt = conn.prepareStatement(passwordQuery);
            stmt.setString(1,userId);
            ResultSet pwdRs = stmt.executeQuery();
            if (pwdRs.next()){
                String storedHash = pwdRs.getString(1);
                isPwdMatch = new PasswordHash(password,storedHash).matchesHash();
            }
            pwdRs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("\u001B[31mSQL ERROR: " + e.getMessage() + "\u001B[0m");
            isPwdMatch = false;
        }
        return isPwdMatch;
    }
}
