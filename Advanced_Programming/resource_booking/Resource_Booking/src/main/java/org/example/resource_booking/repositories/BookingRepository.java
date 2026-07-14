package org.example.resource_booking.repositories;

import org.example.resource_booking.models.Booking;
import org.example.resource_booking.models.BookingStatus;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingRepository {
    private static final String BOOKINGS_FILE = "data/bookings.csv";
    private List<Booking> bookings;

    public BookingRepository() {
        this.bookings = new ArrayList<>();
        loadBookings();
    }

    public void loadBookings() {
        bookings.clear();
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    bookings.add(new Booking(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            LocalDate.parse(parts[3].trim()),
                            LocalTime.parse(parts[4].trim()),
                            LocalTime.parse(parts[5].trim()),
                            BookingStatus.valueOf(parts[6].trim())
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
    }

    public void saveAll() {
        File file = new File(BOOKINGS_FILE);
        file.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Booking booking : bookings) {
                writer.println(booking.toCsvLine());
            }
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
        saveAll();
    }

    public List<Booking> findBookingsByStudentId(String studentId) {
        return bookings.stream()
                .filter(b -> b.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Booking> findBookingsByResourceId(String resourceId) {
        return bookings.stream()
                .filter(b -> b.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }
    public List<Booking> findBookingsByResourceAndDate(
            String resourceId,
            LocalDate date) {

        return bookings.stream()
                .filter(b ->
                        b.getResourceId().equals(resourceId)
                                && b.getBookingDate().equals(date)
                                && b.getStatus() == BookingStatus.CONFIRMED
                )
                .collect(Collectors.toList());
    }

    public Booking findById(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }

    public void cancelBooking(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                booking.setStatus(BookingStatus.CANCELLED);
                saveAll();
                return;
            }
        }
    }

    public String generateBookingId() {
        return "BKG-" + (bookings.size() + 1001);
    }
}