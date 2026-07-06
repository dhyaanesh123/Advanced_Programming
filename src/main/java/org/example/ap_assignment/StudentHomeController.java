package org.example.ap_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.YearMonth;

public class StudentHomeController {
    @FXML
    private Label homeLabel;
    @FXML
    private Label bookingLabel;
    @FXML
    private Label resourcesLabel;
    @FXML
    private Label profileLabel;
    @FXML
    private Label logoutLabel;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private VBox reminderBox;
    @FXML
    private Button chatButton;

    @FXML
    public void initialize() {
        loadCalendar();
        loadReminders();
        setupSidebarNavigation();
        setupHoverEffects();
        chatButton.setOnAction(e -> {
            //AI pop up
        });
    }

    private void setupSidebarNavigation() {
        homeLabel.setOnMouseClicked(e -> {
            // stay home
        });
        bookingLabel.setOnMouseClicked(e -> {
            // navigate to booking page
        });
        resourcesLabel.setOnMouseClicked(e -> {
            // navigate to resource page
        });
        profileLabel.setOnMouseClicked(e -> {
            // navigate to profile
        });
        logoutLabel.setOnMouseClicked(e -> {
            handleLogout();
        });
    }

    private void setupHoverEffects() {
        addHover(homeLabel);
        addHover(bookingLabel);
        addHover(resourcesLabel);
        addHover(profileLabel);
        addHover(logoutLabel);
    }

    private void addHover(Label label) {
        String baseStyle = label.getStyle();
        label.setOnMouseEntered(e -> {
            label.setStyle(baseStyle + "-fx-text-fill: #3498db; -fx-cursor: hand;");
        });
        label.setOnMouseExited(e -> {
            label.setStyle(baseStyle);
        });
    }

    @FXML
    private void handleLogout() {
        System.exit(0);
    }

    @FXML
    private void handleBookResource() {
        // navigate to Booking page
    }

    private void loadCalendar() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(3);
        calendarGrid.setVgap(3);
        LocalDate firstDay = LocalDate.of(today.getYear(), today.getMonth(), 1);
        int offset = firstDay.getDayOfWeek().getValue() - 1;
        String[] headers = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            Button btn = new Button(headers[i]);
            btn.setDisable(true);
            btn.setPrefWidth(43);
            btn.setStyle("-fx-background-color:transparent;" + "-fx-text-fill:white;" + "-fx-font-weight:bold;" + "-fx-font-size:10;");
            calendarGrid.add(btn, i, 0);
        }
        int row = 1;
        int col = offset;
        for (int day = 1; day <= daysInMonth; day++) {
            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setPrefWidth(43);
            dayBtn.setPrefHeight(36);
            dayBtn.setStyle("-fx-background-color:#404040;" + "-fx-text-fill:white;" + "-fx-background-radius:5;" + "-fx-font-size:12;");
            if (day == today.getDayOfMonth()) {
                dayBtn.setStyle("-fx-background-color:#2196F3;" + "-fx-text-fill:white;" + "-fx-background-radius:5;" + "-fx-font-size:12;");
            }
            calendarGrid.add(dayBtn, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void loadReminders() {
        Label emptyLabel = new Label("No reminders yet. Book a resource!");
        emptyLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14px;");
        reminderBox.getChildren().add(emptyLabel);
    }

}
