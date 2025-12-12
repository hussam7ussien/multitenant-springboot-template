-- Seed default tenant settings (Key-Value format)
INSERT INTO tenant_settings (`key`, `value`, data_type) VALUES
('cover_image', 'https://via.placeholder.com/1200x400?text=Restaurant+Cover', 'string'),
('title', 'Country Hills', 'string'),
('subtitle', 'Order delicious food online', 'string'),
('eat_in_enabled', 'true', 'boolean'),
('delivery_enabled', 'true', 'boolean'),
('pickup_enabled', 'true', 'boolean');
