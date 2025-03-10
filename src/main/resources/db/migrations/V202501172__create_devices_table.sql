CREATE TABLE IF NOT EXISTS devices
(
    device_id SERIAL PRIMARY KEY,
    device_name VARCHAR(255) NOT NULL,
    user_id INTEGER REFERENCES users(user_id),
    uuid VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
)