-- =============================================
-- V21: Change prices.price/unit_price from DOUBLE PRECISION to DECIMAL(10,2)
-- =============================================

ALTER TABLE prices ADD COLUMN price_temp DECIMAL(10,2);
ALTER TABLE prices ADD COLUMN unit_price_temp DECIMAL(10,2);

UPDATE prices SET
    price_temp = price::DECIMAL(10,2),
    unit_price_temp = unit_price::DECIMAL(10,2);

ALTER TABLE prices DROP COLUMN price;
ALTER TABLE prices DROP COLUMN unit_price;

ALTER TABLE prices RENAME COLUMN price_temp TO price;
ALTER TABLE prices RENAME COLUMN unit_price_temp TO unit_price;

-- Restore NOT NULL constraint (lost during temp-column swap)
ALTER TABLE prices ALTER COLUMN price SET NOT NULL;
