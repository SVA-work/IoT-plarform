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

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final RuleRepository ruleRepository;

    public RuleResponse applyRule(RuleRequest ruleRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(ruleRequest.getLogin());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("Получен пользователь \"" + user.getLogin() + "\" из базы данных");
        } else {
            log.error("Пользователь \"" + ruleRequest.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        Optional<Device> optionalDevice = deviceRepository.findByDeviceName(ruleRequest.getDeviceName());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + ruleRequest.getDeviceName() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"" + ruleRequest.getLogin() + "\" отсутсвует устройство \"" + ruleRequest.getDeviceName());

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (optionalRule.isPresent()) {
            log.error("У устройства \"" + ruleRequest.getDeviceName() + "\" уже есть правило \"" + ruleRequest.getRule());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Правило с таким название уже есть у этого устройства");
        }
        Integer low = ruleRequest.getLowestValue();
        Integer hight = ruleRequest.getHighestValue();
        if ((low == null || hight == null) || !(ruleRequest.getRule().equals("Temperature"))) {
            log.error("Правило \"" + ruleRequest.getRule() + "\" не соответствует формату");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный формат правила");
        }

        Rule rule = buildRuleRequest(ruleRequest, device);
        ruleRepository.save(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" добавленно");
        return buildRuleResponse(rule);
    }

    public RuleResponse deleteDeviceRule(RuleRequest ruleRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(ruleRequest.getLogin());
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("Получен пользователь \"" + user.getLogin() + "\" из базы данных");
        } else {
            log.error("Пользователь \"" + ruleRequest.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        Optional<Device> optionalDevice = deviceRepository.findByDeviceName(ruleRequest.getDeviceName());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + ruleRequest.getDeviceName() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"" + ruleRequest.getLogin() + "\" отсутсвует устройство \"" + ruleRequest.getDeviceName() + "\"");

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (optionalRule.isEmpty()) {

            log.error("У устройства \"" + ruleRequest.getDeviceName() + "\" отсутсвует правило \"" + ruleRequest.getRule() + "\"");

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Правило с таким названием не найдено");
        }

        Rule rule = optionalRule.get();
        RuleResponse ruleResponse = buildRuleResponse(rule);
        ruleRepository.delete(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" удалено");
        return ruleResponse;
    }

    public RuleResponse updateDeviceRule(RuleRequest ruleRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(ruleRequest.getLogin());
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("Получен пользователь \"" + user.getLogin() + "\" из базы данных");
        } else {
            log.error("Пользователь \"" + ruleRequest.getLogin() + "\" не найден в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        Optional<Device> optionalDevice = deviceRepository.findByDeviceName(ruleRequest.getDeviceName());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + ruleRequest.getDeviceName() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"" + ruleRequest.getLogin() + "\" отсутсвует устройство \"" + ruleRequest.getDeviceName() + "\"");

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (optionalRule.isEmpty()) {

            log.error("У устройства \"" + ruleRequest.getDeviceName() + "\" отсутсвует правило \"" + ruleRequest.getRule() + "\"");

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Правило с таким названием не найдено");
        }

        Rule rule = optionalRule.get();
        rule.setLowestValue(ruleRequest.getLowestValue());
        rule.setHighestValue(ruleRequest.getHighestValue());
        RuleResponse ruleResponse = buildRuleResponse(rule);
        ruleRepository.save(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" обновлено");
        return ruleResponse;
    }

    public RuleResponse buildRuleResponse(@NotNull Rule rule) {
        RuleResponse ruleResponse = new RuleResponse();
        ruleResponse.setRule(rule.getRule());
        ruleResponse.setLowestValue(rule.getLowestValue());
        ruleResponse.setHighestValue(rule.getHighestValue());
        ruleResponse.setDeviceName(rule.getDevice().getDeviceName());
        return ruleResponse;
    }

    private Rule buildRuleRequest(@NotNull RuleRequest request, Device device) {
        Rule rule = new Rule();
        rule.setRule(request.getRule());
        rule.setDevice(device);
        rule.setLowestValue(request.getLowestValue());
        rule.setHighestValue(request.getHighestValue());
        return rule;
    }
}
