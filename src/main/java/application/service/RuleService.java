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
import java.util.regex.Pattern;

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

        Optional<Device> optionalDevice = deviceRepository.findByUuid(ruleRequest.getUuid());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + ruleRequest.getUuid() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"" + ruleRequest.getLogin() + "\" отсутсвует устройство \"" + ruleRequest.getUuid());

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (optionalRule.isPresent()) {
            log.error("У устройства \"" + ruleRequest.getUuid() + "\" уже есть правило \"" + ruleRequest.getRule());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Правило с таким название уже есть у этого устройства");
        }

        String pattern = "^Temperature/[-+]?\\d+(\\.\\d+)?/[-+]?\\d+(\\.\\d+)?$";
        if (!Pattern.matches(pattern, ruleRequest.getRule())) {
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

        Optional<Device> optionalDevice = deviceRepository.findByUuid(ruleRequest.getUuid());
        if (optionalDevice.isEmpty()) {
            log.error("Устройство \"" + ruleRequest.getUuid() + "\" не найдено в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            log.error("У пользователя \"" + ruleRequest.getLogin() + "\" отсутсвует устройство \"" + ruleRequest.getUuid() + "\"");

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Устройство принадлежит не этому пользователю");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (optionalRule.isEmpty()) {

            log.error("У устройства \"" + ruleRequest.getUuid() + "\" отсутсвует правило \"" + ruleRequest.getRule() + "\"");

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Правило с таким названием не найдено");
        }

        Rule rule = optionalRule.get();
        RuleResponse ruleResponse = buildRuleResponse(rule);
        ruleRepository.delete(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" удалено");
        return ruleResponse;
    }

    public RuleResponse buildRuleResponse(@NotNull Rule rule) {
        RuleResponse ruleResponse = new RuleResponse();
        ruleResponse.setRule(rule.getRule());
        ruleResponse.setUuid(rule.getDevice().getUuid());
        return ruleResponse;
    }

    private Rule buildRuleRequest(@NotNull RuleRequest request, Device device) {
        Rule rule = new Rule();
        rule.setRule(request.getRule());
        rule.setDevice(device);
        return rule;
    }
}
