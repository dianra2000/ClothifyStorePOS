package com.clothify.model;

import java.time.LocalDateTime;

public class Product {
    private int productId;
    private String productCode;
    private String productName;
    private int categoryId;
    private String categoryName;
    private int supplierId;
    private String supplierName;
    private String size;
    private String color;
    private double price;
    private double cost;
    private int quantity;
    private int reorderLevel;
    private String description;
    private String imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Product() {}

    public Product(int productId, String productCode, String productName, int categoryId,
                   int supplierId, double price, double cost, int quantity) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
        this.reorderLevel = 10;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business Logic Methods
    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    public double getProfit() {
        return price - cost;
    }

    public double getProfitMargin() {
        return (price - cost) / price * 100;
    }

    @Override
    public String toString() {
        return productName + " (" + productCode + ")";
    }
}