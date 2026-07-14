package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import org.example.resource_booking.services.AuthService;

public class AdminController {
    @FXML
    private Label resource;

    @FXML
    private StackPane contentArea;

    @FXML
    private TextField searchField; // Hooks up to the search bar

    @FXML
    private Label profileLabel;

    @FXML
    private VBox resourceListContainer; // The VBox where we will inject data later

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (authService.getCurrentUser() != null) {

            String displayName = authService.getCurrentUser().getFullName();

            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = "ADMIN";
            }

            profileLabel.setText("👤 " + displayName);
        }
        onHomeClicked(null);
    }

    @FXML
    void onHomeClicked(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home Page(Admin).fxml"));
            Parent fxml = loader.load();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Home Page(Admin).fxml. Check the file path!");
        }
    }

    @FXML
    void onResourceClicked(MouseEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Resource Page(Admin).fxml"));
            Parent fxml = loader.load();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Resource Page(Admin).fxml. Check the file path!");
        }
    }

    @FXML
    void onRegistrationClicked(MouseEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("User Registration(Admin).fxml"));
            Parent fxml = loader.load();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load User Registration(Admin).fxml. Check the file path!");
        }
    }

    @FXML
    private void onLogOut() {
        if (authService != null) {
            authService.logout();
        }
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) contentArea.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}