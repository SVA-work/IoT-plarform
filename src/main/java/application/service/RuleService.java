package application.service;

import application.dto.request.RuleRequest;
import application.dto.response.RuleResponse;
import application.entity.Device;
import application.entity.Rule;
import application.entity.User;
import application.repository.RuleRepository;
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

    private final RuleRepository ruleRepository;
    private final UserService userService;
    private final DeviceService deviceService;

    public RuleResponse applyRule(RuleRequest ruleRequest) {
        User user = userService.getUserByLogin(ruleRequest.getLogin());
        Device device = deviceService.getDeviceByUserAndName(user, ruleRequest.getDeviceName());

        Double value = ruleRequest.getValue();
        String comparison = ruleRequest.getComparison();

        Optional<Rule> optionalRule = ruleRepository.findByRuleAndValueAndComparisonAndDevice(ruleRequest.getRule(), value, comparison, device);
        if (optionalRule.isPresent()) {
            log.error("У устройства \"" + ruleRequest.getDeviceName() + "\" уже есть такое правило");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "У устройства уже есть такое правило");
        }

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
        User user = userService.getUserByLogin(ruleRequest.getLogin());
        Device device = deviceService.getDeviceByUserAndName(user, ruleRequest.getDeviceName());
        Rule rule = getRuleForDevice(ruleRequest, device);

        RuleResponse ruleResponse = buildRuleResponse(rule);
        ruleRepository.delete(rule);
        log.info("Правило \"" + ruleRequest.getRule() + "\" удалено");
        return ruleResponse;
    }

    private Rule getRuleForDevice(RuleRequest ruleRequest, Device device) {
        Optional<Rule> optionalRule = ruleRepository.findByRuleAndValueAndComparisonAndDevice(ruleRequest.getRule(), ruleRequest.getValue(), ruleRequest.getComparison(), device);
        if (optionalRule.isEmpty()) {
            log.error("У устройства \"{}\" отсутствует правило \"{}\"", device.getDeviceName(), ruleRequest.getRule());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Правило с таким названием не найдено");
        }
        return optionalRule.get();
    }

    public RuleResponse buildRuleResponse(@NotNull Rule rule) {
        RuleResponse ruleResponse = new RuleResponse();
        ruleResponse.setRule(rule.getRuleType());
        ruleResponse.setValue(rule.getValue());
        ruleResponse.setComparison(rule.getComparison());
        ruleResponse.setDeviceName(rule.getDevice().getDeviceName());
        return ruleResponse;
    }

    private Rule buildRuleRequest(@NotNull RuleRequest request, Device device) {
        Rule rule = new Rule();
        rule.setRuleType(request.getRule());
        rule.setDevice(device);
        rule.setValue(request.getValue());
        rule.setComparison(request.getComparison());
        return rule;
    }
}
