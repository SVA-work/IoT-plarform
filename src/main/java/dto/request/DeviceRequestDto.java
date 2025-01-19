package dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceRequestDto {
    private String login;
    private String uuid;
    private String type;

    public String getLogin() {
        return login;
    }

    public String getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
