-- Orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    branch_id BIGINT,
    status VARCHAR(50) NOT NULL,
    order_mode VARCHAR(50) NOT NULL,
    order_code VARCHAR(100) UNIQUE,
    delivery_address VARCHAR(500),
    
    -- Financial details
    subtotal DECIMAL(10, 2) DEFAULT 0,
    vat DECIMAL(10, 2) DEFAULT 0,
    total DECIMAL(10, 2) DEFAULT 0,
    discount DECIMAL(10, 2) DEFAULT 0,
    special_discount DECIMAL(10, 2) DEFAULT 0,
    covers_fee DECIMAL(10, 2) DEFAULT 0,
    
    -- Payment and promo
    payment_method VARCHAR(50),
    promo_code VARCHAR(100),
    coupon_code VARCHAR(100),
    
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_branch_id (branch_id),
    INDEX idx_status (status),
    INDEX idx_order_code (order_code),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
