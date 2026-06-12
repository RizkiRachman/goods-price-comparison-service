-- =============================================
-- V17: Add FK constraint and index for alert_subscriptions.product_id
-- =============================================
-- alert_subscriptions.product_id references products(id) but has no FK
-- constraint and no index on the FK column.

ALTER TABLE alert_subscriptions
    ADD CONSTRAINT fk_alert_subscriptions_product
    FOREIGN KEY (product_id) REFERENCES products (id);

CREATE INDEX IF NOT EXISTS idx_alert_subscriptions_product_id
    ON alert_subscriptions (product_id);
