CREATE TABLE IF NOT EXISTS rules
(
    rule_id SERIAL PRIMARY KEY,
    rule VARCHAR(255) NOT NULL,
    lowestValue INTEGER NOT NULL,
    highestValue INTEGER NOT NULL,
    device_id INTEGER,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
)