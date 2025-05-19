CREATE TABLE IF NOT EXISTS users
(
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telegram_tokens_id INTEGER,
    FOREIGN KEY (telegram_tokens_id) REFERENCES telegram_tokens(id) ON DELETE CASCADE
)