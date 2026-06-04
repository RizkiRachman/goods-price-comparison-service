CREATE TABLE feedback_questions (
    id          UUID            PRIMARY KEY,
    user_name   VARCHAR(100)    NOT NULL,
    user_email  VARCHAR(150)    NOT NULL,
    type        VARCHAR(20)     NOT NULL,
    message     TEXT            NOT NULL,
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
