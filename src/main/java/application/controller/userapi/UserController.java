package application.controller.userapi;

import application.dto.request.DeviceRequest;
import application.dto.request.UserRequest;
import application.dto.response.DeviceResponse;
import application.dto.response.UserResponse;
import application.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/registration")
    public UserResponse registration(@RequestBody UserRequest user) {
        return userService.registration(user);
    }

    @PostMapping("/entry")
    public UserResponse entry(@RequestBody UserRequest user) {
        return userService.entry(user);
    }

    @GetMapping("/{login}/devices")
    public List<DeviceResponse> listOfDevicesOfUser(@PathVariable String login) {
        UserRequest user = new UserRequest();
        user.setLogin(login);
        return userService.listOfDevicesOfUser(user);
    }
}
