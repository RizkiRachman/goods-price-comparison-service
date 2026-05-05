-- =============================================
-- V6: Add missing columns to products table
-- =============================================
-- Entity ProductEntity has 'brand' and 'status' fields
-- that were not included in the initial table creation (V2)

ALTER TABLE products
    ADD COLUMN brand VARCHAR(255);

ALTER TABLE products
    ADD COLUMN status VARCHAR(255);
