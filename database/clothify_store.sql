-- ============================================================
--   CLOTHIFY STORE POS SYSTEM - DATABASE SETUP
--   Run this in MySQL Workbench or MySQL command line
-- ============================================================

DROP DATABASE IF EXISTS clothify_store;
CREATE DATABASE clothify_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clothify_store;

-- ============================================================
-- TABLE 1: USERS  (for login)
-- ============================================================
CREATE TABLE users (
    user_id    INT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(100),
    phone      VARCHAR(20),
    role       ENUM('admin','manager','staff') DEFAULT 'staff',
    is_active  BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default login accounts
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('admin',   'admin123',   'System Administrator', 'admin@clothify.com',   '077-1234567', 'admin'),
('manager', 'manager123', 'Store Manager',         'manager@clothify.com', '077-2345678', 'manager'),
('staff1',  'staff123',   'John Doe',              'john@clothify.com',    '077-3456789', 'staff'),
('staff2',  'staff123',   'Jane Smith',            'jane@clothify.com',    '077-4567890', 'staff');

-- ============================================================
-- TABLE 2: CATEGORIES
-- ============================================================
CREATE TABLE categories (
    category_id   INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    description   TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO categories (category_name, description) VALUES
('Shirts',      'Formal and casual shirts for men and women'),
('Pants',       'Trousers, jeans, and casual pants'),
('Dresses',     'Women''s dresses and gowns'),
('Accessories', 'Belts, hats, scarves, and other accessories'),
('Footwear',    'Shoes, sandals, and boots'),
('Outerwear',   'Jackets, coats, and hoodies');

-- ============================================================
-- TABLE 3: SUPPLIERS
-- ============================================================
CREATE TABLE suppliers (
    supplier_id     INT PRIMARY KEY AUTO_INCREMENT,
    supplier_code   VARCHAR(20) UNIQUE NOT NULL,
    supplier_name   VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT,
    city            VARCHAR(50),
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO suppliers (supplier_code, supplier_name, contact_person, phone, email, address, city) VALUES
('SUP001', 'Fashion Hub Ltd',     'Kamal Perera',   '011-2345678', 'kamal@fashionhub.lk',  'No. 123, Galle Road',  'Colombo'),
('SUP002', 'Textile Importers',   'Nimal Silva',    '011-3456789', 'nimal@textile.lk',      'No. 45, Kandy Road',   'Colombo'),
('SUP003', 'Global Garments',     'Sunil Fernando', '011-4567890', 'sunil@global.lk',       'No. 78, Union Place',  'Colombo');

-- ============================================================
-- TABLE 4: PRODUCTS
-- ============================================================
CREATE TABLE products (
    product_id    INT PRIMARY KEY AUTO_INCREMENT,
    product_code  VARCHAR(20) UNIQUE NOT NULL,
    product_name  VARCHAR(100) NOT NULL,
    category_id   INT,
    supplier_id   INT,
    size          VARCHAR(20),
    color         VARCHAR(30),
    price         DECIMAL(10,2) NOT NULL,
    cost          DECIMAL(10,2) NOT NULL,
    quantity      INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    description   TEXT,
    image_path    VARCHAR(255),
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)  ON DELETE SET NULL
);

INSERT INTO products (product_code, product_name, category_id, supplier_id, size, color, price, cost, quantity, reorder_level, description) VALUES
('PRD001', 'Classic White Shirt', 1, 1, 'M',        'White',  2500.00, 1500.00, 50, 10, 'Classic fit cotton shirt'),
('PRD002', 'Slim Fit Jeans',      2, 2, '32',       'Blue',   3500.00, 2200.00, 30,  8, 'Slim fit denim jeans'),
('PRD003', 'Summer Dress',        3, 1, 'S',        'Floral', 4500.00, 2800.00, 25,  5, 'Floral print summer dress'),
('PRD004', 'Leather Belt',        4, 3, 'L',        'Brown',  1200.00,  600.00,100, 20, 'Genuine leather belt'),
('PRD005', 'Running Shoes',       5, 2, '42',       'Black',  5500.00, 3500.00, 40, 10, 'Comfortable running shoes'),
('PRD006', 'Denim Jacket',        6, 1, 'L',        'Blue',   6500.00, 4200.00, 20,  5, 'Classic denim jacket'),
('PRD007', 'Silk Tie',            4, 3, 'One Size', 'Red',    1500.00,  800.00, 60, 15, 'Pure silk necktie'),
('PRD008', 'Cotton T-Shirt',      1, 2, 'XL',       'Black',  1800.00,  900.00, 80, 20, '100% cotton t-shirt');

-- ============================================================
-- TABLE 5: CUSTOMERS
-- ============================================================
CREATE TABLE customers (
    customer_id   INT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) UNIQUE NOT NULL,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    phone         VARCHAR(20) NOT NULL,
    email         VARCHAR(100),
    address       TEXT,
    city          VARCHAR(50),
    loyalty_points INT DEFAULT 0,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO customers (customer_code, first_name, last_name, phone, email, address, city) VALUES
('CUST001', 'Amal',   'Perera',      '071-1234567', 'amal@gmail.com',   'No. 12, Main Street', 'Colombo'),
('CUST002', 'Kamala', 'Silva',       '072-2345678', 'kamala@gmail.com', 'No. 34, Park Road',   'Kandy'),
('CUST003', 'Nuwan',  'Fernando',    '073-3456789', 'nuwan@gmail.com',  'No. 56, Beach Road',  'Galle'),
('CUST004', 'Dilini', 'Weerasinghe', '074-4567890', 'dilini@gmail.com', 'No. 78, Temple Road', 'Colombo'),
('CUST005', 'Saman',  'Kumara',      '075-5678901', 'saman@gmail.com',  'No. 90, Lake Road',   'Negombo');

-- ============================================================
-- TABLE 6: ORDERS
-- ============================================================
CREATE TABLE orders (
    order_id       INT PRIMARY KEY AUTO_INCREMENT,
    order_number   VARCHAR(20) UNIQUE NOT NULL,
    customer_id    INT NOT NULL,
    user_id        INT NOT NULL,
    order_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal       DECIMAL(10,2) NOT NULL,
    tax            DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount       DECIMAL(10,2) DEFAULT 0,
    total          DECIMAL(10,2) NOT NULL,
    payment_method ENUM('Cash','Card','Mobile') DEFAULT 'Cash',
    payment_status ENUM('paid','pending','cancelled','refunded') DEFAULT 'pending',
    notes          TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id)     REFERENCES users(user_id)
);

INSERT INTO orders (order_number, customer_id, user_id, subtotal, tax, discount, total, payment_method, payment_status) VALUES
('ORD20260001', 1, 1, 7500.00, 750.00, 0.00, 8250.00, 'Cash', 'paid'),
('ORD20260002', 2, 1, 5500.00, 550.00, 500.00, 5550.00, 'Card', 'paid'),
('ORD20260003', 3, 2, 3200.00, 320.00, 0.00, 3520.00, 'Mobile', 'paid');

-- ============================================================
-- TABLE 7: ORDER ITEMS
-- ============================================================
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id      INT NOT NULL,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL,
    price         DECIMAL(10,2) NOT NULL,
    subtotal      DECIMAL(10,2) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id)   REFERENCES orders(order_id)   ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

INSERT INTO order_items (order_id, product_id, quantity, price, subtotal) VALUES
(1, 1, 2, 2500.00, 5000.00),
(1, 4, 1, 1200.00, 1200.00),
(1, 7, 1, 1500.00, 1500.00),
(2, 2, 1, 3500.00, 3500.00),
(2, 8, 2, 1800.00, 3600.00),
(3, 5, 1, 5500.00, 5500.00);

-- ============================================================
-- VERIFICATION: Check all tables
-- ============================================================
SELECT 'users'       AS table_name, COUNT(*) AS records FROM users
UNION ALL
SELECT 'categories',  COUNT(*) FROM categories
UNION ALL
SELECT 'suppliers',   COUNT(*) FROM suppliers
UNION ALL
SELECT 'products',    COUNT(*) FROM products
UNION ALL
SELECT 'customers',   COUNT(*) FROM customers
UNION ALL
SELECT 'orders',      COUNT(*) FROM orders
UNION ALL
SELECT 'order_items', COUNT(*) FROM order_items;

SELECT '✅ Database setup complete! Login with: admin / admin123' AS status;
