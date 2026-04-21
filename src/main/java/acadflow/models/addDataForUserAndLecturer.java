package acadflow.models;

import acadflow.util.DBConnection;
import acadflow.util.PasswordHash;

import java.sql.Connection;

public class addDataForUserAndLecturer {

    public void addUserAndLecturerData() {
        UserAndLecturerData[] users = {
                new UserAndLecturerData("lec0001", "ICT", "P.H.P. Nuwan Laksiri", "1990-05-12", "M", "nuwanLaksiri@gmailcom", 1),
                new UserAndLecturerData("lec0002", "MDS", "Dr. K K N B Adikaram", "1991-03-08", "F", "adhikaram@gmailcom", 2),
                new UserAndLecturerData("lec0003", "ICT", "S.J.k RiDMI Kumarihami", "1992-07-19", "F", "ridmiKumarihami@gmailcom", 3),
                new UserAndLecturerData("lec0004", "ET", "K.M.K. Weerasnghe", "1993-01-25", "M", "weerasinghe@gmailcom", 4),
                new UserAndLecturerData("lec0005", "BST", "J.M. Chinthaka Kumara", "1994-11-03", "M", "chinathaka@gmailcom", 5),
                new UserAndLecturerData("lec0006", "BST", "S.M. Kavindu Perera", "1991-03-08", "F", "kavindu.perera@company.com", 6)
        };

        String address = "Faculty of Technology, University of Ruhuna";
        PasswordHash passwordHash = new PasswordHash("lec@123");

        try(Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);

            userDBLogic udbl = new userDBLogic();

            for (UserAndLecturerData u : users) {
                if (udbl.isEmailExists(conn, u.email)){
                    System.out.println("Email Already Exists: " + u.email);
                }
                udbl.insertUserAndLecturer(conn, u, address, passwordHash);
            }

            conn.commit();
            System.out.println("Lecturers inserted!");

        } catch (Exception e) {
            System.out.println("Error in inserting Users And Lecturers Data: " + e.getMessage());
        }
    }
}
