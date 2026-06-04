CREATE TABLE categories (
    id          VARCHAR(50)     PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    description TEXT,
    status      VARCHAR(50)     DEFAULT 'ACTIVE',
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
