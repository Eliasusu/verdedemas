-- Categories
INSERT INTO categories (id, name, description, created_at, updated_at) VALUES
(1, 'Frescos', 'Hojas verdes y vegetales listos para ensaladas', NOW(), NOW()),
(2, 'Salteados', 'Mix de vegetales listos para saltear', NOW(), NOW()),
(3, 'Horno', 'Vegetales preparados para llevar al horno', NOW(), NOW()),
(4, 'Bases Aromáticas', 'Cebollas, pimientos y mezclas aromáticas', NOW(), NOW()),
(5, 'Confort', 'Sopas y bases livianas', NOW(), NOW()),
(6, 'Tartas', 'Rellenos de vegetales para tartas y empanadas', NOW(), NOW());

-- Products - FRESCOS
INSERT INTO products (id, name, description, price, servings, usages, is_active, category_id, created_at, updated_at) VALUES
(1, 'Hojas del día', 'Mix de hojas verdes listas para usar. Lechuga, rúcula y radicheta en hojas enteras. Rinde 2–3 comidas. Refrigerado, consumir dentro de 3 días.', 320.00, 3, 'Ensaladas, acompañamientos, wraps', true, 1, NOW(), NOW()),
(2, 'Ensalada viva', 'Vegetales de estación preparados y combinados. Zanahoria, tomate cherry, repollo y hojas verdes rallados y trocados. Rinde 3–4 comidas. Refrigerado, consumir dentro de 3 días.', 380.00, 4, 'Ensaladas completas, bowls, platos fríos', true, 1, NOW(), NOW()),
(3, 'Repollo suave', 'Repollo preparado, liviano y fácil de usar. Repollo rallado fino. Rinde 3 comidas. Refrigerado, consumir dentro de 4 días.', 280.00, 3, 'Ensaladas, salteados suaves, guarniciones', true, 1, NOW(), NOW()),
(4, 'Zanahoria lista', 'Zanahoria fresca lista para usar. Zanahoria rallada o en juliana. Rinde 3–4 comidas. Refrigerado, consumir dentro de 4 días.', 300.00, 4, 'Ensaladas, salteados, rellenos', true, 1, NOW(), NOW()),

-- Products - SALTEADOS
(5, 'Salteado diario', 'Mix de vegetales listo para saltear. Brócoli, tomate cherry, pimiento verde, zanahoria y cebolla en juliana y trozos medianos. Rinde 3–4 comidas. Refrigerado, consumir dentro de 4 días.', 420.00, 4, 'Salteados rápidos, guarniciones, woks', true, 2, NOW(), NOW()),
(6, 'Salteado verde', 'Salteado suave con zucchini y vegetales. Zucchini, cebolla, pimiento y zanahoria en juliana y cubo mediano. Rinde 3 comidas. Refrigerado, consumir dentro de 4 días.', 400.00, 3, 'Salteados livianos, tortillas, rellenos', true, 2, NOW(), NOW()),
(7, 'Salteado intenso', 'Mix de vegetales con sabor profundo. Berenjena, zanahoria, pimiento y cebolla en cubo mediano. Rinde 3 comidas. Refrigerado, consumir dentro de 4 días.', 400.00, 3, 'Salteados, tartas, platos al horno', true, 2, NOW(), NOW()),

-- Products - HORNO
(8, 'Horno simple', 'Vegetales listos para llevar al horno. Calabaza, zanahoria y camote en cubo y rodajas. Rinde 3–4 comidas. Refrigerado, consumir dentro de 4 días.', 380.00, 4, 'Horno, guarniciones, platos principales', true, 3, NOW(), NOW()),
(9, 'Horno completo', 'Mix de vegetales para platos más sabrosos. Zanahoria, calabaza, camote, cebolla y pimiento en cubo y rodajas. Rinde 4 comidas. Refrigerado, consumir dentro de 4 días.', 450.00, 4, 'Horno, tartas, guisos suaves', true, 3, NOW(), NOW()),
(10, 'Camote al punto', 'Camote listo para cocinar. Camote en rodajas. Rinde 2–3 comidas. Refrigerado, consumir dentro de 4 días.', 300.00, 3, 'Horno, salteados, purés', true, 3, NOW(), NOW()),
(11, 'Calabaza lista', 'Calabaza preparada para usar. Calabaza en cubo mediano. Rinde 3 comidas. Refrigerado, consumir dentro de 4 días.', 320.00, 3, 'Horno, sopas, tartas', true, 3, NOW(), NOW()),
(12, 'Zapallito listo', 'Zapallito fresco listo para usar. Zapallito en cubo mediano. Rinde 3 comidas. Refrigerado, consumir dentro de 3 días.', 300.00, 3, 'Salteados, rellenos, guarniciones', true, 3, NOW(), NOW()),

