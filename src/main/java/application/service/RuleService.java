package application.service;

import application.dto.request.RuleRequest;
import application.dto.response.RuleResponse;
import application.entity.Device;
import application.entity.Rule;
import application.entity.User;
import application.repository.DeviceRepository;
import application.repository.RuleRepository;
import application.repository.UserRepository;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class RuleService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final RuleRepository ruleRepository;

    public RuleResponse applyRule(RuleRequest ruleRequest) {
        User user = getUserByLogin(ruleRequest.getLogin());
        Device device = getDeviceByUserAndName(user, ruleRequest.getDeviceName());

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (optionalRule.isPresent()) {
            log.error("У устройства \"" + ruleRequest.getDeviceName() + "\" уже есть правило \"" + ruleRequest.getRule());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Правило с таким название уже есть у этого устройства");
        }

        Double value = ruleRequest.getValue();
        String comparison = ruleRequest.getComparison();
        if ((value == null || comparison == null) || !(ruleRequest.getRule().equals("Temperature"))) {
            log.error("Правило \"" + ruleRequest.getRule() + "\" не соответствует формату");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный формат правила");
        }

        Rule rule = buildRuleRequest(ruleRequest, device);
        ruleRepository.save(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" добавленно");
        return buildRuleResponse(rule);
    }

    public RuleResponse deleteDeviceRule(RuleRequest ruleRequest) {
        User user = getUserByLogin(ruleRequest.getLogin());
        Device device = getDeviceByUserAndName(user, ruleRequest.getDeviceName());
        Rule rule = getRuleForDevice(ruleRequest.getRule(), device);

        RuleResponse ruleResponse = buildRuleResponse(rule);
        ruleRepository.delete(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" удалено");
        return ruleResponse;
    }

    public RuleResponse updateDeviceRule(RuleRequest ruleRequest) {
        User user = getUserByLogin(ruleRequest.getLogin());
        Device device = getDeviceByUserAndName(user, ruleRequest.getDeviceName());
        Rule rule = getRuleForDevice(ruleRequest.getRule(), device);

        rule.setValue(ruleRequest.getValue());
        rule.setComparison(ruleRequest.getComparison());
        RuleResponse ruleResponse = buildRuleResponse(rule);
        ruleRepository.save(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" обновлено");
        return ruleResponse;
    }

    private Rule getRuleForDevice(String ruleName, Device device) {
        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleName, device);
        if (!optionalRule.isPresent()) {
            log.error("У устройства \"{}\" отсутствует правило \"{}\"", device.getDeviceName(), ruleName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Правило с таким названием не найдено");
        }
        return optionalRule.get();
    }

    public User getUserByLogin(String login) {
        Optional<User> optionalUser = userRepository.findByLogin(login);
        if (!optionalUser.isPresent()) {
            log.error("Пользователь \"{}\" не найден в базе данных", login);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        } else {
            log.info("Получен пользователь \"{}\" из базы данных", login);
        }
        return optionalUser.get();
    }

    public Device getDeviceByUserAndName(User user, String deviceName) {
        Optional<Device> optionalDevice = deviceRepository.findByDeviceNameAndUserId(deviceName, user.getId());
        if (!optionalDevice.isPresent()) {
            log.error("Устройство \"{}\" не найдено в базе данных", deviceName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"{}\" отсутствует устройство \"{}\"", user.getLogin(), deviceName);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }
        return device;
    }

    public RuleResponse buildRuleResponse(@NotNull Rule rule) {
        RuleResponse ruleResponse = new RuleResponse();
        ruleResponse.setRule(rule.getRule());
        ruleResponse.setValue(rule.getValue());
        ruleResponse.setComparison(rule.getComparison());
        ruleResponse.setDeviceName(rule.getDevice().getDeviceName());
        return ruleResponse;
    }

    private Rule buildRuleRequest(@NotNull RuleRequest request, Device device) {
        Rule rule = new Rule();
        rule.setRule(request.getRule());
        rule.setDevice(device);
        rule.setValue(request.getValue());
        rule.setComparison(request.getComparison());
        return rule;
    }
}
