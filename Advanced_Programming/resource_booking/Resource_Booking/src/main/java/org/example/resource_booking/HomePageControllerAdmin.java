package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class HomePageControllerAdmin {

    @FXML
    private Label dateLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private VBox blockedResourceBox;

    private static final String BLOCKED_FILE = "data/blocked_resources.csv";

    @FXML
    public void initialize() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        dateLabel.setText(today.format(formatter));

        loadCalendar();
        loadBlockedResources();
    }

    // reads data/blocked_resources.csv and lists each blocked resource id
    private void loadBlockedResources() {
        blockedResourceBox.getChildren().clear();

        File file = new File(BLOCKED_FILE);
        if (!file.exists()) {
            blockedResourceBox.getChildren().add(placeholderLabel());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean anyBlocked = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                anyBlocked = true;
                Label row = new Label(line);
                row.getStyleClass().add("status-blocked");
                blockedResourceBox.getChildren().add(row);
            }

            if (!anyBlocked) {
                blockedResourceBox.getChildren().add(placeholderLabel());
            }
        } catch (IOException e) {
            System.out.println("Error reading blocked resources: " + e.getMessage());
        }
    }

    private Label placeholderLabel() {
        Label label = new Label("No resources blocked");
        label.setStyle("-fx-text-fill: #888888;");
        return label;
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
            btn.setPrefWidth(70);
            btn.setStyle("-fx-background-color:transparent;" + "-fx-text-fill:white;" + "-fx-font-weight:bold;" + "-fx-font-size:14;");
            calendarGrid.add(btn, i, 0);
        }

        int row = 1;
        int col = offset;
        for (int day = 1; day <= daysInMonth; day++) {
            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setPrefWidth(70);
            dayBtn.setPrefHeight(60);
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
}