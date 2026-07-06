module org.example.ap_assignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.ap_assignment to javafx.fxml;
    exports org.example.ap_assignment;
}