package com.clothify.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.clothify.dao.UserDAO;
import com.clothify.model.User;
import com.clothify.util.SessionManager;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            loadDashboard();
        } else {
            messageLabel.setText("Invalid username or password");
            passwordField.clear();
        }
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }

    private void loadDashboard() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Clothify Store POS - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard");
        }
    }
}