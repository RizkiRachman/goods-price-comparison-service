-- =============================================
-- V7: Add missing columns to stores table
-- =============================================
-- Entity StoreEntity has additional fields that were not
-- included in the initial table creation (V1):
--   - chain
--   - address
--   - latitude
--   - longitude
--   - status

ALTER TABLE stores
    ADD COLUMN chain VARCHAR(255);

ALTER TABLE stores
    ADD COLUMN address VARCHAR(500);

ALTER TABLE stores
    ADD COLUMN latitude DOUBLE PRECISION;

ALTER TABLE stores
    ADD COLUMN longitude DOUBLE PRECISION;

ALTER TABLE stores
    ADD COLUMN status VARCHAR(255);
