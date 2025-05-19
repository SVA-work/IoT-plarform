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
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BuildService buildService;

    public UserResponse registration(@NotNull UserRequest request) {
        User user = buildUserRequest(request);
        if (!checkUserExistence(user)) {
            userRepository.save(user);
            log.info("Пользователь \"" + request.getLogin() + "\" сохранен");
            return buildService.buildUserResponse(user);
        } else {
            log.error("Пользователь \"" + request.getLogin() + "\" уже существует в базе данных");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        }
    }

    public UserResponse entry(@NotNull UserRequest request) {
        User user = buildUserRequest(request);
        if (checkUserExistence(user)) {
            return buildService.buildUserResponse(user);
        } else {
            log.error("Пользователь \"" + request.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
    }

    public List<DeviceResponse> listOfDevicesOfUser(@NotNull UserRequest request) {
        User user = getUserByLogin(request.getLogin());

        UserResponse userResponse = buildService.buildUserResponse(user);
        return userResponse.getDevices();
    }

    public User getUserByLogin(String login) {
        Optional<User> optionalUser = userRepository.findByLogin(login);
        if (optionalUser.isEmpty()) {
            log.error("Пользователь \"{}\" не найден в базе данных", login);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        } else {
            log.info("Получен пользователь \"{}\" из базы данных", login);
        }
        return optionalUser.get();
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
