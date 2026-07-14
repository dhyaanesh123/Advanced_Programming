package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.resource_booking.services.AuthService;
import org.example.resource_booking.mcp.CampusMcpClient;
import java.io.IOException;

public class LoginController {
    @FXML
    private Button btnStudent;
    @FXML
    private Button btnAdmin;
    @FXML
    private Button btnLogin;
    @FXML
    private TextField tfID;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private Label lbID;
    @FXML
    private Label lbError;

    private AuthService authService;
    private CampusMcpClient mcpClient;
    private boolean adminMode = false;

    public void initialize() {
        authService = new AuthService();
        lbError.setVisible(false);
        setStudentMode();
    }

    public void setMcpClient(CampusMcpClient mcpClient) {
        this.mcpClient = mcpClient;
    }

    @FXML
    private void handleStudentTab() {
        setStudentMode();
    }

    @FXML
    private void handleAdminTab() {
        setAdminMode();
    }

    private void setStudentMode() {
        adminMode = false;
        lbID.setText("Student ID");
        tfID.setPromptText("Student ID");
        tfID.clear();
        tfPassword.clear();
        lbError.setVisible(false);
        btnStudent.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnAdmin.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
    }

    private void setAdminMode() {
        adminMode = true;
        lbID.setText("Admin ID");
        tfID.setPromptText("Admin ID");
        tfID.clear();
        tfPassword.clear();
        lbError.setVisible(false);
        btnAdmin.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnStudent.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black;");
    }

    @FXML
    private void handleLogin() {
        String id = tfID.getText().trim();
        String password = tfPassword.getText().trim();

        if (id.isEmpty() || password.isEmpty()) {
            lbError.setText("Please enter both ID and Password.");
            lbError.setVisible(true);
            return;
        }
        // Validate student ID format
        if (!adminMode) {
            if (!id.matches("0\\d{6}") || id.equals("0000000")) {
                lbError.setText("Student ID must be a 7-digit number starting with '0'.");
                lbError.setVisible(true);
                return;
            }
        } else {
            if (id.trim().isEmpty()) {
                lbError.setText("Admin ID cannot be empty.");
                lbError.setVisible(true);
                return;
            }
        }

        boolean loginSuccess = authService.login(id, password);
        if (loginSuccess) {
            try {
                String fxmlFile = adminMode ? "Admin Page.fxml" : "Student Page.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();
                if (!adminMode) {
                    StudentController controller = loader.getController();
                    controller.setAuthService(authService);
                    controller.setMcpClient(this.mcpClient);
                } else {
                    AdminController controller = loader.getController();
                    controller.setAuthService(authService);
                }
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                lbError.setText("Error loading page: " + e.getMessage());
                lbError.setVisible(true);
            }
        } else {
            lbError.setText("Invalid ID or Password!");
            lbError.setVisible(true);
        }
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact the administrator to reset your password.");
        alert.showAndWait();
    }
}