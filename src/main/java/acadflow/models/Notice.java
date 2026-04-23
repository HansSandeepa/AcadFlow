package acadflow.models;

import java.time.LocalDate;

public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private LocalDate date;
    private String adminId;
    private String audience;
    private boolean isImportant;

    public Notice(int noticeId, String title, String content, LocalDate date,
                  String adminId, String audience, boolean isImportant) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.date = date;
        this.adminId = adminId;
        this.audience = audience;
        this.isImportant = isImportant;
    }


    public int getNoticeId() { return noticeId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDate getDate() { return date; }
    public String getAdminId() { return adminId; }
    public String getAudience() { return audience; }
    public boolean isImportant() { return isImportant; }


    public void setNoticeId(int noticeId) { this.noticeId = noticeId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public void setAudience(String audience) { this.audience = audience; }
    public void setImportant(boolean important) { isImportant = important; }
}