package application.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class UserResponse {
    private String login;
    private String password;
    private String telegramToken;
    private List<DeviceResponse> devices;
}
