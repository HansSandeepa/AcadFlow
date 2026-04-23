package acadflow.models.users;

import acadflow.models.userDBLogic;
import acadflow.util.DBConnection;

import java.sql.Connection;

public class addNoticeData {

    public void addDataForNotice() {

        NoticeData[] data = {
                new NoticeData("Welcome to New Semester", "The new semester starts on January 15th. Please complete your registration by January 10th.",  "2026-04-22", "Students", 1),
                new NoticeData("Faculty Meeting", "All faculty members are requested to attend a meeting on Friday at 2 PM in the conference room.", "2026-04-13", "Lectures", 0),
                new NoticeData("Holiday Announcement", "The institute will remain closed on Monday for Republic Day celebration.", "2026-03-12", "All", 1),
                new NoticeData("Exam Schedule", "Mid-term examinations will begin from March 1st. Check the portal for detailed schedule.", "2026-03-22", "Students", 0),
                new NoticeData("Staff Training Program", "Mandatory training for all staff members on March 10th from 10 AM to 4 PM.", "2026-04-4", "Staff", 1),
        };

        String admin = "admin0001";

        try (Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);

            userDBLogic udbl = new userDBLogic();

            for (NoticeData d : data) {
                if (udbl.isNoticeExists(conn, d.noticeTitle)) {
                    System.out.println("Notice with title " + d.noticeTitle + " already exists");
                    continue;
                }

                udbl.insertNotices(conn, d, admin);
            }

            conn.commit();
            System.out.println("Successfully added notice data");
        } catch (Exception e) {
            System.out.println("Error while adding notice data: " + e.getMessage());
        }
    }
}
