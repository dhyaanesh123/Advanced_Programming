package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class HomePageControllerAdmin {

    @FXML
    private Label dateLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    public void initialize() {
        // This will now run perfectly when the Home Page is loaded!
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        dateLabel.setText(today.format(formatter));

        loadCalendar();
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
}
