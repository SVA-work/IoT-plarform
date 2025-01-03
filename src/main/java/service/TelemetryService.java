package service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import dto.MicroclimateSensor;

public class TelemetryService {

  public TelemetryService() {
  }

  public String decodeBase64(String base64Data) {
    if (base64Data == null || base64Data.isEmpty()) {
      return "";
    }
    byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  public void reportProcessing(String uuid, MicroclimateSensor deviceDto) {
    // логика обработки сообщения
  }
}
