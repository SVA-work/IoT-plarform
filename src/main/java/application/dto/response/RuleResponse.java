package application.dto.response;

import lombok.Data;

@Data
public class RuleResponse {
    private String rule;
    private Integer lowestValue;
    private Integer highestValue;
    private String deviceName;
}
