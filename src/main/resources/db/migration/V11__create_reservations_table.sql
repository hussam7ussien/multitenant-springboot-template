-- Reservations table
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    branch_id BIGINT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    covers INT,
    table_id BIGINT,
    special_requests TEXT,
    status VARCHAR(50) DEFAULT 'pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_branch_id (branch_id),
    INDEX idx_date (date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

