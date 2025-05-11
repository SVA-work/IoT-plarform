package application.dto.kafka;

import application.kafka.command.TelemetryCommand;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaveTelemetryDto {
  private TelemetryCommand telemetryCommand;
  private Telemetry message;
}
