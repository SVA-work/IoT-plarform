package application.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String login;
    private String password;
    private String telegramToken;
    private List<DeviceResponse> devices;
}
