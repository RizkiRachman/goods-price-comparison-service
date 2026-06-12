-- =============================================
-- V19: Change receipts.receipt_date from VARCHAR(255) to DATE
-- =============================================
-- The receipt_date column is VARCHAR(255) but should be DATE.
-- Steps: add temp DATE column, migrate data, drop old, rename, re-index.

ALTER TABLE receipts ADD COLUMN receipt_date_temp DATE;

UPDATE receipts
SET receipt_date_temp =
    CASE
        WHEN receipt_date ~ '^\d{4}-\d{2}-\d{2}$' THEN receipt_date::DATE
        WHEN receipt_date ~ '^\d{2}/\d{2}/\d{4}$' THEN TO_DATE(receipt_date, 'MM/DD/YYYY')
        WHEN receipt_date ~ '^\d{4}/\d{2}/\d{2}$' THEN TO_DATE(receipt_date, 'YYYY/MM/DD')
        ELSE NULL
    END;

DROP INDEX IF EXISTS idx_receipts_receipt_date;
ALTER TABLE receipts DROP COLUMN receipt_date;
ALTER TABLE receipts RENAME COLUMN receipt_date_temp TO receipt_date;

CREATE INDEX IF NOT EXISTS idx_receipts_receipt_date ON receipts (receipt_date);
