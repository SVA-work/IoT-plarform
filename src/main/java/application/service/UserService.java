package application.service;

import application.dto.request.UserRequest;
import application.dto.response.DeviceResponse;
import application.dto.response.UserResponse;
import application.entity.Device;
import application.entity.TelegramToken;
import application.entity.User;
import application.repository.UserRepository;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final RuleService ruleService;

    public UserResponse registration(@NotNull UserRequest request) {
        User user = buildUserRequest(request);
        if (!checkUserExistence(user)) {
            userRepository.save(user);
            log.info("Пользователь \"" + request.getLogin() + "\" сохранен");
            return buildUserResponse(user);
        } else {
            log.error("Пользователь \"" + request.getLogin() + "\" уже существует в базе данных");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        }
    }

    public UserResponse entry(@NotNull UserRequest request) {
        User user = buildUserRequest(request);
        if (checkUserExistence(user)) {
            return buildUserResponse(user);
        } else {
            log.error("Пользователь \"" + request.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
    }

    public List<DeviceResponse> listOfDevicesOfUser(@NotNull UserRequest request) {
        User user = ruleService.getUserByLogin(request.getLogin());

        UserResponse userResponse = buildUserResponse(user);
        return userResponse.getDevices();
    }

    private UserResponse buildUserResponse(@NotNull User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setLogin(user.getLogin());
        userResponse.setPassword(user.getPassword());
        userResponse.setTelegramToken(user.getTelegramToken().getToken());

        List<DeviceResponse> devicesResponse = new ArrayList<>();
        List<Device> devices = user.getDevices();

        if (devices != null) {
            for (Device device : devices) {
                devicesResponse.add(deviceService.buildDeviceResponse(device));
            }
        }
        userResponse.setDevices(devicesResponse);
        return userResponse;
    }

    private User buildUserRequest(@NotNull UserRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        TelegramToken telegramToken = new TelegramToken();
        telegramToken.setToken(request.getTelegramToken());
        telegramToken.setUser(user);
        user.setTelegramToken(telegramToken);
        return user;
    }

    private boolean checkUserExistence(@NotNull User user) {
        try {
            return userRepository.existsByLogin(user.getLogin());
        } catch (Exception e) {
            log.error("Ошибка при проверке существования пользователя: " + e.getMessage());
            return false;
        }
    }
}
