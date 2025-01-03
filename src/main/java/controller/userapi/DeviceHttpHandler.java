package controller.userapi;

import library.AbstractHttpMappingHandler;
import library.annotation.Get;
import library.annotation.Post;
import library.annotation.QueryParam;
import library.annotation.RequestBody;
import library.json.JsonParser;
import service.DeviceService;
import java.nio.charset.StandardCharsets;
import dto.UserDto;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class DeviceHttpHandler extends AbstractHttpMappingHandler {

  private static DeviceService device = new DeviceService();

  public DeviceHttpHandler(JsonParser parser) {
    super(parser);
  }

  @Get("/get/listOfDevicesOfUser")
  public DefaultFullHttpResponse deviceInformation(@QueryParam("login") String login) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.listOfDevicesOfUser(login), StandardCharsets.UTF_8));
  }
  
  @Post("/post/addDevice")
  public DefaultFullHttpResponse addDevices(@RequestBody UserDto message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.addDevice(message), StandardCharsets.UTF_8));
  }

  @Post("/post/deleteDevice")
  public DefaultFullHttpResponse deleteDevices(@RequestBody UserDto message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
            Unpooled.copiedBuffer(device.deleteDevice(message), StandardCharsets.UTF_8));
  }
}
