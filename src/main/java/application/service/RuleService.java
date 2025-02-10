package application.service;

import application.config.ServerConfig;
import application.dto.DbConnectionDto;
import application.dto.objects.DeviceDto;
import application.dto.objects.RuleDto;
import application.dto.objects.UserDto;
import application.repository.DevicesRepository;
import application.repository.RulesRepository;
import application.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class RuleService {

    private final UsersRepository usersRepository;
    private final DevicesRepository devicesRepository;
    private final RulesRepository rulesRepository;
    private final DbConnectionDto dbConnectionDto;

    public RuleService(DbConnectionDto dbConnectionDto) {
        usersRepository = new UsersRepository(dbConnectionDto);
        devicesRepository = new DevicesRepository(dbConnectionDto);
        rulesRepository = new RulesRepository(dbConnectionDto);
        this.dbConnectionDto = dbConnectionDto;
    }

    public String getAllAvailableRules() {
        return "1) Датчик температуры. \n" +
                "Отправьте запрос на адрес: " + ServerConfig.LINK_APPLY_RULE + "\n" +
                "Укажите название устройства и приемлемую для вас температуры в таком формате: {login: 99, token: 123, rule: Temperature/lowTemperature/highTemperature}";
    }

    public boolean existenceUser(UserDto userDto) {
        for (UserDto currentUserDto : usersRepository.getAll()) {
            if (userDto.getLogin().equals(currentUserDto.getLogin())) {
                return true;
            }
        }
        return false;
    }

    public boolean existenceUserDevice(UserDto userDto, DeviceDto deviceDto) {
        List<DeviceDto> allDevices = usersRepository.devicesOfUser(userDto);
        if (allDevices != null) {
            for (DeviceDto currentMessage : allDevices) {
                String token = getTokenByDeviceId(userDto, currentMessage);
                currentMessage.setToken(token);
                if (currentMessage.getToken().equals(deviceDto.getToken())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String applyRule(UserDto userDto, DeviceDto deviceDto, RuleDto ruleDto) {
        DeviceService deviceService = new DeviceService(dbConnectionDto);
        userDto.setUserId(deviceService.getUserIdByLogin(userDto));
        String deviceId = getDeviceIdByToken(userDto, deviceDto);
        deviceDto.setDeviceId(deviceId);

        if (!(existenceUser(userDto))) {
            return "Пользователя с таким логином не существует.";
        }

        if (!existenceUserDevice(userDto, deviceDto)) {
            return "У вас нет такого устройства.";
        }

        ruleDto.setDeviceId(deviceId);
        String pattern = "^Temperature/[-+]?\\d+(\\.\\d+)?/[-+]?\\d+(\\.\\d+)?$";

        if (Pattern.matches(pattern, ruleDto.getRule())) {
            rulesRepository.create(ruleDto);
            return "Правила успешно добавлены.";
        }
        return "Неверный формат правила";
    }

    public String getDeviceRules(UserDto userDto, DeviceDto deviceDto) {
        UserService userService = new UserService(dbConnectionDto);
        if (!userService.existenceUser(userDto)) {
            return "Такого пользователя нет";
        }

        String deviceId = getDeviceIdByToken(userDto, deviceDto);
        if (deviceId == null) {
            return "Такого устройства нет";
        }

        deviceDto.setDeviceId(deviceId);
        List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(deviceDto);
        StringBuilder info = new StringBuilder();
        boolean hasAnyRule = false;

        for (RuleDto currentRuleDto : allRulesOfDevice) {
            info.append(currentRuleDto.getRule()).append("\n");
            hasAnyRule = true;
        }

        if (hasAnyRule) {
            String result = info.toString();
            return result.substring(0, result.length() - 1);
        }
        return "У данного устройства нет правил.";
    }

    public String deleteDeviceRule(UserDto userDto, DeviceDto deviceDto, RuleDto ruleDto) {
        if (getDeviceIdByToken(userDto, deviceDto) != null) {
            deviceDto.setDeviceId(getDeviceIdByToken(userDto, deviceDto));
            ruleDto.setRuleId(getRuleIdByRule(deviceDto, ruleDto));
            List<RuleDto> allRulesOfDevice = devicesRepository.rulesOfDevice(deviceDto);

            boolean hasDeletedAnyRule = false;
            for (RuleDto currentRuleDto : allRulesOfDevice) {
                if (Objects.equals(currentRuleDto.getRule(), ruleDto.getRule())) {
                    rulesRepository.delete(ruleDto);
                    hasDeletedAnyRule = true;
                    break;
                }
            }
            if (!hasDeletedAnyRule) {
                return "У данного устройства нет правила с таким названием.";
            }
            return "Правило успешно удалено.";
        } else {
            return "Такого устройства не существует";
        }
    }

    public String getDeviceIdByToken(UserDto userDto, DeviceDto deviceDto) {
        for (UserDto currentUserDto : usersRepository.getAll()) {
            if (userDto.getLogin().equals(currentUserDto.getLogin())) {
                for (DeviceDto currentDeviceDto : usersRepository.devicesOfUser(currentUserDto)) {
                    if (deviceDto.getToken().equals(currentDeviceDto.getToken())) {
                        return currentDeviceDto.getDeviceId();
                    }
                }
            }
        }
        return null;
    }

    public String getTokenByDeviceId(UserDto userDto, DeviceDto deviceDto) {
        for (UserDto currentUserDto : usersRepository.getAll()) {
            if (userDto.getLogin().equals(currentUserDto.getLogin())) {
                for (DeviceDto currentDeviceDto : usersRepository.devicesOfUser(currentUserDto)) {
                    if (deviceDto.getDeviceId().equals(currentDeviceDto.getDeviceId())) {
                        return currentDeviceDto.getToken();
                    }
                }
            }
        }
        return null;
    }

    public String getRuleIdByRule(DeviceDto deviceDto, RuleDto ruleDto) {
        for (RuleDto ruleMessage : rulesRepository.getAll()) {
            if (deviceDto.getDeviceId().equals(ruleMessage.getDeviceId()) && (ruleDto.getRule().equals(ruleMessage.getRule()))) {
                return ruleMessage.getRuleId();
            }
        }
        return null;
    }

}
