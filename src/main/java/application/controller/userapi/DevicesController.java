package application.controller.userapi;

import application.dto.request.DeviceRequest;
import application.dto.response.DeviceResponse;
import application.dto.response.RuleResponse;
import application.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/device")
public class DevicesController {
    private final DeviceService deviceService;

    @PostMapping("/add")
    public DeviceResponse addDevice(@RequestBody DeviceRequest device) {
        log.info("Получен запрос на добавление устройства");
        return deviceService.addDevice(device);
    }

    @DeleteMapping("/delete")
    public DeviceResponse deleteDevice(@RequestBody DeviceRequest device) {
        log.info("Получен запрос на удаление устройства");
        return deviceService.deleteDevice(device);
    }

    @GetMapping("/{login}/rules/{uuid}")
    public List<RuleResponse> deviceRules(@PathVariable String login, @PathVariable String uuid) {
        log.info("Получен запрос на получение списка правила у устройства");
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setLogin(login);
        deviceRequest.setUuid(uuid);
        return deviceService.getDeviceRules(deviceRequest);
    }
}
