package test.DTO;

public class Message {
  private String userId;
  private String deviceId;
  private String login;
  private String password;
  private String telegramToken;
  private String token;
  private boolean successful;
  private String columnTitle;
  private String temperature;

  public Message() {
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getTelegramToken() {
    return telegramToken;
  }

  public void setTelegramToken(String telegramToken) {
    this.telegramToken = telegramToken;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public void setSuccessful(boolean successful) {
    this.successful = successful;
  }

  public String getColumnTitle() {
    return columnTitle;
  }

  public void setColumnTitle(String columnTitle) {
    this.columnTitle = columnTitle;
  }

  public String getTemperature() {
    return temperature;
  }

  public void setTemperature(String temperature) {
    this.temperature = temperature;
  }
}
