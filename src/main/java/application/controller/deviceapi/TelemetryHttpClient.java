package application.controller.deviceapi;

import application.dto.request.devices.MicroclimateSensor;
import application.service.TelemetryService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
public class TelemetryHttpClient {

  @Autowired
  TelemetryService telemetryService;

  private final TaskScheduler taskScheduler;

  public TelemetryHttpClient(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  @PostConstruct
  public void scheduleTask() {
    log.info("TaskScheduler working");
//    taskScheduler.scheduleAtFixedRate(this::sendHttpRequestToDevice, Instant.now().plusSeconds(10), Duration.ofSeconds(20));
  }

  private void sendHttpRequestToDevice() {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.getForEntity("http://192.168.0.69/sensors", String.class);
    MicroclimateSensor microclimateSensor = new MicroclimateSensor();
    microclimateSensor.setUuid("testDevice");
    microclimateSensor.setTemperature(response.getBody().split(":")[2].split(";")[0]);
    telemetryService.sendReport(microclimateSensor);
  }
}
