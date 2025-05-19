package application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleRequest {
    private String login;
    private String deviceName;
    private String rule;
    private Double value;
    private String comparison;
    private String updateRule;
}
