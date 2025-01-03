package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MicroclimateSensor {
  private String temperature;
  private String humidity;
  private String pressure;
  private String aqi;
  private String rssi;
  private String snr;
  private String uuid;
  private String base64Message;

  public MicroclimateSensor() {
  }

  public String getTemperature() {
    return temperature;
  }

  /**
   * @param temperature значение температуры.
   */
  public void setTemperature(String temperature) {
    this.temperature = temperature;
  }

  public String getHumidity() {
    return humidity;
  }

  /**
   * @param humidity значение влажности.
   */
  public void setHumidity(String humidity) {
    this.humidity = humidity;
  }

  public String getPressure() {
    return pressure;
  }

  /**
   * @param pressure значение давления.
   */
  public void setPressure(String pressure) {
    this.pressure = pressure;
  }

  public String getAqi() {
    return aqi;
  }

  /**
   * @param aqi значение индекса качества воздуха.
   */
  public void setAqi(String aqi) {
    this.aqi = aqi;
  }

  public String getRssi() {
    return rssi;
  }

  /**
   * @param rssi значение уровня сигнала.
   */
  public void setRssi(String rssi) {
    this.rssi = rssi;
  }

  public String getSnr() {
    return snr;
  }

  /**
   * @param snr значение отношения сигнал/шум.
   */
  public void setSnr(String snr) {
    this.snr = snr;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getBase64Message() {
    return base64Message;
  }

  public void setBase64Message(String base64Message) {
    this.base64Message = base64Message;
  }
}
