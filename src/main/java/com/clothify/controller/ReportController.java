package com.clothify.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import com.clothify.util.ReportGenerator;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> reportTypeCombo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());

        reportTypeCombo.getItems().addAll(
                "Sales Report",
                "Inventory Report",
                "Customer Report",
                "Product Report"
        );
        reportTypeCombo.setValue("Sales Report");
    }

    @FXML
    private void handleGenerateReport() {
        String reportType = reportTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select date range!");
            return;
        }

        ReportGenerator.generateReport(reportType, startDate, endDate);
    }

    @FXML
    private void handlePrintReport() {
        showAlert(Alert.AlertType.INFORMATION, "Info", "Print feature coming soon!");
    }

    @FXML
    private void handleExportPDF() {
        showAlert(Alert.AlertType.INFORMATION, "Info", "PDF export coming soon!");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}