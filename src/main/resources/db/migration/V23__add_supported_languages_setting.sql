-- Seed supported languages setting
INSERT INTO tenant_settings (`key`, `value`, data_type) VALUES
('supported_languages', '[{"code":"en","name":"English","isDefault":true,"direction":"ltr"},{"code":"ar","name":"Arabic","isDefault":false,"direction":"rtl"}]', 'json')
ON DUPLICATE KEY UPDATE `value` = VALUES(`value`), data_type = VALUES(data_type);
