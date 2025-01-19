CREATE TABLE IF NOT EXISTS telegramToken
(
    telegram_token_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    telegram_token VARCHAR(255) NOT NULL
)