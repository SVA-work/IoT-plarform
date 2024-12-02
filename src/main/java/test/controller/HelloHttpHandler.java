package test.controller;

import test.DTO.Message;
import test.library.AbstractHttpMappingHandler;
import test.library.annotation.*;
import test.library.json.JsonParser;
import test.service.UserInfoService;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class HelloHttpHandler extends AbstractHttpMappingHandler {
  private static final UserInfoService user = new UserInfoService();

  public HelloHttpHandler(JsonParser parser) {
    super(parser);
  }

  @Get("/test/get/listOfDevices")
  public DefaultFullHttpResponse deviceInformation(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.listOfDevices(message), StandardCharsets.UTF_8));
  }

  @Post("/test/post/registration")
  public DefaultFullHttpResponse registration(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.successfulRegistration(message), StandardCharsets.UTF_8));
  }

  @Post("/test/post/entry")
  public DefaultFullHttpResponse entery(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.successfulEntry(message), StandardCharsets.UTF_8));
  }

  @Post("/test/post/addDevice")
  public DefaultFullHttpResponse addDevices(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.addDevice(message), StandardCharsets.UTF_8));
  }

  @Post("/test/post/deleteDevice")
  public DefaultFullHttpResponse deleteDevices(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.deleteDevice(message), StandardCharsets.UTF_8));
  }
}
