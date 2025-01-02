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
  private static UserInfoService user = new UserInfoService();

  public HelloHttpHandler(JsonParser parser) {
    super(parser);
  }

  @Get("/test/get/allAvailableRules")
  public DefaultFullHttpResponse AvailableDeviceRules() {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.getAllAvailableRules(), StandardCharsets.UTF_8));
  }

  @Get("/test/get/deviceRules")
  public DefaultFullHttpResponse deviceRules(@QueryParam("login") String login, @QueryParam("token") String token) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.getDeviceRules(login, token), StandardCharsets.UTF_8));
  }

  @Get("/test/get/listOfDevicesOfUser")
  public DefaultFullHttpResponse deviceInformation(@QueryParam("login") String login) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.listOfDevicesOfUser(login), StandardCharsets.UTF_8));
  }

  @Post("/test/post/registration")
  public DefaultFullHttpResponse registration(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.registration(message), StandardCharsets.UTF_8));
  }

  @Post("/test/post/entry")
  public DefaultFullHttpResponse entery(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.userVerification(message), StandardCharsets.UTF_8));
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

  @Post("/test/post/applyRule")
  public DefaultFullHttpResponse applyRule(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.applyRule(message), StandardCharsets.UTF_8));
  }

  @Post("/test/post/deleteDeviceRule")
  public DefaultFullHttpResponse deleteDeviceRule(@RequestBody Message message) {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
        Unpooled.copiedBuffer(user.deleteDeviceRule(message), StandardCharsets.UTF_8));
  }
}
