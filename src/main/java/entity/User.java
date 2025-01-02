package entity;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
  private int id;
  private String login;
  private String password;
  private List<Device> devices;
  private String telegramToken;
  private boolean verified = false;


  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public Boolean getVerified() {
    return verified;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", login='" + login + '\'' +
            ", password=" + password +
            ", devices='" + devices.toString() + '\'' +
            ", telegramToken='" + telegramToken + '\'' +
            '}';
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<Device> getDevices() {
    return devices;
  }

  public void setDevices(List<Device> devices) {
    this.devices = devices;
  }

  public String getTelegramToken() {
    return telegramToken;
  }

  public void setTelegramToken(String telegramToken) {
    this.telegramToken = telegramToken;
  }
}