package com.clothify.controller;

import com.clothify.dao.ProductDAO;
import com.clothify.dao.CategoryDAO;
import com.clothify.dao.SupplierDAO;
import com.clothify.model.Product;
import com.clothify.model.Category;
import com.clothify.model.Supplier;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TextField productCodeField;
    @FXML private TextField productNameField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private ComboBox<Supplier> supplierCombo;
    @FXML private TextField sizeField;
    @FXML private TextField colorField;
    @FXML private TextField priceField;
    @FXML private TextField costField;
    @FXML private TextField quantityField;
    @FXML private TextField reorderLevelField;
    @FXML private TextArea descriptionArea;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> colCode;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colStatus;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Label lowStockCountLabel;

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadComboBoxes();
        loadProducts();
        setupListeners();
        updateLowStockCount();
    }

    private void setupTableColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Status column with color coding
        colStatus.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            String status = p.isLowStock() ? "LOW STOCK" : "In Stock";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        colStatus.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("LOW STOCK")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green;");
                    }
                }
            }
        });

        // Price formatting
        colPrice.setCellFactory(column -> new TableCell<Product, Double>() {
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
    }

    private void loadComboBoxes() {
        // Load categories
        ObservableList<Category> categories = categoryDAO.getAllCategories();
        categoryCombo.setItems(categories);

        // Load suppliers
        ObservableList<Supplier> suppliers = supplierDAO.getAllSuppliers();
        supplierCombo.setItems(suppliers);
    }

    private void loadProducts() {
        productList = productDAO.getAllProducts();
        productTable.setItems(productList);
    }

    private void setupListeners() {
        // Table selection listener
        productTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        displayProductDetails(newSelection);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    }
                });

        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                productTable.setItems(productList);
            } else {
                productTable.setItems(productDAO.searchProducts(newVal));
            }
        });

        // Disable buttons initially
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void displayProductDetails(Product product) {
        productCodeField.setText(product.getProductCode());
        productNameField.setText(product.getProductName());

        // Select category
        for (Category c : categoryCombo.getItems()) {
            if (c.getCategoryId() == product.getCategoryId()) {
                categoryCombo.setValue(c);
                break;
            }
        }

        // Select supplier
        for (Supplier s : supplierCombo.getItems()) {
            if (s.getSupplierId() == product.getSupplierId()) {
                supplierCombo.setValue(s);
                break;
            }
        }

        sizeField.setText(product.getSize());
        colorField.setText(product.getColor());
        priceField.setText(String.format("%.2f", product.getPrice()));
        costField.setText(String.format("%.2f", product.getCost()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        reorderLevelField.setText(String.valueOf(product.getReorderLevel()));
        descriptionArea.setText(product.getDescription());
    }

    @FXML
    private void handleAdd() {
        if (!validateInputs()) return;

        Product product = new Product();
        product.setProductCode(productCodeField.getText().trim());
        product.setProductName(productNameField.getText().trim());
        product.setCategoryId(categoryCombo.getValue().getCategoryId());
        product.setSupplierId(supplierCombo.getValue().getSupplierId());
        product.setSize(sizeField.getText().trim());
        product.setColor(colorField.getText().trim());
        product.setPrice(Double.parseDouble(priceField.getText().trim()));
        product.setCost(Double.parseDouble(costField.getText().trim()));
        product.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        product.setReorderLevel(Integer.parseInt(reorderLevelField.getText().trim()));
        product.setDescription(descriptionArea.getText().trim());

        boolean success = productDAO.addProduct(product);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
            clearForm();
            loadProducts();
            updateLowStockCount();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product!");
        }
    }

    @FXML
    private void handleUpdate() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product to update!");
            return;
        }

        if (!validateInputs()) return;

        selected.setProductName(productNameField.getText().trim());
        selected.setCategoryId(categoryCombo.getValue().getCategoryId());
        selected.setSupplierId(supplierCombo.getValue().getSupplierId());
        selected.setSize(sizeField.getText().trim());
        selected.setColor(colorField.getText().trim());
        selected.setPrice(Double.parseDouble(priceField.getText().trim()));
        selected.setCost(Double.parseDouble(costField.getText().trim()));
        selected.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        selected.setReorderLevel(Integer.parseInt(reorderLevelField.getText().trim()));
        selected.setDescription(descriptionArea.getText().trim());

        boolean success = productDAO.updateProduct(selected);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
            clearForm();
            loadProducts();
            updateLowStockCount();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update product!");
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product to delete!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Product");
        confirm.setContentText("Are you sure you want to delete " + selected.getProductName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = productDAO.deleteProduct(selected.getProductId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully!");
                clearForm();
                loadProducts();
                updateLowStockCount();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Cannot delete product! It may have existing orders.");
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        productCodeField.clear();
        productNameField.clear();
        categoryCombo.setValue(null);
        supplierCombo.setValue(null);
        sizeField.clear();
        colorField.clear();
        priceField.clear();
        costField.clear();
        quantityField.clear();
        reorderLevelField.clear();
        descriptionArea.clear();

        productTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (productNameField.getText().trim().isEmpty()) {
            errors.append("• Product name is required\n");
        }
        if (categoryCombo.getValue() == null) {
            errors.append("• Category is required\n");
        }
        if (supplierCombo.getValue() == null) {
            errors.append("• Supplier is required\n");
        }
        if (priceField.getText().trim().isEmpty()) {
            errors.append("• Price is required\n");
        } else {
            try {
                Double.parseDouble(priceField.getText().trim());
            } catch (NumberFormatException e) {
                errors.append("• Price must be a valid number\n");
            }
        }
        if (quantityField.getText().trim().isEmpty()) {
            errors.append("• Quantity is required\n");
        } else {
            try {
                Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException e) {
                errors.append("• Quantity must be a valid number\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please fix the following errors:\n" + errors.toString());
            return false;
        }

        return true;
    }

    private void updateLowStockCount() {
        int lowStockCount = productDAO.getLowStockProducts().size();
        lowStockCountLabel.setText("Low Stock Items: " + lowStockCount);

        if (lowStockCount > 0) {
            lowStockCountLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lowStockCountLabel.setStyle("-fx-text-fill: green;");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        loadProducts();
        loadComboBoxes();
        updateLowStockCount();
        clearForm();
    }

    @FXML
    private void handleExport() {
        // Implementation for export functionality
        showAlert(Alert.AlertType.INFORMATION, "Info", "Export feature coming soon!");
    }
}