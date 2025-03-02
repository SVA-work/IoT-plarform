package application.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TelemetryResponse {
    private String temperature;
    private String humidity;
    private String pressure;
    private String aqi;
    private String rssi;
    private String snr;
    private LocalDateTime time;
}
