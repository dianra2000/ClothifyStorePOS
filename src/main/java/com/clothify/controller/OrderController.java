package com.clothify.controller;

import com.clothify.dao.OrderDAO;
import com.clothify.dao.ProductDAO;
import com.clothify.dao.CustomerDAO;
import com.clothify.model.Order;
import com.clothify.model.Product;
import com.clothify.model.Customer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<Product> productCombo;
    @FXML private TextField quantityField;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colOrderNumber;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, LocalDateTime> colDate;
    @FXML private TableColumn<Order, Double> colTotal;

    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    @FXML private Button addItemButton;
    @FXML private Button checkoutButton;
    @FXML private Button clearButton;

    private OrderDAO orderDAO = new OrderDAO();
    private ProductDAO productDAO = new ProductDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup table columns
        colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colCustomer.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomer().getFullName()));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Load data
        loadCustomers();
        loadProducts();
        loadOrders();
    }

    private void loadCustomers() {
        ObservableList<Customer> customers = customerDAO.getAllCustomers();
        customerCombo.setItems(customers);
    }

    private void loadProducts() {
        ObservableList<Product> products = productDAO.getAllProducts();
        productCombo.setItems(products);
    }

    private void loadOrders() {
        ObservableList<Order> orders = orderDAO.getAllOrders();
        orderTable.setItems(orders);
    }

    @FXML
    private void handleAddItem() {
        // Implementation
    }

    @FXML
    private void handleCheckout() {
        // Implementation
    }

    @FXML
    private void handleClear() {
        // Implementation
    }
}