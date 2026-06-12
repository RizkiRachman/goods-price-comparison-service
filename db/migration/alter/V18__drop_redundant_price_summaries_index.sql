-- =============================================
-- V18: Drop redundant index on product_price_summaries.product_id
-- =============================================
-- product_id is the PRIMARY KEY of product_price_summaries, which
-- auto-creates a unique B-tree index. The separate
-- idx_price_summaries_product_id index is redundant.

DROP INDEX IF EXISTS idx_price_summaries_product_id;
