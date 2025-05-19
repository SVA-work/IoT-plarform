package application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeviceResponse {
    private String uuid;
    private String type;
    private String login;
    private String deviceName;
    private List<RuleResponse> rules;
}
