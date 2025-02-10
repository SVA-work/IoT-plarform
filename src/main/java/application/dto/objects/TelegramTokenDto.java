package application.dto.objects;

public class TelegramTokenDto extends BaseDto {
    private String userId;
    private String telegramToken;
    private String telegramTokenId;

    public TelegramTokenDto() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTelegramToken() {
        return telegramToken;
    }

    public void setTelegramToken(String telegramToken) {
        this.telegramToken = telegramToken;
    }

    public String getTelegramTokenId() {
        return telegramTokenId;
    }

    public void setTelegramTokenId(String telegramTokenId) {
        this.telegramTokenId = telegramTokenId;
    }
}
