package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class StudentController {

    // --- Side Menu Elements ---
    @FXML
    private Label home;

    @FXML
    private Label booking;

    @FXML
    private Label resource;

    @FXML
    private AnchorPane contentArea;

    // --- Resource Page Elements ---
    @FXML
    private TextField searchField; // Hooks up to the search bar

    @FXML
    private VBox resourceListContainer; // The VBox where we will inject data later

    @FXML
    void onResourceClicked(MouseEvent event){
        try {
            // Load the Student Resource Page
            Parent fxml = FXMLLoader.load(getClass().getResource("Resource Page(Student).fxml"));

            // Clear any existing content currently showing in the display area
            contentArea.getChildren().removeAll();

            // Set the newly loaded FXML view into the display area
            contentArea.getChildren().setAll(fxml);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Resource Page(Student).fxml. Check the file path!");
        }
    }

    @FXML
    void onHomeClicked(MouseEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("Home Page(Student).fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Home Page(Admin).fxml. Check the file path!");
        }
    }

    @FXML
    void onBookingClicked(MouseEvent event){
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("Booking Page(Student).fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Home Page(Admin).fxml. Check the file path!");
        }
    }

    @FXML
    private void onLogOut() {
        System.exit(0);
    }

    // During the Part B backend implementation, you will add a method here to
    // parse the CSV and inject the "Available" and "Booked" rooms dynamically
    // into the resourceListContainer.
}