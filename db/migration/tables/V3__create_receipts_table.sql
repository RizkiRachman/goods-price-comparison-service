CREATE TABLE receipts (
    id                  UUID            NOT NULL,
    image_hash          VARCHAR(255)    NOT NULL,
    original_filename   VARCHAR(255),
    status              VARCHAR(50)     NOT NULL,
    error_message       VARCHAR(1024),
    store_name          VARCHAR(255),
    store_location      VARCHAR(255),
    receipt_date        VARCHAR(255),
    total_amount        DOUBLE PRECISION,
    extracted_data      TEXT,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP       NOT NULL,
    processed_at        TIMESTAMP,

    CONSTRAINT pk_receipts PRIMARY KEY (id),
    CONSTRAINT uc_receipts_image_hash UNIQUE (image_hash)
);
