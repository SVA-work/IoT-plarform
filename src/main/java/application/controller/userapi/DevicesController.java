package application.controller.userapi;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import application.dto.request.DeviceRequest;
import application.dto.response.DeviceResponse;
import application.dto.response.RuleResponse;
import application.service.DeviceService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/device")
public class DevicesController {
    private final DeviceService deviceService;

    @PostMapping("/add")
    public DeviceResponse addDevice( @RequestBody DeviceRequest device) {
        return deviceService.addDevice(device);
    }

    @PostMapping("/delete")
    public DeviceResponse deleteDevice(@RequestBody DeviceRequest device) {
        return deviceService.deleteDevice(device);
    }

    @GetMapping("/{login}/rules/{uuid}")
    public List<RuleResponse> deviceRules(@PathVariable String login, @PathVariable String uuid) {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setLogin(login);
        deviceRequest.setUuid(uuid);
        return deviceService.getDeviceRules(deviceRequest);
    }
}
