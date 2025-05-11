package application.dto.kafka;

import application.kafka.command.NotificationCommand;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendNotificationDto {
  private NotificationCommand command;
  private NotificationDto message;
}