-- Products - BASES AROMÁTICAS
(13, 'Cebolla lista', 'Cebolla preparada para cocinar. Cebolla en cubo o juliana. Rinde 4–5 comidas. Refrigerado, consumir dentro de 5 días.', 250.00, 5, 'Salteados, guisos, rellenos', true, 4, NOW(), NOW()),
(14, 'Pimiento listo', 'Pimiento fresco listo para usar. Pimiento en cubo o juliana. Rinde 3–4 comidas. Refrigerado, consumir dentro de 4 días.', 300.00, 4, 'Salteados, guarniciones, rellenos', true, 4, NOW(), NOW()),
(15, 'Base de sabor', 'Mezcla lista de cebolla y pimiento. Cebolla y pimiento en cubo o juliana. Rinde 5–6 comidas. Refrigerado, consumir dentro de 5 días.', 350.00, 6, 'Base para casi cualquier preparación', true, 4, NOW(), NOW()),

-- Products - CONFORTTRUNCATE TABLE products CASCADE;
TRUNCATE TABLE categories CASCADE;
TRUNCATE TABLE delivery_zones CASCADE;
TRUNCATE TABLE orders CASCADE;
TRUNCATE TABLE order_items CASCADE;
DELETE FROM flyway_schema_history WHERE version = '2';
(16, 'Sopa casera', 'Sopa de vegetales suaves lista para calentar. Zapallito, calabaza, zapallo y choclo cocidos y procesados. Rinde 3–4 porciones. Refrigerado, consumir dentro de 3 días.', 380.00, 4, 'Cenas livianas, entradas, colaciones', true, 5, NOW(), NOW()),
(17, 'Revuelto suave', 'Base liviana lista para saltear. Zapallito, cebolla y pimiento en cubo chico. Rinde 3 comidas. Refrigerado, consumir dentro de 3 días.', 320.00, 3, 'Salteados suaves, tortillas, tartas', true, 5, NOW(), NOW()),

-- Products - TARTAS
(18, 'Relleno clásico', 'Base de vegetales lista para tartas. Cebolla, zanahoria y pimiento en cubo chico. Rinde 1–2 tartas. Refrigerado, consumir dentro de 4 días.', 350.00, 2, 'Tartas, empanadas, rellenos', true, 6, NOW(), NOW()),
(19, 'Relleno verde', 'Relleno suave con vegetales verdes. Zapallito, cebolla y zanahoria en cubo chico. Rinde 1–2 tartas. Refrigerado, consumir dentro de 3 días.', 340.00, 2, 'Tartas livianas, wraps calientes', true, 6, NOW(), NOW()),
(20, 'Relleno intenso', 'Relleno con vegetales de sabor profundo. Berenjena, cebolla y pimiento en cubo chico. Rinde 1–2 tartas. Refrigerado, consumir dentro de 4 días.', 360.00, 2, 'Tartas, platos al horno', true, 6, NOW(), NOW());

-- Delivery Zones
INSERT INTO delivery_zones (id, name, description, shipping_cost, delivery_day, is_active, created_at, updated_at) VALUES
(1, 'Zona Norte', 'Fisherton, Alberdi, Rucci', 300.00, 'FRIDAY_PM', true, NOW(), NOW()),
(2, 'Zona Sur', 'Echesortu, Azcuénaga', 300.00, 'SATURDAY_AM', true, NOW(), NOW());