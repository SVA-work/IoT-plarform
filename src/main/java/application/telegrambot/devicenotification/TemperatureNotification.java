package application.telegrambot.devicenotification;

import org.springframework.stereotype.Component;

@Component
public class TemperatureNotification {

    public String lowerTempNotification(String deviceToken, String deviceType, String lowerBorderOfTemperature) {
        String response = "Температура на вашем устройсте \"%s\" типа \"%s\" опустилась ниже %s";
        return String.format(response, deviceToken, deviceType, lowerBorderOfTemperature);
    }

    public String highTempNotification(String deviceToken, String deviceType, String highBorderOfTemperature) {
        String response = "Температура на вашем устройсте \"%s\" типа \"%s\" поднялась выше %s";
        return String.format(response, deviceToken, deviceType, highBorderOfTemperature);
    }
}
