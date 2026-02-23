package com.clothify.util;

import com.clothify.dao.DatabaseConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerator {

    public static void generateReport(String reportType, LocalDate startDate, LocalDate endDate) {
        try {
            String reportPath = "";
            Map<String, Object> parameters = new HashMap<>();

            parameters.put("startDate", java.sql.Date.valueOf(startDate));
            parameters.put("endDate", java.sql.Date.valueOf(endDate));

            switch (reportType) {
                case "Sales Report":
                    reportPath = "/reports/sales_report.jrxml";
                    break;
                case "Inventory Report":
                    reportPath = "/reports/inventory_report.jrxml";
                    break;
                case "Customer Report":
                    reportPath = "/reports/customer_report.jrxml";
                    break;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(
                    ReportGenerator.class.getResourceAsStream(reportPath)
            );

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    DatabaseConnection.getConnection()
            );

            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}