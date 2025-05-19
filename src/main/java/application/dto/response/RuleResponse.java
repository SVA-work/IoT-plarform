package application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleResponse {
    private String rule;
    private Double value;
    private String comparison;
    private String deviceName;
}
