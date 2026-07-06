module org.example.resource_booking {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.resource_booking to javafx.fxml;
    exports org.example.resource_booking;
}