package telegrambot.devicenotification;

public class TemperatureNotification {

  public String lowerTempNotification(String deviceName, String lowerBorderOfTemperature) {
    String response = "Температура на устройсте %s опустилась ниже %s";
    return String.format(response, deviceName, lowerBorderOfTemperature);
  }

  public String highTempNotification(String deviceName, String highBorderOfTemperature) {
    String response = "Температура на устройсте %s поднялась выше %s";
    return String.format(response, deviceName, highBorderOfTemperature);
  }
}
