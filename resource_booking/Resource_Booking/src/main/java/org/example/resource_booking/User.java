package org.example.resource_booking;

public class User {
    protected String id;
    protected String password;
    protected String role;
    protected String fullName;
    protected String email;
    protected String programme;
    protected int bookingCount;

    public User(String id, String password, String role, String fullName, String email, String programme) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.programme = programme;
        this.bookingCount = 0;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getProgramme() { return programme; }
    public String getStatus() { return "ACTIVE"; }
    public int getBookingCount() { return bookingCount; }
    public void setBookingCount(int count) { this.bookingCount = count; }

    public String toAuthCsvLine() {
        return id + "," + password + "," + role;
    }

    public String toProfileCsvLine() {
        return id + "," + fullName + "," + email + "," + programme;
    }
}
