package application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceRequest {
    private String login;
    private String uuid;
    private String type;
    private String deviceName;
}
