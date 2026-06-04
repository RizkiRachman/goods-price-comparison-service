CREATE TABLE units (
    id          VARCHAR(50)     PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    symbol      VARCHAR(10),
    type        VARCHAR(20)     NOT NULL,
    description TEXT,
    status      VARCHAR(50)     DEFAULT 'ACTIVE',
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
