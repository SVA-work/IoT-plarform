package application.service;

import application.dto.request.DeviceRequest;
import application.dto.response.DeviceResponse;
import application.dto.response.RuleResponse;
import application.entity.Device;
import application.entity.Rule;
import application.entity.User;
import application.repository.DeviceRepository;
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
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final RuleService ruleService;

    public DeviceResponse addDevice(DeviceRequest deviceRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(deviceRequest.getLogin());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("Получен пользователь \"" + user.getLogin() + "\" из базы данных");
        } else {
            log.error("Пользователь \"" + deviceRequest.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        Optional<Device> optionalDevice = deviceRepository.findByUuid(deviceRequest.getUuid());
        if (optionalDevice.isPresent()) {
            log.error("Устройство \"" + deviceRequest.getUuid() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство с таким названием уже существует");
        }
        log.info("Получено устройство \"" + deviceRequest.getUuid() + "\" из базы данных");
        Device device = buildDeviceRequest(deviceRequest, user);
        deviceRepository.save(device);
        return buildDeviceResponse(device);
    }

    public DeviceResponse deleteDevice(DeviceRequest deviceRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(deviceRequest.getLogin());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("Получен пользователь \"" + user.getLogin() + "\" из базы данных");
        } else {
            log.error("Пользователь \"" + deviceRequest.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        Optional<Device> optionalDevice = deviceRepository.findByUuid(deviceRequest.getUuid());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + deviceRequest.getUuid() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }
        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"" + deviceRequest.getLogin() + "\" отсутсвует устройство \"" + deviceRequest.getUuid() + "\"");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }
        DeviceResponse deviceResponse = buildDeviceResponse(device);
        deviceRepository.delete(device);
        return deviceResponse;
    }

    public List<RuleResponse> getDeviceRules(DeviceRequest deviceRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(deviceRequest.getLogin());
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("Получен пользователь \"" + user.getLogin() + "\" из базы данных");
        } else {
            log.error("Пользователь \"" + deviceRequest.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        Optional<Device> optionalDevice = deviceRepository.findByUuid(deviceRequest.getUuid());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + deviceRequest.getUuid() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"" + deviceRequest.getLogin() + "\" отсутсвует устройство \"" + deviceRequest.getUuid() + "\"");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }

        DeviceResponse deviceResponse = buildDeviceResponse(device);
        return deviceResponse.getRules();
    }

    public DeviceResponse buildDeviceResponse(@NotNull Device device) {
        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setUuid(device.getUuid());
        deviceResponse.setType(device.getType());
        deviceResponse.setLogin(device.getUser().getLogin());

        List<RuleResponse> rulesResponse = new ArrayList<>();
        List<Rule> rules = device.getRules();
        for (Rule rule : rules) {
            rulesResponse.add(ruleService.buildRuleResponse(rule));
        }
        deviceResponse.setRules(rulesResponse);

        return deviceResponse;
    }

    private Device buildDeviceRequest(@NotNull DeviceRequest request, User user) {
        Device device = new Device();
        device.setUuid(request.getUuid());
        device.setType(request.getType());
        device.setUser(user);
        return device;
    }
}
