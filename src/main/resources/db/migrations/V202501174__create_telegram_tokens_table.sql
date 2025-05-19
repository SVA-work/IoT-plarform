CREATE TABLE IF NOT EXISTS telegram_tokens
(
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    telegram_token VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)