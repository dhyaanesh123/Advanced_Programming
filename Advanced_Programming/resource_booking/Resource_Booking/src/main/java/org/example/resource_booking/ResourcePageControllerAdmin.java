package org.example.resource_booking;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.resource_booking.mcp.CampusMcpClient;
import org.example.resource_booking.models.Resource;
import org.example.resource_booking.models.ResourceStatus;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourcePageControllerAdmin {

    @FXML
    private VBox resourceListContainer;

    @FXML
    private TextField searchField;

    private final CampusMcpClient mcpClient = HelloApplication.mcpClient;

    private final List<Resource> resources = new ArrayList<>();

    private final Set<String> blockedIds = new HashSet<>();
    private static final String BLOCKED_FILE = "data/blocked_resources.csv";

    private static final Pattern TABLE_ROW = Pattern.compile("^(.+?)\\|(.+?)\\|(.+?)\\|(.+?)\\|.*$");

    public void initialize() {
        loadBlockedIds();
        loadResourcesFromMcp();
        refreshList();

        searchField.textProperty().addListener((obs, oldText, newText) -> refreshList());
    }

    private void loadResourcesFromMcp() {
        resources.clear();

        if (mcpClient == null) {
            showAlert("Not connected to the campus server.");
            return;
        }

        try {
            String text = mcpClient.readResource("campus://facilities");
            boolean inTable = false;

            for (String line : text.split("\\r?\\n")) {
                String trimmed = line.trim();

                if (trimmed.equals("[Bookable Resources]")) {
                    inTable = true;
                    continue;
                }
                if (!inTable) continue;
                if (trimmed.isEmpty() || trimmed.startsWith("[")) break;
                if (trimmed.startsWith("ROOM")) continue;

                Matcher m = TABLE_ROW.matcher(trimmed);
                if (m.matches()) {
                    String id = m.group(1).trim();
                    String type = m.group(2).trim();
                    String building = m.group(4).trim();

                    Resource resource = new Resource(id, id, friendlyBuilding(building), ResourceStatus.AVAILABLE, type);
                    if (blockedIds.contains(id)) {
                        resource.setStatus(ResourceStatus.BLOCKED);
                    }
                    resources.add(resource);
                }
            }
        } catch (Exception e) {
            showAlert("Could not read facilities from the campus server: " + e.getMessage());
        }
    }

    private String friendlyBuilding(String code) {
        switch (code) {
            case "D": return "Block D";
            case "E": return "Block E";
            case "LAB": return "Block D - Computer Labs";
            case "LIB": return "Library";
            case "OUT": return "Outdoor Sports";
            default: return code;
        }
    }

    private void refreshList() {
        resourceListContainer.getChildren().clear();
        resourceListContainer.setSpacing(10);

        String keyword = searchField.getText() == null ? "" : searchField.getText().toLowerCase();

        for (Resource resource : resources) {
            if (!resource.getName().toLowerCase().contains(keyword)) continue;
            resourceListContainer.getChildren().add(buildCard(resource));
        }
    }

    private HBox buildCard(Resource resource) {
        HBox card = new HBox(15);
        card.getStyleClass().add("resource-item");
        card.setAlignment(Pos.CENTER_LEFT);

        String type = resource.getType().toLowerCase();
        String iconText = "📍";
        if (type.contains("room")) iconText = "👥";
        else if (type.contains("pod")) iconText = "📖";
        else if (type.contains("lab")) iconText = "🖥️";
        else if (type.contains("court") || type.contains("sport")) iconText = "⚽";

        VBox iconBox = new VBox(new Label(iconText));
        iconBox.getStyleClass().add("resource-icon-box");
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPrefSize(45, 45);

        Label nameLabel = new Label(resource.getName());
        nameLabel.getStyleClass().add("resource-title");
        Label locationLabel = new Label(resource.getLocation());
        locationLabel.getStyleClass().add("resource-location");
        VBox textDetails = new VBox(3, nameLabel, locationLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(resource.getStatus().name());
        statusLabel.getStyleClass().add(resource.getStatus() == ResourceStatus.BLOCKED ? "status-blocked" : "status-available");

        Button toggleButton = new Button();
        toggleButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        if (resource.getStatus() == ResourceStatus.BLOCKED) {
            toggleButton.setText("Unblock");
            toggleButton.setOnAction(e -> toggleBlock(resource, ResourceStatus.AVAILABLE));
        } else {
            toggleButton.setText("Block");
            toggleButton.setOnAction(e -> toggleBlock(resource, ResourceStatus.BLOCKED));
        }

        VBox actionBox = new VBox(5, statusLabel, toggleButton);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(iconBox, textDetails, spacer, actionBox);
        return card;
    }

    private void toggleBlock(Resource resource, ResourceStatus newStatus) {
        resource.setStatus(newStatus);
        if (newStatus == ResourceStatus.BLOCKED) {
            blockedIds.add(resource.getId());
        } else {
            blockedIds.remove(resource.getId());
        }
        saveBlockedIds();
        refreshList();
    }

    private void loadBlockedIds() {
        File file = new File(BLOCKED_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) blockedIds.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading blocked resources: " + e.getMessage());
        }
    }

    private void saveBlockedIds() {
        File file = new File(BLOCKED_FILE);
        try {
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            for (String id : blockedIds) {
                writer.println(id);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving blocked resources: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }
}