package telegrambot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.devicenotification.TemperatureNotification;


@Component
public class IoTServiceBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(IoTServiceBot.class);

    private static final String START = "/start";

    private static final TemperatureNotification TEMPERATURE_NOTIFICATION = new TemperatureNotification();

    public IoTServiceBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
        }
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                Иcпользуйте этот телеграм токен при регистрации: %d
                """;
        var formattedText = String.format(text, userName, chatId);
        String chatIdStr = String.valueOf(chatId);
        sendMessage(chatIdStr, formattedText);
    }

    public void sendLowerTempNotification(String chatId,
                                          String deviceToken,
                                          String deviceType,
                                          String lowerBorderOfTemperature) {
        String message = TEMPERATURE_NOTIFICATION.lowerTempNotification(deviceToken, deviceType, lowerBorderOfTemperature);
        sendMessage(chatId, message);
    }

    public void sendHighTempNotification(String chatId,
                                         String deviceToken,
                                         String deviceType,
                                         String highBorderOfTemperature) {
        String message = TEMPERATURE_NOTIFICATION.highTempNotification(deviceToken, deviceType, highBorderOfTemperature);
        sendMessage(chatId, message);
    }

    private void sendMessage(String chatId, String text) {
        var sendMessage = new SendMessage(chatId, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return null;
    }
}
