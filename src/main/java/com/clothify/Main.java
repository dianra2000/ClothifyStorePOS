package com.clothify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.clothify.dao.DatabaseConnection;

public class Main extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load login screen - FIXED: Added / at beginning
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // Make window draggable
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            });

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            primaryStage.setTitle("Clothify Store POS - Login");
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DatabaseConnection.closePool();
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DatabaseConnection.closePool();
    }

    public static void main(String[] args) {
        launch(args);
    }
}