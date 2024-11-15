package test.DTO;

import java.util.HashMap;
import java.util.Map;

import test.entity.Device;

public class Message {
  private boolean verified = false;
  private String login;
  private String password;
  private Map<String, Device> userDevices = new HashMap<>();

  public Message() {
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

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public Boolean getVerified() {
    return verified;
  }

  public void setUserDevices(Map<String, Device> userDevices) {
    this.userDevices = userDevices;
  }

  public Map<String, Device> getUserDevices() {
    return userDevices;
  }
}
