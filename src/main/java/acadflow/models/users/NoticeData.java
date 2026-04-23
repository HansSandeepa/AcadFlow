package acadflow.models.users;

public class NoticeData {
    public String noticeTitle;
    public String noticeContent;
    public String noticeDate;
    public String audience;
    public int isImportant;

    public NoticeData(String noticeTitle, String noticeContent, String noticeDate, String audience, int isImportant) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeDate = noticeDate;
        this.audience = audience;
        this.isImportant = isImportant;
    }

}
