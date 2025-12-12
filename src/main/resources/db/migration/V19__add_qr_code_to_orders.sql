-- Add QR code URL field to orders table
ALTER TABLE orders ADD COLUMN qr_code_url VARCHAR(1000);
