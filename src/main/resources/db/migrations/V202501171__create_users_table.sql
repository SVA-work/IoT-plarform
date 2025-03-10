CREATE TABLE IF NOT EXISTS users
(
    user_id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telegram_tokens_id INTEGER,
    FOREIGN KEY (telegram_tokens_id) REFERENCES telegram_tokens(telegram_token_id) ON DELETE CASCADE
)