package com.clothify.model;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private Product product;
    private int quantity;
    private double price;
    private double subtotal;

    // Constructors
    public OrderItem() {}

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice();
        calculateSubtotal();
    }

    // Getters and Setters
    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    // Business Method
    private void calculateSubtotal() {
        this.subtotal = this.price * this.quantity;
    }

    public String getProductName() {
        return product != null ? product.getProductName() : "";
    }

    @Override
    public String toString() {
        return getProductName() + " x" + quantity + " = $" + subtotal;
    }
}