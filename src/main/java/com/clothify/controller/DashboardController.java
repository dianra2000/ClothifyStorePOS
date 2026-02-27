package com.clothify.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.clothify.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label userLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private BorderPane mainPane;
    @FXML
    private StackPane contentArea;

    // Sidebar nav buttons
    @FXML
    private Button navHome;
    @FXML
    private Button navProducts;
    @FXML
    private Button navOrders;
    @FXML
    private Button navCustomers;
    @FXML
    private Button navReports;

    // Style constants
    private static final String ACTIVE_STYLE = "-fx-background-color: linear-gradient(to right, #1d4ed8, #2563eb);" +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-alignment: CENTER-LEFT; -fx-padding: 12 18;" +
            "-fx-cursor: hand; -fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.4), 8, 0, 0, 2);";

    private static final String INACTIVE_STYLE = "-fx-background-color: transparent; -fx-text-fill: #94a3b8;" +
            "-fx-font-size: 13px; -fx-alignment: CENTER-LEFT;" +
            "-fx-padding: 12 18; -fx-cursor: hand; -fx-background-radius: 10;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String fullName = SessionManager.getInstance().getCurrentUser().getFullName();
        String role = SessionManager.getInstance().getCurrentUser().getRole();
        userLabel.setText("Welcome, " + fullName);
        roleLabel.setText("Role: " + role.toUpperCase());
        // Home is active by default
        setActiveNav(navHome);
    }

    // ── Sets active highlight on clicked nav button ──
    private void setActiveNav(Button activeBtn) {
        Button[] allNavButtons = { navHome, navProducts, navOrders, navCustomers, navReports };
        for (Button btn : allNavButtons) {
            btn.setStyle(btn == activeBtn ? ACTIVE_STYLE : INACTIVE_STYLE);
        }
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
    private void showDashboardHome() {
        setActiveNav(navHome);
        mainPane.setCenter(contentArea);
    }

    @FXML
    private void showProducts() {
        setActiveNav(navProducts);
        loadView("products");
    }

    @FXML
    private void showOrders() {
        setActiveNav(navOrders);
        loadView("orders");
    }

    @FXML
    private void showCustomers() {
        setActiveNav(navCustomers);
        loadView("customers");
    }

    @FXML
    private void showReports() {
        setActiveNav(navReports);
        loadView("reports");
    }

    // Card click handlers (from home screen quick-action cards)
    @FXML
    private void showProductsClick(MouseEvent e) {
        showProducts();
    }

    @FXML
    private void showOrdersClick(MouseEvent e) {
        showOrders();
    }

    @FXML
    private void showCustomersClick(MouseEvent e) {
        showCustomers();
    }

    @FXML
    private void showReportsClick(MouseEvent e) {
        showReports();
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