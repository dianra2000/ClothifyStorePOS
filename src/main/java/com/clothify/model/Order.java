package com.clothify.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private String orderNumber;
    private Customer customer;
    private User user;
    private LocalDateTime orderDate;
    private double subtotal;
    private double tax;
    private double discount;
    private double total;
    private String paymentMethod;
    private String paymentStatus;
    private String notes;
    private List<OrderItem> items;

    // Constructors
    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
    }

    public Order(int orderId, String orderNumber, Customer customer, User user) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.user = user;
        this.orderDate = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    // Business Methods
    public void addItem(OrderItem item) {
        this.items.add(item);
        calculateTotals();
    }

    public void removeItem(OrderItem item) {
        this.items.remove(item);
        calculateTotals();
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
        this.tax = subtotal * 0.10; // 10% tax
        this.total = subtotal + tax - discount;
    }

    public int getItemCount() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    @Override
    public String toString() {
        return "Order #" + orderNumber + " - " + customer.getFullName() + " - $" + total;
    }
}