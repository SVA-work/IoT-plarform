package application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TelemetryResponse {
    private String temperature;
    private String humidity;
    private String pressure;
    private String aqi;
    private String rssi;
    private String snr;
    private LocalDateTime time;
}
