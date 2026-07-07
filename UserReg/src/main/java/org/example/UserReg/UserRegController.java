package org.example.UserReg;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserRegController {

    @FXML private BorderPane listPage;
    @FXML private BorderPane registerPage;

    @FXML private Label studentCountLabel;
    @FXML private Label messageLabel;

    @FXML private Label homeLabel;
    @FXML private Label resourcesLabel;
    @FXML private Label userRegLabel;
    @FXML private Label profileLabel;
    @FXML private Label logoutLabel;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> idColumn;
    @FXML private TableColumn<User, String> programmeColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, Integer> bookingsColumn;

    @FXML private TextField fullNameField;
    @FXML private TextField studentIdField;
    @FXML private TextField programmeField;
    @FXML private TextField emailField;

    private static final String USERS_FILE = "data/users.csv";
    private static final String PROFILES_FILE = "data/student_profiles.csv";
    private static final String BOOKINGS_FILE = "data/bookings.csv";

    private final List<User> users = new ArrayList<>();

    @FXML
    public void initialize() {
        // link columns to user fields
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        programmeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));
        bookingsColumn.setCellValueFactory(new PropertyValueFactory<>("bookingCount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // lock headers from dragging
        idColumn.setReorderable(false);
        nameColumn.setReorderable(false);
        programmeColumn.setReorderable(false);
        bookingsColumn.setReorderable(false);
        statusColumn.setReorderable(false);

        // setup sidebar and load data
        setupSidebarNavigation();
        setupHoverEffects();
        loadUsers();
        refreshTable();
        showListPage();
    }

    // sidebar nav
    private void setupSidebarNavigation() {
        homeLabel.setOnMouseClicked(e -> {});
        resourcesLabel.setOnMouseClicked(e -> {});
        userRegLabel.setOnMouseClicked(e -> {});
        profileLabel.setOnMouseClicked(e -> {});
        logoutLabel.setOnMouseClicked(e -> handleLogout());
    }

    // hover effects
    private void setupHoverEffects() {
        addHover(homeLabel);
        addHover(resourcesLabel);
        addHover(userRegLabel);
        addHover(profileLabel);
        addHover(logoutLabel);
    }

    // hover logic
    private void addHover(Label label) {
        String baseStyle = label.getStyle();
        label.setOnMouseEntered(e -> label.setStyle(baseStyle + "-fx-text-fill: #3498db; -fx-cursor: hand;"));
        label.setOnMouseExited(e -> label.setStyle(baseStyle));
    }

    @FXML
    private void handleLogout() {
        // close the app
        System.exit(0);
    }

    @FXML
    private void onAddStudentClicked() {
        // clear inputs and show reg page
        messageLabel.setText("");
        fullNameField.clear();
        studentIdField.clear();
        programmeField.clear();
        emailField.clear();
        showRegisterPage();
    }

    @FXML
    private void onReturnClicked() {
        // back to student list
        showListPage();
    }

    private void showListPage() {
        // show list, hide reg
        listPage.setVisible(true);
        listPage.setManaged(true);
        registerPage.setVisible(false);
        registerPage.setManaged(false);
    }

    private void showRegisterPage() {
        // show reg, hide list
        listPage.setVisible(false);
        listPage.setManaged(false);
        registerPage.setVisible(true);
        registerPage.setManaged(true);
    }

    @FXML
    private void onRegisterClicked() {
        // get input text
        String fullName = fullNameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String programme = programmeField.getText().trim();
        String email = emailField.getText().trim();

        // check if empty
        if (fullName.isEmpty() || studentId.isEmpty() || programme.isEmpty() || email.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        // check name format
        for (char c : fullName.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') {
                showError("Full name must only contain letters and spaces.");
                return;
            }
        }

        // check id format
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

        // check for duplicate id
        for (User user : users) {
            if (user.getId().equals(studentId)) {
                showError("This Student ID is already registered.");
                return;
            }
        }

        // check email format
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        // default password
        String defaultPassword = "password_" + fullName.toLowerCase().replace(" ", "");

        // create new student
        User newUser = new User(studentId, defaultPassword, "STUDENT", fullName, email, programme);
        users.add(newUser);

        saveAllData();
        refreshTable();
        showListPage();
    }

    private void showError(String text) {
        messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        messageLabel.setText(text);
    }

    private void refreshTable() {
        // count bookings for each user
        for (User user : users) {
            int bookings = countBookingsFor(user.getId());
            user.setBookingCount(bookings);
        }
        userTable.setItems(FXCollections.observableArrayList(users));
        studentCountLabel.setText(users.size() + " registered students");
    }

    private void loadUsers() {
        File userFile = new File(USERS_FILE);
        File profileFile = new File(PROFILES_FILE);

        if (!userFile.exists()) {
            return;
        }

        // load auth data first
        try {
            Scanner userScanner = new Scanner(userFile);
            while (userScanner.hasNextLine()) {
                String line = userScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String id = parts[0].trim();
                    String pwd = parts[1].trim();
                    String role = parts[2].trim();

                    // skip admin accounts
                    if (role.equalsIgnoreCase("ADMIN")) {
                        continue;
                    }

                    // add placeholder student details
                    users.add(new User(id, pwd, role, "Unknown Student", "unknown@campus.edu", "General"));
                }
            }
            userScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found.");
        }

        // load profiles
        if (profileFile.exists()) {
            try {
                Scanner profileScanner = new Scanner(profileFile);
                while (profileScanner.hasNextLine()) {
                    String line = profileScanner.nextLine().trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        String email = parts[2].trim();
                        String prog = parts[3].trim();

                        // find match and update profile
                        for (User u : users) {
                            if (u.getId().equals(id)) {
                                u.fullName = name;
                                u.email = email;
                                u.programme = prog;
                                break;
                            }
                        }
                    }
                }
                profileScanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("Profiles file not found.");
            }
        }
    }

    private void saveAllData() {
        File userFile = new File(USERS_FILE);
        File profileFile = new File(PROFILES_FILE);

        try {
            if (userFile.getParentFile() != null && !userFile.getParentFile().exists()) {
                userFile.getParentFile().mkdirs();
            }

            // save auth data
            PrintWriter userWriter = new PrintWriter(new FileWriter(userFile));
            for (User user : users) {
                userWriter.println(user.toAuthCsvLine());
            }
            userWriter.close();

            // save profile data
            PrintWriter profileWriter = new PrintWriter(new FileWriter(profileFile));
            for (User user : users) {
                profileWriter.println(user.toProfileCsvLine());
            }
            profileWriter.close();

        } catch (IOException e) {
            System.out.println("Error saving system records.");
        }
    }

    private int countBookingsFor(String studentId) {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return 0;

        int count = 0;
        // find matches in bookings file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            // loop until file ends
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    if (parts[1].trim().equals(studentId)) {
                        count++;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading bookings.");
        }
        return count;
    }

    // user data class
    public static class User {
        private String id;
        private String password;
        private String role;
        private String fullName;
        private String email;
        private String programme;
        private int bookingCount;

        public User(String id, String password, String role, String fullName, String email, String programme) {
            this.id = id;
            this.password = password;
            this.role = role;
            this.fullName = fullName;
            this.email = email;
            this.programme = programme;
            this.bookingCount = 0;
        }

        public String getId() { return id; }
        public String getFullName() { return fullName; }
        public String getProgramme() { return programme; }
        public String getStatus() { return "ACTIVE"; }
        public int getBookingCount() { return bookingCount; }
        public void setBookingCount(int count) { this.bookingCount = count; }

        public String toAuthCsvLine() {
            return id + "," + password + "," + role;
        }

        public String toProfileCsvLine() {
            return id + "," + fullName + "," + email + "," + programme;
        }
    }
}