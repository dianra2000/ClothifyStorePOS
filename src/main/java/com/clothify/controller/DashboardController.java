package com.clothify.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.clothify.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private BorderPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String fullName = SessionManager.getInstance().getCurrentUser().getFullName();
        String role = SessionManager.getInstance().getCurrentUser().getRole();

        userLabel.setText("Welcome, " + fullName);
        roleLabel.setText("Role: " + role.toUpperCase());
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();

        try {
            Stage stage = (Stage) userLabel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setTitle("Clothify Store POS - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showProducts() {
        loadView("products");
    }

    @FXML
    private void showOrders() {
        loadView("orders");
    }

    @FXML
    private void showCustomers() {
        loadView("customers");
    }

    @FXML
    private void showReports() {
        loadView("reports");
    }

    private void loadView(String viewName) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + viewName + ".fxml"));
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}