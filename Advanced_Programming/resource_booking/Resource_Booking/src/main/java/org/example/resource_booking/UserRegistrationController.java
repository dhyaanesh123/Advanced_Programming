package org.example.resource_booking;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.example.resource_booking.models.User;
import org.example.resource_booking.repositories.UserRepository;

import java.util.List;

public class UserRegistrationController {
    @FXML
    private BorderPane listPage;
    @FXML
    private BorderPane registerPage;
    @FXML
    private Label studentCountLabel;
    @FXML
    private Label messageLabel;

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> idColumn;
    @FXML
    private TableColumn<User, String> programmeColumn;
    @FXML
    private TableColumn<User, String> statusColumn;
    @FXML
    private TableColumn<User, Integer> bookingsColumn;

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField studentIdField;
    @FXML
    private TextField programmeField;
    @FXML
    private TextField emailField;

    private UserRepository userRepository;
    private List<User> users;

    public void initialize() {
        userRepository = new UserRepository();
        users = userRepository.getAllStudents();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        programmeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));
        bookingsColumn.setCellValueFactory(new PropertyValueFactory<>("bookingCount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        refreshTable();
        showListPage();
    }

    @FXML
    private void onAddStudentClicked() {
        messageLabel.setText("");
        fullNameField.clear();
        studentIdField.clear();
        programmeField.clear();
        emailField.clear();
        showRegisterPage();
    }

    @FXML
    private void onReturnClicked() {
        showListPage();
    }

    private void showListPage() {
        listPage.setVisible(true);
        listPage.setManaged(true);
        registerPage.setVisible(false);
        registerPage.setManaged(false);
        refreshTable();
    }

    private void showRegisterPage() {
        listPage.setVisible(false);
        listPage.setManaged(false);
        registerPage.setVisible(true);
        registerPage.setManaged(true);
    }

    @FXML
    private void onRegisterClicked() {
        String fullName = fullNameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String programme = programmeField.getText().trim();
        String email = emailField.getText().trim();

        if (fullName.isEmpty() || studentId.isEmpty() || programme.isEmpty() || email.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        for (char c : fullName.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') {
                showError("Full name must only contain letters and spaces.");
                return;
            }
        }

        if (studentId.length() != 7 || !studentId.startsWith("0")) {
            showError("Student ID must be a 7-digit number starting with 0.");
            return;
        }

        for (char c : studentId.toCharArray()) {
            if (!Character.isDigit(c)) {
                showError("Student ID must be a 7-digit number starting with 0.");
                return;
            }
        }

        if (studentId.equals("0000000")) {
            showError("Student ID '0000000' is not allowed.");
            return;
        }

        if (userRepository.existsById(studentId)) {
            showError("This Student ID is already registered.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        String defaultPassword = "password_" + fullName.toLowerCase().replace(" ", "");
        User newUser = new User(studentId, defaultPassword, "STUDENT", fullName, email, programme);

        userRepository.addUser(newUser);
        users = userRepository.getAllStudents();

        refreshTable();
        showListPage();
        showSuccess("Student registered successfully!");
    }

    private void showError(String text) {
        messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        messageLabel.setText(text);
    }

    private void showSuccess(String text) {
        messageLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        messageLabel.setText(text);
    }

    private void refreshTable() {
        users = userRepository.getAllStudents();
        userTable.setItems(FXCollections.observableArrayList(users));
        studentCountLabel.setText(users.size() + " registered students");
    }
}