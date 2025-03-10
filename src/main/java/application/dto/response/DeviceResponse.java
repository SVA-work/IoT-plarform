package application.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceResponse {
    private String uuid;
    private String type;
    private String login;
    private String deviceName;
    private List<RuleResponse> rules;
}
