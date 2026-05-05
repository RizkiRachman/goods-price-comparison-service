ALTER TABLE products ADD COLUMN last_price_update TIMESTAMP;
ALTER TABLE products ADD COLUMN summary_last_calculated TIMESTAMP;

CREATE INDEX idx_products_last_price_update ON products (last_price_update);
CREATE INDEX idx_products_summary_last_calc ON products (summary_last_calculated);
