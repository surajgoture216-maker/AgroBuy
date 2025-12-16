-- Create default admin user (password: admin123)
INSERT INTO users (name, email, password, phone, address, role, created_at) VALUES
('Admin User', 'admin@fertilizer.com', '$2a$10$1uTN2B6l..9QhHSuvLt6Ie41aSxbVvtvb5TKfvkhiprIAQHvdt/0m', '+1234567890', 'Admin Office, Fertilizer Shop', 'ADMIN', CURRENT_TIMESTAMP);

-- Create sample customers (password: password123)
INSERT INTO users (name, email, password, phone, address, role, created_at) VALUES
('John Doe', 'john@example.com', '$2a$10$f3JS.kvjaNaN3q2AgxLYAOvmit/I11QpvvQ0f/uWmX28faAoBe36m', '+1234567891', '123 Main Street, City, State', 'CUSTOMER', CURRENT_TIMESTAMP),
('Jane Smith', 'jane@example.com', '$2a$10$f3JS.kvjaNaN3q2AgxLYAOvmit/I11QpvvQ0f/uWmX28faAoBe36m', '+1234567892', '456 Oak Avenue, City, State', 'CUSTOMER', CURRENT_TIMESTAMP),
('Bob Johnson', 'bob@example.com', '$2a$10$f3JS.kvjaNaN3q2AgxLYAOvmit/I11QpvvQ0f/uWmX28faAoBe36m', '+1234567893', '789 Pine Road, City, State', 'CUSTOMER', CURRENT_TIMESTAMP);

-- Create sample fertilizer products
INSERT INTO products (name, description, image_url, price, stock, category, brand, weight, is_active, created_at) VALUES
-- NPK Fertilizers
('NPK 20-20-20', 'Complete balanced fertilizer with equal parts nitrogen, phosphorus, and potassium for all crops', 'https://via.placeholder.com/300x200/28a745/ffffff?text=NPK+20-20-20', 500.00, 50, 'NPK', 'GreenGrow', 50.0, true, CURRENT_TIMESTAMP),
('NPK 10-26-26', 'High phosphorus and potassium fertilizer for flowering and fruiting crops', 'https://via.placeholder.com/300x200/28a745/ffffff?text=NPK+10-26-26', 550.00, 30, 'NPK', 'CropMax', 50.0, true, CURRENT_TIMESTAMP),
('NPK 17-6-10', 'Nitrogen-rich fertilizer for vegetative growth in cereals and vegetables', 'https://via.placeholder.com/300x200/28a745/ffffff?text=NPK+17-6-10', 480.00, 40, 'NPK', 'AgriPlus', 50.0, true, CURRENT_TIMESTAMP),

-- Organic Fertilizers
('Vermicompost', 'Premium vermicompost made from earthworm castings, rich in nutrients and beneficial microorganisms', 'https://via.placeholder.com/300x200/27ae60/ffffff?text=Vermicompost', 200.00, 100, 'Organic', 'NatureCare', 25.0, true, CURRENT_TIMESTAMP),
('Cow Manure Compost', 'Well-decomposed cow manure compost, excellent for improving soil structure', 'https://via.placeholder.com/300x200/27ae60/ffffff?text=Cow+Manure', 150.00, 80, 'Organic', 'EcoFarm', 40.0, true, CURRENT_TIMESTAMP),
('Bone Meal', 'Organic phosphorus source from ground animal bones, promotes root development and flowering', 'https://via.placeholder.com/300x200/27ae60/ffffff?text=Bone+Meal', 300.00, 60, 'Organic', 'OrganicLife', 25.0, true, CURRENT_TIMESTAMP),

