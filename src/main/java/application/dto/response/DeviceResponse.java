package application.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class DeviceResponse {
    private String uuid;
    private String type;
    private String login;
    private List<RuleResponse> rules;
}
