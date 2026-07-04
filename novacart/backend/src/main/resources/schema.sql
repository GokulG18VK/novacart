-- ===========================================================
-- NovaCart Database Schema
-- Run this manually OR let Spring Boot auto-run it on startup
-- (spring.sql.init.mode=always in application.properties)
-- ===========================================================

CREATE DATABASE IF NOT EXISTS novacart_db;
USE novacart_db;

-- ---------------- USERS ----------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'CUSTOMER',     -- CUSTOMER or ADMIN
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------- CATEGORIES ----------------
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    image_url VARCHAR(500)
);

-- ---------------- PRODUCTS ----------------
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category_id INT,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    old_price DECIMAL(10,2),
    image_url VARCHAR(500),
    rating DECIMAL(2,1) DEFAULT 4.5,
    reviews_count INT DEFAULT 0,
    badge VARCHAR(30),
    stock INT DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- ---------------- CART ----------------
CREATE TABLE IF NOT EXISTS cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_item (user_id, product_id)
);

-- ---------------- WISHLIST ----------------
CREATE TABLE IF NOT EXISTS wishlist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_wishlist_item (user_id, product_id)
);

-- ---------------- ORDERS ----------------
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(30) NOT NULL UNIQUE,
    user_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30),
    status VARCHAR(30) DEFAULT 'Pending',     -- Pending, Shipped, Delivered, Cancelled
    full_name VARCHAR(120),
    phone VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ---------------- ORDER ITEMS ----------------
CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT,
    product_name VARCHAR(200),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
);

-- ---------------- SEED DATA: CATEGORIES ----------------
INSERT IGNORE INTO categories (name, image_url) VALUES
('Fashion', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=500'),
('Electronics', 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500'),
('Home & Living', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500'),
('Beauty & Personal Care', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500'),
('Sports & Fitness', 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=500'),
('Groceries', 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=500'),
('Watches & Accessories', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500'),
('Gaming', 'https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?w=500');

-- ---------------- SEED DATA: SAMPLE ADMIN USER ----------------
-- Default Admin Login -> email: admin@novacart.com | password: admin123
INSERT IGNORE INTO users (name, email, phone, password, role) VALUES
('NovaCart Admin', 'admin@novacart.com', '9999999999', 'admin123', 'ADMIN');

-- ---------------- SEED DATA: SAMPLE PRODUCTS ----------------
INSERT IGNORE INTO products (name, category_id, description, price, old_price, image_url, rating, reviews_count, badge, stock) VALUES
('Classic Leather Jacket', 1, 'Premium genuine leather jacket with modern fit.', 4999, 6999, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500', 4.5, 128, 'NEW', 50),
('Wireless Noise-Cancel Headphones', 2, 'Industry leading noise cancellation with 30hr battery.', 7999, 9999, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500', 4.7, 340, '-20%', 80),
('Minimalist Table Lamp', 3, 'Modern minimalist lamp for your living space.', 1899, 2499, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500', 4.3, 76, NULL, 60),
('Rose Gold Perfume Set', 4, 'Luxury fragrance set in rose gold packaging.', 2599, 3199, 'https://images.unsplash.com/photo-1541643600914-78b084683601?w=500', 4.6, 210, 'NEW', 40),
('Pro Yoga Mat', 5, 'Non-slip extra thick yoga mat for all levels.', 1299, 1799, 'https://images.unsplash.com/photo-1592432678016-e910b452f9a2?w=500', 4.4, 98, NULL, 100),
('Organic Almonds 1kg', 6, 'Premium grade organic almonds, naturally grown.', 899, 1099, 'https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=500', 4.2, 55, NULL, 200),
('Gold Chronograph Watch', 7, 'Elegant gold chronograph with leather strap.', 12999, 15999, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=500', 4.8, 412, 'BESTSELLER', 25),
('RGB Mechanical Keyboard', 8, 'Hot-swappable RGB mechanical gaming keyboard.', 5499, 6999, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500', 4.6, 230, NULL, 70);
