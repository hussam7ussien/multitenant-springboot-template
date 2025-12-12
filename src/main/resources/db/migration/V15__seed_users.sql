-- Seed Users
-- Note: Passwords should be hashed in production. These are example plain passwords for testing.
-- For real use, ensure proper bcrypt or similar hashing is applied.
INSERT INTO users (username, password, email, name, phone, verified, otp) VALUES
('mobileapp', 'country@hills123', 'mobileapp@example.com', 'Mobile App User', '212-555-0100', TRUE, NULL),
('testuser', 'testpass123', 'testuser@example.com', 'Test User', '212-555-0101', TRUE, NULL),
('admin', 'admin123', 'admin@example.com', 'Admin User', '212-555-0102', TRUE, NULL),
('customer1', 'customer123', 'customer1@example.com', 'John Doe', '718-555-0103', TRUE, NULL),
('customer2', 'customer123', 'customer2@example.com', 'Jane Smith', '212-555-0104', TRUE, NULL);
