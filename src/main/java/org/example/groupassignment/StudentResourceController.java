package org.example.groupassignment;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class StudentResourceController {

    @FXML
    private void handleHomeClick(ActionEvent event) {
        System.out.println("Home clicked");
    }

    @FXML
    private void handleBookingClick(ActionEvent event) {
        System.out.println("Booking clicked");
    }

    @FXML
    private void handleResourceClick(ActionEvent event) {
        System.out.println("Resource clicked");
    }

    @FXML
    private void handleLogOutClick(ActionEvent event) {
        System.out.println("Logging out");
    }

    @FXML
    private void handleBookNowClick(ActionEvent event) {
        System.out.println("Book Now clicked");
    }
}