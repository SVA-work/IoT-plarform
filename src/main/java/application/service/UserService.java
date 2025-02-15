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

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DeviceService deviceService;

    public UserResponse registration(@NotNull UserRequest request) {
        User user = buildUserRequest(request);
        if (!checkUserExistence(user)) {
            userRepository.save(user);
            return buildUserResponse(user);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        }
    }

    public UserResponse entry(@NotNull UserRequest request) {
        User user = buildUserRequest(request);
        if (checkUserExistence(user)) {
            return buildUserResponse(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Пользователь не найден");
        }
    }

    public List<DeviceResponse> listOfDevicesOfUser(@NotNull UserRequest request) {
        Optional<User> optionalUser = userRepository.findByLogin(request.getLogin());
        User user = new User();

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Пользователь не найден");
        }

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
        
        for (Device device : devices) {
            devicesResponse.add(deviceService.buildDeviceResponse(device));
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
        return userRepository.existsByLogin(user.getLogin());
    }
}
