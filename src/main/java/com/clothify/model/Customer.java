package com.clothify.model;

import java.time.LocalDateTime;

public class Customer {
    private int customerId;
    private String customerCode;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private int loyaltyPoints;
    private LocalDateTime createdAt;

    // Constructors
    public Customer() {}

    public Customer(int customerId, String customerCode, String firstName, String lastName,
                    String phone, String email, String address) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.loyaltyPoints = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return getFullName() + " (" + customerCode + ")";
    }
}