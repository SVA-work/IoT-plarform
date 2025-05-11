package application.dto.kafka;

import application.kafka.command.Command;
import application.kafka.command.TelemetryCommand;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaMessage {
  private Command telemetryCommand;
  private String message;
}
