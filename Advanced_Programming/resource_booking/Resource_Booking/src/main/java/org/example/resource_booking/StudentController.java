package org.example.resource_booking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.resource_booking.mcp.CampusMcpClient;
import org.example.resource_booking.services.AuthService;
import org.example.resource_booking.models.Booking;
import org.example.resource_booking.repositories.BookingRepository;
import java.util.List;

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
    private StackPane contentArea;// Changed from AnchorPane to StackPane

    @FXML
    private AnchorPane contentArea2;

    @FXML
    private Label profileLabel;

    @FXML
    private VBox bookingListContainer;

    @FXML
    private Label recordCountLabel;

    // --- Resource Page Elements ---
    @FXML
    private TextField searchField; // Hooks up to the search bar

    @FXML
    private VBox resourceListContainer; // The VBox where we will inject data later

    private AuthService authService;
    private CampusMcpClient mcpClient;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        if (profileLabel != null
                && authService != null
                && authService.getCurrentUser() != null) {

            profileLabel.setText(authService.getCurrentUser().getFullName());
        }

    }

    public void setMcpClient(CampusMcpClient mcpClient) {
        this.mcpClient = mcpClient;
        System.out.println("StudentController received: " + mcpClient);
    }

    @FXML
    void onHomeClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home Page(Student).fxml"));
            Parent fxml = loader.load();
            HomePageControllerStudent controller = loader.getController();
            controller.setAuthService(this.authService);

            contentArea.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Home Page(Student).fxml. Check the file path!");
        }
    }

    @FXML
    void onResourceClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Resource Page(Student).fxml"));
            Parent root = loader.load();

            ResourcePageControllerStudent controller = loader.getController();

            System.out.println("Passing MCP: " + this.mcpClient);

            controller.setAuthService(authService);
            controller.setMcpClient(this.mcpClient);
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Resource Page(Student).fxml. Check the file path!");
        }
    }

    @FXML
    void onBookingClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Booking Page(Student).fxml"));
            Parent fxml = loader.load();

            StudentController controller = loader.getController();

            // Give the new controller the MCP client
            controller.setMcpClient(this.mcpClient);

            // Give it the auth service too
            controller.setAuthService(this.authService);

            controller.loadBookings();

            contentArea.getChildren().setAll(fxml);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Booking Page(Student).fxml. Check the file path!");
        }
    }

    @FXML
    void onBookNowClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Resource Page(Student).fxml"));
            Parent resourceView = loader.load();

            ResourcePageControllerStudent controller = loader.getController();
            controller.setAuthService(this.authService);
            controller.setMcpClient(this.mcpClient);

            Button clickedButton = (Button) event.getSource();
            Scene currentScene = clickedButton.getScene();
            StackPane mainContentArea = (StackPane) currentScene.lookup("#contentArea");

            if (mainContentArea != null) {
                mainContentArea.getChildren().setAll(resourceView);
            } else {
                System.err.println("Error: Could not find the main content area (#contentArea).");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Resource Page(Student).fxml. Check the file path!");
        }
    }

    @FXML
    private void onLogOut() {
        if (authService != null) {
            authService.logout();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMcpClient(this.mcpClient);
            javafx.stage.Stage stage = (javafx.stage.Stage) contentArea.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadBookings() {

        if (bookingListContainer == null) {
            System.out.println("bookingListContainer is null");
            return;
        }

        bookingListContainer.getChildren().clear();

        BookingRepository bookingRepository = new BookingRepository();

        String studentId = (authService != null && authService.getCurrentUser() != null)
                ? authService.getCurrentUser().getId()
                : "UNKNOWN";

        System.out.println("Current student ID: " + studentId);

        List<Booking> myBookings =
                bookingRepository.findBookingsByStudentId(studentId);

        if (recordCountLabel != null) {
            recordCountLabel.setText(myBookings.size() + " RECORDS");
        }

        if (myBookings.isEmpty()) {
            Label emptyLabel = new Label("No bookings found.");
            emptyLabel.setStyle("-fx-text-fill: white;");
            bookingListContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Booking booking : myBookings) {

            Label bookingLabel = new Label(
                    "Booking ID: " + booking.getBookingId()
                            + "\nResource: " + booking.getResourceId()
                            + "\nDate: " + booking.getBookingDate()
                            + "\nTime: " + booking.getStartTime()
                            + " - " + booking.getEndTime()
                            + "\nStatus: " + booking.getStatus()
            );

            bookingLabel.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 10;"
            );

            bookingListContainer.getChildren().add(bookingLabel);
        }
    }
}