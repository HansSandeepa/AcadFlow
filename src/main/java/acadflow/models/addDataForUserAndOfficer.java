package acadflow.models;

import acadflow.util.DBConnection;
import acadflow.util.PasswordHash;

import java.sql.Connection;

public class addDataForUserAndOfficer {

    public void addUserAndOfficerData(){
        UserAndOfficerData[] users = {
                new UserAndOfficerData("Officer ICT1", "1990-05-12", "M", "ict1@company.com", "ICT", "to0001", "2020-05-12"),
                new UserAndOfficerData("Officer BST1", "1991-03-08", "F", "bst1@company.com", "BST", "to0002", "2021-03-08"),
                new UserAndOfficerData("Officer ET1",  "1992-07-19", "M", "et1@company.com",  "ET",  "to0003", "2022-07-19"),
                new UserAndOfficerData("Officer ICT2",  "1993-01-25", "F", "ict2@company.com",  "ICT",  "to0004", "2023-01-25"),
                new UserAndOfficerData("Officer BST2", "1994-11-03", "M", "bst2@company.com", "BST", "to0005", "2024-11-03")

        };

        String address = "Faculty of Technology, University of Ruhuna";
        PasswordHash passwordHash = new PasswordHash("to@123");

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            userDBLogic udbl = new userDBLogic();

            for (UserAndOfficerData u : users) {
                if (udbl.isEmailExists(conn, u.email)) {
                    System.out.println("Email is already exists: " + u.email);
                    continue;
                }
                udbl.insertUserAndOfficer(conn, u, address, passwordHash);
            }

            conn.commit();
            System.out.println("Tech Officers inserted!");

        } catch (Exception e) {
                System.out.println("Error in inserting User And Officers Data: " + e.getMessage());
        }
    }
}
