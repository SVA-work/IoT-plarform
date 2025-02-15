package application.controller.userapi;

import application.dto.request.RuleRequest;
import application.dto.response.RuleResponse;
import application.service.RuleService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RulesController {
    private final RuleService ruleService;

    @PostMapping("/apply")
    public RuleResponse applyRule(@RequestBody RuleRequest ruleRequest) {
        ruleRequest.getUuid();
        return ruleService.applyRule(ruleRequest);
    }

    @PostMapping("/delete")
    public RuleResponse deleteDeviceRule(@RequestBody RuleRequest ruleRequest) {
        return ruleService.deleteDeviceRule(ruleRequest);
    }
}
