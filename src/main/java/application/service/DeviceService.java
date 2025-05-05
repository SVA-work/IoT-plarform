package application.service;

import application.dto.request.DeviceRequest;
import application.dto.response.DeviceResponse;
import application.dto.response.RuleResponse;
import application.dto.response.TelemetryResponse;
import application.entity.Device;
import application.entity.Rule;
import application.entity.Telemetry;
import application.entity.User;
import application.repository.DeviceRepository;

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
    private final RuleService ruleService;
    private final TelemetryService telemetryService;

    public DeviceResponse addDevice(DeviceRequest deviceRequest) {
        User user = ruleService.getUserByLogin(deviceRequest.getLogin());

        Optional<Device> optionalDevice = deviceRepository.findByDeviceNameAndUserId(deviceRequest.getDeviceName(), user.getId());
        if (optionalDevice.isPresent()) {
            log.error("Устройство \"" + deviceRequest.getDeviceName() + "\" уже существует");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство с таким названием уже существует");
        }

        Optional<Device> optionalDeviceUuid = deviceRepository.findByUuid(deviceRequest.getUuid());
        if (optionalDeviceUuid.isPresent()) {
            log.error("Устройство \"" + deviceRequest.getDeviceName() + "\" с таким uuid уже существует");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство с таким uuid уже существует");
        }

        log.info("Получено устройство \"" + deviceRequest.getDeviceName() + "\" из базы данных");

        Device device = buildDeviceRequest(deviceRequest, user);
        deviceRepository.save(device);
        return buildDeviceResponse(device);
    }

    public DeviceResponse deleteDevice(DeviceRequest deviceRequest) {
        User user = ruleService.getUserByLogin(deviceRequest.getLogin());

        Device device = ruleService.getDeviceByUserAndName(user, deviceRequest.getDeviceName());

        DeviceResponse deviceResponse = buildDeviceResponse(device);
        deviceRepository.delete(device);
        return deviceResponse;
    }

    public List<RuleResponse> getDeviceRules(DeviceRequest deviceRequest) {
        User user = ruleService.getUserByLogin(deviceRequest.getLogin());

        Device device = ruleService.getDeviceByUserAndName(user, deviceRequest.getDeviceName());

        DeviceResponse deviceResponse = buildDeviceResponse(device);
        return deviceResponse.getRules();
    }

    public List<TelemetryResponse> getDeviceTelemetry(DeviceRequest deviceRequest) {
        User user = ruleService.getUserByLogin(deviceRequest.getLogin());

        Device device = ruleService.getDeviceByUserAndName(user, deviceRequest.getDeviceName());

        List<TelemetryResponse> telemetryResponse = new ArrayList<>();
        List<Telemetry> telemetryy = device.getTelemetry();
        if (telemetryy != null) {
            for (Telemetry telemetry : telemetryy) {
                telemetryResponse.add(telemetryService.buildTelemetryResponse(telemetry));
            }
        }
        return telemetryResponse;
    }

    public DeviceResponse buildDeviceResponse(@NotNull Device device) {
        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setUuid(device.getUuid());
        deviceResponse.setType(device.getType());
        deviceResponse.setDeviceName(device.getDeviceName());
        deviceResponse.setLogin(device.getUser().getLogin());

        List<RuleResponse> rulesResponse = new ArrayList<>();
        List<Rule> rules = device.getRules();

        if (rules != null) {
            for (Rule rule : rules) {
                rulesResponse.add(ruleService.buildRuleResponse(rule));
            }
        }
        deviceResponse.setRules(rulesResponse);

        return deviceResponse;
    }

    private Device buildDeviceRequest(@NotNull DeviceRequest request, User user) {
        Device device = new Device();
        device.setUuid(request.getUuid());
        device.setType(request.getType());
        device.setDeviceName(request.getDeviceName());
        device.setUser(user);
        return device;
    }
}
