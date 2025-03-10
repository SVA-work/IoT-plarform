CREATE TABLE IF NOT EXISTS telegramToken
(
    telemetry_id SERIAL PRIMARY KEY,
    temperature VARCHAR(255),
    humidity VARCHAR(255),
    pressure VARCHAR(255),
    aqi VARCHAR(255),
    rssi VARCHAR(255),
    snr VARCHAR(255),
    time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    device_id INTEGER,
    FOREIGN KEY (device_id) REFERENCES devices(device_id) ON DELETE CASCADE
)
