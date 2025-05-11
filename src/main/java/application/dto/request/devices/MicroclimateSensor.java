package application.dto.request.devices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MicroclimateSensor {
    private String temperature;
    private String humidity;
    private String pressure;
    private String aqi;
    private String rssi;
    private String snr;
    private String uuid;
    private String message;
}
