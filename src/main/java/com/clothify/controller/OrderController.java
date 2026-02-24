package com.clothify.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import com.clothify.dao.*;
import com.clothify.model.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML
    private ComboBox<Customer> customerCombo;
    @FXML
    private ComboBox<Product> productCombo;
    @FXML
    private TextField quantityField;
    @FXML
    private TableView<OrderItem> cartTable;
    @FXML
    private TableColumn<OrderItem, String> colProduct;
    @FXML
    private TableColumn<OrderItem, Double> colPrice;
    @FXML
    private TableColumn<OrderItem, Integer> colQty;
    @FXML
    private TableColumn<OrderItem, Double> colSubtotal;
    @FXML
    private TableColumn<OrderItem, Void> colAction;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label taxLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private ComboBox<String> paymentMethodCombo;
    @FXML
    private TextArea notesArea;
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, String> colOrderNo;
    @FXML
    private TableColumn<Order, String> colCustomer;
    @FXML
    private TableColumn<Order, String> colDateTime;
    @FXML
    private TableColumn<Order, Double> colAmount;
    @FXML
    private TableColumn<Order, String> colStatus;

    private CustomerDAO customerDAO = new CustomerDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private ObservableList<OrderItem> cartItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupCartTable();
        setupOrderTable();
        loadData();
    }

    private void setupComboBoxes() {
        customerCombo.setItems(customerDAO.getAllCustomers());
        customerCombo.setCellFactory(param -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName() + " (" + item.getPhone() + ")");
                }
            }
        });

        productCombo.setItems(productDAO.getAllProducts());
        productCombo.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getProductName() + " - $" + item.getPrice() + " (Stock: " + item.getQuantity() + ")");
                }
            }
        });

        paymentMethodCombo.getItems().addAll("Cash", "Card", "Mobile");
        paymentMethodCombo.setValue("Cash");
    }

    private void setupCartTable() {
        colProduct.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getProduct().getProductName()));
        colPrice.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        colSubtotal.setCellFactory(col -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        cartTable.setItems(cartItems);
    }

    private void setupOrderTable() {
        colOrderNo.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        colCustomer.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getFullName()));
        colDateTime.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new SimpleStringProperty(cellData.getValue().getOrderDate().format(formatter));
        });
        colAmount.setCellValueFactory(new PropertyValueFactory<>("total"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
    }

    private void loadData() {
        orderTable.setItems(orderDAO.getAllOrders());
    }

    @FXML
    private void handleAddItem() {
        // Implementation
    }
}