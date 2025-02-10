package application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleRequestDto {
    private String login;
    private String token;
    private String rule;

    public String getLogin() {
        return login;
    }

    public String getToken() {
        return token;
    }

    public String getRule() {
        return rule;
    }
}