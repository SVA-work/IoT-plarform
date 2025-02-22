package application.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private String login;
    private String password;
    private String telegramToken;
    private List<DeviceResponse> devices;
}
