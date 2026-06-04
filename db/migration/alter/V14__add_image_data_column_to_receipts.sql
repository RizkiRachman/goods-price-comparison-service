-- =============================================
-- V14: Add image_data column to receipts table
-- =============================================
-- Entity ReceiptEntity has an 'image_data' BYTEA field
-- that was not included in the initial table creation (V3).
-- This column stores the raw receipt image bytes.

ALTER TABLE receipts
    ADD COLUMN image_data BYTEA;
