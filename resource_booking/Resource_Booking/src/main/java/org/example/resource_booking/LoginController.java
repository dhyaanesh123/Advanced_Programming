package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    private boolean adminMode = false;
    public void initialize() {
        lbError.setVisible(false);
        setStudentMode();
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
        btnStudent.setStyle("-fx-background-color: white;");
        btnAdmin.setStyle("-fx-background-color: lightgray;");
    }

    private void setAdminMode() {
        adminMode = true;
        lbID.setText("Admin ID");
        tfID.setPromptText("Admin ID");
        tfID.clear();
        tfPassword.clear();
        lbError.setVisible(false);
        btnAdmin.setStyle("-fx-background-color: white;");
        btnStudent.setStyle("-fx-background-color: lightgray;");
    }

    @FXML
    private void handleLogin() {
        String id = tfID.getText().trim();
        String password = tfPassword.getText().trim();
/*        lbError.setVisible(false);

        if (id.isEmpty() || password.isEmpty()) {
            lbError.setText("Please enter both ID and Password.");
            lbError.setVisible(true);
            return;
        }
*/
        boolean loginSuccess;
        if (adminMode) {

            // Create admin.csv file ltr!!!!!!!!!!!!!!
            loginSuccess = validateLogin("admins.csv", id, password);
        } else {

            // Create students.csv file ltr!!!!!!!!!
            loginSuccess = validateLogin("students.csv", id, password);
        }

        if (loginSuccess) {
            if (adminMode) {
                openPage("Admin Page.fxml");
            } else {
                openPage("Student Page.fxml");
            }
        } else {
            lbError.setText("ID or Password is invalid!");
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

    private boolean validateLogin(String fileName, String id, String password) {

        //need to return false ltr.return true just for testing
        return true;
    }

    private void openPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open: " + fxmlFile);
            alert.showAndWait();
        }
    }
}