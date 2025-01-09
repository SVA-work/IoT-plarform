package dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceRequestDto {
  private String login;
  private String token;

  public String getLogin() {
    return login;
  }

  public String getToken() {
    return token;
  }
}
