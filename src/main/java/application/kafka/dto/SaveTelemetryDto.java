package application.kafka.dto;

import application.kafka.Command;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaveTelemetryDto {
  private Command command;
  private Telemetry message;
}
