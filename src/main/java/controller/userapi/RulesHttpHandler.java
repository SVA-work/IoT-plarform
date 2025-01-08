package controller.userapi;

import java.nio.charset.StandardCharsets;

import config.ServerConfig;
import dto.entity.DeviceDto;
import dto.entity.RuleDto;
import dto.entity.UserDto;
import dto.request.RuleRequestDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import library.AbstractHttpMappingHandler;
import library.annotation.Get;
import library.annotation.Post;
import library.annotation.QueryParam;
import library.annotation.RequestBody;
import library.json.JsonParser;
import service.RuleService;


import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class RulesHttpHandler extends AbstractHttpMappingHandler {
  private static RuleService rule = new RuleService();

  public RulesHttpHandler(JsonParser parser) {
    super(parser);
  }

  @Get(ServerConfig.SHORT_LINK_ALL_AVAILABLE_RULES)
  public DefaultFullHttpResponse AvailableDeviceRules() {
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
