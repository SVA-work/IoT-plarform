package application.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto {
    private String chatId;
    private String deviceToken;
    private String deviceType;
    private String value;
}
