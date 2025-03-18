CREATE TABLE IF NOT EXISTS rules
(
    id SERIAL PRIMARY KEY,
    rule VARCHAR(255) NOT NULL,
    lowest_value INTEGER NOT NULL,
    highest_value INTEGER NOT NULL,
    device_id INTEGER,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
)
