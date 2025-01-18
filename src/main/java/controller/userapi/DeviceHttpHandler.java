package controller.userapi;

import config.ServerConfig;
import dto.DbConnectionDto;
import dto.entity.DeviceDto;
import dto.entity.UserDto;
import dto.request.DeviceRequestDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import netty.library.AbstractHttpMappingHandler;
import netty.library.annotation.Get;
import netty.library.annotation.Post;
import netty.library.annotation.QueryParam;
import netty.library.annotation.RequestBody;
import netty.library.json.JsonParser;
import service.DeviceService;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class DeviceHttpHandler extends AbstractHttpMappingHandler {

  private static DeviceService device;

  public DeviceHttpHandler(JsonParser parser, DbConnectionDto dbConnectionDto) {
    super(parser);
    device = new DeviceService(dbConnectionDto);
  }

  @Get(ServerConfig.SHORT_LINK_GET_DEVICE_INFORMATION)
  public DefaultFullHttpResponse deviceInformation(@QueryParam("login") String login) {

    UserDto userDto = new UserDto();
    userDto.setLogin(login);

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.listOfDevicesOfUser(userDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.SHORT_LINK_ADD_DEVICE)
  public DefaultFullHttpResponse addDevices(@RequestBody DeviceRequestDto deviceRequestDto) {

    UserDto userDto = new UserDto();
    userDto.setLogin(deviceRequestDto.getLogin());

    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setToken(deviceRequestDto.getToken());
    deviceDto.setType(deviceRequestDto.getType());

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.addDevice(userDto, deviceDto), StandardCharsets.UTF_8));
  }

  @Post(ServerConfig.SHORT_LINK_DELETE_DEVICE)
  public DefaultFullHttpResponse deleteDevices(@RequestBody DeviceRequestDto deviceRequestDto) {

    UserDto userDto = new UserDto();
    userDto.setLogin(deviceRequestDto.getLogin());

    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setToken(deviceRequestDto.getToken());

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.deleteDevice(userDto, deviceDto), StandardCharsets.UTF_8));
  }
}
