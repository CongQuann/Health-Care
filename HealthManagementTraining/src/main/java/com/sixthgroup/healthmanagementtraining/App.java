package com.sixthgroup.healthmanagementtraining;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;


        Parent root = loadFXML("Dashboard");


        // Không đặt kích thước cố định, JavaFX sẽ tự động lấy từ FXML
        scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene(); // Cập nhật kích thước theo FXML
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        if (stage == null) {
            return;
        }
        Parent root = loadFXML(fxml);
        stage.getScene().setRoot(root);
        stage.sizeToScene(); // Điều chỉnh kích thước nếu root thay đổi
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
