package com.clothify.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.clothify.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private ProductDAO productDAO = new ProductDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private UserDAO userDAO = new UserDAO();

    // Create new order
    public boolean createOrder(Order order) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders (order_number, customer_id, user_id, order_date, subtotal, tax, discount, total, payment_method, payment_status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);

            orderStmt.setString(1, order.getOrderNumber());
            orderStmt.setInt(2, order.getCustomer().getCustomerId());
            orderStmt.setInt(3, order.getUser().getUserId());
            orderStmt.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));
            orderStmt.setDouble(5, order.getSubtotal());
            orderStmt.setDouble(6, order.getTax());
            orderStmt.setDouble(7, order.getDiscount());
            orderStmt.setDouble(8, order.getTotal());
            orderStmt.setString(9, order.getPaymentMethod());
            orderStmt.setString(10, order.getPaymentStatus());
            orderStmt.setString(11, order.getNotes());

            int affectedRows = orderStmt.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setOrderId(generatedKeys.getInt(1));
            } else {
                conn.rollback();
                return false;
            }

            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);

            for (OrderItem item : order.getItems()) {
                itemStmt.setInt(1, order.getOrderId());
                itemStmt.setInt(2, item.getProduct().getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getPrice());
                itemStmt.setDouble(5, item.getSubtotal());
                itemStmt.addBatch();

                productDAO.updateStock(item.getProduct().getProductId(), -item.getQuantity());
            }

            itemStmt.executeBatch();
            conn.commit();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (generatedKeys != null)
                    generatedKeys.close();
                if (orderStmt != null)
                    orderStmt.close();
                if (itemStmt != null)
                    itemStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Get all orders
    public ObservableList<Order> getAllOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                if (order != null) {
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Get order by ID
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get order items
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                item.setSubtotal(rs.getDouble("subtotal"));

                int productId = rs.getInt("product_id");
                Product product = productDAO.getProductById(productId);
                item.setProduct(product);

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // Update order status
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET payment_status = ? WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get today's sales
    public double getTodaySales() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM orders WHERE DATE(order_date) = CURDATE() AND payment_status = 'paid'";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get today's order count
    public int getTodayOrderCount() {
        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Extract order from ResultSet
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderNumber(rs.getString("order_number"));

        int customerId = rs.getInt("customer_id");
        if (customerId > 0) {
            Customer customer = customerDAO.getCustomerById(customerId);
            order.setCustomer(customer);
        }

        int userId = rs.getInt("user_id");
        if (userId > 0) {
            User user = userDAO.getUserById(userId);
            order.setUser(user);
        }

        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setSubtotal(rs.getDouble("subtotal"));
        order.setTax(rs.getDouble("tax"));
        order.setDiscount(rs.getDouble("discount"));
        order.setTotal(rs.getDouble("total"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setNotes(rs.getString("notes"));

        List<OrderItem> items = getOrderItems(order.getOrderId());
        order.setItems(items);

        return order;
    }
}