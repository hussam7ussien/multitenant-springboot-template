CREATE TABLE product_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE option_choices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price_modifier DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    display_order INT NOT NULL DEFAULT 0,
    FOREIGN KEY (option_id) REFERENCES product_options(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_options_product_id ON product_options(product_id);
CREATE INDEX idx_option_choices_option_id ON option_choices(option_id);
