CREATE TABLE IF NOT EXISTS rules
(
    rule_id SERIAL PRIMARY KEY,
    device_id INTEGER REFERENCES devices(device_id),
    rule VARCHAR(255) NOT NULL
)