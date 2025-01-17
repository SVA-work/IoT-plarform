package telegrambot.devicenotification;

public class TemperatureNotification {

  public String lowerTempNotification(String deviceName, String lowerBorderOfTemperature) {
    String response = "Температура на устройсте %s опустилась ниже %s";
    String formattedResponse = String.format(response, deviceName, lowerBorderOfTemperature);
    return formattedResponse;
  }

  public String highTempNotification(String deviceName, String highBorderOfTemperature) {
    String response = "Температура на устройсте %s поднялась выше %s";
    String formattedResponse = String.format(response, deviceName, highBorderOfTemperature);
    return formattedResponse;
  }
}
