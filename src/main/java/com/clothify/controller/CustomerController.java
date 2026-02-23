package com.clothify.controller;

import com.clothify.dao.CustomerDAO;
import com.clothify.model.Customer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextArea addressArea;
    @FXML private TextField loyaltyPointsField;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colCode;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, Integer> colPoints;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Customer> customerList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup table columns
        colCode.setCellValueFactory(new PropertyValueFactory<>("customerCode"));
        colName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));

        // Load data
        loadCustomers();

        // Add listener for table selection
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        displayCustomerDetails(newSelection);
                    }
                });

        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                loadCustomers();
            } else {
                searchCustomers(newVal);
            }
        });

        // Enable/disable buttons based on selection
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void loadCustomers() {
        customerList = customerDAO.getAllCustomers();
        customerTable.setItems(customerList);
    }

    private void searchCustomers(String searchTerm) {
        ObservableList<Customer> searchResults = customerDAO.searchCustomers(searchTerm);
        customerTable.setItems(searchResults);
    }

    private void displayCustomerDetails(Customer customer) {
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
        addressArea.setText(customer.getAddress());
        loyaltyPointsField.setText(String.valueOf(customer.getLoyaltyPoints()));

        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        addButton.setDisable(false);
    }

    @FXML
    private void handleAdd() {
        if (!validateInputs()) return;

        Customer customer = new Customer();
        customer.setFirstName(firstNameField.getText().trim());
        customer.setLastName(lastNameField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setAddress(addressArea.getText().trim());
        customer.setLoyaltyPoints(0);

        boolean success = customerDAO.addCustomer(customer);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully!");
            clearForm();
            loadCustomers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add customer!");
        }
    }

    @FXML
    private void handleUpdate() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a customer to update!");
            return;
        }

        if (!validateInputs()) return;

        selected.setFirstName(firstNameField.getText().trim());
        selected.setLastName(lastNameField.getText().trim());
        selected.setPhone(phoneField.getText().trim());
        selected.setEmail(emailField.getText().trim());
        selected.setAddress(addressArea.getText().trim());

        boolean success = customerDAO.updateCustomer(selected);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully!");
            clearForm();
            loadCustomers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update customer!");
        }
    }

    @FXML
    private void handleDelete() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a customer to delete!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete " + selected.getFullName() + "?",
                ButtonType.YES, ButtonType.NO);

        if (confirm.showAndWait().get() == ButtonType.YES) {
            boolean success = customerDAO.deleteCustomer(selected.getCustomerId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer deleted successfully!");
                clearForm();
                loadCustomers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete customer!");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
        addressArea.clear();
        loyaltyPointsField.clear();

        customerTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }

    private boolean validateInputs() {
        if (firstNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "First name is required!");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Last name is required!");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Phone number is required!");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}