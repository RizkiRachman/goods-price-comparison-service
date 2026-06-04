CREATE TABLE activity_logs (
    id          UUID            NOT NULL,
    type        VARCHAR(50)     NOT NULL,
    action      VARCHAR(20)     NOT NULL,
    description TEXT,
    created_at  TIMESTAMP       NOT NULL,
    updated_at  TIMESTAMP       NOT NULL,

    CONSTRAINT pk_activity_logs PRIMARY KEY (id)
);

CREATE INDEX idx_activity_logs_type ON activity_logs (type);
CREATE INDEX idx_activity_logs_created_at ON activity_logs (created_at);
