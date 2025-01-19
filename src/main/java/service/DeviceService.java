package service;

import dto.DbConnectionDto;
import dto.objects.DeviceDto;
import dto.objects.RuleDto;
import dto.objects.UserDto;
import repository.DevicesRepository;
import repository.UsersRepository;

import java.util.List;
import java.util.Objects;

public class DeviceService {

    private final UsersRepository usersRepository;
    private final DevicesRepository devicesRepository;
    private final DbConnectionDto dbConnectionDto;

    public DeviceService(DbConnectionDto dbConnectionDto) {
        this.usersRepository = new UsersRepository(dbConnectionDto);
        this.devicesRepository = new DevicesRepository(dbConnectionDto);
        this.dbConnectionDto = dbConnectionDto;
    }

    public String listOfDevicesOfUser(UserDto userDto) {

        userDto.setUserId(getUserIdByLogin(userDto));

        List<DeviceDto> allDevicesOfUser = usersRepository.devicesOfUser(userDto);

        if (allDevicesOfUser.isEmpty()) {
            return "У вас нет устройств.";
        }

        StringBuilder info = new StringBuilder();
        for (DeviceDto currentDeviceDto : allDevicesOfUser) {
            info.append(currentDeviceDto.getToken()).append('\n');
            for (RuleDto ruleDto : devicesRepository.rulesOfDevice(currentDeviceDto)) {
                info.append("  ").append(ruleDto.getRule()).append("\n");
            }
        }

        String result = info.toString();
        return result.substring(0, result.length() - 1);
    }

    public String addDevice(UserDto userDto, DeviceDto deviceDto) {
        String userId = getUserIdByLogin(userDto);

        if (userId == null) {
            return "Нет такого пользователя";
        }

        RuleService ruleService = new RuleService(dbConnectionDto);
        userDto.setUserId(userId);

        if (ruleService.existenceUserDevice(userDto, deviceDto)) {
            return "У этого пользователя уже есть устройство с таким названием";
        }

        deviceDto.setUserId(getUserIdByLogin(userDto));
        devicesRepository.create(deviceDto);

        return "Устройство успешно добавлено.\n" + "Список ваших устройств:\n" + listOfDevicesOfUser(userDto);
    }

    public String deleteDevice(UserDto userDto, DeviceDto deviceDto) {
        String userId = getUserIdByLogin(userDto);

        if (userId == null) {
            return "Нет такого пользователя";
        }

        userDto.setUserId(getUserIdByLogin(userDto));
        List<DeviceDto> allDevicesOfUser = usersRepository.devicesOfUser(userDto);

        boolean hasDeletedAnyDevice = false;
        for (DeviceDto currentDeviceDto : allDevicesOfUser) {
            if (Objects.equals(currentDeviceDto.getToken(), deviceDto.getToken())) {
                devicesRepository.delete(currentDeviceDto);
                hasDeletedAnyDevice = true;
                break;
            }
        }

        if (!hasDeletedAnyDevice) {
            return "У вас нет такого устройства.";
        }

        String infoAboutDevices = listOfDevicesOfUser(userDto);
        if (Objects.equals(infoAboutDevices, "У вас нет устройств.")) {
            return "У вас больше нет устройств.";
        }

        return ("Список ваших устройств:\n" + infoAboutDevices);
    }

    public String getUserIdByLogin(UserDto userDto) {
        for (UserDto currentUserDto : usersRepository.getAll()) {
            if (userDto.getLogin().equals(currentUserDto.getLogin())) {
                return currentUserDto.getUserId();
            }
        }
        return null;
    }
}
