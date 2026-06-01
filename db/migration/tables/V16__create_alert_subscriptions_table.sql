-- =============================================
-- V16: Create alert_subscriptions table
-- =============================================
CREATE TABLE alert_subscriptions (
    id              VARCHAR(36) PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    product_name    VARCHAR(255),
    target_price    DOUBLE PRECISION NOT NULL,
    current_price   DOUBLE PRECISION,
    notification_method VARCHAR(50),
    email           VARCHAR(255),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
