CREATE TABLE IF NOT EXISTS devices
(
    device_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    token VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL
)