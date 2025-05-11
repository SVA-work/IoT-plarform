package application.controller.userapi;

import application.dto.request.UserRequest;
import application.dto.response.DeviceResponse;
import application.dto.response.UserResponse;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/registration")
    public UserResponse registration(@RequestBody UserRequest user) {
        log.info("Получен запрос на регистрацию");
        return userService.registration(user);
    }

    @PostMapping("/entry")
    public UserResponse entry(@RequestBody UserRequest user) {
        log.info("Получен запрос вход пользователя");
        return userService.entry(user);
    }

    @GetMapping("/{login}/devices")
    public List<DeviceResponse> listOfDevicesOfUser(@PathVariable String login) {
        log.info("Получен запрос на получения списка устройств пользователя");
        UserRequest user = new UserRequest();
        user.setLogin(login);
        return userService.listOfDevicesOfUser(user);
    }
}
