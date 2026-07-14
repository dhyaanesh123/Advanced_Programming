package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import org.example.resource_booking.mcp.CampusMcpClient;
import org.example.resource_booking.models.BookingStatus;
import org.example.resource_booking.models.Resource;
import org.example.resource_booking.models.ResourceStatus;
import org.example.resource_booking.repositories.BookingRepository;
import org.example.resource_booking.services.AuthService;
import org.example.resource_booking.models.Booking;
import org.example.resource_booking.models.BookingStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ResourcePageControllerStudent {
    @FXML
    private VBox resourceListContainer;
    private BookingRepository bookingRepository;
    private AuthService authService;
    private CampusMcpClient mcpClient = HelloApplication.mcpClient;

    // ids that admin has blocked - read only, admin's page owns writing this file
    private final Set<String> blockedIds = new HashSet<>();
    private static final String BLOCKED_FILE = "data/blocked_resources.csv";

    public void setMcpClient(CampusMcpClient mcpClient) {
        if (mcpClient != null) {
            this.mcpClient = mcpClient;
        }
        System.out.println("ResourcePageControllerStudent received: " + mcpClient);
    }

    public void initialize() {
        bookingRepository = new BookingRepository();
        loadBlockedIds();
        loadResourceCards();
        System.out.println("initialize(): mcpClient = " + mcpClient);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    private void loadResourceCards() {
        resourceListContainer.getChildren().clear();
        resourceListContainer.setSpacing(10);
        // Fetch all resources from the campus MCP server instead of the CSV
        List<Resource> resources = getResourcesFromMcp();

        // Loop through and create a UI card for each one
        for (Resource resource : resources) {
            HBox card = new HBox(15);
            card.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 15; -fx-background-radius: 8;");
            card.setPrefHeight(80);
            card.setAlignment(Pos.CENTER_LEFT);

            String type = resource.getType().toLowerCase();
            String iconText = "📍";
            if (type.contains("room")) iconText = "👥";
            else if (type.contains("pod")) iconText = "📖";
            else if (type.contains("lab")) iconText = "🖥️";
            else if (type.contains("court") || type.contains("sport")) iconText = "⚽";

            Label icon = new Label(iconText);
            icon.setStyle("-fx-font-size: 30;");

            VBox textDetails = new VBox(5);
            textDetails.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(resource.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");

            Label locationLabel = new Label(resource.getLocation());
            locationLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");

            textDetails.getChildren().addAll(nameLabel, locationLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox actionBox = new VBox(5);
            actionBox.setAlignment(Pos.CENTER_RIGHT);

            Label statusLabel = new Label(resource.getStatus().name());
            Button bookBtn = new Button();

            if (resource.getStatus() == ResourceStatus.AVAILABLE) {
                statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12; -fx-font-weight: bold;");
                bookBtn.setText("Book Now");
                bookBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;");

                bookBtn.setOnAction(e -> showBookingDialog(resource));

            } else {
                statusLabel.setStyle("-fx-text-fill: #F44336; -fx-font-size: 12; -fx-font-weight: bold;");
                bookBtn.setText("Unavailable");
                bookBtn.setStyle("-fx-background-color: #555555; -fx-text-fill: #888888; -fx-background-radius: 5;");
                // Prevents clicking
                bookBtn.setDisable(true);
            }
            actionBox.getChildren().addAll(statusLabel, bookBtn);

            card.getChildren().addAll(icon, textDetails, spacer, actionBox);

            resourceListContainer.getChildren().add(card);
        }
    }

    // Reads campus://facilities from the MCP server and builds the resource list from its room table
    private List<Resource> getResourcesFromMcp() {
        List<Resource> resources = new ArrayList<>();

        if (mcpClient == null) {
            System.out.println("Not connected to the campus server.");
            return resources;
        }

        try {
            String text = mcpClient.readResource("campus://facilities");
            boolean inTable = false;

            for (String line : text.split("\\r?\\n")) {
                String trimmed = line.trim();

                // The room table starts after this header line
                if (trimmed.equals("[Bookable Resources]")) {
                    inTable = true;
                    continue;
                }
                if (!inTable) continue;
                if (trimmed.isEmpty() || trimmed.startsWith("[")) break; // end of table
                if (trimmed.startsWith("ROOM")) continue; // skip the column header row

                // Each row looks like: KA-P1 | study_pod | 2 | LIB | 07:00 | 23:00
                String[] parts = trimmed.split("\\|");
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String type = parts[1].trim();
                    String building = parts[3].trim();

                    ResourceStatus status = blockedIds.contains(id) ? ResourceStatus.BLOCKED : ResourceStatus.AVAILABLE;
                    resources.add(new Resource(id, id, friendlyBuilding(building), status, type));
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read facilities from the campus server: " + e.getMessage());
        }

        return resources;
    }

    private String friendlyBuilding(String code) {
        switch (code) {
            case "D": return "Block D";
            case "E": return "Block E";
            case "LAB": return "Block D - Computer Labs";
            case "LIB": return "Library";
            case "OUT": return "Outdoor Sports";
            default: return code;
        }
    }

    // Reads the list of resource ids that admin has blocked
    private void loadBlockedIds() {
        File file = new File(BLOCKED_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) blockedIds.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading blocked resources: " + e.getMessage());
        }
    }

    private void fetchLatestBookings() {
        try {
            String response = mcpClient.callTool("search_campus_info", java.util.Map.of("query", "What are my current bookings?"));
            System.out.println("SERVER DATA: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBookingDialog(Resource resource) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Book Campus Resource");
        dialog.setHeaderText("Booking: " + resource.getName()
                + "\nLocation: " + resource.getLocation());

        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.setPromptText("Select Time");

        // Load times immediately when dialog opens
        loadAvailableTimes(timeComboBox, datePicker.getValue());

        // Reload times when user changes the date
        datePicker.setOnAction(e -> {
            System.out.println("Date selected: " + datePicker.getValue());
            loadAvailableTimes(timeComboBox, datePicker.getValue());
        });

        VBox layout = new VBox(
                10,
                new Label("Select Date:"),
                datePicker,
                new Label("Select Time:"),
                timeComboBox
        );

        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (timeComboBox.getValue() == null) {
                Alert alert = new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a booking time."
                );
                alert.showAndWait();
                return;
            }

            String studentId =
                    (authService != null && authService.getCurrentUser() != null)
                            ? authService.getCurrentUser().getId()
                            : "UNKNOWN";

            try {

                this.mcpClient.callTool("check_room_availability",
                        java.util.Map.of(
                                "date", datePicker.getValue().toString(),
                                "building", "LIB"
                        )
                );


                String selectedTime = timeComboBox.getValue();

                LocalTime newStart = LocalTime.parse(selectedTime);
                LocalTime newEnd = newStart.plusHours(2);


// Check if this resource is already booked at this date/time
                List<Booking> existingBookings =
                        bookingRepository.findBookingsByResourceAndDate(
                                resource.getId(),
                                datePicker.getValue()
                        );


                for (Booking existing : existingBookings) {

                    boolean overlap =
                            newStart.isBefore(existing.getEndTime())
                                    && newEnd.isAfter(existing.getStartTime());


                    if (overlap) {

                        Alert conflictAlert = new Alert(
                                Alert.AlertType.ERROR,
                                "This resource is already booked from "
                                        + existing.getStartTime()
                                        + " to "
                                        + existing.getEndTime()
                        );

                        conflictAlert.showAndWait();
                        return;
                    }
                }


                String serverResponse = this.mcpClient.callTool(
                        "book_resource",
                        java.util.Map.of(
                                "studentId", studentId,
                                "resourceId", resource.getId(),
                                "date", datePicker.getValue().toString(),
                                "startTime", selectedTime,
                                "endTime", newEnd.toString()
                        )
                );


                if (serverResponse.contains("Booking confirmed")) {

                    Booking newBooking = new Booking(
                            bookingRepository.generateBookingId(),
                            studentId,
                            resource.getId(),
                            datePicker.getValue(),
                            LocalTime.parse(selectedTime),
                            LocalTime.parse(selectedTime).plusHours(2),
                            BookingStatus.CONFIRMED
                    );

                    bookingRepository.addBooking(newBooking);


                    Alert successAlert = new Alert(
                            Alert.AlertType.INFORMATION,
                            serverResponse
                    );

                    successAlert.showAndWait();

                    loadResourceCards();

                } else {

                    Alert errorAlert = new Alert(
                            Alert.AlertType.ERROR,
                            "Server returned: " + serverResponse
                    );

                    errorAlert.showAndWait();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void loadAvailableTimes(ComboBox<String> timeComboBox, LocalDate date) {

        timeComboBox.getItems().setAll(
                "08:00",
                "10:00",
                "12:00",
                "14:00",
                "16:00",
                "18:00"
        );

        try {

            this.mcpClient.callTool(
                    "check_room_availability",
                    java.util.Map.of(
                            "date", date.toString()
                    )
            );

            System.out.println("Available times loaded for: " + date);


        } catch (Exception ex) {

            System.out.println(
                    "Error fetching availability: "
                            + ex.getMessage()
            );
        }
    }
}