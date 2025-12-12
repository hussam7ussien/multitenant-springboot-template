-- Coupons table
CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    discount DECIMAL(10, 2),
    discount_type VARCHAR(20),
    valid BOOLEAN DEFAULT TRUE,
    valid_from DATETIME,
    valid_to DATETIME,
    is_welcome_coupon BOOLEAN DEFAULT FALSE,
    INDEX idx_code (code),
    INDEX idx_valid (valid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

