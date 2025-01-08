package controller.userapi;

import dto.entity.DeviceDto;
import dto.request.DeviceRequestDto;
import library.AbstractHttpMappingHandler;
import library.annotation.Get;
import library.annotation.Post;
import library.annotation.QueryParam;
import library.annotation.RequestBody;
import library.json.JsonParser;
import service.DeviceService;
import java.nio.charset.StandardCharsets;

import config.ServerConfig;
import dto.entity.UserDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class DeviceHttpHandler extends AbstractHttpMappingHandler {

  private static DeviceService device = new DeviceService();

  public DeviceHttpHandler(JsonParser parser) {
    super(parser);
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
