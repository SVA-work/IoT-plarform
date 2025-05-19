package application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {
    private String login;
    private String password;
    private String telegramToken;
    private List<DeviceResponse> devices;
}
