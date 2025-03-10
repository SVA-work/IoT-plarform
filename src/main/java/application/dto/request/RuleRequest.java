package application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleRequest {
    private String login;
    private String DeviceName;
    private String rule;
    private Integer lowestValue;
    private Integer highestValue;
    private String updateRule;
}