package application.controller.userapi;

import application.config.ServerConfig;
import application.dto.DbConnectionDto;
import application.dto.objects.DeviceDto;
import application.dto.objects.RuleDto;
import application.dto.objects.UserDto;
import application.dto.request.RuleRequestDto;
import application.netty.library.AbstractHttpMappingHandler;
import application.netty.library.annotation.Get;
import application.netty.library.annotation.Post;
import application.netty.library.annotation.QueryParam;
import application.netty.library.annotation.RequestBody;
import application.netty.library.json.JsonParser;
import application.service.RuleService;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@RestController
public class RulesHttpHandler extends AbstractHttpMappingHandler {
    private static RuleService rule;

    public RulesHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
        super(parser);
        rule = new RuleService(dbConnectionDto);
    }

    @Get(ServerConfig.SHORT_LINK_ALL_AVAILABLE_RULES)
    public DefaultFullHttpResponse availableDeviceRules() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(rule.getAllAvailableRules(), StandardCharsets.UTF_8));
    }

    @Get(ServerConfig.SHORT_LINK_DEVICE_RULES)
    public DefaultFullHttpResponse deviceRules(@QueryParam("login") String login, @QueryParam("token") String token) {

        UserDto userDto = new UserDto();
        userDto.setLogin(login);

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setToken(token);

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(rule.getDeviceRules(userDto, deviceDto), StandardCharsets.UTF_8));
    }

    @Post(ServerConfig.SHORT_LINK_APPLY_RULE)
    public DefaultFullHttpResponse applyRule(@RequestBody RuleRequestDto ruleRequestDto) {

        UserDto userDto = new UserDto();
        userDto.setLogin(ruleRequestDto.getLogin());

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setToken(ruleRequestDto.getToken());

        RuleDto ruleDto = new RuleDto();
        ruleDto.setRule(ruleRequestDto.getRule());

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(rule.applyRule(userDto, deviceDto, ruleDto), StandardCharsets.UTF_8));
    }

    @Post(ServerConfig.SHORT_LINK_DELETE_DEVICE_RULE)
    public DefaultFullHttpResponse deleteDeviceRule(@RequestBody RuleRequestDto ruleRequestDto) {

        UserDto userDto = new UserDto();
        userDto.setLogin(ruleRequestDto.getLogin());

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setToken(ruleRequestDto.getToken());

        RuleDto ruleDto = new RuleDto();
        ruleDto.setRule(ruleRequestDto.getRule());

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(rule.deleteDeviceRule(userDto, deviceDto, ruleDto), StandardCharsets.UTF_8));
    }
}
