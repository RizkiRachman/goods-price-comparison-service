CREATE TABLE product_price_summaries (
    product_id              BIGINT PRIMARY KEY,
    avg_price               DECIMAL(10,2),
    min_price               DECIMAL(10,2),
    max_price               DECIMAL(10,2),
    store_count             INT,
    price_count             INT,
    last_calculated_at      TIMESTAMP NOT NULL,
    last_price_date         DATE
);

CREATE INDEX idx_price_summaries_product_id ON product_price_summaries (product_id);
CREATE INDEX idx_price_summaries_last_calculated ON product_price_summaries (last_calculated_at);
