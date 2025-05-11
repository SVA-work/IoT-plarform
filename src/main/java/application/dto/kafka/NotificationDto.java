package application.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto {
    String chatId;
    String deviceToken;
    String deviceType;
    String value;
}
