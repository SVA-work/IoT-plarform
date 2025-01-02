package controller.userapi;

import dto.DeviceDto;
import dto.RuleDto;
import dto.UserDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import dto.Message;
import config.ServerConfig;
import library.AbstractHttpMappingHandler;
import library.annotation.Get;
import library.annotation.Post;
import library.annotation.QueryParam;
import library.annotation.RequestBody;
import library.json.JsonParser;
import service.DeviceService;
import service.RuleService;
import service.UserService;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class HelloHttpHandler extends AbstractHttpMappingHandler {
  private static UserService user = new UserService();
  private static DeviceService device = new DeviceService();
  private static RuleService rule = new RuleService();

  public HelloHttpHandler(JsonParser parser) {
    super(parser);
  }

  @Get(ServerConfig.LINK_ALL_AVAILABLE_RULES)
  public DefaultFullHttpResponse AvailableDeviceRules() {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.getAllAvailableRules(), StandardCharsets.UTF_8));
  }

  @Get(ServerConfig.LINK_DEVICE_RULES)
  public DefaultFullHttpResponse deviceRules(@QueryParam("login") String login, @QueryParam("token") String token) {

    UserDto userDto = new UserDto();
    userDto.setLogin(login);

    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setToken(token);

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.getDeviceRules(userDto, deviceDto), StandardCharsets.UTF_8));
  }

  @Get(ServerConfig.LINK_GET_DEVICE_INFORMATION)
  public DefaultFullHttpResponse deviceInformation(@QueryParam("login") String login) {

    UserDto userDto = new UserDto();
    userDto.setLogin(login);

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.listOfDevicesOfUser(userDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.LINK_REGISTRATION)
  public DefaultFullHttpResponse registration(@RequestBody UserDto userDto) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(user.registration(userDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.LINK_ENTRY)
  public DefaultFullHttpResponse entery(@RequestBody UserDto userDto) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(user.userVerification(userDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.LINK_ADD_DEVICE)
  public DefaultFullHttpResponse addDevices(@RequestBody Message message) {

    UserDto userDto = new UserDto();
    userDto.setLogin(message.getLogin());

    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setToken(message.getToken());

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.addDevice(userDto, deviceDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.LINK_DELETE_DEVICE)
  public DefaultFullHttpResponse deleteDevices(@RequestBody Message message) {

    UserDto userDto = new UserDto();
    userDto.setLogin(message.getLogin());

    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setToken(message.getToken());

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.deleteDevice(userDto, deviceDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.LINK_APPLY_RULE)
  public DefaultFullHttpResponse applyRule(@RequestBody Message message) {

    UserDto userDto = new UserDto();
    userDto.setLogin(message.getLogin());

    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setToken(message.getToken());

    RuleDto ruleDto = new RuleDto();
    ruleDto.setRule(message.getRule());

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.applyRule(userDto, deviceDto, ruleDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.LINK_DEVICE_RULES)
  public DefaultFullHttpResponse deleteDeviceRule(@RequestBody Message message) {

    UserDto userDto = new UserDto();
    userDto.setLogin(message.getLogin());

    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setToken(message.getToken());

    RuleDto ruleDto = new RuleDto();
    ruleDto.setRule(message.getRule());

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(rule.deleteDeviceRule(userDto, deviceDto, ruleDto), StandardCharsets.UTF_8));
  }
}