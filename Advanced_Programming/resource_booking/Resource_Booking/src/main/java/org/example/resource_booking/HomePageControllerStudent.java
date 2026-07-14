package org.example.resource_booking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.resource_booking.models.Booking;
import org.example.resource_booking.models.User;
import org.example.resource_booking.services.AuthService;
import org.example.resource_booking.services.BookingService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class HomePageControllerStudent {
    @FXML
    private Label dateLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private VBox blockedResourceBox;

    private AuthService authService;
    private BookingService bookingService;
    private User currentUser;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        this.bookingService = new BookingService();

        if (currentUser != null) {
            welcomeLabel.setText("WELCOME BACK, " + currentUser.getFullName().toUpperCase());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            dateLabel.setText(LocalDate.now().format(formatter));
        }

        loadCalendar();
        loadReminders();
    }

    private void loadCalendar() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();

        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(3);
        calendarGrid.setVgap(3);

        String[] headers = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            Label header = new Label(headers[i]);
            header.setPrefWidth(70);
            header.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10;");
            calendarGrid.add(header, i, 0);
        }

        List<Booking> bookings = bookingService.getStudentBookings(currentUser.getId());

        LocalDate firstDay = LocalDate.of(today.getYear(), today.getMonth(), 1);
        int offset = firstDay.getDayOfWeek().getValue() - 1;
        int row = 1;
        int col = offset;

        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setPrefWidth(70);
            dayLabel.setPrefHeight(60);

            dayLabel.setStyle("-fx-background-color: #404040; -fx-text-fill: white; " +
                    "-fx-background-radius: 8; -fx-font-size: 16; -fx-font-weight: bold; -fx-alignment: center;");

            LocalDate date = LocalDate.of(today.getYear(), today.getMonth(), day);
            boolean hasBooking = bookings.stream().anyMatch(b -> b.getBookingDate().equals(date) && b.getStatus() == org.example.resource_booking.models.BookingStatus.CONFIRMED);

            if (hasBooking) {
                dayLabel.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-font-size: 16; -fx-font-weight: bold; -fx-alignment: center;");
            }

            if (day == today.getDayOfMonth()) {
                dayLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-font-size: 16; -fx-font-weight: bold; -fx-alignment: center;");
            }

            calendarGrid.add(dayLabel, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void loadReminders() {
        blockedResourceBox.getChildren().clear();

        if (currentUser == null) return;

        List<Booking> bookings = bookingService.getStudentBookings(currentUser.getId());
        LocalDate today = LocalDate.now();

        bookings.stream()
                .filter(b -> b.getBookingDate().isAfter(today) || b.getBookingDate().equals(today))
                .filter(b -> b.getStatus() == org.example.resource_booking.models.BookingStatus.CONFIRMED)
                .limit(5)
                .forEach(booking -> {
                    Label reminder = new Label("• " + booking.toString());
                    reminder.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
                    blockedResourceBox.getChildren().add(reminder);
                });

        if (blockedResourceBox.getChildren().isEmpty()) {
            Label noReminders = new Label("No upcoming bookings");
            noReminders.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
            blockedResourceBox.getChildren().add(noReminders);
        }
    }

    @FXML
    private void handleBookResource(ActionEvent event) {
        if (HelloApplication.mcpClient != null) {
            try {
                // Ask the server for its tools using the method in CampusMcpClient
                var tools = HelloApplication.mcpClient.listTools();
                System.out.println("SUCCESS! The server responded. It has " + tools.size() + " tools available.");
            } catch (Exception e) {
                System.out.println("Uh oh, failed to talk to the server: " + e.getMessage());
            }
        } else {
            System.out.println("The client is null. Connection was never established.");
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Resource Page(Student).fxml"));
            Parent resourceView = loader.load();

            ResourcePageControllerStudent controller = loader.getController();

// Pass everything the controller needs
            controller.setMcpClient(HelloApplication.mcpClient);

// If you have an AuthService here, pass it too
            controller.setAuthService(authService);

            // Get the button that was clicked to access the main Scene
            Button clickedButton = (Button) event.getSource();
            Scene currentScene = clickedButton.getScene();

            // Find the main contentArea from the layout using its ID
            StackPane mainContentArea = (StackPane) currentScene.lookup("#contentArea");

            // Swap the view
            if (mainContentArea != null) {
                mainContentArea.getChildren().setAll(resourceView);
            } else {
                System.err.println("Error: Could not find the main content area (#contentArea).");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Resource Page(Student).fxml");
        }
    }
}