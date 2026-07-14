package org.example.resource_booking.services;

import org.example.resource_booking.models.Booking;
import org.example.resource_booking.repositories.BookingRepository;
import java.util.List;

public class BookingService {
    private BookingRepository bookingRepository;

    public BookingService() {
        this.bookingRepository = new BookingRepository();
    }

    public List<Booking> getStudentBookings(String studentId) {
        return bookingRepository.findBookingsByStudentId(studentId);
    }

    public BookingRepository getBookingRepository() {
        return bookingRepository;
    }
}