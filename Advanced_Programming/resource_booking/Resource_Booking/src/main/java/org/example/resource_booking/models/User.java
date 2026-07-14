package org.example.resource_booking.models;

public class User {
    private String id;
    private String password;
    private Role role;
    private String fullName;
    private String email;
    private String programme;
    private int bookingCount;

    public User(String id, String password, String role, String fullName, String email, String programme) {
        this.id = id;
        this.password = password;
        this.role = Role.valueOf(role.toUpperCase());
        this.fullName = fullName;
        this.email = email;
        this.programme = programme;
        this.bookingCount = 0;
    }

    public User(String id, String password, Role role) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.fullName = "";
        this.email = "";
        this.programme = "";
        this.bookingCount = 0;
    }

    public String getId(){
        return id;
    }
    public String getPassword(){
        return password;
    }
    public Role getRole(){
        return role;
    }
    public String getFullName(){
        return fullName;
    }
    public String getEmail(){
        return email;
    }
    public String getProgramme(){
        return programme;
    }
    public int getBookingCount(){
        return bookingCount;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setRole(Role role){
        this.role = role;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setProgramme(String programme){
        this.programme = programme;
    }
    public void setBookingCount(int bookingCount){
        this.bookingCount = bookingCount;
    }

    public String toAuthCsvLine() {
        return id + "," + password + "," + role.name();
    }

    public String toProfileCsvLine() {
        return id + "," + fullName + "," + email + "," + programme;
    }
}