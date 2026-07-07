module org.example.UserReg {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.UserReg to javafx.fxml;
    exports org.example.UserReg;
}