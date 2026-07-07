package org.example.UserReg;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/UserReg/UserRegView.fxml"));
        Scene scene = new Scene(loader.load());
        scene.setUserData(loader.getController());
        stage.setTitle("User Management");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(); }
}