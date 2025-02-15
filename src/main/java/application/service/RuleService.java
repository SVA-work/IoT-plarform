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

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class RuleService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final RuleRepository ruleRepository;

    public RuleResponse applyRule(RuleRequest ruleRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(ruleRequest.getLogin());
        User user = new User();
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Пользователь не найден");
        }

        Optional<Device> optionalDevice = deviceRepository.findByUuid(ruleRequest.getUuid());
        if (!optionalDevice.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Устройство принадлежит не этому пользователю");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (optionalRule.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Правило с таким название уже есть у этого устройства");
        }

        String pattern = "^Temperature/[-+]?\\d+(\\.\\d+)?/[-+]?\\d+(\\.\\d+)?$";
        if (!Pattern.matches(pattern, ruleRequest.getRule())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Неверный формат правила");
        }

        Rule rule = buildRuleRequest(ruleRequest, device);
        ruleRepository.save(rule);
        RuleResponse ruleResponse = buildRuleResponse(rule);
        return ruleResponse;
    }

    public RuleResponse deleteDeviceRule(RuleRequest ruleRequest) {
        Optional<User> optionalUser = userRepository.findByLogin(ruleRequest.getLogin());
        User user = new User();

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Пользователь не найден");
        }

        Optional<Device> optionalDevice = deviceRepository.findByUuid(ruleRequest.getUuid());
        if (!optionalDevice.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Устройство с таким названием не найдено");
        }

        Device device = optionalDevice.get();
        if (!user.equals(device.getUser())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Устройство принадлежит не этому пользователю");
        }

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndDevice(ruleRequest.getRule(), device);
        if (!optionalRule.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Правило с таким названием не найдено");
        }
        
        Rule rule = optionalRule.get();
        RuleResponse ruleResponse = buildRuleResponse(rule);
        ruleRepository.delete(rule);
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
