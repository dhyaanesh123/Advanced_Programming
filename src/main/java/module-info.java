module org.example.groupassignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.groupassignment to javafx.fxml;
    exports org.example.groupassignment;
}