-- Bio Fertilizers
('Rhizobium Inoculant', 'Nitrogen-fixing bacteria for legume crops, reduces nitrogen fertilizer requirement', 'https://via.placeholder.com/300x200/1abc9c/ffffff?text=Rhizobium', 400.00, 25, 'Bio', 'BioAgri', 1.0, true, CURRENT_TIMESTAMP),
('Azospirillum', 'Free-living nitrogen-fixing bacteria for cereals and vegetables', 'https://via.placeholder.com/300x200/1abc9c/ffffff?text=Azospirillum', 350.00, 30, 'Bio', 'MicrobeLife', 1.0, true, CURRENT_TIMESTAMP),
('Phosphate Solubilizing Bacteria', 'Bacteria that convert insoluble phosphates into plant-available forms', 'https://via.placeholder.com/300x200/1abc9c/ffffff?text=PSB', 380.00, 20, 'Bio', 'SoilBio', 1.0, true, CURRENT_TIMESTAMP),

-- Micro-nutrients
('Zinc Sulfate', 'Essential zinc supplement for preventing zinc deficiency in crops', 'https://via.placeholder.com/300x200/e67e22/ffffff?text=Zinc+Sulfate', 250.00, 45, 'Micro-nutrients', 'ChelatePro', 25.0, true, CURRENT_TIMESTAMP),
('Iron Chelate', 'Chelated iron for preventing and correcting iron chlorosis in plants', 'https://via.placeholder.com/300x200/e67e22/ffffff?text=Iron+Chelate', 600.00, 20, 'Micro-nutrients', 'ChelatePro', 5.0, true, CURRENT_TIMESTAMP),
('Boron Granules', 'Boron supplement essential for cell wall formation and reproductive development', 'https://via.placeholder.com/300x200/e67e22/ffffff?text=Boron', 320.00, 35, 'Micro-nutrients', 'CropHealth', 25.0, true, CURRENT_TIMESTAMP),

-- Specialty Fertilizers
('Urea', 'High-nitrogen fertilizer (46% N) for rapid vegetative growth', 'https://via.placeholder.com/300x200/3498db/ffffff?text=Urea', 280.00, 60, 'NPK', 'NitroMax', 50.0, true, CURRENT_TIMESTAMP),
('DAP (Di-Ammonium Phosphate)', 'Phosphorus and nitrogen source for seedling establishment and root development', 'https://via.placeholder.com/300x200/3498db/ffffff?text=DAP', 320.00, 45, 'NPK', 'PhosphatePro', 50.0, true, CURRENT_TIMESTAMP),
('MOP (Muriate of Potash)', 'Potassium chloride for improving fruit quality and stress tolerance', 'https://via.placeholder.com/300x200/3498db/ffffff?text=MOP', 350.00, 40, 'NPK', 'PotashPlus', 50.0, true, CURRENT_TIMESTAMP);

-- Sample Orders (some pending, some confirmed, some delivered)
INSERT INTO orders (customer_id, total_amount, status, shipping_address, created_at, updated_at) VALUES
(2, 1250.00, 'DELIVERED', '123 Main Street, City, State', '2025-11-10 10:00:00', '2025-11-15 11:00:00'),
(2, 800.00, 'CONFIRMED', '123 Main Street, City, State', '2025-11-20 14:30:00', '2025-11-20 14:30:00'),
(3, 450.00, 'SHIPPED', '456 Oak Avenue, City, State', '2025-11-24 09:15:00', '2025-11-26 16:45:00'),
(3, 300.00, 'PENDING', '456 Oak Avenue, City, State', '2025-11-26 10:00:00', '2025-11-26 10:00:00'),
(4, 750.00, 'CANCELLED', '789 Pine Road, City, State', '2025-11-17 13:20:00', '2025-11-19 15:30:00');

-- Sample Order Items
INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES
-- Order 1 items (DELIVERED)
(1, 1, 2, 500.00),
(1, 7, 1, 250.00),
-- Order 2 items (CONFIRMED)
(2, 4, 1, 200.00),
(2, 10, 2, 300.00),
-- Order 3 items (SHIPPED)
(3, 8, 1, 350.00),
(3, 11, 1, 400.00),
-- Order 4 items (PENDING)
(4, 2, 1, 550.00),
-- Order 5 items (CANCELLED)
(5, 3, 1, 480.00),
(5, 5, 1, 150.00),
(5, 12, 1, 320.00);
