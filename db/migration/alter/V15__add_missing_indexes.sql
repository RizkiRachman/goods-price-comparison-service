-- =============================================
-- V15: Add missing database indexes
-- =============================================
-- Adds indexes for commonly queried columns
-- that were missing from initial table creation.
-- Improves query performance on filtered lookups.
--
-- Indexes added:
--   - products.category        (category-based filtering)
--   - stores.name              (store name lookups)
--   - receipts.status          (status-based filtering)
--   - receipts.receipt_date    (date-range queries)

CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_stores_name ON stores(name);
CREATE INDEX IF NOT EXISTS idx_receipts_status ON receipts(status);
CREATE INDEX IF NOT EXISTS idx_receipts_receipt_date ON receipts(receipt_date);
