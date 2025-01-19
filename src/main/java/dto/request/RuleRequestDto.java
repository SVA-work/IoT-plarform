package dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleRequestDto {
    private String login;
    private String uuid;
    private String rule;

    public String getLogin() {
        return login;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRule() {
        return rule;
    }
}