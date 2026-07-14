package org.example.resource_booking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.resource_booking.mcp.CampusMcpClient;
import java.io.IOException;

public class HelloApplication extends Application {
    public static CampusMcpClient mcpClient;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Attempting to connect to the Campus Server...");
        try {
            mcpClient = new CampusMcpClient("http://localhost:8080");
            mcpClient.connect();
        } catch (Exception e) {
            System.out.println("Failed to connect! Make sure the server is running.");
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setMcpClient(mcpClient);
        stage.setTitle("Resource Booking System");
        stage.setScene(scene);

        stage.show();
    }

    public void stop() {
        if (mcpClient != null) {
            mcpClient.close();
            System.out.println("Disconnected from server.");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}