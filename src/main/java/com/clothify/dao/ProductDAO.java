package com.clothify.dao;

// ========== ADD ALL MISSING IMPORTS ==========
import com.clothify.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDateTime;

public class ProductDAO {

    // ==================== GET ALL PRODUCTS ====================
    public ObservableList<Product> getAllProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.*, c.category_name, s.supplier_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                "ORDER BY p.product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // ==================== SEARCH PRODUCTS ====================
    public ObservableList<Product> searchProducts(String searchTerm) {
        ObservableList<Product> products = FXCollections.observableArrayList();

        // FIXED: Removed the comma after "products p"
        String sql = "SELECT p.*, c.category_name, s.supplier_name " +
                "FROM products p " +                       // ← No comma here!
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                "WHERE p.product_name LIKE ? OR p.product_code LIKE ? OR c.category_name LIKE ? " +
                "ORDER BY p.product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // FIXED: Correct pattern syntax
            String pattern = "%" + searchTerm + "%";  // ← Fixed: removed ? from string
            stmt.setString(1, pattern);               // ← Fixed parameter index syntax
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                products.add(product);                 // ← This will now work
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // ==================== GET LOW STOCK PRODUCTS ====================
    public ObservableList<Product> getLowStockProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.*, c.category_name, s.supplier_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                "WHERE p.quantity <= p.reorder_level " +
                "ORDER BY p.quantity";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // ==================== GET PRODUCT BY ID ====================
    public Product getProductById(int productId) {
        String sql = "SELECT p.*, c.category_name, s.supplier_name " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                "WHERE p.product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================== ADD PRODUCT ====================
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (product_code, product_name, category_id, supplier_id, " +
                "size, color, price, cost, quantity, reorder_level, description, image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (product.getProductCode() == null || product.getProductCode().isEmpty()) {
                product.setProductCode(generateProductCode());
            }

            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getProductName());
            stmt.setInt(3, product.getCategoryId());
            stmt.setInt(4, product.getSupplierId());
            stmt.setString(5, product.getSize());
            stmt.setString(6, product.getColor());
            stmt.setDouble(7, product.getPrice());
            stmt.setDouble(8, product.getCost());
            stmt.setInt(9, product.getQuantity());
            stmt.setInt(10, product.getReorderLevel());
            stmt.setString(11, product.getDescription());
            stmt.setString(12, product.getImagePath());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== UPDATE PRODUCT ====================
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET product_name = ?, category_id = ?, supplier_id = ?, " +
                "size = ?, color = ?, price = ?, cost = ?, quantity = ?, reorder_level = ?, " +
                "description = ?, image_path = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setInt(3, product.getSupplierId());
            stmt.setString(4, product.getSize());
            stmt.setString(5, product.getColor());
            stmt.setDouble(6, product.getPrice());
            stmt.setDouble(7, product.getCost());
            stmt.setInt(8, product.getQuantity());
            stmt.setInt(9, product.getReorderLevel());
            stmt.setString(10, product.getDescription());
            stmt.setString(11, product.getImagePath());
            stmt.setInt(12, product.getProductId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== UPDATE STOCK ====================
    public boolean updateStock(int productId, int quantityChange) {
        String sql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantityChange);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== DELETE PRODUCT ====================
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== HELPER METHOD ====================
    // THIS WAS MISSING! This method extracts product data from ResultSet
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductCode(rs.getString("product_code"));
        product.setProductName(rs.getString("product_name"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setSupplierId(rs.getInt("supplier_id"));
        product.setSupplierName(rs.getString("supplier_name"));
        product.setSize(rs.getString("size"));
        product.setColor(rs.getString("color"));
        product.setPrice(rs.getDouble("price"));
        product.setCost(rs.getDouble("cost"));
        product.setQuantity(rs.getInt("quantity"));
        product.setReorderLevel(rs.getInt("reorder_level"));
        product.setDescription(rs.getString("description"));
        product.setImagePath(rs.getString("image_path"));
        return product;
    }

    // ==================== GENERATE PRODUCT CODE ====================
    private String generateProductCode() {
        return "PRD" + System.currentTimeMillis();
    }
}