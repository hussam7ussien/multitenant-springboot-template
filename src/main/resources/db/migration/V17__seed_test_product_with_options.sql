-- Seed a test product with multiple options and variations for UI testing
-- This will add one product into category_id = 2 (Main Courses)

INSERT INTO products (name, description, price, image, category_id) VALUES
('Test Steak Deluxe', 'A test product for UI testing with multiple options and variations', 18.99, '/assets/images/product.png', 2);

-- Get the last inserted product id (works for MySQL in Flyway migrations)
SET @prod_id = LAST_INSERT_ID();

-- Add two options: Temperature (mandatory, single choice) and Sides (optional, multiple choices)
INSERT INTO product_options (product_id, name, description, mandatory, display_order) VALUES
(@prod_id, 'Cook Temperature', 'How you want your steak cooked', TRUE, 1),
(@prod_id, 'Choose a Side', 'Pick one or more sides', FALSE, 2);

-- Retrieve option ids
SET @opt_temp = (SELECT id FROM product_options WHERE product_id = @prod_id AND name = 'Cook Temperature' LIMIT 1);
SET @opt_sides = (SELECT id FROM product_options WHERE product_id = @prod_id AND name = 'Choose a Side' LIMIT 1);

-- Insert choices for Temperature (no price modifiers)
INSERT INTO option_choices (option_id, name, description, price_modifier, display_order) VALUES
(@opt_temp, 'Rare', 'Lightly seared, cool red center', 0.00, 1),
(@opt_temp, 'Medium Rare', 'Warm red center', 0.00, 2),
(@opt_temp, 'Medium', 'Warm pink center', 0.00, 3),
(@opt_temp, 'Well Done', 'Fully cooked', 0.00, 4);

-- Insert choices for Sides (some have price modifiers)
INSERT INTO option_choices (option_id, name, description, price_modifier, display_order) VALUES
(@opt_sides, 'French Fries', 'Crispy golden fries', 0.00, 1),
(@opt_sides, 'Mashed Potatoes', 'Creamy mashed potatoes', 0.00, 2),
(@opt_sides, 'Grilled Vegetables', 'Seasonal veggies', 0.00, 3),
(@opt_sides, 'Truffle Fries', 'Fries with truffle oil', 2.50, 4);

-- Ensure indices exist (should be created by earlier migrations but harmless to include)
-- index exists in earlier migrations; skip creating it here to avoid incompatible syntax
