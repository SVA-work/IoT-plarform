package dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationRequestDto {

  private String login;
  private String password;
  private String telegramToken;
      
  public String getLogin() {
    return login;
  }
      
  public String getPassword() {
    return password;
  }
      
  public String getTelegramToken() {
    return telegramToken;
  }
}
