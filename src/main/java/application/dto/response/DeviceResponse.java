package application.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DeviceResponse {
    private String uuid;
    private String type;
    private String login;
    private String deviceName;
    private List<RuleResponse> rules;
}
