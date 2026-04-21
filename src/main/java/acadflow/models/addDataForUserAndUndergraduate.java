package acadflow.models;

import acadflow.util.DBConnection;
import acadflow.util.PasswordHash;

import java.sql.Connection;

public class addDataForUserAndUndergraduate {

    public void addUserAndUndergraduateData() {
        UserAndUndergraduateData[] data = {
                new UserAndUndergraduateData("tg0001", "ICT", 8, 2, 1, "S.M.H.Lankathilaka", "2003-03-31", "M", "hirushalankathilaka@gmail.com"),
                new UserAndUndergraduateData("tg0002", "ICT", 7, 3, 2, "A.K.Perera", "2002-05-12", "F", "aperera@gmail.com"),
                new UserAndUndergraduateData("tg0003", "ICT", 6, 2, 1, "R.Silva", "2001-07-20", "M", "rsilva@gmail.com"),
                new UserAndUndergraduateData("tg0004", "ICT", 9, 1, 2, "N.Kumari", "2003-11-15", "F", "nkumari@gmail.com"),
                new UserAndUndergraduateData("tg0005", "ICT", 5, 2, 1, "D.Jayasinghe", "2002-02-28", "M", "djayasinghe@gmail.com"),

                new UserAndUndergraduateData("tg0006", "ET", 8, 2, 1, "P.Wijesinghe", "2001-09-10", "M", "pwijesinghe@gmail.com"),
                new UserAndUndergraduateData("tg0007", "ET", 7, 3, 2, "K.De Silva", "2003-04-05", "F", "kdesilva@gmail.com"),
                new UserAndUndergraduateData("tg0008", "ET", 6, 2, 1, "M.Fernando", "2002-06-18", "M", "mfernando@gmail.com"),
                new UserAndUndergraduateData("tg0009", "ET", 9, 1, 2, "T.Rathnayake", "2001-12-25", "F", "trathnayake@gmail.com"),
                new UserAndUndergraduateData("tg0010", "ET", 5, 2, 1, "H.Gunawardena", "2003-08-14", "M", "hgunawardena@gmail.com"),

                new UserAndUndergraduateData("tg0011", "BST", 8, 2, 1, "S.Abeysekera", "2002-03-09", "F", "sabeysekera@gmail.com"),
                new UserAndUndergraduateData("tg0012", "BST", 7, 3, 2, "C.Karunaratne", "2001-10-22", "M", "ckarunaratne@gmail.com"),
                new UserAndUndergraduateData("tg0013", "BST", 6, 2, 1, "B.Dissanayake", "2003-01-17", "F", "bdissanayake@gmail.com"),
                new UserAndUndergraduateData("tg0014", "BST", 9, 1, 2, "J.Samarasinghe", "2002-07-30", "M", "jsamarasinghe@gmail.com"),
                new UserAndUndergraduateData("tg0015", "BST", 5, 2, 1, "L.Rajapaksha", "2001-05-25", "F", "lrajapaksha@gmail.com"),

                new UserAndUndergraduateData("tg0016", "ICT", 7, 2, 1, "V.Bandara", "2003-09-02", "M", "vbandara@gmail.com"),
                new UserAndUndergraduateData("tg0017", "ET", 6, 3, 2, "Y.Amarasinghe", "2002-11-11", "F", "yamarasinghe@gmail.com"),
                new UserAndUndergraduateData("tg0018", "BST", 8, 1, 2, "G.Senanayake", "2001-04-07", "M", "gsenanayake@gmail.com"),
                new UserAndUndergraduateData("tg0019", "ICT", 9, 2, 1, "F.Kumuduni", "2002-12-19", "F", "fkumuduni@gmail.com"),
                new UserAndUndergraduateData("tg0020", "ET", 5, 3, 1, "O.Ranasinghe", "2003-06-23", "M", "oranasinghe@gmail.com")

        };

        String address = "Faculty of Technology, University of Ruhuna";
        PasswordHash passwordHash = new PasswordHash("stu@123");

        try(Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);

            userDBLogic udbl = new userDBLogic();

            for (UserAndUndergraduateData d : data) {
                if (udbl.isEmailExists(conn, d.email)){
                    System.out.println("Undergraduate Already Exists" + d.email);
                }
                udbl.insertUserAndUndergraduate(conn, d, address, passwordHash);
            }

            conn.commit();
            System.out.println("Undergraduates inserted!");

        } catch (Exception e) {
            System.out.println("Error in inserting Users And Undergraduates Data: " + e.getMessage());
        }
    }

}
