package org.example.resource_booking.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private String bookingId;
    private String studentId;
    private String resourceId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus status;

    public Booking(String bookingId, String studentId, String resourceId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime, BookingStatus status) {
        this.bookingId = bookingId;
        this.studentId = studentId;
        this.resourceId = resourceId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public String getStudentId() { return studentId; }
    public String getResourceId() { return resourceId; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public BookingStatus getStatus() { return status; }

    public void setStatus(BookingStatus status) { this.status = status; }

    // Used by BookingRepository to save to bookings.csv
    public String toCsvLine() {
        return bookingId + "," + studentId + "," + resourceId + "," + bookingDate + "," + startTime + "," + endTime + "," + status.name();
    }

    // Used by the Home Page to display the reminder text
    @Override
    public String toString() {
        return "Resource " + resourceId + " on " + bookingDate + " (" + startTime + " - " + endTime + ")";
    }
}