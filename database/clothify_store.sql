DROP DATABASE IF EXISTS clothify_store;

CREATE DATABASE clothify_store
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE clothify_store;

-- =====================================================
-- CLOTHIFY STORE POS SYSTEM - SAFE INSTALLATION
-- (Will not error if tables already exist)
-- =====================================================

USE clothify_store;

-- =====================================================
-- 1. USERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
                                     user_id INT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role ENUM('admin', 'manager', 'staff') DEFAULT 'staff',
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_role (role)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert only if table was just created or use INSERT IGNORE
INSERT IGNORE INTO users (username, password, full_name, email, phone, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@clothify.com', '077-1234567', 'admin'),
('manager', 'manager123', 'Store Manager', 'manager@clothify.com', '077-2345678', 'manager'),
('staff1', 'staff123', 'John Doe', 'john@clothify.com', '077-3456789', 'staff'),
('staff2', 'staff123', 'Jane Smith', 'jane@clothify.com', '077-4567890', 'staff');

USE clothify_store;

CREATE TABLE IF NOT EXISTS categories (
                                          category_id INT PRIMARY KEY AUTO_INCREMENT,
                                          category_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_category_name (category_name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample categories
INSERT IGNORE INTO categories (category_name, description) VALUES
('Shirts', 'Formal and casual shirts for men and women'),
('Pants', 'Trousers, jeans, and casual pants'),
('Dresses', 'Women\'s dresses and gowns'),
('Accessories', 'Belts, hats, scarves, and other accessories'),
('Footwear', 'Shoes, sandals, and boots'),
('Outerwear', 'Jackets, coats, and hoodies');

-- Verify
SELECT * FROM categories;

USE clothify_store;

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_code VARCHAR(20) UNIQUE NOT NULL,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    country VARCHAR(50) DEFAULT 'Sri Lanka',
    payment_terms VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_supplier_name (supplier_name),
    INDEX idx_supplier_code (supplier_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample suppliers
INSERT IGNORE INTO suppliers (supplier_code, supplier_name, contact_person, phone, email, address, city) VALUES
('SUP001', 'Fashion Hub Ltd', 'Kamal Perera', '011-2345678', 'kamal@fashionhub.lk', 'No. 123, Galle Road', 'Colombo'),
('SUP002', 'Textile Importers', 'Nimal Silva', '011-3456789', 'nimal@textile.lk', 'No. 45, Kandy Road', 'Colombo'),
('SUP003', 'Global Garments', 'Sunil Fernando', '011-4567890', 'sunil@global.lk', 'No. 78, Union Place', 'Colombo');

-- Verify
SELECT * FROM suppliers;


-- products table

USE clothify_store;

CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(20) UNIQUE NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    category_id INT,
    supplier_id INT,
    size VARCHAR(20),
    color VARCHAR(30),
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    description TEXT,
    image_path VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL,

    INDEX idx_product_code (product_code),
    INDEX idx_product_name (product_name),
    INDEX idx_category (category_id),
    INDEX idx_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample products
INSERT IGNORE INTO products (product_code, product_name, category_id, supplier_id, size, color, price, cost, quantity, reorder_level, description) VALUES
('PRD001', 'Classic White Shirt', 1, 1, 'M', 'White', 2500.00, 1500.00, 50, 10, 'Classic fit cotton shirt'),
('PRD002', 'Slim Fit Jeans', 2, 2, '32', 'Blue', 3500.00, 2200.00, 30, 8, 'Slim fit denim jeans'),
('PRD003', 'Summer Dress', 3, 1, 'S', 'Floral', 4500.00, 2800.00, 25, 5, 'Floral print summer dress'),
('PRD004', 'Leather Belt', 4, 3, 'L', 'Brown', 1200.00, 600.00, 100, 20, 'Genuine leather belt'),
('PRD005', 'Running Shoes', 5, 2, '42', 'Black', 5500.00, 3500.00, 40, 10, 'Comfortable running shoes'),
('PRD006', 'Denim Jacket', 6, 1, 'L', 'Blue', 6500.00, 4200.00, 20, 5, 'Classic denim jacket'),
('PRD007', 'Silk Tie', 4, 3, 'One Size', 'Red', 1500.00, 800.00, 60, 15, 'Pure silk necktie'),
('PRD008', 'Cotton T-Shirt', 1, 2, 'XL', 'Black', 1800.00, 900.00, 80, 20, '100% cotton t-shirt');

-- Verify
SELECT p.product_id, p.product_code, p.product_name, c.category_name, s.supplier_name, p.price, p.quantity
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id;

-- customers table
USE clothify_store;

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    loyalty_points INT DEFAULT 0,
    customer_type ENUM('regular', 'vip', 'wholesale') DEFAULT 'regular',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_customer_code (customer_code),
    INDEX idx_phone (phone),
    INDEX idx_name (first_name, last_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample customers
INSERT IGNORE INTO customers (customer_code, first_name, last_name, phone, email, address, city) VALUES
('CUST001', 'Amal', 'Perera', '071-1234567', 'amal@gmail.com', 'No. 12, Main Street', 'Colombo'),
('CUST002', 'Kamala', 'Silva', '072-2345678', 'kamala@gmail.com', 'No. 34, Park Road', 'Kandy'),
('CUST003', 'Nuwan', 'Fernando', '073-3456789', 'nuwan@gmail.com', 'No. 56, Beach Road', 'Galle'),
('CUST004', 'Dilini', 'Weerasinghe', '074-4567890', 'dilini@gmail.com', 'No. 78, Temple Road', 'Colombo'),
('CUST005', 'Saman', 'Kumara', '075-5678901', 'saman@gmail.com', 'No. 90, Lake Road', 'Negombo');

-- Verify
SELECT * FROM customers;

-- orders table
-- =====================================================
USE clothify_store;

CREATE TABLE IF NOT EXISTS orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id INT NOT NULL,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    tax DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    payment_method ENUM('cash', 'card', 'mobile') DEFAULT 'cash',
    payment_status ENUM('paid', 'pending', 'cancelled', 'refunded') DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),

    INDEX idx_order_number (order_number),
    INDEX idx_customer (customer_id),
    INDEX idx_user (user_id),
    INDEX idx_order_date (order_date),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample orders
INSERT INTO orders (order_number, customer_id, user_id, subtotal, tax, discount, total, payment_method, payment_status) VALUES
('ORD20240001', 1, 3, 7500.00, 750.00, 0, 8250.00, 'cash', 'paid'),
('ORD20240002', 2, 3, 5500.00, 550.00, 500.00, 5550.00, 'card', 'paid'),
('ORD20240003', 3, 4, 3200.00, 320.00, 0, 3520.00, 'mobile', 'paid'),
('ORD20240004', 1, 3, 4500.00, 450.00, 200.00, 4750.00, 'cash', 'paid'),
('ORD20240005', 4, 4, 8900.00, 890.00, 0, 9790.00, 'card', 'pending');

-- Verify
SELECT o.order_id, o.order_number, c.first_name, c.last_name, u.full_name as cashier, o.total, o.payment_status
FROM orders o
JOIN customers c ON o.customer_id = c.customer_id
JOIN users u ON o.user_id = u.user_id;

-- order items table
USE clothify_store;

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),

    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample order items
INSERT IGNORE INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES
-- Order 1 items
(1, 1, 2, 2500.00, 5000.00),
(1, 4, 1, 1200.00, 1200.00),
(1, 7, 1, 1500.00, 1500.00),
-- Order 2 items
(2, 2, 1, 3500.00, 3500.00),
(2, 8, 2, 1800.00, 3600.00),
-- Order 3 items
(3, 5, 1, 5500.00, 5500.00),
-- Order 4 items
(4, 3, 1, 4500.00, 4500.00),
-- Order 5 items
(5, 6, 1, 6500.00, 6500.00),
(5, 4, 2, 1200.00, 2400.00);

-- Verify
SELECT oi.order_item_id, o.order_number, p.product_name, oi.quantity, oi.price, oi.subtotal
FROM order_items oi
JOIN orders o ON oi.order_id = o.order_id
JOIN products p ON oi.product_id = p.product_id
ORDER BY o.order_id;

-- inventory transaction table

USE clothify_store;

CREATE TABLE IF NOT EXISTS inventory_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    transaction_type ENUM('purchase', 'sale', 'adjustment', 'return') NOT NULL,
    quantity_before INT NOT NULL,
    quantity_change INT NOT NULL,
    quantity_after INT NOT NULL,
    reference_number VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),

    INDEX idx_product (product_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample inventory transactions
INSERT IGNORE INTO inventory_transactions
(product_id, user_id, transaction_type, quantity_before, quantity_change, quantity_after, reference_number, notes) VALUES
(1, 1, 'purchase', 0, 50, 50, 'PO-001', 'Initial stock'),
(2, 1, 'purchase', 0, 30, 30, 'PO-002', 'Initial stock'),
(3, 1, 'purchase', 0, 25, 25, 'PO-003', 'Initial stock'),
(1, 3, 'sale', 50, -2, 48, 'ORD20240001', 'Sale to customer'),
(4, 3, 'sale', 100, -1, 99, 'ORD20240001', 'Sale to customer');

-- Verify
SELECT * FROM inventory_transactions;

-- views for reporting

USE clothify_store;

-- Drop views if they exist (to avoid errors)
DROP VIEW IF EXISTS sales_summary;
DROP VIEW IF EXISTS product_sales;
DROP VIEW IF EXISTS inventory_status;
DROP VIEW IF EXISTS customer_summary;

-- Create sales summary view
CREATE VIEW sales_summary AS
SELECT
    DATE(order_date) as sale_date,
    COUNT(DISTINCT order_id) as order_count,
    COUNT(DISTINCT customer_id) as unique_customers,
    SUM(total) as total_sales,
    SUM(tax) as total_tax,
    SUM(discount) as total_discount,
    AVG(total) as avg_order_value,
    SUM(subtotal) as total_subtotal
FROM orders
WHERE payment_status = 'paid'
GROUP BY DATE(order_date)
ORDER BY sale_date DESC;

-- Create product sales view
CREATE VIEW product_sales AS
SELECT
    p.product_id,
    p.product_code,
    p.product_name,
    c.category_name,
    COALESCE(SUM(oi.quantity), 0) as total_quantity_sold,
    COALESCE(SUM(oi.subtotal), 0) as total_revenue,
    COALESCE(AVG(oi.price), 0) as avg_selling_price,
    COUNT(DISTINCT oi.order_id) as times_ordered
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
LEFT JOIN order_items oi ON p.product_id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.order_id AND o.payment_status = 'paid'
GROUP BY p.product_id;

-- Create inventory status view
CREATE VIEW inventory_status AS
SELECT
    p.product_id,
    p.product_code,
    p.product_name,
    c.category_name,
    p.quantity as current_stock,
    p.reorder_level,
    CASE
        WHEN p.quantity <= 0 THEN 'OUT OF STOCK'
        WHEN p.quantity <= p.reorder_level THEN 'LOW STOCK'
        ELSE 'IN STOCK'
    END as stock_status,
    p.price * p.quantity as inventory_value,
    p.price as selling_price,
    p.cost as cost_price,
    (p.price - p.cost) as profit_per_unit
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id;

-- Create customer summary view
CREATE VIEW customer_summary AS
SELECT
    c.customer_id,
    c.customer_code,
    CONCAT(c.first_name, ' ', c.last_name) as customer_name,
    c.phone,
    c.email,
    c.loyalty_points,
    COUNT(DISTINCT o.order_id) as total_orders,
    COALESCE(SUM(o.total), 0) as total_spent,
    MAX(o.order_date) as last_order_date
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id AND o.payment_status = 'paid'
GROUP BY c.customer_id;

-- Verify views
SELECT 'SALES SUMMARY' as view_name, COUNT(*) as rows FROM sales_summary
UNION ALL
SELECT 'PRODUCT SALES', COUNT(*) FROM product_sales
UNION ALL
SELECT 'INVENTORY STATUS', COUNT(*) FROM inventory_status
UNION ALL
SELECT 'CUSTOMER SUMMARY', COUNT(*) FROM customer_summary;

-- triggers
-- =====================================================
-- CODE SET 9: CREATE ALL TRIGGERS
-- =====================================================
USE clothify_store;

-- Drop triggers if they exist (clean start)
DROP TRIGGER IF EXISTS after_order_item_insert;
DROP TRIGGER IF EXISTS after_order_item_delete;
DROP TRIGGER IF EXISTS after_product_update;
DROP TRIGGER IF EXISTS before_order_insert;

DELIMITER //

-- =====================================================
-- TRIGGER 1: Update stock when order is placed
-- =====================================================
CREATE TRIGGER after_order_item_insert
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    DECLARE current_qty INT;
    DECLARE order_user_id INT;
    DECLARE order_ref VARCHAR(50);

    -- Get current quantity
    SELECT quantity INTO current_qty FROM products WHERE product_id = NEW.product_id;

    -- Get user who processed the order
    SELECT user_id, order_number INTO order_user_id, order_ref
    FROM orders WHERE order_id = NEW.order_id;

    -- Update product quantity (reduce stock)
    UPDATE products
    SET quantity = quantity - NEW.quantity
    WHERE product_id = NEW.product_id;

    -- Log transaction in inventory_transactions
    INSERT INTO inventory_transactions
    (product_id, user_id, transaction_type, quantity_before, quantity_change, quantity_after, reference_number)
    VALUES
    (NEW.product_id, order_user_id, 'sale', current_qty, -NEW.quantity, current_qty - NEW.quantity, order_ref);
END//

-- =====================================================
-- TRIGGER 2: Handle order cancellations (restore stock)
-- =====================================================
CREATE TRIGGER after_order_item_delete
AFTER DELETE ON order_items
FOR EACH ROW
BEGIN
    DECLARE current_qty INT;
    DECLARE order_user_id INT;
    DECLARE order_ref VARCHAR(50);

    -- Get current quantity
    SELECT quantity INTO current_qty FROM products WHERE product_id = OLD.product_id;

    -- Get user who processed the order
    SELECT user_id, order_number INTO order_user_id, order_ref
    FROM orders WHERE order_id = OLD.order_id;

    -- Restore stock (add back the quantity)
    UPDATE products
    SET quantity = quantity + OLD.quantity
    WHERE product_id = OLD.product_id;

    -- Log return transaction
    INSERT INTO inventory_transactions
    (product_id, user_id, transaction_type, quantity_before, quantity_change, quantity_after, reference_number, notes)
    VALUES
    (OLD.product_id, order_user_id, 'return', current_qty, OLD.quantity, current_qty + OLD.quantity, order_ref, 'Order cancelled/returned');
END//

-- =====================================================
-- TRIGGER 3: Log manual inventory adjustments
-- =====================================================
CREATE TRIGGER after_product_update
AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    IF OLD.quantity != NEW.quantity THEN
        INSERT INTO inventory_transactions
        (product_id, user_id, transaction_type, quantity_before, quantity_change, quantity_after, notes)
        VALUES
        (NEW.product_id, 1, 'adjustment', OLD.quantity, NEW.quantity - OLD.quantity, NEW.quantity, 'Manual stock adjustment');
    END IF;
END//

-- =====================================================
-- TRIGGER 4: Auto-generate order number before insert
-- =====================================================
CREATE TRIGGER before_order_insert
BEFORE INSERT ON orders
FOR EACH ROW
BEGIN
    DECLARE next_id INT;
    DECLARE year_prefix VARCHAR(4);

    -- Get current year
    SET year_prefix = DATE_FORMAT(NOW(), '%Y');

    -- Get next order number
    SELECT IFNULL(MAX(CAST(SUBSTRING(order_number, 8) AS UNSIGNED)), 0) + 1 INTO next_id
    FROM orders WHERE order_number LIKE CONCAT('ORD', year_prefix, '%');

    -- Set order number (format: ORD20240001)
    SET NEW.order_number = CONCAT('ORD', year_prefix, LPAD(next_id, 4, '0'));
END//

DELIMITER ;

-- =====================================================
-- VERIFY TRIGGERS
-- =====================================================
SHOW TRIGGERS FROM clothify_store;
SELECT '✅ Triggers created successfully' as status;

-- create stored procedures
-- =====================================================
-- STEP 10: CREATE STORED PROCEDURES
-- =====================================================
USE clothify_store;

-- Drop procedures if they exist
DROP PROCEDURE IF EXISTS GetDailySales;
DROP PROCEDURE IF EXISTS GetLowStockAlert;
DROP PROCEDURE IF EXISTS GetTopProducts;

DELIMITER //

-- Procedure 1: Get daily sales report for a date range
CREATE PROCEDURE GetDailySales(IN start_date DATE, IN end_date DATE)
BEGIN
    SELECT
        DATE(o.order_date) as sale_date,
        COUNT(DISTINCT o.order_id) as order_count,
        COUNT(DISTINCT o.customer_id) as customer_count,
        SUM(o.subtotal) as subtotal,
        SUM(o.tax) as tax,
        SUM(o.discount) as discount,
        SUM(o.total) as total_sales,
        AVG(o.total) as average_order
    FROM orders o
    WHERE DATE(o.order_date) BETWEEN start_date AND end_date
        AND o.payment_status = 'paid'
    GROUP BY DATE(o.order_date)
    ORDER BY sale_date DESC;
END//

-- Procedure 2: Get low stock alert
CREATE PROCEDURE GetLowStockAlert(IN threshold INT)
BEGIN
    SELECT
        p.product_id,
        p.product_code,
        p.product_name,
        c.category_name,
        p.quantity,
        p.reorder_level,
        CASE
            WHEN p.quantity <= 0 THEN 'CRITICAL - OUT OF STOCK'
            WHEN p.quantity <= threshold THEN 'LOW STOCK - ORDER NOW'
            ELSE 'OK'
        END as alert_level
    FROM products p
    LEFT JOIN categories c ON p.category_id = c.category_id
    WHERE p.quantity <= threshold
    ORDER BY p.quantity;
END//

-- Procedure 3: Get top selling products
CREATE PROCEDURE GetTopProducts(IN limit_count INT)
BEGIN
    SELECT
        p.product_id,
        p.product_code,
        p.product_name,
        c.category_name,
        COALESCE(SUM(oi.quantity), 0) as total_sold,
        COALESCE(SUM(oi.subtotal), 0) as total_revenue
    FROM products p
    LEFT JOIN categories c ON p.category_id = c.category_id
    LEFT JOIN order_items oi ON p.product_id = oi.product_id
    LEFT JOIN orders o ON oi.order_id = o.order_id AND o.payment_status = 'paid'
    GROUP BY p.product_id
    ORDER BY total_sold DESC
    LIMIT limit_count;
END//

DELIMITER ;

-- Test procedures
CALL GetDailySales('2024-01-01', '2024-12-31');
CALL GetLowStockAlert(10);
CALL GetTopProducts(5);

-- create indexes performance
-- =====================================================
-- STEP 11: CREATE ADDITIONAL INDEXES
-- =====================================================
USE clothify_store;

-- Create indexes for better query performance
CREATE INDEX  idx_orders_date_status ON orders(order_date, payment_status);
CREATE INDEX  idx_order_items_order ON order_items(order_id);
CREATE INDEX  idx_order_items_product ON order_items(product_id);
CREATE INDEX  idx_inventory_date ON inventory_transactions(created_at);
CREATE INDEX  idx_products_category ON products(category_id, quantity);
CREATE INDEX  idx_customers_phone ON customers(phone);

-- Show all indexes
SELECT
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE,
    SEQ_IN_INDEX
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = 'clothify_store'
ORDER BY TABLE_NAME, INDEX_NAME;

-- verifications
USE clothify_store;

-- Create application user (change password as needed)
CREATE USER IF NOT EXISTS 'clothify_app'@'localhost' IDENTIFIED BY 'Clothify@2024';

-- Grant necessary privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON clothify_store.* TO 'clothify_app'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;

-- Verify user
SELECT user, host FROM mysql.user WHERE user = 'clothify_app';
SHOW GRANTS FOR 'clothify_app'@'localhost';

-- =====================================================
-- STEP 13: VERIFY COMPLETE DATABASE SETUP
-- =====================================================
USE clothify_store;

-- Check all tables
SELECT 'DATABASE VERIFICATION' as '';
SELECT CONCAT('Database: ', DATABASE()) as info;

-- Table counts
SELECT 'TABLE COUNTS' as '';
SELECT 'users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'categories', COUNT(*) FROM categories
UNION ALL
SELECT 'suppliers', COUNT(*) FROM suppliers
UNION ALL
SELECT 'products', COUNT(*) FROM products
UNION ALL
SELECT 'customers', COUNT(*) FROM customers
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'order_items', COUNT(*) FROM order_items
UNION ALL
SELECT 'inventory_transactions', COUNT(*) FROM inventory_transactions;

-- Sample data verification
SELECT 'SAMPLE DATA' as '';
SELECT 'Users:' as sample, username, full_name, role FROM users LIMIT 3;
SELECT 'Products:' as sample, product_name, price, quantity FROM products LIMIT 3;
SELECT 'Customers:' as sample, first_name, last_name, phone FROM customers LIMIT 3;
SELECT 'Orders:' as sample, order_number, total, payment_status FROM orders LIMIT 3;

-- Check views
SELECT 'VIEWS:' as '';
SELECT * FROM sales_summary LIMIT 5;
SELECT * FROM inventory_status WHERE stock_status != 'IN STOCK';

-- Final message
SELECT '✅ DATABASE SETUP COMPLETED SUCCESSFULLY!' as status;

