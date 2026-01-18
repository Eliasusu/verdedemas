-- Categories
INSERT INTO categories (id, name, description, created_at, updated_at) VALUES
(1, 'Bases de Vegetales', 'Vegetales preparados listos para usar', NOW(), NOW()),
(2, 'Bases de Cereales', 'Granos y legumbres cocidos', NOW(), NOW());

-- Products
INSERT INTO products (id, name, description, price, servings, usages, is_active, category_id, created_at, updated_at) VALUES
(1, 'Base Vital de Vegetales', 'Vegetales salteados listos para combinar. Rinde 2-3 comidas.', 450.00, 3, 'Salteados, Tartas, Guisos', true, 1, NOW(), NOW()),
(2, 'Mix de Quinoa y Verduras', 'Quinoa cocida con vegetales frescos. Rinde 2 porciones.', 550.00, 2, 'Bowl, Ensaladas', true, 2, NOW(), NOW());

-- Delivery Zones
INSERT INTO delivery_zones (id, name, description, shipping_cost, delivery_day, is_active, created_at, updated_at) VALUES
(1, 'Zona Norte', 'Fisherton, Alberdi, Rucci', 300.00, 'FRIDAY_PM', true, NOW(), NOW()),
(2, 'Zona Sur', 'Echesortu, Azcuénaga', 300.00, 'SATURDAY_AM', true, NOW(), NOW());