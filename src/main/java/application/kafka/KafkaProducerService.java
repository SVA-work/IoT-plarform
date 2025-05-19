package application.kafka;

import application.dto.kafka.KafkaMessage;
import application.dto.kafka.SaveTelemetryDto;
import application.dto.kafka.SendNotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String telemetryTopic;
    private final String notificationTopic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value("${topic-to-send-telemetry}") String telemetryTopic,
                                @Value("${topic-to-send-notification}") String notificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.telemetryTopic = telemetryTopic;
        this.notificationTopic = notificationTopic;
    }

    public void sendMessageWriteTelemetry(SaveTelemetryDto dtoMessage) {
        String message;
        try {
            KafkaMessage kafkaMessage =
                    new KafkaMessage(dtoMessage.getTelemetryCommand(),
                            objectMapper.writeValueAsString(dtoMessage.getMessage()));
            message = objectMapper.writeValueAsString(kafkaMessage);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }

        kafkaTemplate.send(telemetryTopic, message);
    }

    public void sendNotification(SendNotificationDto dtoMessage) {
        String message;
        try {
            KafkaMessage kafkaMessage =
                    new KafkaMessage(dtoMessage.getCommand(),
                            objectMapper.writeValueAsString(dtoMessage.getMessage()));
            message = objectMapper.writeValueAsString(kafkaMessage);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }

        kafkaTemplate.send(notificationTopic, message);
    }
}
