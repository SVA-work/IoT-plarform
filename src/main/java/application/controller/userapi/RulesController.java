package application.controller.userapi;

import application.dto.request.RuleRequest;
import application.dto.response.RuleResponse;
import application.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RulesController {
    private final RuleService ruleService;

    @PostMapping("/apply")
    public RuleResponse applyRule(@RequestBody RuleRequest ruleRequest) {
        log.info("Получен запрос на добавление правила");
        return ruleService.applyRule(ruleRequest);
    }

    @PatchMapping("/update")
    public RuleResponse updateRule(@RequestBody RuleRequest ruleRequest) {
        log.info("Получен запрос на обновление правила");
        return ruleService.updateDeviceRule(ruleRequest);
    }

    @DeleteMapping("/delete")
    public RuleResponse deleteDeviceRule(@RequestBody RuleRequest ruleRequest) {
        log.info("Получен запрос на удаление правила у устройства");
        return ruleService.deleteDeviceRule(ruleRequest);
    }
}
