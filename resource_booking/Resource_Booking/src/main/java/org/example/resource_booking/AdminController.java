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

public class AdminController {

    // --- Side Menu Elements ---
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
            Parent fxml = FXMLLoader.load(getClass().getResource("Resource Page(Admin).fxml"));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Resource Page(Admin).fxml. Check the file path!");
        }
    }

    // In Part B of your project, you will create a method here (like 'loadResources()')
    // to read from your text files, generate new HBox items via code, and add them
    // to the 'resourceListContainer' automatically.
}