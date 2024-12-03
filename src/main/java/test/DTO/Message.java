package test.DTO;

public class Message {
  private String userId;
  private String login;
  private String password;
  private String telegramToken;
  private String deviceId;
  private String token;
  private String ruleId;
  private String rule;
  private String telegramTokenId;
  private boolean successful;
  private String columnTitle;
  private String lowTemperature;
  private String hightTemperature;

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

  public String getTelegramToken() {
    return telegramToken;
  }

  public void setTelegramToken(String telegramToken) {
    this.telegramToken = telegramToken;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRuleId() {
    return ruleId;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public String getTelegramTokenId() {
    return telegramTokenId;
  }

  public void setTelegramTokenId(String telegramTokenId) {
    this.telegramTokenId = telegramTokenId;
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

  public String getHightTemperature() {
    return hightTemperature;
  }

  public void setHightTemperature(String hightTemperature) {
    this.hightTemperature = hightTemperature;
  }

  public String getTemperature() {
    return hightTemperature;
  }

  public void setLowTemperature(String lowTemperature) {
    this.lowTemperature = lowTemperature;
  }
}
