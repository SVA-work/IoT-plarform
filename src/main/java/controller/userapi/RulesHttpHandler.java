package controller.userapi;

import config.ServerConfig;
import dto.DbConnectionDto;
import dto.objects.DeviceDto;
import dto.objects.RuleDto;
import dto.objects.UserDto;
import dto.request.RuleRequestDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import netty.library.AbstractHttpMappingHandler;
import netty.library.annotation.Get;
import netty.library.annotation.Post;
import netty.library.annotation.QueryParam;
import netty.library.annotation.RequestBody;
import netty.library.json.JsonParser;
import service.RuleService;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

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
